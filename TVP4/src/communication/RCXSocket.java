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
        
    private RCXOutputStream out;
    private RCXInputStream in;
    
    public static final int INPUT_BUFFER_SIZE = 100;
    
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
        
        public int read(byte[] buffer, int offset, int length) throws IOException {            
            //Start - Taken from InputStream
            if (buffer == null) {
                throw new NullPointerException();
            } else if (offset < 0 || length < 0 || length > buffer.length - offset) {
                throw new IndexOutOfBoundsException();
            } else if (length == 0) {
                return 0;
            }
            //end

            int available = this.available();            

            for (int i = 0; i < available ; i++) {
                this.readPointer++;
                        
                if (this.readPointer == INPUT_BUFFER_SIZE) {
                    this.readPointer = 0;
                }
                
                buffer[i] = this.buffer[this.readPointer];
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
            //1 - ignore opcode and save the rest of the packet
            //0 - opcodes  are also saved if they are present? not sure?
            for (int i = 0; i < length; i++) {
                this.inPointer++;
                if (this.inPointer == INPUT_BUFFER_SIZE) {
                    this.inPointer = 0;
                }
                this.buffer[this.inPointer] = packet[i];
            }
        }        
    }
    
    private class RCXOutputStream extends OutputStream {
        public void write(int buffer) throws IOException {
            this.write(new byte[] {(byte)buffer}, 0, 1);
        }
        
        public void write(byte[] buffer) throws IOException {
            this.write(buffer, 0, buffer.length);
        }
        
        public void write(byte[] buffer, int offset, int length) throws IOException {
            try {
                while (Serial.isSending()) {
                    Serial.waitTillSent();
                }
            } catch (InterruptedException ie) {;}
            
            Serial.sendPacket(buffer, offset, length);
        }                
    }    
    
    public RCXOutputStream getOutputStream() {
        return this.out;
    }
    
    public RCXInputStream getInputStream() {
        return this.in;
    }
}