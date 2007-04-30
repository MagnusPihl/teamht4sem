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


public class RobotProxy extends Thread{
    
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
    private int avaibleDirections = -1;
    private int timeout;
    
    private int input;
    
    
    /**
     * Creates a new instance of RobotProxy
     */
    public RobotProxy(int _robotID, Semaphore e) {
        robotID = _robotID;
        net = new NetworkSocket(0,1,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        
        sema = e;
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
        
        //System.out.println("1");
        try {
            sema.acquire();
            //System.out.println("2");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        //System.out.println("3");
        boolean success = false;
        while(!success)
        {
            try {
                this.out.write(searchDir);
                success = true;
            }
            catch(IOException e) {
                //System.out.println(e.getMessage());
                success = false;
            }
        }
        //System.out.println("4");
        success = false;
        while(!success)
        {
            try {
                this.out.write(possDir);
                success = true;
            }
            catch(IOException e) {
                //System.out.println(e.getMessage());
                success = false;
            }
        }
        
        int result=-1;
        while(result == -1)
            result = in.read();
//        System.out.println("Move done: "+result);
        if((result & 0xf0)==0x10)
        {
            this.input = result & 0x0f;
            sema.release();
        }
        
        //System.out.println("5");
    }
    
    public void search(int _direction) throws IOException{
        byte searchDir;
        try {
            sema.acquire();
        } catch (InterruptedException ex) {
//            ex.printStackTrace();
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
        this.out.write(searchDir | GameCommands.DISCOVER);
    }
    
    public int getAvaibleDirections(){
        return this.input;
    }
    
    /**
     *
     */
    public void blink() throws IOException{
            //this.out.write(GameCommands.);
    }
    
    public void lights(boolean on) throws IOException{
            if(on){
                int i = GameCommands.LIGHT_ON;
                this.out.write(i);
            } else{
                this.out.write(GameCommands.LIGHT_OFF);
            }
    }
    
    public boolean isDoneMoving(){
        try {
            int i = this.in.read();
            if((i&0xf0) == GameCommands.MOVE_DONE){
                this.avaibleDirections = (i&0x0f);
                this.sema.release();
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
    
    public void setActive(boolean isActive){
        this.socket.setActive(isActive);
    }
}
