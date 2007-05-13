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
    
    //private static final int sensor1Diff = 30;
    //private static final int sensor2Diff = -20;
    //private static final int sensor3Diff = 30;
    
    
    
    //White, yellow, Green, black
    
    /***********
     * ROBOT 1 *
     ***********/
    private static int[] robot1Sensor1Values = new int [] {708 * BUFFERLENGTH, 718 * BUFFERLENGTH, 764 * BUFFERLENGTH, 809 * BUFFERLENGTH};
    private static int[] robot1Sensor2Values = new int [] {723 * BUFFERLENGTH, 740 * BUFFERLENGTH, 777 * BUFFERLENGTH, 807 * BUFFERLENGTH};
    private static int[] robot1Sensor3Values = new int [] {691 * BUFFERLENGTH, 704 * BUFFERLENGTH, 747 * BUFFERLENGTH, 790 * BUFFERLENGTH};
    
    /***********
     * ROBOT 2 *
     ***********/
    private static int[] robot2Sensor1Values = new int [] {684 * BUFFERLENGTH, 690 * BUFFERLENGTH, 732 * BUFFERLENGTH, 765 * BUFFERLENGTH};
    private static int[] robot2Sensor2Values = new int [] {741 * BUFFERLENGTH, 757 * BUFFERLENGTH, 800 * BUFFERLENGTH, 832 * BUFFERLENGTH};
    private static int[] robot2Sensor3Values = new int [] {712 * BUFFERLENGTH, 720 * BUFFERLENGTH, 766 * BUFFERLENGTH, 820 * BUFFERLENGTH};
    
    /***********
     * ROBOT 3 *
     ***********/
    private static int[] robot3Sensor1Values = new int [] {740 * BUFFERLENGTH, 750 * BUFFERLENGTH, 797 * BUFFERLENGTH, 840 * BUFFERLENGTH};
    private static int[] robot3Sensor2Values = new int [] {719 * BUFFERLENGTH, 730 * BUFFERLENGTH, 775 * BUFFERLENGTH, 840 * BUFFERLENGTH};
    private static int[] robot3Sensor3Values = new int [] {730 * BUFFERLENGTH, 740 * BUFFERLENGTH, 789 * BUFFERLENGTH, 810 * BUFFERLENGTH};
    
    private static int[] sensor1Values;
    private static int[] sensor2Values;
    private static int[] sensor3Values;
    
    private static int[] sensor1Diff = new int[3];
    private static int[] sensor2Diff = new int[3];
    private static int[] sensor3Diff = new int[3];
    
    //private static int sensor1Threshold = 0;
    //private static int sensor2Threshold = 0;
    //private static int sensor3Threshold = 0;
    
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
//    public static final int DIRECTIONS_RIGHT        = 0x01;  //001
//    public static final int DIRECTIONS_LEFT         = 0x02;  //010
//    public static final int DIRECTIONS_TCROSS       = 0x03;  //011
//    public static final int DIRECTIONS_FORWARD      = 0x04;  //100
//    public static final int DIRECTIONS_RIGHTFORWARD = 0x05;  //101
//    public static final int DIRECTIONS_LEFTFORWARD  = 0x06;  //110
//    public static final int DIRECTIONS_XCROSS       = 0x07;  //111
    
    private static int lastJunction = 0x04;
    
    /** Creates a new instance of Drive */
    public Drive(int address) {
        InitSensors();
        InitMotor();
        SetSensorValues(address);
    }
    
    private static void InitSensors() {
        Sensor.S1.setTypeAndMode(3, 0x00);
        Sensor.S1.activate();
        Sensor.S2.setTypeAndMode(3, 0x00);
        Sensor.S2.activate();
        Sensor.S3.setTypeAndMode(3, 0x00);
        Sensor.S3.activate();
        //try {
        //   //we must wait a little while before reading from the sensors
        //   Thread.sleep(500);
        //} catch (InterruptedException ex) {
        //}
        //sensor1Threshold = (Sensor.S1.readRawValue() + sensor1Diff) * BUFFERLENGTH;
        //sensor2Threshold = (Sensor.S2.readRawValue() + sensor2Diff) * BUFFERLENGTH;
        //sensor3Threshold = (Sensor.S3.readRawValue() + sensor3Diff) * BUFFERLENGTH;
    }
    
    private static void InitMotor() {
        Motor.A.setPower(4);
        Motor.B.setPower(4);
    }
    
    private static void SetSensorValues(int address) {
        if (address == 1){
            sensor1Values = robot1Sensor1Values;
            sensor2Values = robot1Sensor2Values;
            sensor3Values = robot1Sensor3Values;
        } else if (address == 2){
            sensor1Values = robot2Sensor1Values;
            sensor2Values = robot2Sensor2Values;
            sensor3Values = robot2Sensor3Values;
        } else if (address == 3) {
            sensor1Values = robot3Sensor1Values;
            sensor2Values = robot3Sensor2Values;
            sensor3Values = robot3Sensor3Values;
        }else {
            Sound.buzz();
            System.exit(1);
        }
        CalculateSensorDifferens();
    }
    
    private static void CalculateSensorDifferens() {
        /************
         * Sensor 1 *
         ************/
        sensor1Diff[0] = sensor1Values[0] + ((sensor1Values[1] - sensor1Values[0])/2);
        sensor1Diff[1] = sensor1Values[1] + ((sensor1Values[2] - sensor1Values[1])/2);
        sensor1Diff[2] = sensor1Values[2] + ((sensor1Values[3] - sensor1Values[2])/2);
        
        /************
         * Sensor 2 *
         ************/
        sensor2Diff[0] = sensor2Values[0] + ((sensor2Values[1] - sensor2Values[0])/2);
        sensor2Diff[1] = sensor2Values[1] + ((sensor2Values[2] - sensor2Values[1])/2);
        sensor2Diff[2] = sensor2Values[2] + ((sensor2Values[3] - sensor2Values[2])/2);
        
        /************
         * Sensor 3 *
         ************/
        sensor3Diff[0] = sensor3Values[0] + ((sensor3Values[1] - sensor3Values[0])/2);
        sensor3Diff[1] = sensor3Values[1] + ((sensor3Values[2] - sensor3Values[1])/2);
        sensor3Diff[2] = sensor3Values[2] + ((sensor3Values[3] - sensor3Values[2])/2);
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
        /*
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
        }*/
        if (s1 > sensor1Diff[2]) {
            //We got black
            sensorsColors[0] = COLOR_BLACK;
        } else if (s1 > sensor1Diff[1]) {
            //We got green
            sensorsColors[0] = COLOR_GREEN;
        } else if (s1 > sensor1Diff[0]){
            //We got yellow
            sensorsColors[0] = COLOR_YELLOW;
        } else{
            //We got white
            sensorsColors[0] = COLOR_WHITE;
        }
        
        /*
         * Check on sensor 2
         */
        /*if (s2 > (sensor2Values[3] - 60)){
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
        }*/
        if (s2 > sensor2Diff[2]) {
            //We got black
            sensorsColors[1] = COLOR_BLACK;
        } else if (s2 > sensor2Diff[1]) {
            //We got green
            sensorsColors[1] = COLOR_GREEN;
        } else if (s2 > sensor2Diff[0]){
            //We got yellow
            sensorsColors[1] = COLOR_YELLOW;
        } else{
            //We got white
            sensorsColors[1] = COLOR_WHITE;
        }
        
        
        /*
         * Check on sensor 3
         */
        /*if (s3 > (sensor3Values[3] - 60)){
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
        }*/
        if (s3 > sensor3Diff[2]) {
            //We got black
            sensorsColors[2] = COLOR_BLACK;
        } else if (s3 > sensor3Diff[1]) {
            //We got green
            sensorsColors[2] = COLOR_GREEN;
        } else if (s3 > sensor3Diff[0]){
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
                if ((lastJunction == 0x04 && sensorsColors[1] == COLOR_GREEN) || (((lastJunction & 0x01) == 0x01) || ((lastJunction & 0x02) == 0x02) && b == 0)) {
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
                if ((nextJunction & 0x04) == 0x04 && // next up is a green dot, with only forward road
                        roadCount > 50 &&           // we must roll som before we detect a green dot
                        sensorsColors[1] == COLOR_GREEN &&
                        (sensorsColors[0] == COLOR_WHITE || sensorsColors[0] == COLOR_YELLOW) &&
                        (sensorsColors[2] == COLOR_WHITE || sensorsColors[2] == COLOR_YELLOW)){
                    step = 3;
                } else  if (b == 2){ // 010
                    /*
                     * We are driving on the black line
                     * We should just continue straight forward
                     **/
                    Movement.forward();
                }else if (b == 0){ // 000
                    /*
                     * We found no black line.
                     * If there is side cross road at the next junction, the robot stops
                     * else there is an error, which we must corret.
                     **/
                    if ((nextJunction & 0x01) == 0x01 || (nextJunction & 0x02) == 0x02){
                        /*
                         * A minimun roadCount of 15 before the robot stops
                         **/
                        if (roadCount < 15) {
                            Movement.stop();
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
                    //try {
                    //    Thread.sleep(100);
                    //} catch (InterruptedException ex) {
                    //}
                    Movement.stop();
                    lastJunction = nextJunction;
                    driving = false;
                }
            }
        }
    }
    
    public void Calibrate() {
        int s2Max = 0;
        int s2Min = 2000;
        
        int value= 0;
        int i= 0;
        while (i < 10) {
            value = Sensor.S2.readRawValue();
            if (s2Max < value) {
                s2Max = value;
            }
            if (s2Min > value) {
                s2Min = value;
            }
            Movement.forward();
            try {
                Thread.sleep(50);
                Movement.stop();
                LCD.showNumber(value);
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                ;
            }
            i++;
        }
        try {
            LCD.showNumber(s2Min);
            Thread.sleep(2000);
            LCD.showNumber(s2Max);
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
        }
        
    }
}