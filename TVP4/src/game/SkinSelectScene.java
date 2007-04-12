/*
 * SkinSelectScene.java
 *
 * Created on 29. marts 2007, 09:02
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

public class SkinSelectScene implements Scene {
    
    private InputAction actionUp, actionDown, actionEnter, actionQuit;
    private HighScoreList highScores;
    private Image[] skinImages;
    private File[] skins;
    private Image questionText;
    private FileFilter fileFilter;
    private int selectedSkin;
    
    /** Creates a new instance of HighscoreScene */
    public SkinSelectScene() {
        this.actionUp = new TimedInputAction("Up", 500, 100);
        this.actionDown = new TimedInputAction("Down", 500, 100);
        this.actionEnter = new InputAction("Enter", InputAction.DETECT_FIRST_ACTION);
        this.actionQuit = new InputAction("Escape", InputAction.DETECT_FIRST_ACTION);
        this.fileFilter = new SkinFileFilter();
    }
    
    public void draw(Graphics2D _g) {        
        _g.setColor(Color.BLACK);
        _g.fillRect(0,0,800,600);
        
        _g.drawImage(this.questionText, 30, 30, null);
        /*PacmanApp.getInstance().getFont().drawString(_g,
                    "Choose a skin from the list and press enter. Press escape to get back to main menu:", 30, 30, 740);        */
        
        for (int i = 0; i < this.skinImages.length; i++) {
            if (this.selectedSkin == i) {                
                _g.drawImage(this.skinImages[i], 30, 200 + i*55, null);
            }
            _g.drawImage(this.skinImages[i], 30, 200 + i*55, null);
        }
    }
    
    public void update(long _time) {
        if (this.actionQuit.isPressed()) {
            PacmanApp.getInstance().showTitleScene();
        } else if (this.actionEnter.isPressed()) {
            TileSet.getInstance().loadTileSet(this.skins[this.selectedSkin]);
            PacmanApp.getInstance().showTitleScene();
        } else if (this.actionDown.isPressed()) {            
            this.selectedSkin--;
            if (this.selectedSkin < 0) {
                this.selectedSkin = this.skinImages.length - 1;
            }
        } else if (this.actionUp.isPressed()) {
            this.selectedSkin++;
            if (this.selectedSkin == this.skinImages.length) {
                this.selectedSkin = 0;
            }
        }
    }
    
    public void init(InputManager _input) {        
        if (this.questionText == null) {
            this.questionText = PacmanApp.getInstance().getFont().renderString(
                    "Choose a skin from the list and press enter. Press escape to get back to main menu:", 740);        
        }
        
        this.selectedSkin = 0;
        this.getAvailbleSkins();
        
        _input.mapToKey(this.actionEnter, KeyEvent.VK_ENTER);
        _input.mapToKey(this.actionUp, KeyEvent.VK_UP);
        _input.mapToKey(this.actionDown, KeyEvent.VK_DOWN);
        _input.mapToKey(this.actionQuit, KeyEvent.VK_ESCAPE);
    }
    
    public void deinit(InputManager _input) {
        _input.removeKeyAssociation(KeyEvent.VK_ENTER);
        _input.removeKeyAssociation(KeyEvent.VK_UP);
        _input.removeKeyAssociation(KeyEvent.VK_DOWN);
        _input.removeKeyAssociation(KeyEvent.VK_ESCAPE);
    }    
    
    private void getAvailbleSkins() {        
        BitmapFont font = PacmanApp.getInstance().getFont();
        
        this.skins = (new File(TileSet.SKIN_LIBRARY)).listFiles(this.fileFilter);        
        this.skinImages = new Image[this.skins.length];
        
        for (int i = 0; i < this.skins.length; i++) {
            this.skinImages[i] = font.renderString(this.skins[i].getName(),740);
        }
    }
}
