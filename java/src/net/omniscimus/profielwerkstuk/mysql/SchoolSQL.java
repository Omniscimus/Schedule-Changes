package net.omniscimus.profielwerkstuk.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Toegangspunt voor de SQL database die de school levert, waarin onder andere
 * de leerlingnummers gekoppeld staan aan de naam van leerlingen. De
 * schooldatabase wordt intact gelaten: data wordt er alleen uit gelezen, niet
 * naar geschreven.
 *
 * @author omniscimus
 */
public class SchoolSQL {

    private final MySQLManager mySQLManager;

    /**
     * Maakt een nieuwe SchoolSQL.
     *
     * @param mySQLManager de MySQLManager waarvan de verbinding verkregen zal
     * worden.
     */
    public SchoolSQL(MySQLManager mySQLManager) {
	this.mySQLManager = mySQLManager;
    }

    private ArrayList<String> possibleSchoolClasses;

    /**
     * Zoekt de leerlingnummers op die horen bij de opgegeven voor- en
     * achternaam.
     *
     * @param firstName de voornaam van de leerling
     * @param surname de achternaam van de leerling
     * @return een lijst met gevonden leerlingnummers (mogelijk zijn er meerdere
     * leerlingen met dezelfde naam). Deze lijst is leeg als er niemand gevonden
     * is met deze voor- en achternaam.
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public ArrayList<Integer> getStudentID(String firstName, String surname)
	    throws SQLException, ClassNotFoundException {

	ArrayList<Integer> leerlingnummers = new ArrayList<>();

	PreparedStatement preparedStatement = mySQLManager.getConnection()
		.prepareStatement("SELECT leerlingnummer FROM school.leerlingen WHERE voornaam = ? AND achternaam = ?;");
	preparedStatement.setString(1, firstName);
	preparedStatement.setString(2, surname);
	ResultSet resultSet = preparedStatement.executeQuery();

	while (resultSet.next()) {
	    leerlingnummers.add(resultSet.getInt("leerlingnummer"));
	}

	return leerlingnummers;

	// SELECT leerlingnummer FROM school.leerlingen
	// WHERE voornaam = voornaam
	// AND achternaam = achternaam;
    }

    /**
     * Zoekt de naam van een leerling op aan de hand van zijn/haar
     * leerlingnummer.
     *
     * @param studentID het leerlingnummer van de leerling wiens naam
     * opgezocht moet worden
     * @return de voornaam en achternaam van de leerling, in het formaat
     * Voornaam Achternaam, of slechts een van die als de ander niet in de
     * database staat, of null als de leerling niet is gevonden
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public String getStudentName(int studentID)
	    throws SQLException, ClassNotFoundException {

	Statement statement = mySQLManager.getConnection().createStatement();
	ResultSet resultSet = statement
		.executeQuery("SELECT voornaam,achternaam FROM school.leerlingen WHERE leerlingnummer = " + studentID + ";");

	if (resultSet.next()) {
	    String voornaam = resultSet.getString("voornaam");
	    String achternaam = resultSet.getString("achternaam");
	    if (voornaam == null && achternaam == null) {
		return null;
	    } else if (voornaam == null) {
		return achternaam;
	    } else if (achternaam == null) {
		return voornaam;
	    } else {
		return voornaam + " " + achternaam;
	    }
	}
	return null;

	// SELECT voornaam, achternaam FROM school.leerlingen
	// WHERE leerlingnummer = leerlingnummer;
    }

    /**
     * Zoekt alle klassen op waar een leerling in zit.
     *
     * @param studentID het nummer van de leerling waarvan de klassen
     * opgezocht moeten worden
     * @return een lijst met klassen waar de leerling in zit
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public ArrayList<String> getSchoolClasses(int studentID)
	    throws SQLException, ClassNotFoundException {

	Statement statement = mySQLManager.getConnection().createStatement();
	ResultSet resultSet = statement
		.executeQuery("SELECT klas FROM school.klassen WHERE leerlingnummer = " + studentID + ";");

	ArrayList<String> klassen = new ArrayList<>();

	while (resultSet.next()) {
	    klassen.add(resultSet.getString("klas"));
	}

	return klassen;

	// SELECT klas FROM school.klassen
	// WHERE leerlingnummer = leerlingnummer;
    }

    /**
     * Zoekt alle verschillende mogelijke klassen op waar een leerling in kan
     * zitten.
     *
     * @return een lijst met alle bestaande klassen
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public ArrayList<String> getAllSchoolClasses() throws SQLException, ClassNotFoundException {

	Statement statement = mySQLManager.getConnection().createStatement();
	ResultSet resultSet = statement
		.executeQuery("SELECT DISTINCT klas FROM school.klassen;");

	ArrayList<String> klassen = new ArrayList<>();

	while (resultSet.next()) {
	    klassen.add(resultSet.getString("klas").toLowerCase());
	}

	return klassen;

	// SELECT DISTINCT klas FROM school.klassen;
    }

    /**
     * Geeft of een bepaalde klas bestaat volgens de schooldatabase.
     *
     * @param schoolClass de klas die opgezocht moet worden
     * @return true als de klas bestaat en er geen fout optreedt tijdens een
     * eventuele MySQL query; anders false
     */
    public boolean schoolClassExists(String schoolClass) {
	try {
	    if (possibleSchoolClasses == null) {
		possibleSchoolClasses = getAllSchoolClasses();
	    }
	    return possibleSchoolClasses.contains(schoolClass.toLowerCase());
	} catch (SQLException | ClassNotFoundException e) {
	    return false;
	}
    }

}
