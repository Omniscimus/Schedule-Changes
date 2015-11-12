package net.omniscimus.profielwerkstuk;

import java.util.Map;

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
     * @param ipToMAC een Map met als Key het IP-adres van de device en als
     * Value het MAC-adres
     */
    public void onMACAddressesUpdate(Map<String, String> ipToMAC);

}
