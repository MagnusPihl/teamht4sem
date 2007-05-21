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
import josx.platform.rcx.Motor;
import josx.platform.rcx.Segment;
import josx.platform.rcx.Sound;
import josx.platform.rcx.TextLCD;
import robot.*;


public class GameProxy {
    NewDrive driver = new NewDrive();
    LLCSocket link = new LLCSocket();
    NetworkSocket net;
    TransportSocket socket;
    InputStream in;
    OutputStream out;
    private int command = -1;
    private int directions = -1;
    private byte address = 0;
    private boolean addressDone = false;
    private boolean[] btnRUNbuffer  = new boolean[3];
    private boolean[] btnPGRMbuffer = new boolean[3];
    private byte btnBufferIndex = 0;
    
    private int h =0;
    
    /**
     * Creates a new instance of GameProxy
     */
    public GameProxy() {
        this.setAddress();
        //this.address();
        init();
    }
    
    
    private void setAddress() {
        addressDone = false;
        btnBufferIndex = 0;
        TextLCD.print("Addr");
        while (!addressDone){
            btnRUNbuffer[2] = btnRUNbuffer[1];
            btnRUNbuffer[1] = btnRUNbuffer[0];
            btnRUNbuffer[0] = Button.RUN.isPressed();
            btnPGRMbuffer[2] = btnPGRMbuffer[1];
            btnPGRMbuffer[1] = btnPGRMbuffer[0];
            btnPGRMbuffer[0] = Button.PRGM.isPressed();
            
            if(!btnPGRMbuffer[2] && btnPGRMbuffer[1] && btnPGRMbuffer[0]){
                Sound.beep();
                ++address;
                if (address == 3)
                    address = 0;
            }
            if (!btnRUNbuffer[2] && btnRUNbuffer[1] && btnRUNbuffer[0]){
                Sound.twoBeeps();
                ++address; // we add one, so the address will be between 1 and 3
                addressDone = true;
                TextLCD.print("HT++ ");
                break;
            }
            try {
                UpdateAddressSegments();
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Sound.buzz();
            }
        }
        //driver.calibrate();
    }
    
    private void UpdateAddressSegments() {
        LCD.clearSegment(Segment.SENSOR_1_ACTIVE);
        LCD.clearSegment(Segment.SENSOR_2_ACTIVE);
        LCD.clearSegment(Segment.SENSOR_3_ACTIVE);
        LCD.clearSegment(Segment.SENSOR_1_VIEW);
        LCD.clearSegment(Segment.SENSOR_2_VIEW);
        LCD.clearSegment(Segment.SENSOR_3_VIEW);
        
        LCD.setSegment(address==0?Segment.SENSOR_1_ACTIVE: (address==1?Segment.SENSOR_2_ACTIVE:Segment.SENSOR_3_ACTIVE));
        LCD.setSegment(address==0?Segment.SENSOR_1_VIEW: (address==1?Segment.SENSOR_2_VIEW:Segment.SENSOR_3_VIEW));
        LCD.refresh();
    }
    
