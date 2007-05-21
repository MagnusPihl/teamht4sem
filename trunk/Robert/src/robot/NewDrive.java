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
public class NewDrive {
    
    private static int[][] THRESHOLD = new int[][] {        
        {710,0,0}, //Sensor 1
        {720,760,800},  //Sensor 2, larger than, yellow, black
        {700,0,0}
        
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
    
    private byte i; //iterator;
    
    private boolean isDriving;
    private byte turnState;  
        
    private int turnTimeout;
    private static final int TURN_TIME = 200; //find ud af om tallet er fornuftigt
    private static final int TURN_INIT_TIME = 150; //find ud af om tallet er fornuftigt
    
    private int[] calibrationValues;
    
    private byte pathsDiscovered;
    
    private String[] calibrationMessages = new String[] {"black", "green", "yellow", "white"};
    
    /** Creates a new instance of NewDrive */
    public NewDrive() {                
        for (i = 0; i < SENSOR_COUNT; i++) {
            Sensor.SENSORS[i].setTypeAndMode(3, 0x00);
            Sensor.SENSORS[i].activate();
        }
        Motor.A.setPower(4); //4
        Motor.B.setPower(4); //4
        
        colorBuffer = new byte[3][3];
        currentIndex = 0;
        currentColor = new byte[3];
        calibrationValues = new int[3];
    }    
    
    /*public void calibrate() {
        Sound.beepSequence();
        LCD.showNumber(15);
        while (!Button.RUN.isPressed()) {
            for (i = 0; i < SENSOR_COUNT; i++) {
                calibrationValue = Sensor.SENSORS[i].readRawValue();
                if (calibrationValue > THRESHOLD[i][COLOR_BLACK]) {
                    THRESHOLD[i][COLOR_BLACK] = calibrationValue;
                } 
                if (calibrationValue < THRESHOLD[i][COLOR_YELLOW]) {
                    THRESHOLD[i][COLOR_BLACK] = calibrationValue;
                }
            }
        }
        
        for (i = 0; i < SENSOR_COUNT; i++) {
            calibrationValue = (int)((THRESHOLD[i][COLOR_BLACK] - THRESHOLD[i][COLOR_YELLOW]) / 4f);
            THRESHOLD[i][COLOR_BLACK] -= calibrationValue;
            THRESHOLD[i][COLOR_GREEN] = THRESHOLD[i][COLOR_BLACK] - calibrationValue;
            THRESHOLD[i][COLOR_YELLOW] += calibrationValue;
        }
        LCD.showNumber(16);
    }*/
    
    private int readRaw(byte sensor) {        
        LCD.setSegment(Segment.SENSOR_1_ACTIVE + sensor*2);
//        LCD.showNumber((int)Runtime.getRuntime().freeMemory());
        while (!Button.RUN.isPressed()) {
            calibrationValues[currentIndex++] = Sensor.SENSORS[sensor].readRawValue();

            if (currentIndex == SENSOR_COUNT) {
                currentIndex = 0;
            }
        }
        LCD.clearSegment(Segment.SENSOR_1_ACTIVE + sensor*2);
        
        try {
            Thread.sleep(300);
        } catch (Exception e){
            Sound.buzz();
        }        
        while(Button.RUN.isPressed()) {}
        
        Sound.beep();
        return (int)((calibrationValues[0] + calibrationValues[1] + calibrationValues[2]) / 3f);
    }     
    
    public void calibrate() {// throws InterruptedException {          
        for (i = 0; i < SENSOR_COUNT; i += 2) {
            TextLCD.print(calibrationMessages[COLOR_BLACK]);                        
            THRESHOLD[i][COLOR_BLACK] = readRaw(i);
            
            TextLCD.print(calibrationMessages[COLOR_WHITE]);            
            THRESHOLD[i][COLOR_BLACK] -= (int)((THRESHOLD[i][COLOR_BLACK] - readRaw(i)) / 2f);
//            LCD.showNumber(THRESHOLD[i][COLOR_BLACK]);
//            Thread.sleep(1000);
        }
                        
        TextLCD.print(calibrationMessages[COLOR_BLACK]);                        
        THRESHOLD[MIDDLE_SENSOR][COLOR_BLACK] = readRaw(MIDDLE_SENSOR);
                
        TextLCD.print(calibrationMessages[COLOR_GREEN]);                        
        THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN] = readRaw(MIDDLE_SENSOR);
        THRESHOLD[MIDDLE_SENSOR][COLOR_BLACK] -= (int)((THRESHOLD[MIDDLE_SENSOR][COLOR_BLACK] - THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN]) / 2f);
//        LCD.showNumber(THRESHOLD[MIDDLE_SENSOR][COLOR_BLACK]);
//        Thread.sleep(1000);
        
        TextLCD.print(calibrationMessages[COLOR_YELLOW]);                        
        THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW] = readRaw(MIDDLE_SENSOR);
        THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN] -= (int)((THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN] - THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW]) / 2f);
//        LCD.showNumber(THRESHOLD[MIDDLE_SENSOR][COLOR_GREEN]);
//        Thread.sleep(1000);
        
        TextLCD.print(calibrationMessages[COLOR_WHITE]);
        THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW] -= (int)((THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW] - readRaw(MIDDLE_SENSOR)) / 2f);
