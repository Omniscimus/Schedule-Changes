<?php

/**
 * Description of File_Downloader
 *
 * @author omniscimus
 */
class File_Downloader {

    /**
     * Downloadt het bestand naar de webserver. Als het downloaden mislukt,
     * gebeurt er niets.
     * 
     * @param string $url een URL naar het bestand
     * @param string $target_folder het pad naar de map waar het bestand in moet
     * komen
     */
    function downloadFile($url, $target_folder) {
        $file_name = substr($url, strrpos($url, "/") + 1, strlen($url));
        $target_path = $target_folder . DIRECTORY_SEPARATOR . $file_name;
        $new_file = file_get_contents($url);
        if ($new_file !== FALSE) {
            file_put_contents($target_path, $new_file);
        }
    }

    /**
     * Downloadt het bestand met roosterwijzigingen voor de gegeven dag naar de
     * map ./schedule-files.
     * 
     * @param int $day de dag waarvan het bestand gedownload moet worden,
     * waarbij 0 = zondag en 6 = zaterdag.
     */
    function downloadScheduleFile($day) {
        $today = getDayAbbreviation($day);
        $url = "https://files.itslearning.com/data/394/1076/rooster" . $today . ".htm";
        $this->downloadFile($url, "schedule-files");
    }

    /**
     * Verwijdert oude bestanden met roosterwijzigingen.
     */
    function deleteOldScheduleFiles() {
        $files = scandir("schedule-files/");
        foreach ($files as $file) {
            if($this->endswith($file, ".htm")) {
                unlink("schedule-files/" . $file);
            }
            // TODO also delete old processed .txt files.
        }
    }

    /**
     * Geeft de afkorting van de dag van de week die past bij de benaming van
     * bestanden met roosterwijzigingen. Als het om zaterdag of zondag gaat,
     * wordt "ma" gegeven, omdat er geen roosterwijzigingen zijn in het weekend.
     * 
     * @param int $day_number het nummer van de betreffende dag van de week,
     * waarbij 0 = zondag en 6 = zaterdag
     * @return string de Nederlandse afkorting van de dag van de week
     */
    function getDayAbbreviation($day_number) {
        switch ($day_number) {
            case 6:
            case 0:
            case 1:
                return "ma";
            case 2:
                return "di";
            case 3:
                return "wo";
            case 4:
                return "do";
            case 5:
                return "vr";
        }
    }

    /**
     * Geeft het nummer dat hoort bij de huidige dag van de week.
     * 
     * @return int het nummer van de dag van de week van vandaag, waarbij
     * 0 = zondag en 6 = zaterdag.
     */
    function getTodayNumber() {
        return date("w");
    }

    /**
     * Geeft het nummer dat hoort bij de dag van de week van morgen.
     * 
     * @return int het nummer van de dag van de week van morgen, waarbij
     * 0 = zondag en 6 = zaterdag.
     */
    function getTomorrowNumber() {
        $today = $this->getTodayNumber();
        if ($today === 6) {
            return 0;
        } else {
            return $today + 1;
        }
    }

    /**
     * Geeft of een string eindigt met de gegeven substring.
     * 
     * @param string $string de string waarvan getest moet worden of hij eindigt
     * met $test
     * @param string $test de string waarvan getest moet worden of hij op het
     * einde van $string staat
     * @return boolean TRUE als $string eindigt met $test; anders FALSE
     */
    function endswith($string, $test) {
        $strlen = strlen($string);
        $testlen = strlen($test);
        if ($testlen > $strlen) {
            return FALSE;
        }
        return substr_compare($string, $test, $strlen - $testlen, $testlen) === 0;
    }

}
