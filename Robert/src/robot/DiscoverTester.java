/*
 * DiscoverTester.java
 *
 * Created on 14. maj 2007, 09:23
 *
 * Company: HT++
 *
 * @author thh
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * thh @ 14. maj 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package robot;

import communication.GameCommands;
import communication.LLCSocket;
import communication.NetworkSocket;
import communication.TransportSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import josx.platform.rcx.Button;
import josx.platform.rcx.ButtonListener;
import josx.platform.rcx.LCD;
import josx.platform.rcx.Sound;
import josx.platform.rcx.TextLCD;


public class DiscoverTester implements ButtonListener {
    LLCSocket link = new LLCSocket();
    NetworkSocket net;
    TransportSocket socket;
    InputStream in;
    OutputStream out;
    private int command = -1;
    
    private boolean hest;
    private int[] dirArray = {GameCommands.FORWARD | GameCommands.TURN_NUMBER, GameCommands.TURN_RIGHT | GameCommands.TURN_LEFT, GameCommands.FORWARD | GameCommands.TURN_RIGHT | GameCommands.TURN_LEFT, GameCommands.TURN_NUMBER | GameCommands.TURN_RIGHT | GameCommands.TURN_LEFT,
    GameCommands.TURN_NUMBER | GameCommands.FORWARD | GameCommands.TURN_LEFT, GameCommands.TURN_NUMBER | GameCommands.FORWARD | GameCommands.TURN_RIGHT,
    GameCommands.TURN_NUMBER | GameCommands.FORWARD | GameCommands.TURN_LEFT | GameCommands.FORWARD, GameCommands.TURN_NUMBER | GameCommands.TURN_RIGHT,
    GameCommands.TURN_NUMBER | GameCommands.TURN_LEFT, GameCommands.FORWARD |GameCommands.TURN_RIGHT, GameCommands.FORWARD |GameCommands.TURN_RIGHT};
    private String[] dirNameArray = {"1010", "0101","1101","0111","1011","1110","1111","0110", "0011", "1100", "1001"};
    private int dir;
    
    private int index = 0;

    private int directions;
    
    /** Creates a new instance of DiscoverTester */
    public DiscoverTester() {
        Button.RUN.addButtonListener(this);
        Button.PRGM.addButtonListener(this);
        Button.VIEW.addButtonListener(this);
        init();
    }
    
    private void init(){
        net = new NetworkSocket(1,0,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        socket.setActive(true);
        while(true){
            this.getcommand();
            TextLCD.print("step2");
            this.run();
        }
    }
    
    private void getcommand(){
        command = -1;
        directions = -1;
        while(command == -1){
            try {
                command = in.read();
                LCD.showNumber(command);
            } catch (IOException ex) {
                
            }
            if(command <= (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER) && command > GameCommands.NOP){
                while(directions == -1){
                    try {
                        directions = in.read();
                        LCD.showNumber(directions);
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }
    
    public void buttonPressed(Button button) {
        if (Button.VIEW.isPressed() && hest == true) {
            index--;
        }else if (Button.PRGM.isPressed() && hest == true) {
            index++;
        }else if (Button.RUN.isPressed() && hest == true) {
//            this.sensdMoveDone(GameCommands.MOVE_DONE | dirArray[index]);
            hest = false;
        }
    }
    
    public void buttonReleased(Button button) {
    }
    
    private void run() {
        if(command == (GameCommands.FORWARD | GameCommands.DISCOVER)){
            this.SendDir();
        }else if(command == (GameCommands.TURN_LEFT | GameCommands.DISCOVER) || command == (GameCommands.TURN_LEFT | GameCommands.DISCOVER | GameCommands.TURN_NUMBER)){
            this.SendDir();
        }else if(command == (GameCommands.TURN_RIGHT | GameCommands.DISCOVER) || command == (GameCommands.TURN_RIGHT | GameCommands.DISCOVER | GameCommands.TURN_NUMBER)){
            this.SendDir();
        }else if(command == GameCommands.SEARCH_NODE){
            this.SendDir();
        }else if(command == GameCommands.FORWARD || command == GameCommands.TURN_LEFT || command == (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER) || command == GameCommands.TURN_RIGHT || command == (GameCommands.TURN_RIGHT | GameCommands.TURN_NUMBER)){
            this.sendMoveDone(GameCommands.MOVE_DONE);
        }else{
            LCD.showNumber(command);
            Sound.twoBeeps();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
//                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException, IOException{
        DiscoverTester noget = new DiscoverTester();
    }
    
    private void SendDir() {
        hest = true;
        TextLCD.print("step3");
//        while(hest == true){
//            TextLCD.print(dirNameArray[index]);
//        }
        this.sendMoveDone(GameCommands.MOVE_DONE | dirArray[0]);
    }
    
    public void sendMoveDone(int move){
        LCD.showNumber(move);
        try {
            out.write(move);
        } catch (IOException ex) {
            
        }
        Sound.beep();
    }
    
}
