<?php

require_once 'sql/School_SQL.php';

/**
 * Leest het TXT-bestand dat verwerkt is door File_Processor en deelt de
 * roosterwijzigingen in per klas.
 *
 * @author omniscimus
 */
class Schedule_Reader {

    private static $school_class_pattern;
    private $school_sql;
    private $txt_file;
    private $general_changes;
    private $changes_by_school_class; // Associative, class => array(changes)

    /**
     * Maakt een nieuwe Schedule_Reader.
     * 
     * @param School_SQL $school_sql het toegangspunt voor de schooldatabase
     * @param string $txt_file het TXT-bestand met verwerkte roosterwijzigingen
     */
    function __construct($school_sql, $txt_file) {
        Schedule_Reader::$school_class_pattern = "#^\\S+#";
        $this->school_sql = $school_sql;
        $this->txt_file = $txt_file;
        $this->general_changes = [];
        $this->changes_by_school_class = [];
    }
    
    /**
     * Verdeelt de roosterwijzigingen per klas en stopt wijzigingen die niet
     * voor een bepaalde klas zijn in een array met algemene roosterwijzigingen.
     */
    function categorizeScheduleChanges() {
        $schedule_changes = file($this->txt_file);
        foreach ($schedule_changes as $schedule_change) {
            $school_class = $this->getSchoolClass($schedule_change);
            if ($school_class === FALSE) {
                // Bewaar in algemene roosterwijzigingen
                array_push($this->general_changes, $schedule_change);
            } else {
                // Bewaar in klas-specifieke array
                if($this->changes_by_school_class[$school_class] === NULL) {
                    $this->changes_by_school_class[$school_class] = [];
                }
                array_push($this->changes_by_school_class[$school_class], $schedule_change);
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
        $matches = [];
        if (preg_match(Schedule_Reader::$school_class_pattern, $schedule_change, $matches) === 1) {
            $school_class = "";
            foreach (str_split($matches[0]) as $char) {
                if ($this->isValidSchoolClassCharacter($char)) {
                    $school_class = $school_class . $char;
                } else {
                    break;
                }
            }
            if ($this->school_sql->schoolClassExists($school_class)) {
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
            'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '&'];
        if (in_array($char, $valid_chars)) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

}
