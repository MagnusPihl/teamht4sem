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

public class Controller {
    LowRider ride = new LowRider();
    GameProxy tower = new GameProxy();
    private static Controller instance = new Controller();
    
    private int command = -1;
    private int directions = 0;
    private int lastCommand = 0x00;
    private int mode = 0x21;
    private int sensor1 = 0;
    private int sensor2 = 0;
    private int sensor3 = 0;
    private int minGreen = 0;
    private int maxGreen = 0;
    private int minBlack = 0;
    private int maxBlack = 0;
    private int address;
    private boolean addressing = true;
    
    private ButtonListener listner = new ButtonListener() {
        public void buttonPressed(Button button) {
            Controller ctr = Controller.getInstance();
            int address = 1;
            if (Button.VIEW.isPressed()) {
                Sound.beep();
                address--;
                ctr.setAddress(address);
            }else if (Button.PRGM.isPressed()) {
                Sound.beep();
                address++;
                ctr.setAddress(address);
            }else if (Button.RUN.isPressed()) {
                Sound.beep();
                ctr.setAddressing(false, address);
            }
        }
        public void buttonReleased(Button button) {
        }
    };
    
    
    /** Creates a new instance of Controller */
    public Controller() {
    }
    
    public void run(){
        this.address();
        while(true){
            command = tower.getcommand();
            if(command == 0x20){
                this.mode = 0x20;
                tower.moveDone();
            }else if(command == 0x21){
                this.mode = 0x21;
                tower.moveDone();
            }else if(command < 0x04 && mode == 0x21){
                this.move();
                tower.moveDone();
            }else if(command < 0x04 && mode == 0x20){
                this.discover();
            }else if(command == 0x10){
                this.flash();
                tower.moveDone();
            }else if(command == 0x11){
                this.lightOn();
                tower.moveDone();
            }else if(command == 0x12){
                this.lightOff();
                tower.moveDone();
            }else if(command == 0x13){
                this.beepOn();
                tower.moveDone();
            }else if(command == 0x14){
                this.beepOff();
                tower.moveDone();
            }else if(command == 0x30){
                ride.callibrate(sensor1, sensor2, sensor3, minGreen, maxGreen, minBlack, maxBlack);
                tower.moveDone();
            }else{
                
            }
        }
    }
    
    private void discover(){
        if(command != lastCommand){
            this.turn();
        }
        int diretions = ride.goToNext();
        tower.moveDone();
        tower.sendDirections(diretions);
    }
    
    private void move(){
        directions = tower.getDirections();
        if(command != lastCommand){
            this.turn();
        }
        if(directions == 10 || directions == 5){
            ride.goToGreen();
        }
        if(directions == 7 || directions == 11 || directions == 13 || directions == 14){
            this.tCross();
        }
        if(directions == 3 || directions == 12){
            if(command == 0x00 || command ==0x02){
                ride.goToLeftCorner();
            }else if(command == 0x01 || command ==0x03){
                ride.goToRightCorner();
            }
        }
        if(directions == 6 || directions == 9){
            if(command == 0x00 || command ==0x02){
                ride.goToRightCorner();
            }else if(command == 0x01 || command ==0x03){
                ride.goToLeftCorner();
            }
        }
        if(directions == 15){
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
        if(command == 0x00){
            if(lastCommand == 0x01){
                ride.left90();
            }else if(lastCommand == 0x03){
                ride.right90();
            }else if(lastCommand == 0x02){
                ride.turn180();
            }
        }else if(command == 0x01){
            if(lastCommand == 0x02){
                ride.left90();
            }else if(lastCommand == 0x00){
                ride.right90();
            }else if(lastCommand == 0x03){
                ride.turn180();
            }
        }else if(command == 0x02){
            if(lastCommand == 0x00){
                ride.turn180();
            }else if(lastCommand == 0x01){
                ride.right90();
            }else if(lastCommand == 0x03){
                ride.left90();
            }
        }else if(command == 0x03){
            if(lastCommand == 0x00){
                ride.left90();
            }else if(lastCommand == 0x01){
                ride.turn180();
            }else if(lastCommand == 0x02){
                ride.right90();
            }
        }
    }
    
    public static Controller getInstance() {
        return instance;
    }
    
    private void tCross() {
        if(directions == 7){
            if(command == 8){
                ride.goToCross();
            }else if(command == 4){
                ride.goToRightCorner();
            }else if(command == 1){
                ride.goToLeftCorner();
            }
        }else if(directions == 11){
            if(command == 8){
                ride.goToLeftCorner();
            }else if(command == 4){
                ride.goToCross();
            }else if(command == 1){
                ride.goToRightCorner();
            }
        }else if(directions == 13){
            if(command == 4){
                ride.goToLeftCorner();
            }else if(command == 2){
                ride.goToCross();
            }else if(command == 1){
                ride.goToRightCorner();
            }
        }else if(directions == 14){
            if(command == 8){
                ride.goToRightCorner();
            }else if(command == 2){
                ride.goToLeftCorner();
            }else if(command == 1){
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
    
    public void setAddressing(boolean addressing, int address){
        this.addressing = addressing;
        this.address = address;
    }
    
    public void setAddress(int address){
        this.address = address;
    }
    
    private void address(){
//        Button.RUN.addButtonListener(listner);
//        Button.PRGM.addButtonListener(listner);
//        Button.VIEW.addButtonListener(listner);
        while(addressing = true){
            Button.RUN.addButtonListener(listner);
            Button.PRGM.addButtonListener(listner);
            Button.VIEW.addButtonListener(listner);
            LCD.showNumber(address);
        }
    }
    
    public int getAddress(){
        return address;
    }
}