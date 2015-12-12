<?php
namespace ScheduleChanges;

require_once 'text/File_Manager.php';
require_once 'util/String_Util.php';

/**
 * Description of File_Downloader
 *
 * @author omniscimus
 */
class File_Downloader {

    /**
     * Downloadt het bestand naar de webserver in UTF-8 formaat. Als het
     * downloaden mislukt, gebeurt er niets.
     * 
     * @param string $url een URL naar het bestand
     * @param string $target_folder het pad naar de map waar het bestand in moet
     * komen
     * @throws Exception als het bestand niet gedownload kon worden,
     * bijvoorbeeld door een 404 response code.
     */
    static function downloadFile($url, $target_folder) {
        $file_name = substr($url, strrpos($url, "/") + 1, strlen($url));
        $target_path = $target_folder . DIRECTORY_SEPARATOR . $file_name;
        $remote_file = file_get_contents($url);
        if ($remote_file !== FALSE) {
            $new_file = mb_convert_encoding($remote_file, "UTF-8");
            if ($new_file !== FALSE) {
                file_put_contents($target_path, $new_file);
            }
        } else {
            throw new Exception("Bestand niet beschikbaar.");
        }
    }

    /**
     * Downloadt het bestand met roosterwijzigingen voor de gegeven dag naar de
     * map ./schedule-files.
     * 
     * @param int $day de dag waarvan het bestand gedownload moet worden,
     * waarbij 0 = zondag en 6 = zaterdag.
     * @param string $target_folder de map waar het bestand naartoe gedownload
     * zal worden
     * @return string het pad naar het gedownloade bestand
     */
    static function downloadScheduleFile($day, $target_folder) {
        $today = File_Downloader::getDayAbbreviation($day);
        $url = "https://files.itslearning.com/data/394/1076/rooster" . $today . ".htm";
        File_Downloader::downloadFile($url, $target_folder);
        return $target_folder . "rooster" . $today . ".htm";
    }

    /**
     * Verwijdert oude bestanden met roosterwijzigingen.
     */
    static function deleteOldScheduleFiles() {
        $files = scandir("schedule-files" . DIRECTORY_SEPARATOR);
        foreach ($files as $file) {
            if ($file != ".." && $file != "." && substr($file, 0, 2) < date("W")) {
                unlink("schedule-files/" . $file);
            }
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
    static function getDayAbbreviation($day_number) {
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

}
