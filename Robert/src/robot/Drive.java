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
    
    private static final byte BUFFERLENGTH = 3;
    private static int[] sensor1Buffer = new int[BUFFERLENGTH];
    private static int[] sensor2Buffer = new int[BUFFERLENGTH];
    private static int[] sensor3Buffer = new int[BUFFERLENGTH];
    
    private static int[] sensor1Colors = new int[BUFFERLENGTH];
    private static int[] sensor2Colors = new int[BUFFERLENGTH];
    private static int[] sensor3Colors = new int[BUFFERLENGTH];
    
    private static int[] sensorsColors = new int[] {0x01, 0x08, 0x01};//new int[3] ;
    
    
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
    
    //ko
    private static int[] sensor1Diff = new int[] {714, 748, 794}; // new int[3];
    private static int[] sensor2Diff = new int[] {734, 764, 797}; // new int[3];
    private static int[] sensor3Diff = new int[] {699, 732, 775}; // new int[3];
    
    private static int[] sensor1Values; //placeholder for the acctual thresshold values
    private static int[] sensor2Values; //placeholder for the acctual thresshold values
    private static int[] sensor3Values; //placeholder for the acctual thresshold values
    
    private static final int COLOR_WHITE  = 0x01;
    private static final int COLOR_YELLOW = 0x02;
    private static final int COLOR_GREEN  = 0x04;
    private static final int COLOR_BLACK  = 0x08;
    
    
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
//        SetSensorValues(address); //ko
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
    //ko
//    private static void SetSensorValues(int address) {
//        if (address == 1){
//            sensor1Values = robot1Sensor1Values;
//            sensor2Values = robot1Sensor2Values;
//            sensor3Values = robot1Sensor3Values;
//        } else if (address == 2){
//            sensor1Values = robot2Sensor1Values;
//            sensor2Values = robot2Sensor2Values;
//            sensor3Values = robot2Sensor3Values;
//        } else if (address == 3) {
//            sensor1Values = robot3Sensor1Values;
//            sensor2Values = robot3Sensor2Values;
//            sensor3Values = robot3Sensor3Values;
//        }else {
//            Sound.buzz();
//            System.exit(1);
//        }
//        CalculateSensorDifferens();
//    }
//    
//    private static void CalculateSensorDifferens() {
//        /************
//         * Sensor 1 *
//         ************/
//        sensor1Diff[0] = sensor1Values[1] - ((sensor1Values[1] - sensor1Values[0])/3);
//        sensor1Diff[1] = sensor1Values[2] - ((sensor1Values[2] - sensor1Values[1])/3);
//        sensor1Diff[2] = sensor1Values[3] - ((sensor1Values[3] - sensor1Values[2])/3);
//        
//        /************
//         * Sensor 2 *
//         ************/
//        sensor2Diff[0] = sensor2Values[1] - ((sensor2Values[1] - sensor2Values[0])/3);
//        sensor2Diff[1] = sensor2Values[2] - ((sensor2Values[2] - sensor2Values[1])/3);
//        sensor2Diff[2] = sensor2Values[3] - ((sensor2Values[3] - sensor2Values[2])/3);
//        
//        /************
//         * Sensor 3 *
//         ************/
//        sensor3Diff[0] = sensor3Values[1] - ((sensor3Values[1] - sensor3Values[0])/3);
//        sensor3Diff[1] = sensor3Values[2] - ((sensor3Values[2] - sensor3Values[1])/3);
//        sensor3Diff[2] = sensor3Values[3] - ((sensor3Values[3] - sensor3Values[2])/3);
//    }
    
//    static int i = 0;
//    private final static byte BUFFERLENGTH_1 = BUFFERLENGTH - 1;
    private static void DoRead() {
        for (int i = 0; i < 2; i++){
            sensor1Buffer[2 - i] = sensor1Buffer[1 - i];
            sensor2Buffer[2 - i] = sensor2Buffer[1 - i];
            sensor3Buffer[2 - i] = sensor3Buffer[1 - i];
//            sensor1Buffer[BUFFERLENGTH_1 - i] = sensor1Buffer[BUFFERLENGTH_1 - 1 - i];
//            sensor2Buffer[BUFFERLENGTH_1 - i] = sensor2Buffer[BUFFERLENGTH_1 - 1 - i];
//            sensor3Buffer[BUFFERLENGTH_1 - i] = sensor3Buffer[BUFFERLENGTH_1 - 1 - i];
        }
        
        sensor1Buffer[0] = Sensor.S1.readRawValue();
        sensor2Buffer[0] = Sensor.S2.readRawValue();
        sensor3Buffer[0] = Sensor.S3.readRawValue();
    }
    
    private static int FindColor(int[] sensorDiff, int value){
        if (value > sensorDiff[2]) {
            //We got black
            return COLOR_BLACK;
        } else if (value > sensorDiff[1]) {
            //We got green
            return COLOR_GREEN;
        } else if (value > sensorDiff[0]){
            //We got yellow
            return COLOR_YELLOW;
        } else{
            //We got white
            return COLOR_WHITE;
        }
    }
    
