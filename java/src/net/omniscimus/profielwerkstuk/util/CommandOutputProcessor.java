package net.omniscimus.profielwerkstuk.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deze class kan een commando sturen en de output verwerken.
 *
 * @author omniscimus
 */
public class CommandOutputProcessor {

    /**
     * Zoek de MAC-adressen bij de gegeven IP-adressen. Als het MAC-adres niet
     * gevonden kan worden, komt dat IP-adres niet in de resultaten.
     *
     * @param ips een lijst met IP-adressen waar MAC-adressen bij moeten worden
     * gevonden
     * @return een Map&lt;String, String&gt; waarbij de key het IP-adres is en
     * de value het MAC-adres
     */
    public static Map<String, String> getMACAddresses(ArrayList<String> ips) {

	Map<String, String> ipToMAC = new HashMap<>();
	ips.stream().forEach((ip) -> {
	    try {
		String mac = getMACAddressByIP(ip);
		if (mac != null) {
		    ipToMAC.put(ip, mac);
		}
	    } catch (IOException e) {
		// Er was een fout tijdens het lezen van de command output.
		// Doe niets, log het eventueel, dan kunnen de andere dingen doorgaan.
	    }
	});

	return ipToMAC;

    }

    // 1 of 2 characters en dan een dubbele punt, dat 5 keer, dan nog eens 1 of 2 characters.
    // characters moeten ofwel a-f zijn, ofwel A-F, ofwel 0-9
    private static final Pattern macPattern = Pattern.compile("([a-fA-F0-9][a-fA-F0-9]?:){5}?[a-fA-F0-9][a-fA-F0-9]?");
    private static final Pattern windowsMACPattern = Pattern.compile("([a-fA-F0-9][a-fA-F0-9]?-){5}?[a-fA-F0-9][a-fA-F0-9]?");
    // drie keer iets van 1-255 met een punt erachter, daarna nog een keer zonder punt.
    private static final Pattern ipPattern = Pattern.compile("(\\d{1,3}?\\.){3}\\d\\d?\\d?");

    /**
     * Zoek het MAC-adres bij een IP-adres.
     *
     * @param ip het IP-adres waarvan het MAC-adres gevonden moet worden
     * @return het MAC-adres dat bij ip hoort, of null als er geen gevonden kan
     * worden
     * @throws IOException als er een fout optreedt tijdens het uitvoeren van
     * het commando
     */
    public static String getMACAddressByIP(String ip) throws IOException {

	if (System.getProperty("os.name").toLowerCase().contains("win")) {
	    return getMACAddressByIPWindows(ip);
	} else {
	    return getMACAddressByIPNotWindows(ip);
	}

    }

    /**
     * Zoek het MAC-adres bij een IP-adres als het OS niet Windows is.
     *
     * @param ip het IP-adres waarvan het MAC-adres gevonden moet worden
     * @return het MAC-adres dat bij ip hoort, of null als er geen gevonden kan
     * worden
     * @throws IOException als er een fout optreedt tijdens het uitvoeren van
     * het commando
     */
    private static String getMACAddressByIPNotWindows(String ip) throws IOException {
	Matcher macMatcher = macPattern.matcher(executeCommand("arp -n " + ip).readLine());
	macMatcher.find();
	try {
	    String mac = macMatcher.group();
	    if (mac.equals("")) {
		return null;
	    } else {
		return mac;
	    }
	} catch (IllegalStateException e) {
	    return null;
	}
    }

    /**
     * Zoek het MAC-adres bij een IP-adres als het OS Windows is.
     *
     * @param ip het IP-adres waarvan het MAC-adres gevonden moet worden
     * @return het MAC-adres dat bij ip hoort, of null als er geen gevonden kan
     * worden
     * @throws IOException als er een fout optreedt tijdens het uitvoeren van
     * het commando
     */
    private static String getMACAddressByIPWindows(String ip) throws IOException {
	// Windows moet telkens eerst pingen, dan de hele ARP-tabel geven.
	BufferedReader commandOutputReader = executeCommand("arp -a");
	String line;
	while ((line = commandOutputReader.readLine()) != null) {
	    Matcher ipMatcher = ipPattern.matcher(line);
	    if (ipMatcher.find()) {
		String foundIP = ipMatcher.group();
		if (foundIP != null && foundIP.equals(ip)) {
		    Matcher macMatcher = windowsMACPattern.matcher(line);
		    macMatcher.find();
		    try {
			String mac = macMatcher.group();
			if (mac.equals("")) {
			    return null;
			} else {
			    return mac;
			}
		    } catch (Exception e) {
			return null;
		    }
		}
	    }
	}
	return null;
    }

    /**
     * Geef een commando aan het systeem.
     *
     * @param command het commando dat uitgevoerd moet worden
     * @return een BufferedReader met de output van het commando
     * @throws IOException als er een fout optreedt tijdens het uitvoeren van
     * het commando
     */
    private static BufferedReader executeCommand(String command) throws IOException {
	Process p = Runtime.getRuntime().exec(command);
	return new BufferedReader(new InputStreamReader(p.getInputStream()));
    }

}
