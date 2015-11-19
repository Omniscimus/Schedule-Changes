<?php

require_once 'text/File_Manager.php';
require_once 'util/String_Util.php';

/**
 * Verwerker voor het HTML-bestand met roosterwijzigingen.
 *
 * @author omniscimus
 */
class File_Processor {

    private static $paragraph_tag_pattern;
    private static $pattern;
    private static $save_folder;
    private $source_file;
    private $target_file;

    /**
     * Maakt een nieuwe File_Processor.
     * 
     * @param string $source_file de naam van het HTML-bestand dat verwerkt
     * moet worden (met .htm extensie)
     * @param string $target_file de naam van een nieuw aan te maken bestand
     * waarin de verwerkte wijzigingen terecht moeten komen
     */
    function __construct($source_file) {
        $this->paragraph_tag_pattern = "#<p.+?</p>#";
        $this->pattern = "#>[^<>]+<#";
        $this->save_folder = File_Manager::getScheduleFilesFolder();
        $this->source_file = $this->save_folder . $source_file;
        $this->target_file = $this->save_folder . substr($source_file, 0, strlen($source_file) - 4) . ".txt";
    }

    /**
     * Verwerkt een HTML-bestand met roosterwijzigingen door alle wijzigingen
     * eruit te halen en in een nieuw TXT-bestand te zetten.
     * 
     * @return mixed FALSE als deze File_Processor niet goed geïnitialiseerd
     * is; anders het pad naar het verwerkte bestand
     */
    function processFile() {
        if (!isset($this->source_file)) {
            return FALSE;
        } else {
            // Stop het HTML-bestand in een array met een entry per regel
            $lines = file($this->source_file);
            // Voeg al die regels samen tot een lange regel
            $concatenated_lines = String_Util::concatenateStringArray($lines);
            // Haal alle newline characters eruit
            $trimmed_string = trim(preg_replace('/\s\s+/', ' ', $concatenated_lines));
            // Verwijder het deel van de string voorgaand aan de eerste <p> tag
            $paragraph_tags_string = substr($trimmed_string, strpos($trimmed_string, "<p"));
            // Stop iedere paragraph tag afzonderlijk in een array
            $paragraph_tags = $this->filterParagraphTags($paragraph_tags_string);
            // Haal de roosterwijzigingen uit de paragraph tags
            $crude_schedule_changes = $this->filterReadableScheduleChanges($paragraph_tags);
            // Haal de elementen die niet weergegeven moeten worden, eruit
            $polished_schedule_changes = $this->polishChanges($crude_schedule_changes);

            // Schrijf de resultaten naar het doelbestand
            $this->writeArrayToFile($polished_schedule_changes, $this->target_file);

            return $this->target_file;
        }
    }

    /**
     * Filtert alle HTML paragraph tags uit een string.
     * 
     * @param string $html een string met html waaruit paragraph tags gehaald
     * moeten worden
     * @return array een lijst met paragraph tags, een per entry
     */
    private function filterParagraphTags($html) {
        $paragraph_tags = [];
        $matches = [];
        while (preg_match($this->paragraph_tag_pattern, $html, $matches) === 1) {
            array_push($paragraph_tags, $matches[0]);
            $html = substr($html, strlen($paragraph_tags[count($paragraph_tags) - 1]) - 2);
        }
        return $paragraph_tags;
    }

    /**
     * Haalt de roosterwijzigingen uit een lijst met HTML paragraph tags.
     * 
     * @param array $paragraph_tags een lijst met paragraph tags
     * @return array een lijst met daarin leesbare roosterwijzigingen
     */
    private function filterReadableScheduleChanges($paragraph_tags) {
        $schedule_changes = [];
        foreach ($paragraph_tags as $paragraph_tag) {
            $matches = [];
            preg_match_all($this->pattern, $paragraph_tag, $matches);
            $parts = $matches[0];
            $schedule_change = "";
            foreach ($parts as $part) {
                $schedule_change = $schedule_change . substr($part, 1, strlen($part) - 2);
            }
            array_push($schedule_changes, $schedule_change);
        }
        return $schedule_changes;
    }

    /**
     * Verbetert een lijst met roosterwijzigingen.
     * 
     * @param array $schedule_changes een net verwerkte lijst met
     * roosterwijzigingen
     * @return array de lijst met roosterwijzigingen, waarin een aantal
     * verkeerde entries uit zijn weggehaald
     */
    private function polishChanges($schedule_changes) {
        $polished_changes = [];
        foreach ($schedule_changes as $change) {
            if (strlen($change) !== 0 && substr($change, 0, 23) != "ROOSTERWIJZIGINGEN VOOR") {
                array_push($polished_changes, $change);
            }
        }
        return $polished_changes;
    }

    /**
     * Schrijft een lijst met strings naar een bestand.
     * 
     * @param array $array een lijst met strings
     * @param string $file het pad naar het doelbestand
     */
    private function writeArrayToFile($array, $file) {
        $target_file = fopen($file, 'w');
        if ($target_file === FALSE) {
            throw new Exception("Kon het bestand voor verwerkte roosterwijzigingen niet openen.");
        }
        foreach ($array as $entry) {
            fwrite($target_file, $entry . "\n");
        }
        fclose($target_file);
    }

}
