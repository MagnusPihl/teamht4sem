/*
 * NewDrive.java
 *
 * Created on 20. maj 2007, 12:41
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 *
 *
 *  Adjust  - - - -
 *
 * LMK @ 20. maj 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package robot;

import communication.GameCommands;
import josx.platform.rcx.*;

/**
 *
 * @author LMK
 */
public class Driver {
    
    private static int[][] THRESHOLD = new int[][] {        
        {710}, //Sensor 1
        {720,760,800},  //Sensor 2, larger than, yellow, black
        {700}
        
//        {690,720,730}, //Sensor 1
//        {700,760,770},  //Sensor 2, larger than, yellow, black
//        {710,770,780}  //Sensor 3
//        {714, 748, 794},  //Sensor 1, larger than, yellow, black
//        {734, 764, 797},  //Sensor 2
//        {699, 732, 775} //Sensor 3
    };
    
    private static final byte RIGHT_SENSOR = 2;
    private static final byte MIDDLE_SENSOR = 1;
    private static final byte LEFT_SENSOR = 0;
    private static final byte SENSOR_COUNT = 3;
    private byte[][] colorBuffer;
    private byte currentIndex;
    private byte[] currentColor;
    private byte blackSensors;
        
    private static final byte COLOR_BLACK  = 0;
    private static final byte COLOR_GREEN  = 1;
    private static final byte COLOR_YELLOW = 2;
    private static final byte COLOR_WHITE  = 3;
    private static final byte COLOR_UNKNOWN  = 4;
    
    
    private byte i; //iterator; //Also used as roadCount to safe memory
    
    private boolean isDriving;
        
    private int turnTimeout;    
    private static final int MOVE_INIT_TIME = 320; //find ud af om tallet er fornuftigt
    
    private int[] calibrationValues;
    private byte pathsDiscovered, tempPathsDiscovered;
    
    private String[] calibrationMessages = new String[] {"black", "green", "yellow", "white"};
    
    /** Creates a new instance of NewDrive */
    public Driver() {                
        for (i = 0; i < SENSOR_COUNT; i++) {
            Sensor.SENSORS[i].setTypeAndMode(3, 0x00);
            Sensor.SENSORS[i].activate();
        }
        Motor.A.setPower(1); //4
        Motor.C.setPower(1); //4
        
        colorBuffer = new byte[3][3];
        currentIndex = 0;
        currentColor = new byte[3];
        calibrationValues = new int[3];
    }    
    
    /**
     * Get averaged value from the last 3 reads of a specified sensor once user
     * presses the run button. Used for calibration.
     *
     * @param sensor number.
     * @return sensor averaged raw value.
     */
    private int readRaw(byte sensor) {        
        this.setSegment(sensor);
        while (!Button.RUN.isPressed()) {
            calibrationValues[currentIndex++] = Sensor.SENSORS[sensor].readRawValue();

            if (currentIndex == SENSOR_COUNT) {
                currentIndex = 0;
            }
        }
        
        try {
            Thread.sleep(300);
        } catch (Exception e){
            Sound.buzz();
        }        
        while(Button.RUN.isPressed()) {}
        
        Sound.beep();
        return (int)((calibrationValues[0] + calibrationValues[1] + calibrationValues[2]) / 3f);
    }     
    
    /**
     * Set active sensor on lcd display
     *
     * @param sensor number.
     */
    public static void setSegment(byte sensor) {        
        LCD.clearSegment(Segment.SENSOR_1_VIEW);
        LCD.clearSegment(Segment.SENSOR_2_VIEW);
        LCD.clearSegment(Segment.SENSOR_3_VIEW);
        LCD.clearSegment(Segment.SENSOR_1_ACTIVE);
        LCD.clearSegment(Segment.SENSOR_2_ACTIVE);
        LCD.clearSegment(Segment.SENSOR_3_ACTIVE);
        if (sensor == LEFT_SENSOR) {
            LCD.setSegment(Segment.SENSOR_1_ACTIVE); 
            LCD.setSegment(Segment.SENSOR_1_VIEW);
        } else if (sensor == MIDDLE_SENSOR) {
            LCD.setSegment(Segment.SENSOR_2_ACTIVE); 
            LCD.setSegment(Segment.SENSOR_2_VIEW);
        } else if (sensor == RIGHT_SENSOR) {
            LCD.setSegment(Segment.SENSOR_3_ACTIVE); 
            LCD.setSegment(Segment.SENSOR_3_VIEW);
        }
        LCD.refresh();    
    }
    
