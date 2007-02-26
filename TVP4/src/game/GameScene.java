/*
 * GameScene.java
 *
 * Created on 26. februar 2007, 16:41
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

package game;

import game.system.*;
import game.input.*;
import field.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class GameScene implements Scene {
    
    private Field field;
    private InputAction action;
    
    /** Creates a new instance of GameScene */
    public GameScene() {
        TileSet.getInstance().loadTileSet(new File(TileSet.SKIN_LIBRARY + "pacman/"));   
        field = new Field();
        field.loadFrom(new File("D:\\Documents and Settings\\LMK\\Dokumenter\\test'.lvl"));
        action = new InputAction("En handling");
    }
    
    public void draw(Graphics2D _g) {
        field.drawField(_g, new Dimension(800,600));
        
        _g.setColor(Color.WHITE);
        if (action.isPressed()) {
            _g.drawString("Huhej hvor jeg trykker på enter", 350,295);
        }                 
    }            

    public void update(long _time) {
    }

    public void registerKeys(InputManager _input) {
        _input.mapToKey(action, KeyEvent.VK_ENTER);        
    }
    
    public void unregisterKeys(InputManager _input) {
        _input.removeAssociation(action);
    }
}
