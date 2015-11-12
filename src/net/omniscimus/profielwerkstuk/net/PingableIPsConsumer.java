package net.omniscimus.profielwerkstuk.net;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
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
	this.pingerPool = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {
	while (true) {
	    try {
		InetAddress ipToPing = pingManager.getPingQueue().take();
		System.out.println("IP taken: " + ipToPing.getHostName());
		HostPinger pinger = new HostPinger(ipToPing, ConfigValueCache.getPingTimeout());
		pingerPool.execute(pinger);
		// This is not good, maybe try CompletionService
		Thread.sleep(ConfigValueCache.getPingTimeout());
	    } catch (InterruptedException ex) {
		Logger.getLogger(PingableIPsConsumer.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

}
