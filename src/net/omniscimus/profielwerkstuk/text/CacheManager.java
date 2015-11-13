package net.omniscimus.profielwerkstuk.text;

import java.sql.SQLException;
import java.util.ArrayList;
import net.omniscimus.profielwerkstuk.Roosterwijzigingen;

/**
 * Manager voor de ScheduleChangesCaches van verschillende dagen waarin de
 * roosterwijzigingen worden opgeslagen.
 *
 * @author omniscimus
 */
public class CacheManager {

    private final Roosterwijzigingen rw;

    /**
     * Maakt een nieuwe CacheManager.
     *
     * @param rw de basis van dit programma
     */
    public CacheManager(Roosterwijzigingen rw) {
	this.rw = rw;
    }

    private ScheduleChangesCache todayCache;
    private ScheduleChangesCache tomorrowCache;

    /**
     * Geeft de Roosterwijzigingen instance die dit programma heeft gestart.
     *
     * @return de gebruikte instance van Roosterwijzigingen
     */
    public Roosterwijzigingen getRoosterwijzigingen() {
	return rw;
    }

    /**
     * Geeft of een cache met roosterwijzigingen beschikbaar is.
     *
     * @param today true als het om de roosterwijzigingen voor vandaag gaat;
     * false als het om de roosterwijzigingen van morgen gaat
     * @return true als de cache beschikbaar is en de roosterwijzigingen dus
     * gegeven kunnen worden; anders false
     */
    public boolean scheduleCacheIsAvailable(boolean today) {
	return getScheduleCache(today) != null;
    }

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
     * Geeft een lijst met roosterwijzigingen, gefilterd voor de persoon met het
     * gegeven MAC-adres.
     *
     * @param macAddress het MAC-adres van de persoon van wie de
     * roosterwijzigingen opgezocht moeten worden
     * @param today true als het om de roosterwijzigingen van vandaag gaat;
     * false als het om de roosterwijzigingen van de volgende schooldag
     * @return een lijst met roosterwijzigingen, of null als de
     * roosterwijzigingen voor de gespecifieerde dag niet beschikbaar zijn
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public ArrayList<String> getSpecificScheduleChanges(String macAddress, boolean today)
	    throws SQLException, ClassNotFoundException {

	ScheduleChangesCache cacheToUse = getScheduleCache(today);
	if (cacheToUse != null) {
	    ArrayList<String> schoolClasses
		    = rw.getMySQLManager().getDatabaseLink()
		    .getSchoolClassesByMACAddress(macAddress);

	    ArrayList<String> scheduleChanges = new ArrayList<>();
	    schoolClasses.stream().map((schoolClass)
		    -> cacheToUse
		    .getScheduleChangesByClass(schoolClass)).forEach((classChanges) -> {
			if (classChanges != null) {
			    classChanges.stream().forEach((change) -> {
				scheduleChanges.add(change);
			    });
			}
		    });

	    return scheduleChanges;
	} else {
	    return null;
	}
    }

    /**
     * Geeft de algemene (voor alle leerlingen geldende) roosterwijzigingen.
     *
     * @param today true als het om de roosterwijzigingen van vandaag gaat;
     * false als het om de roosterwijzigingen van morgen gaat
     * @return een lijst met algemene roosterwijzigingen
     */
    public ArrayList<String> getGeneralScheduleChanges(boolean today) {
	return getScheduleCache(today).getGeneralChanges();
    }

}
