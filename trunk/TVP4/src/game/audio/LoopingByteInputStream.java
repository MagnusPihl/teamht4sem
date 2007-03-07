/*
 * LoopingByteInputStream.java
 *
 * Taken from "Devoloping Games in Java" [Brackeen]
 * by Brackeen, David
 * www.brackeen.com
 * Listing 4.2 LoopingByteInputStream.java, page 171-173
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 6. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game.audio;

import java.io.*;

/**
 *
 * @author LMK
 */
public class LoopingByteInputStream extends ByteArrayInputStream {
    
    private boolean closed;
    
    /** 
     * Create a looping input stream reading from supplied buffer.
     *
     * @param buffer to read from.
     */
    public LoopingByteInputStream(byte[] buffer) {
        super(buffer);
        closed = false;
    }
    
    /**
     * Read bytes into buffer. If the end of the buffer is reached the read
     * will continue from the start of the array.
     *
     * @param buffer to read data into
     * @param offset to read from
     * @param amount of bytes to read
     */
    public int read(byte[] buffer, int offset, int length) {
        if (closed) {
            return -1;
        }
        
        int totalBytesRead = 0;
        int bytesRead = -1;
        
        while (totalBytesRead < length) {            
            bytesRead = super.read(
                    buffer, 
                    offset + totalBytesRead, 
                    length - totalBytesRead);
            
            if (bytesRead > 0) {
                totalBytesRead += bytesRead;
            } else {
                super.reset();
            }
        }
        
        return totalBytesRead;
    }
    
    /**
     * Close stream. Future calls to read will return -1. 
     */
    public void close() throws IOException {
        super.close();
        closed = true;
    }
}
