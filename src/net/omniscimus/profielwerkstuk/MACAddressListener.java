package net.omniscimus.profielwerkstuk;

/**
 * Interface die alle Listeners moeten implementeren die verwittigd willen
 * worden wanneer een nieuwe lijst met devices vrijkomt.
 *
 * @author omniscimus
 */
public interface MACAddressListener {

    /**
     * Code die uitgevoerd moet worden wanneer een nieuwe lijst met MAC- en
     * IP-adressen van gedetecteerde devices vrijkomt.
     *
     * @param ip het IP-adres van het nieuwe gedetecteerde apparaat
     * @param mac het MAC-adres van het nieuwe gedetecteerde apparaat
     */
    public void onMACAddressUpdate(String ip, String mac);

}
