//package obsolete;
//
//import communication.*;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//public class SenderTester {
//    
//    private final String mode = "transport";
//    
//    public SenderTester()
//    {
//        TowerSocket link = new TowerSocket();
//        NetworkSocket network = new NetworkSocket(0, 1, link.getInputStream(), link.getOutputStream());
//        TransportSocket transport = new TransportSocket(network.getInputStream(), network.getOutputStream());
//        
//        NetworkSocket net2 = new NetworkSocket(0, 2, link.getInputStream(), link.getOutputStream());
//        TransportSocket trans2 = new TransportSocket(net2.getInputStream(), net2.getOutputStream());
//        
//        int data = 0;
//        InputStreamReader isr = new InputStreamReader( System.in );
//        BufferedReader stdin = new BufferedReader( isr );
//        
//        int inc_data = -1, inc_data2 = -1;
//        data = 1;
//        
//        while(true)
//        {
//            System.out.println("Det her skulle gerne blive gentaget...");
//            try {
//                //data = Integer.parseInt(stdin.readLine());
//                
////                if(mode == "transport")
////                    inc_data = transport.getInputStream().read();
////                if(mode == "network")
////                    inc_data = network.getInputStream().read();
////                if(inc_data != -1)
////                    System.out.println("RECEIVED DATA:      "+inc_data);
//                
//            } catch (NumberFormatException ex) {
//                ex.printStackTrace();
//            }
////                catch (IOException ex) {
////                ex.printStackTrace();
////            }
//            
//            try {
//                System.out.println("Skriver nu!");
//                trans2.getOutputStream().write(50+(data));
//                transport.getOutputStream().write(0+(data));
//                System.out.println("Færdig med at skrive!");
////                System.out.println("SENDING DATA: "+data+", "+Integer.toBinaryString(data));
////                inc_data = transport.getInputStream().read();
////                inc_data2 = trans2.getInputStream().read();
////                if(inc_data != -1)
////                    System.out.println("RECEIVING DATA: "+inc_data+", "+Integer.toBinaryString(inc_data));
//                
//                data = (data+1)%50;
//                
//                try {
//                    
//                    Thread.sleep(250);
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
//    
//    public static void main(String[] args)
//    {
//        new SenderTester();
//    }
//}