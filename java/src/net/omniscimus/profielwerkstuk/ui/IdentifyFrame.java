package net.omniscimus.profielwerkstuk.ui;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.omniscimus.profielwerkstuk.mysql.MySQLManager;

/**
 * Dit is de frame waarin de leerling zijn/haar naam in moet vullen.
 *
 * @author omniscimus
 */
public class IdentifyFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final UIManager uiManager;
    private String macAddressToRegister;

    /**
     * Maakt een nieuwe IdentityFrame.
     *
     * @param uiManager de User Interface Manager die deze frame gebruikt
     */
    public IdentifyFrame(UIManager uiManager) {
	this.uiManager = uiManager;
	this.setUndecorated(true);
	this.setExtendedState(Frame.MAXIMIZED_BOTH);
	initComponents();
    }
    
    private final DocumentListener textFieldListener = new DocumentListener() {
	private static final long serialVersionUID = 1L;

	@Override
	public void changedUpdate(DocumentEvent e) {
	    uiManager.resetHomescreenTimer();
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
	    uiManager.resetHomescreenTimer();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
	    uiManager.resetHomescreenTimer();
	}
    };

    /**
     * Verandert het MAC-adres dat gekoppeld moet worden aan een leerling.
     *
     * @param macAddress het nieuwe MAC-adres
     */
    public void setMACAddressToRegister(String macAddress) {
	this.macAddressToRegister = macAddress;
	if (firstName != null) {
	    firstName.setText("");
	}
	if (surname != null) {
	    surname.setText("");
	}
	if (errorMessage != null) {
	    errorMessage.setText("");
	}
    }

    private JButton backToHomescreen;
    private JLabel firstNameLabel;
    private JTextField firstName;
    private JLabel surnameLabel;
    private JTextField surname;
    private JLabel errorMessage;
    private JButton submitButton;

    /**
     * De componenten die getoond moeten worden wanneer de leerling zijn/haar
     * naam intypt.
     */
    private final JComponent[] identifyComponents = {
	firstNameLabel,
	firstName,
	surnameLabel,
	surname,
	errorMessage,
	submitButton
    };

    /**
     * Zet de componenten op het frame.
     */
    private void initComponents() {

	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	getContentPane().setLayout(new GridBagLayout());

	backToHomescreen = new JButton();
	backToHomescreen.setText("Terug");
	GridBagConstraints backToHomescreenConstraints = new GridBagConstraints();
	backToHomescreenConstraints.gridx = 2;
	backToHomescreen.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		uiManager.resetHomescreenTimer();
		uiManager.showHomescreen();
	    }
	});
	getContentPane().add(backToHomescreen, backToHomescreenConstraints);

	firstNameLabel = new JLabel();
	firstNameLabel.setText("Voornaam: ");
	GridBagConstraints firstNameLabelConstraints = new GridBagConstraints();
	firstNameLabelConstraints.gridx = 0;
	firstNameLabelConstraints.gridy = 1;
	getContentPane().add(firstNameLabel, firstNameLabelConstraints);

	firstName = new JTextField();
	GridBagConstraints firstNameConstraints = new GridBagConstraints();
	firstNameConstraints.gridx = 1;
	firstNameConstraints.gridy = 1;
	firstNameConstraints.ipadx = 200;
	firstName.getDocument().addDocumentListener(textFieldListener);
	getContentPane().add(firstName, firstNameConstraints);

	surnameLabel = new JLabel();
	surnameLabel.setText("Achternaam: ");
	GridBagConstraints surnameLabelConstraints = new GridBagConstraints();
	surnameLabelConstraints.gridx = 0;
	surnameLabelConstraints.gridy = 2;
	getContentPane().add(surnameLabel, surnameLabelConstraints);

	surname = new JTextField();
	GridBagConstraints surnameConstraints = new GridBagConstraints();
	surnameConstraints.gridx = 1;
	surnameConstraints.gridy = 2;
	surnameConstraints.ipadx = 200;
	surname.getDocument().addDocumentListener(textFieldListener);
	getContentPane().add(surname, surnameConstraints);

	errorMessage = new JLabel();
	GridBagConstraints errorMessageConstraints = new GridBagConstraints();
	errorMessageConstraints.gridwidth = 2;
	errorMessageConstraints.gridy = 4;
	getContentPane().add(errorMessage, errorMessageConstraints);

	submitButton = new JButton();
	submitButton.setText("Zoek");
	GridBagConstraints submitButtonConstraints = new GridBagConstraints();
	submitButtonConstraints.gridwidth = 2;
	submitButtonConstraints.gridy = 3;
	submitButton.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		uiManager.resetHomescreenTimer();
		String voornaam = firstName.getText();
		String achternaam = surname.getText();
		try {
		    MySQLManager mySQLManager = uiManager.getRoosterwijzigingen().getMySQLManager();
		    ArrayList<Integer> possibleLeerlingnummers = mySQLManager.getSchoolSQL().getLeerlingnummer(voornaam, achternaam);
		    switch (possibleLeerlingnummers.size()) {
			case 0:
			    errorMessage.setText("Niet gevonden.");
			    break;
			case 1:
			    mySQLManager.getRoosterwijzigingenSQL().deleteUser(macAddressToRegister);
			    mySQLManager.getRoosterwijzigingenSQL().saveNewUser(possibleLeerlingnummers.get(0), macAddressToRegister);
			    errorMessage.setText("Gefeliciteerd, je bent nu geregistreerd!");
			    break;
			default:
			    switchMode(possibleLeerlingnummers);
			    // display Meerdere resultaten. Kies je llnr.
			    break;
		    }
		} catch (SQLException | ClassNotFoundException ex) {
		    // display Fout tijdens het opzoeken van je gegevens!
		}
	    }
	});
	getContentPane().add(submitButton, submitButtonConstraints);

	pack();

    }

    private ArrayList<JButton> possibleLeerlingnummerButtons;

    /**
     * Verander het frame naar ofwel de modus waar de leerling zijn/haar naam in
     * moet vullen, ofwel de modus waar meerdere leerlingnummers zijn gevonden
     * met dezelfde gegevens, en de leerling zijn/haar leerlingnummer moet
     * aanklikken.
     *
     * @param possibleLeerlingnummers een lijst met mogelijke leerlingnummers
     * van de leerling
     */
    public void switchMode(List<Integer> possibleLeerlingnummers) {
	if (possibleLeerlingnummers != null) {
	    for (JComponent component : identifyComponents) {
		component.setVisible(false);
	    }
	    errorMessage.setText("Er zijn meerdere mensen met die naam gevonden. Kies je leerlingnummer.");
	    errorMessage.setVisible(true);
	    List<String> possibleLeerlingnummersStrings = new ArrayList<>();
	    possibleLeerlingnummers.stream().forEach((leerlingnummer) -> {
		possibleLeerlingnummersStrings.add(Integer.toString(leerlingnummer));
	    });
	    addDynamicButtons(possibleLeerlingnummersStrings, 0, 4);
	} else {
	    possibleLeerlingnummerButtons.stream().forEach((button) -> {
		button.setVisible(false);
	    });
	    possibleLeerlingnummerButtons.clear();
	    errorMessage.setVisible(false);
	    for (JComponent component : identifyComponents) {
		component.setVisible(true);
	    }
	}
    }

    /**
     * Voegt een aantal knoppen toe aan de frame op een geordende manier.
     *
     * @param buttonTexts een lijst met teksten die op de knoppen moeten staan
     * @param startColumn de gridx waar gestart moet worden met het plaatsen van
     * knoppen
     * @param startRow de gridy waar gestart moet worden met het plaatsen van
     * knoppen
     */
    public void addDynamicButtons(List<String> buttonTexts, int startColumn, int startRow) {

	possibleLeerlingnummerButtons = new ArrayList<>();

	for (int i = 0; i < buttonTexts.size(); i++) {
	    String text = buttonTexts.get(i);
	    if (text != null) {
		JButton button = new JButton();
		button.setText(text);
		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.gridx = startColumn;
		buttonConstraints.gridy = startRow;
		if (startColumn == 0) { // blijf op dezelfde rij
		    startColumn = 1;
		} else { // ga naar linksonder
		    startColumn = 0;
		    startRow++;
		}
		if (i == buttonTexts.size() - 1) {
		    buttonConstraints.gridwidth = 2;
		}
		getContentPane().add(button, buttonConstraints);
		possibleLeerlingnummerButtons.add(button);
	    }
	}

    }

}
