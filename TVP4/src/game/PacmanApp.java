/*
 * PacmanApp.java
 *
 * Created on 26. februar 2007, 11:48
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 28. februar 2007 (v 1.1)
 * Moved scene functionality and loop to GameCore
 * Added showGameScene and showTitleScene
 * ShowHighscoreScene not implemented yet
 * Changed to singleton
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
public class PacmanApp {
    
    private TitleScene titleScene;
    private GameScene gameScene;  
    private GameCore core;
    
    private static final PacmanApp instance = new PacmanApp();
        
    public static void main(String[] args) {
        PacmanApp.getInstance().getCore().start();
    }    
    
    /** Creates a new instance of PacmanApp */
    private PacmanApp() {        
        this.gameScene = new GameScene();
        this.titleScene = new TitleScene();
        this.core = new GameCore(this.titleScene);       
    }
    
    public static PacmanApp getInstance() {
        return instance;
    }                       
    
    public void showTitleScene() {
        this.core.setScene(this.titleScene);
    }
    
    public void showGameScene() {
        this.core.setScene(this.gameScene);
    }
    
    public void showHighscoreScene() {
    
    }
    
    public GameScene getGameScene() {
        return this.gameScene;
    }
    
    public TitleScene getTitleScene() {
        return this.titleScene;
    }
    
    public GameCore getCore() {
        return this.core;
    }
}