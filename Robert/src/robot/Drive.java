package robot;
import josx.platform.rcx.LCD;
import josx.platform.rcx.Motor;
import josx.platform.rcx.Sensor;
import josx.platform.rcx.Sound;
/*
 * Drive.java
 *
 * Created on 16. april 2007, 20:51
 *
 */

/**
 *
 * @author Christian Holm, 5601
 */
public class Drive {
    
    private static final byte BUFFERLENGTH = 3;
    private static int sensorBufferIndex = 0;
    private static int[] sensor1Buffer = new int[BUFFERLENGTH];
    private static int[] sensor2Buffer = new int[BUFFERLENGTH];
    private static int[] sensor3Buffer = new int[BUFFERLENGTH];
    
    private static final int sensor1Diff = 30;
    private static final int sensor2Diff = -20;
    private static final int sensor3Diff = 30;
    
    private static int[] sensor1Values = new int [] {708 * BUFFERLENGTH, 718 * BUFFERLENGTH, 764 * BUFFERLENGTH, 809 * BUFFERLENGTH};
    private static int[] sensor2Values = new int [] {723 * BUFFERLENGTH, 740 * BUFFERLENGTH, 777 * BUFFERLENGTH, 807 * BUFFERLENGTH};
    private static int[] sensor3Values = new int [] {691 * BUFFERLENGTH, 704 * BUFFERLENGTH, 747 * BUFFERLENGTH, 790 * BUFFERLENGTH};
    
    private static int sensor1Threshold = 0;
    private static int sensor2Threshold = 0;
    private static int sensor3Threshold = 0;
    
    private static final int COLOR_WHITE  = 0x01;
    private static final int COLOR_YELLOW = 0x02;
    private static final int COLOR_GREEN  = 0x04;
    private static final int COLOR_BLACK  = 0x08;
    
    private static int[] sensorsColors = new int[3];
    
    /* DIRECTIONS uses a 3-bit register
     *
     * Bit-pattern:  C  B  A
     *
     * A = There is a road going right
     * B = There is a road going left
     * C = There is a road going forward
     *
     *  |
     *  *--   Bit-pattern: 101
     *  |
     *
     *
     *  --*   Bit-pattern: 010
     *    |
     *
     */
    public static final int DIRECTIONS_RIGHT        = 0x01;  //001
    public static final int DIRECTIONS_LEFT         = 0x02;  //010
    public static final int DIRECTIONS_TCROSS       = 0x03;  //011
    public static final int DIRECTIONS_FORWARD      = 0x04;  //100
    public static final int DIRECTIONS_RIGHTFORWARD = 0x05;  //101
    public static final int DIRECTIONS_LEFTFORWARD  = 0x06;  //110
    public static final int DIRECTIONS_XCROSS       = 0x07;  //111
    
    private static int lastJunction = DIRECTIONS_FORWARD;
    
    /** Creates a new instance of Drive */
    public Drive() {
        InitSensors();
        InitMotor();
    }
    
