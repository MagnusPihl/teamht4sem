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
    private static TowerSocket link = new TowerSocket();
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
        read.start();
    }
    
    public class ReadInput extends Thread {
        private boolean isActive = false;
        protected int input = -1;
        private int i = -1;
        
        
        public ReadInput(){
        }
        
        public void setActive(boolean _active){
            this.isActive = _active;
        }
        
        public void run(){
            while(true){
                try {
                    if(this.isActive){
                        i = in.read();
                        System.out.println("Read: "+i);
                        if(i != -1){
                            handleInput(i);
                            i = -1;
                        }
                    }
                    try {
                        this.sleep(300);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        public void handleInput(int i){
            if((i & 0xf0)==0x10){
                input = i & 0x0f;
                //isActive = false;
                sema.release();
            }
        }
    }
    
    public void move(byte direction, byte possDir) throws IOException{
        byte searchDir;
        switch(direction){
            case(Node.DOWN): {
                searchDir = GameCommands.MOVE_DOWN; break;
            }
            case(Node.LEFT): {
                searchDir = GameCommands.MOVE_LEFT; break;
            }
            case(Node.RIGHT): {
                searchDir = GameCommands.MOVE_RIGHT; break;
            }
            case(Node.UP): {
                searchDir = GameCommands.MOVE_UP; break;
            }
            default: return;            
        }
        
        System.out.println("1");
        try {
            sema.acquire();
            System.out.println("2");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("2.5");
        read.setActive(false);
        try {
            this.out.write(searchDir);
            System.out.println("3");
            this.out.write(possDir);
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("4");
        read.setActive(true);
        System.out.println("5");
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
                searchDir = GameCommands.MOVE_DOWN; break;
            }
            case(Node.LEFT): {
                searchDir = GameCommands.MOVE_LEFT; break;
            }
            case(Node.RIGHT): {
                searchDir = GameCommands.MOVE_RIGHT; break;
            }
            case(Node.UP): {
                searchDir = GameCommands.MOVE_UP; break;
            }
            default:{
                searchDir = GameCommands.SEARCH_NODE; break;
            }
            
        }
        read.setActive(true);
        this.out.write(searchDir | GameCommands.DISCOVER);
        read.setActive(false);
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
