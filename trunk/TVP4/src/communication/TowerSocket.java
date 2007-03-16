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
        
    /** 
     * Creates a new instance of TowerSocket that can communicate with
     * the RCX kit over IR.
     */
    public TowerSocket() {
        this.tower = new Tower();
        this.tower.open();
        this.in = new TowerSocket.TowerInputStream();
        this.out = new TowerSocket.TowerOutputStream();
    }            
    
    /**
     * Reads IR data directly from source.
     */
    private class TowerInputStream extends InputStream {
        /*private int readPointer;
        private int inPointer;
        private byte[] buffer;*/
        
        protected TowerInputStream() {       
            //this.buffer = new byte[INPUT_BUFFER_SIZE];
        }
                
        /**
         * Read one byte from IR stream. The data received is not guarenteed
         * to be meant for you, no addressing is done.
         *
         * @return -1 if no bytes could be read.
         */
        public int read() throws IOException {              
            byte[] b = new byte[1];
            
            if (tower.read(b) == 0) {
                return -1;
            } else {
                return b[0];
            }
        }
        
        /**
         * Fill buffer with data from IR stream. The data received is not guarenteed
         * to be meant for you, no addressing is done.
         *
         * @param buffer to read data to.
         * @return number of bytes saved in buffer.
         */
        public int read(byte[] buffer) throws IOException {
            return tower.read(buffer);
        }
        
        /**
         * Fill buffer with data starting from offset, filling at most length bytes.
         * The data received is not guarenteed to be meant for you, 
         * no addressing is done.
         *
         * @param buffer to read data to.
         * @param offset from zero.
         * @param amount bytes from offset to read.
         * @return number of bytes read.
         */
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

            byte[] intermediate = new byte[length];
            
            int available = tower.read(intermediate);

            for (int i = 0; i < available; i++) {
                buffer[i] = intermediate[i + offset];
            }
            
            return available;
        }                
    }
    
    private class TowerOutputStream extends OutputStream {
        
        /**
         * Write a single byte to IR Tower. 
         * Receiption of data is not guarenteed.
         *
         * @param byte to write.
         */
        public void write(int buffer) throws IOException {
            tower.write(new byte[] {(byte)buffer}, 1);
        }
        
        /**
         * Write bytes in buffer to IR Tower.
         * Receiption of data is not guarenteed.
         *
         * @param buffer containing data to write.
         */
        public void write(byte[] buffer) throws IOException {
            tower.write(buffer, buffer.length);
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
            
            tower.write(intermediate, length);
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
        return this.in;
    }
}