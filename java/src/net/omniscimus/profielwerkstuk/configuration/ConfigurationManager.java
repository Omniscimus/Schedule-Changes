package net.omniscimus.profielwerkstuk.configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.profielwerkstuk.ScheduleChanges;

/**
 * Zorgt voor het lezen van en schrijven naar de config.
 *
 * @author omniscimus
 */
public class ConfigurationManager {

    private final ScheduleChanges scheduleChanges;
    private File configFile;

    /**
     * Maakt een nieuwe ConfigurationManager.
     *
     * @param scheduleChanges de basis van het programma
     */
    public ConfigurationManager(ScheduleChanges scheduleChanges) {
	this.scheduleChanges = scheduleChanges;
    }

    /**
     * Laadt de waarden uit het configuratiebestand naar de cache.
     *
     * @throws IOException als het doelbestand niet geopend kon worden
     * @throws URISyntaxException als het bestand in het .jar bestand niet
     * geopend kan worden
     */
    public void loadConfig() throws IOException, URISyntaxException {

	try (BufferedReader br = new BufferedReader(new FileReader(getConfigFile()))) {
	    String line;
	    while ((line = br.readLine()) != null) {
		if (line.startsWith("hotspot-interface")) {
		    ConfigValueCache.setHotspotInterface(line.replace("hotspot-interface: ", ""));
		} else if (line.startsWith("ping-timeout")) {
		    ConfigValueCache.setPingTimeout(Integer.parseInt(line.replace("ping-timeout: ", "")));
		} else if (line.startsWith("interaction-timeout")) {
		    ConfigValueCache.setInteractionTimeout(Integer.parseInt(line.replace("interaction-timeout: ", "")));
		} else if (line.startsWith("mysql-hostname")) {
		    ConfigValueCache.setSQLHostname(line.replace("mysql-hostname: ", ""));
		} else if (line.startsWith("mysql-port")) {
		    ConfigValueCache.setSQLPort(line.replace("mysql-port: ", ""));
		} else if (line.startsWith("mysql-username")) {
		    ConfigValueCache.setSQLUsername(line.replace("mysql-username: ", ""));
		} else if (line.startsWith("mysql-password")) {
		    ConfigValueCache.setSQLPassword(line.replace("mysql-password: ", ""));
		} else if (line.startsWith("admin-code")) {
		    try {
			ConfigValueCache.setAdminCode(Integer.parseInt(line.replace("admin-code: ", "")));
		    } catch (NumberFormatException e) {
			scheduleChanges.shutdown("Kon de admin code niet lezen uit de config! (" + line + ")", false);
		    }
		}
	    }
	} catch (IOException e) {
	    Logger.getLogger(ScheduleChanges.class.getName()).log(Level.SEVERE, "Het configuratiebestand kon niet geladen worden.", e);
	    scheduleChanges.shutdown("Kon het configuratiebestand niet lezen!", false);
	}

    }

    /**
     * Geeft het configuratiebestand; maakt een nieuwe aan als het nog niet
     * bestaat.
     *
     * @return het bestand met de configuratiewaarden
     */
    private File getConfigFile() throws IOException, URISyntaxException {

	if (configFile != null) {
	    return configFile;
	}

	// Het pad is ~/Roosterwijzigingen/config.txt
	String configPath
		= ConfigValueCache.getSaveFolderPath()
		+ File.separator + "config.txt";

	File newConfigFile;
	try {
	    newConfigFile = validateFile(configPath, false);
	} catch (IOException ex) {
	    newConfigFile = new File(configPath);
	    replaceConfigWithDefault(newConfigFile);
	}

	this.configFile = newConfigFile;
	return configFile;

    }

    /**
     * Kopieert de het standaard bestand met configuratiewaarden naar de
     * opgegeven plaats. Als er al een bestand bestaat op de opgegeven plaats,
     * wordt dat bestand gewist en het nieuwe er overheen geschreven.
     *
     * @param destination het bestand waarnaar het configuratiebestand
     * gekopieerd moet worden
     * @throws IOException als het doelbestand niet geopend kon worden
     * @throws URISyntaxException als het bestand in het .jar bestand niet
     * geopend kan worden
     */
    public void replaceConfigWithDefault(File destination) throws IOException, URISyntaxException {
	// Wis een eventueel al bestaand bestand
	destination.delete();
	destination.getParentFile().mkdirs();
	destination.createNewFile();

	OutputStream os;
	// Haal de default config.txt uit de .jar. Gebruik hier niet
	// File.separator omdat het hier niet gaat om bestandsnamen maar om Java
	// packages.
	try (InputStream is = ScheduleChanges.class.getResourceAsStream("/config.txt")) {
	    int readBytes;
	    byte[] buffer = new byte[4096];
	    // Kopieer naar destination
	    os = new FileOutputStream(destination.getAbsolutePath());
	    while ((readBytes = is.read(buffer)) > 0) {
		os.write(buffer, 0, readBytes);
	    }
	}
	os.close();
    }

    /**
     * Check of een map of bestand is wat het zou moeten zijn.
     *
     * @param path het pad naar het bestand
     * @param directory moet dit bestand een map zijn? (anders is het een
     * bestand)
     * @return het bestand of de map als het aan alle voorwaarden voldoet;
     * anders null
     * @throws IOException als iets niet klopt aan het bestand of de map
     */
    private File validateFile(String path, boolean directory) throws IOException {
	if (path == null) {
	    return null;
	}
	File file = new File(path);
	if (!file.exists()) {
	    throw new IOException(path + " bestaat niet!");
	} else {
	    if (directory && !file.isDirectory()) {
		throw new IOException(path + " is een bestand, maar moet een map zijn!");
	    } else if (!directory && file.isDirectory()) {
		throw new IOException(path + " is een map, maar moet een bestand zijn!");
	    }
	}
	return file;
    }

}
