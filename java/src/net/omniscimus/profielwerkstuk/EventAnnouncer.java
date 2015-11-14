package net.omniscimus.profielwerkstuk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.profielwerkstuk.util.CommandOutputProcessor;

/**
 * Verspreidt bepaalde gebeurtenissen in het programma naar de juiste Listeners.
 *
 * @author omniscimus
 */
public class EventAnnouncer {

    private static ArrayList<MACAddressListener> macListeners;

    /**
     * Voegt de gegeven Listener toe aan de te verwittigen classes wanneer een
     * nieuwe lijst met gedetecteerde MAC-adressen beschikbaar komt.
     *
     * @param listener de class die zal luisteren naar nieuwe resultaten van
     * netwerkscans
     */
    public static void registerListener(MACAddressListener listener) {
	if (macListeners == null) {
	    macListeners = new ArrayList<>();
	}
	macListeners.add(listener);
    }

    /**
     * Geeft of een bepaalde Listener al geregistreerd staat.
     *
     * @param listener de Listener die gecontroleerd moet worden
     * @return true als de Listener al geregistreerd is; anders false
     */
    public static boolean isRegistered(MACAddressListener listener) {
	if (macListeners == null) {
	    return false;
	}
	return macListeners.contains(listener);
    }

    /**
     * Zoekt het MAC-adres op dat hoort bij het nieuwe gedetecteerde IP-adres en
     * verwittigt alle MACAddressListeners van de update.
     *
     * @param ip het nieuwe IP-adres dat gedetecteerd is in een scan
     */
    public static void detectedIP(String ip) {
	if (macListeners != null && !macListeners.isEmpty()) {
	    try {
		String mac = CommandOutputProcessor.getMACAddressByIP(ip);
		macListeners.stream().forEach((listener) -> {
		    listener.onMACAddressUpdate(ip, mac);
		});
	    } catch (IOException ex) {
		Logger.getLogger(EventAnnouncer.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }

}
