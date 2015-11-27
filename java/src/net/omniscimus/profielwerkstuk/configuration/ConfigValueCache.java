package net.omniscimus.profielwerkstuk.configuration;

import java.io.File;

/**
 * Opslagplaats voor waarden in het configuratiebestand.
 *
 * @author omniscimus
 */
public class ConfigValueCache {

    private static String hotspotInterface;

    /**
     * Geeft de naam van de netwerkinterface waarop gescand moet worden; bijv.
     * eth0 of en0
     *
     * @return de systeemnaam van de Network Interface waarop gescand moet
     * worden voor nieuwe IP's
     */
    public static String getHotspotInterface() {
	return hotspotInterface;
    }

    /**
     * Verandert de gebruikte hotspot interface naar de gegeven naam.
     *
     * @param newHotspotInterface de naam van de hotspot interface die gebruikt
     * moet worden
     */
    public static void setHotspotInterface(String newHotspotInterface) {
	hotspotInterface = newHotspotInterface;
    }

    private static long scanDelay;

    /**
     * Geeft de tijd die tussen elke port scan van het hotspot netwerk gewacht
     * moet worden.
     *
     * @return de tijd tussen elke scan in milliseconden
     */
    public static long getScanDelay() {
	return scanDelay;
    }

    /**
     * Verandert de tijd die tussen elke port scan gewacht moet worden.
     *
     * @param newScanDelay de nieuwe delay voor netwerkscans
     */
    public static void setScanDelay(Long newScanDelay) {
	scanDelay = newScanDelay;
    }

    private static int pingTimeout;

    /**
     * Geeft de timeout voor een ping: de tijd die een device heeft om te
     * reageren op een ping van het programma.
     *
     * @return de ping timeout, in milliseconden
     */
    public static int getPingTimeout() {
	return pingTimeout;
    }

    /**
     * Verandert de timeout voor een ping.
     *
     * @param newPingTimeout de nieuwe ping timeout, in milliseconden
     */
    public static void setPingTimeout(int newPingTimeout) {
	pingTimeout = newPingTimeout;
    }

    private static int interactionTimeout;

    /**
     * Geeft de tijd die er gewacht moet worden voor er vanuit specifieke frames
     * teruggeschakeld wordt naar het Homescreen.
     *
     * @return de timeout voor terugkeer naar het homescreen, in milliseconden
     */
    public static int getInteractionTimeout() {
	return interactionTimeout;
    }

    /**
     * Verandert de tijd die er gewacht moet worden voor er vanuit specifieke
     * frames teruggeschakeld wordt naar het Homescreen.
     *
     * @param newInteractionTimeout de nieuwe timeout voor terugkeer naar het
     * homescreen, in seconden
     */
    public static void setInteractionTimeout(int newInteractionTimeout) {
	interactionTimeout = newInteractionTimeout * 1000;
    }

    private static String sqlHostname;

    /**
     * Geeft de hostname (IP-adres) van de MySQL server waarmee verbonden moet
     * worden. Bijv: localhost of 127.0.0.1 of 94.103.157.235
     *
     * @return de hostname van de te gebruiken MySQL server
     */
    public static String getSQLHostname() {
	return sqlHostname;
    }

    /**
     * Verandert de te gebruiken hostname van de MySQL server.
     *
     * @param newSQLHostname de nieuwe hostname van de te gebruiken MySQL server
     */
    static void setSQLHostname(String newSQLHostname) {
	sqlHostname = newSQLHostname;
    }

    private static String sqlPort;

    /**
     * Geeft de poort van de MySQL server waarmee verbonden moet worden.
     *
     * @return de poort van de te gebruiken MySQL database
     */
    public static String getSQLPort() {
	return sqlPort;
    }

    /**
     * Verandert de poort van de MySQL server waarmee verbonden moet worden.
     *
     * @param newSQLPort de nieuwe poort van de te gebruiken MySQL server
     */
    static void setSQLPort(String newSQLPort) {
	sqlPort = newSQLPort;
    }

    private static String sqlUsername;

    /**
     * Geeft de gebruikersnaam voor het openen van een verbinding met de MySQL
     * server.
     *
     * @return de gebruikersnaam die geautoriseerd is om de benodigde
     * verbindingen met de MySQL server te leggen
     */
    public static String getSQLUsername() {
	return sqlUsername;
    }

    /**
     * Verandert de gebruikersnaam die gebruikt wordt voor de verbinding met de
     * MySQL server.
     *
     * @param newSQLUsername de nieuwe gebruikersnaam voor de verbinding met de
     * MySQL server
     */
    static void setSQLUsername(String newSQLUsername) {
	sqlUsername = newSQLUsername;
    }

    private static String sqlPassword;

    /**
     * Geeft het wachtwoord dat hoort bij de opgegeven username.
     *
     * @return het wachtwoord van de user van de MySQL server
     */
    public static String getSQLPassword() {
	return sqlPassword;
    }

    /**
     * Verandert het MySQL wachtwoord voor de verbinding met de MySQL server.
     *
     * @param newSQLPassword het nieuwe wachtwoord dat gebruikt moet worden voor
     * verbindingen met de MySQL server
     */
    static void setSQLPassword(String newSQLPassword) {
	sqlPassword = newSQLPassword;
    }

    private static int adminCode;

    /**
     * Geeft de admin code die gebruikt kan worden om het programma af te
     * sluiten.
     *
     * @return de admin code uit het configuratiebestand
     */
    public static int getAdminCode() {
	return adminCode;
    }

    /**
     * Verandert de admin code die gebruikt kan worden om het programma af te
     * sluiten.
     *
     * @param newAdminCode de nieuwe admin code
     */
    static void setAdminCode(int newAdminCode) {
	adminCode = newAdminCode;
    }

    /**
     * Geeft het pad naar de map waarin de configuratiebestanden van dit
     * programma gezet moeten worden. Dit is ~/Roosterwijzigingen.
     *
     * @return het pad naar de map van de save folder
     */
    public static final String getSaveFolderPath() {
	return System.getProperty("user.home")
		+ File.separator
		+ "Roosterwijzigingen";
    }

}
