/*
 * Controller.java
 *
 * Created on 13. april 2007, 11:04
 *
 * Company: HT++
 *
 * @author thh
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * thh @ 13. april 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package robot;

import communication.GameCommands;
import communication.GameProxy;
import josx.platform.rcx.*;
import java.io.*;

public class Controller implements ButtonListener{
    //Drive ride = new Drive();
    LowRider ride = new LowRider();
    GameProxy tower;
    private static Controller instance = new Controller();
    
    private int command = -1;
    private int directions = 0;
    private int lastCommand = GameCommands.MOVE_UP;
    private int sensor1 = 0;
    private int sensor2 = 0;
    private int sensor3 = 0;
    private int minGreen = 0;
    private int maxGreen = 0;
    private int minBlack = 0;
    private int maxBlack = 0;
    private int address = 0;
    private boolean addressing = true;
    
   /** Creates a new instance of Controller */
    private Controller() {
    }
    
    public void run(){
        this.address();
        tower = new GameProxy(address);
        while(true){
            TextLCD.print("get");
            command = tower.getcommand();
            if(command == GameCommands.MOVE_DOWN || command == GameCommands.MOVE_LEFT || command == GameCommands.MOVE_RIGHT || command == GameCommands.MOVE_UP){
                this.move();
                tower.sendMoveDone(GameCommands.MOVE_DONE);
        }else if(command == (GameCommands.DISCOVER | GameCommands.MOVE_UP) || command == (GameCommands.DISCOVER | GameCommands.MOVE_RIGHT) || command == (GameCommands.DISCOVER | GameCommands.MOVE_DOWN) || command == (GameCommands.DISCOVER | GameCommands.MOVE_LEFT)){
                this.discover();
            }else if(command == 0x10){// not implemented yet
                this.flash();
            }else if(command == GameCommands.LIGHT_ON){
                this.lightOn();
            }else if(command == GameCommands.LIGHT_OFF){
                this.lightOff();
            }else if(command == GameCommands.BEEP){
                this.beepOn();//only two beeps
            }else if(command == 0x14){// not implemented yet
                this.beepOff();
            }else if(command == GameCommands.CALIBRATE){
                //ride.callibrate(sensor1, sensor2, sensor3, minGreen, maxGreen, minBlack, maxBlack);
            }else if(command == GameCommands.SEARCH_NODE){
                //directions = ride.searchNode();
                tower.sendMoveDone(GameCommands.MOVE_DONE | directions);
            }else{
                
            }
        }
    }
    
    private void discover(){
        if(command != lastCommand){
            this.turn();
        }
        //directions = ride.goToNext();
        tower.sendMoveDone(GameCommands.MOVE_DONE | directions);
    }
    
    private void move(){
        tower.stopThread();
        TextLCD.print("move");
        if(command != lastCommand){
            this.turn();
        }
        directions = tower.getDirections();
        //ride.Forward(directions);
        ride.run(directions,command);
        lastCommand = command;
        tower.startThread();
    }
    
    public void setCalibrationValues(int sensor1, int sensor2, int sensor3, int minGreen, int maxGreen, int minBlack, int maxBlack){
        this.sensor1 = sensor1;
        this.sensor2 = sensor2;
        this.sensor3 = sensor3;
        this.minGreen = minGreen;
        this.maxGreen = maxGreen;
        this.minBlack = minBlack;
        this.maxBlack = maxBlack;
    }
    
    private void turn(){
        if(command == GameCommands.MOVE_UP || command == (GameCommands.DISCOVER | GameCommands.MOVE_UP)){
            if(lastCommand == GameCommands.MOVE_RIGHT || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_RIGHT)){
                ride.TurnLeft90();
            }else if(lastCommand == GameCommands.MOVE_LEFT || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_LEFT)){
                ride.TurnRight90();
            }else if(lastCommand == GameCommands.MOVE_DOWN || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_DOWN)){
                this.turn180();
            }
        }else if(command == GameCommands.MOVE_RIGHT || command == (GameCommands.DISCOVER | GameCommands.MOVE_RIGHT)){
            if(lastCommand ==  GameCommands.MOVE_DOWN || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_DOWN)){
                ride.TurnLeft90();
            }else if(lastCommand == GameCommands.MOVE_UP || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_UP)){
                ride.TurnRight90();
            }else if(lastCommand == GameCommands.MOVE_LEFT || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_LEFT)){
                this.turn180();
            }
        }else if(command == GameCommands.MOVE_DOWN || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_DOWN)){
            if(lastCommand == GameCommands.MOVE_UP || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_UP)){
                this.turn180();
            }else if(lastCommand == GameCommands.MOVE_LEFT || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_LEFT)){
                ride.TurnLeft90();
            }else if(lastCommand == GameCommands.MOVE_RIGHT || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_RIGHT)){
                ride.TurnRight90();
            }
        }else if(command == GameCommands.MOVE_LEFT || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_LEFT)){
            if(lastCommand == GameCommands.MOVE_UP || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_UP)){
                ride.TurnLeft90();
            }else if(lastCommand == GameCommands.MOVE_RIGHT || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_LEFT)){
                this.turn180();
            }else if(lastCommand == GameCommands.MOVE_DOWN || lastCommand == (GameCommands.DISCOVER | GameCommands.MOVE_DOWN)){
                ride.TurnRight90();
            }
        }
    }
    
    public static Controller getInstance() {
        return instance;
    }
    
    private void flash() {
        //throw new UnsupportedOperationException("Not yet implemented");
    }
    
    private void lightOn() {
        Motor.B.setPower(7);
        Motor.B.forward();
    }
    
    private void lightOff() {
        Motor.B.stop();
    }
    
    private void beepOn() {
        Sound.twoBeeps();
    }
    
    private void beepOff() {
        //throw new UnsupportedOperationException("Not yet implemented");
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
        }
    }
    public void buttonReleased(Button button) {
    }
    
    public static void main(String[] args) throws InterruptedException, IOException{
        Controller noget = Controller.getInstance();
        noget.run();
    }

    private void turn180() {
        if(directions == (GameCommands.UP | GameCommands.DOWN) || directions == (GameCommands.LEFT | GameCommands.RIGHT)){
            ride.TurnLeft90();
        }else if(directions == (GameCommands.UP | GameCommands.RIGHT) || directions == (GameCommands.DOWN | GameCommands.LEFT)){
            if(command == GameCommands.MOVE_UP | command == GameCommands.MOVE_DOWN){
                ride.TurnRight90();
            }else if(command == GameCommands.MOVE_RIGHT | command == GameCommands.MOVE_LEFT){
                ride.TurnLeft90();
            }
        }else if(directions == (GameCommands.UP | GameCommands.LEFT) || directions == (GameCommands.DOWN | GameCommands.RIGHT)){
            if(command == GameCommands.MOVE_UP | command == GameCommands.MOVE_DOWN){
               ride.TurnLeft90(); 
            }else if(command == GameCommands.MOVE_RIGHT | command == GameCommands.MOVE_LEFT){
                ride.TurnRight90();
            }
        }else if(directions == (GameCommands.UP | GameCommands.DOWN |GameCommands.LEFT | GameCommands.RIGHT)){
            ride.TurnRight90();
            ride.TurnRight90();
        }else if(directions == (GameCommands.UP | GameCommands.DOWN | GameCommands.RIGHT)){
            if(command == GameCommands.MOVE_UP){
                ride.TurnRight90();
            }else if(command == GameCommands.MOVE_DOWN){
                ride.TurnLeft90();
            }else if(command == GameCommands.MOVE_RIGHT){
                ride.TurnLeft90();
                ride.TurnLeft90();
            }
        }else if(directions == (GameCommands.UP | GameCommands.DOWN | GameCommands.LEFT)){
            if(command == GameCommands.MOVE_UP){
                ride.TurnLeft90();
            }else if(command == GameCommands.MOVE_DOWN){
                ride.TurnRight90();
        }else if(command == GameCommands.MOVE_LEFT){
                ride.TurnLeft90();
                ride.TurnLeft90();
            }
        }else if(directions == (GameCommands.RIGHT | GameCommands.DOWN | GameCommands.LEFT)){
            if(command == GameCommands.MOVE_LEFT){
                ride.TurnLeft90();
            }else if(command == GameCommands.MOVE_LEFT){
                ride.TurnRight90();
        }else if(command == GameCommands.MOVE_DOWN){
                ride.TurnLeft90();
                ride.TurnLeft90();
            }
        }else if(directions == (GameCommands.UP | GameCommands.RIGHT | GameCommands.LEFT)){
            if(command == GameCommands.MOVE_RIGHT){
                ride.TurnLeft90();
            }else if(command == GameCommands.MOVE_LEFT){
                ride.TurnRight90();
        }else if(command == GameCommands.MOVE_UP){
                ride.TurnLeft90();
                ride.TurnLeft90();
            }
        }
    }
}