/*
 * RobotSocket.java
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
 */

package communication;

import java.io.IOException;


public class RobotSocket {
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
    private static final byte NOP = 0xFF;
    
    private byte mode;
    private int aDirections = -1;
    
    
    /** Creates a new instance of RobotSocket */
    public RobotSocket() {
        mode = SET_MODE_GAME;
    }
    
    public void move(byte direction, byte possDir){
            this.output.getOutputStream().write(direction);
            this.output.getOutputStream().write(possDir);
    }
    
    public int getAvaibleDirections(){
        if(mode == SET_MODE_DISCOVERY){
            return this.output.getInputStream().read();
        }
        return -1;
    }
    
    public void blink(){
            this.output.getOutputStream().write(this.FLASH);
    }
    
    public void lights(boolean on){
        if(on){
            this.output.getOutputStream().write(this.LIGHT_ON);
        }
        else{
            this.output.getOutputStream().write(this.LIGHT_OFF);
        }
    }
    
    public void beep(boolean on){
        if(on){
            this.output.getOutputStream().write(this.BEEP_ON);
        }
        else{
            this.output.getOutputStream().write(this.BEEP_OFF);
        }
    }
    
    public void setMode(int mode){
        if(mode == 1){
            this.output.getOutputStream().write(this.SET_MODE_DISCOVERY); 
            mode = SET_MODE_DISCOVERY;
        }
        else if(mode == 0){
            this.output.getOutputStream().write(this.SET_MODE_GAME);
            mode = SET_MODE_GAME;
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
    
    public void RobotSocket(int _robotID){
        this.robotID = _robotID;
    }
}
