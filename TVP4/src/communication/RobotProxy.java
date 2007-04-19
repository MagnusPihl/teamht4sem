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
 */

package communication;

import field.Node;
import java.awt.RadialGradientPaint;
import java.io.IOException;
import java.util.concurrent.Semaphore;


public class RobotProxy extends Thread{
    public int robotID;
    private IRTransportSocket socket;
    private static final byte MOVE_UP = 0x00;
    private static final byte MOVE_RIGHT = 0x01;
    private static final byte MOVE_DOWN = 0x02;
    private static final byte MOVE_LEFT = 0x03;
    private static final byte MOVE_UP_DISCOVERY = 0x40;
    private static final byte MOVE_RIGHT_DISCOVERY = 0x41;
    private static final byte MOVE_DOWN_DISCOVERY = 0x42;
    private static final byte MOVE_LEFT_DISCOVERY = 0x43;
    private static final byte FLASH = 0x10;
    private static final byte LIGHT_ON = 0x11;
    private static final byte LIGHT_OFF = 0x12;
    private static final byte BEEP_ON = 0x13;
    private static final byte BEEP_OFF = 0x14;
    private static final byte SEARCH_CURRENT_NODE = 0x20;
    private static final byte CALIBRATE = 0x30;
    private static final byte NOP = -0x01;
    
    private static final int TIMEOUT = 300;
    
    protected Semaphore sema;
    private byte mode;
    private int aDirections = -1;
    private int timeout;
    private ReadInput read;
    
    
    /**
     * Creates a new instance of RobotProxy
     */
    public RobotProxy(int _robotID, Semaphore e) {
        robotID = _robotID;
        sema = e;
        read = new ReadInput();
    }
    
    public class ReadInput extends Thread {
        private IRTransportSocket socket;
        private boolean isActive = false;
        protected int input = -1;
        private int i = -1;
        
        
        public ReadInput(){
        }
        
        public void run(){
            while(true){
                while(isActive == true){
                    try {
                        i = this.socket.getInputStream().read();
                        if(i != -1){
                            handleInput(i);
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                if(isActive == false){
                    if(i!=-1){
                        i = -1;
                        input = -1;
                    }
                    try {
                        this.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
        public void handleInput(int i){
            if((i & 0xf0)==0x10){
                input = i & 0x0f;
                isActive = false;
                sema.release();
            }
        }
        
        public void doRead(boolean read){
            isActive = read;
        }
    }
    
    public void move(byte direction, byte possDir) throws IOException{
        timeout = (int)System.currentTimeMillis() + TIMEOUT;
        try {
            sema.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.socket.getOutputStream().write(direction);
        this.socket.getOutputStream().write(possDir);
        read.doRead(true);
    }
    
    public void search(int _direction) throws IOException{
        byte searchDir;
        try {
            sema.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        switch(_direction){
            case(Node.DOWN): {
                searchDir = this.MOVE_DOWN_DISCOVERY;
            }
            case(Node.LEFT): {
                searchDir = this.MOVE_LEFT_DISCOVERY;
            }
            case(Node.RIGHT): {
                searchDir = this.MOVE_RIGHT_DISCOVERY;
            }
            case(Node.UP): {
                searchDir = this.MOVE_UP_DISCOVERY;
            }
            default:{
                searchDir = this.SEARCH_CURRENT_NODE;
            }
            
        }
        this.socket.getOutputStream().write(searchDir);
        read.doRead(true);
    }
    
    public int getAvaibleDirections(){
        return read.input;
    }
    
    /**
     *
     */
    @Deprecated
    public void blink() throws IOException{
            this.socket.getOutputStream().write(this.FLASH);
    }
    
    public void lights(boolean on) throws IOException{
            if(on){
                this.socket.getOutputStream().write(this.LIGHT_ON);
            } else{
                this.socket.getOutputStream().write(this.LIGHT_OFF);
            }
    }
    
    /**
     *
     */
    @Deprecated
    public void beep(boolean on) throws IOException{
            if(on){
                this.socket.getOutputStream().write(this.BEEP_ON);
            } else{
                this.socket.getOutputStream().write(this.BEEP_OFF);
            }
    }
    
    public void calibrate(byte lOffset, byte oOffset, byte rOffset, byte minGreen, byte maxGreen, byte minBlack) throws IOException{
        byte[] outPacket = new byte[5];
        outPacket[0] = this.CALIBRATE;
        outPacket[1] = lOffset;
        outPacket[2] = oOffset;
        outPacket[3] = rOffset;
        outPacket[4] = minGreen;
        outPacket[5] = maxGreen;
        outPacket[6] = minBlack;
        this.socket.getOutputStream().write(outPacket);
    }
}
