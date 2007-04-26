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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;
import obsolete.IRTransportSocket;


public class RobotProxy extends Thread{
    
    private static final int TIMEOUT = 300;
    
    public int robotID;
    
    private int address;
    private TowerSocket link = new TowerSocket();
    private NetworkSocket net;// = new NetworkSocket(1,0,link.getInputStream(),link.getOutputStream());
    private TransportSocket socket;// = new TransportSocket(net.getInputStream(), net.getOutputStream());
    protected InputStream in;// = socket.getInputStream();
    protected OutputStream out;// = socket.getOutputStream();
    //private IRTransportSocket socket;
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
        net = new NetworkSocket(0,robotID,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        
        sema = e;
        read = new ReadInput();
    }
    
    public class ReadInput extends Thread {
        private boolean isActive = false;
        protected int input = -1;
        private int i = -1;
        
        
        public ReadInput(){
        }
        
        public void run(){
            while(true){
                    try {
                        i = in.read();
                        if(i != -1){
                            handleInput(i);
                            i = -1;
                        }
                        else{
                            try {
                                this.sleep(300);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
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
        try {
            sema.acquire();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        this.out.write(direction);
        this.out.write(possDir);
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
                searchDir = GameCommands.MOVE_DOWN;
            }
            case(Node.LEFT): {
                searchDir = GameCommands.MOVE_LEFT;
            }
            case(Node.RIGHT): {
                searchDir = GameCommands.MOVE_RIGHT;
            }
            case(Node.UP): {
                searchDir = GameCommands.MOVE_UP;
            }
            default:{
                searchDir = GameCommands.SEARCH_NODE;
            }
            
        }
        this.out.write(searchDir);
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
            //this.out.write(GameCommands.);
    }
    
    public void lights(boolean on) throws IOException{
            if(on){
                this.out.write(GameCommands.LIGHT_ON);
            } else{
                this.out.write(GameCommands.LIGHT_OFF);
            }
    }
    
    /**
     *
     */
    public void beep() throws IOException{
                this.out.write(GameCommands.BEEP);
    }
    
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
}
