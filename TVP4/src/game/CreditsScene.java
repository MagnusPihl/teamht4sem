/*
 * CreditsScene.java
 *
 * Created on 2. marts 2007, 22:07
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 2. marts 2007 (v 1.1)
 * I've tried to prerender all of the text to be drawn. But flickering still
 * occurs. I am however sure that the algorithm is a bit faster, though it
 * might use more RAM.
 * Added prerender()
 *
 */

package game;
import game.visual.*;
import game.input.*;
import game.system.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;

/**
 *
 * @author LMK
 */
public class CreditsScene implements Scene {
    
    private InputAction actionBack;
    private String[] credits;
    private BufferedImage[] rendereredCredits;
    private int linePointer;
    private float yOffset;
    private float speed = -0.05F;
    
    /** Creates a new instance of HighscoreScene */
    public CreditsScene() {        
        this.actionBack = new InputAction("Escape", InputAction.DETECT_FIRST_ACTION);
        this.loadCredits();
    }
    
    /**
     * Load credits from file on drive.
     */
    private void loadCredits() {
        File creditsFile = new File("data/credits.dat");
        BufferedReader in = null;
        ArrayList credits = new ArrayList(100);
        String currentLine = null;
        
        if (creditsFile.exists()) {
            try {
                in = new BufferedReader(new FileReader(creditsFile));
                while (true) {
                    currentLine = in.readLine();
                    if (currentLine == null) {
                        break;
                    }
                    credits.add(currentLine);
                }
                
                this.credits = new String[credits.size()];
                int j = 0;
                for (Iterator i = credits.iterator(); i.hasNext(); j++) {
                    this.credits[j] = (String)i.next();
                }                
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException ioe) {
                    in = null;
                }
            }
        } else {
            this.credits = new String[] {"Team HT++ apologizes...", "We couldn't open the credits file"};
        }
    }    
    
    /**
     * Render strings before use.
     */
    private void prerender() {        
        BitmapFont font = PacmanApp.getInstance().getFont();
        this.rendereredCredits = new BufferedImage[this.credits.length];
        
        for (int i = 0; i < this.rendereredCredits.length; i++) {
            this.rendereredCredits[i] = font.renderString(this.credits[i], 780);
        }
    }
    
    /**
     * Draw prerendered graphics on supplied graphic
     *
     * @param graphic to draw on
     */
    public void draw(Graphics2D _g) {
        _g.setColor(Color.BLACK);
        _g.fillRect(0,0,800,600);
        int heightUsed = (int)this.yOffset;
                
        for (int i = linePointer; i < this.credits.length; i++) {
            /*heightUsed += font.drawString(_g, this.credits[i], 10, 10 + heightUsed, 780);*/            
            _g.drawImage(this.rendereredCredits[i], 400 - (int)(this.rendereredCredits[i].getWidth()/2), 5 + heightUsed, null);
            heightUsed += this.rendereredCredits[i].getHeight(null);
            
            if (heightUsed < 0) {
                this.yOffset = heightUsed;
                linePointer++;
            }
            if (heightUsed > 600) {
                break;
            }
        }
    }
    
    /**
     * Update scene content. Moves the text on screen.
     *
     * @param time in milliseconds since last update.
     */
    public void update(long _time) {                
        this.yOffset += _time * this.speed;
        
        if ((actionBack.isPressed()) || (linePointer == this.credits.length)) {
            System.exit(0);
        }
    }
    
    /**
     * Register keys to listen for. Also initiates prerender.
     * 
     * @param InputManager to register with.
     */
    public void init(InputManager _input) {
        if (this.rendereredCredits == null) {
            this.prerender();
        }
        _input.mapToKey(this.actionBack, KeyEvent.VK_ESCAPE);
        this.linePointer = 0;
        this.yOffset = 600;
    }
        
    /**
     * Unregister keys that this scene listen for.
     * 
     * @param InputManager to unregister with.
     */
    public void deinit(InputManager _input) {
        _input.removeKeyAssociation(KeyEvent.VK_ESCAPE);        
    }
}
