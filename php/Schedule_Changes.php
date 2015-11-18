<?php

require_once 'sql/MySQL_Manager.php';
require_once 'text/File_Downloader.php';
require_once 'text/Schedule_Reader.php';

/**
 * Main class voor dit programma.
 *
 * @author omniscimus
 */
class Schedule_Changes {

    var $config;
    var $student_id;
    var $mySQL;
    var $file_downloader;
    var $schedule_reader;

    /**
     * Maakt een nieuwe Schedule_Changes class.
     * 
     * @param string $student_id het leerlingnummer van de betreffende leerling
     */
    function __construct($student_id) {
        $this->config = include 'config.php';
        date_default_timezone_set($this->config["default_timezone"]);
        $this->student_id = $student_id;
        $this->mySQL = new MySQL_Manager();
        $this->file_downloader = new File_Downloader();
        $this->schedule_reader = new Schedule_Reader($this);
    }

}
