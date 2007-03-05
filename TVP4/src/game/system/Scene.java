/*
 * Screen.java
 *
 * Created on 26. februar 2007, 16:29
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 26. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game.system;

import java.awt.*;
import game.input.*;

/**
 *
 * @author LMK
 */
public interface Scene {
    
    /**
     * Update objects held by scene.
     *
     * @param deltaTime time since last update.
     */
    public void update(long _deltatTime);
    
    /**
     * Draw objects held by scene on supplied graphic.
     *
     * @param graphic to draw on.
     */
    public void draw(Graphics2D _g);
    
    /**
     * Initialize scene, ie. register keys, load images vice versa.
     * 
     * @param inputmanager to register keys to.
     */
    public void init(InputManager _input);
    
    /**
     * Deinitialize scene, ie. unregister keys vice versa.
     *
     * @param inputmanager to unregister keys from.
     */
    public void deinit(InputManager _input);
}
