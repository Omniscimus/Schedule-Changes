<?php
namespace ScheduleChanges;

/**
 * Deze class biedt een aantal functies die uitgevoerd kunnen worden op een
 * String.
 *
 * @author omniscimus
 */
class String_Util {

    /**
     * Geeft het laatste element uit de gegeven array.
     * 
     * @param array $array de lijst waaruit het laatste element gehaald moet
     * worden
     * @return mixed het laatste element uit de lijst
     */
    static function getLastArrayEntry($array) {
        return $array[count($array) - 1];
    }

    /**
     * Haalt de laatste string uit de gegeven array, plakt
     * $string_to_concatenate achter die string en stopt het resultaat terug in
     * de array op de plaats van het voorgaande laatste element.
     * 
     * @param array $array de te veranderen array
     * @param string $string_to_concatenate de string die aan het laatste
     * element van $array vastgeplakt moet worden
     */
    static function concatenateWithPreviousArrayEntry($array, $string_to_concatenate) {
        if (count($array) > 0) {
            $previous_paragraph_tag = $this->getLastArrayEntry($array);
            $array[count($array) - 1] = $previous_paragraph_tag . " " . $string_to_concatenate;
        }
    }

    /**
     * Voegt alle strings uit een array samen in een lange string.
     * 
     * @param array $array een lijst met strings
     * @return string alle strings uit $array achter elkaar geplakt
     */
    static function concatenateStringArray($array) {
        $long_string = "";
        foreach ($array as $entry) {
            $long_string = $long_string . $entry;
        }
        return $long_string;
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
    static function endswith($string, $test) {
        $strlen = strlen($string);
        $testlen = strlen($test);
        if ($testlen > $strlen) {
            return FALSE;
        }
        return substr_compare($string, $test, $strlen - $testlen, $testlen) === 0;
    }

}
