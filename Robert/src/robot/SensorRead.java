/*
 * SensorRead.java
 *
 * Created on 20. maj 2007, 12:50
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Administrator @ 20. maj 2007 (v 1.1)
 * __________ Changes ____________
 *
 * Administrator @ 20. maj 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package robot;

import josx.platform.rcx.Button;
import josx.platform.rcx.ButtonListener;
import josx.platform.rcx.LCD;
import josx.platform.rcx.Segment;
import josx.platform.rcx.Sensor;


public class SensorRead implements ButtonListener{
    private static int readValue;
    private static byte readSensor;
    private static int currentTime;

    private static boolean showReadings = true;

    private static int lastTime, numberOfReadings, rps = 0;
    
    /** Creates a new instance of SensorRead */
    public SensorRead() {
        readSensor = 1;
        Button.RUN.addButtonListener(this);
        Button.PRGM.addButtonListener(this);
        Button.VIEW.addButtonListener(this);
        run();
    }
    
    private static void deInitSensors(){
        Sensor.S1.passivate();
        Sensor.S2.passivate();
        Sensor.S3.passivate();
    }

    public void buttonPressed(Button button) {
        LCD.clearSegment(Segment.SENSOR_1_VIEW);
        LCD.clearSegment(Segment.SENSOR_2_VIEW);
        LCD.clearSegment(Segment.SENSOR_3_VIEW);
        if (Button.VIEW.isPressed()) {
            deInitSensors();
            readSensor++;
            if(readSensor > 3){
                readSensor = 1;
            }
        if(readSensor == 1){
            Sensor.S1.setTypeAndMode(3, 0x00);
            Sensor.S1.activate();
        }
        if(readSensor == 2){
            Sensor.S2.setTypeAndMode(3, 0x00);
            Sensor.S2.activate();
        }
        if(readSensor == 3){
            Sensor.S3.setTypeAndMode(3, 0x00);
            Sensor.S3.activate();
        }
        }else if (Button.PRGM.isPressed()) {
            if(showReadings == false){
                showReadings = true;
            }else{
                showReadings = false;
            }
        }else if (Button.RUN.isPressed()) {
        }
    }
    
    public void buttonReleased(Button button) {
    }
    
    public static void main(String[] args) throws InterruptedException {
        SensorRead noget = new SensorRead();
    }

    private void run() {
        while(true){
            if(readSensor == 1){
                readValue = Sensor.S2.readRawValue();
                readValue = Sensor.S3.readRawValue();
                readValue = Sensor.S1.readRawValue();
            }
            else if(readSensor == 2){
                readValue = Sensor.S3.readRawValue();
                readValue = Sensor.S1.readRawValue();
                readValue = Sensor.S2.readRawValue();
            }
            else if(readSensor == 3){
                readValue = Sensor.S1.readRawValue();
                readValue = Sensor.S2.readRawValue(); //Will test how many times it can read all sensors within 1 sec.
                readValue = Sensor.S3.readRawValue();
            }
            numberOfReadings++;
            this.currentTime = (int)System.currentTimeMillis();
            if(this.showReadings == true){
                LCD.showNumber(readValue);
            } else{
                LCD.showNumber(rps);
                if((this.currentTime - this.lastTime) > 1000){
                    this.lastTime = this.currentTime;
                    rps = this.numberOfReadings;
                    numberOfReadings = 0;
                }
            }
            LCD.showProgramNumber(readSensor);
            LCD.setSegment(readSensor==1?Segment.SENSOR_1_VIEW: (readSensor==2?Segment.SENSOR_2_VIEW:Segment.SENSOR_3_VIEW));
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException ex) {
//                LCD.showNumber(9999);
//            }
        }
    }
}
