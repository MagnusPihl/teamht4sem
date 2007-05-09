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
    
    /** 
     * Creates a new instance of IRDatagramSocket 
     */
    public NetworkSocket(int sender, int receiver, ClearableInputStream in, ClearableOutputStream out) {
        this.sender = sender;
        this.receiver = receiver;
        this.in = new NetworkSocket.NetworkInputStream(in);
        this.out = new NetworkSocket.NetworkOutputStream(out);
    }    
    
    /**
     * Input stream that reads address header in each package and only
     * accepts relevant pacakges.
     */
    public class NetworkInputStream extends ClearableInputStream {
        private ClearableInputStream in;
        private int expectedHeader;
        private int receivedHeader;
        private byte[] buffer;
        private int readIndex;
        private int bufferIndex;
        
        private boolean packetAccepted;
        private int data;
        
        /**
         * Create address filtered input stream reading packages from supplied
         * input stream
         *
         * @param stream to read from.
         */
        protected NetworkInputStream(ClearableInputStream in) {
            this.in = in;
            this.expectedHeader = NetworkDatagram.getAdressHeader(receiver, sender);
            this.buffer = new byte[INPUT_BUFFER_SIZE];
            this.bufferIndex = 0;
            this.readIndex = 0;
        }
        
        /**
         * Read packet from IR Stream and save it to buffer.
         *
         * @return
         */
        private boolean readPacket() throws IOException {
            this.bufferIndex = -1;
            this.packetAccepted = false;
                        
            do {
                this.data = this.in.read();
                if (this.data != -1) {
//                    System.out.println("Network: Data = "+this.data);
                    if (this.bufferIndex == -1) {
//                        System.out.println(this.data + " ?= "+this.expectedHeader);
                        if (this.data == this.expectedHeader) {
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
                        this.bufferIndex = -1;
                    }
                } else if (this.bufferIndex == -1) {                    
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
            
            return data & 0xFF;
        }             
        
        /**
         * Clear buffer
         */
        public void clear() {            
            this.in.clear();
            this.bufferIndex = this.readIndex;
        }
    }
    
    /**
     * Output stream that adds address header to outbound packages.
     */
    public class NetworkOutputStream extends ClearableOutputStream {        
        private ClearableOutputStream out;
        private int sendHeader;
        private byte[] buffer;
        private int bufferPosition;
        
        /**
         * Create new output stream that adds address headers to data
         *
         * @param output stream to write datagrams to
         */
        protected NetworkOutputStream(ClearableOutputStream out) {
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
//                System.out.println("Network: Sending...");
                this.out.write(this.sendHeader);
//                System.out.println("Network: Header Sent");
                this.out.write(this.buffer);
//                System.out.println("Network: Data Sent");
            }
        }    
        
        /**
         * Clear buffer
         */
        public void clear() {
            this.out.clear();
            this.bufferPosition = -1;
        }
    }
    
    /**
     * Get output stream
     *
     * @return OutputStream
     */
    public ClearableOutputStream getOutputStream() {
        return this.out;
    }
    
    /**
     * Get output stream
     *
     * @return NetworkInputStream
     */
    public ClearableInputStream getInputStream() {
        return this.in;
    }
    
    /**
     * Clear data in all buffers
     */
    public void clear() {
        this.in.clear();
        this.out.clear();
    }
}
