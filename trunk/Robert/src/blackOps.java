import josx.platform.rcx.Button;
import josx.platform.rcx.ButtonListener;
import josx.platform.rcx.LCD;
import josx.platform.rcx.Motor;
import josx.platform.rcx.Sensor;
import josx.platform.rcx.Sound;
import josx.util.Timer;
import josx.util.TimerListener;
/*
 * blackOps.java
 *
 * Created on 5. marts 2007, 13:37
 *
 * The black ops only task is to follow a black line
 * At startup it reads the 3 sensors.
 * Sensor no. 2 should be placed over the black line.
 */

/**
 *
 * @author Christian Holm, 5601
 */
public class blackOps implements TimerListener, ButtonListener{
    Timer timer;
    int sensorIndex = 0;
    
    static final int bufferLength = 3;
    int[] s1Buffer = new int[bufferLength];// {0,0,0,0,0,0,0,0,0,0};
    int s1BufferIndex = 0;
    int s1Average = 0;
    int[] s2Buffer = new int[bufferLength];//{0,0,0,0,0,0,0,0,0,0};
    int s2BufferIndex = 0;
    int s2Average = 0;
    int[] s3Buffer = new int[bufferLength];//{0,0,0,0,0,0,0,0,0,0};
    int s3BufferIndex = 0;
    int s3Average = 0;
    
    boolean showThreshold = false;
    int s1Threshold = 0;
    int s2Threshold = 0;
    int s3Threshold = 0;
    
    boolean running = false;
    
    /** Creates a new instance of blackOps */
    public blackOps() {
        timer = new Timer(100, this);
        
        Button.RUN.addButtonListener(this);
        Button.PRGM.addButtonListener(this);
        Button.VIEW.addButtonListener(this);
        
        Sensor.S1.setTypeAndMode(3, 0x00);
        Sensor.S1.activate();
        
        Sensor.S2.setTypeAndMode(3, 0x00);
        Sensor.S2.activate();
        
        Sensor.S3.setTypeAndMode(3, 0x00);
        Sensor.S3.activate();
        
        Motor.A.setPower(7);
        Motor.C.setPower(7);
        
        timer.start();
    }
    
    public void timedOut() {
        s1Buffer[s1BufferIndex++] = Sensor.S1.readRawValue();
        s2Buffer[s2BufferIndex++] = Sensor.S2.readRawValue();
        s3Buffer[s3BufferIndex++] = Sensor.S3.readRawValue();
        if (s1BufferIndex == bufferLength)
            s1BufferIndex = 0;
        if (s2BufferIndex == bufferLength)
            s2BufferIndex = 0;
        if (s3BufferIndex == bufferLength)
            s3BufferIndex = 0;
        
        s1Average = average(s1Buffer);
        s2Average = average(s2Buffer);
        s3Average = average(s3Buffer);
        
        if (showThreshold) {
            if (sensorIndex == 1){
                LCD.showNumber(s1Threshold);
            }else if (sensorIndex == 2){
                LCD.showNumber(s2Threshold);
            }else if (sensorIndex == 3){
                LCD.showNumber(s3Threshold);
            }
        } else if (sensorIndex > 0) {
            if (sensorIndex == 1){
                LCD.showNumber(s1Average);
            }else if (sensorIndex == 2){
                LCD.showNumber(s2Average);
            }else if (sensorIndex == 3){
                LCD.showNumber(s3Average);
            }
        } else {
            if (running) {
                LCD.showNumber(1111);
                motorControl();
            } else {
                LCD.showNumber(0);
                stop();
            }
        }
    }
    
    private void motorControl() {
        boolean value1 = s1Average > s1Threshold;
        boolean value2 = s2Average > s2Threshold;
        boolean value3 = s3Average > s3Threshold;
        int value = 0;
        value += value1?1:0;
        value <<= 1;
        value += value2?1:0;
        value <<= 1;
        value += value3?1:0;
        
        if (value == 0 || value == 2)
            forward();
/*        else if (value == 5)
        {
            Sound.beep();
            sharpLeft();
        }
*/        else if(value == 1 || value == 3)
            right();
        else if (value >= 4)
            left();
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
        Motor.A.forward();
        Motor.C.stop();
    }
    
    private void stop() {
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
    
    private int average(int[] number) {
        int sum = 0;
        for(int i = 0; i < bufferLength; i++) {
            sum += number[i];
        }
        return sum/bufferLength;
    }
    
    public void buttonPressed(Button button) {
        if (Button.VIEW.isPressed()) {
            Sound.beep();
            ++sensorIndex;
            ClearAllSensorSegment();
            
            if (sensorIndex == 1){
                SetSensorSegment(1);
            } else if (sensorIndex == 2){
                SetSensorSegment(2);
            } else if (sensorIndex == 3){
                SetSensorSegment(3);
            } else if (sensorIndex == 4){
                sensorIndex = 0;
            }
        }else if (Button.PRGM.isPressed()) {
            if (sensorIndex > 0){
                showThreshold = true;
                if (sensorIndex == 1) {
                    if (s1Threshold == 0) {
                        s1Threshold = average(s1Buffer);
                    } else {
                        s1Threshold += 5;
                    }
                } else if (sensorIndex == 2) {
                    if (s2Threshold == 0) {
                        s2Threshold = average(s2Buffer);
                    } else {
                        s2Threshold += 5;
                    }
                }else if (sensorIndex == 3) {
                    if (s3Threshold == 0) {
                        s3Threshold = average(s3Buffer);
                    } else {
                        s3Threshold += 5;
                    }
                }
            }
        }else if (Button.RUN.isPressed()) {
            if (sensorIndex > 0){
                showThreshold = true;
                if (sensorIndex == 1) {
                    if (s1Threshold == 0) {
                        s1Threshold = average(s1Buffer);
                    } else {
                        s1Threshold -= 5;
                    }
                } else if (sensorIndex == 2) {
                    if (s2Threshold == 0) {
                        s2Threshold = average(s2Buffer);
                    } else {
                        s2Threshold -= 5;
                    }
                }else if (sensorIndex == 3) {
                    if (s3Threshold == 0) {
                        s3Threshold = average(s3Buffer);
                    } else {
                        s3Threshold -= 5;
                    }
                }
            } else {
                Sound.twoBeeps();
                running = !running;
            }
        }
    }
    
    public void buttonReleased(Button button) {
        ;
    }
    
    private void SetSensorSegment(int index) {
        int aCode = 0;
        if (index == 1) {
            aCode = 0x3008;
        } else if (index == 2) {
            aCode = 0x300a;
        } else if(index == 3) {
            aCode = 0x300c;
        }
        LCD.setSegment(aCode);
        LCD.refresh();
    }
    
    private void ClearSensorSegment(int index) {
        int aCode = 0;
        if (index == 1) {
            aCode = 0x3008;
        } else if (index == 2) {
            aCode = 0x300a;
        } else if(index == 3) {
            aCode = 0x300c;
        }
        LCD.clearSegment(aCode);
        LCD.refresh();
    }
    
    private void ClearAllSensorSegment() {
        int aCode = 0x3008;
        LCD.clearSegment(aCode);
        aCode = 0x300a;
        LCD.clearSegment(aCode);
        aCode = 0x300c;
        LCD.clearSegment(aCode);
        LCD.refresh();
        showThreshold = false;
    }
}

