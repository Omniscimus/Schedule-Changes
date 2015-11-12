package net.omniscimus.profielwerkstuk.text;

import java.util.Calendar;
import java.util.Timer;
import net.omniscimus.profielwerkstuk.Roosterwijzigingen;

/**
 * Manager voor de uurlijkse download van het nieuwe bestand met
 * roosterwijzigingen.
 *
 * @author omniscimus
 */
public class DownloadScheduler {

    private final NehalenniaFileReader fileReader;
    private ScheduleChangesCache todayCache;
    private ScheduleChangesCache tomorrowCache;

    /**
     * Geeft de ScheduleChangesCache voor de gespecifieerde dag.
     *
     * @param today true als de cache voor de roosterwijzigingen van vandaag
     * gegeven moet worden; false als de de cache voor de roosterwijzigingen van
     * morgen gegeven moet worden
     * @return de ScheduleChangesCache voor de gespecifieerde dag
     */
    public ScheduleChangesCache getScheduleCache(boolean today) {
	if (today) {
	    return todayCache;
	} else {
	    return tomorrowCache;
	}
    }

    /**
     * Maakt een nieuwe ScheduleChangesCache.
     *
     * @param today true als het de roosterwijzigingen van vandaag moet bewaren;
     * false als het de roosterwijzigingen van morgen moet bewaren
     * @return de nieuwe ScheduleChangesCache
     */
    public ScheduleChangesCache createScheduleChangesCache(boolean today) {
	if (today) {
	    todayCache = new ScheduleChangesCache();
	    return todayCache;
	} else {
	    tomorrowCache = new ScheduleChangesCache();
	    return tomorrowCache;
	}
    }

    /**
     * Maakt een nieuwe FileManager.
     *
     * @param rw de basis van dit programma
     */
    public DownloadScheduler(Roosterwijzigingen rw) {
	fileReader = new NehalenniaFileReader(rw, this);
    }

    /**
     * Geeft de NehalenniaFileReader die door deze DownloadScheduler gebruikt
     * wordt.
     *
     * @return een instance van NehalenniaFileReader die bij voorkeur gebruikt
     * moet worden
     */
    public NehalenniaFileReader getFileReader() {
	return fileReader;
    }

    /**
     * Start met elk uur de server checken voor een nieuw bestand met
     * roosterwijzigingen, en check ook nu.
     */
    public void startScheduling() {
	Timer timer = new Timer();
	FileFetcher fileFetcher = new FileFetcher(this);
	fileFetcher.run();
	timer.scheduleAtFixedRate(fileFetcher, timeToNextHour(), 1000 * 60 * 60);
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
