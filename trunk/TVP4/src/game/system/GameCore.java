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
 * Based on examples in "Devoloping Games in Java"
 * by Brackeen, David
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
abstract public class GameCore {
    
    //Display modes that game will run under
    private static final DisplayMode[] modes = new DisplayMode[] {
            new DisplayMode(800,600,32,0),
            new DisplayMode(800,600,16,0)
    };

    private boolean running;
    protected ScreenManager screen;
    protected InputManager input;

    /**
     * Halt execution of game on compleption of curren cycle.
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

            /*try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }*/
            
            //frames++;
        }
        //System.out.println(frames);
    }

    /**
     * Override this method to update all objects used in the game.
     */
    abstract public void update(long _time);
    
    /**
     * Override this method to draw all objects to screen.
     */
    abstract public void draw(Graphics2D _g);
}
