<?php
require_once 'sql/MySQL_Manager.php';
require_once 'text/File_Downloader.php';
require_once 'text/File_Processor.php';

$config = include 'config.php';
date_default_timezone_set($config["default_timezone"]);

if (is_numeric($_POST["studentID"]) && strlen($_POST["studentID"]) === 6) {
    $student_id = $_POST["studentID"];
    $mySQL = new MySQL_Manager();
    $file_downloader = new File_Downloader();
    $file_downloader->deleteOldScheduleFiles();
    $file_processor = new File_Processor($file_downloader->downloadScheduleFile($file_downloader->getTodayNumber()));
    $file_processor->processFile();
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
$mySQL->closeConnection();
