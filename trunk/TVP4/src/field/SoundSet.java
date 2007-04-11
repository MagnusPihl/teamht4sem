/*
 * SoundSet.java
 *
 * Created on 22. marts 2007, 10:40
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Administrator @ 22. marts 2007 (v 1.1)
 * __________ Changes ____________
 *
 * Administrator @ 22. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package field;

import game.audio.Sound;
import java.io.File;
import java.util.ArrayList;


public class SoundSet {
    
    private static final String[] pacman = {"beginning.wav", "chomp.wav", "death.wav",
                "eatfruit.wav", "eatghost.wav", "extrapac.wav", "intermission.wav"};
    public static final String SKIN_LIBRARY = (new File("sounds/")).getAbsolutePath() + File.separator;
    
    private static SoundSet instance = new SoundSet(SKIN_LIBRARY + "nodes/");
    private ArrayList players; 
    
    /** Creates a new instance of SoundSet */
    private SoundSet(String path) {
        this.loadSoundSet(new File(path));
    }
    
    /**
     * Returns a reference to the Singleton instance of TileSet.
     * @return Singleton instance of TileSet.
     */
    public static SoundSet getInstance()
    {
        return instance;
    }
    
    public boolean loadSoundSet(String path)
    {
        return this.loadSoundSet(new File(path));
    }
    
    public boolean loadSoundSet(File file)
    {
        players = new ArrayList();
        try
        {
            if(file.isDirectory())
            {
                for(int i = 0; i < 5 ; i++){
                    Sound sound = new Sound(file.getAbsolutePath() + file.separator + pacman[i], false);
                    this.players.add(sound);
                }
            }
            
            return true;
        }
        catch(Exception e)
        {
            //Most likely a FileNotFoundException
            return false;
        }
    }
    
    public ArrayList getPlaylist(){
        return this.players;
    }
    
}
