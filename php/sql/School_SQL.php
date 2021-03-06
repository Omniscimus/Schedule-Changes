<?php
namespace ScheduleChanges;

/**
 * Verzorgt de communicatie met de SQL database die geleverd wordt door de
 * school.
 *
 * @author omniscimus
 */
class School_SQL {

    private $mySQL_manager;

    /**
     * Maakt een nieuwe School_SQL.
     * 
     * @param MySQL_Manager $mySQL_manager de manager van deze instance
     */
    function __construct($mySQL_manager) {
        $this->mySQL_manager = $mySQL_manager;
    }

    private $school_classes;

    /**
     * Geeft de naam van een leerling.
     * 
     * @param int $studentID het leerlingnummer van de leerling
     * @return string de naam van de leerling, in het formaat Voornaam
     * Achternaam
     */
    function getStudentName($studentID) {
        $statement = $this->mySQL_manager->getConnection()->
                prepare("SELECT voornaam,achternaam FROM school.leerlingen WHERE leerlingnummer = ?;");
        $statement->bind_param("i", $studentID);
        $statement->execute();

        $statement->bind_result($first_name, $surname);
        $statement->fetch();

        return $first_name . " " . $surname;
    }

    /**
     * Geeft een lijst met klassen waar de leerling in zit.
     * 
     * @param int $studentID het leerlingnummer van de betreffende leerling
     */
    function getSchoolClasses($studentID) {
        $statement = $this->mySQL_manager->getConnection()->
                prepare("SELECT klas FROM school.klassen WHERE leerlingnummer = ?;");
        $statement->bind_param("i", $studentID);
        $statement->execute();

        $statement->bind_result($school_class);
        $school_classes = [];
        while ($statement->fetch() !== NULL) {
            array_push($school_classes, $school_class);
        }
        return $school_classes;
    }

    /**
     * Geeft een lijst met alle bestaande klassen.
     * 
     * @return array een array met daarin alle klassen die in de database staan
     */
    function getAllSchoolClasses() {
        if (isset($this->school_classes)) {
            return $this->school_classes;
        } else {
            $results = $this->mySQL_manager->getConnection()->
                    query("SELECT DISTINCT klas FROM school.klassen;");
            if ($results !== FALSE) {
                $school_classes = [];
                for ($i = 0; $i < $results->num_rows; $i++) {
                    $school_classes[$i] = strtolower($results->fetch_assoc()["klas"]);
                }
                $this->school_classes = $school_classes;
                return $school_classes;
            }
        }
    }

    /**
     * Geeft of een klas bestaat of niet.
     * 
     * @param string $school_class de klas in kwestie
     * @return bool TRUE als de klas bestaat; anders FALSE
     */
    function schoolClassExists($school_class) {
        $school_class_lower = strtolower($school_class);
        return in_array($school_class_lower, $this->getAllSchoolClasses());
    }

}
