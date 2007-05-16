/*
 * IRDatagram.java
 *
 * Created on 13. marts 2007, 10:14
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.2
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 16. marts 2007 (v 1.2)
 * Changed addressInfo to addressHeader
 * LMK @ 15. marts 2007 (v 1.1)
 * Added static methods getSender, getReceiver, getAddressInfo
 *
 */

package communication;

/**
 *
 * @author LMK
 */
public class NetworkDatagram {
    
    private int sender;
    private int receiver;
    private byte[] data;    
    
    /**
     * Create new datagram with address header and data
     *
     * @param addressHeader, 4 most significant bits is sender, 
     * 4 least significant bits is receiver.
     * @param data
     */
    public NetworkDatagram(int addressHeader, byte[] data) {
        this.sender = (addressHeader & 0xF0) >> 4;
        this.receiver = (addressHeader & 0x0F);
        this.data = data;
    }
    
    /** 
     * Creates a new instance of IRDatagram 
     *
     * @param int sender, only the 4 least significant bits can be used
     * @param int receiver, only the 4 least significant bits can be used
     * @param data
     */
    public NetworkDatagram(int sender, int receiver, byte[] data) {
        this.sender = sender;
        this.receiver = receiver;
        this.data = data;
    }
    
    /**
     * Get sender
     *
     * @result int with 4 least significant bits containing sender ID
     */
    public int getSender() {
        return this.sender;
    }
    
    
    /**
     * Get receiver
     *
     * @result int with 4 least significant bits containing receiver ID
     */
    public int getReceiver() {
        return this.receiver;
    }
    
    
    /**
     * Get address header.
     *
     * @result int containing sender and receiver.
     */
    public int getAddressHeader() {
        return ((sender & 0x0F) << 4) | (receiver & 0x0F);
    }
    
    /**
     * Get data portion of datagram.
     *
     * @return byte[]
     */
    public byte[] getData() {
        return this.data;
    }
    
    /**
     * Get sender portion of supplied address header.
     *
     * @param addressHeader
     * @result int with 4 least significant bits containing sender ID.
     */
    public static int getSender(int addressHeader) {
        return (addressHeader & 0xF0) >> 4;
    }
    
    /**
     * Get receiver portion of supplied address header.
     *
     * @param addressHeader
     * @result int with 4 least significant bits containing receiver ID.
     */
    public static int getReceiver(int addressHeader) {
        return (addressHeader & 0x0F);
    }
    
    /**
     * Create address header using supplied sender and receiver.
     *
     * @param sender
     * @param receiver
     */
    public static int getAdressHeader(int sender, int receiver) {
        return ((sender & 0x0F) << 4) | (receiver & 0x0F);
    }
}
