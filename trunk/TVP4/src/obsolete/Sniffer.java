//package obsolete;
//
//import communication.NetworkSocket;
//import communication.TowerSocket;
//import communication.TransportSocket;
//import game.PacmanApp;
//import java.awt.BorderLayout;
//import java.awt.Container;
//import java.awt.Dimension;
//import java.awt.FlowLayout;
//import java.awt.GridLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.KeyEvent;
//import java.awt.event.KeyListener;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import javax.swing.JButton;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JTextField;
///*
// * Sniffer.java
// *
// * Created on 7. maj 2007, 09:14
// *
// * Company: HT++
// *
// * @author Mikkel Brøndsholm Nielsen
// * @version 1.0
// *
// *
// * ******VERSION HISTORY******
// *
// * Administrator @ 7. maj 2007 (v 1.1)
// * __________ Changes ____________
// *
// * Administrator @ 7. maj 2007 (v 1.0)
// * __________ Changes ____________
// *
// */
//
//public class Sniffer extends JFrame{
//    
//    private KeyListener listenKey;
//    private Container cont;
//    
//    private JTextField pacman;
//    private JTextField robot1;
//    private JTextField robot2;
//    private JTextField robot3;
//    
//    //private 
//    
//    private JButton startBt;
//    
//    private static TowerSocket link = new TowerSocket();
//    
//    private NetworkSocket net0;// = new NetworkSocket(1,0,link.getInputStream(),link.getOutputStream());
//    private TransportSocket socket0;// = new TransportSocket(net.getInputStream(), net.getOutputStream());
//    protected InputStream in0;// = socket.getInputStream();
//    protected OutputStream out0;// = socket.getOutputStream();
//    
//    private NetworkSocket net1;// = new NetworkSocket(1,0,link.getInputStream(),link.getOutputStream());
//    private TransportSocket socket1;// = new TransportSocket(net.getInputStream(), net.getOutputStream());
//    protected InputStream in1;// = socket.getInputStream();
//    protected OutputStream out1;// = socket.getOutputStream();
//    
//    private NetworkSocket net2;// = new NetworkSocket(1,0,link.getInputStream(),link.getOutputStream());
//    private TransportSocket socket2;// = new TransportSocket(net.getInputStream(), net.getOutputStream());
//    protected InputStream in2;// = socket.getInputStream();
//    protected OutputStream out2;// = socket.getOutputStream();
//    
//    private NetworkSocket net3;// = new NetworkSocket(1,0,link.getInputStream(),link.getOutputStream());
//    private TransportSocket socket3;// = new TransportSocket(net.getInputStream(), net.getOutputStream());
//    protected InputStream in3;// = socket.getInputStream();
//    protected OutputStream out3;// = socket.getOutputStream();
//    
//    protected boolean doRead;
//    
//    /** Creates a new instance of Sniffer */
//    public Sniffer() {
//        this.cont = this.getContentPane();
//        Reader x = new Reader();
//        x.start();
//        doRead = false;
//        
//        link.open("usb");
//        net0 = new NetworkSocket(1,0,link.getInputStream(),link.getOutputStream());
//        socket0 = new TransportSocket(net0.getInputStream(), net0.getOutputStream());
//        socket0.setActive(true);
//        in0 = net0.getInputStream();
//        //out1 = socket.getOutputStream();
//        
//        net1 = new NetworkSocket(0,1,link.getInputStream(),link.getOutputStream());
//        socket1 = new TransportSocket(net1.getInputStream(), net1.getOutputStream());
//        socket1.setActive(true);
//        in1 = net1.getInputStream();
//        //out1 = socket.getOutputStream();
////        
////        net2 = new NetworkSocket(2,0,link.getInputStream(),link.getOutputStream());
////        socket2 = new TransportSocket(net2.getInputStream(), net2.getOutputStream());
////        socket2.setActive(true);
////        in2 = socket2.getInputStream();
////        //out1 = socket.getOutputStream();
////        
////        net3 = new NetworkSocket(3,0,link.getInputStream(),link.getOutputStream());
////        socket3 = new TransportSocket(net3.getInputStream(), net3.getOutputStream());
////        socket3.setActive(true);
////        in3 = socket3.getInputStream();
////        //out1 = socket.getOutputStream();
//        
//        //Create JPanels
//        JPanel flowDesign = new JPanel(new FlowLayout());
//        JPanel borderDesign = new JPanel(new BorderLayout());
//        JPanel gridDesign = new JPanel(new GridLayout(2,2));
//        
//        //Arrange JPanels
//        cont.add(flowDesign);
//        flowDesign.add(borderDesign);
//        borderDesign.add(gridDesign, BorderLayout.CENTER);
//        
//        //Create and arrange ze textfields
//        pacman = new JTextField();
//        Dimension d = new Dimension(300,50);
//        pacman.setPreferredSize(d);
//        robot1 = new JTextField();
//        robot2 = new JTextField();
//        robot3 = new JTextField();
//        gridDesign.add(pacman);
//        gridDesign.add(robot1);
//        gridDesign.add(robot2);
//        gridDesign.add(robot3);
//        
//        // Add keylisteners
//        this.listenKey = new KeyListener() {
//            public void keyPressed(KeyEvent e){}
//            public void keyReleased(KeyEvent e){
//                if (e.getKeyCode() == e.VK_ESCAPE) {
//                    System.exit(0);
//                }
//            }
//            public void keyTyped(KeyEvent e) {}
//        };
//        this.addKeyListener(listenKey);
//        cont.addKeyListener(listenKey);
//        
//        startBt = new JButton("start");
//        startBt.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                setButtonText();
////                if(doRead == false)
////                    doRead = true;
////                else{
////                    doRead = false;
////                }
//            }
//        });
//        borderDesign.add(startBt, BorderLayout.SOUTH);
//        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
//        this.pack();
//        this.setVisible(true);
//    }
//    
//    public void setButtonText(){
//        if(this.startBt.getText().equals("start")){
//            this.startBt.setText("stop");
//            doRead = true;
//        } else{
//            this.startBt.setText("start");
//            doRead = false;
//        }
//    }
//    
////    public void read(){
////        int h = -1;
////        int i = -1;
////        int j = -1;
////        int k = -1;
////
////        while(doRead){
////            try {
////                h = in0.read();
////                i = in1.read();
////                j = in2.read();
////                k = in3.read();
////
////                int timestamp = (int)System.currentTimeMillis();
////                if(h != -1)
////                System.out.println("Data fra comp: " + h);
////                    pacman.setText("\nMessage received at: " + timestamp + "     -     " + Integer.toBinaryString(h));
////                if(i != -1)
////                System.out.println("Data fra comp: " + i);
////                    pacman.setText("\nMessage received at: " + timestamp + "     -     " + Integer.toBinaryString(i));
////                if(j != -1)
////                    pacman.setText("\nMessage received at: " + timestamp + "     -     " + Integer.toBinaryString(j));
////                if(k != -1)
////                    pacman.setText("\nMessage received at: " + timestamp + "     -     " + Integer.toBinaryString(k));
////            } catch (IOException ex) {
////                ex.printStackTrace();
////            }
////        }
////    }
//    
//    public class Reader extends Thread {
//        private int sentIndex;
//        int h = -1;
//        int i = -1;
//        int j = -1;
//        int k = -1;
//                        int o = 0;
//        String bits = "";
//        public Reader(){
//            this.sentIndex = 0;
//        }
//        
//        public void run(){
//            while(true){
//                if(doRead){
//                    try {
//                        h = in0.read();
//                        i = in1.read();
//                        String hh = "";
//                        String ii = "";
////                        j = in2.read();
////                        k = in3.read();
////                        int result = h + i + j+ k;
////                        if(o%1000 == 0){
////                            System.out.println("Still nothing " + result + "");
////                        }
////                            o++;
//                        int timestamp = (int)System.currentTimeMillis();
//                        if(h > 0){
//                            bits = Integer.toBinaryString(h);
//                            System.out.println("Data fra comp: " + h);
//                            hh += "\nMessage received at: " + timestamp + "     -     " + bits + "  -  : " + h;
//                            pacman.setText(hh);//+ bits.substring(bits.length()-8,bits.length()));
//                        }
//                        if(i > 0){
//                            bits = Integer.toBinaryString(i);
//                            System.out.println("Data fra pac: " + i);
//                            ii += "\nMessage received at: " + timestamp + "     -     " + bits + "  -  : " + i;
//                            robot1.setText("\nMessage received at: " + timestamp + "     -     " + bits + "  -  : " + i);// + bits.substring(bits.length()-8,bits.length()));
//                        }
//                        if(j != -1){
//                            bits = Integer.toBinaryString(j);
//                            System.out.println("Data fra comp: " + j);
//                            robot2.setText("\nMessage received at: " + timestamp + "     -     " + bits.substring(bits.length()-8,bits.length()));
//                        }
//                        if(k != -1){
//                            bits = Integer.toBinaryString(k);
//                            System.out.println("Data fra comp: " + k);
//                            robot3.setText("\nMessage received at: " + timestamp + "     -     " + bits.substring(bits.length()-8,bits.length()));
//                        }
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//    
//    public static void main(String[] args){
//        Sniffer snifnu = new Sniffer();
//    }
//    
//}
