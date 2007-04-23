/*
 * TransportSocket.java
 *
 * Created on 24. marts 2007, 12:06
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.2
 *
 *
 * ******VERSION HISTORY******
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
    
    public static final int DATA = 0;
    public static final int RECEIPT = 1;
    public static final int READ_TIMEOUT = 250;
    public static final int ACKNOWLEDGE_TIMEOUT = 250;
    
    private static int sequence;
    
    /** Creates a new instance of IRSocket */
    public TransportSocket(InputStream in, OutputStream out)
    {
        this.in = new TransportSocket.TransportInputStream(in, out);
        this.out = new TransportSocket.TransportOutputStream(in, out);
        
        this.sequence = 0;
    }
    
    public class TransportInputStream extends InputStream
    {
        private InputStream in;
        private OutputStream out;
        
        private int last_sequence;
        
        protected TransportInputStream(InputStream in, OutputStream out)
        {
            this.in = in;
            this.out = out;
            
            this.last_sequence = -1;
        }
        
        public int read() throws IOException
        {
            int header, data;
            int timestamp = (int)System.currentTimeMillis();
            
            do
            {
                if(((int)System.currentTimeMillis())-timestamp >= TransportSocket.READ_TIMEOUT)
                    return -1;
                header = this.in.read();
            } while(header==-1);
            
            //Only continue if byte received is Data header.
            if(TransportPackage.getType(header) == DATA)
            {
                //Check indefinitely for second byte.
                do
                {
                    data = this.in.read();
                } while(data==-1);
                
                //Send receipt.
                this.out.write(0x80 | TransportPackage.getSequenceNumber(header));
                this.out.write(RobotProxy.NOP);
                
                //Return data only if not a repeat of the last sequence
                if(TransportPackage.getSequenceNumber(header) != this.last_sequence)
                {
                    this.last_sequence = TransportPackage.getSequenceNumber(header);
                    return data;
                }
            }
            
            return -1;
        }
    }
    
    public class TransportOutputStream extends OutputStream
    {
        
        private OutputStream out;
        private InputStream in;
        
        protected TransportOutputStream(InputStream in, OutputStream out)
        {
            this.out = out;
            this.in = in;
        }
        
        public void write(int b) throws IOException
        {
            int sequence = TransportSocket.getSequenceNumber();
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
            this.out.write(0x7F & sequence);
            this.out.write(b);
            
            int header, data;
            int timestamp = (int)System.currentTimeMillis();
            
            //Try to read acknowledge header. Timeout if needed.
            do
            {
                if(((int)System.currentTimeMillis())-timestamp >= TransportSocket.ACKNOWLEDGE_TIMEOUT)
                {
                    throw new IOException("Transport layer timeout. No acknowledge received.");
                }
                header = this.in.read();
            } while(header==-1);
            
            //Acknowledge header received. Wait for data byte (NOP).
            do
            {
                data = this.in.read();
            } while(data==-1);
            
            //End method if the proper acknowledge was received. Retry if it didn't match sent package.
            if(TransportPackage.getType(header) == TransportSocket.RECEIPT && TransportPackage.getSequenceNumber(header) == sequence)
                return;
            else
                this.write(b, sequence);
        }
    }
    
    public static int getSequenceNumber()
    {
        sequence = (sequence+1)%128;
        return sequence;
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