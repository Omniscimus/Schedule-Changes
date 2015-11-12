package net.omniscimus.profielwerkstuk.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author omniscimus
 */
public class PingableIPsProducer implements Runnable {

    private final PingManager pingManager;
    private final byte[] routerIPBytes;

    public PingableIPsProducer(PingManager pingManager, InetAddress routerIP) {
	this.pingManager = pingManager;
	this.routerIPBytes = routerIP.getAddress();
    }

    @Override
    public void run() {

	while (true) {
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
			System.out.println("IP queued: " + hostIP.getHostName());
		    } catch (UnknownHostException | InterruptedException ex) {
			Logger.getLogger(PingableIPsProducer.class.getName()).log(Level.SEVERE, null, ex);
		    }

		}
	    }
	}

    }

}
