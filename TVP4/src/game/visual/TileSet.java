/*
 * TileSet.java
 *
 * Created on 16. februar 2007, 10:18
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.4
 *
 * ******VERSION HISTORY******
 * Lau Maack-Krommes @ 21. marts 2007 (v 1.4)
 * Saved file.getAbsolutePath() + file.separator in an itermediate variable 
 * instead of constructing over and over again.
 * Moved to game.visual
 *
 * Magnus Hemmer Pihl @ 5. marts 2007 (v 1.3)
 * Added point tiles, along with getPointsTile method.
 *
 * Magnus Hemmer Pihl @ 19. februar 2007 (v 1.2)
 * TileSet is now a Singleton.
 * Implemented getInstance() method.
 *
 * Magnus Hemmer Pihl @ 16. februar 2007 (v 1.1)
 * Corrected name of method getEntityTile()
 *
 * Magnus Hemmer Pihl @ 16. februar 2007 (v 1.0)
 * Created.
 *
 */

package game.visual;

import field.*;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;

public class TileSet
{
    private Image[][][] entityTiles;
    private Image[] pathTiles;
    private Image[] pointTiles;
    private Image baseTile;
    private int tileSize;
    private int frameCount;
    
    public static final int MAX_FRAME_COUNT = 8;
    public static final int ENTITY_COUNT = 3;
    public static final int PATH_TILE_COUNT = 16;
    public static final String SKIN_LIBRARY = (new File("skins/")).getAbsolutePath() + File.separator;
    
    private static TileSet instance = new TileSet();
    
    /**
     * All images should be of the same size and should be in .png format.
     *
     * Entity tiles must be named in the format: t_d_f.png
     * where 't' is type (0 = Pac-Man, 1 = Ghost1, 2 = Ghost2, 3 = Fleeing ghost);
     * 'd' is direction (0 = up, 1 = right, 2 = down, 3 = left);
     * 'f' is frame (sequentially numbered from 0 to 1).
     * Example: The second frame of Pac-Man moving left: "0_3_1.png"
     *
     * Path tiles must be named in the format: urdl.png
     * where each symbol is a binary digit signifying if the path exists in the specified direction:
     * 'u' = up, 'r' = right, 'd' = down, 'l' = left.
     * Example: "1010.png" signifies a tile in which there is an upward and a downward path.
     *
     * The base or empty tile must be named "base.png"
     *
     * Point tiles must be named "p_" followed by the amount of points.
     * Example: A 100 point marker should be called "p_100.png"
     */
    
    /**
     * Initializes the TileSet object and loads the specified tileset.
     * @param path A path in the filesystem pointing to the initial tileset to be loaded.
     */
    public TileSet()
    {
        this.entityTiles = new Image[ENTITY_COUNT][Node.DIRECTION_COUNT][MAX_FRAME_COUNT];
        this.pathTiles = new Image[PATH_TILE_COUNT];
        this.pointTiles = new Image[2];
        this.frameCount = 0;
    }
    
    /**
     * Returns a reference to the Singleton instance of TileSet.
     * @return Singleton instance of TileSet.
     */
    public static TileSet getInstance()
    {
        return instance;
    }
    
    /**
     * Loads a new tileset, found at the specified path.
     * @param path A path in the filesystem pointing to the tileset to be loaded.
     * @return True on succes, false on failure.
     */
    public boolean loadTileSet(String path)
    {
        return this.loadTileSet(new File(path));
    }
    
