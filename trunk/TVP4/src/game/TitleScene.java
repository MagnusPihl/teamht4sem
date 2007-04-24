/*
 * GameScene.java
 *
 * Created on 26. februar 2007, 16:41
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.3
 *
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 24. april 2007 (v 1.3)
 * New Game and Continue now automatically apply option settings before start, even if the user didn't enter the options scene.
 * Replay now works again, setting controllers properly.
 *
 * Magnus Hemmer Pihl @ 18. april 2007 (v 1.2)
 * Removed calls to GameScene.setMode (now deprecated). Will need to implement new method of setting Replay mode.
 * Removed online/offline selection, as that's now in the Options screen.
 *
 * Magnus Hemmer Pihl @ 11. april 2007 (v 1.1)
 * Removed skin selection dialog and added options menu.
 *
 * LMK @ 26. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game;

import game.entitycontrol.ReplayController;
import game.system.*;
import game.input.*;
import game.visual.*;
import field.*;
import game.visual.TileSet;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class TitleScene implements Scene {
    
    private Field field;
    private InputAction actionUp, actionDown, actionEnter, actionQuit;
    private Image background;
    private Image[] menuItems;    
    private Image onlineImage;    
    private Image pointer;
    private int currentItem;
    private boolean isOnline;
    private JFileChooser openLevelDialog, openReplayDialog, selectSkinDialog;
    
    public static final String[] MENU_ITEMS = new String[] {"New Game", "Continue", "View Replay", "View High Scores", "Options" ,"Quit"};
    
    /** Creates a new instance of GameScene */
    public TitleScene() {
        this.background = new ImageIcon("images/title.png").getImage();
        this.actionUp = new TimedInputAction("Up", 500, 100);
        this.actionDown = new TimedInputAction("Down", 500, 100);
        this.actionEnter = new InputAction("Enter", InputAction.DETECT_FIRST_ACTION);
        this.actionQuit = new InputAction("Escape", InputAction.DETECT_FIRST_ACTION);
        this.pointer = new ImageIcon("images/pointer.png").getImage();        
        this.currentItem = 1;
        this.isOnline = false;
        
        this.openLevelDialog = new JFileChooser();
        this.openLevelDialog.setSelectedFile(new File("\\."));
        this.openLevelDialog.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {                
                return ((f.getName().toLowerCase().endsWith(".lvl") && f.isFile()) || (f.isDirectory()));
            }
            public String getDescription() {
                return "Level files";
            }
        });
        
        this.openReplayDialog = new JFileChooser();
        this.openReplayDialog.setSelectedFile(new File(""));
        this.openReplayDialog.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {                
                return ((f.getName().toLowerCase().endsWith(".rpl") && f.isFile()) || (f.isDirectory()));
            }
            public String getDescription() {
                return "Replay files";
            }
        });
    }
    
    public void draw(Graphics2D _g) {
        _g.setColor(Color.BLACK);
        _g.fillRect(0,0,800,600);
        _g.drawImage(background,150,30,null);
        
        _g.drawImage(this.pointer,200, 275 + 40 * this.currentItem, null);
        
        if (this.isOnline) {
            if (this.currentItem == 0) {
                _g.drawImage(this.onlineImage, 250, 280 + 0, null);            
            }
            _g.drawImage(this.onlineImage, 250, 280, null);
        } else {
            _g.drawImage(this.menuItems[0], 250, 280, null);
        }
        
        
        if (!this.isOnline) {
            _g.drawImage(this.menuItems[this.currentItem], 250, 280 + this.currentItem*40, null);            
        }
        
        for (int i = 1; i < this.menuItems.length; i++) {
            _g.drawImage(this.menuItems[i], 250, 280 + i*40, null);            
            
        }        
    }
    
    public void update(long _time) {
        if (this.actionQuit.isPressed()) {
            System.exit(0);
        } else if (this.actionEnter.isPressed()) {
            switch (this.currentItem) {
                case 0: this.newGame(); break;
                case 1: this.continueGame(); break;
                case 2: this.replayGame(); break;
                case 3: PacmanApp.getInstance().showHighScoreScene(); break;
                case 4: PacmanApp.getInstance().showOptionsScene(); break;
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
    
    public void init(InputManager _input) {
        this.prepareMenu();
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
    
    public void newGame() {
        PacmanApp.getInstance().getOptionsScene().apply();
        if (this.openLevelDialog.showOpenDialog(
                PacmanApp.getInstance().getCore().getScreenManager().getFullScreenWindow()) == JFileChooser.APPROVE_OPTION) {
            File file = this.openLevelDialog.getSelectedFile();
            PacmanApp.getInstance().getGameScene().setLevel(file);
            PacmanApp.getInstance().showGameScene();             
        }
    }
    
    public void continueGame() {
        PacmanApp.getInstance().getOptionsScene().apply();
        PacmanApp.getInstance().showGameScene();
    }
    
    public void replayGame() {
        if (this.openReplayDialog.showOpenDialog(
                PacmanApp.getInstance().getCore().getScreenManager().getFullScreenWindow()) == JFileChooser.APPROVE_OPTION) {
            File file = this.openReplayDialog.getSelectedFile();
            PacmanApp.getInstance().getGameScene().setReplay(file);
            for(int i=0; i<3; i++)
            {
                Entity e = PacmanApp.getInstance().getGameScene().getEntity(i);
                e.setController(new ReplayController(e, PacmanApp.getInstance().getGameScene().getReplay().list[i]));
            }
            PacmanApp.getInstance().showGameScene();
        }
    }
    
    public void prepareMenu() {
        BitmapFont font = PacmanApp.getInstance().getFont();
        this.menuItems = new Image[MENU_ITEMS.length];
                
        for (int i = 0; i < this.menuItems.length; i++) {
            this.menuItems[i] = font.renderString(MENU_ITEMS[i], 800);
        }
    }
}