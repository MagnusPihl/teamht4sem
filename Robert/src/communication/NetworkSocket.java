/*
 * NetworkSocket.java
 *
 * Created on 13. marts 2007, 09:46
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 13. marts 2007 (v 1.1)
 * Changed name of class to from IRDatagramSocket to NetworkSocket 
 * to reflect layer and responsibility.
 * Timeout when reading has been added. Timeout only occurs once 3 bytes have
 * been read.
 *
 * LMK @ 13. marts 2007 (v 1.0)
 * Current version only supports package of size 2.
 *
 */

package communication;

import java.io.*;

public class NetworkSocket {
    
    private int sender;
    private int receiver;
    private NetworkInputStream in;
    private NetworkOutputStream out;
    
    public static final int INPUT_BUFFER_SIZE = 2;
    public static final int OUTPUT_BUFFER_SIZE = 2;
    public static final int TIMEOUT = 60;
    
    /** 
     * Creates a new instance of IRDatagramSocket 
     */
    public NetworkSocket(int sender, int receiver, InputStream in, OutputStream out) {
        this.sender = sender;
        this.receiver = receiver;
        this.in = new NetworkSocket.NetworkInputStream(in);
        this.out = new NetworkSocket.NetworkOutputStream(out);
    }    
    
    /**
     * Input stream that reads address header in each package and only
     * accepts relevant pacakges.
     */
    public class NetworkInputStream extends InputStream {
        private InputStream in;
        private int exspectedHeader;
        private int receivedHeader;
        private byte[] buffer;
        private int readIndex;
        private int bufferIndex;
        
        private int timeoutCount;
        private int timeout;
        private boolean packetAccepted;
        private int data;
        
        /**
         * Create address filtered input stream reading packages from supplied
         * input stream
         *
         * @param stream to read from.
         */
        protected NetworkInputStream(InputStream in) {
            this.in = in;
            this.exspectedHeader = NetworkDatagram.getAdressHeader(receiver, sender);
            this.buffer = new byte[INPUT_BUFFER_SIZE];
            this.bufferIndex = 0;
            this.readIndex = 0;
            this.timeoutCount = 0;
        }
        
        private boolean readPacket() throws IOException {
            this.timeout = (int)System.currentTimeMillis() + TIMEOUT; 
            this.bufferIndex = -1;
            this.packetAccepted = false;
            
            do {
                this.data = this.in.read();
                if (this.data != -1) {
                    if (this.bufferIndex == -1) {
                        if (this.data == this.exspectedHeader) {
                            this.packetAccepted = true;
                        }
                    } else {
                        this.buffer[this.bufferIndex] = (byte)this.data;
                    }
                    
                    this.bufferIndex++;
                    if (this.bufferIndex == INPUT_BUFFER_SIZE) {                        
                        if (this.packetAccepted) {
                            this.bufferIndex = 0;
                            return true;
                        }                                             
                        this.timeout = (int)System.currentTimeMillis() + TIMEOUT; 
                        this.bufferIndex = -1;
                    }
                } else if ((this.bufferIndex == -1)&&(this.timeout < (int)System.currentTimeMillis())) {
                    this.timeoutCount++;
                    this.bufferIndex = 0;
                    return false;
                }                                    
            } while (this.bufferIndex < INPUT_BUFFER_SIZE);                             
            
            this.bufferIndex = 0;
            return false;
        }
        
        /**
         * Read byte from input stream. Bytes are filtered so that only packages
         * that has matching addressHeader to what you're trying to catch
         * will be read.
         */
        public int read() throws IOException {          
            if ((this.bufferIndex == this.readIndex)) {
                if (!readPacket()) {
                    return -1;
                }
            }
            
            this.data = this.buffer[this.readIndex];
            
            this.readIndex++;
            if (this.readIndex == INPUT_BUFFER_SIZE) {
                this.readIndex = 0;
            }
            
            return data;
        }               
    }
    
    /**
     * Output stream that adds address header to outbound packages.
     */
    public class NetworkOutputStream extends OutputStream {        
        private OutputStream out;
        private int sendHeader;
        private byte[] buffer;
        private int bufferPosition;
        
        /**
         * Create new output stream that adds address headers to data
         *
         * @param output stream to write datagrams to
         */
        protected NetworkOutputStream(OutputStream out) {
            this.out = out;
            this.sendHeader = NetworkDatagram.getAdressHeader(sender, receiver);
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
     * @return NetworkOutputStream
     */
    public NetworkOutputStream getOutputStream() {
        return this.out;
    }
    
    /**
     * Get output stream
     *
     * @return NetworkInputStream
     */
    public NetworkInputStream getInputStream() {
        return this.in;
    }
}
