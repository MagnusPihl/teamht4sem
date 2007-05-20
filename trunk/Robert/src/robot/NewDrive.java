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

import josx.platform.rcx.*;

/**
 *
 * @author LMK
 */
public class NewDrive {
    
    private static int[][] THRESHOLD = new int[][] {
        {690,720,730}, //Sensor 1
        {700,760,770},  //Sensor 2, larger than, yellow, black
        {710,770,780},  //Sensor 3
//        {714, 748, 794},  //Sensor 1, larger than, yellow, black
//        {734, 764, 797},  //Sensor 2
//        {699, 732, 775} //Sensor 3
    };
    
    private static final byte RIGHT_SENSOR = 0;
    private static final byte MIDDLE_SENSOR = 1;
    private static final byte LEFT_SENSOR = 2;
    private static final byte SENSOR_COUNT = 3;
    private byte[][] colorBuffer;
    private byte currentIndex;
    private byte[] currentColor;
    private byte blackSensors;
    
    private static final byte COLOR_YELLOW = 0;
    private static final byte COLOR_GREEN  = 1;
    private static final byte COLOR_BLACK  = 2;
    private static final byte COLOR_WHITE  = 3;
//    private static final byte YELLOW_THRESHOLD = 2;
//    private static final byte GREEN_THRESHOLD  = 1;
//    private static final byte BLACK_THRESHOLD  = 0;
    
    private byte i; //iterator;
    
    private boolean isDriving;
    private byte turnState;  
        
    private int turnTimeout;
    private static final int TURN_TIME = 200; //find ud af om tallet er fornuftigt
    private static final int TURN_INIT_TIME = 100; //find ud af om tallet er fornuftigt
    
    private int calibrationValue;
    
    /** Creates a new instance of NewDrive */
    public NewDrive() {                
        for (i = 0; i < SENSOR_COUNT; i++) {
            Sensor.SENSORS[i].setTypeAndMode(3, 0x00);
            Sensor.SENSORS[i].activate();
        }
        Motor.A.setPower(4);
        Motor.B.setPower(4);
        
        colorBuffer = new byte[3][3];
        currentIndex = 0;
        currentColor = new byte[3];
    }
    
    
    public void calibrate() {
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
        if (rawSensorValue > THRESHOLD[sensor][COLOR_BLACK]) {
            return COLOR_BLACK; //We got black
        } else if (rawSensorValue > THRESHOLD[sensor][COLOR_GREEN]) {
            return COLOR_GREEN; //We got green
        } else if (rawSensorValue > THRESHOLD[sensor][COLOR_YELLOW]){
            return COLOR_YELLOW; //We got yellow
        } else{
            return COLOR_WHITE; //We got white
        }
    }
    
    public void calculateBlackSensors() {
        for (i = 0; i < SENSOR_COUNT; i++) {
            if ((currentColor[i] == COLOR_BLACK) || (currentColor[i] == COLOR_GREEN)) {
                blackSensors = (byte)(1 << i);
            }
        }
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
                    }
                    Movement.stop();
                    isDriving = false;
                }
            }
        }
        
        forward();
    }
    
    public void turnRight(boolean sharpTurn) {
        initMove();
        Movement.sharpRight();
        
        while(isDriving){
            LCD.showNumber(turnState+1);
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
                    }
                    Movement.stop();
                    isDriving = false;
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
            if (blackSensors == 2){                
                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN) {
                    LCD.showNumber(0010);
                    isDriving = false;
                    Movement.stop();
                } else {
                    Movement.forward();
                }
            } else if (blackSensors == 3) {
                LCD.showNumber(0011);
                Movement.right();
            } else if (blackSensors == 6) {
                LCD.showNumber(0110);
                Movement.left();
            } else if (blackSensors == 4) {
                LCD.showNumber(0100);
                if (currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    isDriving = false;
                    Movement.stop();
                } else {
                    Movement.left();
                }            
            } else if (blackSensors == 1) {
                LCD.showNumber(0001);
                if (currentColor[MIDDLE_SENSOR] == COLOR_YELLOW) {
                    isDriving = false;
                    Movement.stop();
                } else {
                    Movement.right();
                }
            } else if (blackSensors == 5) {                
                LCD.showNumber(0101);
                isDriving = false;
                Movement.stop();        
            } else if (blackSensors == 0) {     
                LCD.showNumber(0000);
                if (currentColor[MIDDLE_SENSOR] == COLOR_WHITE) {                
                    waitForHelp();
                    Movement.forward();
                } else if ((currentColor[MIDDLE_SENSOR] == COLOR_YELLOW)) {                    
                    isDriving = false;
                    Movement.stop();
                }
            } else if (blackSensors == 7) {   
                LCD.showNumber(0111);
                waitForHelp();                
//                Movement.forward();
//                if (currentColor[MIDDLE_SENSOR] == COLOR_GREEN) {
//                    isDriving = false;
//                    Movement.stop();           
//                }
                //le hvad
            }
        }
    }
    
    public void waitForHelp() {
        Movement.stop();           
        while (!Button.RUN.isPressed()) {
        }
    }
}