//    private static void Read1(){
//        
//        if (sensor1Colors[0] == sensor1Colors[1] && sensor1Colors[1] == sensor1Colors[2]) {
//            if (sensor1Colors[0] == COLOR_BLACK){
//                sensorsColors[0] = COLOR_BLACK;
//            } else if(sensor1Colors[0] == COLOR_GREEN){
//                sensorsColors[0] = COLOR_GREEN;
//            } else if(sensor1Colors[0] == COLOR_YELLOW){
//                sensorsColors[0] = COLOR_YELLOW;
//            } else if(sensor1Colors[0] == COLOR_WHITE){
//                sensorsColors[0] = COLOR_WHITE;
//            }
//        }
//    }
//    
//    private static void Read2(){
//        
//        if (sensor2Colors[0] == sensor2Colors[1] && sensor2Colors[1] == sensor2Colors[2]) {
//            if (sensor2Colors[0] == COLOR_BLACK){
//                sensorsColors[1] = COLOR_BLACK;
//            } else if(sensor2Colors[0] == COLOR_GREEN){
//                sensorsColors[1] = COLOR_GREEN;
//            } else if(sensor2Colors[0] == COLOR_YELLOW){
//                sensorsColors[1] = COLOR_YELLOW;
//            } else if(sensor2Colors[0] == COLOR_WHITE){
//                sensorsColors[1] = COLOR_WHITE;
//            }
//        }
//    }
//    
//    private static void Read3(){
//        
//        
//        if (sensor3Colors[0] == sensor3Colors[1] && sensor3Colors[1] == sensor3Colors[2]) {
//            if (sensor3Colors[0] == COLOR_BLACK){
//                sensorsColors[2] = COLOR_BLACK;
//            } else if(sensor3Colors[0] == COLOR_GREEN){
//                sensorsColors[2] = COLOR_GREEN;
//            } else if(sensor3Colors[0] == COLOR_YELLOW){
//                sensorsColors[2] = COLOR_YELLOW;
//            } else if(sensor3Colors[0] == COLOR_WHITE){
//                sensorsColors[2] = COLOR_WHITE;
//            }
//        }
//        
//    }
//    
//    private static void Read4(){
//        
//        if ((sensor1Colors[0] == sensor1Colors[1] && sensor1Colors[1] != sensor1Colors[2]) || (sensor1Colors[0] != sensor1Colors[1] && sensor1Colors[1] == sensor1Colors[2])) {
//            if (sensor1Colors[1] == COLOR_BLACK){
//                sensorsColors[0] = COLOR_BLACK;
//            } else if(sensor1Colors[1] == COLOR_GREEN){
//                sensorsColors[0] = COLOR_GREEN;
//            } else if(sensor1Colors[1] == COLOR_YELLOW){
//                sensorsColors[0] = COLOR_YELLOW;
//            } else if(sensor1Colors[1] == COLOR_WHITE){
//                sensorsColors[0] = COLOR_WHITE;
//            }
//        }
//    }
//    
//    private static void Read5(){
//        
//        if ((sensor2Colors[0] == sensor2Colors[1] && sensor2Colors[1] != sensor2Colors[2]) || (sensor2Colors[0] != sensor2Colors[1] && sensor2Colors[1] == sensor2Colors[2])) {
//            if (sensor2Colors[1] == COLOR_BLACK){
//                sensorsColors[1] = COLOR_BLACK;
//            } else if(sensor2Colors[1] == COLOR_GREEN){
//                sensorsColors[1] = COLOR_GREEN;
//            } else if(sensor2Colors[1] == COLOR_YELLOW){
//                sensorsColors[1] = COLOR_YELLOW;
//            } else if(sensor2Colors[1] == COLOR_WHITE){
//                sensorsColors[1] = COLOR_WHITE;
//            }
//        }
//    }
//    
//    private static void Read6(){
//        
//        if ((sensor3Colors[0] == sensor3Colors[1] && sensor3Colors[1] != sensor3Colors[2]) || (sensor3Colors[0] != sensor3Colors[1] && sensor3Colors[1] == sensor3Colors[2])) {
//            if (sensor3Colors[1] == COLOR_BLACK){
//                sensorsColors[2] = COLOR_BLACK;
//            } else if(sensor3Colors[1] == COLOR_GREEN){
//                sensorsColors[2] = COLOR_GREEN;
//            } else if(sensor3Colors[1] == COLOR_YELLOW){
//                sensorsColors[2] = COLOR_YELLOW;
//            } else if(sensor3Colors[1] == COLOR_WHITE){
//                sensorsColors[2] = COLOR_WHITE;
//            }
//        }
//    }
    
    private static void ReadColors(){
//        DoRead();
//        
//        for (int i = 0; i < BUFFERLENGTH ; i++){
//            sensor1Colors[i] = FindColor(sensor1Diff, sensor1Buffer[i]);
//            sensor2Colors[i] = FindColor(sensor2Diff, sensor2Buffer[i]);
//            sensor3Colors[i] = FindColor(sensor3Diff, sensor2Buffer[i]);
//        }
//
//        Read1();
//        Read2();
//        Read3();
//        Read4();
//        Read5();
//        Read6();
        
//        sensor1Colors[0] = ;
//        sensor1Colors[1] = ;
//        sensor1Colors[2] = ;
        
////        int s1 = Sum(sensor1Buffer);
////        int s2 = Sum(sensor2Buffer);
////        int s3 = Sum(sensor3Buffer);
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
////        if (s1 > sensor1Diff[2]) {
////            //We got black
////            sensorsColors[0] = COLOR_BLACK;
////        } else if (s1 > sensor1Diff[1]) {
////            //We got green
////            sensorsColors[0] = COLOR_GREEN;
////        } else if (s1 > sensor1Diff[0]){
////            //We got yellow
////            sensorsColors[0] = COLOR_YELLOW;
////        } else{
////            //We got white
////            sensorsColors[0] = COLOR_WHITE;
////        }
        
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
////        if (s2 > sensor2Diff[2]) {
////            //We got black
////            sensorsColors[1] = COLOR_BLACK;
////        } else if (s2 > sensor2Diff[1]) {
////            //We got green
////            sensorsColors[1] = COLOR_GREEN;
////        } else if (s2 > sensor2Diff[0]){
////            //We got yellow
////            sensorsColors[1] = COLOR_YELLOW;
////        } else{
////            //We got white
////            sensorsColors[1] = COLOR_WHITE;
////        }
        
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
////        if (s3 > sensor3Diff[2]) {
////            //We got black
////            sensorsColors[2] = COLOR_BLACK;
////        } else if (s3 > sensor3Diff[1]) {
////            //We got green
////            sensorsColors[2] = COLOR_GREEN;
////        } else if (s3 > sensor3Diff[0]){
////            //We got yellow
////            sensorsColors[2] = COLOR_YELLOW;
////        } else{
////            //We got white
////            sensorsColors[2] = COLOR_WHITE;
////        }
        
        //LCD.showNumber(sensorsColors[0] * 100 + sensorsColors[1] * 10 + sensorsColors[2]);
    }
    
    private static int tempReturn = 0;
    private static int ReadBlack() {
        
        //ko
        ReadColors();
        tempReturn = 0;
        if (sensorsColors[0] == COLOR_BLACK || sensorsColors[0] == COLOR_GREEN)
            tempReturn += 0x04;
        
        
        return 0x07;
//        if (sensorsColors[1] == COLOR_BLACK || sensorsColors[1] == COLOR_GREEN)
//            tempReturn += 0x02;
//        
//        if (sensorsColors[2] == COLOR_BLACK || sensorsColors[2] == COLOR_GREEN)
//            tempReturn += 0x01;
//        
//        
//        return tempReturn;
        
/*        DoRead();
        tempReturn = 0;
        if (Sum(sensor1Buffer) > sensor1Threshold)
            tempReturn += 0x04;
        if (Sum(sensor2Buffer) > sensor2Threshold)
            tempReturn += 0x02;
        if (Sum(sensor3Buffer) > sensor3Threshold)
            tempReturn += 0x01;
        return tempReturnReturn;*/
    }
    
//    private static int Sum(int[] numbers){
//        int sum = 0;
//        for (int i = 0; i < numbers.length; i++) {
//            sum += numbers[i];
//        }
//        return sum;
//    }
    
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