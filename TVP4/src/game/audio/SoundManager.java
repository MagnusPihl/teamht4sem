/*
 * SoundPlayerDemo.java
 *
 * Created on 19. marts 2007, 07:50
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Administrator @ 19. marts 2007 (v 1.1)
 * __________ Changes ____________
 *
 * Administrator @ 19. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game.audio;
import field.SoundSet;
import java.awt.BorderLayout; 
import java.awt.Color; 
import java.awt.Component; 
import java.awt.Container; 
import java.awt.GridLayout;
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent; 
import java.io.File; 
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.media.ControllerEvent; 
import javax.media.ControllerListener; 
import javax.media.Manager; 
import javax.media.Player; 
import javax.media.RealizeCompleteEvent; 
import javax.media.Time;
import javax.swing.JButton; 
import javax.swing.JFileChooser; 
import javax.swing.JFrame; 
import javax.swing.JOptionPane; 
import javax.swing.JPanel;

public class SoundManager{    
    private ArrayList players; 
    private Sound sound;
    private boolean isPaused = false;
    private File file;     
    
    /** Creates a new instance of SoundPlayerDemo */
    public SoundManager() {
        this.players = SoundSet.getInstance().getPlaylist();
    } 
    
    /**
    private void openFile() { 
        JFileChooser fileChooser = new JFileChooser(); 

        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
        int result = fileChooser.showOpenDialog(this); 

        // user clicked Cancel button on dialog 
        if (result == JFileChooser.CANCEL_OPTION) 
            file = null; 
        else 
            file = fileChooser.getSelectedFile(); 
    } */
    
    /**
    private void createPlayer() { 
        if (file == null) 
            return; 

        removePreviousPlayers(); 
        try { 
            // create a new player 
            sound = new SoundPlayer(file.toURI().toURL(), false);
            players.add(sound);
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, "Invalid file or location", 
                    "Error loading file", JOptionPane.ERROR_MESSAGE); 
        }
    } 
    
    
    private void createPlayers(){
        this.players = new ArrayList();
        for(int i = 0; i < 5 ; i++){
                sound = new SoundPlayer(pacman[i], false);
                this.players.add(sound);
        }
        System.out.println("Size: " + players.size());
    }*/

    public void removePreviousPlayers() { 
        for(Iterator iter = this.players.iterator(); iter.hasNext();){
            Sound sound = (Sound) iter.next();
            sound.removePreviousPlayer();
        }
    }
    
    public void runSound(int i, boolean repeat){
        try{
            if (this.players.size() >= i) {
                Sound sound = (Sound) this.players.get(i-1);
                sound.setRepeat(repeat);
                //sound.startSound();
                sound.run();
            }
        }
        catch(IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }
    
    public void pause(){
        for(Iterator iter = this.players.iterator(); iter.hasNext();){
            Sound sound = (Sound) iter.next();
            sound.pause();
        }
    }
}
