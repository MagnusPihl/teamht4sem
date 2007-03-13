/*
 * RCXSocket.java
 *
 * Created on 13. marts 2007, 10:52
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

import josx.platform.rcx.*;
import java.io.*;

/**
 *
 * @author LMK
 */
public class RCXSocket implements SerialListener {
    
    private int readPointer;
    private int writePointer;
    private byte[] buffer;
    private OutputStream out;
    private InputStream in;
    
    /** Creates a new instance of RCXSocket */
    public RCXSocket() {
        this.buffer = new byte[100];
        this.in = RCXnputStream();
        this.out = RCXOutputStream();
        Serial.setDataBuffer(this.buffer);
        Serial.addSerialListener(this);
    }        
    
    public void packetAvailable(byte[] packet, int length) {
        
    }
    
    private class RCXInputStream extends InputStream {
        public int read() throws IOException {            
            int output = buffer[readPointer];
            
            readPointer++;
            if (readPointer == buffer.length) {
                readPointer = 0;
            }
            
            return output;
        }
        
        public int available() throws IOException {
            return buffer;
        }

        public void close() throws IOException {}

        public synchronized void mark(int readlimit) {}

        public synchronized void reset() throws IOException {
            throw new IOException("mark/reset not supported");
        }
        
        public boolean markSupported() {
            return false;
        }
    }
    
    private class RCXOutputStream extends OutputStream {
        public void write(int b) throws IOException {
            Serial.sendPacket(new byte[] {(byte)b}, 0, 1);
        }
        
        public void write(byte[] b) throws IOException {
            Serial.sendPacket(b, 0, b.length);
        }
        
        public void write(byte[] b, int off, int len) throws IOException {
            Serial.sendPacket(buffer, off, len);
        }
        
        public void flush() throws IOException {
        }
        
        public void close() throws IOException {
        }
    }    
    
    public OutputStream getOutputStream() {
        return this.out;
    }
    
    public InputStream getInputStream() {
        return this.in;
    }
}