/*
 * TransportSocket.java
 *
 * Created on 24. marts 2007, 12:06
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
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
    public static final int ACKNOWLEDGE_TIMEOUT = 250;
    
    private static int sequence;
    
    /** Creates a new instance of IRSocket */
    public TransportSocket(InputStream in, OutputStream out)
    {
        this.in = new TransportSocket.TransportInputStream(in, out);
        this.out = new TransportSocket.TransportOutputStream(in, out);
        
        this.sequence = 1;
    }
    
    public class TransportInputStream extends InputStream
    {
        private InputStream in;
        private OutputStream out;
        
        private TransportPackage pack;
        public static final int timeout = 500;
        
        protected TransportInputStream(InputStream in, OutputStream out)
        {
            this.in = in;
            this.out = out;
        }
        
        public int read() throws IOException
        {
            int header, data;
            int timestamp = (int)System.currentTimeMillis();
            
            do
            {
                if(((int)System.currentTimeMillis())-timestamp >= timeout)
                    return -1;
                header = this.in.read();
            } while(header==-1);
            
            do
            {
                data = this.in.read();
            } while(data==-1);
            
            pack = new TransportPackage(header, data);

            if(pack.getType() == DATA)
            {
                this.out.write(0x80 | pack.getSequenceNumber());
                this.out.write(0x00);
            }
                
            return pack.getData();
        }
    }
    
    public class TransportOutputStream extends OutputStream
    {
        
        private OutputStream out;
        private InputStream in;
        
        public static final int timeout = 1000;
        
        protected TransportOutputStream(InputStream in, OutputStream out)
        {
            this.out = out;
            this.in = in;
        }
        
        public void write(int b) throws IOException
        {
            int sequence = TransportSocket.getSequenceNumber();
            this.out.write(0x7F & sequence);
            this.out.write(b);
            
            int header, data;
            TransportPackage pack;
            int timestamp = (int)System.currentTimeMillis();
            
            do
            {
                if(((int)System.currentTimeMillis())-timestamp >= timeout)
                {
                    this.write(b);
                    return;
                }
                header = this.in.read();
            } while(header==-1);
            
            do
            {
                data = this.in.read();
            } while(data==-1);
            
            pack = new TransportPackage(header, data);
            
            if(pack.getType() == TransportSocket.RECEIPT && pack.getSequenceNumber() == sequence)
                return;
            else
                this.write(b);
        }
    }
    
    public static int getSequenceNumber()
    {
        sequence++;
        if(sequence > 127)
            sequence = 1;
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