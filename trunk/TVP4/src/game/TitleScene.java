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
import javax.swing.ImageIcon;
import java.io.*;

public class TitleScene implements Scene {
    
    private Field field;
    private InputAction actionUp, actionDown, actionEnter, actionQuit;
    private Image background;
    private Image[] menuItems;
    private Image[] menuItemsOn;
    private int currentItem;
    
    /** Creates a new instance of GameScene */
    public TitleScene() {
        this.background = new ImageIcon("images/title.png").getImage();
        this.actionUp = new TimedInputAction("Up", 500, 100);
        this.actionDown = new TimedInputAction("Down", 500, 100);
        this.actionEnter = new InputAction("Enter", InputAction.DETECT_FIRST_ACTION);
        this.actionQuit = new InputAction("Escape", InputAction.DETECT_FIRST_ACTION);
        this.menuItems = new Image[6];
        this.menuItemsOn = new Image[this.menuItems.length];
        
        for (int i = 0; i < this.menuItems.length; i++) {
            this.menuItems[i] = new ImageIcon("images/titleMenu_" + i + ".png").getImage();
            this.menuItemsOn[i] = new ImageIcon("images/titleMenuOn_" + i + ".png").getImage();
        }
        
        this.currentItem = 1;
    }
    
    public void draw(Graphics2D _g) {
        _g.setColor(Color.BLACK);
        _g.fillRect(0,0,800,600);
        _g.drawImage(background,150,30,null);
        
        for (int i = 0; i < this.menuItems.length; i++) {
            if (i == this.currentItem) {
                _g.drawImage(this.menuItemsOn[i], 250, 300 + i*40, null);
            } else {
                _g.drawImage(this.menuItems[i], 250, 300 + i*40, null);                
            }
        }        
    }
    
    public void update(long _time) {
        if (this.actionQuit.isPressed()) {
            System.exit(0);
        } else if (this.actionEnter.isPressed()) {
            switch (this.currentItem) {
                case 0: ; break;
                case 1: PacmanApp.getInstance().showGameScene(); break;
                case 2: PacmanApp.getInstance().showGameScene(); break;
                case 3: PacmanApp.getInstance().showGameScene(); break;
                case 4: PacmanApp.getInstance().showHighScoreScene(); break;
                case 5: PacmanApp.getInstance().showCreditsScene(); break;
                //case 5: System.exit(0); break;
            }
        } else if (this.actionUp.isPressed()) {
            this.currentItem--;
            if (this.currentItem < 0) {
                this.currentItem += this.menuItems.length;
            }
        } else if (this.actionDown.isPressed()) {
            this.currentItem++;
            if (this.currentItem >= this.menuItems.length) {
                this.currentItem = 0;
            }
        }
    }
    
    public void registerKeys(InputManager _input) {
        _input.mapToKey(this.actionEnter, KeyEvent.VK_ENTER);
        _input.mapToKey(this.actionUp, KeyEvent.VK_UP);
        _input.mapToKey(this.actionDown, KeyEvent.VK_DOWN);
        _input.mapToKey(this.actionQuit, KeyEvent.VK_ESCAPE);
    }
    
    public void unregisterKeys(InputManager _input) {
        _input.removeKeyAssociation(KeyEvent.VK_ENTER);
        _input.removeKeyAssociation(KeyEvent.VK_UP);
        _input.removeKeyAssociation(KeyEvent.VK_DOWN);
        _input.removeKeyAssociation(KeyEvent.VK_ESCAPE);
    }
}
