package net.omniscimus.profielwerkstuk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.omniscimus.profielwerkstuk.text.CommandOutputProcessor;

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
     * Verwittigt alle geregistreerde Listeners van een nieuwe lijst van
     * gedetecteerde IP- en MAC-adressen.
     *
     * @param ipToMAC een Map met als Key het IP-adres en als Value het
     * MAC-adres van de gedetecteerde device
     */
    /*public static void detectedMACAddressesUpdateEvent(Map<String, String> ipToMAC) {
     if (macListeners != null) {
     macListeners.stream().forEach((listener) -> {
     listener.onMACAddressesUpdate(ipToMAC);
     });
     }
     }*/
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
