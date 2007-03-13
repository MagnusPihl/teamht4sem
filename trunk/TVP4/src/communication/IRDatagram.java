/*
 * IRDatagram.java
 *
 * Created on 13. marts 2007, 10:14
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 13. marts 2007 (v 1.0)
 * __________ Changes ____________
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
}
