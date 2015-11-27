package net.omniscimus.profielwerkstuk.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Toegangspunt voor de SQL database van de Roosterwijzigingen app waarin de
 * leerlingnummers aan de MAC-adressen van de devices van leerlingen gekoppeld
 * staan.
 *
 * @author omniscimus
 */
public class ScheduleChangesSQL {

    private final MySQLManager mySQLManager;

    /**
     * Maakt een nieuwe RoosterwijzigingenSQL.
     *
     * @param mySQLManager de MySQLManager waarvan de verbinding verkregen zal
     * worden.
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public ScheduleChangesSQL(MySQLManager mySQLManager) throws SQLException, ClassNotFoundException {
	this.mySQLManager = mySQLManager;
	try (Statement databaseCreator = mySQLManager.getConnection().createStatement()) {
	    databaseCreator.executeUpdate("CREATE DATABASE IF NOT EXISTS roosterwijzigingen;");
	}
	try (Statement tableCreator = mySQLManager.getConnection().createStatement()) {
	    tableCreator.executeUpdate("CREATE TABLE IF NOT EXISTS roosterwijzigingen.leerlingen ( leerlingnummer MEDIUMINT(6) UNSIGNED NOT NULL, macadres CHAR(12), registratiedatum TIMESTAMP DEFAULT CURRENT_TIMESTAMP, laatstingelogd TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");
	    tableCreator.close();
	}
	// CREATE TABLE IF NOT EXISTS roosterwijzigingen.leerlingen
	// ( leerlingnummer MEDIUMINT(6) UNSIGNED NOT NULL, macadres CHAR(12), registratiedatum TIMESTAMP DEFAULT CURRENT_TIMESTAMP, laatstingelogd TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
    }

    /**
     * Slaat een nieuwe leerling met zijn/haar MAC-adres op in de database.
     *
     * @param studentID het leerlingnummer van de leerling
     * @param macAddress het te registreren MAC-adres van de leerling
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public void saveNewUser(int studentID, String macAddress) throws SQLException, ClassNotFoundException {

	try (PreparedStatement preparedStatement = mySQLManager.getConnection()
		.prepareStatement("INSERT INTO roosterwijzigingen.leerlingen (leerlingnummer, macadres) VALUES (?, ?);")) {
	    preparedStatement.setInt(1, studentID);
	    preparedStatement.setString(2, macAddressToDatabase(macAddress));
	    preparedStatement.executeUpdate();

	    // INSERT INTO roosterwijzigingen.leerlingen (leerlingnummer, macadres)
	    // VALUES (123456, '1234567890ab');
	    // laatstingelogd en registratiedatum worden automatisch naar de huidige
	    // datum gezet
	}
    }

    /**
     * Verwijdert de registratie van de leerling met het gegeven MAC-adres.
     *
     * @param macAddress het MAC-adres van de leerling
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public void deleteUser(String macAddress) throws SQLException, ClassNotFoundException {
	try (Statement deleteStatement = mySQLManager.getConnection().createStatement()) {
	    deleteStatement.executeUpdate("DELETE FROM roosterwijzigingen.leerlingen WHERE macadres = '" + macAddressToDatabase(macAddress) + "';");
	}
    }

    /**
     * Zoekt het leerlingnummer op van de leerling met het gegeven MAC-adres.
     *
     * @param macAddress het MAC-adres van de leerling
     * @return het leerlingnummer van de eigenaar van het gegeven MAC-adres, of
     * 0 als het adres niet geregistreerd is
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public int getStudentID(String macAddress) throws SQLException, ClassNotFoundException {

	macAddress = macAddressToDatabase(macAddress);

	int result;

	ResultSet resultSet;
	Statement updateStatement;
	try (Statement selectStatement = mySQLManager.getConnection().createStatement()) {
	    resultSet = selectStatement
		    .executeQuery("SELECT leerlingnummer FROM roosterwijzigingen.leerlingen WHERE macadres = '" + macAddress + "';");
	    updateStatement = mySQLManager.getConnection().createStatement();
	    updateStatement.executeUpdate("UPDATE roosterwijzigingen.leerlingen SET laatstingelogd=now() WHERE macadres='" + macAddress + "';");
	    if (resultSet.next()) {
		result = resultSet.getInt("leerlingnummer");
	    } else {
		result = 0;
	    }
	}
	updateStatement.close();
	return result;

	// SELECT leerlingnummer FROM roosterwijzigingen.leerlingen
	// WHERE macadres = '1234567890ab';
	// UPDATE roosterwijzigingen.leerlingen
	// SET laatstingelogd=now() WHERE macadres='1234567890ab';
    }

    /**
     * Geeft aan of een MAC-adres al gekoppeld is aan een leerling.
     *
     * @param macAddress het MAC-adres dat opgezocht moet worden
     * @return true als het MAC-adres geregistreerd is; anders false
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public boolean macAddressIsRegistered(String macAddress) throws SQLException, ClassNotFoundException {
	return getStudentID(macAddress) != 0;
    }

    /**
     * Verandert een MAC-adres van het formaat 12:34:56:78:90:ab of
     * 12-34-56-78-90-ab naar het formaat 1234567890ab. Voegt een 0 toe als de
     * byte &lt; 16.
     *
     * @param mac een MAC-adres waar streepjes of dubbele punten in staan
     * @return het MAC-adres geconverteerd naar het formaat dat in de database
     * past
     */
    public String macAddressToDatabase(String mac) {
	String[] bytes = mac.split(":");
	if (bytes.length < 2) {
	    bytes = mac.split("-");
	}
	StringBuilder databaseMACBuilder = new StringBuilder();
	for (String sub : bytes) {
	    if (sub.length() == 1) {
		sub = 0 + sub;
	    }
	    databaseMACBuilder.append(sub);
	}
	return databaseMACBuilder.toString();
    }

    /**
     * Verandert een MAC-adres van het formaat 1234567890ab naar het formaat
     * 12:34:56:78:90:ab.
     *
     * @param mac een MAC-adres dat in het formaat van de database staat
     * @return het MAC-adres waarbij tussen de bytes dubbele punten staan
     */
    public String macAddressToReadable(String mac) {
	char[] macChars = mac.toCharArray();
	StringBuilder readableMACBuilder = new StringBuilder();
	boolean flipflop = true;
	for (char c : macChars) {
	    readableMACBuilder.append(c);
	    if (!flipflop) {
		readableMACBuilder.append(":");
	    }
	    flipflop = !flipflop;
	}
	return readableMACBuilder.toString();
    }

}
