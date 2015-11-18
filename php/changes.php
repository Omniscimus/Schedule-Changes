<?php
if (is_numeric($_POST["studentID"]) && strlen($_POST["studentID"]) === 6) {
    $schedule_changes = new Schedule_Changes($_POST["studentID"]);
    $schedule_changes->initiate();
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

                echo "<h2>Roosterwijzigingen voor " . $schedule_changes->mySQL->getSchoolSQL()->getStudentName($student_id) . "</h2><br />";
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
