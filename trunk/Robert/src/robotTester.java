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
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class robotTester extends JFrame{
    private KeyListener listenKey;
    
    private JButton r1Up;
    private JButton r1Right;
    private JButton r1Down;
    private JButton r1Left;
    private JButton r1Sound;
    private JButton r1LightOn;
    private JButton r1LightOff;
    
    private JButton r2Up;
    private JButton r2Right;
    private JButton r2Down;
    private JButton r2Left;
    private JButton r2Sound;
    private JButton r2LightOn;
    private JButton r2LightOff;
    
    private Container cont;
//    private static TowerSocket link = new TowerSocket();
    private Semaphore e = new Semaphore(1);
    private RobotProxy robertProxy = new RobotProxy(1,e);
   
//    private NetworkSocket net1 = new NetworkSocket(0,1,link.getInputStream(),link.getOutputStream());
//    private TransportSocket socket1 = new TransportSocket(net1.getInputStream(), net1.getOutputStream());
//    private InputStream in1 = socket1.getInputStream();
//    private OutputStream out1 = socket1.getOutputStream();
    
//    private NetworkSocket net2 = new NetworkSocket(0,2,link.getInputStream(),link.getOutputStream());
//    //private TransportSocket socket2 = new TransportSocket(net2.getInputStream(), net2.getOutputStream());
//    private InputStream in2 = net2.getInputStream();
//    private OutputStream out2 = net2.getOutputStream();
    
    private GameCommands x = GameCommands.getInstance();
    
    public robotTester(){
        //robertProxy = new RobotProxy(1, new Semaphore(1));
        cont = this.getContentPane();
        JPanel design1 = new JPanel();
        design1.setLayout(new FlowLayout());
        JPanel robot1 = new JPanel();
        robot1.setLayout(new GridLayout(6,3));
        
        this.listenKey = new KeyListener() {
            public void keyPressed(KeyEvent e){}
            public void keyReleased(KeyEvent e){
                if (e.getKeyCode() == e.VK_ESCAPE) {
                    System.exit(0);
                }
                if (e.getKeyCode() == e.VK_UP){
                    try {
                    robertProxy.beep();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                    move(x.MOVE_UP);
                }
                if (e.getKeyCode() == e.VK_DOWN){
                    move(x.MOVE_DOWN);
                }
                if (e.getKeyCode() == e.VK_LEFT){
                    move(x.MOVE_LEFT);
                }
                if (e.getKeyCode() == e.VK_RIGHT){
                    move(x.MOVE_RIGHT);
                }
            }
            public void keyTyped(KeyEvent e) {}
        };
        this.addKeyListener(listenKey);
        design1.addKeyListener(listenKey);
        robot1.addKeyListener(listenKey);
        cont.addKeyListener(listenKey);
        r1Up = new JButton("Up");
        r1Up.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                move(x.MOVE_UP);
            }
        });
        
        r1Down = new JButton("Down");
        r1Down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                move(x.MOVE_DOWN);
            }
        });
        
        r1Right = new JButton("Right");
        r1Right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                move(x.MOVE_RIGHT);
            }
        });
        
        r1Left = new JButton("Left");
        r1Left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                move(x.LEFT);
            }
        });
        r1Sound = new JButton("Sound");
        r1Sound.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    robertProxy.beep();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        r1LightOn = new JButton("Light on");
        r1LightOn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    robertProxy.lights(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                //robot1(x.LIGHT_ON);
            }
        });
        r1LightOff = new JButton("Light off");
        r1LightOff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    robertProxy.lights(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                //robot1(x.LIGHT_OFF);
            }
        });
        
        JPanel robot2 = new JPanel();
        robot2.setLayout(new GridLayout(6,3));
        r2Up = new JButton("Up");
        r2Up.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                robot2Send(0x00);
            }
        });
        r2Down = new JButton("Down");
        r2Down.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                robot2Send(0x02);
            }
        });
        r2Right = new JButton("Right");
        r2Right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                robot2Send(0x01);
            }
        });
        r2Left = new JButton("Left");
        r2Left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                robot2Send(0x03);
            }
        });
        r2Sound = new JButton("Sound");
        r2Sound.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                robot2(0x13);
            }
        });
        r2LightOn = new JButton("Light on");
        r2LightOn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                robot2(0x11);
            }
        });
        
        r2LightOff = new JButton("Light off");
        r2LightOff.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                robot2(0x12);
            }
        });
        
        robot1.add(new JLabel(""));
        robot1.add(new JLabel("Robot 1"));
        robot1.add(new JLabel(""));
        robot1.add(new JLabel(""));
        robot1.add(r1Up);
        robot1.add(new JLabel(""));
        robot1.add(r1Left);
        robot1.add(new JLabel(""));
        robot1.add(r1Right);
        robot1.add(new JLabel(""));
        robot1.add(r1Down);
        robot1.add(new JLabel(""));
        robot1.add(new JLabel(""));
        robot1.add(new JLabel(""));
        robot1.add(new JLabel(""));
        robot1.add(r1Sound);
        robot1.add(r1LightOn);
        robot1.add(r1LightOff);
        
        robot2.add(new JLabel(""));
        robot2.add(new JLabel("Robot 2"));
        robot2.add(new JLabel(""));
        robot2.add(new JLabel(""));
        robot2.add(r2Up);
        robot2.add(new JLabel(""));
        robot2.add(r2Left);
        robot2.add(new JLabel(""));
        robot2.add(r2Right);
        robot2.add(new JLabel(""));
        robot2.add(r2Down);
        robot2.add(new JLabel(""));
        robot2.add(new JLabel(""));
        robot2.add(new JLabel(""));
        robot2.add(new JLabel(""));
        robot2.add(r2Sound);
        robot2.add(r2LightOn);
        robot2.add(r2LightOff);
        
        design1.add(robot1);
        design1.add(robot2);
        cont.add(design1);
        
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
    }
    
    private void move(byte i){
        try {
            this.robertProxy.move(i, (byte)(GameCommands.UP | GameCommands.DOWN | GameCommands.LEFT | GameCommands.RIGHT));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void robot1Send(int i){
//        int j = -1;
////        try {
//////            out1.write(i);
////        } catch (IOException ex) {
////            System.out.println("felj 1");
////            ex.printStackTrace();
////        }
//        try {
////            out1.write(10);
//        } catch (IOException ex) {
//            System.out.println("felj 2");
//            ex.printStackTrace();
//        }
//        while(j == -1){
//            try {
////                j = in1.read();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
    }
    private void robot1(int i){
//        try {
////            out1.write(i);
//        } catch (IOException ex) {
//            System.out.println("felj 3");
//            ex.printStackTrace();
//        }
////        try {
////            out1.write(0xFE);
////        } catch (IOException ex) {
////            ex.printStackTrace();
////        }
    }
    
    
    private void robot2Send(int i){
//        int j = -1;
//        try {
////            out2.write(i);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        try {
////            out2.write(10);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        while(j == -1){
//            try {
////                j = in2.read();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
    }
    private void robot2(int i){
//        try {
////            out2.write(i);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
////        try {
////            out2.write(0xFE);
////        } catch (IOException ex) {
////            ex.printStackTrace();
////        }
    }
    
    public static void main(String[] args) throws InterruptedException, IOException{
        robotTester noget = new robotTester();
        //noget.run();
    }
    
}
