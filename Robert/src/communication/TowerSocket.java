/*  
 * TowerSocket.java
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
 * LMK @ 14. marts 2007 (v 1.0) 
 * 
 */

package communication;

import josx.rcxcomm.*;
import java.io.*;

public class TowerSocket {
        
    private TowerOutputStream out;
    private TowerInputStream in;
    private Tower tower;
    
    private int bufferIndex;
    private byte[] readBuffer;
    private int packetIndex;
    private byte[] packetBuffer;
    
    public static final int INPUT_BUFFER_SIZE = 400;    
    public static final byte PACKET_HEADER = 0x55;
    public static final int DATA_OFFSET = 2;
    public static final int CHECKSUM_OFFSET = 8;
    public static final int PACKET_SIZE = 10;
        
    /** 
     * Creates a new instance of TowerSocket that can communicate with
     * the RCX kit over IR. Reception of data is not guaranteed. 
     * No addressing is done.
     */
    public TowerSocket() {
        this.tower = new Tower();
        this.tower.open("usb");
        this.out = new TowerSocket.TowerOutputStream();
        this.bufferIndex = -1;
        this.readBuffer = new byte[INPUT_BUFFER_SIZE];
        
        this.packetBuffer = new byte[PACKET_SIZE];
        this.packetIndex = DATA_OFFSET;
    }
        
    /**
     * Read packet to input stream buffer. 
     * @return true if packet received has a valid checksum and has been added
     * to buffer, or false if the packet was invalid and trashed.
     */
    private boolean readPacket() {
        byte[] data = new byte[1];
        //byte[] data = new byte[DATA_OFFSET];
        int available = 0;
        this.packetIndex = 0;
        
        do {
            available = this.tower.read(data);            
            
            if (available == 1) {
                if ((this.packetIndex < DATA_OFFSET)) {
                    //wait for start bytes.
                    if (data[0] == PACKET_HEADER) {
                        this.packetIndex++;
                    } else {
                        this.packetIndex = 0;
                    }
                } else {
                    this.packetBuffer[this.packetIndex++] = data[0];
                }
            }
        } while (this.packetIndex < PACKET_SIZE);
                
        //if checksum is valid add packet to stream.
        if (this.checksum()) {
            for (int i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
                this.bufferIndex++;
                if (this.bufferIndex == INPUT_BUFFER_SIZE) {
                    this.bufferIndex = 0;
                }
                this.readBuffer[this.bufferIndex++] = this.packetBuffer[i];
            }
            return true;
        } else {
            return false;
        }
    }
        
    /**
     * Check that the last packet read from IR is valid.     
     *
     * @return true if packet is valid.
     */
    private boolean checksum() {                        
        int checksum = 0;
        for (int i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
            checksum += this.packetBuffer[i];
        }

        if (checksum - this.packetBuffer[CHECKSUM_OFFSET] == 0) {
            return true;
        }        

        return false;                        
    }
    
    
    /**
     * InputStream that converts incoming packets into an inputstream.
     * Packets are always 3 bytes long. 
     */
    public class TowerInputStream extends InputStream {
        private int readIndex;
                
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
            this.readIndex++;
            if (this.readIndex == INPUT_BUFFER_SIZE) {
                this.readIndex = 0;
            }
            
            while ((bufferIndex == -1)||(bufferIndex == this.readIndex)) {
                readPacket();
            }                 
            
            return readBuffer[this.readIndex];
        }  
    }
    
    /**
     * Output stream that let's you write packets to IR.
     * Each packet can hold 3 bytes. A packet is sent every 3 bytes written.
     */
    public class TowerOutputStream extends OutputStream {
        
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
        public void write(int buffer) throws IOException {
            this.packetBuffer[this.packetIndex++] = (byte)buffer;
            this.packetBuffer[this.packetIndex++] = (byte)~buffer;
            
            if (this.packetIndex == CHECKSUM_OFFSET) {
                this.addChecksum();
                System.out.println(tower.strerror(tower.write(this.packetBuffer, PACKET_SIZE)));
                this.packetIndex = DATA_OFFSET;
            }            
        }
        
        /**
         * Calculate and add checksum to current packet.
         */
        private void addChecksum() {            
            this.packetBuffer[CHECKSUM_OFFSET] = 0;
            
            for (int i = DATA_OFFSET; i < CHECKSUM_OFFSET; i += 2) {
                this.packetBuffer[CHECKSUM_OFFSET] += this.packetBuffer[i];
            }
            this.packetBuffer[CHECKSUM_OFFSET + 1] = (byte)~this.packetBuffer[CHECKSUM_OFFSET];
        }                
    }    
    
    /**
     * Get output stream with which you can write to the IR Tower.
     *
     * @return TowerOutputStream
     */
    public TowerOutputStream getOutputStream() {
        return this.out;
    }
    
    /**
     * Get input stream to read from IR Tower with.
     * 
     * @return TowerInputStream
     */
    public TowerInputStream getInputStream() {
        return new TowerSocket.TowerInputStream();
    }
}