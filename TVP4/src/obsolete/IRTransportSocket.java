/*
 * IRTransportSocket.java
 *
 * Created on 15. marts 2007, 09:32
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 15. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package obsolete;

import java.io.*;
import josx.util.*;
/**
 *
 * @author LMK
 */
public class IRTransportSocket {
        
    private int sender;
    private int receiver;
    private IRTransportInputStream in;
    private IRTransportOutputStream out;
    
    public static final int DATA = 0x0000;
    public static final int ACKNOWLEDGE = 0xFFFF;
    public static final int ACKNOWLEDGE_TIMEOUT = 250;
    
    /** Creates a new instance of IRSocket */
    public IRTransportSocket(InputStream in, OutputStream out) {
        this.in = new IRTransportSocket.IRTransportInputStream(in, out);
        this.out = new IRTransportSocket.IRTransportOutputStream(in, out);
    }    
    
    public class IRTransportInputStream extends InputStream {
        private InputStream in;
        private OutputStream out;
        private int addressInfo;
        private int type;            
        private int data;
        private int lastData;
        
        protected IRTransportInputStream(InputStream in, OutputStream out) {
            this.in = in;
            this.out = out;
        }
        
        public int read() throws IOException {            
            do { 
                this.type = this.in.read();            
                this.data = this.in.read();
                
                if ((this.type == DATA) && (this.data == this.lastData)) {
                    this.acknowledge();
                } else if (this.type == DATA) {
                    break;
                }
                
            } while (true);
                        
            this.acknowledge();
            return this.data;            
        }                
        
        private void acknowledge() throws IOException {
            this.out.write(ACKNOWLEDGE);
            this.out.write(0);
        }        
    }
    
    public class IRTransportOutputStream extends OutputStream implements TimerListener {        
        
        private OutputStream out;
        private InputStream in;
        private boolean dataSent = false;
        
        
        protected IRTransportOutputStream(InputStream in, OutputStream out) {
            this.out = out;
            this.in = in;
        }
        
        public void write(int b) throws IOException {            
            boolean dataSent = false;
                        
            while (true) {
                this.out.write(b);
                if (this.in.read() == ACKNOWLEDGE) {
                    break;
                } else {
                    this.out.write(b);
                }
                
                this.in.read();
            }        
            this.in.read();
        }        
        
        public void timedOut() {
            
        }
    }
    
    public IRTransportOutputStream getOutputStream() {
        return this.out;
    }
    
    public IRTransportInputStream getInputStream() {
        return this.in;
    }
}