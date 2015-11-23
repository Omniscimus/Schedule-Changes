<?php
require_once 'Schedule_Changes.php';
$config = include 'config.php';
date_default_timezone_set($config["default_timezone"]);
// Schakel waarschuwingen uit zodat er geen warning verschijnt als de URL naar
// het bestand met roosterwijzigingen een 404 response code oplevert.
//error_reporting(E_ERROR | E_WARNING | E_PARSE | E_NOTICE);
error_reporting(E_ERROR);

if (is_numeric($_GET["studentID"]) && strlen($_GET["studentID"]) === 6) {
    if (isset($_GET["weekDay"]) && $_GET["weekDay"] < 7 && $_GET["weekDay"] > -1) {
        $day = $_GET["weekDay"];
    } else {
        $day = date("w");
    }
    $schedule_changes = new Schedule_Changes($_GET["studentID"], $day);
}
?>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Persoonlijke wijzigingen</title>
    </head>
    <body>
        <h1>Roosterwijzigingen</h1>
        <br />
        <?php
        if (isset($schedule_changes)) {
            try {
                echo "<h2>Algemene roosterwijzigingen</h2><br />";
                $general_changes = $schedule_changes->file_manager->schedule_reader->getGeneralChanges();
                if (!empty($general_changes)) {
                    foreach ($general_changes as $general_change) {
                        echo $general_change . "<br />";
                    }
                } else {
                    echo "Er zijn geen algemene roosterwijzigingen voor deze dag.";
                }
                echo "<h2>Roosterwijzigingen voor " . $schedule_changes->mySQL->getSchoolSQL()->getStudentName($schedule_changes->student_id) . "</h2><br />";
                $specific_changes = $schedule_changes->file_manager->schedule_reader->getSpecificChanges();
                if (!empty($specific_changes)) {
                    foreach ($specific_changes as $specific_change) {
                        echo $specific_change . "<br />";
                    }
                } else {
                    echo "Je hebt geen persoonlijke roosterwijzigingen voor deze dag.";
                }
            } catch (Exception $e) {
                echo $e->getMessage();
            }
        } else {
            echo "<p>Leerlingnummer niet gevonden.</p>";
        }
        ?>
    </body>
</html>
<?php
if (isset($schedule_changes)) {
    $schedule_changes->mySQL->closeConnection();
}
