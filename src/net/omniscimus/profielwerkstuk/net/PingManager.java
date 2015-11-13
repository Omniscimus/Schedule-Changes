package net.omniscimus.profielwerkstuk.net;

import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Manager voor een Ping scan op het netwerk.
 *
 * @author omniscimus
 */
public class PingManager {

    private final NetworkManager networkManager;

    /**
     * Maakt een nieuwe PingManager.
     *
     * @param networkManager de NetworkManager die deze PingManager moet
     * gebruiken om netwerkinformatie op te vragen
     */
    public PingManager(NetworkManager networkManager) {
	this.networkManager = networkManager;
    }

    private final BlockingQueue<InetAddress> pingQueue = new ArrayBlockingQueue<>(3);

    /**
     * Geeft de lijst met IP-adressen die gepingt moeten worden.
     *
     * @return de lijst met InetAddresses van hosts die mogelijk up zijn
     */
    BlockingQueue<InetAddress> getPingQueue() {
	return pingQueue;
    }

    /**
     * Start met het pingen van mogelijke hosts door de Producer en de Consumer
     * threads te starten, en geef de hosts die up zijn door aan de UI.
     */
    public void startPinging() {
	Thread producerThread = new Thread(new PingableIPsProducer(this, networkManager.getHotspotIP()));
	producerThread.start();

	Thread consumerThread = new Thread(new PingableIPsConsumer(this));
	consumerThread.start();
    }

}
