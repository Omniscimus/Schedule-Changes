<?php

namespace ScheduleChanges;

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

    /**
     * De configuratie voor het programma.
     * @var array een array met configuratiewaarden, zoals in config.php
     */
    var $config;
    /**
     * Geeft de dag van de week, waarbij 0 zondag is, en 6 zaterdag.
     * @var int het nummer van de dag van de week
     */
    var $day;
    /**
     * Geeft het nummer van de leerling waarvoor roosterwijzigingen moeten
     * worden getoond.
     * @var int het leerlingnummer
     */
    var $student_id;
    /**
     * Geeft de gebruikte MySQL_Manager van dit programma.
     * @var MySQL_Manager het toegangspunt van de MySQL code
     */
    var $mySQL;
    /**
     * Geeft de gebruikte File_Manager van dit programma.
     * @var File_Manager het toegangspunt van de verwerkingscode van de
     * roosterwijzigingen
     */
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
