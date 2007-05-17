/*
 * SkinFileFilter.java
 *
 * Created on 25. marts 2007, 20:14
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 25. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game;

import java.io.*;
import game.visual.*;

/**
 *
 * @author LMK
 */
public class SkinFileFilter implements FileFilter {
    
    /**
     * Filter that only accepts directories containing all neccesary skin files.
     */
    public boolean accept(File file) {
        
        if (file.isDirectory()) {
            String path = file.getAbsolutePath() + file.separator;
            
            for(int i = 0; i < TileSet.ENTITY_COUNT; i++) {
                for(int j = 0; j < 4; j++) {
                    if (!new File(path + i +"_"+ j + "_0.png").isFile()) {
                        return false;
                    }
                    if (!new File(path + i +"_"+ j + "_1.png").isFile()) {
                        return false;
                    }
                }
            }
            
            for (int i = 0; i < TileSet.PATH_TILE_COUNT; i++) {
                if (!new File(path + TileSet.zeroPad(Integer.toBinaryString(i),4) + ".png").isFile()) {
                    return false;
                }
            }
            
            if ((!new File(path + "p10.png").isFile())||
                    (!new File(path + "p25.png").isFile())||
                    (!new File(path + "base.png").isFile())) {
                return false;
            }
            
            return true;
        }
        
        return false;
    }
}