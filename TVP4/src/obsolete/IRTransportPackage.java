/*
 * IRTransportPackage.java
 *
 * Created on 13. marts 2007, 10:29
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

package obsolete;

/**
 *
 * @author LMK
 */
public class IRTransportPackage {
        
    private int sequenceNumber;
    private int acknowledgeNumber;
    private byte data;    
    
    public IRTransportPackage(int header, byte data) {
        this.sequenceNumber = (header & 0xF0);
        this.acknowledgeNumber = (header & 0x0F);
        this.data = data;
    }
    
    /** Creates a new instance of IRDatagram */
    public IRTransportPackage(int senquenceNumber, int acknowledgeNumber, byte data) {
        this.sequenceNumber = senquenceNumber;
        this.acknowledgeNumber = acknowledgeNumber;
        this.data = data;
    }
    
    public int getSequenceNumber() {
        return this.sequenceNumber;
    }
    
    public int getAcknowledgeNumber() {
        return this.acknowledgeNumber;
    }
    
    public int getHeader() {
        return ((this.sequenceNumber & 0x0F) << 4) | (this.acknowledgeNumber & 0x0F);
    }
    
    public byte getData() {
        return this.data;
    }
}