    private void init(){
        net = new NetworkSocket(address,(byte)0,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        socket.setActive(true);
//        ride = new Drive(address);
        this.run();
    }
    
    private byte paths;
    
    public void run(){
        //((driver.calibrate();
        while(true){
            //this.getcommand();
            if (Button.RUN.isPressed()) {
                LCD.showNumber(12);
                command = GameCommands.FORWARD;
                driver.forward();
            } else if (Button.PRGM.isPressed()) {
                LCD.showNumber(13);
                command = GameCommands.TURN_LEFT;
                driver.turnLeft(false);
            } else if (Button.VIEW.isPressed()) {
                LCD.clearSegment(Segment.SENSOR_1_VIEW);
                LCD.clearSegment(Segment.SENSOR_2_VIEW);
                LCD.clearSegment(Segment.SENSOR_3_VIEW);
                LCD.showNumber(9999);
                
                paths = driver.search();
                if((paths & GameCommands.TURN_LEFT) > 0)
                    LCD.setSegment(Segment.SENSOR_1_VIEW);
                if((paths & GameCommands.FORWARD) > 0)
                    LCD.setSegment(Segment.SENSOR_2_VIEW);
                if((paths & GameCommands.TURN_RIGHT) > 0)
                    LCD.setSegment(Segment.SENSOR_3_VIEW);
                LCD.showNumber(paths);
            } else {
                command = GameCommands.NOP;
            }
            
            //LCD.showNumber(command);
            
            /*if (command == GameCommands.FORWARD) {
                driver.forward();
                this.respond(GameCommands.MOVE_DONE);
             
            } else if (command == GameCommands.TURN_LEFT || command == (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER)) {
                driver.turnLeft(((command & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER));
                this.respond(GameCommands.MOVE_DONE);
             
            } else if (command == GameCommands.TURN_RIGHT || command == (GameCommands.TURN_RIGHT | GameCommands.TURN_NUMBER)) {
                driver.turnRight(((command & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER));
                this.respond(GameCommands.MOVE_DONE);
             
            } else if (command == (GameCommands.FORWARD | GameCommands.DISCOVER)) {
                driver.forward();
                this.discover();
                this.respond(GameCommands.MOVE_DONE | directions);
             
            } else if (command == (GameCommands.TURN_LEFT | GameCommands.DISCOVER) || command == (GameCommands.TURN_LEFT | GameCommands.DISCOVER | GameCommands.TURN_NUMBER)) {
                driver.turnLeft(((command & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER));
                this.discover();
                this.respond(GameCommands.MOVE_DONE | directions);
             
            } else if (command == (GameCommands.TURN_RIGHT | GameCommands.DISCOVER) || command == (GameCommands.TURN_RIGHT | GameCommands.DISCOVER | GameCommands.TURN_NUMBER)) {
                driver.turnRight(((command & GameCommands.TURN_NUMBER) == GameCommands.TURN_NUMBER));
                this.discover();
                this.respond(GameCommands.MOVE_DONE | directions);
             
            } else if (command == GameCommands.LIGHT_ON) {
                Movement.LightOn();
             
            } else if (command == GameCommands.LIGHT_OFF) {
                Movement.LightOff();
             
            } else if (command == GameCommands.BEEP){//only two beeps
                Sound.twoBeeps();
             
            } else if (command == GameCommands.CALIBRATE) {
                //ride.callibrate(sensor1, sensor2, sensor3, minGreen, maxGreen, minBlack, maxBlack);
             
            } else if (command == GameCommands.SEARCH_NODE) {
                this.discover();
                this.respond(GameCommands.MOVE_DONE | directions);
            }*/
        }
    }
    
    private void getcommand(){
        TextLCD.print("recvB");
        command = -1;
        directions = -1;
        while(command == -1){
            try {
                command = in.read();
            } catch (IOException ex) {
                
            }
        }
        LCD.showNumber(command);
        if(command <= (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER) && command > GameCommands.NOP){
            while(directions == -1){
                try {
                    directions = in.read();
                } catch (IOException ex) {
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
    
    private void discover() {
        if(h < 9){
            directions = (GameCommands.FORWARD | GameCommands.TURN_NUMBER);
            h++;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
            }
        }else{
//            h = socket.random.nextInt()&0x7F;
//            LCD.showNumber(h);
//            if(h < 20 ){
            directions = (GameCommands.TURN_NUMBER | GameCommands.TURN_RIGHT);
//            }else if(h < 40 && h > 20){
//                directions = (GameCommands.TURN_NUMBER | GameCommands.TURN_LEFT);
//            }else if((h < 60 && h > 40)){
//                directions = (GameCommands.TURN_NUMBER | GameCommands.TURN_RIGHT | GameCommands.TURN_LEFT);
//            }else if(h < 70 && h > 60){
//                directions = (GameCommands.TURN_NUMBER | GameCommands.TURN_RIGHT | GameCommands.TURN_LEFT | GameCommands.FORWARD);
//            }else if(h < 80 && h > 70){
//                directions = (GameCommands.TURN_NUMBER | GameCommands.TURN_RIGHT | GameCommands.FORWARD);
//            }else if(h < 90 && h > 80){
//                directions = (GameCommands.TURN_NUMBER | GameCommands.TURN_LEFT | GameCommands.FORWARD);
//            }else{
////                directions = (GameCommands.FORWARD | GameCommands.TURN_NUMBER);
//            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ex) {
            }
            h = 0;
        }
    }
}
