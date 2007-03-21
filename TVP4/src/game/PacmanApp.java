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
import game.visual.*;
/**
 *
 * @author LMK
 */
public class PacmanApp {
    
    private TitleScene titleScene;
    private GameScene gameScene;  
    private HighScoreScene highscoreScene;
    private CreditsScene creditsScene;
    private GameCore core;
    private BitmapFont font;
    
    private static final PacmanApp instance = new PacmanApp();
        
    public static void main(String[] args) {
        PacmanApp.getInstance().getCore().start();
    }    
    
    /** Creates a new instance of PacmanApp */
    private PacmanApp() {        
        this.gameScene = new GameScene();
        this.titleScene = new TitleScene();
        this.highscoreScene = new HighScoreScene();
        this.creditsScene = new CreditsScene();
        this.font = new BitmapFont(new File("images/alfa"), 5);
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
    
    public void showHighScoreScene() {
        this.core.setScene(this.highscoreScene);
    }
    
    public void showCreditsScene() {
        this.core.setScene(this.creditsScene);
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
    
    public BitmapFont getFont() {
        return this.font;
    }
    
    protected void finalize() throws Throwable {
        System.gc();
    }
}