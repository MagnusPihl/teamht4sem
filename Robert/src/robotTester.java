/*
 * robotTester.java
 *
 * Created on 15. april 2007, 13:56
 *
 * Company: HT++
 *
 * @author thh
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * thh @ 15. april 2007 (v 1.0)
 * __________ Changes ____________
 *
 */
import communication.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class robotTester {
    
    /*LLCSocket link = new LLCSocket();
    NetworkSocket net = new NetworkSocket(0,1,link.getInputStream(),link.getOutputStream());
    TransportSocket socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
    InputStream in = socket.getInputStream();
    OutputStream out = socket.getOutputStream();*/
    /** Creates a new instance of robotTester */
    
    public robotTester(){
         System.out.println("Making connection");
        LLCSocket link = new LLCSocket();
         System.out.println("LLC made");
        NetworkSocket net = new NetworkSocket(0,1,link.getInputStream(),link.getOutputStream());
         System.out.println("Network made");
        TransportSocket socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
         System.out.println("trasport made");
        InputStream in = socket.getInputStream();
         System.out.println("inputstream made");
        OutputStream out = socket.getOutputStream();
        System.out.println("start");
        try {
            out.write(0x00);
        } catch (IOException ex) {
            System.out.println("out.write(0x00)");
            //ex.printStackTrace();
        }
        try {
            out.write(10);
        } catch (IOException ex) {
            System.out.println("out.write(10)");
            //ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws InterruptedException, IOException{
        robotTester noget = new robotTester();
        //noget.run();
    }
    
}
