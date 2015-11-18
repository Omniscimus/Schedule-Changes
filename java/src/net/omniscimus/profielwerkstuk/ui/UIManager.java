package net.omniscimus.profielwerkstuk.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import net.omniscimus.profielwerkstuk.Roosterwijzigingen;
import net.omniscimus.profielwerkstuk.EventAnnouncer;
import net.omniscimus.profielwerkstuk.configuration.ConfigValueCache;

/**
 * Basispunt voor alle User Interface handelingen.
 *
 * @author omniscimus
 */
public class UIManager {

    private final Roosterwijzigingen rw;

    /**
     * Maakt een nieuwe UIManager.
     *
     * @param rw de basis van dit programma
     */
    public UIManager(Roosterwijzigingen rw) {
	this.rw = rw;
    }

    /**
     * Geeft het basispunt voor dit programma, waar o.a. de SQL sectie
     * opgevraagd kan worden.
     *
     * @return de Roosterwijzigingen instance die dit programma aanstuurt
     */
    public Roosterwijzigingen getRoosterwijzigingen() {
	return rw;
    }

    private UIListener uiListener;
    private HomescreenFrame homescreenFrame;
    private RegisterFrame registerFrame;
    private IdentifyFrame identifyFrame;
    private ScheduleFrame scheduleFrame;
    private HelpFrame helpFrame;
    private JFrame currentFrame;

    private Timer homescreenTimer;
    private HomescreenTimerTask homescreenTimerTask;

    /**
     * Geeft de JFrame die op dit moment zichtbaar is voor de gebruikers.
     *
     * @return de huidige zichtbare JFrame
     */
    public JFrame getCurrentFrame() {
	return currentFrame;
    }

    /**
     * Laadt de User Interface; laat het homescreen zien.
     */
    public void load() {
	uiListener = new UIListener(rw, this);
	EventAnnouncer.registerListener(uiListener);
	showHomescreen();
    }

    /**
     * Laat het homescreen zien.
     */
    public void showHomescreen() {
	EventQueue.invokeLater(() -> {
	    if (homescreenFrame == null) {
		homescreenFrame = new HomescreenFrame(this);
	    }
	    homescreenFrame.getContentPane().setBackground(Color.YELLOW);
	    homescreenFrame.setVisible(true);
	    stopHomescreenTimer();
	    if (currentFrame != null && currentFrame != homescreenFrame) {
		currentFrame.setVisible(false);
	    }
	    currentFrame = homescreenFrame;
	});
    }

    /**
     * Laat het scherm zien waar men zijn device kan aanklikken.
     */
    public void showRegisterScreen() {
	EventQueue.invokeLater(() -> {
	    if (registerFrame == null) {
		registerFrame = new RegisterFrame(this);
	    }
	    registerFrame.getContentPane().setBackground(Color.GREEN);
	    registerFrame.setVisible(true);
	    if (currentFrame != null && currentFrame != registerFrame) {
		currentFrame.setVisible(false);
	    }
	    currentFrame = registerFrame;
	    startHomescreenTimer();
	});
    }

    /**
     * Laat het scherm zien waar men zijn naam in moet vullen om zich te
     * registreren.
     *
     * @param macAddressToRegister het MAC-adres dat aan de naam van de
     * betreffende leerling gekoppeld moet worden
     */
    public void showIdentifyScreen(String macAddressToRegister) {
	EventQueue.invokeLater(() -> {
	    if (identifyFrame == null) {
		identifyFrame = new IdentifyFrame(this);
	    }
	    identifyFrame.setMACAddressToRegister(macAddressToRegister);
	    identifyFrame.getContentPane().setBackground(Color.ORANGE);
	    identifyFrame.setVisible(true);
	    if (currentFrame != null && currentFrame != identifyFrame) {
		currentFrame.setVisible(false);
	    }
	    currentFrame = identifyFrame;
	    startHomescreenTimer();
	});
    }

