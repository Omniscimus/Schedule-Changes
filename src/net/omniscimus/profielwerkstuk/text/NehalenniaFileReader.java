package net.omniscimus.profielwerkstuk.text;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.omniscimus.profielwerkstuk.Roosterwijzigingen;

/**
 * Deze class kan een verwerkt bestand met roosterwijzigingen (dus NIET een HTML
 * bestand) interpreteren.
 *
 * @author omniscimus
 */
public class NehalenniaFileReader {

    private final Roosterwijzigingen rw;
    private final DownloadScheduler downloadScheduler;

    /**
     * Maakt een nieuwe NehalenniaFileReader.
     *
     * @param rw de basis van dit programma
     * @param downloadScheduler de Downloads manager
     */
    public NehalenniaFileReader(Roosterwijzigingen rw, DownloadScheduler downloadScheduler) {
	this.rw = rw;
	this.downloadScheduler = downloadScheduler;
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
    public ArrayList<String> getScheduleChanges(String macAddress, boolean today)
	    throws SQLException, ClassNotFoundException {

	ScheduleChangesCache cacheToUse = downloadScheduler.getScheduleCache(today);
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
     * Schrijft de roosterwijzigingen uit het opgegeven bestand naar
     * ScheduleChangesCache.
     *
     * @param fileToRead het bestand met roosterwijzigingen dat naar de cache
     * geschreven moet worden
     * @param today true als het om de roosterwijzigingen van vandaag gaat;
     * false als het om de roosterwijzigingen van de volgende schooldag
     * @throws IOException als het bestand met roosterwijzigingen niet gelezen
     * kan worden
     */
    public void writeChangesToCache(File fileToRead, boolean today) throws IOException {

	Stream<String> lines = NehalenniaFileProcessor.fileAsLinesStream(fileToRead.toPath(), "UTF-8");

	Pattern classPattern = Pattern.compile("^\\S+");
	lines.forEach((line) -> {
	    Matcher classMatcher = classPattern.matcher(line);
	    if (classMatcher.find()) {
		// Opmerking: de spatie die hier vervangen wordt is geen gewone
		// spatie, het is een speciale Unicode char die het programma
		// van de roostermaakster blijkbaar gebruikt ipv de spatie...
		String schoolClass = classMatcher.group().replaceAll(" ", "");
		ScheduleChangesCache cacheToWrite = downloadScheduler.getScheduleCache(today);
		if(cacheToWrite == null) {
		    cacheToWrite = downloadScheduler.createScheduleChangesCache(today);
		}
		if (ScheduleChangesCache.schoolClassExists(schoolClass)) {
		    cacheToWrite.store(schoolClass, line);
		} else {
		    cacheToWrite.store(line);
		}
	    }
	});

    }

}
