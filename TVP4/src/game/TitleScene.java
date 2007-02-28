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
    private InputAction actionUp, actionDown, actionEnter;
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
        
        
    /*_g.setColor(Color.WHITE);
        if (action.isPressed()) {
            _g.drawString("Huhej hvor jeg trykker på enter", 350,295);
        } */
    }
    
    public void update(long _time) {
        if (actionEnter.isPressed()) {
            switch (this.currentItem) {
                case 0: ; break;
                case 1: PacmanApp.getInstance().showGameScene(); break;
                case 2: PacmanApp.getInstance().showGameScene(); break;
                case 3: PacmanApp.getInstance().showGameScene(); break;
                case 4: PacmanApp.getInstance().showHighscoreScene(); break;
                case 5: System.exit(0); break;
            }
        } else if (actionUp.isPressed()) {
            this.currentItem--;
            if (this.currentItem < 0) {
                this.currentItem += this.menuItems.length;
            }
        } else if (actionDown.isPressed()) {
            this.currentItem++;
            if (this.currentItem >= this.menuItems.length) {
                this.currentItem = 0;
            }
        }
    }
    
    public void registerKeys(InputManager _input) {
        _input.mapToKey(actionEnter, KeyEvent.VK_ENTER);
        _input.mapToKey(actionUp, KeyEvent.VK_UP);
        _input.mapToKey(actionDown, KeyEvent.VK_DOWN);
    }
    
    public void unregisterKeys(InputManager _input) {
        _input.removeKeyAssociation(KeyEvent.VK_ENTER);
        _input.removeKeyAssociation(KeyEvent.VK_UP);
        _input.removeKeyAssociation(KeyEvent.VK_DOWN);
    }
}
