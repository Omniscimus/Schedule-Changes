# Schedule Changes

## Description
This is my final assignment for high school. Using the file with changes of schedule the school puts online every day, the program filters the changes by class. Next, using the school's database (which links students to classes), it can give students personalized changes of schedule. It has two versions: a Java interface and a PHP version.<br>

### Java
The Java version is supposed to run on a separate computer somewhere in the school. To identify themselves, students can use their mobile phones to connect to the program's WiFi network and type in their name. The student's ID then gets linked to their device's MAC-address in a MySQL database. Other ways to identify themselves are to type in their ID or to scan the barcode of their student pass.<br>
The program scans the network all the time to look for new connected devices. When it detects a new registered device, it displays the student's name, which the student can then click to get their personalized changes of schedule.

### PHP
The PHP version is supposed to run on a webserver somewhere on the internet. Students can type in their ID on a webpage and get their personalized changes of schedule, after they have been looked up by processing the file of changes using the school's database.

## Programming
This program is mostly written in Java, in English, however the comments are in Dutch because my high school is in the Netherlands. Some covered programming topics:
<ul>
<li>Java Swing</li>
<li>Java Networking</li>
<li>Java Multithreading</li>
<li>RegEx</li>
<li>PHP</li>
<li>HTML</li>
<li>CSS</li>
<li>(My)SQL</li>
</ul>
