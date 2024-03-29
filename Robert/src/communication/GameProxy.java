/*
 * GameProxy.java
 *
 * Created on 27. marts 2007, 12:00
 *
 * Company: HT++
 *
 * @author thh
 * @version 2.0
 *
 *
 * ******VERSION HISTORY******
 *
 * thh @ 27. marts 2007 (v 1.0)
 * __________ Changes ____________
 * merged with Controller.
 */

package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import josx.platform.rcx.Button;
import josx.platform.rcx.LCD;
import josx.platform.rcx.Sound;
import josx.platform.rcx.TextLCD;
import robot.Driver;
import robot.Movement;


public class GameProxy {
    private Driver driver;
    private LLCSocket link;
    private NetworkSocket net;
    private TransportSocket socket;
    private InputStream in;
    private OutputStream out;
    private int command, directions;
    private byte address;
    private String addressMessage = "Addr";
    
    /**
     * Creates a new instance of GameProxy
     */
    public GameProxy() {
        this.driver = new Driver();
        this.link = new LLCSocket();
        this.getAddress();
        this.net = new NetworkSocket(address,(byte)0,link.getInputStream(),link.getOutputStream());
        this.socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        this.in = socket.getInputStream();
        this.out = socket.getOutputStream();
        this.driver.calibrate();
        this.socket.setActive(true);
        this.run();
    }
    
    private void getAddress() {
        TextLCD.print(addressMessage);
        address = 0;
        while (!Button.RUN.isPressed()){
            if (Button.PRGM.isPressed()) {
                Sound.beep();
                ++address;
                if (address == 3) {
                    address = 0;
                }
                
                try {
                    this.driver.setSegment(address);
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
//                    Sound.buzz();
                }
            }
        }
        Sound.twoBeeps();
        ++address; // we add one, so the address will be between 1 and 3
    }
    
    private byte paths;
    
    public void run(){
        int turn_timer;
        while(true){
//            LCD.showNumber((int)Runtime.getRuntime().freeMemory());
//            this.driver.read();
//            if (Button.RUN.isPressed()) {
//                LCD.showNumber(12);
//                //command = GameCommands.FORWARD;
//                this.driver.forward();
//            } else if (Button.PRGM.isPressed()) {
//                LCD.showNumber(13);
//                //command = GameCommands.TURN_LEFT;
//                this.driver.turnLeft(false);
            if (Button.VIEW.isPressed()) {
//                this.driver.setSegment((byte)-1);
//                LCD.showNumber(9999);
//             
//                paths = driver.search(true);
                this.driver.search(false);
            } else if (Button.PRGM.isPressed()) {
                turn_timer = (int)System.currentTimeMillis();
                this.driver.turnLeftSearch();
                LCD.showNumber(((int)System.currentTimeMillis()) - turn_timer);
                this.driver.adjust((byte)2);
            } else if(Button.RUN.isPressed()) {
                while(true) {
                    this.driver.forward();
                    this.driver.search(true);
                }
            }
            else {
                command = -1;
                try {
                    command = in.read();                    
                } catch (Exception e) {
//                    Sound.buzz();
                }
                if (command != -1) {
                    //LCD.showNumber(command);
//                    LCD.showNumber(((command & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER) ? 1 : 2);
                    if (command == GameCommands.FORWARD) {
                        this.driver.forward();
                        this.respond(GameCommands.MOVE_DONE);

                    } else if (command == GameCommands.TURN_LEFT || command == (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER)) {
                        this.driver.turnLeft(((command & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER));
                        this.respond(GameCommands.MOVE_DONE);

                    } else if (command == GameCommands.TURN_RIGHT || command == (GameCommands.TURN_RIGHT | GameCommands.TURN_NUMBER)) {
                        this.driver.turnRight(((command & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER));
                        this.respond(GameCommands.MOVE_DONE);

                    } else if (command == (GameCommands.FORWARD | GameCommands.DISCOVER)) {
                        do{
                            this.driver.forward();
                            command = this.driver.search(true);
                        }while(command == -1);
                        this.respond(GameCommands.MOVE_DONE | command);
//                        this.respond(GameCommands.MOVE_DONE | GameCommands.LEFT | GameCommands.RIGHT);

                    } else if (command == (GameCommands.TURN_LEFT | GameCommands.DISCOVER) || command == (GameCommands.TURN_LEFT | GameCommands.DISCOVER | GameCommands.TURN_NUMBER)) {
                        do{
                            this.driver.turnLeft(((command & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER));
                            command = this.driver.search(true);
                        }while(command == -1);
                        this.respond(GameCommands.MOVE_DONE | command);
//                        this.respond(GameCommands.MOVE_DONE | GameCommands.LEFT | GameCommands.RIGHT);

                    } else if (command == (GameCommands.TURN_RIGHT | GameCommands.DISCOVER) || command == (GameCommands.TURN_RIGHT | GameCommands.DISCOVER | GameCommands.TURN_NUMBER)) {
                        do{
                        this.driver.turnRight(((command & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER));
                        command = this.driver.search(true);
                        }while(command == -1);
                        this.respond(GameCommands.MOVE_DONE | command);
                       // this.respond(GameCommands.MOVE_DONE | GameCommands.LEFT | GameCommands.RIGHT);

                    } else if (command == (GameCommands.SEARCH_NODE | GameCommands.DISCOVER)) {
                        do{
                            command = this.driver.search(true);
                        }while(command == -1);
                        this.respond(GameCommands.MOVE_DONE | command);
                        //this.respond(GameCommands.MOVE_DONE | GameCommands.LEFT | GameCommands.RIGHT);

                    } else if (command == GameCommands.LIGHT_ON) {
                        Movement.LightOn();

                    } else if (command == GameCommands.LIGHT_OFF) {
                        Movement.LightOff();

                    } else if (command == GameCommands.BEEP) {//only two beeps
                        Sound.beep();
                    } else if (command == GameCommands.RESET_DIRECTION) {
                        this.driver.resetDirection();
                    }
                }
            }
        }
    }    
    
//    public void pauseCommunication(){
//        socket.setActive(false);
//    }
//
//    public void startCommunication(){
//        socket.setActive(true);
//    }
    
    public void respond(int data){
        try {
            out.write(data);
        } catch (IOException ex) {
            
        }
        Sound.beep();
    }
    
    public static void main(String[] args) throws InterruptedException, IOException{
        GameProxy noget = new GameProxy();
    }
}