    /**
     * Laat het scherm met gepersonaliseerde roosterwijzigingen zien.
     *
     * @param studentID de leerlingnummer van de betreffende leerling
     */
    public void showScheduleFrame(int studentID) {
	EventQueue.invokeLater(() -> {
	    boolean success = true;
	    if (scheduleFrame == null) {
		try {
		    scheduleFrame = new ScheduleFrame(this);
		    if (scheduleFrame.setIdentity(studentID)) {
			scheduleFrame.initComponents();
		    } else {
			success = false;
		    }
		} catch (SQLException | ClassNotFoundException ex) {
		    Logger.getLogger(UIManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		try {
		    if (scheduleFrame.setIdentity(studentID)) {
			scheduleFrame.switchDay(true, true);
		    } else {
			success = false;
		    }
		} catch (SQLException | ClassNotFoundException ex) {
		    Logger.getLogger(UIManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	    if (success) {
		scheduleFrame.getContentPane().setBackground(Color.BLUE);
		scheduleFrame.setVisible(true);
		if (currentFrame != null && currentFrame != scheduleFrame) {
		    currentFrame.setVisible(false);
		}
		currentFrame = scheduleFrame;
		startHomescreenTimer();
	    }
	});
    }

    /**
     * Laat het scherm met gepersonaliseerde roosterwijzigingen zien voor de
     * betreffende leerling.
     *
     * @param identity de naam van de leerling
     * @param macAddress het MAC-adres van de leerling
     */
    public void showScheduleFrame(String identity, String macAddress) {
	EventQueue.invokeLater(() -> {
	    if (scheduleFrame == null) {
		try {
		    scheduleFrame = new ScheduleFrame(this);
		    scheduleFrame.setIdentity(identity, macAddress);
		    scheduleFrame.initComponents();
		} catch (SQLException | ClassNotFoundException ex) {
		    Logger.getLogger(UIManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	    } else {
		try {
		    scheduleFrame.setIdentity(identity, macAddress);
		    scheduleFrame.switchDay(true, true);
		} catch (SQLException | ClassNotFoundException ex) {
		    Logger.getLogger(UIManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	    }
	    scheduleFrame.getContentPane().setBackground(Color.BLUE);
	    scheduleFrame.setVisible(true);
	    if (currentFrame != null && currentFrame != scheduleFrame) {
		currentFrame.setVisible(false);
	    }
	    currentFrame = scheduleFrame;
	    startHomescreenTimer();
	});
    }

    /**
     * Geeft het scherm met gebruikersinstructies weer.
     */
    public void showHelpFrame() {
	EventQueue.invokeLater(() -> {
	    if (helpFrame == null) {
		helpFrame = new HelpFrame(this);
	    }
	    helpFrame.getContentPane().setBackground(Color.LIGHT_GRAY);
	    helpFrame.setVisible(true);
	    if (currentFrame != null && currentFrame != helpFrame) {
		currentFrame.setVisible(false);
	    }
	    currentFrame = helpFrame;
	    startHomescreenTimer();
	});
    }

    /**
     * Start een timer zodat na de tijd die aangegeven is in het
     * configuratiebestand het HomescreenFrame weergegeven wordt.
     */
    private void startHomescreenTimer() {
	if (homescreenTimer == null) {
	    homescreenTimer = new Timer();
	}
	homescreenTimerTask = new HomescreenTimerTask(this);
	homescreenTimer.schedule(homescreenTimerTask, ConfigValueCache.getInteractionTimeout());
    }

    /**
     * Stopt de timer die ervoor zorgt dat na een bepaalde tijd het
     * HomescreenFrame weergegeven wordt.
     */
    private void stopHomescreenTimer() {
	if (homescreenTimer != null && homescreenTimerTask != null) {
	    homescreenTimerTask.cancel();
	}
    }

    /**
     * Reset de timer die ervoor zorgt dat na een bepaalde tijd het Homescreen
     * weergegeven wordt. Dit moet uitgevoerd worden bij een
     * gebruikersinteractie op een frame anders dan het HomescreenFrame.
     */
    public void resetHomescreenTimer() {
	stopHomescreenTimer();
	startHomescreenTimer();
    }

}

/**
 * Deze class zorgt ervoor dat het programma na een bepaalde tijd van
 * inactiviteit weer terugkeert naar het Homescreen.
 *
 * @author omniscimus
 */
class HomescreenTimerTask extends TimerTask {

    private final UIManager uiManager;

    /**
     * Maakt een nieuwe HomescreenTimerTask.
     *
     * @param uiManager
     */
    HomescreenTimerTask(UIManager uiManager) {
	this.uiManager = uiManager;
    }

    @Override
    public void run() {
	uiManager.showHomescreen();
    }

}
