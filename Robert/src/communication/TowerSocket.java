/*
 * TowerSocket.java
 *
 * Created on 13. marts 2007, 10:52
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.2
 *
 *
 * ******VERSION HISTORY******
 * MHP @ 28. april 2007 (v 1.3)
 * Removed timeout from readPacket. Will now instantly return false if no data is available.
 *
 * LMK @ 23. marts 2007 (v 1.2)
 * Removed printout when sending
 *
 * LMK @ 23. marts 2007 (v 1.1)
 * Moved checksum methods and features common with LLCSocket to LinkLayerSocket
 * Added timeouts to read methods.
 *
 * LMK @ 14. marts 2007 (v 1.0)
 *
 */

package communication;

import java.util.concurrent.Semaphore;
import josx.rcxcomm.*;
import java.io.*;

public class TowerSocket extends LinkLayerSocket {
    
    private Tower tower;
    
    private int bufferIndex;
    private byte[] readBuffer;
    private int packetIndex;
    private byte[] packetBuffer;
    private boolean isOpen;
    
    private static Semaphore sem = new Semaphore(1);
    
    public static final int INPUT_BUFFER_SIZE = 400;
    
    /**
     * Creates a new instance of TowerSocket that can communicate with
     * the RCX kit over IR. Reception of data is not guaranteed.
     * No addressing is done.
     */
    public TowerSocket() {
        super();
        this.tower = new Tower();
        this.bufferIndex = 0;
        this.readBuffer = new byte[INPUT_BUFFER_SIZE];
        
        this.packetBuffer = new byte[PACKET_SIZE];
        this.packetIndex = DATA_OFFSET;
    }
    
    public void open(String port) {
        this.tower.open(port);
    }
    
    public void close() {
        this.tower.close();
    }
    
    /**
     * Read packet to input stream buffer.
     * @return true if packet received has a valid checksum and has been added
     * to buffer, or false if the packet was invalid and trashed.
     */
    private synchronized boolean readPacket() {
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
//            ex.printStackTrace();
        }
        byte[] data = new byte[1];
        int available = 0;
        this.packetIndex = 0;
        long timeout = System.currentTimeMillis() + TIMEOUT;
        
        do {
            available = this.tower.read(data);
            if (available == 1) {
                if ((this.packetIndex < DATA_OFFSET)) {
                    //wait for start bytes.
                    if (data[0] == PACKET_HEADER) {
                        this.packetIndex++;
                        timeout = System.currentTimeMillis() + TIMEOUT;
                    } else {
                        this.packetIndex = 0;
                    }
                } else {
                    this.packetBuffer[this.packetIndex++] = data[0];
                    timeout = System.currentTimeMillis() + TIMEOUT;
                }
            } else if ((this.packetIndex == 0) || (System.currentTimeMillis() > timeout)) {                
                sem.release();
                return false;
            }
        } while (this.packetIndex < PACKET_SIZE);        
        
        //if checksum is valid add packet to stream.
        if (super.checksumIsValid(this.packetBuffer)) {
            sem.release();            
            for (int i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
                this.readBuffer[this.bufferIndex] = this.packetBuffer[i];
                
                this.bufferIndex++;
                if (this.bufferIndex == INPUT_BUFFER_SIZE) {
                    this.bufferIndex = 0;
                }
            }
            return true;
        } else {
            sem.release();
            return false;
        }
    }
    
    /**
     * InputStream that converts incoming packets into an inputstream.
     * Packets are always 3 bytes long.
     */
    public class TowerInputStream extends ClearableInputStream {
        private int readIndex;
        private byte data;
        
        protected TowerInputStream() {
            this.readIndex = bufferIndex;
        }
        
        /**
         * Read one byte from IR stream. The data received is not guarenteed
         * to be meant for you, no addressing is done.
         *
         * @return -1 if no bytes could be read.
         */
        public int read() throws IOException {
            if ((bufferIndex == this.readIndex)) {
                if (!readPacket()) {
                    return -1;
                }
            }
            
            this.data = readBuffer[this.readIndex];
            
            this.readIndex++;
            if (this.readIndex == INPUT_BUFFER_SIZE) {
                this.readIndex = 0;
            }
            
            return this.data & 0xFF;
        }
        
        /**
         * Clear buffer
         */
        public synchronized void clear() {
            this.readIndex = bufferIndex;
        }
    }
    
    /**
     * Output stream that let's you write packets to IR.
     * Each packet can hold 3 bytes. A packet is sent every 3 bytes written.
     */
    public class TowerOutputStream extends ClearableOutputStream {
        
        private byte[] packetBuffer;
        private int packetIndex;
        
        /**
         * Create new TowerOutputStream.
         */
        protected TowerOutputStream() {
            this.packetBuffer = new byte[PACKET_SIZE];
            this.packetBuffer[0] = PACKET_HEADER;
            this.packetBuffer[1] = PACKET_HEADER;
            this.packetIndex = DATA_OFFSET;
        }
        
        /**
         * Write a single byte to packet buffer. When 3 bytes have been
         * written, a packet will be sent.
         * Reception of data is not guarenteed.
         *
         * @param byte to write.
         */
        public synchronized void write(int buffer) throws IOException {
            this.packetBuffer[this.packetIndex++] = (byte)buffer;
            this.packetBuffer[this.packetIndex++] = (byte)~buffer;
            
            if (this.packetIndex >= CHECKSUM_OFFSET) {
                try {
                    sem.acquire();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                addChecksum(this.packetBuffer);
                tower.write(this.packetBuffer, PACKET_SIZE);
                this.packetIndex = DATA_OFFSET;
                sem.release();
            }
        }
        
        /**
         * Clear buffer
         */
        public synchronized void clear() {
            this.packetIndex = 0;
        }
    }
    
    /**
     * Get InputStream
     */
    public ClearableInputStream getInputStream() {
        return new TowerSocket.TowerInputStream();
    }
    
    /**
     * Get OutputStream
     */
    public ClearableOutputStream getOutputStream() {
        return new TowerSocket.TowerOutputStream();
    }
}