package net.omniscimus.profielwerkstuk.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import net.omniscimus.profielwerkstuk.Roosterwijzigingen;
import net.omniscimus.profielwerkstuk.configuration.ConfigValueCache;

/**
 * Manager voor het hotspot netwerk.
 *
 * @author omniscimus
 */
public class NetworkManager {

    private final Roosterwijzigingen roosterwijzigingen;

    /**
     * Maakt een nieuwe NetworkManager.
     *
     * @param roosterwijzigingen de basis van dit programma
     */
    public NetworkManager(Roosterwijzigingen roosterwijzigingen) {
	this.roosterwijzigingen = roosterwijzigingen;
    }

    private PingManager pingManager;
    private NetworkInterface hotspotInterface;
    private InetAddress hotspotIP;

    /**
     * Geef het IP-adres van de WiFi hotspot.
     *
     * @return het InetAddress van de WiFi hotspot
     */
    public InetAddress getHotspotIP() {
	return hotspotIP;
    }

    /**
     * Laadt de hotspot implementatie en start ping scans.
     */
    public void load() {

	if (!reloadHotspotInterface()) {
	    roosterwijzigingen.crash("Kon de interface van de hotspot niet vinden! (" + ConfigValueCache.getHotspotInterface() + ")");
	}
	reloadHotspotIP();

	pingManager = new PingManager(this);
	pingManager.startPinging();

    }

    /**
     * Zoekt de Network Interface uit de config op.
     *
     * @return false als de interface niet geüpdate kon worden
     */
    public boolean reloadHotspotInterface() {

	String requiredHotspotInterface = ConfigValueCache.getHotspotInterface();
	try {
	    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	    while (networkInterfaces.hasMoreElements()) {
		NetworkInterface ni = networkInterfaces.nextElement();
		if (ni.getName().equals(requiredHotspotInterface)) {
		    hotspotInterface = ni;
		    return true;
		}
	    }
	} catch (SocketException e) {
	    return false;
	}
	return false;

    }

    /**
     * Zoekt het IP van het hotspot network op.
     */
    public void reloadHotspotIP() {

	Enumeration<InetAddress> hotspotIPs = hotspotInterface.getInetAddresses();
	while (hotspotIPs.hasMoreElements()) {
	    InetAddress ip = hotspotIPs.nextElement();
	    if (!ip.isAnyLocalAddress() && ip.isSiteLocalAddress()) {
		hotspotIP = ip;
		break;
	    }
	}

    }

}
