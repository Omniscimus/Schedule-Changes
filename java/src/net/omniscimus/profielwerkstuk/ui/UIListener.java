package net.omniscimus.profielwerkstuk.ui;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import net.omniscimus.profielwerkstuk.ScheduleChanges;
import net.omniscimus.profielwerkstuk.MACAddressListener;

/**
 * Listener die nieuwe gedetecteerde devices weergeeft in de User Interface.
 *
 * @author omniscimus
 */
public class UIListener implements MACAddressListener {

    private final ScheduleChanges sc;
    private final UIManager uiManager;

    /**
     * Maakt een nieuwe UIListener.
     *
     * @param rw de basis van dit programma
     * @param uiManager de UIManager die deze Listener controleert
     */
    public UIListener(ScheduleChanges rw, UIManager uiManager) {
	this.sc = rw;
	this.uiManager = uiManager;
    }

    @Override
    public void onMACAddressUpdate(String ip, String mac) {

	// Check of het MAC-adres in de database staat.
	// Als dat zo is, zet dan de naam van de leerling op het scherm;
	// Als dat niet zo is, zet hem dan in de lijst met adressen die
	// weergegeven worden in het RegisterFrame.
	JFrame currentFrame = uiManager.getCurrentFrame();

	if (currentFrame instanceof RegisterFrame) {
	    ((RegisterFrame) currentFrame).addButton(ip, mac);
	} else if (currentFrame instanceof HomescreenFrame) {
	    try {
		String studentName = sc.getMySQLManager().getDatabaseLink().getNameByMACAddress(mac);
		if (studentName != null) {
		    ((HomescreenFrame) currentFrame).addButton(studentName, mac);
		}
	    } catch (SQLException | ClassNotFoundException ex) {
		Logger.getLogger(UIListener.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

    }

}
