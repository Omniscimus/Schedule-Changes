<?php

require_once 'util/Json_Handler.php';
require_once 'text/File_Processor.php';
require_once 'text/Schedule_Organizer.php';

/**
 * Geeft specifieke roosterwijzigingen gebaseerd op de leerling.
 *
 * @author omniscimus
 */
class Schedule_Reader {

    private $schedule_changes_class;

    /**
     * Maakt een nieuwe Schedule_Reader.
     * 
     * @param Schedule_Changes $schedule_changes_class de main class van dit
     * programma
     */
    function __construct($schedule_changes_class) {
        $this->schedule_changes_class = $schedule_changes_class;
    }

    /**
     * Geeft de roosterwijzigingen die voor alle leerlingen gelden.
     * 
     * @return array een lijst met algemene roosterwijzigingen
     */
    function getGeneralChanges() {
        $general_changes = $this->getGeneralChangesFromFile();
        if ($general_changes === FALSE) {
            $this->processNewScheduleChanges();
            $general_changes = $this->getGeneralChangesFromFile();
        }
        return $general_changes;
    }

    /**
     * Geeft de roosterwijzigingen die voor iedereen gelden vanuit een verwerkt
     * json-bestand.
     * 
     * @return mixed FALSE als het bestand niet geopend kon worden en het
     * bestand waarschijnlijk nog niet gedownload is; anders een array met
     * algemene roosterwijzigingen
     */
    private function getGeneralChangesFromFile() {
        try {
            $general_changes = Json_Handler::readFromJsonFile("schedule-files" . DIRECTORY_SEPARATOR . "json" . DIRECTORY_SEPARATOR . "general.json");
        } catch (Exception $e) {
            $general_changes = FALSE;
        }
        return $general_changes;
    }

    /**
     * Geeft de roosterwijzigingen voor een specifieke leerling.
     * 
     * @return array een lijst met specifieke roosterwijzigingen
     */
    function getSpecificChanges() {
        $student_ID = $this->schedule_changes_class->student_id;
        $specific_changes = $this->getSpecificChangesFromFile();
        if ($specific_changes === FALSE) {
            $this->processNewScheduleChanges();
            $specific_changes = $this->getSpecificChangesFromFile();
        }
        $student_classes = $this->schedule_changes_class->mySQL->getSchoolSQL()->getSchoolClasses($student_ID);
        $student_changes = [];
        $classes = array_keys($specific_changes);
        foreach ($classes as $school_class) {
            if (in_array($school_class, $student_classes)) {
                $student_changes = array_merge($student_changes, $specific_changes[$school_class]);
            }
        }
        return $student_changes;
    }

    /**
     * Geeft de specifieke roosterwijzigingen voor een bepaalde leerling.
     * 
     * @return mixed FALSE als het bestand niet geopend kon worden en het
     * bestand waarschijnlijk nog niet gedownload is; anders een array met
     * specifieke roosterwijzigingen
     */
    private function getSpecificChangesFromFile() {
        try {
            $specific_changes = (array) Json_Handler::readFromJsonFile("schedule-files" . DIRECTORY_SEPARATOR . "json" . DIRECTORY_SEPARATOR . "specific.json");
        } catch (Exception $e) {
            $specific_changes = FALSE;
        }
        return $specific_changes;
    }

    /**
     * Downloadt en verwerkt een nieuw bestand met roosterwijzigingen.
     */
    function processNewScheduleChanges() {
        $file_downloader = new File_Downloader();
        $file_downloader->deleteOldScheduleFiles();
        $today = $file_downloader->getTodayNumber();
        $schedule_file = $file_downloader->downloadScheduleFile($today);
        $file_processor = new File_Processor($schedule_file);
        $processed_file = $file_processor->processFile();
        $schedule_organizer = new Schedule_Organizer($this->schedule_changes_class, $processed_file);
        $schedule_organizer->readScheduleChanges();
    }

}
