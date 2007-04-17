/*
 * OptionsScene.java
 *
 * Created on 14. marts 2007, 12:06
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.4
 *
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 17. april 2007 (v 1.4)
 * Added joystick autodetection.
 *
 * Magnus Hemmer Pihl @ 13. april 2007 (v 1.3)
 * Added isOnline() and getInterface() methods.
 *
 * Magnus Hemmer Pihl @ 13. april 2007 (v 1.2)
 * Fixed bug that would crash the program if the cursor was moved below the bottom option.
 *
 * Magnus Hemmer Pihl @ 13. april 2007 (v 1.1.1)
 * Changed enter key to not do anything, until detailed options are implemented.
 *
 * Magnus Hemmer Pihl @ 11. april 2007 (v 1.1)
 * Changed ControllerScene to OptionsScene and rewrote most of it.
 * Now supports setting controllers for all three entities and changing skins.
 * Other settings are not currently implemented.
 *
 * Magnus Hemmer Pihl @ 14. marts 2007 (v 1.0)
 * Initial.
 *
 */

package game;

import de.hardcode.jxinput.JXInputManager;
import game.entitycontrol.*;
import game.system.*;
import game.input.*;
import game.visual.*;
import field.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class OptionsScene implements Scene
{
    private InputAction up, down, left, right, enter, quit;
    
    private int cursor;
    private int option[];
    
    private String menuItemsStr[];
    private Image menuItems[];
    private String menuOptionsStr[][];
    private Image menuOptions[][];
    private String menuHelpStr[];
    private Image menuHelp[];
    
    /** Creates a new instance of GameScene */
    public OptionsScene() {
        this.up = new TimedInputAction("Down", 500, 100);
        this.down = new TimedInputAction("Down", 500, 100);
        this.left = new TimedInputAction("Down", 500, 100);
        this.right = new TimedInputAction("Down", 500, 100);
        this.enter = new InputAction("Enter", InputAction.DETECT_FIRST_ACTION);
        this.quit = new InputAction("Escape", InputAction.DETECT_FIRST_ACTION);
        
        this.menuItemsStr = new String[] {"Entity0", "Entity1", "Entity2", "Skin", "Volume", "Mode", "Interface"};
        
        String[] joyList = new String[JXInputManager.getNumberOfDevices()];
        for(int i=0; i<joyList.length; i++)
            joyList[i] = JXInputManager.getJXInputDevice(i).getName();
        
        this.menuOptionsStr = new String[this.menuItemsStr.length][];
        this.menuOptionsStr[0] = new String[3+joyList.length];
        this.menuOptionsStr[1] = new String[6+joyList.length];
        this.menuOptionsStr[2] = new String[6+joyList.length];
        for(int i=0; i<3; i++)
        {
            this.menuOptionsStr[i][0] = "Keyboard (arrow keys)";
            this.menuOptionsStr[i][1] = "Keyboard (W/A/S/D)";
            this.menuOptionsStr[i][2] = "Keyboard (numpad)";
            for(int j=0; j<joyList.length; j++)
                this.menuOptionsStr[i][3+j] = joyList[j];
            if(i>0)
            {
                this.menuOptionsStr[i][3+joyList.length] = "CPU - Easy";
                this.menuOptionsStr[i][4+joyList.length] = "CPU - Normal";
                this.menuOptionsStr[i][5+joyList.length] = "CPU - Hard";
            }
        }
        
        //Get skin list by getting file list from skins directory, then checking every entry with SkinFileFilter.
        File dir = new File("skins/");
        File skinList[] = dir.listFiles(new SkinFileFilter());
        this.menuOptionsStr[3] = new String[skinList.length];
        for(int i=0; i<skinList.length; i++)
            this.menuOptionsStr[3][i] = skinList[i].getName();
        
        this.menuOptionsStr[4] = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        this.menuOptionsStr[5] = new String[] {"Offline", "Online"};
        this.menuOptionsStr[6] = new String[] {"USB", "COM1", "COM2", "COM3", "COM4"};
        
        this.menuHelpStr = new String[]
        {
            "Choose which device to control Pac-Man with by pressing left or right.\nPress enter to customize controls for your device.",
            "Choose which device to control Ghost A with by pressing left or right.\nPress enter to customize controls for your device.",
            "Choose which device to control Ghost B with by pressing left or right.\nPress enter to customize controls for your device.",
            "Press left or right to choose a skin.\nPress enter to see a preview of the currently selected skin.",
            "Press left or right to decrease or increase sound volume.",
            "Press left or right to choose either offline or online mode.\n"+
                "In online mode, robots will physically mimic the game - in offline you don't need any robots.",
            "Press left or right to choose the port at which a Lego Mindstorm Tower is connected.\nOnly needed for Online mode."
        };
        
        this.cursor = 0;
        this.option = new int[this.menuItemsStr.length];
        this.option[0] = 0; //Keyboard control for Entity0
        this.option[1] = this.menuOptionsStr[1].length-2; //Normal CPU for Entity1
        this.option[2] = this.menuOptionsStr[1].length-2; //Normal CPU for Entity2
        this.option[3] = 0; //No fucking clue for skin :p
        this.option[4] = 5; //Average sound volume
        this.option[5] = 0; //Offline mode
        this.option[6] = 0; //USB interface
    }
    
    public void draw(Graphics2D _g) {
        _g.setColor(Color.BLACK);
        _g.fillRect(0,0,800,600);
        
        int y = 50;
        for(int i=0; i<this.menuItems.length; i++)
        {
            _g.drawImage(this.menuItems[i], 150, y, null);
            _g.drawImage(this.menuOptions[i][this.option[i]], 350, y, null);
            if(i == this.cursor)
            {
//                _g.drawImage(this.menuItems[i], 150, y, null);
                _g.drawImage(this.menuOptions[i][this.option[i]], 350, y, null);
            }
                
            y+=50;
        }
        
        _g.drawImage(this.menuHelp[this.cursor], 20, 400, null);
    }
    
    public void update(long _time) {
        if(this.up.isPressed() && this.cursor>0)
            this.cursor--;
        if(this.down.isPressed() && this.cursor<this.menuItemsStr.length-1)
            this.cursor++;
        if(this.left.isPressed() && this.option[this.cursor]>0)
            this.option[this.cursor]--;
        if(this.right.isPressed() && this.menuOptions[this.cursor][this.option[this.cursor]+1] != null)
            this.option[this.cursor]++;
        if(this.enter.isPressed())
            ;
        if(this.quit.isPressed())
        {
            for(int i=0; i<3; i++)
            {
                Entity e = PacmanApp.getInstance().getGameScene().getEntity(i);
                if(this.option[i] == 0)
                    e.setController(new KeyboardController(e));
                if(this.option[i] == 1)
                    e.setController(new KeyboardController(e, KeyEvent.VK_W,KeyEvent.VK_D,KeyEvent.VK_S,KeyEvent.VK_A));
                if(this.option[i] == 2)
                    e.setController(new KeyboardController(e, KeyEvent.VK_NUMPAD8,KeyEvent.VK_NUMPAD6,KeyEvent.VK_NUMPAD2,KeyEvent.VK_NUMPAD4));
                int numJoy = this.menuOptionsStr[0].length - 3;
                for(int j=0; j<numJoy; j++)
                    if(this.option[i] == 3+j)
                        e.setController(new JoystickController(e, j));
                if(this.option[i] == 3+numJoy)
                    e.setController(new PreyAIController(e));
                if(this.option[i] == 4+numJoy)
                    e.setController(new InSightController(e, PacmanApp.getInstance().getGameScene().getEntity(0)));
                if(this.option[i] == 5+numJoy)
                    e.setController(new HunterAIController(e, PacmanApp.getInstance().getGameScene().getEntity(0)));
            }
            TileSet.getInstance().loadTileSet("skins/"+this.menuOptionsStr[3][this.option[3]]);
            //Set volume
            //Set online/offline mode
            //Set tower interface
            
            PacmanApp.getInstance().showTitleScene();
        }
    }
    
    private void prerender()
    {
        BitmapFont font = PacmanApp.getInstance().getFont();
        this.menuItems = new Image[this.menuItemsStr.length];
        int length = 0;
        for(int i=0; i<this.menuOptionsStr.length; i++)
            if(this.menuOptionsStr[i].length > length)
                length = this.menuOptionsStr[i].length;
        this.menuOptions = new Image[this.menuOptionsStr.length][length+1];
        this.menuHelp = new Image[this.menuHelpStr.length];
        
        this.menuItems[0] = TileSet.getInstance().getEntityTile(0,1,0);
        this.menuItems[1] = TileSet.getInstance().getEntityTile(1,1,0);
        this.menuItems[2] = TileSet.getInstance().getEntityTile(2,1,0);
        for(int i=0; i<this.menuItemsStr.length; i++)
        {
            if(i>2)
                this.menuItems[i] = font.renderString(this.menuItemsStr[i], 760);
            for(int j=0; j<this.menuOptionsStr[i].length; j++)
                if(this.menuOptionsStr[i][j] != null)
                    this.menuOptions[i][j] = font.renderString(this.menuOptionsStr[i][j], 760);
            this.menuHelp[i] = font.renderString(this.menuHelpStr[i], 760);
        }
    }
    
    public boolean isOnline()
    {
        if(this.option[5] == 1)
            return true;
        return false;
    }
    
    public String getInterface()
    {
        return this.menuOptionsStr[6][this.option[6]].toLowerCase();
    }
    
    public void init(InputManager _input) {
        _input.mapToKey(this.enter, KeyEvent.VK_ENTER);
        _input.mapToKey(this.quit, KeyEvent.VK_ESCAPE);
        _input.mapToKey(this.up, KeyEvent.VK_UP);
        _input.mapToKey(this.down, KeyEvent.VK_DOWN);
        _input.mapToKey(this.left, KeyEvent.VK_LEFT);
        _input.mapToKey(this.right, KeyEvent.VK_RIGHT);
        
        this.prerender();
    }
    
    public void deinit(InputManager _input) {
        _input.removeKeyAssociation(KeyEvent.VK_ENTER);
        _input.removeKeyAssociation(KeyEvent.VK_ESCAPE);
        _input.removeKeyAssociation(KeyEvent.VK_UP);
        _input.removeKeyAssociation(KeyEvent.VK_DOWN);
        _input.removeKeyAssociation(KeyEvent.VK_LEFT);
        _input.removeKeyAssociation(KeyEvent.VK_RIGHT);
    }
}