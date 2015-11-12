package net.omniscimus.profielwerkstuk.net;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.TimerTask;
import net.omniscimus.profielwerkstuk.text.CommandOutputProcessor;
import net.omniscimus.profielwerkstuk.configuration.ConfigValueCache;
import net.omniscimus.profielwerkstuk.EventAnnouncer;

/**
 * Stuurt een ping naar alle devices op het hotspot netwerk en zoekt het
 * MAC-adres op.
 *
 * @author omniscimus
 */
public class NetworkScanTask extends TimerTask {

    private final PingManager pingManager;
    private final InetAddress routerIP;
    private final int timeout;

    /**
     * Maakt een nieuwe NetworkScanTask.
     *
     * @param routerIP het IP-adres van de router, dat genegeerd moet worden bij
     * de scan.
     */
    public NetworkScanTask(InetAddress routerIP) {
	pingManager = new PingManager();
	this.routerIP = routerIP;
	timeout = ConfigValueCache.getPingTimeout();
    }

    @Override
    public void run() {

	try {
	    // Zoek op welke mobieltjes er verbonden zijn met het hotspot netwerk
	    ArrayList<InetAddress> foundHosts = pingManager.checkHosts(routerIP, timeout);

	    // Verander de lijst met InetAddresses in een lijst met Strings
	    ArrayList<String> foundHostsIPStrings = new ArrayList<>();
	    if (foundHosts.size() > 0) {
		foundHosts.stream().forEach((ia) -> {
		    if (ia != null) {
			foundHostsIPStrings.add(ia.getHostAddress());
		    }
		});
	    }

	    // Zoek de MAC-adressen op die bij die IP's horen
	    Map<String, String> foundMACAddresses = CommandOutputProcessor.getMACAddresses(foundHostsIPStrings);

	    // Geef die adressen weer in de interface.
	    EventAnnouncer.detectedMACAddressesUpdateEvent(foundMACAddresses);

	} catch (InterruptedException e) {
	    // Doe niets
	}

    }

}
