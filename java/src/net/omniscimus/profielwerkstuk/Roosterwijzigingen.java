package net.omniscimus.profielwerkstuk;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.profielwerkstuk.configuration.ConfigurationManager;
import net.omniscimus.profielwerkstuk.mysql.MySQLManager;
import net.omniscimus.profielwerkstuk.net.NetworkManager;
import net.omniscimus.profielwerkstuk.ui.UIManager;
import net.omniscimus.profielwerkstuk.text.FileManager;

/**
 * Main class voor dit project.
 *
 * @author omniscimus
 */
public class Roosterwijzigingen {

    private ConfigurationManager configurationManager;
    private FileManager fileManager;
    private MySQLManager mySQLManager;
    private NetworkManager networkManager;
    private UIManager uiManager;

    /**
     * Een statisch toegangspunt voor dit programma.
     */
    public static Roosterwijzigingen rw;

    /**
     * Geeft de DownloadScheduler van dit programma.
     *
     * @return de DownloadScheduler die dit programma gebruikt
     */
    public FileManager getFileManager() {
	return fileManager;
    }

    /**
     * Geeft de MySQLManager van dit programma.
     *
     * @return de MySQLManager die dit programma gebruikt
     */
    public MySQLManager getMySQLManager() {
	return mySQLManager;
    }

    /**
     * Main method van de class, aangeroepen als het programma start.<br>
     * Flags:<br>
     * <li>-n: print beschikbare network interfaces en sluit af</li>
     *
     * @param args de command-line arguments
     */
    public static void main(String[] args) {
	for (String arg : args) {
	    switch (arg) {
		case "-n":
		    NetworkManager.printNetworkInterfaces();
		    System.exit(0);
		    break;
	    }
	}
	new Roosterwijzigingen().initiate();
    }

    /**
     * Start het programma.
     */
    public void initiate() {

	rw = this;

	// Load de config values.
	configurationManager = new ConfigurationManager(this);
	try {
	    configurationManager.loadConfig();
	} catch (IOException | URISyntaxException ex) {
	    Logger.getLogger(Roosterwijzigingen.class.getName()).log(
		    Level.SEVERE, "Configuratie kon niet geladen worden", ex);
	    shutdown("Kon de configuratie niet laden.", false);
	}

	// Start de MySQL verbinding.
	try {
	    mySQLManager = new MySQLManager();
	} catch (SQLException | ClassNotFoundException ex) {
	    Logger.getLogger(Roosterwijzigingen.class.getName()).log(
		    Level.SEVERE, "MySQL kon niet geladen worden", ex);
	    shutdown(ex.getMessage(), false);
	}

	// Start een scheduler die elk uur kijkt of er een nieuw bestand is op de server
	fileManager = new FileManager(this);
	fileManager.load();

	// Start de User Interface.
	uiManager = new UIManager(this);
	uiManager.load();

	// Laad de netwerkmanager en start een scan interval.
	networkManager = new NetworkManager(this);
	networkManager.load();

    }

    /**
     * Stopt het programma.
     *
     * @param error een bericht met de reden voor de shutdown
     * @param force true als het programma onmiddelijk moet worden afgesloten;
     * anders false
     */
    public void shutdown(String error, boolean force) {
	System.out.println(error);
	shutdown(force);
    }

    /**
     * Stopt het programma.
     *
     * @param force true als het programma onmiddelijk moet worden afgesloten;
     * anders false
     */
    public void shutdown(boolean force) {
	if (!force) {
	    networkManager.stop();
	    uiManager.stop();
	    fileManager.stop();
	    if (mySQLManager != null) {
		mySQLManager.closeConnection();
	    }
	}
	System.exit(0);
    }

}