    /**
     * Calibration routine, should be run before trying to drive.
     * Asks the user to scan the black, white values for the left and right sensors
     * and the black, white, green and yellow values for the middle sensor.
     * When done the values are averaged and threshold values are created.     
     */
    public void calibrate() {// throws InterruptedException {    
        try {
            Thread.sleep(300);
        } catch (Exception e){
            Sound.buzz();
        } 
        
        TextLCD.print(calibrationMessages[COLOR_BLACK]);                        
        for (i = 0; i < SENSOR_COUNT; i++) {            
            THRESHOLD[i][COLOR_BLACK] = readRaw(i);                        
        }
        
        TextLCD.print(calibrationMessages[COLOR_GREEN]);                        
        THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN] = readRaw(MIDDLE_SENSOR);
        THRESHOLD[MIDDLE_SENSOR][COLOR_BLACK] -= (int)((THRESHOLD[MIDDLE_SENSOR][COLOR_BLACK] - THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN]) / 3f);
       
        TextLCD.print(calibrationMessages[COLOR_YELLOW]);                        
        THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW] = readRaw(MIDDLE_SENSOR);
        THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN] -= (int)((THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN] - THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW]) / 3f);
        
        TextLCD.print(calibrationMessages[COLOR_WHITE]);    
        for (i = 0; i < SENSOR_COUNT; i++) {
            if (i != MIDDLE_SENSOR) {
                THRESHOLD[i][COLOR_BLACK] -= (int)((THRESHOLD[i][COLOR_BLACK] - readRaw(i)) / 2f);
            } else {
                THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW] -= (int)((THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW] - readRaw(i)) / 2f);
            }
        }
        
        LCD.clearSegment(Segment.SENSOR_1_ACTIVE);
        LCD.clearSegment(Segment.SENSOR_2_ACTIVE);
        LCD.clearSegment(Segment.SENSOR_3_ACTIVE);
        LCD.clearSegment(Segment.SENSOR_1_VIEW);
        LCD.clearSegment(Segment.SENSOR_2_VIEW);
        LCD.clearSegment(Segment.SENSOR_3_VIEW);
        LCD.clear();
        LCD.refresh();
    }        
    
    /**
     * Read data from light sensors, convert to color constants, and save to 
     * buffers.
     */
    public void read() {
        for (i = 0; i < SENSOR_COUNT; i++) {
            colorBuffer[i][currentIndex] = getColor(Sensor.SENSORS[i].readRawValue(), i);
        }
                currentIndex++;
        if (currentIndex == SENSOR_COUNT) {
            currentIndex = 0;
        }
        
        calculateCurrentColors();
        calculateBlackSensors();
    }
    
    /**
     * Calculate color based on raw value and sensor number.
     * 
     * @param rawSensorValue, value from 0 - 1000 indicating light intensity
     * @param sensor sensor number.
     */
    public byte getColor(int rawSensorValue, byte sensor) {
        if (sensor == MIDDLE_SENSOR) {
            if (rawSensorValue > THRESHOLD[sensor][COLOR_BLACK]) {
                return COLOR_BLACK; //We got black
            } else if (rawSensorValue > THRESHOLD[sensor][COLOR_GREEN]) {
                return COLOR_GREEN; //We got green
            } else if (rawSensorValue > THRESHOLD[sensor][COLOR_YELLOW]){
                return COLOR_YELLOW; //We got yellow
            } else{
                return COLOR_WHITE; //We got white
            }
        } else {
            if (rawSensorValue > THRESHOLD[sensor][COLOR_BLACK]) {
                return COLOR_BLACK; //We got black
            } else{
                return COLOR_WHITE; //We got white
            }
        }
    }
    
    //private int colorsToLCD;
    
    /**
     * Analyze color values and flag sensors reading black in the blackSensors
     * variable. The flags are arranged as follows: 0000 0lmr
     */
    public void calculateBlackSensors() {
        blackSensors = 0;
        
        for (i = 0; i < SENSOR_COUNT; i++) {
            if (currentColor[i] == COLOR_BLACK) {
                blackSensors += (byte)(1 << (2-i));
                
            }            
        }        
    
        /*colorsToLCD = currentColor[RIGHT_SENSOR];
        colorsToLCD += currentColor[MIDDLE_SENSOR] * 10;
        colorsToLCD += currentColor[LEFT_SENSOR] * 100;
            
        LCD.showNumber(colorsToLCD);*/
    }
    
    /**
     * Calculate the current colors for all sensors.
     */
    public void calculateCurrentColors() {
        for (i = 0; i < SENSOR_COUNT; i++) {
            if ((colorBuffer[i][0] == colorBuffer[i][1]) && (colorBuffer[i][1] == colorBuffer[i][2])) {
                currentColor[i] = colorBuffer[i][1];
            } else {
                currentColor[i] = COLOR_UNKNOWN; //COLOR unknown
            }
        }
    }
        
    /**
     * Start a move. Ensures that the robot has moved off of the dot it's
     * standing on.
     */
    private void initMove() {
        isDriving = true;
        
        Movement.forward();
        try {
            Thread.sleep(MOVE_INIT_TIME);
        } catch (Exception e) {
            Sound.buzz();
        }
        Movement.stop();
        read();//we must ensure that the buffer is all cleared after we move the robot
        read();
    }
    
    /**
     * Turn robot left and continue forward.
    /**
     * Turn robot left and continue forward.
     * 
     * @param if sharpTurn is true a U-Turn will be performed.
     */
    public void turnLeft(boolean sharpTurn) {        
        initMove();        
        adjust();
        Movement.sharpLeft();
        
        while(isDriving){
            read();
                if ((blackSensors & 0x04) == 0x04) { // 100 110                    
                    if (sharpTurn) {
                        sharpTurn = false;
                        adjust();                        
                        isDriving = true;
                        Movement.sharpLeft();
                    } else {
                        Movement.stop();
                        isDriving = false;
                    }
                }
        }
        
        adjust();
        forward();
    }
    
    /**
     * Turn robot right and continue forward.
     * 
     * @param if sharpTurn is true a U-Turn will be performed.
     */
    public void turnRight(boolean sharpTurn) {      
        initMove();        
        adjust();
        Movement.sharpRight();
        
        while(isDriving){
            read();
                if ((blackSensors & 0x01) == 0x01) { // 001 011                    
                    if (sharpTurn) {
                        sharpTurn = false;
                        adjust();
                        isDriving = true;
                        Movement.sharpRight();
                    } else {
                        Movement.stop();
                        isDriving = false;
                    }
                }
        }
        
        adjust();        
        forward();
    }
        
    public void ascertain(){
        Movement.stop();
        Movement.backward();
        try {
            Thread.sleep(125);
        } catch (InterruptedException ex) {
        }
        Movement.stop();
        read();
        read();
        read();
    }
    
    /**
     * Move robot forward.
     */
    public void adjust(){
        isDriving = true;
        //LCD.showProgramNumber(0);
            while(isDriving){
            read();
            if (blackSensors == 3) {//011
                Movement.right();
            } else if (blackSensors == 6) {//110
                Movement.left();
            } else if (blackSensors == 4) {//100
                    Movement.sharpLeft();
            } else if (blackSensors == 1) {//001
                    Movement.sharpRight();
            }
            else{
                isDriving = false;
                Movement.stop();
            }
        }
    }
    
    
    public void backward() {
        isDriving = true;        
        Movement.backward();               
        read();//we must ensure that the buffer is all cleared after we move the robot
        read();
        
        while(isDriving) {
            read();
            if (currentColor[MIDDLE_SENSOR] == COLOR_BLACK || currentColor[MIDDLE_SENSOR] == COLOR_WHITE) {
                isDriving = false;
            }            
        }
        
        isDriving = true;
        Movement.backward();
        
        while(isDriving){
            read();
            if (blackSensors == 2){//010
                isDriving = false;
            } else if (blackSensors == 3) {//011
                Movement.backwardRight();
            } else if (blackSensors == 6) {//110
                Movement.backwardLeft();
            } else if (blackSensors == 4) {//100
                Movement.sharpLeft();
            } else if (blackSensors == 1) {//001
                Movement.sharpRight();
            } else if (blackSensors == 5) {//101
                //?
            } else if (blackSensors == 0) {//000
                isDriving = false;
            } else if (blackSensors == 7) {//111
                //?;
            }
        }        
        adjust();        
    }
    
    public void forward() {
        initMove();
        Movement.forward();
        while(isDriving){
            read();
            if (blackSensors == 2){//010
                Movement.forward();
            } else if (blackSensors == 3) {//011
                Movement.right();
            } else if (blackSensors == 6) {//110
                Movement.left();
            } else if (blackSensors == 4) {//100
                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    ascertain();
                    if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW)
                        isDriving = false;
                    Sound.beep();
                } else {
                    Movement.sharpLeft();
                }
            } else if (blackSensors == 1) {//001
                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    ascertain();
                    if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW)
                        isDriving = false;
                    Sound.beep();
                } else {
                    Movement.sharpRight();
                }
            } else if (blackSensors == 5) {//101
                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    ascertain();
                    if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW)
                        isDriving = false;
                    Sound.beep();
                } else {
                    Movement.forward();
                }
            } else if (blackSensors == 0) {//000
                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    ascertain();
                    if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW)
                        isDriving = false;
                    Sound.beep();
                } else {
                    Movement.forward();
                }
            } else if (blackSensors == 7) {//111
                waitForHelp();
                Movement.forward();
            }
        }
        Movement.stop();
    }
    
    /**
     * Wait for user to press run before continuing.
     */
    public void waitForHelp() {
        //should print something
        Sound.playTone(123,32);
//        Movement.stop();
//        while (!Button.RUN.isPressed()) {
//        }
    }
    
    public void forwardUntil(byte sensor, byte color1, byte color2)
    {
        while(currentColor[sensor] != color1 && currentColor[sensor] != color2)
        {
            Movement.forward();
            try { Thread.sleep(100); } catch (InterruptedException ex) {}
            Movement.stop();
            this.read();
            this.read();
            this.read();
        }
    }
    
    public void backwardUntil(byte sensor, byte color1, byte color2)
    {
        while(currentColor[sensor] != color1 && currentColor[sensor] != color2)
        {
            Movement.backward();
            try { Thread.sleep(100); } catch (InterruptedException ex) {}
            Movement.stop();
            this.read();
            this.read();
            this.read();
        }
    }
    
    public byte search(boolean doubleCheck)
    {
        this.pathsDiscovered = 0;
        read();
        read();
        read();
        
        Movement.forward();
        try { Thread.sleep(100); } catch (InterruptedException ex) {}
        Movement.backward();
        
        read();
        read();
        read();
        
        if(currentColor[MIDDLE_SENSOR] == COLOR_YELLOW)
        {
            if(this.currentColor[this.RIGHT_SENSOR] == this.COLOR_BLACK)
                this.pathsDiscovered |= GameCommands.TURN_RIGHT;
            if(this.currentColor[this.LEFT_SENSOR] == this.COLOR_BLACK)
                this.pathsDiscovered |= GameCommands.TURN_LEFT;
            Movement.forward();
            try { Thread.sleep(320); } catch (InterruptedException ex) {}
            Movement.stop();
            read();
            read();
            read();
            if(currentColor[MIDDLE_SENSOR] == COLOR_BLACK)
                this.pathsDiscovered |= GameCommands.FORWARD;
            Movement.backward();
            try { Thread.sleep(320); } catch (InterruptedException ex) {}
            Movement.stop();
            read();
            read();
            read();
            if((currentColor[RIGHT_SENSOR] == COLOR_BLACK) && ((pathsDiscovered & GameCommands.TURN_RIGHT) == 0))
                return search(true);
            if((currentColor[LEFT_SENSOR] == COLOR_BLACK) && ((pathsDiscovered & GameCommands.TURN_LEFT) == 0))
                return search(true);
            Movement.backward();
            try { Thread.sleep(320); } catch (InterruptedException ex) {}
            Movement.stop();
            adjust();
            read();
            read();
            read();
            if(currentColor[MIDDLE_SENSOR] == COLOR_BLACK)
                this.pathsDiscovered |= GameCommands.TURN_NUMBER;
            Movement.forward();
            try { Thread.sleep(320); } catch (InterruptedException ex) {}
            Movement.stop();
            this.tempPathsDiscovered = this.pathsDiscovered;
            if(doubleCheck)
                search(false);
            if(this.tempPathsDiscovered == this.pathsDiscovered && this.pathsDiscovered != GameCommands.TURN_NUMBER &&
                    (this.pathsDiscovered & GameCommands.TURN_LEFT)>0 && (this.pathsDiscovered & GameCommands.TURN_RIGHT)>0)
                return this.pathsDiscovered;
            else
                return search(true);
        }
        else if(currentColor[MIDDLE_SENSOR] == COLOR_GREEN)
        {
            return GameCommands.FORWARD | GameCommands.TURN_NUMBER;
        }
        else
            return -1;
    }
            
    
    /**
     * Search current node for available paths.
     * 
     * @return byte paths available.
     */
    public byte searchStateMachine(boolean doubleCheck)
    {
        this.pathsDiscovered = 0;
        read();
        read();
        read();
        
        if(this.currentColor[this.MIDDLE_SENSOR] == this.COLOR_YELLOW)
        {
            backwardUntil(MIDDLE_SENSOR, COLOR_BLACK, COLOR_WHITE);
            adjust();
            read();
            read();
            read();
            
            if(this.currentColor[this.MIDDLE_SENSOR] == this.COLOR_BLACK)
                this.pathsDiscovered |= GameCommands.TURN_NUMBER;
            
            forwardUntil(MIDDLE_SENSOR, COLOR_YELLOW, COLOR_GREEN);
            read();
            read();
            read();
            if(this.currentColor[this.MIDDLE_SENSOR] == this.COLOR_GREEN)
                return search(true);
            if(this.currentColor[this.RIGHT_SENSOR] == this.COLOR_BLACK)
                this.pathsDiscovered |= GameCommands.TURN_RIGHT;
            if(this.currentColor[this.LEFT_SENSOR] == this.COLOR_BLACK)
                this.pathsDiscovered |= GameCommands.TURN_LEFT;
            
            forwardUntil(MIDDLE_SENSOR, COLOR_BLACK, COLOR_WHITE);
            adjust();
            read();
            read();
            read();
            if(this.currentColor[this.MIDDLE_SENSOR] == this.COLOR_BLACK)
                this.pathsDiscovered |= GameCommands.FORWARD;
            
            backwardUntil(MIDDLE_SENSOR, COLOR_YELLOW, COLOR_GREEN);
            this.read();
            this.read();
            this.read();
            if(this.currentColor[this.MIDDLE_SENSOR] == this.COLOR_GREEN)
                return search(true);
            if((currentColor[RIGHT_SENSOR] == COLOR_BLACK) && ((pathsDiscovered & GameCommands.TURN_RIGHT) == 0))
                    return search(true);
            if((currentColor[LEFT_SENSOR] == COLOR_BLACK) && ((pathsDiscovered & GameCommands.TURN_LEFT) == 0))
                    return search(true);
            
//            LCD.clearSegment(Segment.SENSOR_1_ACTIVE);
//            LCD.clearSegment(Segment.SENSOR_2_ACTIVE);
//            LCD.clearSegment(Segment.SENSOR_2_VIEW);
//            LCD.clearSegment(Segment.SENSOR_3_ACTIVE);
//            LCD.clear();
//            LCD.refresh();
//            if((pathsDiscovered & GameCommands.TURN_LEFT) > 0)
//                LCD.setSegment(Segment.SENSOR_1_ACTIVE);
//            if((pathsDiscovered & GameCommands.FORWARD) > 0)
//                LCD.setSegment(Segment.SENSOR_2_ACTIVE);
//            if((pathsDiscovered & GameCommands.TURN_RIGHT) > 0)
//                LCD.setSegment(Segment.SENSOR_3_ACTIVE);
//            if((pathsDiscovered & GameCommands.TURN_NUMBER) > 0)
//                LCD.setSegment(Segment.SENSOR_2_VIEW);
//            LCD.showNumber(pathsDiscovered);
//            LCD.refresh();
            
            this.tempPathsDiscovered = this.pathsDiscovered;
            if(doubleCheck)
                search(false);
            if(this.tempPathsDiscovered == this.pathsDiscovered && this.pathsDiscovered != GameCommands.TURN_NUMBER)
                return this.pathsDiscovered;
            else
                return search(true);
        }
        else if(this.currentColor[this.MIDDLE_SENSOR] == this.COLOR_GREEN)
        {
            Movement.forward();
            try { Thread.sleep(50); } catch (InterruptedException ex) {}
            Movement.stop();
            Movement.backward();
            try { Thread.sleep(50); } catch (InterruptedException ex) {}
            Movement.stop();
//            forwardUntil(MIDDLE_SENSOR, COLOR_BLACK, COLOR_WHITE);
//            adjust();
//            backwardUntil(MIDDLE_SENSOR, COLOR_BLACK, COLOR_GREEN);
//            backwardUntil(MIDDLE_SENSOR, COLOR_BLACK, COLOR_WHITE);
//            adjust();
//            forwardUntil(MIDDLE_SENSOR, COLOR_BLACK, COLOR_GREEN);
            if(currentColor[MIDDLE_SENSOR] == COLOR_GREEN)
                return GameCommands.FORWARD | GameCommands.TURN_NUMBER;
            else
                return -1;
//            if((pathsDiscovered & GameCommands.TURN_LEFT) > 0)
//                LCD.setSegment(Segment.SENSOR_1_ACTIVE);
//            if((pathsDiscovered & GameCommands.FORWARD) > 0)
//                LCD.setSegment(Segment.SENSOR_2_ACTIVE);
//            if((pathsDiscovered & GameCommands.TURN_RIGHT) > 0)
//                LCD.setSegment(Segment.SENSOR_3_ACTIVE);
//            if((pathsDiscovered & GameCommands.TURN_NUMBER) > 0)
//                LCD.setSegment(Segment.SENSOR_2_VIEW);
//            LCD.showNumber(pathsDiscovered);
//            return pathsDiscovered;    //Forward and back available
        }
        
        return search(true);    //Not on a node
    }
}