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

import communication.GameProxy;
import josx.platform.rcx.*;
import java.io.*;
import josx.rcxcomm.RCXPort;

public class Controller implements ButtonListener{
    LowRider ride = new LowRider();
    GameProxy tower;
    private static Controller instance = new Controller();
    
    private int command = -1;
    private int directions = 0;
    private int lastCommand = 0x00;
    private int sensor1 = 0;
    private int sensor2 = 0;
    private int sensor3 = 0;
    private int minGreen = 0;
    private int maxGreen = 0;
    private int minBlack = 0;
    private int maxBlack = 0;
    private int address = 0;
    private boolean addressing = true;
    
    public static int NORTH = 0x08;
    public static int SOUTH = 0x02;
    public static int WEST = 0x01;
    public static int EAST = 0x04;
    public static int MOVE_UP = 0x00;
    public static int MOVE_RIGHT = 0x01;
    public static int MOVE_DOWN = 0x02;
    public static int MOVE_LEFT = 0x03;
    public static int DISCOVER = 0x40;
    public static int DONE = 0x10;

    private int diretions;
    
   /** Creates a new instance of Controller */
    public Controller() {
    }
    
    public void run(){
        this.address();
        tower = new GameProxy(address);
        while(true){
            command = tower.getcommand();
            if(command == this.MOVE_DOWN || command == this.MOVE_LEFT || command == this.MOVE_RIGHT || command == this.MOVE_UP){
                LCD.showNumber(command);
                this.move();
                tower.moveDone(this.DONE);
        }else if(command == (this.DISCOVER & this.MOVE_UP) || command == (this.DISCOVER & this.MOVE_RIGHT) || command == (this.DISCOVER & this.MOVE_DOWN) || command == (this.DISCOVER & this.MOVE_LEFT)){
                this.discover();
            }else if(command == 0x10){
                this.flash();
                tower.moveDone(this.DONE);
            }else if(command == 0x11){
                this.lightOn();
                tower.moveDone(this.DONE);
            }else if(command == 0x12){
                this.lightOff();
                tower.moveDone(this.DONE);
            }else if(command == 0x13){
                this.beepOn();
                tower.moveDone(this.DONE);
            }else if(command == 0x14){
                this.beepOff();
                tower.moveDone(this.DONE);
            }else if(command == 0x30){
                ride.callibrate(sensor1, sensor2, sensor3, minGreen, maxGreen, minBlack, maxBlack);
                tower.moveDone(this.DONE);
            }else if(command == 0x20){
                diretions = ride.searchNode();
                tower.moveDone(this.DONE | diretions);
            }else{
                
            }
        }
    }
    
    private void discover(){
        if(command != lastCommand){
            this.turn();
        }
        diretions = ride.goToNext();
        tower.moveDone(this.DONE | diretions);
    }
    
