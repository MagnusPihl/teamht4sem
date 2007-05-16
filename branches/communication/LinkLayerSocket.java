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
    private int checksum;
    private int i;
    
    public static final byte PACKET_HEADER = 0x55;
    public static final byte ESCAPE_CHAR = ~PACKET_HEADER;
    public static final byte DATA_OFFSET = 2;
    public static final byte CHECKSUM_OFFSET = 8;
    public static final byte PACKET_SIZE = 10;
    public static final int TIMEOUT = 10; //ms, maximum time between bytes. 
     
    /**
     * Generate checksum of available data in buffer and compare it to 
     * checksum in buffer
     *
     * @param byte[] buffer
     * @return true if checksum is valid
     */
    protected boolean checksumIsValid(byte[] buffer) {                        
        this.checksum = 0;
        for (i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
            this.checksum += 0xFF & buffer[i];            
            if ((this.checksum & 0x0100) == 0x0100) {
                this.checksum ^= 0x0101;
            }
        }

        if ((byte)checksum - buffer[CHECKSUM_OFFSET] == 0) {
            return true;
        }        
        
        return false;                        
    }
    
    /**
     * Add checksum to data in buffer.
     * Checksum is calculated based on every second value from DATA_OFFSET to
     * CHECKSUM_OFFSET-1.
     *
     * @param byte[] buffer
     */
    protected void addChecksum(byte[] buffer) {            
        this.checksum = 0;

        for (i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
            this.checksum += 0xFF & buffer[i];
            if ((this.checksum & 0x0100) == 0x0100) {
                this.checksum ^= 0x0101;
            }
        }

        buffer[CHECKSUM_OFFSET] = (byte)checksum;
        buffer[CHECKSUM_OFFSET + 1] = (byte)(~buffer[CHECKSUM_OFFSET]);
    }    
    
    /**
     * Get InputStream
     */
    public abstract ClearableInputStream getInputStream();
    
    /**
     * Get OutputStream
     */
    public abstract ClearableOutputStream getOutputStream();
}