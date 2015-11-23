<?php

require_once 'text/File_Processor.php';
require_once 'text/Schedule_Organizer.php';

/**
 * Centraal punt voor waarden die met de bestanden van dit programma te maken
 * hebben.
 *
 * @author omniscimus
 */
class File_Manager {

    private $schedule_changes_class;
    private $schedule_files_folder;
    private $json_folder;
    
    var $schedule_reader;

    /**
     * Maakt een nieuwe File_Manager.
     * 
     * @param Schedule_Changes $schedule_changes_class het centrum van dit
     * programma.
     */
    function __construct($schedule_changes_class) {
        $this->schedule_changes_class = $schedule_changes_class;
        $this->schedule_files_folder = "schedule-files" . DIRECTORY_SEPARATOR . date("W") . "-" . $schedule_changes_class->day . DIRECTORY_SEPARATOR;
        $this->json_folder = $this->schedule_files_folder . "json" . DIRECTORY_SEPARATOR;
        $this->schedule_reader = new Schedule_Reader($schedule_changes_class);
    }

    /**
     * Geeft de map waarin bestanden met roosterwijzigingen tijdelijk worden
     * opgeslagen.
     * 
     * @return string het pad naar de map met tijdelijke bestanden
     */
    function getScheduleFilesFolder() {
        if (!file_exists($this->schedule_files_folder)) {
            mkdir($this->schedule_files_folder, 0775, TRUE);
        }
        return $this->schedule_files_folder;
    }

    /**
     * Geeft de map waarin verwerkte JSON-bestanden worden opgeslagen.
     * 
     * @return string het pad naar de map met JSON-bestanden
     */
    function getJsonFolder() {
        if (!file_exists($this->json_folder)) {
            mkdir($this->json_folder, 0775, TRUE);
        }
        return $this->json_folder;
    }

    /**
     * Downloadt en verwerkt een nieuw bestand met roosterwijzigingen.
     */
    function processNewScheduleChanges() {
        File_Downloader::deleteOldScheduleFiles();
        $schedule_file = File_Downloader::downloadScheduleFile($this->schedule_changes_class->day, $this->getScheduleFilesFolder());
        $file_processor = new File_Processor($schedule_file);
        $processed_file = $file_processor->processFile();
        $schedule_organizer = new Schedule_Organizer($this->schedule_changes_class, $processed_file);
        $schedule_organizer->readScheduleChanges();
    }

}
