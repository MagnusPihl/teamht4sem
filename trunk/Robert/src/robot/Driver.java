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
    private byte j = 0;
    
    private boolean isDriving;
    
    private int turnTimeout;
    private static final int MOVE_INIT_TIME = 700;//235;//+(2*(Battery.getVoltageMilliVolt()/100)); //find ud af om tallet er fornuftigt
    
    private int searchTimer;
    private byte searchLastFound;
    private byte searchSpecialCase;
    
    private int[] calibrationValues;
    private byte pathsDiscovered, tempPathsDiscovered;
    
    private String[] calibrationMessages = new String[] {"black", "green", "yellow", "white"};
    
    /** Creates a new instance of NewDrive */
    public Driver() {
        for (i = 0; i < SENSOR_COUNT; i++) {
            Sensor.SENSORS[i].setTypeAndMode(3, 0x00);
            Sensor.SENSORS[i].activate();
        }
//        Motor.A.setPower(1); //4
//        Motor.C.setPower(1); //4
        Movement.setPower(1);
        
        colorBuffer = new byte[3][3];
        currentIndex = 0;
        currentColor = new byte[3];
        calibrationValues = new int[3];
        
        searchSpecialCase = -1;
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
//            Sound.buzz();
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
        try { Thread.sleep(300); } catch (Exception e){}
        
        TextLCD.print(calibrationMessages[COLOR_BLACK]);
        for (i = 0; i < SENSOR_COUNT; i++) {
            THRESHOLD[i][COLOR_BLACK] = readRaw(i);
        }
        
        TextLCD.print(calibrationMessages[COLOR_GREEN]);
        THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN] = readRaw(MIDDLE_SENSOR);
        THRESHOLD[MIDDLE_SENSOR][COLOR_BLACK] -= (int)((THRESHOLD[MIDDLE_SENSOR][COLOR_BLACK] - THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN]) / 2f);
        
        TextLCD.print(calibrationMessages[COLOR_YELLOW]);
        THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW] = readRaw(MIDDLE_SENSOR);
        THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN] -= (int)((THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN] - THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW]) / 2f);
        
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
        
//        colorsToLCD = currentColor[RIGHT_SENSOR];
//        colorsToLCD += currentColor[MIDDLE_SENSOR] * 10;
//        colorsToLCD += currentColor[LEFT_SENSOR] * 100;
        
//        LCD.showNumber(leblacks);
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
        
//        Movement.forward();
//        try {
//            Thread.sleep(MOVE_INIT_TIME);
//        } catch (Exception e) {
//            Sound.buzz();
//        }
//        Movement.stop();
        Movement.forward();
        Movement.forward();
        read();//we must ensure that the buffer is all cleared after we move the robot
        read();
        read();
    }
    
    /**
     * Turn robot left and continue forward.
     *
     * @param if sharpTurn is true a U-Turn will be performed.
     */
    public void turnLeft(boolean sharpTurn) {
        LCD.showNumber(this.searchSpecialCase);
        if(this.searchSpecialCase == -1)
            initMove();
        if(this.searchSpecialCase <= 0 || sharpTurn == true) {
            Sound.buzz();
            if(this.searchSpecialCase > 0 && sharpTurn == true)
                sharpTurn = false;
            adjust(COLOR_BLACK);
            Movement.sharpLeft();
            isDriving = true;
            while(isDriving){
                read();
                if (blackSensors == 4 || blackSensors == 6) { // 100 110
                    if (sharpTurn) {
                        sharpTurn = false;
                        adjust(COLOR_BLACK);
                        isDriving = true;
                        Movement.sharpLeft();
                    } else {
                        Movement.stop();
                        isDriving = false;
                    }
                }
            }
            
            adjust(COLOR_BLACK);
        }
        this.searchSpecialCase = -1;
        forward();
    }
    
    /**
     * Turn robot right and continue forward.
     *
     * @param if sharpTurn is true a U-Turn will be performed.
     */
    public void turnRight(boolean sharpTurn) {
        if(this.searchSpecialCase == 1) {
            Movement.sharpLeft();
            isDriving = true;
            while(isDriving){
                read();
                if (blackSensors == 4 || blackSensors == 6) { // 100 110
                    Movement.stop();
                    isDriving = false;
                }
            }
            adjust(COLOR_BLACK);
            forward();
        } else {
            initMove();
            adjust(COLOR_BLACK);
            Movement.sharpRight();
            isDriving = true;
            
            while(isDriving){
                read();
                if (blackSensors == 1 || blackSensors == 3) { // 001 011
                    if (sharpTurn) {
                        sharpTurn = false;
                        adjust(COLOR_BLACK);
                        isDriving = true;
                        Movement.sharpRight();
                    } else {
                        Movement.stop();
                        isDriving = false;
                    }
                }
            }
            
            adjust(COLOR_BLACK);
            this.searchSpecialCase = -1;
            forward();
        }
    }
    
    public void turnLeftSearch() {
        adjust(COLOR_BLACK);
        Movement.sharpLeft();
        isDriving = true;
        
        while(isDriving) {
            read();
            if(blackSensors == 4 || blackSensors == 6)  //100 110
            {
                Movement.stop();
                isDriving = false;
            }
        }
        adjust(COLOR_BLACK);
    }
    
    public void ascertain(){
        Movement.stop();
        Movement.backward();
        try { Thread.sleep(60); } catch (InterruptedException ex) {}
        Movement.stop();
        read();
        read();
        read();
    }
    
    /**
     * Move robot forward.
     */
    public void adjust(byte color){
        Movement.stop();
        isDriving = true;
        if(color == this.COLOR_BLACK){
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
                } else{
                    isDriving = false;
                    Movement.stop();
                }
            }
        } else if(color == this.COLOR_YELLOW){
            while(isDriving){
                read();
                if ((currentColor[LEFT_SENSOR] != this.COLOR_WHITE) && (currentColor[LEFT_SENSOR] != this.COLOR_BLACK)) {
                    Movement.left();
                } else if ((currentColor[RIGHT_SENSOR] != this.COLOR_WHITE) && (currentColor[RIGHT_SENSOR] != this.COLOR_BLACK)) {
                    Movement.right();
                } else{
                    isDriving = false;
                    Movement.stop();
                }
            }
            //Sound.beep();
            //Sound.beep();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
        }
