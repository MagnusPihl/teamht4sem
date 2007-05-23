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
    
    private NetworkInputStream in;
    private NetworkOutputStream out;
    
    public static final byte INPUT_BUFFER_SIZE = 2;
    public static final byte OUTPUT_BUFFER_SIZE = 2;
    
    /** 
     * Creates a new instance of IRDatagramSocket 
     */
    public NetworkSocket(byte sender, byte receiver, InputStream in, OutputStream out) {        
        this.in = new NetworkSocket.NetworkInputStream(in, sender, receiver);
        this.out = new NetworkSocket.NetworkOutputStream(out, sender, receiver);
    }    
    
    /**
     * Input stream that reads address header in each package and only
     * accepts relevant pacakges.
     */
    public class NetworkInputStream extends InputStream {
        private InputStream in;
        private byte expectedHeader;
        private byte receivedHeader;
        private byte[] buffer;
        private byte readIndex;
        private byte bufferIndex;
        
        private boolean packetAccepted;
        private int data;
        
        /**
         * Create address filtered input stream reading packages from supplied
         * input stream
         *
         * @param stream to read from.
         */
        protected NetworkInputStream(InputStream in, byte sender, byte receiver) {
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
                        
            //System.out.print("Reading network packet:");
            do {
                this.data = this.in.read();
                if (this.data != -1) {                    
                    if (this.bufferIndex == -1) {
                        //System.out.println(Integer.toBinaryString(this.data) + " ?= " + Integer.toBinaryString(this.expectedHeader));
                        if ((byte)this.data == this.expectedHeader) {
                            this.packetAccepted = true;
                        }
                    } else {
                            //System.out.println("Network: Data = "+this.data);
                        this.buffer[this.bufferIndex] = (byte)this.data;
                    }
                    
                    this.bufferIndex++;
                    if (this.bufferIndex == INPUT_BUFFER_SIZE) {                        
                        if (this.packetAccepted) {
                            this.bufferIndex = 0;
                            //System.out.println("le ja");
                            return true;
                        }                                                
                        this.bufferIndex = -1;
                    }
                } else if (this.bufferIndex == -1) {                    
                    this.bufferIndex = 0;
                    //System.out.println("le nej");
                    return false;
                }                                    
            } while (this.bufferIndex < INPUT_BUFFER_SIZE);                             
            
            //System.out.println("le nej");
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
            
            return this.data & 0xFF;
        }                     
    }
    
    /**
     * Output stream that adds address header to outbound packages.
     */
    public class NetworkOutputStream extends OutputStream {        
        private OutputStream out;
        private byte sendHeader;
        private byte[] buffer;
        private byte bufferPosition;
        
        /**
         * Create new output stream that adds address headers to data
         *
         * @param output stream to write datagrams to
         */
        protected NetworkOutputStream(OutputStream out, byte sender, byte receiver) {
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
    }
    
    /**
     * Get output stream
     *
     * @return OutputStream
     */
    public OutputStream getOutputStream() {
        return this.out;
    }
    
    /**
     * Get output stream
     *
     * @return NetworkInputStream
     */
    public InputStream getInputStream() {
        return this.in;
    }    
}
