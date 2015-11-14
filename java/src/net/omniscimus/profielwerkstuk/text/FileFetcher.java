package net.omniscimus.profielwerkstuk.text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.profielwerkstuk.configuration.ConfigValueCache;

/**
 * Downloader voor bestanden over HTTP(S).
 *
 * @author omniscimus
 */
public class FileFetcher extends TimerTask {

    private final DownloadScheduler downloadScheduler;

    /**
     * Maakt een nieuwe FileFetcher.
     *
     * @param downloadScheduler de manager van deze taak
     */
    public FileFetcher(DownloadScheduler downloadScheduler) {
	this.downloadScheduler = downloadScheduler;
    }

    @Override
    public void run() {
	Calendar calendar = Calendar.getInstance();
	int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
	DayOfWeek yesterday = DayOfWeek.of(dayOfWeekNumber(dayOfWeek - 2));
	DayOfWeek today = DayOfWeek.of(dayOfWeekNumber(dayOfWeek - 1));
	DayOfWeek tomorrow = DayOfWeek.of(dayOfWeekNumber(dayOfWeek));

	deleteFiles(yesterday);

	try {
	    File todaysFile = downloadFileIfEligible(getNehalenniaURL(today));
	    processFile(todaysFile, true);
	    File tomorrowsFile = downloadFileIfEligible(getNehalenniaURL(tomorrow));
	    processFile(tomorrowsFile, false);
	} catch (IOException ex) {
	    Logger.getLogger(FileFetcher.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    /**
     * Geeft het nummer van de dag van de week dat gebruikt kan worden om
     * Calendar.get(Calendar.DAY_OF_WEEK) waarden te converteren naar DayOfWeek
     * waarden.
     *
     * @param dayOfWeekFromCalendar het nummer van de dag van de week, zoals
     * gegeven door Calendar.get(Calendar.DAY_OF_WEEK)
     * @return het nummer van de dag van de week, te gebruiken voor
     * DayOfWeek.of(int)
     */
    private int dayOfWeekNumber(int dayOfWeekFromCalendar) {
	if (dayOfWeekFromCalendar == 0) {
	    return 7;
	} else if (dayOfWeekFromCalendar == -1) {
	    return 6;
	} else if (dayOfWeekFromCalendar == -2) {
	    return 5;
	} else {
	    return dayOfWeekFromCalendar;
	}
    }

    /**
     * Haalt de roosterwijzigingen uit een HTML-bestand en schrijft ze naar de
     * cache.
     *
     * @param fileToProcess het te verwerken HTML-bestand
     * @param today true als het om de roosterwijzigingen van vandaag gaat;
     * anders false
     * @throws IOException als het bestand met roosterwijzigingen niet geopend
     * kon worden
     */
    public void processFile(File fileToProcess, boolean today) throws IOException {
	if (fileToProcess != null) {
	    String todaysFileName = fileToProcess.getName();
	    File processedFile = new File(
		    ConfigValueCache.getSaveFolderPath() + File.separator
		    + todaysFileName.substring(0, todaysFileName.length() - 4) + ".txt");
	    NehalenniaFileProcessor.processFile(fileToProcess.toPath(), processedFile.toPath());
	    downloadScheduler.getFileManager().getFileReader().writeChangesToCache(processedFile, today);
	}
    }

    /**
     * Downloadt het gegeven bestand als de webpagina bestaat en we nog geen
     * bestand met hetzelfde naam hebben.
     *
     * @param url een HTTP URL naar het te downloaden bestand
     * @return het bestand nadat het gedownload is, of null als er al een
     * bestand met dezelfde naam bestond of als de webpagina niet bestaat
     * @throws MalformedURLException als de gegeven url niet klopt
     * @throws ProtocolException als het HTTP GET protocol niet gebruikt kon
     * worden bij de gegeven url
     * @throws IOException als er geen verbinding tot stand gebracht kan worden
     */
    public File downloadFileIfEligible(String url)
	    throws MalformedURLException, ProtocolException, IOException {

	URL httpURL = new URL(url);
	if (fileIsAlreadyDownloaded(url) || getHTTPResponseCode(httpURL) == 404) {
	    return null;
	}
	File saveFile = new File(
		ConfigValueCache.getSaveFolderPath() + File.separator
		+ getRemoteFileName(url));
	downloadFile(httpURL, saveFile);
	return saveFile;
    }

    /**
     * Geeft of er op het lokale systeem, in de map van het programma, al een
     * bestand is dat dezelfde naam heeft als het bestand dat opgegeven wordt
     * door de url.
     *
     * @param url de HTTP URL naar het te checken bestand
     * @return true als er al een bestand bestaat met die naam; anders false
     */
    public boolean fileIsAlreadyDownloaded(String url) {
	File localFile = new File(getRemoteFileName(url));
	return localFile.exists();
    }

    /**
     * Geeft de bestandsnaam waar de opgegeven URL naar wijst.
     *
     * @param url de URL naar het bestand
     * @return de bestandsnaam, inclusief bestandsextensie, van het bestand
     * aangegeven door url
     */
    public String getRemoteFileName(String url) {
	return url.substring(url.lastIndexOf('/') + 1, url.length());
    }

    /**
     * Geeft de HTTP Response code die de server geeft bij het verbinden met het
     * gegeven adres.
     *
     * @param url de HTTP URL waarmee verbonden moet worden
     * @return de resulterende HTTP Response code
     * @throws ProtocolException als het GET-protocol niet gebruikt kan worden
     * @throws IOException als er geen verbinding met de server geopend kon
     * worden
     */
    public int getHTTPResponseCode(URL url)
	    throws ProtocolException, IOException {

	HttpURLConnection huc = (HttpURLConnection) url.openConnection();
	huc.setRequestMethod("GET");
	huc.connect();
	int responseCode = huc.getResponseCode();
	huc.disconnect();
	return responseCode;
    }

    /**
     * Downloadt een bestand vanaf een gegeven internetadres.
     *
     * @param url de HTTP URL waarvan het bestand gehaald moet worden
     * @param toLocalFile het bestand waarnaar gedownload moet worden
     * @return het bestand, nadat het gedownload is
     * @throws MalformedURLException als de opgegeven URL niet geldig is
     * @throws IOException als het doelbestand niet geopend kon worden
     */
    public File downloadFile(URL url, File toLocalFile)
	    throws MalformedURLException, IOException {
	ReadableByteChannel rbc = Channels.newChannel(url.openStream());

	// Zeker weten dat het doelbestand leeg is
	toLocalFile.delete();
	toLocalFile.createNewFile();

	try (FileOutputStream fos = new FileOutputStream(toLocalFile)) {
	    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}

	return toLocalFile;
    }

    /**
     * Geeft de URL voor het bestand met roosterwijzigingen van Nehalennia voor
     * de opgegeven dag.
     *
     * @param dayOfWeek de dag waarvoor de URL naar de roosterwijzigingen voor
     * moet worden opgezocht
     * @return een URL met het internetadres naar het correcte bestand met
     * roosterwijzigingen
     */
    public static String getNehalenniaURL(DayOfWeek dayOfWeek) {
	if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
	    dayOfWeek = DayOfWeek.MONDAY;
	}
	StringBuilder urlBuilder = new StringBuilder("https://files.itslearning.com/data/394/1076/rooster");
	String dayOfWeekPrefix = getDayOfWeekPrefix(dayOfWeek);
	urlBuilder.append(dayOfWeekPrefix);
	urlBuilder.append(".htm");
	return urlBuilder.toString();
    }

    /**
     * Verwijdert de bestanden met roosterwijzigingen voor de opgegeven dag van
     * de week.
     *
     * @param day de dag van de week waarvan de verouderde roosterwijzigingen
     * moeten worden verwijderd
     */
    private void deleteFiles(DayOfWeek day) {
	if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
	    day = DayOfWeek.FRIDAY;
	}
	File htmFileToDelete = new File(
		ConfigValueCache.getSaveFolderPath() + File.separator
		+ "rooster" + getDayOfWeekPrefix(day) + ".htm");
	File txtFileToDelete = new File(
		ConfigValueCache.getSaveFolderPath() + File.separator
		+ "rooster" + getDayOfWeekPrefix(day) + ".txt");
	htmFileToDelete.delete();
	txtFileToDelete.delete();
    }

    /**
     * Geeft de verkorte versie van een weekdag. Zo wordt 'woensdag' 'wo'.
     *
     * @param dayOfWeek de dag van de week waarvan de verkorte versie gegeven
     * moet worden
     * @return een verkorte versie van de weekdag
     */
    public static String getDayOfWeekPrefix(DayOfWeek dayOfWeek) {
	switch (dayOfWeek) {
	    case SUNDAY:
		return "zo";
	    case MONDAY:
		return "ma";
	    case TUESDAY:
		return "di";
	    case WEDNESDAY:
		return "wo";
	    case THURSDAY:
		return "do";
	    case FRIDAY:
		return "vr";
	    case SATURDAY:
		return "za";
	    default:
		return null;
	}
    }

}
