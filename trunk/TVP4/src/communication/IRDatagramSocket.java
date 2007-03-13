/*
 * IRSocket.java
 *
 * Created on 13. marts 2007, 09:46
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
 * __________ Changes ____________
 *
 */

package communication;

import java.io.*;
/**
 *
 * @author LMK
 */
public class IRDatagramSocket {
    
    /** Creates a new instance of IRSocket */
    public IRDatagramSocket() {
    }    
    
    private class IRInputStream extends InputStream {
        public int read() throws IOException {
            return 0;
        }
        
        public int available() throws IOException {
            return 0;
        }

        public void close() throws IOException {}

        public synchronized void mark(int readlimit) {}

        public synchronized void reset() throws IOException {
            throw new IOException("mark/reset not supported");
        }
        
        public boolean markSupported() {
            return false;
        }
    }
    
    private class IROutputStream extends OutputStream {
        public void write(int b) throws IOException {
        }
        
        public void flush() throws IOException {
        }
        
        public void close() throws IOException {
        }
    }
}
