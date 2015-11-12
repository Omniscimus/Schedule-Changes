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

    public PingManager(NetworkManager networkManager) {
	this.networkManager = networkManager;
    }

    private final BlockingQueue<InetAddress> pingQueue = new ArrayBlockingQueue<>(256);

    BlockingQueue<InetAddress> getPingQueue() {
	return pingQueue;
    }

    public void startPinging() {
	Thread producerThread = new Thread(new PingableIPsProducer(this, networkManager.getHotspotIP()));
	producerThread.start();

	Thread consumerThread = new Thread(new PingableIPsConsumer(this));
	consumerThread.start();
    }

}
