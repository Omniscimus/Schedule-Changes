package net.omniscimus.profielwerkstuk.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import net.omniscimus.profielwerkstuk.text.CacheManager;

/**
 * Het frame met de gepersonaliseerde roosterwijzigingen van een bepaalde
 * leerling.
 *
 * @author omniscimus
 */
public class ScheduleFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final UIManager uiManager;

    /**
     * Maakt een nieuwe ScheduleFrame.
     *
     * @param uiManager de User Interface Manager die deze frame gebruikt
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public ScheduleFrame(UIManager uiManager) throws SQLException, ClassNotFoundException {
	this.uiManager = uiManager;
	this.setUndecorated(true);
	this.setExtendedState(Frame.MAXIMIZED_BOTH);
    }

    private int studentID;
    private String identity;
    private String macAddress;

    /**
     * Verandert de leerling voor wie roosterwijzigingen getoond moeten worden.
     *
     * @param studentID het leerlingnummer van de nieuwe leerling
     * @return false als de opgegeven leerling niet gevonden kon worden; anders
     * true
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public boolean setIdentity(int studentID)
	    throws SQLException, ClassNotFoundException {
	macAddress = null;
	this.identity = uiManager.getRoosterwijzigingen().getMySQLManager()
		.getSchoolSQL().getStudentName(studentID);
	if (identity == null) {
	    return false;
	}
	this.studentID = studentID;
	return true;
    }

    /**
     * Verandert de leerling wiens roosterwijzigingen getoond moeten worden.
     *
     * @param newIdentity de naam van de nieuwe leerling
     * @param newMACAddress het MAC-adres van de nieuwe leerling
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public void setIdentity(String newIdentity, String newMACAddress)
	    throws SQLException, ClassNotFoundException {
	this.identity = newIdentity;
	this.macAddress = newMACAddress;
	this.studentID = uiManager.getRoosterwijzigingen().getMySQLManager()
		.getScheduleChangesSQL().getStudentID(macAddress);
    }

    private JButton changeRegistration;
    private JButton backToHomescreen;
    private JButton switchDay;
    private JLabel generalTitle;
    private JPanel generalContainer;
    private JLabel title;
    private JPanel specificContainer;

    /**
     * Zet de benodigde componenten op het frame.<br>
     * VOOR initcomponents() moet setIdentity() gebeurd zijn, zodat de frame
     * weet wiens roosterwijzigingen getoond moeten worden.
     *
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public void initComponents() throws SQLException, ClassNotFoundException {

	setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	getContentPane().setLayout(new GridBagLayout());

	backToHomescreen = new JButton();
	backToHomescreen.setText("Terug");
	GridBagConstraints backToHomescreenConstraints = new GridBagConstraints();
	backToHomescreenConstraints.gridy = 0;
	backToHomescreenConstraints.ipady = 10;
	backToHomescreen.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		uiManager.showHomescreen();
	    }
	});
	getContentPane().add(backToHomescreen, backToHomescreenConstraints);

	if (macAddress != null) {
	    changeRegistration = new JButton();
	    changeRegistration.setText("Wijzig registratie");
	    GridBagConstraints changeRegistrationConstraints = new GridBagConstraints();
	    changeRegistrationConstraints.gridy = 1;
	    changeRegistrationConstraints.ipady = 10;
	    changeRegistration.addActionListener(new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
		    uiManager.resetHomescreenTimer();
		    uiManager.showIdentifyScreen(macAddress);
		}
	    });
	    getContentPane().add(changeRegistration, changeRegistrationConstraints);
	}

	switchDay = new JButton();
	GridBagConstraints switchDayConstraints = new GridBagConstraints();
	switchDayConstraints.gridy = 2;
	switchDayConstraints.ipady = 10;
	getContentPane().add(switchDay, switchDayConstraints);

	Border titleBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);

	generalTitle = new JLabel();
	generalTitle.setText("Algemene roosterwijzigingen");
	generalTitle.setBorder(titleBorder);
	Font titleFont = new Font(generalTitle.getFont().getName(), Font.BOLD, 16);
	generalTitle.setFont(titleFont);
	generalTitle.setForeground(Color.WHITE);
	GridBagConstraints generalTitleConstraints = new GridBagConstraints();
	generalTitleConstraints.gridy = 3;
	getContentPane().add(generalTitle, generalTitleConstraints);

	Border containerBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);

	generalContainer = new JPanel();
	generalContainer.setBackground(Color.WHITE);
	generalContainer.setLayout(new GridBagLayout());
	generalContainer.setBorder(containerBorder);
	GridBagConstraints generalContainerConstraints = new GridBagConstraints();
	generalContainerConstraints.gridy = 4;
	getContentPane().add(generalContainer, generalContainerConstraints);

	title = new JLabel();
	title.setBorder(titleBorder);
	title.setFont(titleFont);
	title.setForeground(Color.WHITE);
	GridBagConstraints titleConstraints = new GridBagConstraints();
	titleConstraints.gridy = 5;
	getContentPane().add(title, titleConstraints);

	specificContainer = new JPanel();
	specificContainer.setBackground(Color.WHITE);
	specificContainer.setLayout(new GridBagLayout());
	specificContainer.setBorder(containerBorder);
	GridBagConstraints containerConstraints = new GridBagConstraints();
	containerConstraints.gridy = 6;
	getContentPane().add(specificContainer, containerConstraints);

	switchDay(true, false);
	pack();

    }

    /**
     * Adds HTML formatting to the label text.
     *
     * @param textToDisplay the text to display on the label
     * @return a String which can be put on a label
     */
    private String getProperLabelText(String textToDisplay) {
	int screenWidthPixels = Toolkit.getDefaultToolkit().getScreenSize().width;
	return "<html><p style='width:" + screenWidthPixels * .5 + "px;'>" + textToDisplay + "</p></html>";
    }

    private int generalRow = 0;

    private int getGeneralRow() {
	return generalRow++;
    }

    private int row = 0;

    private int getRow() {
	return row++;
    }

    /**
     * Verandert de weergegeven roosterwijzigingen naar de gespecifieerde dag.
     *
     * @param today true als de roosterwijzigingen van vandaag weergegeven
     * moeten worden; false als de roosterwijzigingen van morgen weergegeven
     * moeten worden
     * @param repack true als het frame opnieuw weergegeven moet worden; anders
     * false
     * @throws SQLException als er geen toegang tot de database verkregen kon
     * worden
     * @throws ClassNotFoundException als het stuurprogramma voor de MySQL
     * server niet gevonden kon worden
     */
    public void switchDay(boolean today, boolean repack) throws SQLException, ClassNotFoundException {

	title.setText("Roosterwijzigingen voor " + identity);

	if (macAddress == null && changeRegistration != null) {
	    changeRegistration.setVisible(false);
	}
	generalContainer.removeAll();
	specificContainer.removeAll();

	CacheManager cacheManager = uiManager.getRoosterwijzigingen()
		.getFileManager().getCacheManager();
	if (cacheManager.scheduleCacheIsAvailable(today)) {

	    ArrayList<String> generalChanges = cacheManager.getGeneralScheduleChanges(today);
	    if (!generalChanges.isEmpty()) {
		generalChanges.stream().map((change) -> {
		    JLabel changeLabel = new JLabel();
		    changeLabel.setText(getProperLabelText(change));
		    changeLabel.setFont(new Font("Courier New", Font.BOLD, 14));
		    return changeLabel;
		}).forEach((changeLabel) -> {
		    GridBagConstraints changeLabelConstraints = new GridBagConstraints();
		    changeLabelConstraints.gridy = getGeneralRow();
		    changeLabelConstraints.anchor = GridBagConstraints.WEST;
		    generalContainer.add(changeLabel, changeLabelConstraints);
		});
	    } else {
		JLabel emptyLabel = new JLabel();
		emptyLabel.setText(getProperLabelText("Geen algemene roosterwijzigingen!"));
		emptyLabel.setFont(new Font("Courier New", Font.BOLD, 14));
		generalContainer.add(emptyLabel);
	    }

	    ArrayList<String> specificChanges = cacheManager.getSpecificScheduleChanges(studentID, today);
	    if (!specificChanges.isEmpty()) {
		specificChanges.stream().map((change) -> {
		    JLabel changeLabel = new JLabel();
		    changeLabel.setText(getProperLabelText(change));
		    changeLabel.setFont(new Font("Courier New", Font.BOLD, 14));
		    return changeLabel;
		}).forEach((changeLabel) -> {
		    GridBagConstraints changeLabelConstraints = new GridBagConstraints();
		    changeLabelConstraints.gridy = getRow();
		    changeLabelConstraints.anchor = GridBagConstraints.WEST;
		    specificContainer.add(changeLabel, changeLabelConstraints);
		});
	    } else {
		JLabel emptyLabel = new JLabel();
		emptyLabel.setText(getProperLabelText("Geen persoonlijke roosterwijzigingen!"));
		emptyLabel.setFont(new Font("Courier New", Font.BOLD, 14));
		specificContainer.add(emptyLabel);
	    }

	} else {
	    JLabel notAvailableLabel = new JLabel();
	    notAvailableLabel.setText((today)
		    ? getProperLabelText("De roosterwijzigingen van vandaag zijn nog niet beschikbaar!")
		    : getProperLabelText("De roosterwijzigingen van morgen zijn nog niet beschikbaar!"));
	    notAvailableLabel.setFont(new Font("Courier New", Font.BOLD, 14));
	    generalContainer.add(notAvailableLabel);
	    specificContainer.add(notAvailableLabel);
	}

	switchDay.setText((today)
		? "Wijzigingen voor morgen"
		: "Wijzigingen voor vandaag");
	for (ActionListener listener : switchDay.getActionListeners()) {
	    switchDay.removeActionListener(listener);
	}

	switchDay.addActionListener(new AbstractAction() {
	    private static final long serialVersionUID = 1L;

	    @Override
	    public void actionPerformed(ActionEvent e) {
		try {
		    uiManager.resetHomescreenTimer();
		    switchDay(!today, true);
		} catch (SQLException | ClassNotFoundException ex) {
		    Logger.getLogger(ScheduleFrame.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	}
	);

	if (repack) {
	    revalidate();
	    repaint();
	}

    }

}
