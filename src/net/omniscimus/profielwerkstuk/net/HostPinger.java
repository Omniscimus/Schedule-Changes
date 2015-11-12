package net.omniscimus.profielwerkstuk.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.profielwerkstuk.EventAnnouncer;
import net.omniscimus.profielwerkstuk.text.CommandOutputProcessor;

/**
 *
 * @author omniscimus
 */
public class HostPinger implements Runnable {

    private final InetAddress ipToPing;
    private final int timeout;

    HostPinger(InetAddress ipToPing, int timeout) {
	this.ipToPing = ipToPing;
	this.timeout = timeout;
    }

    @Override
    public final void run() {
	try {
	    if (ipToPing.isReachable(timeout)) {
		String hostname = ipToPing.getHostName();
		String macAddress = CommandOutputProcessor.getMACAddressByIP(hostname);
		EventAnnouncer.detectedMACAddress(hostname, macAddress);
	    }
	} catch (IOException ex) {
	    Logger.getLogger(HostPinger.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

}
