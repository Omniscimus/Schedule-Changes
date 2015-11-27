package net.omniscimus.profielwerkstuk.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Dit is de taak die uitgevoerd moet worden door de Producer thread. Deze
 * thread voegt constant nieuwe IPs toe aan de lijst met IPs die gepingd moeten
 * worden in PingManager.
 *
 * @author omniscimus
 */
public class PingableIPsProducer implements Runnable {

    private final PingManager pingManager;
    private final byte[] routerIPBytes;

    /**
     * Maakt een nieuwe PingableIPsProducer.
     *
     * @param pingManager de PingManager met de lijst waaraan nieuwe IPs
     * toegevoegd moeten worden
     * @param routerIP het IP-adres van de router, dat genegeerd moet worden bij
     * scans
     */
    public PingableIPsProducer(PingManager pingManager, InetAddress routerIP) {
	this.pingManager = pingManager;
	this.routerIPBytes = routerIP.getAddress();
    }

    private volatile boolean running = true;

    @Override
    public void run() {
	
	while (running) {
	    for (int i = 1; i < 256; i++) {
		if (i != routerIPBytes[3]) {
		    // Bepaal voor elke host die gescand moet worden, het IP-adres
		    byte[] remoteHostIPBytes = new byte[4];
		    remoteHostIPBytes[0] = routerIPBytes[0];
		    remoteHostIPBytes[1] = routerIPBytes[1];
		    remoteHostIPBytes[2] = routerIPBytes[2];
		    remoteHostIPBytes[3] = (byte) i;
		    InetAddress hostIP;
		    try {
			hostIP = InetAddress.getByAddress(remoteHostIPBytes);
			pingManager.getPingQueue().put(hostIP);
		    } catch (UnknownHostException | InterruptedException ex) {
			Logger.getLogger(PingableIPsProducer.class.getName()).log(Level.SEVERE, null, ex);
		    }

		}
	    }
	}

    }

    /**
     * Stopt deze Producer taak.
     */
    public void stop() {
	running = false;
    }

}
