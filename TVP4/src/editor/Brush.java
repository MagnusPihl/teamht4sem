/*
 * Brush.java
 *
 * Created on 14. februar 2007, 18:26
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 14. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package editor;

import java.awt.event.*;

/**
 *
 * @author LMK
 */

public abstract class Brush implements MouseMotionListener, MouseListener {
    
    protected EditorPanel panel;
    
    public Brush(EditorPanel panel) {
        this.panel = panel;
    }
    
}
