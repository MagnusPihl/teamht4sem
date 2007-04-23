/*
 * HighscoreScene.java
 *
 * Created on 1. marts 2007, 09:02
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 * ******VERSION HISTORY******
 * LMK @ 23. april 2007 (v 1.1)
 * Reflowed scores so that they are displayed nicely.
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
import java.util.Iterator;

public class HighScoreScene implements Scene {
    
    private InputAction actionBack;
    private HighScoreList highScores;
    
    /** Creates a new instance of HighscoreScene */
    public HighScoreScene() {
        this.actionBack = new InputAction("Escape", InputAction.DETECT_FIRST_ACTION);
    }
    
    private HighScoreList getHighScoreList(){
        return PacmanApp.getInstance().getGameScene().getField().getHighScores();
    }
    
    public void draw(Graphics2D _g) {
        BitmapFont font = PacmanApp.getInstance().getFont();
        _g.setColor(Color.BLACK);
        _g.fillRect(0,0,800,600);
        HighScore current;
        
        int offset = 80;        
        int j = 0;
        font.drawString(_g, "#", 20, 20, 80);
        font.drawString(_g, "Name:", 100, 20, 500);
        font.drawString(_g, "Score:", 600, 20, 200);
            
        
        for (Iterator i = this.highScores.iterator(); i.hasNext(); j++) {
            current = (HighScore)i.next();
            font.drawString(_g, (j + 1) + ".", 20, offset);
            font.drawString(_g, Integer.toString(current.getScore()), 600, offset, 180);
            offset += font.drawString(_g, current.getName(), 100, offset, 500) + 13;
        }
    }
    
    public void update(long _time) {
        if (actionBack.isPressed()) {
            PacmanApp.getInstance().showTitleScene();
        }
    }
    
    public void init(InputManager _input) {
        this.highScores = getHighScoreList();
        _input.mapToKey(this.actionBack, KeyEvent.VK_ESCAPE);
    }
    
    public void deinit(InputManager _input) {
        _input.removeKeyAssociation(KeyEvent.VK_ESCAPE);
    }    
}
