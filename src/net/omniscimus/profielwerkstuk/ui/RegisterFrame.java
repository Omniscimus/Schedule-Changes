package net.omniscimus.profielwerkstuk.ui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
	initComponents();
    }

    private JButton backToHomescreen;
    private JLabel title;
    private ArrayList<JButton> currentButtons;
    private ArrayList<String> currentlyDisplayedIPs;

    private int nextColumn = 0;
    private int nextRow = 2;

    private void initComponents() {

	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	getContentPane().setLayout(new GridBagLayout());

	title = new JLabel();

	if (currentButtons != null) {
	    currentButtons.stream().forEach((button) -> {
		getContentPane().remove(button);
	    });
	}

	currentButtons = new ArrayList<>();
	currentlyDisplayedIPs = new ArrayList<>();

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
     * Voegt een knop toe aan het frame, mits er niet al een knop is die
     * verwijst naar het gegeven MAC-adres.
     *
     * @param ipAddress het IP-adres dat op de knop moet staan
     * @param macAddress het MAC-adres dat op de knop moet staan
     */
    public void addButton(String ipAddress, String macAddress) {

	try {
	    if (!currentlyDisplayedIPs.contains(ipAddress)
		    && !uiManager.getRoosterwijzigingen().getMySQLManager()
		    .getRoosterwijzigingenSQL().macAddressIsRegistered(macAddress)) {

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

		revalidate();
		repaint();
	    }
	} catch (SQLException | ClassNotFoundException ex) {
	    Logger.getLogger(RegisterFrame.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

}
