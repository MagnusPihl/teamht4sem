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
import josx.platform.rcx.Sound;
import josx.platform.rcx.TextLCD;
import robot.Drive;
import robot.LowRider;


public class GameProxy implements ButtonListener {
//    Drive ride = new Drive();
    LowRider ride = new LowRider();
    LLCSocket link = new LLCSocket();
    NetworkSocket net;
    TransportSocket socket;
    InputStream in;
    OutputStream out;
    private int command = -1;
    private int directions = -1;
    private int sensor1 = -1;
    private int sensor2 = -1;
    private int sensor3 = -1;
    private int maxGreen = -1;
    private int minGreen = -1;
    private int maxBlack = -1;
    private int minBlack = -1;
    private int address = 0;
    private boolean addressing = true;
    
    /**
     * Creates a new instance of GameProxy
     */
    public GameProxy() {
        this.address();
    }
    
    private void init(){
        net = new NetworkSocket(address,0,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        socket.setActive(true);
        this.run();
    }
    
    public void run(){
        while(true){
            LCD.showNumber(command);
            this.getcommand();
            if(command == GameCommands.FORWARD){
                this.stopThread();
                //ride.Forward(directions);
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
                //ride.Forward(directions);
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
                //ride.Forward(directions);
                ride.run(directions,command);
                this.startThread();
                this.sendMoveDone(GameCommands.MOVE_DONE);
//              ******************************************
            }else if(command == (GameCommands.MOVE_UP_DISCOVER) || command == (GameCommands.MOVE_RIGHT_DISCOVER) || command == (GameCommands.MOVE_DOWN_DISCOVER) || command == (GameCommands.MOVE_LEFT_DISCOVER)){
                this.stopThread();
                //directions = ride.goToNext();
                this.startThread();
                this.sendMoveDone(GameCommands.MOVE_DONE | directions);
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
        LCD.showNumber(command);
        if(command <= (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER) && command > GameCommands.NOP){
            while(directions == -1){
                try {
                    directions = in.read();
                } catch (IOException ex) {
                    
                }
            }
        }
        // lav evt. noget timeout here.
        if(command == GameCommands.CALIBRATE){
            sensor1 = -1;
            sensor2 = -1;
            sensor3 = -1;
            maxGreen = -1;
            minGreen = -1;
            maxBlack = -1;
            minBlack = -1;
            while(sensor1 == -1){
                try {
                    sensor1 = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(sensor2 == -1){
                try {
                    sensor2 = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(sensor3 == -1){
                try {
                    sensor3 = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(minGreen == -1){
                try {
                    minGreen = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(maxGreen == -1){
                try {
                    maxGreen = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(minBlack == -1){
                try {
                    minBlack = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(maxBlack == -1){
                try {
                    maxBlack = in.read();
                } catch (IOException ex) {
                    
                }
            }
        }
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
    
    private void address(){
        Button.RUN.addButtonListener(this);
        Button.PRGM.addButtonListener(this);
        Button.VIEW.addButtonListener(this);
        while(addressing == true){
            LCD.showNumber(address+1);
        }
    }
    
    public void buttonPressed(Button button) {
        if (Button.VIEW.isPressed() && addressing == true) {
            
        }else if (Button.PRGM.isPressed() && addressing == true) {
            Sound.beep();
            address++;
            address = address%3;
        }else if (Button.RUN.isPressed() && addressing == true) {
            Sound.twoBeeps();
            address++;
            addressing = false;
            init();
        }
    }
    public void buttonReleased(Button button) {
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
}
