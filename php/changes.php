<?php
require_once 'Schedule_Changes.php';
$config = include 'config.php';
date_default_timezone_set($config["default_timezone"]);
// Schakel waarschuwingen uit zodat er geen warning verschijnt als de URL naar
// het bestand met roosterwijzigingen een 404 response code oplevert.
//error_reporting(E_ERROR | E_WARNING | E_PARSE | E_NOTICE);
error_reporting(E_ERROR);

$today = date("w");
$tomorrow = ($today + 1)%7;

if (is_numeric($_GET["studentID"]) && strlen($_GET["studentID"]) === 6) {
    if (isset($_POST["weekDay"]) && $_POST["weekDay"] < 7 && $_POST["weekDay"] > -1) {
        $day = $_POST["weekDay"];
    } else {
        $day = $today;
    }
    $schedule_changes = new Schedule_Changes($_GET["studentID"], $day);
}
?>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Persoonlijke wijzigingen</title>
        <!-- Foundation and custom stylesheets -->
        <link rel="stylesheet" type="text/css" href="foundation/css/foundation.css">
        <link rel="stylesheet" type="text/css" href="foundation/css/schedule-changes.css">
    </head>
    <body>
        <div class="top-bar">
            <div class="row">
                <div class="top-bar-left small-12 medium-6 columns" style="padding-left: 0px">
                    <div class="title">Roosterwijzigingen</div>
                </div>
                <div class="top-bar-right">
                    <form action="" method="post">
                        <div class="button-group">
                            <button name="weekDay" value="<?php echo $today; ?>" type="submit" class="small-6 columns
                        button <?php if
                            ($today == $day) echo "disabled"; ?>">Vandaag</button>
                            <button name="weekDay" value="<?php echo $tomorrow; ?>" type="submit" class="small-6
                            columns button <?php if
                            ($tomorrow
                              == $day) echo "disabled"; ?>">Morgen</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div clas="row" style="margin-top: 1em;">
        <?php
        if (isset($schedule_changes)) {
            try {
                $general_changes = $schedule_changes->file_manager->schedule_reader->getGeneralChanges();
                if (!empty($general_changes)) {
                    foreach ($general_changes as $general_change) {
                        DisplayMessage($general_change, "general");
                    }
                } else {
                    echo "Er zijn geen algemene roosterwijzigingen voor deze dag.";
                }
                echo "<h2>Roosterwijzigingen voor " . $schedule_changes->mySQL->getSchoolSQL()->getStudentName($schedule_changes->student_id) . "</h2><br />";
                $specific_changes = $schedule_changes->file_manager->schedule_reader->getSpecificChanges();
                if (!empty($specific_changes)) {
                    foreach ($specific_changes as $specific_change) {
                        DisplayMessage($specific_change, "specific");
                    }
                } else {
                    echo "Je hebt geen persoonlijke roosterwijzigingen voor deze dag.";
                }
            } catch (Exception $e) {
                echo $e->getMessage();
            }
        } else {
            DisplayMessage("Leerlingnummer niet gevonden.", "warning");
        }


        ?>
        </div>
    </body>
</html>
<?php

// DisplayMessage("today: " . $today . " tomorrow: " . $tomorrow . " day: " . $day . "<br />", "warning");

if (isset($schedule_changes)) {
    $schedule_changes->mySQL->closeConnection();
}

/**
 * Gebruikt echo om een bericht als messageItem (css) weer te geven.
 *
 * @param string $message Het bericht dat weergegeven moet worden.
 * @param string $type De manier waarop het bericht weergegeven moet worden -> [general, specific, warning].
 * */
function DisplayMessage($message, $type)
{
    echo "
<div class='row'>
    <div class='messageItem'>
        <div class='small-12 medium-offset-1 columns $type'>
              $message
        </div>
    </div>
</div>
";
}