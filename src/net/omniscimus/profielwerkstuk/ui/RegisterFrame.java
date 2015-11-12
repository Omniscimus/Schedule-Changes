package net.omniscimus.profielwerkstuk.ui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

/**
 * Het frame waar de gebruiker zijn/haar device moet selecteren.
 *
 * @author omniscimus
 */
public class RegisterFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final UIManager uiManager;

    /**
     * Maakt een nieuwe RegisterFrame.
     *
     * @param uiManager
     */
    public RegisterFrame(UIManager uiManager) {
	this.uiManager = uiManager;
	this.setUndecorated(true);
	this.setExtendedState(Frame.MAXIMIZED_BOTH);
	initComponents(new HashMap<>());
    }

    private JButton backToHomescreen;
    private JLabel title;
    private ArrayList<JButton> currentButtons;
    private ArrayList<String> currentDisplayedIPs;

    private int nextColumn = 0;
    private int nextRow = 2;

    private void initComponents(Map<String, String> buttons) {

	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	getContentPane().setLayout(new GridBagLayout());

	title = new JLabel();

	if (currentButtons != null) {
	    currentButtons.stream().forEach((button) -> {
		getContentPane().remove(button);
	    });
	}

	currentButtons = new ArrayList<>();
	currentDisplayedIPs = new ArrayList<>();

	Object[] ipAddresses = buttons.keySet().toArray();
	Object[] macAddresses = buttons.values().toArray();

	for (int i = 0; i < buttons.size(); i++) {
	    currentDisplayedIPs.add((String) ipAddresses[i]);
	    addButton(ipAddresses[i].toString(), macAddresses[i].toString());
	}

	backToHomescreen = new JButton();
	backToHomescreen.setText("Terug");
	GridBagConstraints backToHomescreenConstraints = new GridBagConstraints();
	backToHomescreenConstraints.gridx = 2;
	backToHomescreen.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		uiManager.showHomescreen();
	    }
	});
	getContentPane().add(backToHomescreen, backToHomescreenConstraints);

	title.setText("Registreren");
	GridBagConstraints titleConstraints = new GridBagConstraints();
	titleConstraints.gridx = 0;
	titleConstraints.gridy = 1;
	titleConstraints.gridwidth = 2;
	getContentPane().add(title, titleConstraints);

	pack();
    }

    /**
     * Voegt devices die nog niet in het frame stonden, maar wel in de gegeven
     * lijst met adressen, toe aan het frame.
     *
     * @param buttons een Map met als Key het IP-adres en als Value het
     * MAC-adres
     */
    public void refreshButtons(Map<String, String> buttons) {

	Object[] keySetArray = buttons.keySet().toArray();
	Object[] valuesArray = buttons.values().toArray();

	for (int i = 0; i < buttons.size(); i++) {
	    try {
		if (!currentDisplayedIPs.contains((String) keySetArray[i])
			&& !uiManager.getRoosterwijzigingen().getMySQLManager().getRoosterwijzigingenSQL().macAddressIsRegistered((String) valuesArray[i])) {
		    currentDisplayedIPs.add((String) keySetArray[i]);
		    addButton(keySetArray[i].toString(), valuesArray[i].toString());
		}
	    } catch (SQLException | ClassNotFoundException ex) {

	    }
	}

	revalidate();
	repaint();

    }

    /**
     * Voegt een knop toe aan het frame.
     * 
     * @param ipAddress het IP-adres dat op de knop moet staan
     * @param macAddress het MAC-adres dat op de knop moet staan
     */
    private void addButton(String ipAddress, String macAddress) {
	JButton button = new JButton();
	button.setText("<html><center>" + ipAddress + "<br />" + macAddress + "</center></html>");
	GridBagConstraints buttonConstraints = new GridBagConstraints();
	buttonConstraints.gridx = nextColumn;
	buttonConstraints.gridy = nextRow;
	button.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		uiManager.resetHomescreenTimer();
		uiManager.showIdentifyScreen(macAddress);
	    }
	});
	if (nextColumn == 0) { // blijf op dezelfde rij
	    nextColumn = 1;
	} else { // ga naar linksonder
	    nextColumn = 0;
	    nextRow++;
	}
	getContentPane().add(button, buttonConstraints);
	currentButtons.add(button);
    }

}
