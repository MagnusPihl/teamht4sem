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
 * LMK @ 21. marts 2007 (v 1.0)
 * Derived from RCXSocket
 *
 */

package communication;

import josx.rcxcomm.*;
import java.io.*;

public class LLCSocket {
        
    private LLCOutputStream out;
    private LLCInputStream in;   
        
    public static final byte PACKET_HEADER = 0x55;
    public static final byte DATA_OFFSET = 2;
    public static final byte CHECKSUM_OFFSET = 8;
    public static final byte PACKET_SIZE = 10;
    
    /** Creates a new instance of RCXSocket */
    public LLCSocket() {        
        this.in = new LLCSocket.LLCInputStream();
        this.out = new LLCSocket.LLCOutputStream();
        LLC.init();
    }           
    
    public class LLCInputStream extends InputStream {
        private int readPointer;
        private int inPointer;
        private byte[] buffer;        
        
        protected LLCInputStream() {       
            this.buffer = new byte[PACKET_SIZE];
        }
        
        private boolean readPacket() {
            int data;
            this.inPointer = 0;            
            
            do {
                data = LLC.read();
                if (data != -1) {
                    if ((this.inPointer < DATA_OFFSET)) {
                        if (data == PACKET_HEADER) {
                            this.inPointer++;
                        } else {
                            this.inPointer = 0;
                        }
                    } else {
                        this.buffer[this.inPointer++] = (byte)data;
                    }
                }                
            } while (this.inPointer < PACKET_SIZE);            
            
            return this.checksum();
        }
        
        private boolean checksum() {                        
            int checksum = 0;
            for (int i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
                checksum += this.buffer[i];
            }
            
            if (checksum - this.buffer[CHECKSUM_OFFSET] == 0) {
                return true;
            }        
            
            this.inPointer = 0;
            return false;                        
        }
                
        public int read() throws IOException {                           
            if ((this.inPointer != PACKET_SIZE)||(this.readPointer == CHECKSUM_OFFSET)) {
                if (!this.readPacket()) {
                    return -1;
                } else {
                    this.readPointer = DATA_OFFSET;
                }
            }
                        
            byte output = this.buffer[this.readPointer];
            this.readPointer += 2;
            return output;            
        }                        
    }
    
    public class LLCOutputStream extends OutputStream {
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
        
        public void write(int buffer) throws IOException {
            this.buffer[this.writePointer++] = (byte)buffer;
            this.buffer[this.writePointer++] = (byte)(~buffer);
            
            if (this.writePointer == CHECKSUM_OFFSET) {
                this.addChecksum();
                LLC.sendBytes(this.buffer, PACKET_SIZE);
                this.writePointer = DATA_OFFSET;
            }
        }
                
        private void addChecksum() {            
            this.buffer[CHECKSUM_OFFSET] = 0;
            
            for (int i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
                this.buffer[CHECKSUM_OFFSET] += this.buffer[i];
            }
            
            this.buffer[CHECKSUM_OFFSET + 1] = (byte)(~this.buffer[CHECKSUM_OFFSET]);
        }
    }    
    
    public LLCOutputStream getOutputStream() {
        return this.out;
    }
    
    public LLCInputStream getInputStream() {
        return this.in;
    }
}