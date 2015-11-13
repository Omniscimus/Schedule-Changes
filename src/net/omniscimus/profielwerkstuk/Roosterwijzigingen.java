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
import net.omniscimus.profielwerkstuk.text.DownloadScheduler;

/**
 * Main class voor dit project.
 *
 * @author omniscimus
 */
public class Roosterwijzigingen {

    private ConfigurationManager configurationManager;
    private DownloadScheduler downloadScheduler;
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
    public DownloadScheduler getFileManager() {
	return downloadScheduler;
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
     * Main method van de class, aangeroepen als het programma start.
     *
     * @param args de command-line arguments
     */
    public static void main(String[] args) {
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
	    crash("Kon de configuratie niet laden.");
	}

	// Start de MySQL verbinding.
	try {
	    mySQLManager = new MySQLManager();
	} catch (SQLException | ClassNotFoundException ex) {
	    Logger.getLogger(Roosterwijzigingen.class.getName()).log(
		    Level.SEVERE, "MySQL kon niet geladen worden", ex);
	    crash(ex.getMessage());
	}

	// Start een scheduler die elk uur kijkt of er een nieuw bestand is op de server
	downloadScheduler = new DownloadScheduler(this);
	downloadScheduler.startScheduling();

	// Start de User Interface.
	uiManager = new UIManager(this);
	uiManager.load();

	// Laad de netwerkmanager en start een scan interval.
	networkManager = new NetworkManager(this);
	networkManager.load();

    }

    /**
     * Stopt het programma geforceerd.
     *
     * @param error een bericht met de reden voor de crash
     */
    public void crash(String error) {
	System.out.println(error);
	System.exit(0);
    }

}