    private void move(){
        TextLCD.print("move");
        directions = tower.getDirections();
        if(command != lastCommand){
            this.turn();
        }
        if(directions == (this.NORTH | this.SOUTH) || directions == (this.EAST | this.WEST)){
            TextLCD.print("run1");
            ride.goToGreen();
        }
        if(directions == (this.EAST | this.WEST | this.SOUTH) || directions == (this.NORTH | this.SOUTH | this.WEST) || directions == (this.EAST | this.WEST | this.NORTH) || directions == (this.NORTH | this.SOUTH | this.EAST)){
            this.tCross();
        }
        if(directions == (this.SOUTH | this.WEST) || directions == (this.NORTH | this.EAST)){
            if(command == this.MOVE_UP || command == this.MOVE_DOWN){
                ride.goToLeftCorner();
            }else if(command == this.MOVE_RIGHT || command == this.MOVE_LEFT){
                ride.goToRightCorner();
            }
        }
        if(directions == (this.SOUTH | this.EAST) || directions == (this.NORTH | this.WEST)){
            if(command == this.MOVE_UP || command == this.MOVE_DOWN){
                ride.goToRightCorner();
            }else if(command == this.MOVE_RIGHT || command == this.MOVE_LEFT){
                ride.goToLeftCorner();
            }
        }
        if(directions == (this.SOUTH | this.EAST | this.NORTH | this.WEST)){
            ride.goToCross();
        }
        lastCommand = command;
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
        if(command == this.MOVE_UP || command == (this.DISCOVER | this.MOVE_UP)){
            if(lastCommand == this.MOVE_RIGHT || lastCommand == (this.DISCOVER | this.MOVE_RIGHT)){
                ride.left90();
            }else if(lastCommand == this.MOVE_LEFT || lastCommand == (this.DISCOVER | this.MOVE_LEFT)){
                ride.right90();
            }else if(lastCommand == this.MOVE_DOWN || lastCommand == (this.DISCOVER | this.MOVE_DOWN)){
                ride.turn180();
            }
        }else if(command == this.MOVE_RIGHT || command == (this.DISCOVER | this.MOVE_RIGHT)){
            if(lastCommand ==  this.MOVE_DOWN || lastCommand == (this.DISCOVER | this.MOVE_DOWN)){
                ride.left90();
            }else if(lastCommand == this.MOVE_UP || lastCommand == (this.DISCOVER | this.MOVE_UP)){
                ride.right90();
            }else if(lastCommand == this.MOVE_LEFT || lastCommand == (this.DISCOVER | this.MOVE_LEFT)){
                ride.turn180();
            }
        }else if(command == this.MOVE_DOWN || lastCommand == (this.DISCOVER | this.MOVE_DOWN)){
            if(lastCommand == this.MOVE_UP || lastCommand == (this.DISCOVER | this.MOVE_UP)){
                ride.turn180();
            }else if(lastCommand == this.MOVE_LEFT || lastCommand == (this.DISCOVER | this.MOVE_LEFT)){
                ride.right90();
            }else if(lastCommand == this.MOVE_RIGHT || lastCommand == (this.DISCOVER | this.MOVE_RIGHT)){
                ride.left90();
            }
        }else if(command == this.MOVE_LEFT || lastCommand == (this.DISCOVER | this.MOVE_LEFT)){
            if(lastCommand == this.MOVE_UP || lastCommand == (this.DISCOVER | this.MOVE_UP)){
                ride.left90();
            }else if(lastCommand == this.MOVE_RIGHT || lastCommand == (this.DISCOVER | this.MOVE_LEFT)){
                ride.turn180();
            }else if(lastCommand == this.MOVE_DOWN || lastCommand == (this.DISCOVER | this.MOVE_DOWN)){
                ride.right90();
            }
        }
    }
    
    public static Controller getInstance() {
        return instance;
    }
    
    private void tCross() {
        if(directions == (this.EAST | this.WEST | this.SOUTH)){
            if(command == this.MOVE_UP){
                ride.goToCross();
            }else if(command == this.MOVE_RIGHT){
                ride.goToRightCorner();
            }else if(command == this.MOVE_LEFT){
                ride.goToLeftCorner();
            }
        }else if(directions == (this.NORTH | this.SOUTH | this.WEST)){
            if(command == this.MOVE_UP){
                ride.goToLeftCorner();
            }else if(command == this.MOVE_RIGHT){
                ride.goToCross();
            }else if(command == this.MOVE_DOWN){
                ride.goToRightCorner();
            }
        }else if(directions == (this.EAST | this.WEST | this.NORTH)){
            if(command == this.MOVE_RIGHT){
                ride.goToLeftCorner();
            }else if(command == this.MOVE_DOWN){
                ride.goToCross();
            }else if(command == this.MOVE_LEFT){
                ride.goToRightCorner();
            }
        }else if(directions == (this.NORTH | this.SOUTH | this.EAST)){
            if(command == this.MOVE_UP){
                ride.goToRightCorner();
            }else if(command == this.MOVE_DOWN){
                ride.goToLeftCorner();
            }else if(command == this.MOVE_LEFT){
                ride.goToCross();
            }
        }
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
        TextLCD.print("run");
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
    
    public int getAddress(){
        return address;
    }
}