/*
 * TransportSocket.java
 *
 * Created on 24. marts 2007, 12:06
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.6
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 30. april 2007 (1.6)
 * Added non blocking read and write, via new reading thread. 
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
//import josx.platform.rcx.*;
//import josx.util.*;

public class TransportSocket
{
    private TransportInputStream in;
    private TransportOutputStream out;
    private TransportCommunicationThread com;
    private int readBufferIndex; 
    private int writeBufferIndex; 
    private byte[] readBuffer;
    private byte[] writeBuffer;    
    
    public static final int DATA = 0;
    public static final int RECEIPT = 1;    
    private static final byte NOP = 0x00;
    
    public static final int WRITE_PAUSE = 10;
    public static final int BUFFER_SIZE = 20;    
    //Time to wait for acknowledge before retrying to write data. Should always be lower than WRITE_TIMEOUT.
    public static final int ACKNOWLEDGE_TIMEOUT = 1000;     
    
    /** Creates a new instance of IRSocket */
    public TransportSocket(InputStream in, OutputStream out) {        
        this.readBuffer = new byte[BUFFER_SIZE];   
        this.writeBuffer = new byte[BUFFER_SIZE];
        this.writeBufferIndex = 0;
        this.readBufferIndex = 0;
        this.in = new TransportSocket.TransportInputStream();
        this.out = new TransportSocket.TransportOutputStream();                       
        
        this.com = new TransportCommunicationThread(in, out);        
        this.com.start();        
    }
        
    public void setActive(boolean isActive) {
        this.com.isActive = isActive;
    }
    
    public class TransportCommunicationThread extends Thread {               
        private int header;    
        private int data;     
        private int lastPackageReceived;
        private int lastPackageSent, sentIndex;
        private boolean readyToSend;
        protected boolean isActive;
        private int timeout;
        private int nextWriteTime;
     
        private InputStream in;
        private OutputStream out;   
            
        protected TransportCommunicationThread(InputStream in, OutputStream out) {        
            this.in = in;
            this.out = out;     
            this.lastPackageReceived = -1;
            this.lastPackageSent = -1;
            this.sentIndex = 0;
            this.readyToSend = true;
            this.isActive = false;
            this.nextWriteTime = 0;
        }
        
        public void setActive(boolean isActive) {
            this.isActive = isActive;
        }
        
        public void run() {
            while (true) {
                if (isActive) {
                    try {                                 
                        //Read header byte. 
                        this.header = this.in.read();
//                        LCD.showNumber(this.header);
                        if (this.header != -1) {
//                            LCD.showNumber(this.header);
                            //Check indefinitely for second byte.
                            System.out.println(Integer.toBinaryString(this.header));
                            do {
                                this.data = this.in.read();
                            } while (this.data == -1);                            
                            System.out.println(Integer.toBinaryString(this.data));
                            
                            //Only continue if byte received is Data header.
                            if (TransportPackage.getType(this.header) == DATA) {
                                //Send receipt.
//                                LCD.showNumber(this.data);
//                                System.out.println("le data");
                                this.out.write(TransportPackage.createAcknowledgeHeader(this.header));
                                this.out.write(NOP);

                                //Return data only if not a repeat of the last sequence
                                if(TransportPackage.getSequenceNumber(this.header) != this.lastPackageReceived) {
                                    this.lastPackageReceived = TransportPackage.getSequenceNumber(this.header);                                                
                                    readBuffer[readBufferIndex++] = (byte)this.data;

                                    if (readBufferIndex == BUFFER_SIZE) {
                                        readBufferIndex = 0;
                                    }                        
                                }
                            } else if (TransportPackage.getType(header) == RECEIPT) {
//                                Sound.beep();
//                                LCD.showNumber(-1);
//                                    System.out.println("le receipt");
//                                    System.out.println(Integer.toBinaryString(TransportPackage.getSequenceNumber(this.header)) + " - "+  Integer.toBinaryString(this.lastPackageSent));
                                if ((!this.readyToSend)&&(TransportPackage.getSequenceNumber(this.header) == this.lastPackageSent)) {
                                    this.sentIndex++;
                                    if (this.sentIndex == BUFFER_SIZE) {
                                        this.sentIndex = 0;
                                    }
                                this.readyToSend = true;
                                    this.nextWriteTime = (int)System.currentTimeMillis() + WRITE_PAUSE;
                                }
                            }
                        }
                        
                        //System.out.println("le start");
                        if ((this.readyToSend)&&(this.sentIndex != writeBufferIndex)&&(this.nextWriteTime < (int)System.currentTimeMillis())) {   
//                            System.out.println("Starter med at skrive");
                            this.readyToSend = false;
                            this.lastPackageSent = TransportPackage.getSequenceNumber((lastPackageSent + 1) % 127);
//                            System.out.println(System.currentTimeMillis());
                            this.out.write(this.lastPackageSent);
                            this.out.write(writeBuffer[this.sentIndex]);
//                            System.out.println(System.currentTimeMillis());
                            timeout = (int)System.currentTimeMillis() + ACKNOWLEDGE_TIMEOUT;
                            //System.out.println("Slut med at skrive");

                        } else if ((!this.readyToSend)&&(timeout < (int)System.currentTimeMillis())) {                        
                            //Sound.beep();
//                            System.out.println("Gensender");
//                            LCD.showNumber((int)System.currentTimeMillis() - timeout);
                            System.out.println("le arg " + writeBuffer[this.sentIndex]);
                            System.out.println((int)System.currentTimeMillis() + "    " + System.currentTimeMillis());
                            timeout = (int)System.currentTimeMillis() + ACKNOWLEDGE_TIMEOUT;
                            this.out.write(this.lastPackageSent);
                            this.out.write(writeBuffer[this.sentIndex]);                            
                            //System.out.println("Færdig med at gensende");
                        }
                        
                        Thread.yield();
                    } catch (Exception e) {
    //                    e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
    //                    e.printStackTrace();
                    }
                }
            }
        }        
    }
    
    public class TransportInputStream extends InputStream {
        
        private int readIndex;
        private int data;                
        
        protected TransportInputStream() {
            this.readIndex = readBufferIndex;
        }
                
        public int read() throws IOException {
            if (this.readIndex == readBufferIndex) {
                return -1;
            } else {                
                this.data = readBuffer[this.readIndex++];
                
                if (this.readIndex == BUFFER_SIZE) {
                    this.readIndex = 0;
                }
                
                return this.data;
            }
        }
    }
    
    public class TransportOutputStream extends OutputStream {                
        public void write(int b) throws IOException {            
            writeBuffer[writeBufferIndex++] = (byte)b;
                
            if (writeBufferIndex == BUFFER_SIZE) {
                writeBufferIndex = 0;
            }
        }                
   }
           
    public TransportOutputStream getOutputStream()
    {
        return this.out;
    }
    
    public TransportInputStream getInputStream()
    {
        return this.in;
    }
}