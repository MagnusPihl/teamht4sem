/*
 * PacmanApp.java
 *
 * Created on 26. februar 2007, 11:48
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
import java.io.*;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author LMK
 */
public class PacmanApp extends GameCore {
    
    private TitleScene titleScene;
    private GameScene gameScene;
    private Scene scene;    
    
    private static final PacmanApp instance = new PacmanApp();
        
    public static void main(String[] args) {
        PacmanApp.getInstance().start();
    }    
    
    /** Creates a new instance of PacmanApp */
    public PacmanApp() {        
        super();       
        this.gameScene = new GameScene();
        this.titleScene = new TitleScene();
    }
    
    public static PacmanApp getInstance() {
        return instance;
    }
    /**
     * Update all objects in the current scene
     */
    public void update(long _time) {
        if (scene != null) {
            scene.update(_time);
        }
    }
    
    /**
     * Overrides GameCore init
     * Sets the initial scene.
     */
    public void init() {
        super.init();
        this.showTitleScene();
    }
    
    /**
     * Draw all objects in scene
     *
     * @param graphic to draw on
     */
    public void draw(Graphics2D _g) {
        if (scene != null) {
            scene.draw(_g);
        }                    
    }            
    
    /**
     * Set the current scene
     * 
     * @param scene to view
     */
    public void setScene(Scene _scene) {
        if (scene != null) {
            scene.unregisterKeys(super.input);
        }
        
        scene = _scene;
        scene.registerKeys(super.input);
    }
    
    public void showTitleScene() {
        this.setScene(this.titleScene);
    }
    
    public void showGameScene() {
        this.setScene(this.gameScene);
    }
    
    public void showHighscoreScene() {
    
    }
    
    public GameScene getGameScene() {
        return this.gameScene;
    }
    
    public TitleScene getTitleScene() {
        return this.titleScene;
    }
}