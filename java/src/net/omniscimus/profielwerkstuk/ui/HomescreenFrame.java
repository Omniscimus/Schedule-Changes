package net.omniscimus.profielwerkstuk.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import net.omniscimus.profielwerkstuk.configuration.ConfigValueCache;

/**
 * De Frame waarin knoppen met de namen van de leerlingen staan wiens device
 * gedetecteerd zijn.
 *
 * @author omniscimus
 */
public class HomescreenFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final UIManager uiManager;

    /**
     * Maakt een nieuwe HomescreenFrame.
     *
     * @param uiManager de User Interface Manager die deze frame gebruikt
     */
    public HomescreenFrame(UIManager uiManager) {
	this.uiManager = uiManager;
	this.setUndecorated(true);
	this.setExtendedState(Frame.MAXIMIZED_BOTH);
	timer = new Timer();
	initComponents();
    }

    private GridBagLayout layout;
    private JLabel title;
    private JPanel buttonPanel;
    private JButton help;
    private JButton register;
    private JLabel studentIDText;
    private JTextField studentID;
    private final Timer timer;
    private HashMap<JButton, TimerTask> currentButtons;

    /**
     * Zet de components op het frame.
     */
    private void initComponents() {

	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	layout = new GridBagLayout();
	getContentPane().setLayout(layout);

	buttonPanel = new JPanel();
	buttonPanel.setLayout(new GridBagLayout());
	buttonPanel.setBackground(Color.YELLOW);
	GridBagConstraints buttonPanelConstraints = new GridBagConstraints();
	buttonPanelConstraints.ipady = 10;
	getContentPane().add(buttonPanel, buttonPanelConstraints);

	help = new JButton();
	help.setText("Help");
	help.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		uiManager.showHelpFrame();
	    }
	});
	buttonPanel.add(help);

	register = new JButton();
	register.setText("Registreren");
	GridBagConstraints registerConstraints = new GridBagConstraints();
	registerConstraints.gridx = 2;
	register.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		uiManager.showRegisterScreen();
	    }
	});
	buttonPanel.add(register, registerConstraints);

	title = new JLabel();
	title.setText("Roosterwijzigingen");
	Font titleFont = new Font(title.getFont().getName(), Font.BOLD, 20);
	title.setFont(titleFont);
	GridBagConstraints titleConstraints = new GridBagConstraints();
	titleConstraints.gridy = 1;
	titleConstraints.ipady = 10;
	getContentPane().add(title, titleConstraints);

	studentIDText = new JLabel();
	studentIDText.setText("Leerlingnummer: ");
	GridBagConstraints studentIDTextConstraints = new GridBagConstraints();
	studentIDTextConstraints.gridy = 2;
	getContentPane().add(studentIDText, studentIDTextConstraints);

	studentID = new JTextField();
	Dimension textFieldSize = new Dimension(1, 25);
	studentID.setPreferredSize(textFieldSize);
	studentID.setHorizontalAlignment(JTextField.CENTER);
	studentID.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		try {
		    int number = Integer.parseInt(e.getActionCommand());
		    if (number != ConfigValueCache.getAdminCode()) {
			uiManager.showScheduleFrame(number);
		    } else {
			uiManager.getRoosterwijzigingen().shutdown(false);
		    }
		} catch (NumberFormatException ignored) {
		}
		studentID.setText("");
	    }
	});
	GridBagConstraints studentIDConstraints = new GridBagConstraints();
	studentIDConstraints.gridy = 3;
	studentIDConstraints.ipadx = 100;
	getContentPane().add(studentID, studentIDConstraints);
	studentID.requestFocus(false);

	pack();
    }

    @Override
    public void setVisible(boolean b) {
	super.setVisible(b);
	studentID.requestFocus(false);
    }

    /**
     * Geeft of een knop met de gegeven tekst al bestaat.
     *
     * @param withText de tekst die op de knop staat
     * @return true als er al een knop bestaat met de gegeven tekst; anders
     * false
     */
    private boolean buttonExists(String withText) {
	for (JButton button : currentButtons.keySet()) {
	    if (button.getText().equals(withText)) {
		resetButtonTimer(button);
		return true;
	    }
	}
	return false;
    }

    /**
     * Voegt een knop toe aan het scherm met daarop de naam van de betreffende
     * leerling.
     *
     * @param studentName de naam van de leerling van wie de device is waar de
     * knop naar verwijst
     * @param macAddress het MAC-adres van de device waarmee de leerling zich
     * identificeert
     */
    public void addButton(String studentName, String macAddress) {

	if (currentButtons == null) {
	    currentButtons = new HashMap<>();
	}

	if (!buttonExists(studentName)) {
	    JButton button = new JButton();
	    button.setText(studentName);
	    GridBagConstraints buttonConstraints = getButtonConstraints();

	    button.addActionListener(new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
		    uiManager.resetHomescreenTimer();
		    uiManager.showScheduleFrame(studentName, macAddress);
		}
	    });
	    addButton(button, buttonConstraints);
	}

	revalidate();
	repaint();

    }

    /**
     * Voegt een nieuwe knop toe aan het scherm, en start een timer zodat de
     * knop na 15 seconden verwijderd wordt, mits de timer niet gereset wordt.
     *
     * @param button de knop die toegevoegd moet worden
     * @param constraints de Constraints die o.a. aangeven op welke plaats in
     * het scherm de knop weergegeven moet worden
     */
    private void addButton(JButton button, GridBagConstraints constraints) {
	getContentPane().add(button, constraints);
	currentButtons.put(button, getNewButtonTimerTask(button));
    }

    /**
     * Verwijdert een knop van het scherm.
     *
     * @param button de knop die verwijderd moet worden
     */
    public void removeButton(JButton button) {
	getContentPane().remove(button);
	currentButtons.remove(button);
    }

    /**
     * Reset de timer van een knop, zodat hij pas over 15 seconden verwijderd
     * wordt van het scherm.
     *
     * @param button de knop waarvan de timer gereset moet worden
     */
    private void resetButtonTimer(JButton button) {
	currentButtons.get(button).cancel();
	currentButtons.put(button, getNewButtonTimerTask(button));
    }

    /**
     * Geeft een nieuwe TimerTask die na 15 seconden de gegeven knop verwijdert
     * van het scherm.
     *
     * @param button de knop die na 15 seconden van het scherm verwijderd moet
     * worden
     * @return een nieuwe TimerTask die na 15 seconden de knop verwijdert
     */
    private TimerTask getNewButtonTimerTask(JButton button) {
	TimerTask buttonTimerTask = new TimerTask() {

	    @Override
	    public void run() {
		removeButton(button);
	    }

	};
	timer.schedule(buttonTimerTask, 15 * 1000);
	return buttonTimerTask;
    }

    /**
     * Geef een GridBagConstraints waarin een lege plaats voor een nieuwe knop
     * staat.
     *
     * @return een GridBagConstraints met een beschikbare gridx en gridy
     */
    private GridBagConstraints getButtonConstraints() {
	// Startwaarden voor de plaats
	int column = 0;
	int row = 4;

	// Run deze code net zolang tot er een lege plek in de frame is gevonden
	boolean emptySpotFound = false;
	while (!emptySpotFound) {
	    // Falsifieer dat de huidig aangewezen plaats leeg is
	    boolean spotIsEmpty = true;
	    for (JButton presentButton : currentButtons.keySet()) {
		GridBagConstraints constraints = layout.getConstraints(presentButton);
		// Als de plaats bezet is, ga dan een plekje verder en probeer
		// het nog eens
		if (constraints.gridx == column && constraints.gridy == row) {
		    spotIsEmpty = false;
		    if (column == 0) { // blijf op dezelfde rij
			column = 1;
		    } else { // ga naar linksonder
			column = 0;
			row++;
		    }
		    break;
		}
	    }
	    if (spotIsEmpty) {
		emptySpotFound = true;
	    }
	}

	// Er is een lege plaats gevonden, geef het resultaat.
	GridBagConstraints buttonConstraints = new GridBagConstraints();
	buttonConstraints.gridx = column;
	buttonConstraints.gridy = row;
	return buttonConstraints;
    }

}
