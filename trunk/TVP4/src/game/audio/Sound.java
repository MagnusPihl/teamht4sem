/*
 * SongPlayer.java
 *
 * Created on 18. marts 2007, 20:19
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Administrator @ 18. marts 2007 (v 1.1)
 * __________ Changes ____________
 *
 * Administrator @ 18. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game.audio;

import java.io.File;
import java.net.URL;
import javax.media.CachingControlEvent;
import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.Player;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;
import javax.swing.JOptionPane;

public class Sound extends Thread implements ControllerListener{
    
    private Player player;
    private boolean isPaused, isBackground;
    /** Creates a new instance of SongPlayer */
    public Sound(String fileName, boolean isBackground) {
        isPaused = false;
        try { 
            // create a new player and add listener 
            File f = new File(fileName);
            player = Manager.createPlayer(f.toURI().toURL()); 
            player.addControllerListener(this);
            this.isBackground = isBackground;
        } catch (Exception e) { 
            //JOptionPane.showMessageDialog("Invalid file or location", 
            //        "Error loading file", JOptionPane.ERROR_MESSAGE); 
        }
    }
    
    public Sound(URL fileName, boolean isBackground) {
        isPaused = false;
        try { 
            // create a new player and add listener 
            player = Manager.createPlayer(fileName); 
            player.addControllerListener(this);
            this.isBackground = isBackground;
        } catch (Exception e) { 
            //JOptionPane.showMessageDialog("Invalid file or location", 
            //        "Error loading file", JOptionPane.ERROR_MESSAGE); 
        }
    }
    
    public void run(){
        startSound();
    }
    
    public void removePreviousPlayer() { 
        if (player == null) 
            return; 

        player.close();
    }
    
    public void startSound(){
        player.realize();
        player.prefetch();
        player.start();
    }
    
    public void pause(){
        Time time = this.player.getMediaTime();
        if(this.isPaused == false){
            this.player.stop();
            time = this.player.getMediaTime();
            this.isPaused = true;
        } else{
            this.player.start();
            time = this.player.getMediaTime();
            this.isPaused = false;
        }
    }
    
    public void setRepeat(boolean repeat){
        this.isBackground = repeat;
    }
    
    /**
     * This controllerUpdate function must be defined in order to
     * implement a ControllerListener interface. This 
     * function will be called whenever there is a media event
     */
    public synchronized void controllerUpdate(ControllerEvent event) {
	// If we're getting messages from a dead player, 
	// just leave
	if (player == null){
	    return;
        }
	
	// When the player is Realized, get the visual 
	// and control components and add them to the Applet
	if (event instanceof RealizeCompleteEvent) {
	    player.setMediaTime(new Time(0));
            player.setRate(2);
 	} else if (event instanceof CachingControlEvent) {
	} else if (event instanceof EndOfMediaEvent) {
	    // We've reached the end of the media; rewind and
	    // start over
            if(isBackground == true){
                player.setMediaTime(new Time(0));
                player.start();
            }
        }
    }

    
}
