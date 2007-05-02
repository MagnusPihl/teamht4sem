import communication.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/*
 * PCTest.java
 *
 * Created on 23. april 2007, 11:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Magnus
 */
public class PCTest {
    
    /** Creates a new instance of PCTest */
    public PCTest()
    {
        TowerSocket link = new TowerSocket();
        NetworkSocket network = new NetworkSocket(1, 0, link.getInputStream(), link.getOutputStream());
        TransportSocket transport = new TransportSocket(network.getInputStream(), network.getOutputStream());
        
        int data = 0, inc = -1;
        InputStreamReader isr = new InputStreamReader( System.in );
        BufferedReader stdin = new BufferedReader( isr );
        
        transport.setActive(true);
        
        ReadThread rt = new ReadThread(transport);
        rt.start();
        
        while(true)
        {
            try {
//                inc = transport.getInputStream().read();
                data = Integer.parseInt(stdin.readLine());
            } catch (NumberFormatException ex) {
                System.out.println("Blah");
                data = -1;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
//            if(inc != -1)
//                System.out.println("INCOMING: "+inc);
            
            try {
                if(data != -1)
                {
                    System.out.println("OUT: "+data);
                    transport.getOutputStream().write(data);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
                    
            try {   
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args)
    {
        new PCTest();
    }
    
    public class ReadThread extends Thread
    {
        int inc = -1;
        TransportSocket transport;
        
        public ReadThread(TransportSocket trans)
        {
            transport = trans;
        }
        
        public void run()
        {
            while(true)
            {
                try {
                    inc = transport.getInputStream().read();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
               
               if(inc != -1)
                   System.out.println("INC: "+inc);
                
               Thread.sleep(100);
            }
        }
    }
}
