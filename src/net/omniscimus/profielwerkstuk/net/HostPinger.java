package net.omniscimus.profielwerkstuk.net;

import java.io.IOException;
import java.net.InetAddress;

/**
 *
 * @author omniscimus
 */
public class HostPinger implements Runnable {
    
    private final PingManager pingManager;
    private final InetAddress ipToPing;
    private final int timeout;
    
    HostPinger(PingManager pingManager, InetAddress ipToPing, int timeout) {
	this.pingManager = pingManager;
	this.ipToPing = ipToPing;
	this.timeout = timeout;
    }
    
    @Override
    public final void run() {
	try {
	    if(ipToPing.isReachable(timeout)) pingManager.addReachableIP(ipToPing);
	} catch (IOException e) {
	    // Doe niets; stop het evt in een log
	}
    }
    
}
