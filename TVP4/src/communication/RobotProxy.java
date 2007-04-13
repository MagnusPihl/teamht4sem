/*
 * RobotProxy.java
 *
 * Created on 9. april 2007, 22:47
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 *
 * Administrator @ 9. april 2007 (v 1.0)
 * __________ Changes ____________
 *
 *
 * TODO:
 * Skift navn til RobotProxy
 * Tilføj isAvailable metode som fortæller om robotten er igang med at lave noget
 * eller der kan afsendes en ny besked.
 * Tilføj int i constructoren som fortæller hvilke robot der kontaktes
 */

package communication;

import java.io.IOException;


public class RobotProxy {
    public int robotID;
    private IRTransportSocket output;
    private static final byte MOVE_UP = 0x00;
    private static final byte MOVE_RIGHT = 0x01;
    private static final byte MOVE_DOWN = 0x02;
    private static final byte MOVE_LEFT = 0x03;
    private static final byte FLASH = 0x10;
    private static final byte LIGHT_ON = 0x11;
    private static final byte LIGHT_OFF = 0x12;
    private static final byte BEEP_ON = 0x13;
    private static final byte BEEP_OFF = 0x14;
    private static final byte SET_MODE_DISCOVERY = 0x20;
    private static final byte SET_MODE_GAME = 0x21;
    private static final byte CALIBRATE = 0x30;
    private static final byte NOP = -0x01;
    
    private byte mode;
    private int aDirections = -1;
    
    
    /**
     * Creates a new instance of RobotProxy
     */
    public RobotProxy(int _robotID) {
        mode = SET_MODE_GAME;
        robotID = _robotID;
    }
    
    public void move(byte direction, byte possDir){
        try {
            this.output.getOutputStream().write(direction);
            this.output.getOutputStream().write(possDir);
        } catch (IOException ex) {
            System.out.println("Unable to send data");
        }
    }
    
    public int getAvaibleDirections(){
        if(mode == SET_MODE_DISCOVERY){
            try {
                return this.output.getInputStream().read();
            } catch (IOException ex) {
                System.out.println("Unable to read data");
            }
        }
        return -1;
    }
    
    public void blink(){
        try {
            this.output.getOutputStream().write(this.FLASH);
        } catch (IOException ex) {
            System.out.println("Unable to send FLASH");
        }
    }
    
    public void lights(boolean on){
        try {
            if(on){
                this.output.getOutputStream().write(this.LIGHT_ON);
            } else{
                this.output.getOutputStream().write(this.LIGHT_OFF);
            }
        } catch (IOException ex) {
            System.out.println("Unable to send data");
        }
    }
    
    public void beep(boolean on){
        try {
            if(on){
                this.output.getOutputStream().write(this.BEEP_ON);
            } else{
                this.output.getOutputStream().write(this.BEEP_OFF);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void setMode(int mode){
        try {
            if(mode == 1){
                this.output.getOutputStream().write(this.SET_MODE_DISCOVERY);
                mode = SET_MODE_DISCOVERY;
            } else if(mode == 0){
                this.output.getOutputStream().write(this.SET_MODE_GAME);
                mode = SET_MODE_GAME;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void calibrate(byte lOffset, byte oOffset, byte rOffset, byte minGreen, byte maxGreen, byte minBlack){
        byte[] outPacket = new byte[5];
        outPacket[0] = this.CALIBRATE;
        outPacket[1] = lOffset;
        outPacket[2] = oOffset;
        outPacket[3] = rOffset;
        outPacket[4] = minGreen;
        outPacket[5] = maxGreen;
        outPacket[6] = minBlack;
        try {
            this.output.getOutputStream().write(outPacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void isAvaible(){
        
    }
}
