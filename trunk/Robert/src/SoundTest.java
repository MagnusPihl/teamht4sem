/*
 * SoundTest.java
 *
 * Created on 22. februar 2007, 21:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import josx.platform.rcx.Sound;
import josx.util.*;

/**
 *
 * @author Christian Holm, 5601
 */
public class SoundTest implements TimerListener{
    
    Timer t;
    short i = 0;
    int[] freqz = {2000, 1500, 500, 900, 1100};
    /** Creates a new instance of SoundTest */
    public SoundTest() {
        t = new Timer(600, this);
        t.start();
    }
    
    public void timedOut()
    {
        Sound.playTone(freqz[i++], 20);
        if (i == 5)
            i = 0;
    }
    
}
