/*
 * LinkLayerSocket.java
 *
 * Created on 22. marts 2007, 11:20
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
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
    public static final int TIMEOUT = 2000; //millis approximately one frame
    
    /** Creates a new instance of LinkLayerSocket */
    public LinkLayerSocket() {        
        this.timeoutCount = 0;
    }
        
    public static boolean checksumIsValid(byte[] buffer) {                        
        int checksum = 0;
        for (int i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
            checksum += buffer[i];
        }

        if (checksum - buffer[CHECKSUM_OFFSET] == 0) {
            return true;
        }        
        
        return false;                        
    }
    
    public static void addChecksum(byte[] buffer) {            
        buffer[CHECKSUM_OFFSET] = 0;

        for (int i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
            buffer[CHECKSUM_OFFSET] += buffer[i];
        }

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
