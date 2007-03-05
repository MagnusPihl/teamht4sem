package game.system;

import java.awt.DisplayMode;
import java.awt.Window;
import java.awt.Paint;
import java.awt.Graphics2D;
import game.input.*;

/*
 * GameCore.java
 *
 * Created 2006 by LMK
 * Based on examples in "Devoloping Games in Java" [Brackeen]
 * by Brackeen, David 
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 28. februar 2007 (v 1.1)
 * Added constructed with initial scene
 * Implemented scenes
 * No longer abstract
 *
 */
public class GameCore {
    
    //Display modes that game will run under
    private static final DisplayMode[] modes = new DisplayMode[] {
            new DisplayMode(800,600,32,0),
            new DisplayMode(800,600,16,0)
    };

    private boolean running;
    protected ScreenManager screen;
    protected InputManager input;
    protected Scene scene;  

    /**
     * Construct GameCore with an initial scene set.
     */
    public GameCore(Scene startScene) {
        this.scene = startScene;
    }
    
    /**
     * Halt execution of game on compleption of curren cycle.
     * Taken [Brackeen]
     */
    public void stop() {
        this.running = false;
    }

    /**
     * Initialize game and start loop.
     */
    public void start() {
        try {
            this.init();
            this.loop();
        } finally {
            this.screen.restoreScreen();
        }
    }

    /**
     * Initialize display and input. If more initialization is needed override 
     * this method and call super.init() as the first command.
     */
    public void init() {
        this.screen = new ScreenManager();
        this.screen.setFullScreen(this.screen.getCompatibleMode(modes));
        Window window = this.screen.getFullScreenWindow();
        this.input = new InputManager(window);
        this.input.setCursor(InputManager.INVISIBLE_CURSOR);
        this.running = true;        
        this.setScene(this.scene);
    }

    /**
     * Game loop.
     */
    public void loop() {
        long startTime = System.currentTimeMillis();
        long currentTime = startTime;
        //long frames = 0;
        
        while (this.running) {
            long elapsedTime = System.currentTimeMillis() - currentTime;
            currentTime += elapsedTime;

            this.update(elapsedTime);

            Graphics2D _g = this.screen.getGraphics();
            this.draw(_g);
            _g.dispose();
            this.screen.update();

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }            
            //frames++;
        }
        //System.out.println(frames);
    }

    /**
     * Update all objects in the current scene
     */
    public void update(long _time) {
        if (this.scene != null) {
            this.scene.update(_time);
        }
    }
    
    /**
     * Draw all objects in scene
     *
     * @param graphic to draw on
     */
    public void draw(Graphics2D _g) {
        if (this.scene != null) {
            this.scene.draw(_g);
        }                    
    }     
    
    /**
     * Set the current scene
     * 
     * @param scene to view
     */
    public void setScene(Scene _scene) {
        if (this.scene != null) {
            this.scene.unregisterKeys(this.input);
        }
        
        this.scene = _scene;
        this.scene.registerKeys(this.input);
    }
    
    /**
     * Get ScreenManager
     *
     * @return ScreenManager
     */
    public ScreenManager getScreenManager() {
        return this.screen;
    }
}
