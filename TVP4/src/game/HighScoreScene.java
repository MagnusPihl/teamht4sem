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
 * Should be changed so that the list is rendered "nicely"
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
    private HighScoreList highScores;
    
    /** Creates a new instance of HighscoreScene */
    public HighScoreScene() {
        this.actionBack = new InputAction("Escape", InputAction.DETECT_FIRST_ACTION);
    }
    
    private HighScoreList getHighScoreList(){
        return PacmanApp.getInstance().getGameScene().getField().getHighScores();
    }
    
    public void draw(Graphics2D _g) {
        _g.setColor(Color.BLACK);
        _g.fillRect(0,0,800,600);
        String writeScores = this.highScores.scoreToString();
        String writeNames = this.highScores.namesToString();
        PacmanApp.getInstance().getFont().drawString(_g, writeNames, 20, 20, 580);
        PacmanApp.getInstance().getFont().drawString(_g, writeScores, 600, 20, 780);
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
