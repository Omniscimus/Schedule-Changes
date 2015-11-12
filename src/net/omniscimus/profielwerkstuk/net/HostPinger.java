package net.omniscimus.profielwerkstuk.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.profielwerkstuk.text.CommandOutputProcessor;

/**
 *
 * @author omniscimus
 */
public class HostPinger implements Callable<String> {

    private final InetAddress ipToPing;
    private final int timeout;

    HostPinger(InetAddress ipToPing, int timeout) {
	this.ipToPing = ipToPing;
	this.timeout = timeout;
    }

    @Override
    public final String call() {
	try {
	    if (ipToPing.isReachable(timeout)) {
		String hostname = ipToPing.getHostName();
		String macAddress = CommandOutputProcessor.getMACAddressByIP(hostname);
		return macAddress;
	    }
	} catch (IOException ex) {
	    Logger.getLogger(HostPinger.class.getName()).log(Level.SEVERE, null, ex);
	}
	return null;
    }

}
