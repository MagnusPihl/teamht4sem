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
import josx.platform.rcx.ButtonListener;
import josx.platform.rcx.LCD;
import josx.platform.rcx.Motor;
import josx.platform.rcx.Segment;
import josx.platform.rcx.Sound;
import josx.platform.rcx.TextLCD;
import robot.Drive;
import robot.LowRider;


public class GameProxy {
//    Drive ride;
    LowRider ride = new LowRider();
    LLCSocket link = new LLCSocket();
    NetworkSocket net;
    TransportSocket socket;
    InputStream in;
    OutputStream out;
    private int command = -1;
    private int directions = -1;
    private int address = 0;
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
                TextLCD.print("    ");
                break;
            }
            try {
                UpdateAddressSegments();
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Sound.buzz();
            }
        }
    }
    
    private void UpdateAddressSegments()
    {
        //SENSOR_0_VIEW
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
        net = new NetworkSocket(address,0,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        socket.setActive(true);
//        ride = new Drive(address);
        this.run();
    }
    
    public void run(){
        while(true){
            LCD.showNumber(command);
            this.getcommand();
            if(command == GameCommands.FORWARD){
                this.stopThread();
//                ride.Forward(directions);
                ride.run(directions,command);
                this.startThread();
                this.sendMoveDone(GameCommands.MOVE_DONE);
//              ******************************************
            }else if(command == GameCommands.TURN_LEFT || command == (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER)){
                this.stopThread();
                if(command == (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER)){
                    ride.TurnLeft90();
                    ride.TurnLeft90();
                }else{
                    ride.TurnLeft90();
                }
//                ride.Forward(directions);
                ride.run(directions,command);
                this.startThread();
                this.sendMoveDone(GameCommands.MOVE_DONE);
//              ******************************************
            }else if(command == GameCommands.TURN_RIGHT || command == (GameCommands.TURN_RIGHT | GameCommands.TURN_NUMBER)){
                this.stopThread();
                if(command == (GameCommands.TURN_RIGHT | GameCommands.TURN_NUMBER)){
                    ride.TurnRight90();
                    ride.TurnRight90();
                }else{
                    ride.TurnRight90();
                }
//                ride.Forward(directions);
                ride.run(directions,command);
                this.startThread();
                this.sendMoveDone(GameCommands.MOVE_DONE);
//              ******************************************
            }else if(command == (GameCommands.FORWARD | GameCommands.DISCOVER)){
                this.stopThread();
                this.discover();
                this.startThread();
                this.sendMoveDone(GameCommands.MOVE_DONE | directions);
//              *******************************************************
            }else if(command == (GameCommands.TURN_LEFT | GameCommands.DISCOVER) || command == (GameCommands.TURN_LEFT | GameCommands.DISCOVER | GameCommands.TURN_NUMBER)){
                this.stopThread();
                if(command == (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER | GameCommands.DISCOVER)){
                    ride.TurnLeft90();
                    ride.TurnLeft90();
                }else{
                    ride.TurnLeft90();
                }
                this.discover();
                this.startThread();
                this.sendMoveDone(GameCommands.MOVE_DONE | directions);
//              *******************************************************
            }else if(command == (GameCommands.TURN_RIGHT | GameCommands.DISCOVER) || command == (GameCommands.TURN_RIGHT | GameCommands.DISCOVER | GameCommands.TURN_NUMBER)){
                this.stopThread();
                if(command == (GameCommands.TURN_RIGHT | GameCommands.TURN_NUMBER | GameCommands.DISCOVER)){
                    ride.TurnRight90();
                    ride.TurnRight90();
                }else{
                    ride.TurnRight90();
                }
                this.discover();
                this.startThread();
                this.sendMoveDone(GameCommands.MOVE_DONE | directions);
//              *******************************************************
            }else if(command == GameCommands.LIGHT_ON){
                this.lightOn();
            }else if(command == GameCommands.LIGHT_OFF){
                this.lightOff();
            }else if(command == GameCommands.BEEP){
                Sound.twoBeeps();//only two beeps
            }else if(command == GameCommands.CALIBRATE){
                //ride.callibrate(sensor1, sensor2, sensor3, minGreen, maxGreen, minBlack, maxBlack);
            }else if(command == GameCommands.SEARCH_NODE){
                this.stopThread();
                //directions = ride.searchNode();
                this.startThread();
                this.sendMoveDone(GameCommands.MOVE_DONE | directions);
            }else{
                
            }
        }
    }
    
    private void getcommand(){
        command = -1;
        directions = -1;
        while(command == -1){
            try {
                command = in.read();
            } catch (IOException ex) {
                
            }
        }
        if(command <= (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER) && command > GameCommands.NOP){
            while(directions == -1){
                try {
                    directions = in.read();
                } catch (IOException ex) {
                }
            }
            LCD.showNumber(directions + 700);
        }
        //TextLCD.print("step3");
    }
    
    public void stopThread(){
        socket.setActive(false);
    }
    
    public void startThread(){
        socket.setActive(true);
    }
    
    public void sendMoveDone(int move){
        TextLCD.print("Step4");
        try {
            out.write(move);
        } catch (IOException ex) {
            
        }
        Sound.beep();
    }
    
    private void lightOn() {
        Motor.B.setPower(7);
        Motor.B.forward();
    }
    
    private void lightOff() {
        Motor.B.stop();
    }
    
    public static void main(String[] args) throws InterruptedException, IOException{
        GameProxy noget = new GameProxy();
    }

    private void discover() {
        if(h < 4){
            directions = (GameCommands.FORWARD | GameCommands.TURN_NUMBER);
            h++;
        }else{
            h = 0;
            directions = (GameCommands.FORWARD | GameCommands.TURN_NUMBER | GameCommands.TURN_RIGHT);;
        }
    }
}
