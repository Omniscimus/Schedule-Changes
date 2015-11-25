package net.omniscimus.profielwerkstuk.text;

import net.omniscimus.profielwerkstuk.Roosterwijzigingen;

/**
 * Manager voor de bestanden met roosterwijzigingen.
 *
 * @author omniscimus
 */
public class FileManager {

    private final CacheManager cacheManager;
    private final NehalenniaFileReader nehalenniaFileReader;
    private final DownloadScheduler downloadScheduler;

    /**
     * Maakt een nieuwe FileManager.
     *
     * @param rw de basis van dit programma
     */
    public FileManager(Roosterwijzigingen rw) {
	cacheManager = new CacheManager(rw);
	nehalenniaFileReader = new NehalenniaFileReader(cacheManager);
	downloadScheduler = new DownloadScheduler(this);
    }

    /**
     * Geeft de CacheManager.
     *
     * @return de CacheManager die de caches met roosterwijzigingen beheert
     */
    public CacheManager getCacheManager() {
	return cacheManager;
    }

    /**
     * Geeft de NehalenniaFileReader die door deze DownloadScheduler gebruikt
     * wordt.
     *
     * @return een instance van NehalenniaFileReader die bij voorkeur gebruikt
     * moet worden
     */
    public NehalenniaFileReader getFileReader() {
	return nehalenniaFileReader;
    }

    /**
     * Begin met netwerkscans.
     */
    public void load() {
	downloadScheduler.startScheduling();
    }

    /**
     * Stopt operaties die nu bezig zijn.
     */
    public void stop() {
	downloadScheduler.stopScheduling();
    }

}
