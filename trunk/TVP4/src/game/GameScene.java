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
 * Magnus Hemmer Pihl @ 27. februar 2007 (v 1.1)
 * Added bindings and temporary actions for movement and pause/quit keys.
 * Added points variable and display.
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
import java.io.*;

public class GameScene implements Scene {
    
    private int points;
    private boolean paused;
    
    private Field field;
    private InputAction up, down, left, right, pause, confirm;
    private EntityRenderer[] entity;
    
    /** Creates a new instance of GameScene */
    public GameScene() {
        this.points = 0;
        this.paused = false;
        
        TileSet.getInstance().loadTileSet(new File(TileSet.SKIN_LIBRARY + "pacman/"));   
        field = new Field();
        entity = field.getEntityRenderers();
        field.loadFrom(new File("test.lvl"));
        this.up = new InputAction("Move up");
        this.down = new InputAction("Move down");
        this.left = new InputAction("Move left");
        this.right = new InputAction("Move right");
        this.pause = new InputAction("Pause");
        this.confirm = new InputAction("Confirm quit");
        this.pause.setBehaviour(this.pause.DETECT_FIRST_ACTION);
    }
    
    public void draw(Graphics2D _g) {
        field.drawField(_g, new Dimension(800,600));
        
        _g.setColor(Color.WHITE);
        
        _g.drawString(""+this.points, 700, 20);
        
        if (this.paused)
        {
            _g.setColor(Color.GRAY);
            _g.fillRect(300, 250, 200, 100);
            _g.setColor(Color.DARK_GRAY);
            _g.fillRect(305, 255, 190, 90);
            _g.setColor(Color.WHITE);
            _g.drawString("Game Paused", 360,295);
        }
        
        //Temp stuff
        if (up.isPressed() && !this.paused)
            _g.drawString(up.getName(), 350,295);
        if (down.isPressed() && !this.paused)
            _g.drawString(down.getName(), 350,295);
        if (left.isPressed() && !this.paused)
            _g.drawString(left.getName(), 350,295);
        if (right.isPressed() && !this.paused)
            _g.drawString(right.getName(), 350,295);
        if (confirm.isPressed() && this.paused)
            _g.drawString("You're totally quitting right now.", 310,310);
        //Temp stuff end
    }            

    public void update(long _time) {
        if(pause.isPressed())
        {
            this.paused = !this.paused;
            this.confirm.release();
        }
        
        if(!this.paused)
        {
            //Game stuff goes in here
            /*if(up.isPressed())
                entity[0].getEntity().setDirection(0);
            if(right.isPressed())
                entity[0].getEntity().setDirection(1);
            if(down.isPressed())
                entity[0].getEntity().setDirection(2);
            if(left.isPressed())
                entity[0].getEntity().setDirection(3);*/
        }
    }

    public void registerKeys(InputManager _input) {
        _input.mapToKey(up, KeyEvent.VK_UP);
        _input.mapToKey(down, KeyEvent.VK_DOWN);
        _input.mapToKey(left, KeyEvent.VK_LEFT);
        _input.mapToKey(right, KeyEvent.VK_RIGHT);
        _input.mapToKey(pause, KeyEvent.VK_SPACE);
        _input.mapToKey(confirm, KeyEvent.VK_Y);
    }
    
    public void unregisterKeys(InputManager _input) {
        _input.removeAssociation(up);
        _input.removeAssociation(down);
        _input.removeAssociation(left);
        _input.removeAssociation(right);
        _input.removeAssociation(pause);
        _input.removeAssociation(confirm);
    }
}
