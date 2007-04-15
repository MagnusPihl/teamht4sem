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
        TowerSocket link = new TowerSocket();
        NetworkSocket net = new NetworkSocket(0,1,link.getInputStream(),link.getOutputStream());
        TransportSocket socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        try {
            out.write(0x00);
            System.out.println("out.write(0x00) sendt");
        } catch (IOException ex) {
            System.out.println("out.write(0x00)");
            //ex.printStackTrace();
        }
        try {
            out.write(10);
            System.out.println("out.write(10) sendt");
        } catch (IOException ex) {
            System.out.println("out.write(10)");
            //ex.printStackTrace();
        }
        int i = -1;
        while(i== -1){
            try {
                i = in.read();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(i);
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        try {
            out.write(0x01);
            System.out.println("out.write(0x01) sendt");
        } catch (IOException ex) {
            System.out.println("out.write(0x01)");
            //ex.printStackTrace();
        }
        try {
            out.write(5);
            System.out.println("out.write(5) sendt");
        } catch (IOException ex) {
            System.out.println("out.write(5)");
            //ex.printStackTrace();
        }
        i = -1;
        while(i== -1){
            try {
                i = in.read();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println(i);
        }
    }
    
    
    public static void main(String[] args) throws InterruptedException, IOException{
        robotTester noget = new robotTester();
        //noget.run();
    }
    
}
