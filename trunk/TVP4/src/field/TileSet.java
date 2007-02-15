/*
 * TileSet.java
 *
 * Created on 14. februar 2007, 19:18
 *
 * Company: HT++
 *
 * @author Magnus
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 14. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package field;

/**
 *
 * @author Magnus
 */
public class TileSet {
    
    protected Image[] pathTiles;
    protected Image baseTile;
    
    public static final String SKIN_LIBRARY = (new File("skins/")).getAbsolutePath() + File.separator;
    
    /**
     * Tiles should be equal in size  and png.
     *
     * Path tiles should be named using 1 when a path  is present and 0 when not.
     * 1s and 0s should be put in the order UP, RIGHT, DOWN, LEFT.
     * Thus a path tile for a horisontally connected node should be named 0101.png
     * and a vertically connected node should be named 1010.png
     *
     * The base or empty tile should be called base.png
     * 
     * Point tiles should be named p_ followed by the amount of points. 
     * Thus a 100 point marker should be called p_100.png     
     */
    
    /** Creates a new instance of TileSet */
    public TileSet() {
    }
    
    public void loadTile(String path) {
        this.loadTile(new File(path));
    }
    
    public void loadTile(File file) {    
        if (file.isDirectory()) {
            this.pathTiles = new Image[16];

            for (int i = 0; i < this.pathTiles.length; i++) {          
                //System.out.println(file.getAbsolutePath() + file.separator + this.zeroPad(Integer.toBinaryString(i),4));
                this.pathTiles[i] = (new ImageIcon(file.getAbsolutePath() + file.separator + this.zeroPad(Integer.toBinaryString(i),4) + ".png")).getImage();
            }

            this.baseTile = (new ImageIcon(file.getAbsolutePath() + file.separator +  "base.png")).getImage();
            super.tileSize = this.baseTile.getWidth(null);
        }        
    }
    
    
    public static String zeroPad(String string, int length) {
        StringBuilder builder = new StringBuilder(length);
        
        for (int i = string.length(); i < length; i++) {
            builder.append('0');
        }
        
        builder.append(string);
        
        return builder.toString();
    }
}
