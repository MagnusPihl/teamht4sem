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
import josx.platform.rcx.LCD;
import josx.platform.rcx.TextLCD;
import robot.Controller;


public class GameProxy {
    Controller OS = Controller.getInstance();
    int address;
    //LLCSocket link = new LLCSocket();
    TowerSocket link = new TowerSocket();
    NetworkSocket net;
    TransportSocket socket;
    InputStream in;
    OutputStream out;
    private int command = -1;
    private int directions = -1;
    
    /**
     * Creates a new instance of GameProxy
     */
    public GameProxy(int add) {
        this.address = 1;
        net = new NetworkSocket(address,0,link.getInputStream(),link.getOutputStream());
        socket = new TransportSocket(net.getInputStream(), net.getOutputStream());
        in = socket.getInputStream();
        out = socket.getOutputStream();
    }
    
    public int getcommand(){
        command = -1;
        directions = -1;
        int x = 0;
        System.out.println("start");
        while(command == -1){
            try {
                x++;
                System.out.println("start " + x);
                command = in.read();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
        System.out.println("done");
        if(command == GameCommands.MOVE_UP || command == GameCommands.MOVE_RIGHT || command == GameCommands.MOVE_DOWN || command == GameCommands.MOVE_LEFT){
            while(directions == -1){
                try {
                    directions = in.read();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("Dir");
        // lav evt. noget timeout here.
        if(command == GameCommands.CALIBRATE){
            int sensor1 = -1;
            int sensor2 = -1;
            int sensor3 = -1;
            int maxGreen = -1;
            int minGreen = -1;
            int maxBlack = -1;
            int minBlack = -1;
            TextLCD.print("Cali");
            while(sensor1 == -1){
                try {
                    sensor1 = in.read();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            while(sensor2 == -1){
                try {
                    sensor2 = in.read();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            while(sensor3 == -1){
                try {
                    sensor3 = in.read();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            while(minGreen == -1){
                try {
                    minGreen = in.read();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            while(maxGreen == -1){
                try {
                    maxGreen = in.read();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            while(minBlack == -1){
                try {
                    minBlack = in.read();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            while(maxBlack == -1){
                try {
                    maxBlack = in.read();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            OS.setCalibrationValues(sensor1, sensor2, sensor3, minGreen, maxGreen, minBlack, maxBlack);
        }
        return command;
    }
    
    public int getDirections(){
        return directions;
    }
    
    public void sendMoveDone(int move){
        try {
            out.write(move);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
