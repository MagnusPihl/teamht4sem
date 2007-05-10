import josx.platform.rcx.Button;
import josx.platform.rcx.ButtonListener;
import josx.platform.rcx.LCD;
import josx.platform.rcx.Motor;
import josx.platform.rcx.Sensor;
import josx.platform.rcx.Sound;
import josx.platform.rcx.TextLCD;
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
    private int sampleTime = 3;
    
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
    
    final static int greenDiff = 4;
    int s1GreenThreshold = 0;
    int s2GreenThreshold = 0;
    int s3GreenThreshold = 0;
    
    boolean showThreshold = false;
    int s1Threshold = 0;
    int s2Threshold = 0;
    int s3Threshold = 0;
    
    boolean running = false;
    
    private boolean turning = false;
    
    private int turnProgress = 0;
    
    private int curr = 0;
    
    
    //boolean toogle = false;
    
    /** Creates a new instance of blackOps */
    public blackOps() {
        
        timer = new Timer(sampleTime, this);
        
        Button.RUN.addButtonListener(this);
        Button.PRGM.addButtonListener(this);
        Button.VIEW.addButtonListener(this);
        
        Sensor.S1.setTypeAndMode(4, 0x00);
        Sensor.S1.activate();
        
        Sensor.S2.setTypeAndMode(3, 0x00);
        Sensor.S2.activate();
        
        Sensor.S3.setTypeAndMode(4, 0x00);
        Sensor.S3.activate();
        
        Motor.A.setPower(1);
        Motor.C.setPower(1);
        
        timer.start();
    }
    
    public void timedOut() {
        this.updateSensor();
        
        if (showThreshold) {
            if (sensorIndex == 1){
                LCD.showNumber(s1Threshold);
            }else if (sensorIndex == 2){
                LCD.showNumber(s2Threshold);
            }else if (sensorIndex == 3){
                LCD.showNumber(s3Threshold);
            }else if (sensorIndex == 4){
                LCD.showNumber(s1GreenThreshold);
            }else if (sensorIndex == 5){
                LCD.showNumber(s2GreenThreshold);
            }else if (sensorIndex == 6){
                LCD.showNumber(s3GreenThreshold);
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
            if (turning){
                this.yellowLeft90();
            }else
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
        
        
        if (s1Buffer[s1BufferIndex] > s1Threshold && s1Buffer[s1BufferIndex] < s1GreenThreshold - 10) {
            //if (s1Buffer[s1BufferIndex] > s1GreenThreshold - greenDiff && s1Buffer[s1BufferIndex] < s1GreenThreshold+ greenDiff) {
            this.correctLeft();
            //this.correctRight();
        } else if (s3Buffer[s3BufferIndex] > s3Threshold && s3Buffer[s3BufferIndex] < s3GreenThreshold - 10) {
            //if (s3Buffer[s3BufferIndex] > s3GreenThreshold - greenDiff && s3Buffer[s3BufferIndex] < s3GreenThreshold+ greenDiff) {
            this.correctRight();
            //this.correctLeft();
        } else {
            
            if (value == 0 || value == 2) {
                forward();
            } else if (value == 5 || value == 7){
                this.coast();
                //turning = true;
            } else if(value == 1 || value == 3) {
                right();
            } else if (value >= 4){
                left();
            }
        }
        
        // The light part is not pretty, but wtf....
        if (s2Buffer[s2BufferIndex] > s2GreenThreshold - greenDiff && s2Buffer[s2BufferIndex] < s2GreenThreshold+ greenDiff) {
            Sound.beep();
            LightOn();
        } else {
            LightOff();
        }
    }
    
    private void LightOn(){
        Motor.B.forward();
    }
    
    private void LightOff(){
        Motor.B.stop();
    }
    
    private void forward() {
        curr = 0;
        Motor.A.forward();
        Motor.C.backward();
    }
    
    private void backward() {
        Motor.A.backward();
        Motor.C.forward();
    }
    
    private void left() {
        if(curr < 5){
            Motor.A.flt();
        }else {
            Motor.A.stop();
        }
        curr++;
        Motor.C.backward();
    }
    
    private void right() {
        if(curr < 5){
            Motor.C.flt();
        }else{
            Motor.C.stop();
        }
        curr++;
        Motor.A.forward();
    }
    
    private void correctRight(){
        Motor.A.forward();
        //Motor.C.stop();
        Motor.C.flt();
    }
    
    private void correctLeft(){
        //Motor.A.stop();
        Motor.A.flt();
        Motor.C.backward();
    }
    
    private void coast() {
        Motor.A.flt();
        Motor.C.flt();
    }
    
    private void stop() {
        curr = 0;
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
    
    private void updateSensor(){
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
    }
    
    
    private void yellowLeft90(){
        if(!((this.average(s1Buffer) < s1Threshold) && (this.average(s3Buffer) < s3Threshold)) && (turnProgress == 0 || turnProgress == 1)){
            this.forward();
            turnProgress = 1;
        }else if(this.average(s1Buffer) < s1Threshold && this.average(s3Buffer) < s3Threshold && this.average(s2Buffer) > s2Threshold && (turnProgress == 1 || turnProgress == 2)){
            turnProgress = 2;
        }else if(this.average(s1Buffer) < s1Threshold && this.average(s3Buffer) < s3Threshold && (!(this.average(s2Buffer) > s2Threshold)) && turnProgress == 2){
            this.stop();
            turnProgress = 3;
        }else if(this.average(s1Buffer) < s1Threshold && (turnProgress == 3 || turnProgress == 4)){
            this.sharpLeft();
            turnProgress = 4;
        }else if((!(this.average(s1Buffer) < s1Threshold)) && (turnProgress == 4 || turnProgress == 5)){
            turnProgress =5;
        }else if(this.average(s1Buffer) < s1Threshold && turnProgress == 5){
            turning = false;
            turnProgress = 0;
        }else{
            LCD.showNumber(turnProgress);
            this.stop();
        }
    }
    
    private void left90() {
        if((!(this.average(s1Buffer) < s1Threshold)) && (turnProgress == 0 || turnProgress == 1)){
            this.left();
            turnProgress = 1;
        }else if(this.average(s1Buffer) < s1Threshold && (turnProgress == 1 || turnProgress ==2)){
            turnProgress = 2;
        }else if((!(this.average(s1Buffer) < s1Threshold)) && (turnProgress == 2 || turnProgress ==3)){
            turnProgress =3;
        }else if(this.average(s1Buffer) < s1Threshold && turnProgress == 3){
            turning = false;
            turnProgress = 0;
        }else{
            this.stop();
        }
    }
    
    private void right90() {
        if((!(this.average(s3Buffer) < s3Threshold)) && (turnProgress == 0 || turnProgress == 1)){
            this.right();
            turnProgress = 1;
        }else if(this.average(s3Buffer) < s3Threshold && (turnProgress == 1 || turnProgress ==2)){
            turnProgress = 2;
        }else if((!(this.average(s3Buffer) < s3Threshold)) && (turnProgress == 2 || turnProgress ==3)){
            turnProgress =3;
        }else if(this.average(s3Buffer) < s3Threshold && turnProgress == 3){
            turning = false;
            turnProgress = 0;
        }else{
            this.stop();
        }
    }
    
    
    /*private void left90() throws InterruptedException{
        Sound.twoBeeps();
        this.left();
        while(!(this.average(s1Buffer) < s1Threshold)){
            Thread.sleep(sampleTime);
            this.updateSensor();
        }
        while(this.average(s1Buffer) < s1Threshold){
            Thread.sleep(sampleTime);
            this.updateSensor();
        }
        while(!(this.average(s1Buffer) < s1Threshold)){
            Thread.sleep(sampleTime);
            this.updateSensor();
        }
        turning = false;
    }
     
    private void right90() throws InterruptedException{
        this.right();
        while(!(this.average(s3Buffer) < s3Threshold)){
            Thread.sleep(sampleTime);
            this.updateSensor();
        }
        while(this.average(s3Buffer) < s3Threshold){
            Thread.sleep(sampleTime);
            this.updateSensor();
        }
        while(!(this.average(s3Buffer) < s3Threshold)){
            Thread.sleep(sampleTime);
            this.updateSensor();
        }
        turning = false;
    }*/
    
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
            } else if (sensorIndex == 4) {
                SetSensorSegment(4);
            } else if (sensorIndex == 5) {
                SetSensorSegment(5);
            } else if (sensorIndex == 6) {
                SetSensorSegment(6);
            } else if (sensorIndex == 7){
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
                }else if (sensorIndex == 4) {
                    s1GreenThreshold = average(s1Buffer);
                }else if (sensorIndex == 5) {
                    s2GreenThreshold = average(s2Buffer);
                }else if (sensorIndex == 6) {
                    s3GreenThreshold = average(s3Buffer);
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
        } else if (index == 4){
            aCode = 0x301d;
        } else if (index == 5){
            aCode = 0x301a;
        } else if (index == 6){
            aCode = 0x3019;
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
        }else if (index == 4){
            aCode = 0x301d;
        } else if (index == 5){
            aCode = 0x301a;
        } else if (index == 6){
            aCode = 0x3019;
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
        aCode = 0x301d;
        LCD.clearSegment(aCode);
        aCode = 0x301a;
        LCD.clearSegment(aCode);
        aCode = 0x3019;
        LCD.clearSegment(aCode);
        
        LCD.refresh();
        showThreshold = false;
    }
}

