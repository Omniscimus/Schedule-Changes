<?php

require_once 'sql/MySQL_Manager.php';
require_once 'text/File_Downloader.php';
require_once 'text/File_Processor.php';
require_once 'text/Schedule_Organizer.php';

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
    }
    
    /**
     * Doet routine-operaties aan bestanden, zoals het verwijderen van
     * verouderde bestanden en het downloaden van nieuwe bestanden met
     * roosterwijzigingen. Moet aangeroepen worden door changes.php.
     */
    function initiate() {
        $this->file_downloader->deleteOldScheduleFiles();
        // TODO: Only download & process a new file if it's not already present
        $today = $this->file_downloader->getTodayNumber();
        $schedule_file = $this->file_downloader->downloadScheduleFile($today);
        $file_processor = new File_Processor($schedule_file);
        $processed_file = $file_processor->processFile();
    }

}
