<?php
require_once 'Schedule_Changes.php';
$config = include 'config.php';
date_default_timezone_set($config["default_timezone"]);

if (is_numeric($_GET["studentID"]) && strlen($_GET["studentID"]) === 6) {
    if (is_int($_GET["weekDay"]) && $_GET["weekDay"] < 7 && $_GET["weekDay"] > -1) {
        $day = $_GET["weekDay"];
    } else {
        $day = date("d");
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
                foreach ($schedule_changes->file_manager->schedule_reader->getGeneralChanges() as $general_change) {
                    echo $general_change . "<br />";
                }
                echo "<h2>Roosterwijzigingen voor " . $schedule_changes->mySQL->getSchoolSQL()->getStudentName($schedule_changes->student_id) . "</h2><br />";
                foreach ($schedule_changes->file_manager->schedule_reader->getSpecificChanges() as $specific_change) {
                    echo $specific_change . "<br />";
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
