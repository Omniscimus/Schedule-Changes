package net.omniscimus.profielwerkstuk.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Opslagplaats voor roosterwijzigingen.
 *
 * @author omniscimus
 */
public class ScheduleChangesCache {

    /**
     * Een nieuwe ScheduleChangesCache hoort aangemaakt te worden via
     * DownloadScheduler, vandaar dat hij protected is.
     */
    protected ScheduleChangesCache() {

    }

    // <classes,<changes>>
    private final Map<String, ArrayList<String>> scheduleChanges = new HashMap<>();
    private final ArrayList<String> generalChanges = new ArrayList<>();

    /**
     * Bewaart een nieuwe algemene roosterwijziging.
     *
     * @param scheduleChange de roosterwijziging die bewaard moet worden
     */
    public void store(String scheduleChange) {
	generalChanges.add(scheduleChange);
    }

    /**
     * Geeft de algemene roosterwijzigingen.
     *
     * @return een lijst met roosterwijzigingen die niet voor een specifieke
     * klas bestemd zijn
     */
    public ArrayList<String> getGeneralChanges() {
	return generalChanges;
    }

    /**
     * Bewaart een nieuwe roosterwijziging.
     *
     * @param schoolClass de klas voor welke deze roosterwijziging geldt
     * @param scheduleChange de roosterwijziging
     */
    public void store(String schoolClass, String scheduleChange) {
	ArrayList<String> schoolClassChanges = scheduleChanges.get(schoolClass);
	if (schoolClassChanges == null) {
	    schoolClassChanges = new ArrayList<>();
	}
	schoolClassChanges.add(scheduleChange);
	scheduleChanges.put(schoolClass, schoolClassChanges);
    }

    /**
     * Geeft de roosterwijzigingen voor een bepaalde klas.
     *
     * @param schoolClass de klas waarvoor de roosterwijzigingen gegeven moeten
     * worden
     * @return de roosterwijzigingen voor de gegeven klas
     */
    public ArrayList<String> getScheduleChangesByClass(String schoolClass) {
	return scheduleChanges.get(schoolClass);
    }

}
