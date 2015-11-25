package net.omniscimus.profielwerkstuk.net;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.profielwerkstuk.EventAnnouncer;
import net.omniscimus.profielwerkstuk.configuration.ConfigValueCache;

/**
 * Dit is de taak die uitgevoerd moet worden door de Consumer thread. Hij pingt
 * de IP-adressen en geeft IP-adressen die bereikbaar zijn door naar
 * EventAnnouncer.
 *
 * @author omniscimus
 */
public class PingableIPsConsumer implements Runnable {

    private final PingManager pingManager;
    private final ExecutorService pingerPool;

    /**
     * Maakt een nieuwe PingableIPsConsumer.
     *
     * @param pingManager de PingManager waar deze Consumer IP-adressen uit moet
     * halen die gepingd moeten worden
     */
    public PingableIPsConsumer(PingManager pingManager) {
	this.pingManager = pingManager;
	this.pingerPool = Executors.newCachedThreadPool();
    }

    private volatile boolean running;
    private List<Future<String>> pingResults;

    @Override
    public void run() {
	pingResults = Collections.synchronizedList(new ArrayList<Future<String>>());
	while (running) {
	    try {
		if (pingResults.size() < 50) {
		    InetAddress ipToPing = pingManager.getPingQueue().take();
		    HostPinger pinger = new HostPinger(ipToPing, ConfigValueCache.getPingTimeout());
		    pingResults.add(pingerPool.submit(pinger));
		}
		processCompletedTasks();
	    } catch (InterruptedException ex) {
		Logger.getLogger(PingableIPsConsumer.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

    /**
     * Stopt deze Consumer taak.
     */
    public void stop() {
	running = false;
	pingerPool.shutdownNow();
    }

    /**
     * Kijk de lijst met taken door voor taken die voltooid zijn; stuur de
     * resultaten (IP-adressen) van bereikbare hosts door naar EventAnnouncer.
     */
    private void processCompletedTasks() {

	List<Future<String>> resultsSnapshot = new ArrayList<>(pingResults);

	for (Future<String> task : resultsSnapshot) {
	    try {
		if (!task.isDone() && !task.isCancelled()) {
		    // Eerste niet-beëindigde taak gevonden; alle beëindigde
		    // taken zijn nu dus verwerkt.
		    return;
		}
		String resultIP = task.get();
		if (resultIP != null) {
		    EventAnnouncer.detectedIP(resultIP);
		}
		pingResults.remove(0);
	    } catch (InterruptedException | ExecutionException ex) {
		Logger.getLogger(PingableIPsConsumer.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

    }

}
