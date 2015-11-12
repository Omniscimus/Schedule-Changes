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
 *
 * @author omniscimus
 */
public class PingableIPsConsumer implements Runnable {

    private final PingManager pingManager;
    private final ExecutorService pingerPool;

    public PingableIPsConsumer(PingManager pingManager) {
	this.pingManager = pingManager;
	this.pingerPool = Executors.newCachedThreadPool();
    }

    private List<Future<String>> pingResults;

    @Override
    public void run() {
	pingResults = Collections.synchronizedList(new ArrayList<Future<String>>());
	while (true) {
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

    private void processCompletedTasks() {

	for (Future<String> task : pingResults) {
	    try {
		if (!task.isDone() && !task.isCancelled()) {
		    // Eerste niet-beëindigde taak gevonden; alle beëindigde
		    // taken zijn nu dus verwerkt.
		    return;
		}
		String resultIP = task.get();
		if (resultIP != null) {
		    EventAnnouncer.detectedIP(resultIP);// <-- Niet thread-safe!Ï
		}
		pingResults.remove(0);
	    } catch (InterruptedException | ExecutionException ex) {
		Logger.getLogger(PingableIPsConsumer.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

    }

}
