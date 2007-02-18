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
public class Robert {
    
    public Robert() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Sensor.S1.setTypeAndMode (3, 0x80);
        Sensor.S1.activate();
        Sensor.S1.addSensorListener (new SensorListener() {
         public void stateChanged (Sensor src, int oldValue, int newValue) {
           // Will be called whenever sensor value changes
           LCD.showNumber (newValue);

           try {
                 Thread.sleep (100);
           } catch (InterruptedException e) {
                 // ignore
           }
         }
        });
        for(;;)
        {
        ;
        }
    }
    
}
