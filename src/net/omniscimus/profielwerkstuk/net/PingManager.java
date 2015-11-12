package net.omniscimus.profielwerkstuk.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manager voor een Ping scan op het netwerk.
 *
 * @author omniscimus
 */
public class PingManager {

    private ArrayList<InetAddress> foundIPAddresses;

    /**
     * Voegt een IP-adres toe aan de lijst met bereikbare hosts van deze scan.
     * Is synchronized zodat de lijst op een veilige manier door andere Threads
     * veranderd kan worden.
     *
     * @param reachableIP
     */
    synchronized void addReachableIP(InetAddress reachableIP) {
	/*
	 * Doordat de scans in verschillende threads uitgevoerd worden, kan een
	 * ArrayIndexOutOfBoundsException optreden in ArrayList.java als de
	 * ArrayList niet volatile is.
	 */
	foundIPAddresses.add(reachableIP);
    }

    /**
     * Scant het netwerk van een gegeven IP-adres en stopt alle verbonden
     * apparaten in een ArrayList, behalve routerIP.
     *
     * @param routerIP het InetAddress van de router wiens subnet gescand moet
     * worden
     * @param timeout hoeveel milliseconden het programma moet wachten op een
     * respons
     * @return een lijst met alle IP's van hosts die up zijn
     * @throws InterruptedException als de scan thread wordt onderbroken tijdens
     * de scan
     */
    public ArrayList<InetAddress> checkHosts(InetAddress routerIP, int timeout) throws InterruptedException {

	byte[] routerIPBytes = routerIP.getAddress();
	foundIPAddresses = new ArrayList<>();
	long maxScanDuration = (long) 1.2 * timeout;

	ArrayList<HostPinger> pingers = new ArrayList<>();

	for (int i = 1; i < 256; i++) {
	    if (i != routerIPBytes[3]) {
		// Bepaal voor elke host die gescand moet worden, het IP-adres
		byte[] hostIPBytes = new byte[4];
		hostIPBytes[0] = routerIPBytes[0];
		hostIPBytes[1] = routerIPBytes[1];
		hostIPBytes[2] = routerIPBytes[2];
		hostIPBytes[3] = (byte) i;
		InetAddress hostIP;
		try {
		    hostIP = InetAddress.getByAddress(hostIPBytes);
		    pingers.add(new HostPinger(this, hostIP, timeout));
		} catch (UnknownHostException e) {
		    // Doe niets; stop het eventueel in een log
		}
	    }
	}

	ExecutorService pingPool = Executors.newCachedThreadPool();
	pingers.stream().forEach((pinger) -> {
	    pingPool.execute(pinger);
	});
	pingPool.shutdown();
	pingPool.awaitTermination(maxScanDuration, TimeUnit.MILLISECONDS);

	return foundIPAddresses;

    }

}
