/*
 * ClearableInputStream.java
 *
 * Created on 9. maj 2007, 10:09
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
public abstract class ClearableInputStream extends InputStream {        
    
    /**
     * Clear contents of buffer
     */
    public abstract void clear();
}
