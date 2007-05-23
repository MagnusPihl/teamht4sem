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
    private NetworkSocket net;// = new NetworkSocket(1,0,link.getInputStream(),link.getOutputStream());
    private TransportSocket socket;// = new TransportSocket(net.getInputStream(), net.getOutputStream());
    protected InputStream in;// = socket.getInputStream();
    protected OutputStream out;// = socket.getOutputStream();
    
    private Semaphore sema;
    private int avaibleDirections = -1;
    
    protected byte[] writeBuffer;
    public static final int BUFFER_SIZE = 20;
    private int writeBufferIndex;
    private NonBlockingWriter writer;
    
    private byte lastPossDir;
    private byte lastDir;
    /**
     * Creates a new instance of RobotProxy
     */
    public RobotProxy(int _robotID, Semaphore e) {
        net = new NetworkSocket((byte)0, (byte)_robotID,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        
        writeBuffer = new byte[BUFFER_SIZE];
        writeBufferIndex = 0;
        writer = new NonBlockingWriter();        
        sema = e;
    }
    
    public void init(byte curDirs){
        this.lastDir = Node.UP;
        this.lastPossDir = curDirs;
        writer.start();
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
    
    
    private void write(int b) throws IOException {
        System.out.println("writing:" + b);
        writeBuffer[writeBufferIndex++] = (byte)b;
        
        if (writeBufferIndex == BUFFER_SIZE) {
            writeBufferIndex = 0;
        }
    }
    
    public void move(byte direction, byte possDir) throws IOException{
        //byte possDirs = rotatePossibleDirections(direction, possDir);
        try {
            sema.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.writer.setActive(true);
        this.write(getRotation(direction));
        //this.write(possDirs);
        this.lastDir = direction;
        this.lastPossDir = possDir;
    }
    
    private byte getRotation(byte direction){
        byte searchDir = direction;
        switch(this.lastDir){
            case(Node.DOWN): {
                switch(direction){
                    case(Node.DOWN): searchDir = GameCommands.FORWARD; break;
                    case(Node.LEFT): searchDir = GameCommands.TURN_RIGHT; break;
                    case(Node.RIGHT): searchDir = GameCommands.TURN_LEFT; break;
                    case(Node.UP): searchDir = calcRotation(); break;
                }
                break;
            }
            case(Node.LEFT): {
                switch(direction){
                    case(Node.DOWN): searchDir = GameCommands.TURN_LEFT; break;
                    case(Node.LEFT): searchDir = GameCommands.FORWARD; break;
                    case(Node.RIGHT): searchDir = calcRotation(); break;
                    case(Node.UP): searchDir = GameCommands.TURN_RIGHT; break;
                }
                break;
            }
            case(Node.RIGHT): {
                switch(direction) {
                    case(Node.DOWN): searchDir = GameCommands.TURN_RIGHT; break;
                    case(Node.LEFT): searchDir = calcRotation(); break;
                    case(Node.RIGHT): searchDir = GameCommands.FORWARD; break;
                    case(Node.UP): searchDir = GameCommands.TURN_LEFT; break;
                }
                break;
            }
            case(Node.UP): {
                switch(direction){
                    case(Node.DOWN): searchDir = calcRotation(); break;
                    case(Node.LEFT): searchDir = GameCommands.TURN_LEFT; break;
                    case(Node.RIGHT): searchDir = GameCommands.TURN_RIGHT; break;
                    case(Node.UP): searchDir = GameCommands.FORWARD; break;
                }
                break;
            }
        }
        return searchDir;
    }
    
    private byte calcRotation(){
        byte reByte = 0;
        byte bitmask = (byte)(this.lastDir & 0x05);
        switch(bitmask){
            case(5): {
                reByte = GameCommands.TURN_LEFT | GameCommands.TURN_NUMBER; break;
            }
            case(4): {
                reByte = GameCommands.TURN_LEFT; break;
            }
            case(1): {
                reByte = GameCommands.TURN_RIGHT; break;
            }
            case(0): {
                reByte = GameCommands.TURN_LEFT; break;
            }
        }
        return reByte;
    }
    
    public void search(byte _direction) throws IOException{
        try {
            sema.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.writer.setActive(true);
        if (_direction != Node.INVALID_DIRECTION) {
            this.write(getRotation(_direction) | GameCommands.DISCOVER);
            this.lastDir = _direction;
        } else {
            this.write(GameCommands.SEARCH_NODE | GameCommands.DISCOVER);
        }
    }
    
    public int getAvaibleDirections(){
        return this.avaibleDirections;
    }
    
    /**
     *
     */
    @Deprecated
    public void blink() throws IOException{
        //this.out.write(GameCommands.);
    }
    
    public void lights(boolean on) throws IOException{
        if(on){
            this.out.write(GameCommands.LIGHT_ON);
        } else{
            this.out.write(GameCommands.LIGHT_OFF);
        }
    }
    
    public boolean isDoneMoving(){
        try {
            int input = this.in.read();
            if (input != -1) {            
                if((input&0xf0) == GameCommands.MOVE_DONE) {
                    //System.out.println(Integer.toBinaryString(input & 0x0f));
                    this.avaibleDirections = derotatePossibleDirections((byte)this.lastDir, (byte)input);
                    this.sema.release();
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
     *
     */
    public void beep() throws IOException{
        this.out.write(GameCommands.BEEP);
    }
    
    public static void open(String port) {
        link.open(port);
    }
    
    public static void close() {
        link.close();
    }
    
    /*public void close() {
        this.writer.isAlive = false;
        this.writer.join();
        this.socket.close();
    }*/
    
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
     * Rotate path data from
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
    
    public void clear() {
        this.writer.setActive(false);
    }
}
