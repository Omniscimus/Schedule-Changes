<?php

/**
 * Verzorgt het schrijven naar en lezen van data naar bestanden in json-formaat.
 *
 * @author omniscimus
 */
class Json_Handler {

    /**
     * Slaat een object in json-formaat op in een nieuw bestand.
     * 
     * @param mixed $object het object dat opgeslagen moet worden
     * @param string $file de naam van het aan te maken bestand
     * @throws Exception als $file niet geopend kon worden
     */
    static function writeToJsonFile($object, $file) {
        $json = str_replace("\u00a0", " ", json_encode($object));
        $file_handle = fopen($file, 'w');
        if ($file_handle === FALSE) {
            throw new Exception("Kon het json-bestand niet openen.");
        }
        fwrite($file_handle, $json);
        fclose($file_handle);
    }

    /**
     * Leest vanuit een bestand met json het object dat erin gecodeerd staat.
     * 
     * @param string $file het pad naar het te openen bestand
     * @return mixed het object uit het bestand dat in json gecodeerd was
     */
    static function readFromJsonFile($file) {
        $json = file_get_contents($file);
        return json_decode($json);
    }

}
