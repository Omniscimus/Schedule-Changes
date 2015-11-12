package net.omniscimus.profielwerkstuk.net;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import net.omniscimus.profielwerkstuk.configuration.ConfigValueCache;

/**
 * Zorgt voor de intervallen waarop het netwerk gescand wordt voor nieuwe mobieltjes.
 * @author omniscimus
 */
public class ScanScheduler {
    
    private final InetAddress routerIP;
    
    /**
     * Maakt een nieuwe ScanScheduler.
     * 
     * @param routerIP het IP van de router, dat genegeerd moet worden in scans.
     */
    public ScanScheduler(InetAddress routerIP) {
	this.routerIP = routerIP;
    }
    
    // Iedere scan interval heeft een ID. Normaal gesproken is er maar één van.
    private Map<Integer, NetworkScanTask> scanIntervals;
    private int taskCount = 0;
    private Timer daemonTimer;
    private long delayBetweenScans;
    
    /**
     * Laadt deze ScanScheduler.
     */
    public void load() {
	scanIntervals = new HashMap<>();
	delayBetweenScans = ConfigValueCache.getScanDelay();
    }
    
    /**
     * Start een nieuwe scan interval daemon.
     * @return de ID van de nieuwe interval taak
     */
    public int startNewNetworkScanInterval() {
	/* 
	 * Timer(boolean asDaemon): of het een daemon moet zijn.
	 * Het verschil tussen een user thread en een daemon thread is
	 * dat een daemon thread het niet verhindert dat het programma afsluit.
	 * Als het programma afsluit en het is een daemon, zal die daemon gewoon stoppen.
	 */
	if(daemonTimer == null) daemonTimer = new Timer(true);
	
	NetworkScanTask newTask = new NetworkScanTask(routerIP);
	scanIntervals.put(taskCount++, newTask);
	// Start direct een nieuw scan interval.
	daemonTimer.scheduleAtFixedRate(newTask, 0L, delayBetweenScans);
	return taskCount;
    }
    
    /**
     * Zoekt de NetworkScanTask op aan de hand van de bijbehorende ID.
     * @param id de ID van de taak die opgezocht moet worden
     * @return de NetworkScanTask met de betreffende ID, of null als die niet bestaat
     */
    public NetworkScanTask getScanInterval(int id) {
	return scanIntervals.get(id);
    }
    
    /**
     * Stopt de NetworkScanInterval met de betreffende ID.
     * Als er geen interval is met die ID, gebeurt er niets.
     * @param id de ID van de task die gestopt moet worden
     */
    public void stopNetworkScanInterval(int id) {
	NetworkScanTask taskToCancel = scanIntervals.get(id);
	if(taskToCancel != null) taskToCancel.cancel();
	if(scanIntervals.isEmpty()) stopAllNetworkScanIntervals();
    }
    
    /**
     * Stopt de daemon met alle scan timers.
     */
    public void stopAllNetworkScanIntervals() {
	daemonTimer.cancel();
	daemonTimer = null;
    }
    
}
