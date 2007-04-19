package robot;
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
        Motor.A.backward();
        Motor.C.forward();
    }
    
    private void left() {
        Motor.A.stop();
        Motor.C.backward();
    }
    
    private void right() {
        Motor.C.stop();
        Motor.A.forward();
    }
    
    public void flash(){
        
    }
    
    public void lightOn(){
        
    }
    
    public void lightOff(){
        
    }
    
    public void beepOn(){
        
    }
    
    public void beepOff(){
        
    }
    
    public int goToNext(){
        return 0;
    }
    
    public void goToTCross(){
        
    }
    
    public void goToLeftCorner(){
        
    }
    
    public void goToRightCorner(){
        
    }
    
    public void goToCross(){
        
    }
    
    public void left90(){
        this.left();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            
        }
        this.stop();
    }
    
    public void right90(){
        this.right();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            
        }
        this.stop();
    }
    
    public void turn180(){
        this.right();
        try {
            Thread.sleep(2000);
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
