package net.omniscimus.profielwerkstuk.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.profielwerkstuk.configuration.ConfigValueCache;

/**
 * Verzorgt de verbinding met de MySQL database.
 *
 * @author omniscimus
 */
public class MySQLManager {

    private final SchoolSQL schoolSQL;
    private final ScheduleChangesSQL scheduleChangesSQL;
    private final DatabaseLink databaseLink;

    /**
     * Maakt een nieuwe MySQLManager; opent de verbinding met de MySQL server.
     *
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public MySQLManager() throws SQLException, ClassNotFoundException {
	schoolSQL = new SchoolSQL(this);
	scheduleChangesSQL = new ScheduleChangesSQL(this);
	databaseLink = new DatabaseLink(scheduleChangesSQL, schoolSQL);

	openConnection();
    }

    /**
     * Geeft de DatabaseLink die dit programma gebruikt.
     *
     * @return de te gebruiken DatabaseLink
     */
    public DatabaseLink getDatabaseLink() {
	return databaseLink;
    }

    /**
     * Geeft de SchoolSQL die dit programma gebruikt.
     *
     * @return de te gebruiken SchoolSQL
     */
    public SchoolSQL getSchoolSQL() {
	return schoolSQL;
    }

    /**
     * Geeft de ScheduleChangesSQL die dit programma gebruikt.
     *
     * @return de te gebruiken ScheduleChangesSQL
     */
    public ScheduleChangesSQL getScheduleChangesSQL() {
	return scheduleChangesSQL;
    }

    private Connection connection;

    /**
     * Opent de verbinding met de MySQL server. Gebruikt de gegevens die in
     * ConfigValueCache staan.
     *
     * @return de gemaakte verbinding
     * @throws SQLException als er geen verbinding kon worden geopend
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    private Connection openConnection() throws SQLException, ClassNotFoundException {
	Class.forName("com.mysql.jdbc.Driver");
	connection = DriverManager.getConnection("jdbc:mysql://" + ConfigValueCache.getSQLHostname() + ":" + ConfigValueCache.getSQLPort(),
		ConfigValueCache.getSQLUsername(), ConfigValueCache.getSQLPassword());
	return connection;
    }

    /**
     * Geeft de huidige MySQL verbinding, of maakt een nieuwe als er nog geen is
     * of de huidige niet bruikbaar is.
     *
     * @return een bruikbare verbinding met de MySQL database
     * @throws SQLException als er geen verbinding kon worden geopend
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public Connection getConnection() throws SQLException, ClassNotFoundException {
	if (connection != null && !connection.isClosed() && connection.isValid(3)) {
	    return connection;
	} else {
	    return openConnection();
	}
    }

    /**
     * Sluit de verbinding met de MySQL-database.
     */
    public void closeConnection() {
	try {
	    if (connection != null && !connection.isClosed() && connection.isValid(3)) {
		connection.close();
	    }
	} catch (Exception ex) {
	    Logger.getLogger(MySQLManager.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

}
