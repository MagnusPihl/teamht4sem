/*
 * TransportSocket.java
 *
 * Created on 24. marts 2007, 12:06
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.5
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 15. maj 2007 (v 1.6)
 * Added random write delay
 * 
 * Magnus Hemmer Pihl @ 27. april 2007 (v 1.5)
 * Removed timeout from read-operation. Will now fail immediately if no data is available.
 *
 * Magnus Hemmer Pihl @ 25. april 2007 (v 1.4)
 * Corrected read-method to always read two bytes - no matter what the header contains.
 * Corrected type-cast to of System.currentMilliseconds to int in write-method, for Mindstorm compatibility.
 * read-method no longer attempts to use constants from RobotProxy. Constant -0x01 for NOP is used, instead.
 * Corrected less-than symbol from greater-than in write-method.
 *
 * Magnus Hemmer Pihl @ 23. april 2007 (v 1.3)
 * General code cleanup. Added comments and organized code better.
 *
 * Magnus Hemmer Pihl @ 17. april 2007 (v 1.2)
 * Changed write method to use the same sequence number on retries.
 *
 * Magnus Hemmer Pihl @ 13. april 2007 (v 1.1)
 * Changed write()-method to use static methods rather than creating new objects.
 * write()-method now throws an IOException on failure.
 * write()-method will no longer indefinitely repeat sending on timeouts.
 *
 * Magnus Hemmer Pihl @ 24. marts 2007 (v 1.0)
 * Initial.
 *
 */

package communication;

import java.io.*;
import josx.platform.rcx.LCD;
import josx.util.*;
import java.util.Random;

public class TransportSocket {
    private TransportInputStream in;
    private TransportOutputStream out;
    private int lastAcknowledge;
    private TransportInputThread inputThread;
    private int bufferIndex;
    private byte[] readBuffer;
    private static Random rand = new Random();
    
    public static final int DATA = 0;
    public static final int RECEIPT = 1;
    
    private static final byte NOP = 0x00;
    
    public static final int INPUT_BUFFER_SIZE = 20;
    //Time to wait for acknowledge before retrying to write data. Should always be lower than WRITE_TIMEOUT.
    public static final int ACKNOWLEDGE_TIMEOUT = 150;
    
    /** Creates a new instance of TransportSocket */
    public TransportSocket(ClearableInputStream in, ClearableOutputStream out) {
        this.readBuffer = new byte[INPUT_BUFFER_SIZE];
        this.in = new TransportSocket.TransportInputStream();
        this.out = new TransportSocket.TransportOutputStream(out);
        
        this.inputThread = new TransportInputThread(in, out);
        this.inputThread.start();
    }
    
    public class TransportInputThread extends Thread {
        private int data;
        private int lastSequence;
        private int header;
        protected boolean isActive;
        
        private ClearableInputStream in;
        private ClearableOutputStream out;
        
        /**
         * Create TransportInputThread class used to read indefinetly from
         * lower layers.
         *
         * @param InputStream to read data from.
         * @param OuputStream to write acknowledges to.
         */
        protected TransportInputThread(ClearableInputStream in, ClearableOutputStream out) {
            this.in = in;
            this.out = out;
            bufferIndex = 0;
            this.lastSequence = -1;
            this.isActive = false;
        }
        
        /**
         * Set read thread as active or inactive. When inactive the
         * input thread will not try to read. When setting active a delay may
         * occur before reading resumes.
         *
         * @param boolean, if true the thread will read indefinetly to buffer.
         */
        public void setActive(boolean isActive) {
            this.isActive = isActive;
        }
        
