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
 * Magnus Hemmer Pihl @ 5. marts 2007 (v 1.2)
 * Shifted the rendering of field to be below the points display.
 * Modified points display to use bitmap font.
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
import game.visual.BitmapFont;
import game.visual.EntityRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class GameScene implements Scene {
    
    private int points;
    private boolean paused;
    
    private Field field;
    private InputAction up, down, left, right, pause, confirm;
    private EntityRenderer[] entities;
    
    private File level;
    private BitmapFont font;
    private BufferedImage pointsImage;
    
    /** Creates a new instance of GameScene */
    public GameScene() {
        this.paused = false;
        
        TileSet.getInstance().loadTileSet(new File(TileSet.SKIN_LIBRARY + "pacman/"));
        
        this.loadLevel(new File("test.lvl"));
        
        this.up = new InputAction("Move up");
        this.down = new InputAction("Move down");
        this.left = new InputAction("Move left");
        this.right = new InputAction("Move right");
        this.pause = new InputAction("Pause", this.pause.DETECT_FIRST_ACTION);
        this.confirm = new InputAction("Confirm quit");
    }
    
    public void addPoints(int _points)
    {
        this.points += _points;
        font = PacmanApp.getInstance().getFont();
        pointsImage = font.renderString(""+this.points,400);
    }
    
    public void resetPoints()
    {
        this.points = 0;
        font = PacmanApp.getInstance().getFont();
        pointsImage = font.renderString(""+this.points,400);
    }
    
    public void loadLevel(File _level)
    {
        this.level = _level;
        this.field = new Field();
        this.field.loadFrom(this.level);
        this.entities = field.getEntityRenderers();
    }
    
    public void draw(Graphics2D _g) {
        _g.setColor(Color.BLACK);
        _g.fillRect(0, 0, 800, 600);
        
        if(this.pointsImage == null)
            this.resetPoints();
        
        field.drawField(_g, 0, this.pointsImage.getHeight()+10, new Dimension(800,600));
        
        _g.setColor(Color.WHITE);
        
        _g.drawImage(pointsImage, 795 - pointsImage.getWidth(), 5, null);
        
        if (this.paused)
        {
            _g.setColor(Color.GRAY);
            _g.fillRect(300, 250, 200, 100);
            _g.setColor(Color.DARK_GRAY);
            _g.fillRect(305, 255, 190, 90);
            _g.setColor(Color.WHITE);
            _g.drawString("Game Paused", 360,295);
            _g.drawString("Press 'Y' to exit to the title screen.", 310,310);
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
        else if(confirm.isPressed())
        {
            PacmanApp.getInstance().showTitleScene();
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
        this.points = 0;
        this.paused = false;
        field.loadFrom(this.level);
        
        _input.removeAssociation(up);
        _input.removeAssociation(down);
        _input.removeAssociation(left);
        _input.removeAssociation(right);
        _input.removeAssociation(pause);
        _input.removeAssociation(confirm);
    }
}
