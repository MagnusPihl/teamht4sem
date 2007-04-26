/*
 * GameScene.java
 *
 * Created on 26. februar 2007, 16:41
 *
 * Company: HT++
 *
 * @author LMK
 * @version 2.4
 *
 *
 * ******VERSION HISTORY******
 *
 * MHP @ 26. april 2007 (v 2.4)
 * Changed Pause/Win/Lose dialogs to use the new GameDialog.
 * Added a yes/no dialog before saving a replay.
 * Now (again) only shows the save replay and enter hiscore dialogs if not currently playing back a replay.
 *
 * Magnus Hemmer Pihl @ 24. april 2007 (v 2.3)
 * Changed order of entity move calculation and execution to prevent entities from moving into the same space.
 * Added getReplay method.
 *
 * Magnus Hemmer Pihl @ 18. april 2007 (v 2.2)
 * Deprecated method setMode.
 * Added method setOnline.
 * Added method setRoundTime.
 *
 * Magnus Hemmer Pihl @ 17. april 2007 (v 2.1)
 * Changed the time at which points are taken, to better match smooth animation, also giving a slight optimization.
 * Added getRoundTime()-method.
 *
 * Magnus Hemmer Pihl @ 13. april 2007 (v 2.0)
 * Added preliminal support for RobotProxies, including a standard semaphore. No actual interaction with RobotProxies yet.
 *
 * Magnus Hemmer Pihl @ 12. april 2007 (v 1.9)
 * Added support for setting controllers outside of this class.
 *
 * Magnus Hemmer Pihl @ 21. marts 2007 (v 1.8)
 * Added FPS calculation and display.
 * Removed setting isMoving in entity, as the EntityController classes should take care of that.
 * Added getMoveTimer to support fluid entity movement.
 *
 * Magnus Hemmer Pihl / Mikkel Nielsen @ 13. marts 2007 (v 1.7)
 * Added highscore name entry dialog.
 * Adjusted background tiling to line up properly with levels larger than the screen.
 *
 * Magnus Hemmer Pihl @ 8. marts 2007 (v 1.6)
 * Now fills the background with background tiles.
 * Added win/lose game logic and temporary messages.
 * Fixed a bug with replays, which would not discard old replays after a level had been played.
 * Replays are now automatically saved with the correct extension.
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

import communication.RobotProxy;
import game.audio.SoundManager;
import game.audio.SoundSet;
import game.entitycontrol.*;
import game.system.*;
import game.input.*;
import field.*;
import game.visual.BitmapFont;
import game.visual.EntityRenderer;
import game.visual.TileSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.Semaphore;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class GameScene implements Scene {
    
    private int points;
    private boolean paused, win, lose;
    
    private Field field;
    private Entity[] entity;
    private InputAction pause, confirm;
    
    private File level;
    private BitmapFont font;
    private BufferedImage pointsImage;
    private int levelOffsetX, levelOffsetY;
    
    private Replay replay;
    private int mode;
    private SoundManager soundManager;
    
    private long moveTimer;
    private long roundTime;
    
    private Image fps;
    private int frameCounter;
    private long frameTimer;
    
    private boolean online;
    private Semaphore semaphore;
    private RobotProxy[] proxy;
    
    /** Creates a new instance of GameScene */
    public GameScene() {
        this.replay = new Replay();
        this.mode = 0;
        this.field = new Field();
        this.level = new File("test.lvl");
        this.entity = new Entity[3];
        this.entity[0] = new Entity(null, 0);
        this.entity[1] = new Entity(null, 1);
        this.entity[2] = new Entity(null, 2);
        TileSet.getInstance().loadTileSet(new File(TileSet.SKIN_LIBRARY + "pacman/"));
        SoundSet.getInstance().loadSoundSet(new File(SoundSet.SKIN_LIBRARY + "pacman/"));
        this.soundManager = new SoundManager();
        this.pause = new InputAction("Pause", InputAction.DETECT_FIRST_ACTION);
        this.confirm = new InputAction("Confirm quit", InputAction.DETECT_FIRST_ACTION);
        
        this.roundTime = 250;   //animation problems if set lower than 250
        this.moveTimer = 0;
        
        this.frameCounter = 0;
        this.frameTimer = System.currentTimeMillis();
        
        this.semaphore = new Semaphore(3);
        this.proxy = new RobotProxy[3];
        this.proxy[0] = new RobotProxy(1, this.semaphore);
        this.proxy[1] = new RobotProxy(2, this.semaphore);
        this.proxy[2] = new RobotProxy(3, this.semaphore);
    }
    
    public void setOnline(boolean _online)
    {
        this.online = _online;
    }
    
    @Deprecated
    public void setMode(int _mode)
    {
        this.mode = _mode;
    }
    
    public void addPoints(int _points)
    {
//        if (_points != 0)
//            this.soundManager.runSound(2, false);
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
    
    public Entity getEntity(int id)
    {
        if(id>=0 && id<=2)
            return this.entity[id];
        return null;
    }
    
    public void setReplay(File _replay)
    {
        this.replay.load(_replay);
    }
    
    public Replay getReplay()
    {
        return this.replay;
    }
    
    public Field getField()
    {
        return this.field;
    }
    
    public long getMoveTimer()
    {
        return this.moveTimer;
    }
    
    public long getRoundTime()
    {
        return this.roundTime;
    }
    
    public void setRoundTime(long _time)
    {
        this.roundTime = _time;
    }
    
    public void draw(Graphics2D _g) {
        _g.setColor(Color.BLACK);
        _g.fillRect(0, 0, 800, 600);
        
        if(this.pointsImage == null)
            this.resetPoints();
        
        Shape clip = _g.getClip();
        _g.setClip(0, 40, 800, 560);
        
        if(field.getSize().width * TileSet.getInstance().getTileSize() > 800 ||
                field.getSize().height * TileSet.getInstance().getTileSize() > 600)
        {
            int pacX = field.getEntityRenderers()[0].getEntity().getPosition().x * TileSet.getInstance().getTileSize();
            int pacY = field.getEntityRenderers()[0].getEntity().getPosition().y * TileSet.getInstance().getTileSize();
            this.levelOffsetX = (800/2)-pacX;
            this.levelOffsetY = (600/2)-pacY;
        }
        
        int startTileX = (this.levelOffsetX % TileSet.getInstance().getTileSize()) - TileSet.getInstance().getTileSize();
        int startTileY = (this.levelOffsetY % TileSet.getInstance().getTileSize()) - TileSet.getInstance().getTileSize();
        for(int i=startTileX; i<800; i+=TileSet.getInstance().getTileSize())
            for(int j=startTileY; j<600; j+=TileSet.getInstance().getTileSize())
                _g.drawImage(TileSet.getInstance().getBaseTile(), i, j, null);
        
        field.drawField(_g, this.levelOffsetX, this.levelOffsetY);
        
        _g.setClip(clip);
        
        _g.drawImage(pointsImage, 795 - pointsImage.getWidth(), 5, null);
        
        if (this.paused)
        {
            GameDialog.drawDialogCenter(_g, "Game Paused\nPress 'Y' to exit to the title screen.");
        }
        if(this.win)
        {
            this.soundManager.runSound(6, false);
            GameDialog.drawDialogCenter(_g, "You Win!\nPress 'Y' to exit to the title screen.");
        }
        if(this.lose)
        {
            this.soundManager.runSound(3, false);
            GameDialog.drawDialogCenter(_g, "You Lose!\nPress 'Y' to exit to the title screen.");
        }
        
        this.frameCounter++;
        if(System.currentTimeMillis() - this.frameTimer > 1000)
        {
            this.fps = PacmanApp.getInstance().getFont().renderString("FPS: "+this.frameCounter,400);
            this.frameCounter = 0;
            this.frameTimer = System.currentTimeMillis();
        }
        _g.drawImage(this.fps, 5, 5, null);
    }            

    public void update(long _time)
    {
        this.moveTimer -= _time;
        
        if(!this.win && !this.lose)
        {
            if(pause.isPressed())
            {
                this.confirm.reset();
                this.paused = !this.paused;
                this.soundManager.pause();
            }
            
            EntityRenderer[] entities = this.field.getEntityRenderers();
            entities[0].getEntity().getController().calculateNextMove();
            for(int i=0; i<entities.length; i++)
                if(entities[i].getEntity() != null)
                {
                    //START OF TURN!
                    if(this.moveTimer<0 && this.semaphore.availablePermits()==3 && !this.paused)
                    {
                        //Win/Lose condition
                        if(this.field.getPointsLeft() == 0)
                        {
                            this.win = true;
                        }
                        else
                        {
                            Node n;
                            Entity e;
                            for(int j=0; j<4; j++)
                            {
                                n = this.field.getNodeAt(entities[0].getEntity().getPosition()).getNodeAt(j);
                                if(n != null)
                                {
                                    e = n.getEntity();
                                        if(e != null)
                                            if(e.getID() > 0)
                                                this.lose = true;
                                }
                            }
                        }
                        
                        int dir = -1;
                        if(i == 0)
                        {
                            this.addPoints(entities[0].getEntity().getNode().takePoints());
                            dir = entities[i].getEntity().getController().move();
                        }
                        else
                        {
                            entities[i].getEntity().getController().calculateNextMove();
                            dir = entities[i].getEntity().getController().move();
                        }
                        
                        if(this.online)
                        {
                            for(int j=0; j<3; j++)
                            {
                                try {
                                    this.proxy[j].move((byte)dir, (byte)entities[j].getEntity().getNode().getBinaryDirections());
                                }
                                catch(IOException e)
                                {
                                    System.out.println(e.getMessage());
                                }
                            }
                        }
                        this.replay.list[i].add(dir);
                    }
                    //END OF TURN!
                }
            
            if(this.paused && confirm.isPressed())
            {
                PacmanApp.getInstance().showTitleScene();
            }
        }
        else if(confirm.isPressed())
        {
            PacmanApp.getInstance().showTitleScene();
        }
        
        if(this.moveTimer<0)
                this.moveTimer = this.roundTime;
    }

    public void init(InputManager _input) {
        this.paused = false;
        this.win = false;
        this.lose = false;
        this.resetPoints();
        this.fps = PacmanApp.getInstance().getFont().renderString("FPS: "+this.fps,400);
        this.field.loadFrom(this.level);
        for(int i=0; i<3; i++)
        {
            if(this.field.getEntityRenderers().length > i)
            {
                this.entity[i].setNode(this.field.getEntityRenderers()[i].getEntity().getNode());
                this.entity[i].setDirection(this.field.getEntityRenderers()[i].getEntity().getDirection());
                if(i==0 && this.entity[0].getController() == null)
                    this.entity[0].setController(new KeyboardController(this.entity[0]));
                if(i>0 && this.entity[i].getController() == null)
                    this.entity[i].setController(new RandomController(this.entity[i]));
                this.field.setEntity(i, this.entity[i]);
            }
        }
        this.levelOffsetX = (800/2) - ((this.field.getSize().width * TileSet.getInstance().getTileSize())/2);
        this.levelOffsetY = (600/2) - ((this.field.getSize().height * TileSet.getInstance().getTileSize())/2);
        this.soundManager.runSound(1, true);
        _input.mapToKey(pause, KeyEvent.VK_SPACE);
        _input.mapToKey(confirm, KeyEvent.VK_Y);
        
        EntityRenderer[] entities = this.field.getEntityRenderers();
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
        this.soundManager.removePreviousPlayers();
        EntityRenderer[] entities = this.field.getEntityRenderers();
        for(int i=0; i<entities.length; i++)
            if(entities[i].getEntity() != null)
                entities[i].getEntity().getController().deinit(_input);
        
        if(entities[0].getEntity().getController().getClass().getName() != "game.entitycontrol.ReplayController")
        {
            int saveReplay = JOptionPane.showConfirmDialog(PacmanApp.getInstance().getCore().getScreenManager().getFullScreenWindow(), "Do you wish to save a replay of this game?", "Save Replay...", JOptionPane.YES_NO_OPTION);
            if(saveReplay == 0)
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
                        PacmanApp.getInstance().getCore().getScreenManager().getFullScreenWindow()) == JFileChooser.APPROVE_OPTION) {
                    File file = saveReplayDialog.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".rpl"))
                        file = new File(file.getAbsoluteFile() + ".rpl");
                    this.replay.save(file);
                }
            }
            
            PacmanApp.getInstance().getCore().getScreenManager().update();
                        
            if (this.field.getHighScores().isHighScore(this.points)) {
                String name = JOptionPane.showInputDialog(PacmanApp.getInstance().getCore().getScreenManager().getFullScreenWindow(), "Congratulations, you have reached a new rank.\nPlease enter your name.");
                if(name == null){
                    name = "New player";
                }                
                Field newField = new Field();
                newField.loadFrom(this.level);
                newField.getHighScores().add(new HighScore(name, this.points));
                this.field.getHighScores().add(new HighScore(name, this.points));
                newField.saveTo(this.level);
            }
        }
        this.replay = new Replay();
    }
}
