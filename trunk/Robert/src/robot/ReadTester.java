package robot;
/*
 * ReadTester.java
 *
 * Created on 28. april 2007, 18:45
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 28. april 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

import communication.*;
import java.io.*;
import josx.platform.rcx.*;

/**
 *
 * @author LMK
 */
public class ReadTester implements ButtonListener {
    OutputStream out;
    int j = 1;
    
    public ReadTester() {
        LinkLayerSocket link = new LLCSocket();
        NetworkSocket net = new NetworkSocket(1,0, link.getInputStream(), link.getOutputStream());
        TransportSocket trans = new TransportSocket(net.getInputStream(), net.getOutputStream());
        InputStream in = trans.getInputStream();
        out = trans.getOutputStream();
        trans.setActive(true);
        int tal;
        int amount = 0;
        LCD.showNumber(0);
        Button.RUN.addButtonListener(this);
                
        //for (int i = 0; i < 100; i++) {
        while (true) {
            try {
                tal = in.read();
                if (tal != -1) {
                //    amount++;
                    LCD.showNumber(tal);    
                  /*  Sound.beep();
                    
                    if (amount == 2) {
                        amount = 0;
                        //LCD.showNumber(105);
                        out.write(GameCommands.MOVE_DONE);
                    }*/
                    //System.out.println(tal);
                } 
            } catch (Exception e) {
                //ioe.printStackTrace();
                //System.out.println();
            }
        }        
    }    
    
    public void buttonPressed(Button b) {

    }

    public void buttonReleased(Button b) {
        try {
            out.write(j++);
        } catch (Exception e) {}
    }
    
    public static void main(String[] args) {                
        new ReadTester();
    }    
}
