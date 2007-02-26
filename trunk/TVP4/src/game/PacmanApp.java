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
    
    private Scene scene;    
    
    public static void main(String[] args) {
        new PacmanApp().start();
    }
    
    /** Creates a new instance of PacmanApp */
    public PacmanApp() {
        super();
    }            

    public void update(long _time) {
        if (scene != null) {
            scene.update(_time);
        }
    }
    
    public void init() {
        super.init();
        this.setScene(new GameScene());
    }
    public void draw(Graphics2D _g) {
        if (scene != null) {
            scene.draw(_g);
        }                    
    }            
    
    public void setScene(Scene _scene) {
        if (scene != null) {
            scene.unregisterKeys(super.input);
        }
        
        scene = _scene;
        scene.registerKeys(super.input);
    }
}