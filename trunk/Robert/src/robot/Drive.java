package robot;
import josx.platform.rcx.LCD;
import josx.platform.rcx.Motor;
import josx.platform.rcx.Sensor;
import josx.platform.rcx.Sound;
import tinyvm.rcx.TextLCD;
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
    
    //private static final byte BUFFERLENGTH = 3;
    private static byte[] sensor1Buffer = new byte[3]; // contains the values w, y, g, b
    private static byte[] sensor2Buffer = new byte[3];
    private static byte[] sensor3Buffer = new byte[3];
    
//    private static byte[] sensor1Colors = new byte[BUFFERLENGTH];
//    private static byte[] sensor2Colors = new byte[BUFFERLENGTH];
//    private static byte[] sensor3Colors = new byte[BUFFERLENGTH];
//
    private static byte[] sensorsColors = new byte[] {0x01, 0x08, 0x01};//new int[3] ;
    
    
    /*
     White, yellow, Green, black
     **/
    
    /***********
     * ROBOT 1 *
     ***********/
    //ko
//    private static int[] robot1Sensor1Values = new int [] {708, 718, 764, 809};
//    private static int[] robot1Sensor2Values = new int [] {723, 740, 777, 807};
//    private static int[] robot1Sensor3Values = new int [] {691, 704, 747, 790};
    
    /***********
     * ROBOT 2 *
     ***********/
//    private static int[] robot2Sensor1Values = new int [] {684 * BUFFERLENGTH, 690 * BUFFERLENGTH, 732 * BUFFERLENGTH, 765 * BUFFERLENGTH};
//    private static int[] robot2Sensor2Values = new int [] {741 * BUFFERLENGTH, 757 * BUFFERLENGTH, 800 * BUFFERLENGTH, 832 * BUFFERLENGTH};
//    private static int[] robot2Sensor3Values = new int [] {712 * BUFFERLENGTH, 720 * BUFFERLENGTH, 766 * BUFFERLENGTH, 820 * BUFFERLENGTH};
    
    /***********
     * ROBOT 3 *
     ***********/
//    private static int[] robot3Sensor1Values = new int [] {740 * BUFFERLENGTH, 750 * BUFFERLENGTH, 797 * BUFFERLENGTH, 840 * BUFFERLENGTH};
//    private static int[] robot3Sensor2Values = new int [] {719 * BUFFERLENGTH, 730 * BUFFERLENGTH, 775 * BUFFERLENGTH, 840 * BUFFERLENGTH};
//    private static int[] robot3Sensor3Values = new int [] {730 * BUFFERLENGTH, 740 * BUFFERLENGTH, 789 * BUFFERLENGTH, 810 * BUFFERLENGTH};
    
    //only for robot no 1
    private static int[] sensorDiff = new int[] {   714, 748, 794,  //Sensor 1
    734, 764, 797,  //Sensor 2
    699, 732, 775}; //Sensor 3
//    private static int[] sensor1Diff = new int[] {714, 748, 794}; // new int[3];
//    private static int[] sensor2Diff = new int[] {734, 764, 797}; // new int[3];
//    private static int[] sensor3Diff = new int[] {699, 732, 775}; // new int[3];
    
    //ko
//    private static int[] sensor1Values; //placeholder for the acctual thresshold values
//    private static int[] sensor2Values; //placeholder for the acctual thresshold values
//    private static int[] sensor3Values; //placeholder for the acctual thresshold values
    
//    private static final int 0x01  = 0x01;
//    private static final int 0x02 = 0x02;
//    private static final int 0x04  = 0x04;
//    private static final int COLOR_BLACK  = 0x08;
    
    
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
        InitBuffer();
        InitSensors();
        InitMotor();
//        SetSensorValues(address); //ko
    }
    
    private static void InitBuffer(){
        sensor1Buffer[0] = 0;
        sensor2Buffer[0] = 0;
        sensor3Buffer[0] = 0;
        
        sensor1Buffer[1] = 0;
        sensor2Buffer[1] = 0;
        sensor3Buffer[1] = 0;
        
        sensor1Buffer[2] = 0;
        sensor2Buffer[2] = 0;
        sensor3Buffer[2] = 0;
    }
    
    private static void InitSensors() {
        Sensor.S1.setTypeAndMode(3, 0x00);
        Sensor.S1.activate();
        Sensor.S2.setTypeAndMode(3, 0x00);
        Sensor.S2.activate();
        Sensor.S3.setTypeAndMode(3, 0x00);
        Sensor.S3.activate();
    }
    
    private static void InitMotor() {
        Motor.A.setPower(4);
        Motor.B.setPower(4);
    }
    
