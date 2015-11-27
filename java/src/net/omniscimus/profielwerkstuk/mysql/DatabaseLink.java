package net.omniscimus.profielwerkstuk.mysql;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Zorgt ervoor dat queries van beide databases op een makkelijke manier aan
 * elkaar gekoppeld kunnen worden.
 *
 * @author omniscimus
 */
public class DatabaseLink {

    private final RoosterwijzigingenSQL rwSQL;
    private final SchoolSQL sSQL;

    /**
     * Maakt een nieuwe DatabaseLink.
     *
     * @param rwSQL het toegangspunt voor de database van dit programma
     * @param sSQL het toegangspunt voor de database die door school geleverd
     * wordt
     */
    public DatabaseLink(RoosterwijzigingenSQL rwSQL, SchoolSQL sSQL) {
	this.rwSQL = rwSQL;
	this.sSQL = sSQL;
    }

    /**
     * Zoekt de naam van een leerling bij zijn/haar MAC-adres.
     *
     * @param mac het MAC-adres dat aan een leerling gekoppeld moet worden
     * @return de naam van de leerling met het gegeven MAC-adres
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public String getNameByMACAddress(String mac)
	    throws SQLException, ClassNotFoundException {
	return sSQL.getStudentName(rwSQL.getLeerlingnummer(mac));
    }

    /**
     * Zoekt de klassen bij de leerling met het gegeven MAC-adres.
     *
     * @param mac het MAC-adres van een device van de leerling
     * @return een lijst met klassen waar de leerling toe behoort
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public ArrayList<String> getSchoolClassesByMACAddress(String mac)
	    throws SQLException, ClassNotFoundException {
	return sSQL.getSchoolClasses(rwSQL.getLeerlingnummer(mac));
    }

}
