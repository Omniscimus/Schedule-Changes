package net.omniscimus.profielwerkstuk.text;

import java.util.Calendar;
import java.util.Timer;

/**
 * Manager voor de uurlijkse download van het nieuwe bestand met
 * roosterwijzigingen.
 *
 * @author omniscimus
 */
public class DownloadScheduler {

    private final FileManager fileManager;

    /**
     * Maakt een nieuwe DownloadScheduler.
     *
     * @param fileManager de manager van deze class
     */
    public DownloadScheduler(FileManager fileManager) {
	this.fileManager = fileManager;
    }

    private Timer timer;

    /**
     * Geeft de manager van de bestanden met roosterwijzigingen.
     *
     * @return de bijbehorende FileManager
     */
    public FileManager getFileManager() {
	return fileManager;
    }

    /**
     * Start met elk uur de server checken voor een nieuw bestand met
     * roosterwijzigingen, en check ook nu.
     */
    public void startScheduling() {
	timer = new Timer();
	FileFetcher fileFetcher = new FileFetcher(this);
	fileFetcher.run();
	timer.scheduleAtFixedRate(fileFetcher, timeToNextHour(), 1000 * 60 * 60);
    }

    /**
     * Stopt alle geplande downloads.
     */
    public void stopScheduling() {
	timer.cancel();
    }

    /**
     * Geeft de tijd die het nog duurt voor een nieuw uur van de dag begint.
     *
     * @return de tijd tot aan het volgende uur, in milliseconden
     */
    public long timeToNextHour() {
	Calendar cal = Calendar.getInstance();
	int minutes = cal.get(Calendar.MINUTE);
	int seconds = cal.get(Calendar.SECOND);
	int millis = cal.get(Calendar.MILLISECOND);
	int minutesToNextHour = 60 - minutes;
	int secondsToNextHour = 60 - seconds;
	int millisToNextHour = 1000 - millis;
	return minutesToNextHour * 60 * 1000 + secondsToNextHour * 1000 + millisToNextHour;
    }

}
