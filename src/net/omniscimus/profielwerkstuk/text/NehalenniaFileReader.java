package net.omniscimus.profielwerkstuk.text;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.omniscimus.profielwerkstuk.mysql.SchoolSQL;

/**
 * Deze class kan een verwerkt bestand met roosterwijzigingen (dus NIET een HTML
 * bestand) interpreteren.
 *
 * @author omniscimus
 */
public class NehalenniaFileReader {

    private final CacheManager cacheManager;

    /**
     * Maakt een nieuwe NehalenniaFileReader.
     *
     * @param cacheManager de class waarin de roosterwijzigingen worden
     * opgeslagen
     */
    public NehalenniaFileReader(CacheManager cacheManager) {
	this.cacheManager = cacheManager;
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

	SchoolSQL sqlAccess = cacheManager.getRoosterwijzigingen()
		.getMySQLManager().getSchoolSQL();
	Pattern classPattern = Pattern.compile("^\\S+");
	lines.forEach((line) -> {
	    Matcher classMatcher = classPattern.matcher(line);
	    if (classMatcher.find()) {
		// Opmerking: de spatie die hier vervangen wordt is geen gewone
		// spatie, het is een speciale Unicode char die het programma
		// van de roostermaakster blijkbaar gebruikt ipv de spatie...
		String schoolClass = classMatcher.group().replaceAll(" ", "");
		ScheduleChangesCache cacheToWrite = cacheManager.getScheduleCache(today);
		if (cacheToWrite == null) {
		    cacheToWrite = cacheManager.createScheduleChangesCache(today);
		}
		if (sqlAccess.schoolClassExists(schoolClass)) {
		    cacheToWrite.store(schoolClass, line);
		} else {
		    cacheToWrite.store(line);
		}
	    }
	});

    }

}
