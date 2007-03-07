/*
 * Robert.java
 *
 * Created on 14. februar 2007, 11:10
 *
 */

import josx.platform.rcx.*;

/**
 *
 * @author Christian Holm, 5601
 */
public class Robert {/*1 implements ButtonListener {
                      
    static boolean stop = false;
    int resetValue = 100;
    int value = 0;
                      
    public Robert() {
        Button.PRGM.addButtonListener(this);
        Button.VIEW.addButtonListener(this);
        //Button.RUN.addButtonListener(this);
                      
        Sensor.S1.setTypeAndMode(4, 0xE0);
        Sensor.S1.activate();
        Sensor.S1.addSensorListener(new SensorListener() {
            public void stateChanged(Sensor src, int oldValue, int newValue) {
                // Will be called whenever sensor value changes
                //
                value = newValue;
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        });
    }
                      
    public void buttonPressed(Button button) {
                Sound.beep();
                      
        if(button.PRGM.isPressed())
        {
            int laps = (value- resetValue) / 16;
            LCD.showNumber(laps);
            resetValue = value;
        }
        else if(button.VIEW.isPressed())
        {
            ToogleMotor();
        }
        else if(button.RUN.isPressed())
        {
        }
    }
                      
    public void buttonReleased(Button button) {
                      
    }
                      
    private void ToogleMotor()
    {
        if(Motor.A.isMoving())
        {
            Motor.A.stop();
            Motor.B.stop();
            Motor.C.stop();
        }
        else
        {
            Motor.A.forward();
            Motor.B.forward();
            Motor.C.forward();
        }
    }
 */
    
    
    /**
     * @param args the command line arguments
     */
    //static kiss k;
    static blackOps ops;
    public static void main(String[] args) throws InterruptedException{
/*1        Robert r = new Robert();
        Motor.A.setPower(7);
        Motor.C.setPower(7);
 
        Motor.A.forward();
        Motor.C.forward();
 */
        //irTest ir = new irTest();
        //SoundTest st = new SoundTest();
        //k = new kiss();
        
        ops = new blackOps();
        
        while(true) {
            ;
        }
        //Button.RUN.waitForPressAndRelease();
        
        //System.exit(1);
    }
}
