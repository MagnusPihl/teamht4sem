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
        
    private RCXOutputStream out;
    private RCXInputStream in;
    private Tower tower;
        
    /** Creates a new instance of RCXSocket */
    public TowerSocket() {
        this.tower = new Tower();
        this.tower.open();
        this.in = new TowerSocket.TowerInputStream();
        this.out = new TowerSocket.TowerOutputStream();
    }            
    
    private class TowerInputStream extends InputStream {
        /*private int readPointer;
        private int inPointer;
        private byte[] buffer;*/
        
        protected TowerInputStream() {       
            //this.buffer = new byte[INPUT_BUFFER_SIZE];
        }
                
        public int read() throws IOException {              
            byte[] b = new byte[1];
            
            if (tower.read(b) == 0) {
                return -1;
            } else {
                return b[0];
            }
        }
        
        public int read(byte[] buffer) throws IOException {
            return tower.read(buffer);
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

            byte[] intermediate = new byte[length];
            
            int available = tower.read(intermediate);

            for (int i = 0; i < available; i++) {
                buffer[i] = intermediate[i + offset];
            }
            
            return available;
        }        
        
        public void close() throws IOException {
            tower.close();
        }
    }
    
    private class TowerOutputStream extends OutputStream {
        public void write(int buffer) throws IOException {
            tower.write(new byte[] {(byte)buffer}, 1);
        }
        
        public void write(byte[] buffer) throws IOException {
            tower.write(buffer, buffer.length);
        }
        
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
    
    public TowerOutputStream getOutputStream() {
        return this.out;
    }
    
    public TowerInputStream getInputStream() {
        return this.in;
    }
}