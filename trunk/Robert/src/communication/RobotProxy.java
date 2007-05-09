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
 * Administrator @ 9. april 2007 (v 1.1)
 *  Towersocket made static
 * Administrator @ 9. april 2007 (v 1.0)
 * Created
 *
 *
 */

package communication;

import communication.Node;
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
        net = new NetworkSocket(0, _robotID,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        
        writeBuffer = new byte[BUFFER_SIZE];
        writer = new NonBlockingWriter();
        writer.start();
        
        sema = e;
    }
    
    public void init(byte curDir){
        this.lastDir = curDir;
        this.writeBufferIndex = 0;
    }
    
    //**************Start of inner-class*********************//
    public class NonBlockingWriter extends Thread {
        private boolean isActive = false;
        private int sentIndex;
        
        public NonBlockingWriter(){
            this.sentIndex = 0;
        }
        
        public void run(){
            while(true){
                if ((this.isActive)&&(this.sentIndex != writeBufferIndex)) {
                    try {
                        out.write(writeBuffer[this.sentIndex++]);
                        if(this.sentIndex==BUFFER_SIZE)
                            this.sentIndex = 0;
                    } catch (IOException ex) {
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
        writeBuffer[writeBufferIndex++] = (byte)b;
        
        if (writeBufferIndex == BUFFER_SIZE) {
            writeBufferIndex = 0;
        }
    }
    
    public void move(byte direction, byte possDir) throws IOException{
        byte possDirs = rotatePossibleDirections(direction, possDir);
        byte searchDir = getRotation(direction);
        
        try {
            sema.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.writer.setActive(true);
        this.write(searchDir);
        this.write(possDirs);
        this.lastDir = direction;
    }
    
    private byte getRotation(byte direction){
        byte searchDir = 0;
        switch(this.lastDir){
            case(Node.DOWN): {
                switch(direction){
                    case(Node.DOWN): {
                        searchDir = GameCommands.FORWARD; break;
                    }
                    case(Node.LEFT): {
                        searchDir = GameCommands.TURN_RIGHT; break;
                    }
                    case(Node.RIGHT): {
                        searchDir = GameCommands.TURN_LEFT; break;
                    }
                    case(Node.UP): {
                        searchDir = calcRotation(); break;
                    }
                }
            }
            case(Node.LEFT): {
                switch(direction){
                    case(Node.DOWN): {
                        searchDir = GameCommands.TURN_LEFT; break;
                    }
                    case(Node.LEFT): {
                        searchDir = GameCommands.FORWARD; break;
                    }
                    case(Node.RIGHT): {
                        searchDir = calcRotation(); break;
                    }
                    case(Node.UP): {
                        searchDir = GameCommands.TURN_RIGHT; break;
                    }
                }
            }
            case(Node.RIGHT): {
                switch(direction){
                    case(Node.DOWN): {
                        searchDir = GameCommands.TURN_RIGHT; break;
                    }
                    case(Node.LEFT): {
                        searchDir = calcRotation(); break;
                    }
                    case(Node.RIGHT): {
                        searchDir = GameCommands.FORWARD; break;
                    }
                    case(Node.UP): {
                        searchDir = GameCommands.TURN_LEFT; break;
                    }
                }
            }
            case(Node.UP): {
                switch(direction){
                    case(Node.DOWN): {
                        searchDir = calcRotation(); break;
                    }
                    case(Node.LEFT): {
                        searchDir = GameCommands.TURN_LEFT; break;
                    }
                    case(Node.RIGHT): {
                        searchDir = GameCommands.TURN_RIGHT; break;
                    }
                    case(Node.UP): {
                        searchDir = GameCommands.FORWARD; break;
                    }
                }
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
        byte searchDir = getRotation(_direction);
        try {
            sema.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.write(searchDir | GameCommands.DISCOVER);
    }
    
    public int getAvaibleDirections(){
        return this.avaibleDirections;
    }
    
    /**
     *@Deprecated
     */
    
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
            if((input&0xf0) == GameCommands.MOVE_DONE){
                this.avaibleDirections = (input&0x0f);
                this.sema.release();
                this.writer.setActive(false);
                return true;
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
    
    /**
     *@Deprecated
     */
    
    public void calibrate(byte lOffset, byte oOffset, byte rOffset, byte minGreen, byte maxGreen, byte minBlack) throws IOException{
        byte[] outPacket = new byte[5];
        outPacket[0] = GameCommands.CALIBRATE;
        outPacket[1] = lOffset;
        outPacket[2] = oOffset;
        outPacket[3] = rOffset;
        outPacket[4] = minGreen;
        outPacket[5] = maxGreen;
        outPacket[6] = minBlack;
        this.out.write(outPacket);
    }
    
    public static void open(String port) {
        link.open(port);
    }
    
    public static void close() {
        link.close();
    }
    
    public void setActive(boolean isActive) {
        this.socket.setActive(isActive);
    }
    
    private byte rotatePossibleDirections(byte nodeDir, byte dirs) {
        switch(nodeDir) {
            //turn right
            case Node.RIGHT :
                return (byte)(((dirs << 1) & 0x0F) | ((dirs & GameCommands.UP) >> 3));
            case Node.LEFT:
                return (byte)((dirs >> 1) | ((dirs & GameCommands.LEFT) << 3));
            case Node.DOWN :
                return (byte)((dirs & GameCommands.UP >> 3) |
                        (dirs & GameCommands.RIGHT >> 1) |
                        (dirs & GameCommands.DOWN << 1) |
                        (dirs & GameCommands.LEFT << 3));
            default: return dirs;
        }
    }
}
