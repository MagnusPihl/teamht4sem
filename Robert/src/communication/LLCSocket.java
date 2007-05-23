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
    
    public class LLCInputStream extends InputStream {
        private byte readPointer;
        private byte inPointer;
        private byte[] buffer;        
        private int timeout;
        private int data;
        
        /**
         * Create an InputStream that reads data package from IR stream.         
         */
        protected LLCInputStream() {       
            super();
            this.buffer = new byte[PACKET_SIZE];
            this.readPointer = 0;
            this.inPointer = 0;       
        }
        
        /**
         * Try to read a packet from IR stream. 
         *
         * @return true if a packet could be read. If no data is available at first
         * read false is returned. If no data could be read within the timeout 
         * (10 ms) false is returned.
         */
        private synchronized boolean readPacket() {
            this.inPointer = 0;            
            timeout = (int)System.currentTimeMillis() + TIMEOUT; 
            
            do {
                this.data = LLC.read();
                if (this.data != -1) {
                    if ((this.inPointer < DATA_OFFSET)) {
                        if ((byte)this.data == PACKET_HEADER) {
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
                
        /**
         * Read a single byte from buffer. The byte is returned as the 8 least
         * significant bits of an integer.
         * 
         * @return a single data byte if available, -1 otherwise.
         */
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
                        
            return ((int)this.data) & 0xFF;            
        }              
    }
    
    public class LLCOutputStream extends OutputStream {
        private byte writePointer;
        private byte[] buffer;               
        
        /**
         * Create new OutputStream with which it is possible to write
         * package to the IR stream.
         */
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
        
        /**
         * Write a byte to the output buffer. When the output buffer has been
         * filled the contents will be packaged and sent. The package size
         * is fixed so three bytes must always be sent.
         *
         * @param data byte to sent. Only the 8 least signifacant bits 
         * are preserved, all other bits are discarded.
         */
        public synchronized void write(int buffer) throws IOException {
            this.buffer[this.writePointer++] = (byte)buffer;
            this.buffer[this.writePointer++] = (byte)(~buffer);
            
            if (this.writePointer == CHECKSUM_OFFSET) {
                addChecksum(this.buffer);
                LLC.sendBytes(this.buffer, PACKET_SIZE);
                this.writePointer = DATA_OFFSET;
            }
        }           
    }        
    
    /**
     * Get InputStream to read from.
     */
    public InputStream getInputStream() {
        return this.in;
    }
    
    /**
     * Get OutputStream to write to.
     */
    public OutputStream getOutputStream() {
        return this.out;
    }    
}