    /**
     * Loads a new tileset, found at the specified path.
     * @param path A path in the filesystem pointing to the tileset to be loaded.
     * @return True on succes, false on failure.
     */
    public boolean loadTileSet(File file)
    {
        this.frameCount = 0;
        try
        {
            if(file.isDirectory())
            {
                String path = file.getAbsolutePath() + file.separator;
                for(int i=0; i<ENTITY_COUNT; i++)
                {
                    for(int j=0; j<Node.DIRECTION_COUNT; j++)
                    {
                        for(int k=0; k<this.MAX_FRAME_COUNT; k++)
                        {
                            if(new File(path+i+"_"+j+"_"+k+".png").isFile())
                            {
                                this.entityTiles[i][j][k] = (new ImageIcon(path +  i +"_"+ j + "_"+k+".png")).getImage();
                                if(k+1 > this.frameCount) {
                                    this.frameCount = k+1;
                                }
                            }
                            else
                            {
                                this.entityTiles[i][j][k] = null;
                            }
                        }
                    }
                }

                for (int i = 0; i < PATH_TILE_COUNT; i++)
                {
                    //System.out.println(file.getAbsolutePath() + file.separator + this.zeroPad(Integer.toBinaryString(i),4));
                    this.pathTiles[i] = (new ImageIcon(path + this.zeroPad(Integer.toBinaryString(i),4) + ".png")).getImage();
                }
                
                this.pointTiles[0] = new ImageIcon(path + "p10.png").getImage();
                this.pointTiles[1] = new ImageIcon(path + "p25.png").getImage();

                this.baseTile = (new ImageIcon(path +  "base.png")).getImage();
                this.tileSize = this.baseTile.getWidth(null);
            }
            
            return true;
        }
        catch(Exception e)
        {
            //Most likely a FileNotFoundException
            return false;
        }
    }
    
    /**
     * Retrieves the base (empty) tile of the current tileset.
     * @return The image object of the base tile.
     */
    public Image getBaseTile()
    {
        return this.baseTile;
    }
    
    /**
     * Retrieves the specified entity frame of the current tileset.
     * @param index The entity index to be retrieved. 0 = Pac-Man, 1 = Ghost1, 2 = Ghost2, 3 = Fleeing Ghost.
     * @param direction The direction the entity is facing. 0 = up, 1 = right, 2 = down, 3 = left.
     * @param frame The frame number to be retrieved. Continuous starting from 0.
     * @return The image object of the specified entity frame.
     */
    public Image getEntityTile(int index, int direction, int frame)
    {
        if(index < this.entityTiles.length && direction < this.entityTiles[0].length && frame >= 0)
        {
            if(this.entityTiles[index][direction][frame] != null)
                return this.entityTiles[index][direction][frame];
            else
                return this.getEntityTile(index, direction, frame - 1);
        }
        else
            return null;
    }
    
    /**
     * Retrieves the specified path tile of the current tileset.
     * @param tile The number of the path tile to be retrieved. This number is sequentially numbered depending on the binary representation of the paths present in the image. See source code for examples.
     * @return The image object of the base tile.
     */
    public Image getPathTile(int tile)
    {
        if(tile < this.pathTiles.length)
            return this.pathTiles[tile];
        else
            return null;
    }
    
    /**
     * Retrieves the point tile of the specified number of points, for the current tileset.
     * @param int Number of points to represent
     * @return The image object of the point tile.
     */
    public Image getPointsTile(int points)
    {
        if(points <= 10)
            return this.pointTiles[0];
        else
            return this.pointTiles[1];
    }
    
    /**
     * Retrieves the horizontal pixel size of the tileset. As all tiles must be of the same size, and must be squares, this number is also the height of every tile.
     */
    public int getTileSize()
    {
        return this.tileSize;
    }
    
    public int getFrameCount()
    {
        return this.frameCount;
    }
    
    /**
     * Reformats a string to have leading zeroes, up to the given final length.
     * @param string The string to be reformatted.
     * @param length The desired length of the string, after reformatting.
     * @return The reformatted string including leading zeroes.
     */
    public static String zeroPad(String string, int length)
    {
        StringBuilder builder = new StringBuilder(length);
        
        for (int i = string.length(); i < length; i++)
        {
            builder.append('0');
        }
        
        builder.append(string);
        
        return builder.toString();
    }        
}