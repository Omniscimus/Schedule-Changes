package net.omniscimus.profielwerkstuk.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.profielwerkstuk.ScheduleChanges;
import net.omniscimus.profielwerkstuk.configuration.ConfigValueCache;

/**
 * Manager voor het hotspot netwerk.
 *
 * @author omniscimus
 */
public class NetworkManager {

    private final ScheduleChanges scheduleChanges;

    /**
     * Maakt een nieuwe NetworkManager.
     *
     * @param scheduleChanges de basis van dit programma
     */
    public NetworkManager(ScheduleChanges scheduleChanges) {
	this.scheduleChanges = scheduleChanges;
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
	    scheduleChanges.shutdown("Kon de interface van de hotspot niet vinden! (" + ConfigValueCache.getHotspotInterface() + ")", false);
	}
	reloadHotspotIP();

	pingManager = new PingManager(this);
	pingManager.startPinging();
    }

    /**
     * Sluit de netwerkfunctionaliteit af.
     */
    public void stop() {
	pingManager.stopPinging();
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

    /**
     * Print informatie over alle beschikbare netwerkinterfaces.
     */
    public static void printNetworkInterfaces() {
	try {
	    System.out.println("Beschikbare netwerkinterfaces:\n");
	    Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	    while (networkInterfaces.hasMoreElements()) {
		NetworkInterface interfac = networkInterfaces.nextElement();
		if (interfac.isUp()) {
		    Enumeration<InetAddress> ips = interfac.getInetAddresses();
		    while (ips.hasMoreElements()) {
			InetAddress ip = ips.nextElement();
			System.out.println("hostName: " + ip.getHostName());
			System.out.println("hostAddress: " + ip.getHostAddress());
		    }
		    byte[] address = interfac.getHardwareAddress();
		    if (address != null) {
			System.out.print("MAC-addres: ");
			for (byte b : address) {
			    System.out.print(b);
			}
			System.out.println();
		    }
		    System.out.println("Interface naam: " + interfac.getName());
		}
		System.out.println();
	    }
	} catch (SocketException ex) {
	    Logger.getLogger(NetworkManager.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

}
