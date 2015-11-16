<?php

/**
 * Centraal punt voor waarden die met de bestanden van dit programma te maken
 * hebben.
 *
 * @author omniscimus
 */
class File_Manager {

    const schedule_files_folder = "schedule-files/";

    static function getScheduleFilesFolder() {
        if (!file_exists(File_Manager::schedule_files_folder)) {
            mkdir(File_Manager::schedule_files_folder);
        }
        return File_Manager::schedule_files_folder;
    }

}