//        else if(color == this.COLOR_GREEN){
//            while(isDriving){
//                read();
//                if (currentColor[LEFT_SENSOR] != this.COLOR_WHITE) {
//                    Movement.left();
//                } else if (currentColor[RIGHT_SENSOR] != this.COLOR_WHITE) {
//                    Movement.right();
//                } else{
//                    Movement.stop();
//                    isDriving = false;
//                }
//            }
//        }
    }
    
    public void forward() {
//        initMove();
        isDriving = true;
        Movement.forward();
        Movement.forward();
        Movement.forward();
        Movement.forward();
        while(isDriving){
            read();
            read();
            read();
            if (blackSensors == 2){//010
                        j = 0;
                Movement.forward();
            } else if (blackSensors == 3) {//011
                        j = 0;
                Movement.right();
            } else if (blackSensors == 6) {//110
                        j = 0;
                Movement.left();
            } else if (blackSensors == 4) {//100
                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    adjust(currentColor[MIDDLE_SENSOR]);
                    isDriving = true;
                    ascertain();
                    if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW){
                        isDriving = false;
//                        LCD.showProgramNumber(currentColor[MIDDLE_SENSOR]);
                    }
                } else {
                    if(j < 5){
                        Movement.sharpLeft();
                        ++j;
                    } else{
                        adjust(COLOR_BLACK);
                        j = 0;
                    }
                }
            } else if (blackSensors == 1) {//001
                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    adjust(currentColor[MIDDLE_SENSOR]);
                    isDriving = true;
                    ascertain();
                    if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW){
                        isDriving = false;
//                        LCD.showProgramNumber(currentColor[MIDDLE_SENSOR]);
                }
                } else {
                    if(j < 5){
                        Movement.sharpRight();
                        ++j;
                    } else{
                        adjust(COLOR_BLACK);
                        j = 0;
                    }
                }
            } else if (blackSensors == 5) {//101
                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    ascertain();
                    adjust(currentColor[MIDDLE_SENSOR]);
                    isDriving = true;
                    if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW)
                        isDriving = false;
                } else {
                    Movement.forward();
                }
            } else if (blackSensors == 0) {//000
                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    adjust(currentColor[MIDDLE_SENSOR]);
                    isDriving = true;
                    ascertain();
                    if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN || currentColor[MIDDLE_SENSOR] == COLOR_YELLOW){
//                        LCD.showProgramNumber(currentColor[MIDDLE_SENSOR]);
                        isDriving = false;
                    }
                } else {
                    Movement.stop();
                    try {Thread.sleep(50);} catch (InterruptedException ex) {}
                    read();
                    read();
                    read();
                    if (currentColor[MIDDLE_SENSOR] == COLOR_WHITE) {
                        Movement.backward();
                    }
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
    
    public void resetDirection() {
        this.searchSpecialCase = -1;
    }
    
    public byte findOnLeft()    //Values tested for battery life 8.1
    {
        searchTimer = (int)System.currentTimeMillis();
        this.turnLeftSearch();
        searchTimer = (int)System.currentTimeMillis() - searchTimer;
        if(searchTimer < 1500)          //1300 for 7.5; 1150 for 8.1
            return 1;
        else if(searchTimer < 2300)     //2000 for 7.5-8.1
            return 2;
        else
            return 3;
    }
    
    public byte search(boolean doubleCheck) {
        this.pathsDiscovered = 0;
        read(); read(); read();
//        Movement.forward();
//        try { Thread.sleep(50); } catch (InterruptedException ex) {}
//        Movement.stop();
//        try { Thread.sleep(50); } catch (InterruptedException ex) {}
//        Movement.backward();
//        try { Thread.sleep(50); } catch (InterruptedException ex) {}
//        Movement.stop();
//        try { Thread.sleep(100); } catch (InterruptedException ex) {}
//        read(); read(); read();
        
        if(currentColor[MIDDLE_SENSOR] == COLOR_GREEN) {
            this.searchSpecialCase = -1;
            Sound.twoBeeps();
            Movement.forward();
            return (GameCommands.FORWARD | GameCommands.TURN_NUMBER);
        } else if(currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
            initMove();
            searchLastFound = findOnLeft();
            if(searchLastFound == 1) {
                this.pathsDiscovered |= GameCommands.TURN_LEFT;
                searchLastFound = findOnLeft();
                if(searchLastFound == 1) {
                    this.pathsDiscovered |= GameCommands.TURN_NUMBER;
                    searchLastFound = findOnLeft();
                    if(searchLastFound == 1) {
                        this.pathsDiscovered |= GameCommands.TURN_RIGHT;
                        searchLastFound = findOnLeft();
                        if(searchLastFound == 1) {
                            this.pathsDiscovered |= GameCommands.FORWARD;
                            //Facing FORWARD
                        }
                        //else Facing LEFT
                    } else if(searchLastFound == 2) {
                        this.pathsDiscovered |= GameCommands.FORWARD;
                        //Facing FORWARD
                    }
                } else if(searchLastFound == 2) {
                    this.pathsDiscovered |= GameCommands.TURN_RIGHT;
                    searchLastFound = findOnLeft();
                    if(searchLastFound == 1) {
                        this.pathsDiscovered |= GameCommands.FORWARD;
                        //Facing FORWARD
                    }
                    //else Facing RIGHT
                } else if(searchLastFound == 3) {
                    this.pathsDiscovered |= GameCommands.FORWARD;
                    //Facing FOWARD
                }
            } else if(searchLastFound == 2) {
                this.pathsDiscovered |= GameCommands.TURN_NUMBER;
                searchLastFound = findOnLeft();
                if(searchLastFound == 1) {
                    this.pathsDiscovered |= GameCommands.TURN_RIGHT;
                    searchLastFound = findOnLeft();
                    if(searchLastFound == 1) {
                        this.pathsDiscovered |= GameCommands.FORWARD;
                        //Facing FORWARD
                    }
                    //else Facing BACKWARD
                } else if(searchLastFound == 2) {
                    this.pathsDiscovered |= GameCommands.FORWARD;
                    //Facing FORWARD
                }
                else{
//                    Sound.beepSequence();
                }
            } else if(searchLastFound == 3) {
                this.pathsDiscovered |= GameCommands.TURN_RIGHT;
                searchLastFound = findOnLeft();
                if(searchLastFound == 1) {
                    this.pathsDiscovered |= GameCommands.FORWARD;
                    //Facing FORWARD
                }
                //else Facing RIGHT
            }
            this.searchSpecialCase = 0;
            Sound.twoBeeps();
        }
        else {
            Sound.beepSequence();
            Movement.backward();
                try {
                    Thread.sleep(400);
                } catch (InterruptedException ex) {
                }
            Movement.stop();
            this.searchSpecialCase = -1;
            this.pathsDiscovered = -1;
        }
//        else{
//            this.forward();
//            Sound.beepSequence();
//            return this.search(false);
//        }
        LCD.clearSegment(Segment.SENSOR_1_ACTIVE);
        LCD.clearSegment(Segment.SENSOR_2_ACTIVE);
        LCD.clearSegment(Segment.SENSOR_2_VIEW);
        LCD.clearSegment(Segment.SENSOR_3_ACTIVE);
//        LCD.clear();
        LCD.refresh();
        if((pathsDiscovered & GameCommands.TURN_LEFT) > 0)
            LCD.setSegment(Segment.SENSOR_1_ACTIVE);
        if((pathsDiscovered & GameCommands.FORWARD) > 0)
            LCD.setSegment(Segment.SENSOR_2_ACTIVE);
        if((pathsDiscovered & GameCommands.TURN_RIGHT) > 0)
            LCD.setSegment(Segment.SENSOR_3_ACTIVE);
        if((pathsDiscovered & GameCommands.TURN_NUMBER) > 0)
            LCD.setSegment(Segment.SENSOR_2_VIEW);
//        LCD.showNumber(pathsDiscovered);
        LCD.refresh();
        
        if(this.pathsDiscovered == (GameCommands.TURN_RIGHT | GameCommands.TURN_NUMBER))
            this.searchSpecialCase = 1;
        else if(this.pathsDiscovered == (GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER))
            this.searchSpecialCase = 2;
        else if(this.pathsDiscovered == (GameCommands.TURN_LEFT | GameCommands.TURN_RIGHT | GameCommands.TURN_NUMBER))
            this.searchSpecialCase = 3;
        
        return this.pathsDiscovered;
    }
}