//    static int i = 0;
//    private final static byte BUFFERLENGTH_1 = BUFFERLENGTH - 1;
    private static void DoRead() {
        sensor1Buffer[2] = sensor1Buffer[1];
        sensor2Buffer[2] = sensor2Buffer[1];
        sensor3Buffer[2] = sensor3Buffer[1];
        
        sensor1Buffer[1] = sensor1Buffer[0];
        sensor2Buffer[1] = sensor2Buffer[0];
        sensor3Buffer[1] = sensor3Buffer[0];
        
        sensor1Buffer[0] = FindColor(Sensor.S1.readRawValue(), (byte)0);
        sensor2Buffer[0] = FindColor(Sensor.S2.readRawValue(), (byte)3);
        sensor3Buffer[0] = FindColor(Sensor.S3.readRawValue(), (byte)6);
    }
    
    /*
     * 0x01  = 0x01;
     * 0x02 = 0x02;
     * 0x04  = 0x04;
     * COLOR_BLACK  = 0x08;
     **/
    private static byte FindColor(int value, byte offset){
        if (value > sensorDiff[offset + 2]) {
            return 0x08; //We got black
        } else if (value > sensorDiff[offset + 1]) {
            return 0x04; //We got green
        } else if (value > sensorDiff[offset]){
            return 0x02; //We got yellow
        } else{
            return 0x01; //We got white
        }
    }
    
    private static void read1(){
        
        if ((sensor1Buffer[0] == sensor1Buffer[1]) || (sensor1Buffer[1] == sensor1Buffer[2])) {
            sensorsColors[0] = sensor1Buffer[1];
        } else if(sensor1Buffer[0] == sensor1Buffer[2]){
            sensorsColors[0] = sensor1Buffer[0];
        }
    }
    
    private static void read2(){
        if ((sensor2Buffer[0] == sensor2Buffer[1]) || (sensor2Buffer[1] == sensor2Buffer[2])) {
            sensorsColors[1] = sensor2Buffer[1];
        } else if(sensor2Buffer[0] == sensor2Buffer[2]){
            sensorsColors[1] = sensor2Buffer[0];
        }
    }
    
    private static void read3(){
        if ((sensor3Buffer[0] == sensor3Buffer[1]) || (sensor3Buffer[1] == sensor3Buffer[2])) {
            sensorsColors[2] = sensor3Buffer[1];
        } else if(sensor3Buffer[0] == sensor3Buffer[2]){
            sensorsColors[2] = sensor3Buffer[0];
        }
    }

    
    private static void ReadColors(){
        DoRead();
        
//        for (i = 0; i < BUFFERLENGTH ; i++){
//            sensor1Colors[i] = FindColor(sensor1Diff, sensor1Buffer[i]);
//            sensor2Colors[i] = FindColor(sensor2Diff, sensor2Buffer[i]);
//            sensor3Colors[i] = FindColor(sensor3Diff, sensor2Buffer[i]);
//        }
        
        read1();
        read2();
        read3();
    }
    
    private static byte tempReturn = 0;
    private static byte ReadBlack() {
        ReadColors();
        tempReturn = 0;
        
        if (sensorsColors[0] == 0x08 || sensorsColors[0] == 0x04) { tempReturn += 4; }
        if (sensorsColors[1] == 0x08 || sensorsColors[1] == 0x04) { tempReturn += 2; }
        if (sensorsColors[2] == 0x08 || sensorsColors[2] == 0x04) { tempReturn += 1; }
        return tempReturn;
    }
    
    public void TurnLeft90() {
        boolean driving = true;
        int step = 0;
        LCD.showNumber(9);
        while(driving){
            int value = ReadBlack();
            if(step == 0){
                LCD.showNumber(0);
                step = 1;
                Movement.sharpLeft();
            } else if (step == 1) {
                LCD.showNumber(1);
                if (value == 4 || value == 6) { // 100 110
                    Movement.stop();
                    step = 2;
                    Movement.sharpLeft();
                }
            } else if(step == 2){
                LCD.showNumber(2);
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
    
    public void forwardTest(){
        boolean driving = true;
        int count = 0;
        while(driving){
            count++;
            int readings = ReadBlack();
            if(readings == 2){
                Movement.forward();
            }
            else if(readings == 6){
                Movement.left();
            }
            else if(readings == 3){
                Movement.right();
            }
            else if(readings == 4){
                if(count > 50){
                    if(sensorsColors[1] == 0x02){
                        driving = false;
                        Movement.stop();
                    }
                }
                else{
                    Movement.sharpLeft();
                }
            }
            else if(readings == 1){
                if(count > 50){
                    if(sensorsColors[1] == 0x02){
                        driving = false;
                        Movement.stop();
                    }
                }
                else{
                    Movement.sharpRight();
                }                
            }
        }
    }
    
//    public void Forward(int nextJunction) {
//        boolean driving = true;
//        int i = 0;
//        byte step = 0;
//        int roadCount = 0;
//        while(driving) {
//            i++;
//            int b = ReadBlack();
//            LCD.showNumber(1000);
//            if (step == 0) {
//                if ((lastJunction == 0x04 && sensorsColors[1] == 0x04) || (((lastJunction & 0x01) == 0x01) || ((lastJunction & 0x02) == 0x02) && b == 0)) {
//                    Movement.forward();
//                    step = 1;
//                } else {
//                    // We are not aligned correct on the line,
//                    // sould call a function to correct the robot
//                    // And step = 1 should also be rewritten
//                    
//                    //Sound.buzz();
//                    try {
//                        Thread.sleep(200);
//                    } catch (InterruptedException ex) {
//                    }
//                    
//                }
//            } else if (step == 1){
//                
//                LCD.showNumber(2000);
//                if ((b & 0x02) == 2) { // 010
//                    step = 2;
//                }
//            }
//            if (step == 2) {
//                
//                LCD.showNumber(3000);
//                roadCount++;
//                if ((nextJunction & 0x04) == 0x04 && // next up is a green dot, with only forward road
//                        roadCount > 50 &&           // we must roll som before we detect a green dot
//                        sensorsColors[1] == 0x04 &&
//                        (sensorsColors[0] == 0x01 || sensorsColors[0] == 0x02) &&
//                        (sensorsColors[2] == 0x01 || sensorsColors[2] == 0x02)){
//                    step = 3;
//                } else  if (b == 2){ // 010
//                    /*
//                     * We are driving on the black line
//                     * We should just continue straight forward
//                     **/
//                    Movement.forward();
//                }else if (b == 0){ // 000
//                    /*
//                     * We found no black line.
//                     * If there is side cross road at the next junction, the robot stops
//                     * else there is an error, which we must corret.
//                     **/
//                    if ((nextJunction & 0x01) == 0x01 || (nextJunction & 0x02) == 0x02){
//                        /*
//                         * A minimun roadCount of 15 before the robot stops
//                         **/
//                        if (roadCount < 15) {
//                            Movement.stop();
//                            step = 3;
//                        }
//                    } else {
//                        //error state - should correct the robot, but for now we continue forward
//                        Movement.forward();
//                        //Sound.beep();
//                    }
//                } else if (b == 1){ // 001
//                    //Should all be common
//                    Movement.sharpRight();
//                } else if (b == 3){ // 011
//                    Movement.right();
//                    if ((nextJunction & 0x01) == 0x01)
//                        roadCount = 0;
//                } else if (b == 4){ // 100
//                    //Should all be common
//                    Movement.sharpLeft();
//                } else if (b == 5){ // 101
//                    //used for x-junctions
//                    if ((nextJunction & 0x03) == 0x03)
//                        step = 3;
//                } else if (b == 6){ // 110
//                    Movement.left();
//                    if ((nextJunction & 0x02) == 0x02)
//                        roadCount = 0;
//                } else if (b == 7){ // 111
//                    //used for x-junctions
//                    if ((nextJunction & 0x03) == 0x03)
//                        step = 3;
//                }
//            } else if(step == 3) {
//                
//                LCD.showNumber(4000);
//                if (b== 0 || b == 2){ // 000 010
//                    //try {
//                    //    Thread.sleep(100);
//                    //} catch (InterruptedException ex) {
//                    //}
//                    Movement.stop();
//                    lastJunction = nextJunction;
//                    driving = false;
//                }
//            }
//        }
//    }
    
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
