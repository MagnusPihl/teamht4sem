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
 * Right now the read methods are none blocking
 * Synchronization must be done to prevent error when packetAvailable is run.
 *
 */

package communication;

import josx.platform.rcx.*;
import java.io.*;

public class RCXSocket {
        
    private OutputStream out;
    private InputStream in;
    
    public static final int INPUT_BUFFER_SIZE = 100;
    public static final int OUTPUT_BUFFER_SIZE = 50;
    
    /** Creates a new instance of RCXSocket */
    public RCXSocket() {
        Serial.resetSerial();
        this.in = new RCXSocket.RCXInputStream();
        this.out = new RCXSocket.RCXOutputStream();
    }            
    
    private class RCXInputStream extends InputStream implements SerialListener {
        private int readPointer;
        private int inPointer;
        private byte[] buffer;
        
        protected RCXInputStream() {       
            this.buffer = new byte[INPUT_BUFFER_SIZE];
            Serial.addSerialListener(this);
        }
                
        public int read() throws IOException {                
            if (this.readPointer < this.inPointer) {
                return this.buffer[++this.readPointer];
            
            } else if (this.readPointer > this.inPointer) {
                this.readPointer++;
                if (this.readPointer == this.buffer.length) {
                    this.readPointer = 0;
                }
                return this.buffer[this.readPointer];
            
            } else {
                return -1;
            }            
        }
        
        public int read(byte b[], int off, int len) throws IOException {
            //Start - Taken from InputStream
            if (b == null) {
                throw new NullPointerException();
            } else if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }
            //end

            int available = this.available();            

            for (int i = 0; i < available ; i++) {
                this.readPointer++;
                        
                if (this.readPointer == INPUT_BUFFER_SIZE) {
                    this.readPointer = 0;
                }
                
                b[i] = this.buffer[this.readPointer];
            }
            
            return available;
        }
        
        public int available() throws IOException {
            if (this.readPointer <= this.inPointer) {
                return this.inPointer - this.readPointer;
            } else {
                return this.inPointer + (INPUT_BUFFER_SIZE - this.readPointer);
            }
        }
        
        public void packetAvailable(byte[] packet, int length) {
            //ignore opcode and save the rest of the packet
            for (int i = 1; i < length; i++) {
                this.inPointer++;
                if (this.inPointer == INPUT_BUFFER_SIZE) {
                    this.inPointer = 0;
                }
                this.buffer[this.inPointer] = packet[i];
            }
        }
    }
    
    private class RCXOutputStream extends OutputStream {
        public void write(int b) throws IOException {
            this.write(new byte[] {(byte)b}, 0, 1);
        }
        
        public void write(byte[] b) throws IOException {
            this.write(b, 0, b.length);
        }
        
        public void write(byte[] b, int off, int len) throws IOException {
            try {
                while (Serial.isSending()) {
                    Serial.waitTillSent();
                }
            } catch (InterruptedException ie) {;}
            
            Serial.sendPacket(b, off, len);
        }                
    }    
    
    public OutputStream getOutputStream() {
        return this.out;
    }
    
    public InputStream getInputStream() {
        return this.in;
    }
}