        /**
         * Method for execution in seperate thread.
         * Reads data from lower layers as long as isActive is true.
         */
        public void run() {
            while (true) {
                //                System.out.println("Reading...");
                if (this.isActive) {
                    try {
                        //int timestamp = (int)System.currentTimeMillis();
                        
                        //Read header byte. Timeout if neccessary.
                        this.header = this.in.read();
                        
                        if (this.header != -1) {
                            //Check indefinitely for second byte.
                            do {
                                this.data = this.in.read();
                            } while (this.data == -1);
                            //(System.out.println(this.data);
                        } else {
                            Thread.yield();
                            continue;
                        }
                        
                        //                        System.out.println("Transport: READING:  "+TransportPackage.getType(header)+", "+TransportPackage.getSequenceNumber(header)+", "+data);
                        
                        //Only continue if byte received is Data header.
                        if (TransportPackage.getType(header) == DATA) {
                            //Send receipt.
                            this.out.write(TransportPackage.createAcknowledgeHeader(this.header));
                            this.out.write(NOP);
                            
                            //Return data only if not a repeat of the last sequence
                            if(TransportPackage.getSequenceNumber(this.header) != this.lastSequence) {
                                this.lastSequence = TransportPackage.getSequenceNumber(this.header);
                                readBuffer[bufferIndex++] = (byte)this.data;
                                
                                if (bufferIndex == INPUT_BUFFER_SIZE) {
                                    bufferIndex = 0;
                                }
                            }
                        } else if (TransportPackage.getType(header) == RECEIPT) {
                            lastAcknowledge = TransportPackage.getSequenceNumber(this.header);
                        }
                        Thread.yield();
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(200);
                    } catch (Exception e) {}
                }
            }
        }
        
        public void clear() {
            this.in.clear();
            this.out.clear();
        }
    }
    
    public class TransportInputStream extends ClearableInputStream {
        
        private int readIndex;
        private int data;
        
        /**
         * Create a new TransportInputStream
         */
        protected TransportInputStream() {
            this.readIndex = bufferIndex;
        }
        
        /**
         * Nonblocking read that reads from buffer filled by TransportInputThread.
         * When no data is available -1 is returned.
         *
         * @return a byte of data if one is available otherwise -1 is returned.
         */
        public int read() throws IOException {
            if (this.readIndex == bufferIndex) {
                return -1;
            } else {
                this.data = readBuffer[this.readIndex++];
                
                if (this.readIndex == INPUT_BUFFER_SIZE) {
                    this.readIndex = 0;
                }
                
                return this.data & 0xFF;
            }
        }
        
        /**
         * Clear buffer
         */
        public void clear() {
            this.readIndex = bufferIndex;
        }
    }
    
    public class TransportOutputStream extends ClearableOutputStream {
        private ClearableOutputStream out;
        private int sequence;
        private int timeout;
        
        /**
         * Create new output stream.
         *
         * @param OutputStream to write to.
         */
        protected TransportOutputStream(ClearableOutputStream out) {
            this.out = out;
            this.sequence = (byte)((int)System.currentTimeMillis()) & 0x7F;
            
        }
        
        /**
         * Blocking write. Writes the 8 lsb bits of the supplied int to underlying layers.
         *
         * @param byte to write to underlying layers.
         */
        public void write(int b) throws IOException {
            System.out.println("Sending: " + Integer.toBinaryString(b));
            this.sequence++;    
            if (this.sequence == 127) {
                this.sequence = 0;
            }
            //Write header and data bytes.
            
            try {
                this.out.write(sequence);
                this.out.write(b);                
                this.timeout = (int)System.currentTimeMillis() + TransportSocket.ACKNOWLEDGE_TIMEOUT;
                
                //Try to read acknowledge header. Timeout if needed.
                while(lastAcknowledge != sequence) {
                    if(this.timeout < (int)System.currentTimeMillis()) {
                        this.out.write(sequence);
                        this.out.write(b);
                        this.timeout = (int)System.currentTimeMillis() + TransportSocket.ACKNOWLEDGE_TIMEOUT;
                    }
                    
                    Thread.sleep(rand.nextInt()&0x7F);
                    //Thread.yield();
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        
        /**
         * Clear buffer
         */
        public void clear() {
            this.out.clear();
        }
    }
    
    /**
     * Set read thread as active or inactive. When inactive the
     * input thread will not try to read. When setting active a delay may
     * occur before reading resumes.
     *
     * @param boolean, if true the thread will read indefinetly to buffer.
     */
    public void setActive(boolean isActive) {
        this.inputThread.isActive = isActive;
    }
    
    /**
     * Clear buffers.
     */
    public void clear() {
        this.inputThread.clear();
        this.in.clear();
    }
    
    /**
     * Get reliable output stream.
     */
    public ClearableOutputStream getOutputStream() {
        return this.out;
    }
        
    /**
     * Get input stream.
     */
    public ClearableInputStream getInputStream() {
        return this.in;
    }
}