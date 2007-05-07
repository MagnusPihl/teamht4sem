/*
 * GameProxy.java
 *
 * Created on 27. marts 2007, 12:00
 *
 * Company: HT++
 *
 * @author thh
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * thh @ 27. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import josx.platform.rcx.Sound;
import robot.Controller;


public class GameProxy {
    Controller OS = Controller.getInstance();
    int address;
    LLCSocket link = new LLCSocket();
    NetworkSocket net;
    TransportSocket socket;
    InputStream in;
    OutputStream out;
    private int command = -1;
    private int directions = -1;
    private int sensor1 = -1;
    private int sensor2 = -1;
    private int sensor3 = -1;
    private int maxGreen = -1;
    private int minGreen = -1;
    private int maxBlack = -1;
    private int minBlack = -1;
    
    /**
     * Creates a new instance of GameProxy
     */
    public GameProxy(int add) {
        this.address = add;
        net = new NetworkSocket(address,0,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
        socket.setActive(true);
    }
    
    public int getcommand(){
        command = -1;
        directions = -1;
        while(command == -1){
            try {
                command = in.read();
            } catch (IOException ex) {
                
            }
        }
        if(command == GameCommands.MOVE_UP || command == GameCommands.MOVE_RIGHT || command == GameCommands.MOVE_DOWN || command == GameCommands.MOVE_LEFT){
            while(directions == -1){
                try {
                    directions = in.read();
                } catch (IOException ex) {
                    
                }
            }
        }
        // lav evt. noget timeout here.
        if(command == GameCommands.CALIBRATE){
            sensor1 = -1;
            sensor2 = -1;
            sensor3 = -1;
            maxGreen = -1;
            minGreen = -1;
            maxBlack = -1;
            minBlack = -1;
            while(sensor1 == -1){
                try {
                    sensor1 = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(sensor2 == -1){
                try {
                    sensor2 = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(sensor3 == -1){
                try {
                    sensor3 = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(minGreen == -1){
                try {
                    minGreen = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(maxGreen == -1){
                try {
                    maxGreen = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(minBlack == -1){
                try {
                    minBlack = in.read();
                } catch (IOException ex) {
                    
                }
            }
            while(maxBlack == -1){
                try {
                    maxBlack = in.read();
                } catch (IOException ex) {
                    
                }
            }
            OS.setCalibrationValues(sensor1, sensor2, sensor3, minGreen, maxGreen, minBlack, maxBlack);
        }
        
        return command;
    }
    
    public int getDirections(){
        return directions;
    }
    
    public void stopThread(){
        socket.setActive(false);
    }
    
    public void startThread(){
        socket.setActive(true);
    }
    
    public void sendMoveDone(int move){
        try {
            out.write(move);
        } catch (IOException ex) {
            
        }
        Sound.beep();
    }
}
