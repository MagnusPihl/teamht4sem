/*
 * LLCSocket.java
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
 * MHP @ 28. april 2007 (v 1.3)
 * Removed timeout from readPacket. Will now instantly return false if no data is available.
 *
 * LMK @ 23. marts 2007 (v 1.1) 
 * Moved checksum methods and features common with TowerSocket to LinkLayerSocket
 * Added timeouts to read methods.
 *
 * LMK @ 21. marts 2007 (v 1.0)
 * Derived from RCXSocket
 *
 */

package communication;

import josx.rcxcomm.*;
import java.io.*;

public class LLCSocket extends LinkLayerSocket {                
    
    private LLCOutputStream out;
    private LLCInputStream in;       
    
    /** Creates a new instance of RCXSocket */
    public LLCSocket() { 
        super();
        this.in = new LLCSocket.LLCInputStream();
        this.out = new LLCSocket.LLCOutputStream();
        LLC.init();
        LLC.setRangeLong();
    }           
    
    public class LLCInputStream extends ClearableInputStream {
        private int readPointer;
        private int inPointer;
        private byte[] buffer;        
        private int timeout;
        private int data;
        
        protected LLCInputStream() {       
            super();
            this.buffer = new byte[PACKET_SIZE];
            this.readPointer = 0;
            this.inPointer = 0;       
        }
        
        private synchronized boolean readPacket() {
            this.inPointer = 0;            
            timeout = (int)System.currentTimeMillis() + TIMEOUT; 
            
            do {
                this.data = LLC.read();
                if (this.data != -1) {
                    if ((this.inPointer < DATA_OFFSET)) {
                        if (this.data == PACKET_HEADER) {
                            this.inPointer++;
                            timeout = (int)System.currentTimeMillis() + TIMEOUT; 
                        } else {
                            this.inPointer = 0;
                        }
                    } else {
                        this.buffer[this.inPointer++] = (byte)this.data;
                        timeout = (int)System.currentTimeMillis() + TIMEOUT; 
                    }
                } else if ((this.inPointer == 0) || ((int)System.currentTimeMillis() > timeout)) {
                    this.inPointer = 0;
                    return false;
                }
            } while (this.inPointer < PACKET_SIZE);            
            
            this.inPointer = 0;
            
            return checksumIsValid(this.buffer);                
        }        
                
        public int read() throws IOException {      
            if (this.readPointer == this.inPointer) {
                if (!this.readPacket()) {
                    return -1;
                } else {
                    this.readPointer = DATA_OFFSET;
                }
            }
            
            this.data = this.buffer[this.readPointer];
            this.readPointer += 2;
                        
            if (this.readPointer == CHECKSUM_OFFSET) {
                this.readPointer = 0;
            }
                        
            return this.data & 0xFF;            
        }      
        
        public void clear() {
            this.readPointer = this.inPointer;
        }
    }
    
    public class LLCOutputStream extends ClearableOutputStream {
        private int writePointer;
        private byte[] buffer;               
        
        public LLCOutputStream() {
            this.buffer = new byte[PACKET_SIZE];
            this.buffer[0] = PACKET_HEADER;
            this.buffer[1] = PACKET_HEADER;
            this.writePointer = DATA_OFFSET;
                
            //2 header bytes.
            //data 1, inverse of data 1
            //data 2, inverse of data 2
            //data 3, inverse of data 3
            //checksum, inverse of checksum
        }
        
        public synchronized void write(int buffer) throws IOException {
            this.buffer[this.writePointer++] = (byte)buffer;
            this.buffer[this.writePointer++] = (byte)(~buffer);
            
            if (this.writePointer == CHECKSUM_OFFSET) {
                addChecksum(this.buffer);
                LLC.sendBytes(this.buffer, PACKET_SIZE);
                this.writePointer = DATA_OFFSET;
            }
        }   
        
        public void clear() {
            this.writePointer = DATA_OFFSET;
        }
    }    
    
    
    public void clear() {
        this.in.clear();
        this.out.clear();
    }
    
    public ClearableInputStream getInputStream() {
        return this.in;
    }
    
    public ClearableOutputStream getOutputStream() {
        return this.out;
    }    
}