/*
 * irTest.java
 *
 * Created on 22. februar 2007, 17:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import josx.platform.rcx.*;

/**
 *
 * @author Christian Holm, 5601
 */
public class irTest implements SerialListener{
    
    /** Creates a new instance of irTest */
    public irTest() {
        Serial.addSerialListener(this);
    }
    
    public void packetAvailable(byte[] packet, int length) 
    {
        Sound.beep();
        //Sound.playTone(500, 100);
//        Sound.beepSequence();
    }
    
}
