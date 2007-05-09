package robot;
import communication.GameCommands;
import josx.platform.rcx.Button;
import josx.platform.rcx.LCD;
import josx.platform.rcx.Motor;
import josx.platform.rcx.Sensor;
import josx.platform.rcx.Sound;
/*
 * LowRider.java
 *
 * Created on 25. marts 2007, 12:31
 *
 * Company: HT++
 *
 * @author thh
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * thh @ 25. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

public class LowRider {
    
    
    /** Creates a new instance of LowRider */
    public LowRider() {
        
    }
    
    public void run(int directions, int command){
        if(directions == (GameCommands.UP | GameCommands.DOWN) || directions == (GameCommands.RIGHT | GameCommands.LEFT)){
            this.goToGreen();
        }else if(directions == (GameCommands.RIGHT | GameCommands.LEFT | GameCommands.DOWN) || directions == (GameCommands.UP | GameCommands.DOWN | GameCommands.LEFT) || directions == (GameCommands.RIGHT | GameCommands.LEFT | GameCommands.UP) || directions == (GameCommands.UP | GameCommands.DOWN | GameCommands.RIGHT)){
            this.goToGreen();
        }else if(directions == (GameCommands.DOWN | GameCommands.LEFT) || directions == (GameCommands.UP | GameCommands.RIGHT)){
            if(command == GameCommands.MOVE_UP || command == GameCommands.MOVE_DOWN){
                this.goToLeftCorner();
            }else if(command == GameCommands.MOVE_RIGHT || command == GameCommands.MOVE_LEFT){
                this.goToRightCorner();
            }
        }else if(directions == (GameCommands.DOWN | GameCommands.RIGHT) || directions == (GameCommands.UP | GameCommands.LEFT)){
            if(command == GameCommands.MOVE_UP || command == GameCommands.MOVE_DOWN){
                this.goToRightCorner();
            }else if(command == GameCommands.MOVE_RIGHT || command == GameCommands.MOVE_LEFT){
                this.goToLeftCorner();
            }
        }else if(directions == (GameCommands.DOWN | GameCommands.RIGHT | GameCommands.UP | GameCommands.LEFT)){
            this.goToCross();
        }else{
         Sound.buzz();   
        }
    }
    
    public void callibrate(int sensor1, int sensor2, int sensor3, int minGreen, int maxGreen, int minBlack, int maxBlack){
        
    }
    
    public int searchNode(){
        return 1;
    }
    
    public void goToGreen(){
        this.forward();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            
        }
        this.stop();
    }

    private void forward() {
        Motor.A.forward();
        Motor.C.backward();
    }
    
    private void backward() {
        Motor.C.backward();
        Motor.A.forward();
    }
    
    private void left() {
        Motor.A.stop();
        Motor.C.backward();
    }
    
    private void right() {
        Motor.C.stop();
        Motor.A.forward();
    }
    
    
    public int goToNext(){
        return 0;
    }
    
//    public void goToTCross(){
//        this.forward();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException ex) {
//            
//        }
//        this.stop();
//    }
    
    public void goToLeftCorner(){
        this.forward();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            
        }
        this.stop();
    }
    
    public void goToRightCorner(){
        this.forward();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            
        }
        this.stop();
    }
    
    public void goToCross(){
        this.forward();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            
        }
        this.stop();
    }
    
    public void TurnLeft90(){
        this.left();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            
        }
        this.stop();
    }
    
    public void TurnRight90(){
        this.right();
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            
        }
        this.stop();
    }
    
    public void turn180(){
        this.right();
        try {
            Thread.sleep(3800);
        } catch (InterruptedException ex) {
            
        }
        this.stop();
    }
    
        
    public void stop() {
        Motor.A.stop();
        Motor.C.stop();
    }
    
    private void sharpRight() {
        Motor.A.forward();
        Motor.C.forward();
    }
    
    private void sharpLeft() {
        Motor.A.backward();
        Motor.C.backward();
    }
      
}