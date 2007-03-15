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
    
    private int sender;
    private int receiver;
    private IRInputStream in;
    private IROutputStream out;
    
    public static final int INPUT_BUFFER_SIZE = 2;
    public static final int OUTPUT_BUFFER_SIZE = 2;
    
    /** Creates a new instance of IRSocket */
    public IRDatagramSocket(int sender, int receiver, InputStream in, OutputStream out) {
        this.sender = sender;
        this.receiver = receiver;
        this.in = new IRDatagramSocket.IRInputStream(in);
        this.out = new IRDatagramSocket.IROutputStream(out);
    }    
    
    private class IRInputStream extends InputStream {
        private InputStream in;
        private int receiveInfo;
        private byte[] buffer;
        private int bufferPosition;
        
        protected IRInputStream(InputStream in) {
            this.in = in;
            this.receiveInfo = IRDatagram.getAdressInfo(receiver, sender);
            this.buffer = new byte[INPUT_BUFFER_SIZE];
            this.bufferPosition = INPUT_BUFFER_SIZE - 1;
        }
        
        //needs buffering
        public int read() throws IOException {            
            this.bufferPosition++;
            
            if (this.bufferPosition == INPUT_BUFFER_SIZE) {                
                int addressInfo = -1;
                int bytesRead = 0;                
                this.bufferPosition = 0;
                                
                do {
                    addressInfo = this.in.read();
                    if (addressInfo == this.receiveInfo) {
                        bytesRead = 0;
                        while (bytesRead < INPUT_BUFFER_SIZE) {;
                            bytesRead += this.in.read(this.buffer, bytesRead, INPUT_BUFFER_SIZE - bytesRead);
                        }
                    }
                } while (bytesRead == INPUT_BUFFER_SIZE);
            } 
            
            return this.buffer[this.bufferPosition];
        }               
    }
    
    private class IROutputStream extends OutputStream {        
        private OutputStream out;
        private int sendInfo;
        private byte[] buffer;
        private int bufferPosition;
        
        protected IROutputStream(OutputStream out) {
            this.out = out;
            this.sendInfo = IRDatagram.getAdressInfo(sender, receiver);
            this.buffer = new byte[OUTPUT_BUFFER_SIZE];
            this.bufferPosition = -1;
        }
        
        public void write(int b) throws IOException {
            this.bufferPosition++;
            
            if (this.bufferPosition == OUTPUT_BUFFER_SIZE) {                                       
                this.bufferPosition = 0;                
            }
            
            this.buffer[this.bufferPosition] = (byte)b;
            
            if (this.bufferPosition == OUTPUT_BUFFER_SIZE - 1) {
                this.out.write(this.sendInfo);
                this.out.write(this.buffer);
            }
        }        
    }
    
    public IROutputStream getOutputStream() {
        return this.out;
    }
    
    public IRInputStream getInputStream() {
        return this.in;
    }
}