//        LCD.showNumber(THRESHOLD[MIDDLE_SENSOR][COLOR_YELLOW]);
//        Thread.sleep(1000);
    }        
    
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
    
    private byte leblacks;
    
    public void calculateBlackSensors() {
        blackSensors = 0;
        leblacks = 0;
        
        for (i = 0; i < SENSOR_COUNT; i++) {
            if ((currentColor[i] == COLOR_BLACK) || (currentColor[i] == COLOR_GREEN)) {
                blackSensors += (byte)(1 << (2-i));
            }
        }
        if ((blackSensors & 1) != 0) {
            leblacks = 1;
        }
        if ((blackSensors & 2) != 0) {
            leblacks += 10;
        }
        if ((blackSensors & 4) != 0) {
            leblacks += 100;
        }
        LCD.showNumber(leblacks);
    }
    
    public void calculateCurrentColors() {
        for (i = 0; i < SENSOR_COUNT; i++) {
            if ((colorBuffer[i][0] == colorBuffer[i][1]) || (colorBuffer[i][1] == colorBuffer[i][2])) {
                currentColor[i] = colorBuffer[i][1];
            } else if(colorBuffer[i][0] == colorBuffer[i][2]){
                currentColor[i] = colorBuffer[i][0];
            }
        }
    }
        
    public void initMove() {
        isDriving = true;
        turnState = 0;
        
        turnTimeout = (int)System.currentTimeMillis() + TURN_INIT_TIME;        
        Movement.forward();
        while(turnTimeout > (int)System.currentTimeMillis()) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                Sound.beep();
            }
        }
        Movement.stop();
    }
    
    public void turnLeft(boolean sharpTurn) {        
        initMove();        
        Movement.sharpLeft();
        
        while(isDriving){
            read();
            
            if (turnState == 0) {
                if ((blackSensors == 4) || (blackSensors == 6)) { // 100 110                    
                    turnState = 1;
                }
            } else if(turnState == 1){
                if (blackSensors == 2) { // 010                    
                    if (sharpTurn) {
                        turnState = 0;
                        sharpTurn = false;
                    } else {
                        Movement.stop();
                        isDriving = false;
                    }
                }
            }
        }
        
        forward();
    }
    
    public void turnRight(boolean sharpTurn) {
        initMove();
        Movement.sharpRight();
        
        while(isDriving){
            read();
            
            if (turnState == 0) {
                if ((blackSensors == 1) || (blackSensors == 3)) { // 001 011                    
                    turnState = 1;
                }
            } else if(turnState == 1){
                if (blackSensors == 2) { // 010                                        
                    if (sharpTurn) {
                        turnState = 0;
                        sharpTurn = false;
                    } else {
                        Movement.stop();
                        isDriving = false;
                    }
                }
            }
        }
                
        forward();
    }
    
    public void forward() {
        initMove();
        Movement.forward();
        
        while(isDriving){
            read();
            if (blackSensors == 2){//010
                /*if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN) {
                    isDriving = false;
                    Movement.stop();
                } else {*/
                    Movement.forward();
                //}
            } else if (blackSensors == 3) {//011
                Movement.right();
            } else if (blackSensors == 6) {//110
                Movement.left();
            } else if (blackSensors == 4) {//100
                /*if (currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    isDriving = false;
                    Movement.stop();
                } else {*/
                    Movement.sharpLeft();
                //}
            } else if (blackSensors == 1) {//001
                /*if (currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    isDriving = false;
                    Movement.stop();
                } else {*/
                    Movement.sharpRight();
                //}
            } else if (blackSensors == 5) {//101
                isDriving = false;
                Movement.stop();        
            } else if (blackSensors == 0) {//000
                Movement.forward();
                /*if (currentColor[MIDDLE_SENSOR] == COLOR_WHITE) {                
                    Movemenent
                    while (!Button.RUN.isPressed()) {
                        read();
                        if ()
                    }
                    Movement.forward();
                } else if ((currentColor[MIDDLE_SENSOR] == COLOR_YELLOW)) {
                    isDriving = false;
                    Movement.stop();
                }*/
            } else if (blackSensors == 7) {//111
                waitForHelp();                
                Movement.forward();
//                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN) {
//                    isDriving = false;
//                    Movement.stop();
//                }
                //le hvad
            }
        }
    }
    
    public void waitForHelp() {
        Sound.playTone(123,32);
        Movement.stop();
        while (!Button.RUN.isPressed()) {
        }
    }
    
    public byte search()
    {
        this.pathsDiscovered = GameCommands.TURN_NUMBER;
        this.read();
        
        if(this.currentColor[this.MIDDLE_SENSOR] == this.COLOR_YELLOW)
        {
            if ((this.currentColor[this.RIGHT_SENSOR] == this.COLOR_BLACK) || (this.currentColor[this.RIGHT_SENSOR] == this.COLOR_GREEN))
                this.pathsDiscovered |= GameCommands.TURN_RIGHT;
            if ((this.currentColor[this.LEFT_SENSOR] == this.COLOR_BLACK) || (this.currentColor[this.LEFT_SENSOR] == this.COLOR_GREEN))
                this.pathsDiscovered |= GameCommands.TURN_LEFT;
            
            while(this.currentColor[this.MIDDLE_SENSOR] == this.COLOR_YELLOW)
            {
                Movement.forward();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    //ex.printStackTrace();
                }
                Movement.stop();
                this.read();
            }
            if(this.currentColor[this.MIDDLE_SENSOR] == this.COLOR_BLACK)
                this.pathsDiscovered |= GameCommands.FORWARD;
            while(this.currentColor[this.MIDDLE_SENSOR] != this.COLOR_YELLOW)
            {
                Movement.backward();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    //ex.printStackTrace();
                }
                Movement.stop();
                this.read();
            }
            return this.pathsDiscovered;
        }
        else if(this.currentColor[this.MIDDLE_SENSOR] == this.COLOR_GREEN)
            return this.pathsDiscovered;    //Forward and back available
        
        return 0;    //Not on a node
    }
}
