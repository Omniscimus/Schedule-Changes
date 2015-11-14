<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Persoonlijke wijzigingen</title>
    </head>
    <body>
        <?php
        if(is_numeric($_POST["studentID"]) && strlen($_POST["studentID"]) === 6) {
            echo $_POST["studentID"];
        }
        ?>
    </body>
</html>
