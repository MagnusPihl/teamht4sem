/*
 * RobotProxy.java
 *
 * Created on 9. april 2007, 22:47
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.2
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 9. april 2007 (v 1.2)
 * Added rotatePosibleDirections
 * Administrator @ 9. april 2007 (v 1.1)
 *  Towersocket made static
 * Administrator @ 9. april 2007 (v 1.0)
 * Created
 *
 *
 */

package communication;

import field.Node;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;


public class RobotProxy extends Thread{
    
    private static TowerSocket link = new TowerSocket();
    public static final int BUFFER_SIZE = 20;
    
    private NetworkSocket net;
    private TransportSocket socket;
    protected InputStream in;
    protected OutputStream out;
    
    private Semaphore semaphore;
    private int avaibleDirections = -1;
    private int input;
    
    protected byte[] writeBuffer;
    private int writeBufferIndex;
    private NonBlockingWriter writer;
    
    private byte lastPossDir;
    private byte lastDirection;
    
    /**
     * Creates a new instance of RobotProxy
     */
    public RobotProxy(int _robotID, Semaphore semaphore) {
        net = new NetworkSocket((byte)0, (byte)_robotID,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        
        writeBuffer = new byte[BUFFER_SIZE];
        writeBufferIndex = 0;
        writer = new NonBlockingWriter();        
        this.semaphore = semaphore;        
        this.lastDirection = Node.UP;
        writer.start();
    }    
         
    /**
     * Clear and kill all connections.
     */
    /*public void clear() {
        this.writer.setActive(false);
        this.writer.isAlive = false;
        this.writer.join();
        //this.socket.close();
    }  */  
    
    /**
     * Write command to non blocking write queue
     *
     * @param int containing command, only 8 least significant bits are sent.     
     */
    private void write(int b) throws IOException {
        //System.out.println("writing:" + b);
        writeBuffer[writeBufferIndex++] = (byte)b;
        
        if (writeBufferIndex == BUFFER_SIZE) {
            writeBufferIndex = 0;
        }
    }
    
    public void resetDirection() throws IOException {
        this.lastDirection = Node.UP;
        this.out.write(GameCommands.RESET_DIRECTION);
    }
    
    /**
     * Move robot in a absolute direction.
     *
     * @param direction to move.
     * @param available paths from the current node.
     */
    public void move(byte direction, byte possibleDirections) throws IOException{        
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.writer.setActive(true);
        this.write(getRotation(direction, possibleDirections));
        this.lastDirection = direction;
    }
    
    /**
     * Calculate which direction the supplied direction will be from
     * the perspective of the robot.
     *
     * @param absolute direction to game.
     * @return relative direction to robot.
     */
    private byte getRotation(byte direction, byte possibleDirections){
        switch(this.lastDirection){
            case(Node.DOWN): {
                switch(direction){
                    case(Node.DOWN): return GameCommands.FORWARD;
                    case(Node.LEFT): return GameCommands.TURN_RIGHT;
                    case(Node.RIGHT): return GameCommands.TURN_LEFT;
                    case(Node.UP): 
                        if ((possibleDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                            return GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER;
                        } else {
                            return GameCommands.TURN_LEFT;
                        }
                }
            }
            case(Node.LEFT): {
                switch(direction){
                    case(Node.DOWN): return GameCommands.TURN_LEFT;
                    case(Node.LEFT): return GameCommands.FORWARD;
                    case(Node.RIGHT):
                        if ((possibleDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                            System.out.println("le double turn");
                            return GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER;
                        } else {
                            return GameCommands.TURN_LEFT;
                        }
                    case(Node.UP): return GameCommands.TURN_RIGHT;
                }
            }
            case(Node.RIGHT): {
                switch(direction) {
                    case(Node.DOWN): return GameCommands.TURN_RIGHT; 
                    case(Node.LEFT):
                        if ((possibleDirections & GameCommands.UP) == GameCommands.UP) {
                            System.out.println("le double turn");
                            return GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER;
                        } else {
                            return GameCommands.TURN_LEFT;
                        }
                    case(Node.RIGHT): return GameCommands.FORWARD; 
                    case(Node.UP): return GameCommands.TURN_LEFT; 
                }
            }
            case(Node.UP): {
                switch(direction){
                    case(Node.DOWN): 
                        if ((possibleDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                            System.out.println("le double turn");
                            return GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER;
                        } else {
                            return GameCommands.TURN_LEFT;
                        }
                    case(Node.LEFT): return GameCommands.TURN_LEFT; 
                    case(Node.RIGHT): return GameCommands.TURN_RIGHT; 
                    case(Node.UP): return GameCommands.FORWARD; 
                }
            }
        }
        return direction;
    }
    
    /**
     * Let robot search in the specified direction. To check that robot has
     * finished move, call isMoveDone. To get directions returned 
     * call getAvailableDirections, before executing next move.
     *
     * @param absolute direction. If Node.INVALID is supplied the current node
     * will be searched and no move will be done.
     * @param available paths from the current node, ignored if directions is set
     * to Node.INVALID.
     */
    public void search(byte direction, byte possibleDirections) throws IOException{
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.writer.setActive(true);
        if (direction != Node.INVALID_DIRECTION) {
            this.write(getRotation(direction, possibleDirections) | GameCommands.DISCOVER);
            this.lastDirection = direction;
        } else {
            this.write(GameCommands.SEARCH_NODE | GameCommands.DISCOVER);
        }
    }
    
    /**
     * Get absolute directions returned by robot after search.
     *
     * @return int absolute directions.
     */
    public int getAvaibleDirections(){
        return this.avaibleDirections;
    }
    
    /**
     * Switch light on robot on or off depending on passed boolean.
     * 
     * @param boolean true if the lights should be turned on.
     */
    /**
     * Turns the light on and off
     *
     * @param on
     */
    public void lights(boolean on) throws IOException{
        if(on){
            this.out.write(GameCommands.LIGHT_ON);
        } else{
            this.out.write(GameCommands.LIGHT_OFF);
        }
    }
    
    /**
     * Check whether the robot has finished it's last move command.
     *
     * @result boolean, true if the robot has finished.
     */
    public boolean isDoneMoving(){
        try {
            this.input = this.in.read();
            if (this.input != -1) {            
                if ((this.input & 0xf0) == GameCommands.MOVE_DONE) {
                    //System.out.println(Integer.toBinaryString(input & 0x0f));
                    this.avaibleDirections = derotatePossibleDirections((byte)this.lastDirection, (byte)this.input);
                    this.semaphore.release();
                    this.writer.setActive(false);
                    return true;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * Make the robot beep once.
     */
    public void beep() throws IOException{
        this.out.write(GameCommands.BEEP);
    }
    
    /**
     * Open the underlying tower connection using a specific port. 
     * Should only be called once.
     *
     * @param String port name. Lowercase usb or com + number
     */
    public static void open(String port) {
        link.open(port);
    }
    
    /**
     *  underlying tower connection
     */
    public static void close() {
        link.close();
    }    
    
    /**
     * Set whether the nonblocking writer should send or just do nothing.
     *
     * @param boolean true if the writer should send.
     */
    public void setActive(boolean isActive) {
        this.socket.setActive(isActive);
    }
    
    /*public static byte rotatePossibleDirections(byte nodeDir, byte dirs) {
        switch(nodeDir) {
            //node - up right   down left
            //turns -   forward left right
            case Node.UP :
                //up - forward, right - right, left - left
                return (byte)(((dirs & GameCommands.UP) >> 1) |
                        ((dirs & GameCommands.RIGHT) >> 2) |
                        ((dirs & GameCommands.LEFT) << 1));
            case Node.RIGHT :
                //right - forward, up - left, down - right
                return (byte)((dirs & GameCommands.RIGHT) |
                        ((dirs & GameCommands.UP) >> 2) |
                        ((dirs & GameCommands.DOWN) >> 1));
            case Node.LEFT:
                //left - forward, up - right, down - left
                return (byte)(((dirs & GameCommands.LEFT) << 2) |
                        ((dirs & GameCommands.UP) >> 3) |
                        (dirs & GameCommands.DOWN));
            case Node.DOWN :
                //down - forward, left - right, right - left
                return (byte)(((dirs & GameCommands.DOWN) << 1) |
                        ((dirs & GameCommands.LEFT)) |
                        ((dirs & GameCommands.RIGHT) >> 1));
            default: return dirs;
        }
    }*/
    
    /**
     * Rotate relative path data from robot to match absolute directions, based
     * on the direction of the robot on the field.
     * 
     * @param byte nodeDir, absolute direction of robot.
     * @param byte dirs, relative directions from robot.
     */
    public static byte derotatePossibleDirections(byte nodeDir, byte dirs) {
        switch(nodeDir) {
            //turns -turn_number forward left right
            //node - up          right   down left
            case Node.UP :
                //forward > up, right > right, left > left, turn_number > down
                return (byte)(((dirs & GameCommands.FORWARD) << 1) |
                        ((dirs & GameCommands.TURN_RIGHT) << 2) |
                        ((dirs & GameCommands.TURN_LEFT) >> 1) |
                        ((dirs & GameCommands.TURN_NUMBER) >> 2));
            case Node.RIGHT :
                //forward > right, left > up, right > down, turn_number > left
                return (byte)((dirs & GameCommands.FORWARD) |
                        ((dirs & GameCommands.TURN_LEFT) << 2) |
                        ((dirs & GameCommands.TURN_RIGHT) << 1) |
                        ((dirs & GameCommands.TURN_NUMBER) >> 3));
            case Node.LEFT:
                //forward > left, right > up, left > down, turn_number > right
                return (byte)(((dirs & GameCommands.FORWARD) >> 2) |
                        ((dirs & GameCommands.TURN_RIGHT) << 3) |
                        (dirs & GameCommands.TURN_LEFT) |
                        ((dirs & GameCommands.TURN_NUMBER) >> 1));
            case Node.DOWN :
                //forward > down, right > left, left - right, turn_number > up
                return (byte)(((dirs & GameCommands.FORWARD) >> 1) |
                        ((dirs & GameCommands.TURN_RIGHT)) |
                        ((dirs & GameCommands.TURN_LEFT) << 1) |
                        ((dirs & GameCommands.TURN_NUMBER)));
            default: return dirs;
        }
    }
    
    //**************Start of inner-class*********************//
    public class NonBlockingWriter extends Thread {
        private boolean isActive = false;
        private boolean isAlive = true;
        private int sentIndex;
        
        public NonBlockingWriter(){
            this.sentIndex = 0;
        }
        
        public void run(){
            while(isAlive){
                if ((this.isActive)&&(this.sentIndex != writeBufferIndex)) {
                    try {
                        out.write(writeBuffer[this.sentIndex++]);
                        if(this.sentIndex==BUFFER_SIZE)
                            this.sentIndex = 0;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else{
                    try {
                        this.sleep(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        
        public void setActive(boolean isActive){
            this.isActive = isActive;
        }
    }
    //**************End of inner-class*********************//
}
