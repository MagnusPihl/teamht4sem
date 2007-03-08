/*
 * GameScene.java
 *
 * Created on 26. februar 2007, 16:41
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.6
 *
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 8. marts 2007 (v 1.6)
 * Added win/lose game logic and temporary messages.
 * Fixed a bug with replays, which would not discard old replays after a level had been played.
 *
 * Magnus Hemmer Pihl @ 8. marts 2007 (v 1.5)
 * Entities are no longer animated when standing still.
 *
 * Magnus Hemmer Pihl @ 8. marts 2007 (v 1.4)
 * Added support for maps larger than the screen (map scrolling).
 *
 * Magnus Hemmer Pihl @ 7. marts 2007 (v 1.3)
 * Added replay functionality.
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
 * Initial.
 *
 */

package game;

import game.entitycontrol.*;
import game.entitycontrol.ReplayController;
import game.system.*;
import game.input.*;
import field.*;
import game.visual.BitmapFont;
import game.visual.EntityRenderer;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.swing.JFileChooser;

public class GameScene implements Scene {
    
    private int points;
    private boolean paused, win, lose;
    
    private Field field;
    private InputAction pause, confirm;
    
    private File level;
    private BitmapFont font;
    private BufferedImage pointsImage;
    private int levelOffsetX, levelOffsetY;
    
    private Replay replay;
    private int mode;
    
    long moveTimer;
    
    /** Creates a new instance of GameScene */
    public GameScene() {
        this.replay = new Replay();
        this.mode = 0;
        this.field = new Field();
        this.level = new File("test.lvl");
        TileSet.getInstance().loadTileSet(new File(TileSet.SKIN_LIBRARY + "pacman/"));
        
        this.pause = new InputAction("Pause", InputAction.DETECT_FIRST_ACTION);
        this.confirm = new InputAction("Confirm quit", InputAction.DETECT_FIRST_ACTION);
        
        this.moveTimer = 0;
    }
    
    public void setMode(int _mode)
    {
        this.mode = _mode;
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
    }
    
    public void setReplay(File _replay)
    {
        this.replay.load(_replay);
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
        
        Shape clip = _g.getClip();
        _g.setClip(0, 40, 800, 560);
        
        for(int i=0; i<800/TileSet.getInstance().getTileSize()+1; i++)
            for(int j=0; j<600/TileSet.getInstance().getTileSize()+1; j++)
                _g.drawImage(TileSet.getInstance().getBaseTile(), i*TileSet.getInstance().getTileSize(),
                        j*TileSet.getInstance().getTileSize(), null);
        
        if(field.getSize().width * TileSet.getInstance().getTileSize() > 800 ||
                field.getSize().height * TileSet.getInstance().getTileSize() > 600)
        {
            int pacX = field.getEntityRenderers()[0].getEntity().getPosition().x * TileSet.getInstance().getTileSize();
            int pacY = field.getEntityRenderers()[0].getEntity().getPosition().y * TileSet.getInstance().getTileSize();
            this.levelOffsetX = (800/2)-pacX;
            this.levelOffsetY = (600/2)-pacY;
        }
        field.drawField(_g, this.levelOffsetX, this.levelOffsetY);
        
        _g.setClip(clip);
        
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
        if(this.win)
        {
            _g.setColor(Color.GRAY);
            _g.fillRect(300, 250, 200, 100);
            _g.setColor(Color.DARK_GRAY);
            _g.fillRect(305, 255, 190, 90);
            _g.setColor(Color.WHITE);
            _g.drawString("ZOMG WIN!", 360,295);
            _g.drawString("Press 'Y' to exit to the title screen.", 310,310);
        }
        if(this.lose)
        {
            _g.setColor(Color.GRAY);
            _g.fillRect(300, 250, 200, 100);
            _g.setColor(Color.DARK_GRAY);
            _g.fillRect(305, 255, 190, 90);
            _g.setColor(Color.WHITE);
            _g.drawString("Lawl Lose!", 360,295);
            _g.drawString("Press 'Y' to exit to the title screen.", 310,310);
        }
    }            

