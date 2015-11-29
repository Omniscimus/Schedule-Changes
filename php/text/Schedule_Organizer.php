<?php

require_once 'sql/School_SQL.php';
require_once 'util/Json_Handler.php';

/**
 * Leest het TXT-bestand dat verwerkt is door File_Processor en deelt de
 * roosterwijzigingen in per klas.
 *
 * @author omniscimus
 */
class Schedule_Organizer {

    private static $school_class_pattern;
    private $schedule_changes_class;
    private $txt_file;
    private $general_changes;
    private $changes_by_school_class; // Associative, class => array(changes)

    /**
     * Maakt een nieuwe Schedule_Organizer.
     * 
     * @param Schedule_Changes $schedule_changes_class de basis-class van dit
     * programma, vanwaar toegang tot SQL-functionaliteit verkregen kan worden
     * @param string $txt_file het TXT-bestand met verwerkte roosterwijzigingen
     */

    function __construct($schedule_changes_class, $txt_file) {
        Schedule_Organizer::$school_class_pattern = "#^\\S+#";
        $this->schedule_changes_class = $schedule_changes_class;
        $this->txt_file = $txt_file;
        $this->general_changes = [];
        $this->changes_by_school_class = [];
    }

    /**
     * Verwerkt het TXT-bestand met roosterwijzigingen, deelt de wijzigingen in
     * per klas of merkt ze als algemene wijzigingen, en schrijft de
     * resulterende arrays naar json-bestanden voor makkelijke toegang.
     */
    function readScheduleChanges() {
        $this->categorizeScheduleChanges();
        $this->writeChangesToJson();
    }

    /**
     * Schrijft de verwerkte roosterwijzigingen naar tijdelijke json-bestanden.
     * Er wordt een bestand gemaakt met algemene roosterwijzigingen, een array,
     * en een bestand met specifieke roosterwijzigingen, een mapped array waarin
     * telkens een string (key) gekoppeld staat aan een array met
     * roosterwijzigingen (value).
     */
    private function writeChangesToJson() {
        $directory_to_put_files = $this->schedule_changes_class->file_manager->getScheduleFilesFolder() . "json/";
        if (!file_exists($directory_to_put_files)) {
            mkdir($directory_to_put_files);
        }
        $general_changes_file = $directory_to_put_files . "general.json";
        Json_Handler::writeToJsonFile($this->general_changes, $general_changes_file);
        $specific_changes_file = $directory_to_put_files . "specific.json";
        Json_Handler::writeToJsonFile($this->changes_by_school_class, $specific_changes_file);
    }

    /**
     * Verdeelt de roosterwijzigingen per klas en stopt wijzigingen die niet
     * voor een bepaalde klas zijn in een array met algemene roosterwijzigingen.
     */
    private function categorizeScheduleChanges() {
        $schedule_changes = file($this->txt_file);
        foreach ($schedule_changes as $schedule_change) {
            if ($schedule_change != "&nbsp;\n") {
                $school_class = $this->getSchoolClass($schedule_change);
                if ($school_class === FALSE) {
                    // Bewaar in algemene roosterwijzigingen
                    array_push($this->general_changes, $schedule_change);
                } else {
                    // Bewaar in klas-specifieke array
                    if (!array_key_exists($school_class, $this->changes_by_school_class)) {
                        $this->changes_by_school_class[$school_class] = [];
                    }
                    array_push($this->changes_by_school_class[$school_class], $schedule_change);
                }
            }
        }
    }

    /**
     * Haalt de klas uit een roosterwijziging.
     * 
     * @param string $schedule_change een roosterwijziging
     * @return mixed de schoolklas als hij bestaat; anders FALSE
     */
    private function getSchoolClass($schedule_change) {
        $proper_schedule_change = html_entity_decode($schedule_change);
        $matches = [];
        if (preg_match(Schedule_Organizer::$school_class_pattern, $proper_schedule_change, $matches) === 1) {
            $school_class = "";
            foreach (str_split($matches[0]) as $char) {
                if ($this->isValidSchoolClassCharacter($char)) {
                    $school_class = $school_class . $char;
                } else {
                    break;
                }
            }
            if ($this->schedule_changes_class->mySQL->getSchoolSQL()->schoolClassExists($school_class)) {
                return $school_class;
            } else {
                return FALSE;
            }
        } else {
            return FALSE;
        }
    }

    /**
     * Geeft of een character in de naam van een klas zou kunnen zitten.
     * 
     * @param string $char de character die opgezocht moet worden
     * @return boolean TRUE als de character in een klas zou kunnen zitten;
     * anders FALSE
     */
    private function isValidSchoolClassCharacter($char) {
        $valid_chars = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
            'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '&',
            '-'];
        if (in_array($char, $valid_chars)) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

}
