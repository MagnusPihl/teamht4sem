/*
 * TransportSocket.java
 *
 * Created on 24. marts 2007, 12:06
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.4
 *
 *
 * ******VERSION HISTORY******
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

public class TransportSocket
{
    private TransportInputStream in;
    private TransportOutputStream out;
    private int lastAcknowledge;
    private Thread inputThread;
    private boolean isWriting;
    private int bufferIndex; 
    private byte[] readBuffer;
    
    public static final int DATA = 0;
    public static final int RECEIPT = 1;
    
    private static final byte NOP = 0x00;
    
    public static final int INPUT_BUFFER_SIZE = 20;
    //Total time to attempt writing before failing, in milliseconds.
    public static final int WRITE_TIMEOUT = 300;
    //Time to wait for acknowledge before retrying to write data. Should always be lower than WRITE_TIMEOUT.
    public static final int ACKNOWLEDGE_TIMEOUT = 150;    
    
    /** Creates a new instance of IRSocket */
    public TransportSocket(InputStream in, OutputStream out)
    {
        
        this.readBuffer = new byte[INPUT_BUFFER_SIZE];   
        this.in = new TransportSocket.TransportInputStream();
        this.out = new TransportSocket.TransportOutputStream(in, out);                        
        this.isWriting = false;
        
        this.inputThread = new TransportInputThread(in, out);
        this.inputThread.start();        
    }
    
    public class TransportInputThread extends Thread {               
        private int data;     
        private int lastSequence;
        private int header;    
     
        private InputStream in;
        private OutputStream out;   
            
        protected TransportInputThread(InputStream in, OutputStream out) {        
            this.in = in;
            this.out = out;      
            bufferIndex = 0;
            this.lastSequence = -1;
        }
        
        public void run() {
            while (true) {
                try {
                    //int timestamp = (int)System.currentTimeMillis();

                    //Read header byte. Timeout if neccessary.
                    this.header = this.in.read();

                    if (this.header != -1) {
                        //Check indefinitely for second byte.
                        do {
                            this.data = this.in.read();
                        } while (this.data == -1);
                    } else {
                        Thread.sleep(100);
                        continue;
                    }

                    //Only continue if byte received is Data header.
                    if (TransportPackage.getType(header) == DATA) {
                        //Send receipt.
                        while (isWriting) {
                            Thread.sleep(50);
                        }
                        isWriting = true;
                        this.out.write(TransportPackage.createAcknowledgeHeader(this.header));
                        this.out.write(NOP);
                        isWriting = false;

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

                    Thread.sleep(50);
                } catch (Exception e) {                    
                }
            }
        }        
    }
    
    public class TransportInputStream extends InputStream {
        
        private int readIndex;
        private int data;                
        
        protected TransportInputStream() {
            this.readIndex = bufferIndex;
        }
                
        public int read() throws IOException {
            if (this.readIndex == bufferIndex) {
                return -1;
            } else {                
                this.data = readBuffer[this.readIndex++];
                
                if (this.readIndex == INPUT_BUFFER_SIZE) {
                    this.readIndex = 0;
                }
                
                return this.data;
            }
        }
    }
    
    public class TransportOutputStream extends OutputStream
    {        
        private OutputStream out;
        private InputStream in;
        private int sequence;
        
        protected TransportOutputStream(InputStream in, OutputStream out)
        {
            this.out = out;
            this.in = in;
            this.sequence = 0;
        }
        
        public void write(int b) throws IOException
        {
            sequence = (sequence+1)%127;
            
            try
            {
                this.write(b, sequence);
            }
            catch(IOException e)
            {
                throw e;
            }
        }
        
        private void write(int b, int sequence) throws IOException
        {
            //Write header and data bytes.
            
            try {
                while (isWriting) {
                    Thread.sleep(50);
                }
                isWriting = true;
                this.out.write(0x7F & sequence);
                this.out.write(b);
                isWriting = false;

                //System.out.println("Transport layer sending... "+(0x7F&sequence)+", "+b);

                int timestamp = (int)System.currentTimeMillis();
                int ack_timestamp = timestamp;

                //Try to read acknowledge header. Timeout if needed.
                while(((int)System.currentTimeMillis())-timestamp <= TransportSocket.WRITE_TIMEOUT && lastAcknowledge != sequence)
                {
                    if(((int)System.currentTimeMillis())-ack_timestamp >= TransportSocket.ACKNOWLEDGE_TIMEOUT)
                    {

                        while (isWriting) {
                            Thread.sleep(50);
                        }
                        isWriting = true; 
                        this.out.write(0x7F & sequence);
                        this.out.write(b);
                        ack_timestamp = (int)System.currentTimeMillis();
                        isWriting = false;

                    }                

                    this.wait(ACKNOWLEDGE_TIMEOUT);
                }            

                //End method if the proper acknowledge was received. Retry if it didn't match sent package.
                if(lastAcknowledge == sequence)
                {
                    //System.out.println("Returning");
                    return;
                }
                else
                {
                    //System.out.println("Retrying");
                    this.write(b, sequence);
                }                    
            } catch (Exception e) {

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
