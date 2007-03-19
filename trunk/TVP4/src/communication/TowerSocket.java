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
    
    public static final int INPUT_BUFFER_SIZE = 400;
        
    /** 
     * Creates a new instance of TowerSocket that can communicate with
     * the RCX kit over IR.
     */
    public TowerSocket() {
        this.tower = new Tower();
        this.tower.open();
        this.out = new TowerSocket.TowerOutputStream();
        this.bufferIndex = -1;
        this.readBuffer = new byte[INPUT_BUFFER_SIZE];
    }      
    
    private int fillBuffer(int bytes) {                  
        byte[] intermediate = new byte[bytes];
            
        int available = this.tower.read(intermediate);

        for (int i = 0; i < available; i++) { 
            this.bufferIndex++;
            if (this.bufferIndex == INPUT_BUFFER_SIZE) {
                this.bufferIndex = 0;
            } 
            
            this.readBuffer[this.bufferIndex] = intermediate[i];
        }

        return available;                    
    }
    
    /**
     * Reads IR data directly from source.
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
                fillBuffer(1);
            }                 
            
            return readBuffer[this.readIndex];
        }  
    }
    
    public class TowerOutputStream extends OutputStream {
        
        /**
         * Write a single byte to IR Tower. 
         * Receiption of data is not guarenteed.
         *
         * @param byte to write.
         */
        public void write(int buffer) throws IOException {
            //tower.write(new byte[] {(byte)buffer}, 1)
            System.out.println(tower.strerror(tower.write(new byte[] {(byte)buffer}, 1)));
        }
        
        /**
         * Write bytes in buffer to IR Tower.
         * Receiption of data is not guarenteed.
         *
         * @param buffer containing data to write.
         */
        public void write(byte[] buffer) throws IOException {
            //tower.write(buffer, buffer.length)
            System.out.println(tower.strerror(tower.write(buffer, buffer.length)));
        }
        
        /**
         * Write length bytes from offset in buffer to IR Tower.
         * Receiption of data is not guarenteed.
         *
         * @param buffer containing data to write.
         * @param offset from zero.
         * @param amount bytes from offset to write.
         */
        public void write(byte[] buffer, int offset, int length) throws IOException {
            byte[] intermediate = new byte[length];
            
            for (int i = 0; i < length; i++) {
                intermediate[i] = buffer[i + offset];
            }
            
            //tower.write(intermediate, length);
            System.out.println(tower.strerror(tower.write(intermediate, length)));
        }       
        
        public void close() throws IOException {
            tower.close();
        }               
    }    
    
    /**
     * Get output stream with which you can write to the 
     * IR Tower.
     *
     * @return TowerOutputStream
     */
    public TowerOutputStream getOutputStream() {
        return this.out;
    }
    
    /**
     * Get input stream to read from IR Tower with
     * 
     * @return TowerInputStream
     */
    public TowerInputStream getInputStream() {
        return new TowerSocket.TowerInputStream();
    }
}