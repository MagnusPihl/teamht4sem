/*
 * HighscoreScene.java
 *
 * Created on 1. marts 2007, 09:02
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 1. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game;

import game.system.*;
import game.input.*;
import game.visual.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import java.io.*;

/**
 *
 * @author LMK
 */
public class HighScoreScene implements Scene {
    
    private InputAction actionBack;
    private BitmapFont font;
    
    /** Creates a new instance of HighscoreScene */
    public HighScoreScene() {        
        this.actionBack = new InputAction("Escape", InputAction.DETECT_FIRST_ACTION);
        this.font = new BitmapFont(new File("images/alfa"));
    }
    
    public void draw(Graphics2D _g) {
        _g.setColor(Color.BLACK);
        _g.fillRect(0,0,800,600);
        //this.font.drawString(_g, BitmapFont.CHARACTERS, 10, 10, 780, 5);
        this.font.drawString(_g, "Hvis din mor hun bare vidste,\nHvis din mor hun bare vidst!\nHvad ville hun så sige til dig?", 10, 10, 780, 5);
    }
    
    public void update(long _time) {
        if (actionBack.isPressed()) {
            PacmanApp.getInstance().showTitleScene();
        }
    }
    
    public void registerKeys(InputManager _input) {
        _input.mapToKey(this.actionBack, KeyEvent.VK_ESCAPE);
    }
    
    public void unregisterKeys(InputManager _input) {
        _input.removeKeyAssociation(KeyEvent.VK_ESCAPE);
    }
    
}
