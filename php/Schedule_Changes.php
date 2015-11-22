<?php

require_once 'sql/MySQL_Manager.php';
require_once 'text/File_Manager.php';
require_once 'text/File_Downloader.php';
require_once 'text/Schedule_Reader.php';

/**
 * Main class voor dit programma.
 *
 * @author omniscimus
 */
class Schedule_Changes {

    var $config;
    var $day;
    var $student_id;
    var $mySQL;
    var $file_manager;

    /**
     * Maakt een nieuwe Schedule_Changes class.
     * 
     * @param string $student_id het leerlingnummer van de betreffende leerling
     * @param int $day de dag van de week waarvan de roosterwijzigingen moeten
     * worden weergegeven (0 voor zondag, 6 voor zaterdag)
     */
    function __construct($student_id, $day) {
        $this->config = include 'config.php';
        $this->day = $day;
        $this->student_id = $student_id;
        $this->mySQL = new MySQL_Manager();
        $this->file_manager = new File_Manager($this);
    }

}