    private static void InitSensors() {
        Sensor.S1.setTypeAndMode(3, 0x00);
        Sensor.S1.activate();
        Sensor.S2.setTypeAndMode(3, 0x00);
        Sensor.S2.activate();
        Sensor.S3.setTypeAndMode(3, 0x00);
        Sensor.S3.activate();
        try {
            //we must wait a little while before reading from the sensors
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
        sensor1Threshold = (Sensor.S1.readRawValue() + sensor1Diff) * BUFFERLENGTH;
        sensor2Threshold = (Sensor.S2.readRawValue() + sensor2Diff) * BUFFERLENGTH;
        sensor3Threshold = (Sensor.S3.readRawValue() + sensor3Diff) * BUFFERLENGTH;
    }
    
    private static void InitMotor() {
        Motor.A.setPower(4);
        Motor.B.setPower(4);
    }
    
    private static void DoRead() {
        sensor1Buffer[sensorBufferIndex]   = Sensor.S1.readRawValue();
        sensor2Buffer[sensorBufferIndex]   = Sensor.S2.readRawValue();
        sensor3Buffer[sensorBufferIndex++] = Sensor.S3.readRawValue();
        if (sensorBufferIndex == BUFFERLENGTH) {
            sensorBufferIndex = 0;
        }
    }
    
    private static void ReadColors(){
        DoRead();
        int s1 = Sum(sensor1Buffer);
        int s2 = Sum(sensor2Buffer);
        int s3 = Sum(sensor3Buffer);
        /*
         * Check on sensor 1
         */
        if (s1 > (sensor1Values[3] - 60)){
            //We got black
            sensorsColors[0] = COLOR_BLACK;
        } else if (s1 > (sensor1Values[2] - 50)) {
            //We got green
            sensorsColors[0] = COLOR_GREEN;
        } else if (s1 > (sensor1Values[1] - 12)){
            //We got yellow
            sensorsColors[0] = COLOR_YELLOW;
        } else{
            //We got white
            sensorsColors[0] = COLOR_WHITE;
        }
        
        /*
         * Check on sensor 2
         */
        if (s2 > (sensor2Values[3] - 60)){
            //We got black
            sensorsColors[1] = COLOR_BLACK;
        } else if (s2 > (sensor2Values[2] - 40)) {
            //We got green
            sensorsColors[1] = COLOR_GREEN;
        } else if (s2 > (sensor2Values[1] - 25)){
            //We got yellow
            sensorsColors[1] = COLOR_YELLOW;
        } else{
            //We got white
            sensorsColors[1] = COLOR_WHITE;
        }
        
        /*
         * Check on sensor 3
         */
        if (s3 > (sensor3Values[3] - 60)){
            //We got black
            sensorsColors[2] = COLOR_BLACK;
        } else if (s3 > (sensor3Values[2] - 60)) {
            //We got green
            sensorsColors[2] = COLOR_GREEN;
        } else if (s3 > (sensor3Values[1] - 15)){
            //We got yellow
            sensorsColors[2] = COLOR_YELLOW;
        } else{
            //We got white
            sensorsColors[2] = COLOR_WHITE;
        }
        
        //LCD.showNumber(sensorsColors[0] * 100 + sensorsColors[1] * 10 + sensorsColors[2]);
    }
    
    private static int tempReturn = 0;
    private static int ReadBlack() {
        ReadColors();
        tempReturn = 0;
        if (sensorsColors[0] == COLOR_BLACK || sensorsColors[0] == COLOR_GREEN)
            tempReturn += 0x04;
        
        if (sensorsColors[1] == COLOR_BLACK || sensorsColors[1] == COLOR_GREEN)
            tempReturn += 0x02;
        
        if (sensorsColors[2] == COLOR_BLACK || sensorsColors[2] == COLOR_GREEN)
            tempReturn += 0x01;
        
        
        return tempReturn;
/*        DoRead();
        tempReturn = 0;
        if (Sum(sensor1Buffer) > sensor1Threshold)
            tempReturn += 0x04;
        if (Sum(sensor2Buffer) > sensor2Threshold)
            tempReturn += 0x02;
        if (Sum(sensor3Buffer) > sensor3Threshold)
            tempReturn += 0x01;
        return tempReturn;*/
    }
    
    private static int Sum(int[] numbers){
        int sum = 0;
        for (int i = 0; i < numbers.length; i++) {
            sum += numbers[i];
        }
        return sum;
    }
    
    public void TurnLeft90() {
        boolean driving = true;
        int step = 0;
        while(driving){
            int value = ReadBlack();
            if(step == 0){
                step = 1;
                Movement.sharpLeft();
            } else if (step == 1) {
                if (value == 4 || value == 6) { // 100 110
                    Movement.stop();
                    step = 2;
                    Movement.sharpLeft();
                }
            } else if(step == 2){
                if (value == 0 || value == 2) { // 000 010
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                    Movement.stop();
                    driving = false;
                }
            }
        }
    }
    
    public void TurnRight90() {
        boolean driving = true;
        int step = 0;
        while(driving){
            int value = ReadBlack();
            if(step == 0){
                step = 1;
                Movement.sharpRight();
            } else if (step == 1) {
                if (value == 1 || value == 3) { // 001 011
                    Movement.stop();
                    step = 2;
                    Movement.sharpRight();
                }
            } else if(step == 2){
                if (value == 0 || value == 2) {  // 000 010
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                    Movement.stop();
                    driving = false;
                }
            }
        }
    }
    
    
    public void Forward(int nextJunction) {
        boolean driving = true;
        int i = 0;
        byte step = 0;
        int roadCount = 0;
        while(driving) {
            i++;
            int b = ReadBlack();
            if (step == 0) {
                if ((lastJunction == DIRECTIONS_FORWARD && sensorsColors[1] == COLOR_GREEN) || (((lastJunction & 0x01) == 0x01) || ((lastJunction & 0x02) == 0x02) && b == 0)) {
                    Movement.forward();
                    step = 1;
                } else {
                    // We are not aligned correct on the line,
                    // sould call a function to correct the robot
                    // And step = 1 should also be rewritten
                    
                    //Sound.buzz();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                    }
                    
                }
            } else if (step == 1){
                if ((b & 0x02) == 2) { // 010
                    step = 2;
                }
            }
            if (step == 2) {
                roadCount++;
                if ((nextJunction & 0x04) == 0x04 &&
                        roadCount > 50 &&
                        sensorsColors[1] == COLOR_GREEN &&
                        (sensorsColors[0] == COLOR_WHITE || sensorsColors[0] == COLOR_YELLOW) &&
                        (sensorsColors[2] == COLOR_WHITE || sensorsColors[2] == COLOR_YELLOW)){
                    step = 3;
                } else if (b == 0){ // 000
                    if ((nextJunction & 0x01) == 0x01 || (nextJunction & 0x02) == 0x02){
                        if (roadCount < 15) {
                            Movement.stop();
                            LCD.showNumber(roadCount);
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException ex) {
                            }
                            step = 3;
                        }
                    } else {
                        //error state - should correct the robot, but for now we continue forward
                        Movement.forward();
                        //Sound.beep();
                    }
                } else if (b == 1){ // 001
                    //Should all be common
                    Movement.sharpRight();
                } else if (b == 2){ // 010
                    //Should all be common
                    //@TODO move to the top of if chain, so it gets checked first...
                    Movement.forward();
                } else if (b == 3){ // 011
                    Movement.right();
                    if ((nextJunction & 0x01) == 0x01)
                        roadCount = 0;
                } else if (b == 4){ // 100
                    //Should all be common
                    Movement.sharpLeft();
                } else if (b == 5){ // 101
                    //used for x-junctions
                    if ((nextJunction & 0x03) == 0x03)
                        step = 3;
                } else if (b == 6){ // 110
                    Movement.left();
                    if ((nextJunction & 0x02) == 0x02)
                        roadCount = 0;
                } else if (b == 7){ // 111
                    //used for x-junctions
                    if ((nextJunction & 0x03) == 0x03)
                        step = 3;
                }
            } else if(step == 3) {
                if (b== 0 || b == 2){ // 000 010
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                    lastJunction = nextJunction;
                    Movement.stop();
                    driving = false;
                    Sound.beep();
                }
            }
        }
        LCD.showNumber(i);
    }
}