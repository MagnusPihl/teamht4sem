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

import game.entitycontrol.KeyboardController;
import game.entitycontrol.PreyAIController;
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
    private InputAction pause, confirm;
    
    private File level;
    private BitmapFont font;
    private BufferedImage pointsImage;
    
    /** Creates a new instance of GameScene */
    public GameScene() {
        this.field = new Field();
        this.level = new File("test.lvl");
        TileSet.getInstance().loadTileSet(new File(TileSet.SKIN_LIBRARY + "pacman/"));
        
        this.pause = new InputAction("Pause", InputAction.DETECT_FIRST_ACTION);
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
    
    public void setLevel(File _level)
    {
        this.level = _level;
        this.field.loadFrom(this.level);
    }
    
    public Field getField()
    {
        return this.field;
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
//        if (up.isPressed() && !this.paused)
//            _g.drawString(up.getName(), 350,295);
//        if (down.isPressed() && !this.paused)
//            _g.drawString(down.getName(), 350,295);
//        if (left.isPressed() && !this.paused)
//            _g.drawString(left.getName(), 350,295);
//        if (right.isPressed() && !this.paused)
//            _g.drawString(right.getName(), 350,295);
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
            EntityRenderer[] entities = this.field.getEntityRenderers();
            for(int i=0; i<entities.length; i++)
                if(entities[i].getEntity() != null)
                {
                    entities[i].getEntity().getController().move();
                    entities[i].getEntity().getController().calculateNextMove();
                }
        }
        else if(confirm.isPressed())
        {
            PacmanApp.getInstance().showTitleScene();
        }
    }

    public void init(InputManager _input) {
        this.paused = false;
        this.resetPoints();
        this.field.loadFrom(this.level);
        
        _input.mapToKey(pause, KeyEvent.VK_SPACE);
        _input.mapToKey(confirm, KeyEvent.VK_Y);
        
        EntityRenderer[] entities = this.field.getEntityRenderers();
        entities[0].getEntity().setController(new KeyboardController(entities[0].getEntity()));
        entities[1].getEntity().setController(new PreyAIController(entities[1].getEntity()));
        entities[2].getEntity().setController(new PreyAIController(entities[2].getEntity()));
        for(int i=0; i<entities.length; i++)
        {
            if(entities[i].getEntity() != null)
            {
                if(entities[i].getEntity().getController() != null)
                    entities[i].getEntity().getController().init(_input);
            }
        }
    }
    
    public void deinit(InputManager _input) {
        _input.removeKeyAssociation(KeyEvent.VK_SPACE);
        _input.removeKeyAssociation(KeyEvent.VK_Y);
        
        EntityRenderer[] entities = this.field.getEntityRenderers();
        for(int i=0; i<entities.length; i++)
            if(entities[i].getEntity() != null)
                entities[i].getEntity().getController().deinit(_input);
    }
}