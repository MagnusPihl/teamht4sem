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
 * Current version only supports package of size 2.
 *
 */

package communication;

import java.io.*;

public class IRDatagramSocket {
    
    private int sender;
    private int receiver;
    private IRInputStream in;
    private IROutputStream out;
    
    public static final int INPUT_BUFFER_SIZE = 2;
    public static final int OUTPUT_BUFFER_SIZE = 2;
    
    /** 
     * Creates a new instance of IRDatagramSocket 
     */
    public IRDatagramSocket(int sender, int receiver, InputStream in, OutputStream out) {
        this.sender = sender;
        this.receiver = receiver;
        this.in = new IRDatagramSocket.IRInputStream(in);
        this.out = new IRDatagramSocket.IROutputStream(out);
    }    
    
    /**
     * Input stream that reads address header in each package and only
     * accepts relevant pacakges.
     */
    public class IRInputStream extends InputStream {
        private InputStream in;
        private int receiveHeader;
        private byte[] buffer;
        private int bufferPosition;
        
        /**
         * Create address filtered input stream reading packages from supplied
         * input stream
         *
         * @param stream to read from.
         */
        protected IRInputStream(InputStream in) {
            this.in = in;
            this.receiveHeader = IRDatagram.getAdressHeader(receiver, sender);
            this.buffer = new byte[INPUT_BUFFER_SIZE];
            this.bufferPosition = INPUT_BUFFER_SIZE - 1;
        }
        
        /**
         * Read byte from input stream. Bytes are filtered so that only packages
         * that has matching addressHeader to what you're trying to catch
         * will be read.
         */
        public int read() throws IOException {            
            this.bufferPosition++;
            
            if (this.bufferPosition == INPUT_BUFFER_SIZE) {                
                int addressHeader = -1;
                int bytesRead = 0;                
                this.bufferPosition = 0;
                                
                do {
                    addressHeader = this.in.read();
                    if (addressHeader == this.receiveHeader) {
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
    
    /**
     * Output stream that adds address header to outbound packages.
     */
    public class IROutputStream extends OutputStream {        
        private OutputStream out;
        private int sendHeader;
        private byte[] buffer;
        private int bufferPosition;
        
        /**
         * Create new output stream that adds address headers to data
         *
         * @param output stream to write datagrams to
         */
        protected IROutputStream(OutputStream out) {
            this.out = out;
            this.sendHeader = IRDatagram.getAdressHeader(sender, receiver);
            this.buffer = new byte[OUTPUT_BUFFER_SIZE];
            this.bufferPosition = -1;
        }
        
        /**
         * Write byte to output stream. For every 2 bytes written
         * a package is sent.
         *
         * @param byte to write to output stream
         */
        public void write(int b) throws IOException {
            this.bufferPosition++;
            
            if (this.bufferPosition == OUTPUT_BUFFER_SIZE) {                                       
                this.bufferPosition = 0;                
            }
            
            this.buffer[this.bufferPosition] = (byte)b;
            
            if (this.bufferPosition == OUTPUT_BUFFER_SIZE - 1) {
                this.out.write(this.sendHeader);
                this.out.write(this.buffer);
            }
        }                        
    }
    
    /**
     * Get output stream
     *
     * @return IROutputStream
     */
    public IROutputStream getOutputStream() {
        return this.out;
    }
    
    /**
     * Get output stream
     *
     * @return IRInputStream
     */
    public IRInputStream getInputStream() {
        return this.in;
    }
}
