<?php
require_once 'sql/MySQL_Manager.php';

if (is_numeric($_POST["studentID"]) && strlen($_POST["studentID"]) === 6) {
    $student_id = $_POST["studentID"];
    $mySQL = new MySQL_Manager();
    $file_downloader = new File_Downloader();
    $file_downloader->deleteOldScheduleFiles();
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
        if (isset($student_id)) {
            try {
            echo "<h2>Algemene roosterwijzigingen</h2><br />";
            
            echo "<h2>Roosterwijzigingen voor " . $mySQL->getSchoolSQL()->getStudentName($student_id) . "</h2><br />";
            } catch(Exception $e) {
                echo $e->getMessage();
            }
        } else {
            echo "<p>Leerlingnummer niet gevonden.</p>";
        }
        ?>
    </body>
</html>
<?php
$mySQL->closeConnection();
