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
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class GameScene implements Scene {
    
    //Denne konstant bestemmer hvor mange robotter er sat til. Det SKAL være robot 1 der er sat til, hvis det kun er 1. Det
    // SKAL være robot 1&2, hvis der er 2, osv.
    //Husk at sætte mode til online i options. Bemærk at alle entiteter bevæger sig i spillet, lige meget hvor mange
    // robotter der er sat til. Det kan der ikke gøres noget ved. Der sendes kun til det antal du vælger.
    private final int NUM_ROBOTS = 1;
    
    private int points;
    
    private Field field;
    private Entity[] entity;
    private InputAction cancel, confirm;
    private EntityRenderer[] entities;
    
    private File level;
    private BitmapFont font;
    private BufferedImage pointsImage;
    private Image topImage;
    private BufferedImage tiledBackground;
    private int levelOffsetX, levelOffsetY;
    
    private Replay replay;
    private int mode;
    private SoundManager soundManager;
    
    private boolean soundOn;
    
    private long moveTimer;
    private long roundTime;
    
    private Image fps;
    private int frameCounter;
    private long frameTimer;
    
    private int state;
    private final int STATE_PLACEMENT = 0;
    private final int STATE_RUNNING = 1;
    private final int STATE_WIN = 2;
    private final int STATE_LOSE = 3;
    private final int STATE_PAUSE = 4;
    private int placementState;
    
    private boolean online;
    private String towerPort;
    private Semaphore semaphore;
    private RobotProxy[] proxy;
    
    /** Creates a new instance of GameScene */
    public GameScene() {
        this.replay = new Replay();
        this.field = new Field();
        this.level = new File("test.lvl");
        this.entity = new Entity[3];
        this.entity[0] = new Entity(null, 0);
        this.entity[1] = new Entity(null, 1);
        this.entity[2] = new Entity(null, 2);
        TileSet.getInstance().loadTileSet(new File(TileSet.SKIN_LIBRARY + "pacman/"));
        SoundSet.getInstance().loadSoundSet(new File(SoundSet.SKIN_LIBRARY + "pacman/"));
        this.soundManager = new SoundManager();
        this.cancel = new InputAction("Cancel", InputAction.DETECT_FIRST_ACTION);
        this.confirm = new InputAction("Confirm quit", InputAction.DETECT_FIRST_ACTION);
        
        this.roundTime = 250;   //animation problems if set lower than 250
        this.moveTimer = 0;
        
        this.frameCounter = 0;
        this.frameTimer = System.currentTimeMillis();
        
        this.soundOn = true;
        
        this.semaphore = new Semaphore(3);
//        if(this.online)
//        {
            this.proxy = new RobotProxy[3];
            this.proxy[0] = new RobotProxy(1, this.semaphore);
            this.proxy[1] = new RobotProxy(2, this.semaphore);
            this.proxy[2] = new RobotProxy(3, this.semaphore);
//        }
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
    
    public void setSoundOn(boolean _soundOn)
    {
        this.soundOn = _soundOn;
    }
    
    public boolean getSoundOn()
    {
        return this.soundOn;
    }
    
    public void setTowerPort(String port)
    {
        this.towerPort = port;
    }
    
    public String getTowerPort()
    {
        return this.towerPort;
    }
    
    private void updateLevelOffset()
    {
        if(field.getSize().width * TileSet.getInstance().getTileSize() > 800)
        {
            int pacX = field.getEntityRenderers()[0].getEntity().getPosition().x * TileSet.getInstance().getTileSize();
            
            if(pacX + this.levelOffsetX < 350 && this.levelOffsetX < 25)
                this.levelOffsetX+=3;
            if(pacX + this.levelOffsetX > 450 && field.getSize().width * TileSet.getInstance().getTileSize() + this.levelOffsetX > 775)
                this.levelOffsetX-=3;
        }
        if(field.getSize().height * TileSet.getInstance().getTileSize() > 520)
        {
            int pacY = field.getEntityRenderers()[0].getEntity().getPosition().y * TileSet.getInstance().getTileSize();
            
            if(pacY + this.levelOffsetY < 250 && this.levelOffsetY < 85)
                this.levelOffsetY+=3;
            if(pacY + this.levelOffsetY > 350 && field.getSize().height * TileSet.getInstance().getTileSize() + this.levelOffsetY > 575)
                this.levelOffsetY-=3;
        }
    }
    
    public void prerender()
    {
        this.resetPoints();
        
        //START Draw background
        this.tiledBackground =
                PacmanApp.getInstance().getCore().getScreenManager().createCompatibleImage(800, 600, Transparency.OPAQUE);
        this.updateLevelOffset();

        int startTileX = (this.levelOffsetX % TileSet.getInstance().getTileSize()) - TileSet.getInstance().getTileSize();
        int startTileY = (this.levelOffsetY % TileSet.getInstance().getTileSize()) - TileSet.getInstance().getTileSize();
        for(int i=startTileX; i<800; i+=TileSet.getInstance().getTileSize())
            for(int j=startTileY; j<600; j+=TileSet.getInstance().getTileSize())
                this.tiledBackground.getGraphics().drawImage(TileSet.getInstance().getBaseTile(), i, j, null);
        this.tiledBackground.getGraphics().drawImage(new ImageIcon("images/top.png").getImage(), 0, 0, null);
        //this.topImage = new ImageIcon("images/top.png").getImage();
        //DONE Draw background
    }
    
    public void draw(Graphics2D _g)
    {
        //Any state
            _g.drawImage(this.tiledBackground, 0, 0, null);
            
            this.updateLevelOffset();
            Shape c = _g.getClip();
            _g.setClip(0, 50, 800, 550);
            field.drawField(_g, this.levelOffsetX, this.levelOffsetY);
            _g.setClip(c);
            
            this.frameCounter++;
            if(System.currentTimeMillis() - this.frameTimer > 1000)
            {
                this.fps = PacmanApp.getInstance().getFont().renderString("FPS: "+this.frameCounter,400);
                this.frameCounter = 0;
                this.frameTimer = System.currentTimeMillis();
            }
            
            //_g.drawImage(topImage, 0, 0, null);
            _g.drawImage(this.fps, 5, 5, null);
            _g.drawImage(pointsImage, 795 - pointsImage.getWidth(), 5, null);
        //Any state done
        
        if(this.state == this.STATE_PAUSE)
        {
            GameDialog.drawDialogCenter(_g, "Game Paused.\nPress enter to exit to \nthe title screen.");
        }
            
        if(this.state == this.STATE_WIN)
        {
            if(this.soundOn)
                this.soundManager.runSound(6, false);
            GameDialog.drawDialogCenter(_g, "You Win!\nPress enter to exit to \nthe title screen.");
        }
            
        if(this.state == this.STATE_LOSE)
        {
            if(this.soundOn)
                this.soundManager.runSound(3, false);
            GameDialog.drawDialogCenter(_g, "You Lose!\nPress enter to exit to \nthe title screen.");
        }
        
        if(this.state == this.STATE_PLACEMENT)
        {
            if(placementState%2 == 0){
                GameDialog.drawDialogCenter(_g, "Place robot #" + ((placementState/2)+1) + " where the\nblinking entity is.\nPress enter when done.");
            }
        }
    }            

    public void update(long _time)
    {
        //Any state
        this.moveTimer -= _time;
        
        if(this.state == this.STATE_PLACEMENT)
        {
            if(this.confirm.isPressed()){
                placementState++;
                if(placementState == 6)
                {
                    this.state = this.STATE_RUNNING;
                    return;
                }
                if(placementState%2 == 1){
                    this.field.getEntityRenderers()[(placementState-1)/2].setHighlight(true);
                }
                else{
                    this.field.getEntityRenderers()[(placementState-1)/2].setHighlight(false);
                }
            }
        }

        if(this.state == this.STATE_RUNNING)
        {
            if(cancel.isPressed())
            {
                this.confirm.isPressed();
                this.state = this.STATE_PAUSE;
                if(this.soundOn)
                    this.soundManager.pause();
            }

            //START OF TURN!
            entities[0].getEntity().getController().calculateNextMove();
            if(this.moveTimer<0 && this.semaphore.availablePermits()==3)
            {
                for(int i=0; i<entities.length; i++)
                {
                    if(entities[i].getEntity() != null)
                    {
//                    System.out.println("Sem: "+semaphore.availablePermits());
                        //Win/Lose condition
                        if(this.field.getPointsLeft() == 0)
                        {
                            this.state = this.STATE_WIN;
                        }
                        else
                        {
                            Node n;
                            Entity e = null;
                            for(int j=0; j<4; j++)
                            {
                                n = this.field.getNodeAt(entities[0].getEntity().getPosition()).getNodeAt(j);
                                if(n != null)
                                    e = n.getEntity();
                                        if(e != null)
                                            if(e.getID() > 0)
                                                this.state = this.STATE_LOSE;
                            }
                        }

                        int dir = -1;
                        if(i == 0)
                        {
                            this.addPoints(entities[0].getEntity().getNode().takePoints());
                            this.field.repaintNode(entities[0].getEntity().getNode());
                            dir = entities[i].getEntity().getController().move();
                        }
                        else
                        {
                            entities[i].getEntity().getController().calculateNextMove();
                            dir = entities[i].getEntity().getController().move();
                        }

                        if(this.online && dir!=-1)
                        {
                            if(i < NUM_ROBOTS)
                            {
                                try {
//                                    System.out.println("Moving entity "+j);
                                    System.out.println("Sending move direction "+dir+" ("+((byte)dir)+") to robot "+i);
                                    this.proxy[i].move((byte)dir, (byte)entities[i].getEntity().getNode().getBinaryDirections());
//                                    System.out.println("Done moving.");
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
            }
            if(this.online)
                for(int i=0; i < NUM_ROBOTS; i++)
                    this.proxy[i].isDoneMoving();
        }

        if(this.state == this.STATE_PAUSE)
        {
            if(cancel.isPressed())
            {
                this.state = this.STATE_RUNNING;
                if(this.soundOn)
                    this.soundManager.pause();
            }
            if(confirm.isPressed())
                PacmanApp.getInstance().showTitleScene();
        }

        if(this.state == this.STATE_WIN || this.state == this.STATE_LOSE)
        {
            if(confirm.isPressed())
                PacmanApp.getInstance().showTitleScene();
        }
        
        if(this.moveTimer<0)
                this.moveTimer = this.roundTime;
    }

    public void init(InputManager _input) {
        this.state = this.STATE_RUNNING;
        if(this.online){
            //this.state = this.STATE_PLACEMENT;
            this.placementState = 0;
            this.semaphore.release(3-this.semaphore.availablePermits());
            this.proxy[0].open(this.towerPort);
            for(int i=0; i<3; i++)
                this.proxy[i].setActive(true);
        }
        System.out.println(this.towerPort);
        this.resetPoints();
        this.fps = PacmanApp.getInstance().getFont().renderString("FPS: "+this.fps,400);
        this.field.loadFrom(this.level);
        for(int i=0; i<3; i++)
        {
            if(this.field.getEntityRenderers().length > i)
            {
                if(this.online)
                    this.proxy[i].init((byte)this.field.getEntityRenderers()[i].getEntity().getNode().getBinaryDirections());
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
        if(this.soundOn)
            this.soundManager.runSound(1, true);
        _input.mapToKey(cancel, KeyEvent.VK_ESCAPE);
        _input.mapToKey(confirm, KeyEvent.VK_ENTER);
        
        entities = this.field.getEntityRenderers();
        for(int i=0; i<entities.length; i++)
        {
            if(entities[i].getEntity() != null)
            {
                if(entities[i].getEntity().getController() != null)
                    entities[i].getEntity().getController().init(_input);
            }
        }
        
        this.prerender();
    }
    
    public void deinit(InputManager _input) {
        _input.removeKeyAssociation(KeyEvent.VK_ENTER);
        _input.removeKeyAssociation(KeyEvent.VK_ESCAPE);
        
        if(this.online)
        {
            this.proxy[0].close();
            for(int i=0; i<3; i++)
                this.proxy[i].setActive(false);
        }
        
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
