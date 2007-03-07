/*
 * Robert.java
 *
 * Created on 4. marts 2007, 16:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


import josx.platform.rcx.*;
import josx.rcxcomm.*;
import java.io.*;
/**
 *
 * @author Christian Holm, 5601
 */
public class Robertnew {
    
    
    public static void main(String[] args) {
        recv();
    }
    private static void recv() {
        try {
            RCXPort port = new RCXPort();
            DataInputStream in = new
                    DataInputStream(port.getInputStream());
            byte[] buffer = new byte[4];
            while (true) {
                int bytesRead = in.read(buffer);
                
                
                if (bytesRead == 4) {
                    
                    LCD.showNumber(ByteArray.toInt(buffer));
                    Sound.beep();
                    
                } else {
                    Sound.buzz();
                }
            }
        } catch (java.io.IOException e) {
            LCD.showNumber(8888);
        }
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
        }
        System.exit(0);
    }
}