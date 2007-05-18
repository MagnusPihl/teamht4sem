/*
 * ClearableOutputStream.java
 *
 * Created on 9. maj 2007, 10:11
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 9. maj 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package communication;

import java.io.*;

/**
 *
 * @author LMK
 */
public abstract class ClearableOutputStream extends OutputStream {    
    
    /**
     * Clear contents of buffer
     */
    public abstract void clear();
}