    public void update(long _time)
    {
        if(!this.win && !this.lose)
        {
            if(pause.isPressed())
            {
                this.paused = !this.paused;
                this.confirm.release();
            }

            if(!this.paused)
            {
                this.moveTimer += _time;
                EntityRenderer[] entities = this.field.getEntityRenderers();
                for(int i=0; i<entities.length; i++)
                    if(entities[i].getEntity() != null)
                    {
                        if(this.moveTimer>200)
                        {
                            int dir = entities[i].getEntity().getController().move();
                            this.replay.list[i].add(dir);
                            if(Node.isValidDirection(dir))
                                entities[i].getEntity().setIsMoving(true);
                            else
                                entities[i].getEntity().setIsMoving(false);
                            
                            if(this.field.getPointsLeft() == 0)
                            {
                                this.win = true;
                            }
                            else
                            {
                                for(int j=0; j<4; j++)
                                {
                                    if(entities[0].getEntity().getNode().getNodeAt(j) != null)
                                        if(entities[0].getEntity().getNode().getNodeAt(j).holdsEntity())
                                            this.lose = true;
                                }
                            }
                        }
                        entities[i].getEntity().getController().calculateNextMove();
                    }
                this.addPoints(entities[0].getEntity().getNode().takePoints());
                if(this.moveTimer>200)
                    this.moveTimer = 0;
            }
            else if(confirm.isPressed())
            {
                PacmanApp.getInstance().showTitleScene();
            }
        }
        if(confirm.isPressed())
        {
            PacmanApp.getInstance().showTitleScene();
        }
    }

    public void init(InputManager _input) {
        this.paused = false;
        this.win = false;
        this.lose = false;
        this.resetPoints();
        this.field.loadFrom(this.level);
        this.levelOffsetX = (800/2) - ((this.field.getSize().width * TileSet.getInstance().getTileSize())/2);
        this.levelOffsetY = (600/2) - ((this.field.getSize().height * TileSet.getInstance().getTileSize())/2);
        
        _input.mapToKey(pause, KeyEvent.VK_SPACE);
        _input.mapToKey(confirm, KeyEvent.VK_Y);
        
        EntityRenderer[] entities = this.field.getEntityRenderers();
        if(mode == 0)
        {
            entities[0].getEntity().setController(new KeyboardController(entities[0].getEntity()));
            entities[1].getEntity().setController(new PreyAIController(entities[1].getEntity()));
            entities[2].getEntity().setController(new PreyAIController(entities[2].getEntity()));
        }
        else if(mode == 1)
        {
            entities[0].getEntity().setController(new ReplayController(entities[0].getEntity(), this.replay.list[0]));
            entities[1].getEntity().setController(new ReplayController(entities[1].getEntity(), this.replay.list[1]));
            entities[2].getEntity().setController(new ReplayController(entities[2].getEntity(), this.replay.list[2]));
        }
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
        
        if(this.mode == 0)
        {
            JFileChooser saveReplayDialog = new JFileChooser();
            saveReplayDialog.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {                
                    return ((f.getName().toLowerCase().endsWith(".rpl") && f.isFile()) || (f.isDirectory()));
                }
                public String getDescription() {
                    return "Replay files";
                }
            });
            if (saveReplayDialog.showSaveDialog(
                    PacmanApp.getInstance().getCore().getScreenManager().getFullScreenWindow()) == JFileChooser.APPROVE_OPTION)
            {
                File file = saveReplayDialog.getSelectedFile();
                if (!file.getName().toLowerCase().endsWith(".rpl"))
                    file = new File(file.getAbsoluteFile() + ".rpl");
                this.replay.save(file);
            }
        }
        this.replay = new Replay();
    }
}
