/*
 * LinkLayerSocket.java
 *
 * Created on 22. marts 2007, 11:20
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 30. april 2007 (v 1.1)
 * Fixed checksum overflow errors
 * LMK @ 22. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package communication;

import java.io.*;

/**
 *
 * @author LMK
 */
public abstract class LinkLayerSocket {
    protected OutputStream out;
    protected InputStream in;       
    protected int timeoutCount;
    
    public static final byte PACKET_HEADER = 0x55;
    public static final byte DATA_OFFSET = 2;
    public static final byte CHECKSUM_OFFSET = 8;
    public static final byte PACKET_SIZE = 10;
    public static final int TIMEOUT = 50; //millis approximately one frame
    
    /** Creates a new instance of LinkLayerSocket */
    public LinkLayerSocket() {        
        this.timeoutCount = 0;
    }
        
    public static boolean checksumIsValid(byte[] buffer) {                        
        int checksum = 0;
        for (int i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
            checksum += 0xFF & buffer[i];            
            if ((checksum & 0x0100) == 0x0100) {
                checksum ^= 0x0101;
            }
        }

        if ((byte)checksum - buffer[CHECKSUM_OFFSET] == 0) {
            return true;
        }        
        
        return false;                        
    }
    
    public static void addChecksum(byte[] buffer) {            
        int checksum = 0;

        for (int i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
            checksum += 0xFF & buffer[i];
            if ((checksum & 0x0100) == 0x0100) {
                checksum ^= 0x0101;
            }
        }

        buffer[CHECKSUM_OFFSET] = (byte)checksum;
        buffer[CHECKSUM_OFFSET + 1] = (byte)(~buffer[CHECKSUM_OFFSET]);
    }

    public OutputStream getOutputStream() {
        return this.out;
    }
    
    public InputStream getInputStream() {
        return this.in;
    }
    
    public int getTimeoutCount() {
        return this.timeoutCount;
    }
}
