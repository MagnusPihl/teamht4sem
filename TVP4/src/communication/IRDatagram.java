/*
 * IRDatagram.java
 *
 * Created on 13. marts 2007, 10:14
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 15. marts 2007 (v 1.1)
 * Added static methods getSender, getReceiver, getAddressInfo
 *
 */

package communication;

/**
 *
 * @author LMK
 */
public class IRDatagram {
    
    private int sender;
    private int receiver;
    private byte[] data;    
    
    public IRDatagram(int addressInfo, byte[] data) {
        this.sender = (addressInfo & 0xF0);
        this.receiver = (addressInfo & 0x0F);
        this.data = data;
    }
    
    /** Creates a new instance of IRDatagram */
    public IRDatagram(int sender, int receiver, byte[] data) {
        this.sender = sender;
        this.receiver = receiver;
        this.data = data;
    }
    
    public int getSender() {
        return this.sender;
    }
    
    public int getReceiver() {
        return this.receiver;
    }
    
    public int getAddressInfo() {
        return ((sender & 0x0F) << 4) | (receiver & 0x0F);
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public static int getSender(int addressInfo) {
        return (addressInfo & 0xF0);
    }
    
    public static int getReceiver(int addressInfo) {
        return (addressInfo & 0x0F);
    }
    
    public static int getAdressInfo(int sender, int receiver) {
        return ((sender & 0x0F) << 4) | (receiver & 0x0F);
    }
}
