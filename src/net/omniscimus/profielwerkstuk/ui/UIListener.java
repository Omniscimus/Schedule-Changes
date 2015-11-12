package net.omniscimus.profielwerkstuk.ui;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import net.omniscimus.profielwerkstuk.Roosterwijzigingen;
import net.omniscimus.profielwerkstuk.MACAddressListener;

/**
 * Listener die nieuwe gedetecteerde devices weergeeft in de User Interface.
 * 
 * @author omniscimus
 */
public class UIListener implements MACAddressListener {

    private final Roosterwijzigingen rw;
    private final UIManager uiManager;

    /**
     * Maakt een nieuwe UIListener.
     * 
     * @param rw de basis van dit programma
     * @param uiManager de UIManager die deze Listener controleert
     */
    public UIListener(Roosterwijzigingen rw, UIManager uiManager) {
	this.rw = rw;
	this.uiManager = uiManager;
    }

    /**
     * Geeft de gedetecteerde devices weer op het correcte frame.
     * 
     * @param ipToMAC een Map met als Key het IP-adres van de device en als
     * Value het MAC-adres
     */
    @Override
    public void onMACAddressesUpdate(Map<String, String> ipToMAC) {

	// Lookup if that MAC address is in the database
	// If so, display the leerling's name
	// If not, put it in a list which will be displayed if the leerling clicks Register
	JFrame currentFrame = uiManager.getCurrentFrame();

	if (currentFrame instanceof RegisterFrame) {
	    ((RegisterFrame) currentFrame).refreshButtons(ipToMAC);
	} else if (currentFrame instanceof HomescreenFrame) {
	    Map<String, String> namesToMACAddresses = new HashMap<>();
	    ipToMAC.values().stream().forEach((mac) -> {
		try {
		    namesToMACAddresses.put(rw.getMySQLManager().getDatabaseLink().getNameByMACAddress(mac), mac);
		} catch (SQLException | ClassNotFoundException ex) {
		    Logger.getLogger(Roosterwijzigingen.class.getName()).log(
		    Level.SEVERE, "Fout tijdens het verbinden met MySQL", ex);
		}
	    });
	    ((HomescreenFrame) currentFrame).refreshButtons(namesToMACAddresses, true);
	}

    }

}
