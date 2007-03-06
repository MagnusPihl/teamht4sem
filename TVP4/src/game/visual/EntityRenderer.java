/*
 * EntityRenderer.java
 *
 * Created on 16. februar 2007, 10:40
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.4
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 05. marts 2007 (v 1.4)
 * Added offset coordinates to draw method
 *
 * Magnus Hemmer Pihl @ 27. februar 2007 (v 1.3)
 * Changed entity rendering, so that animation only occurs when entities are moving.
 * 
 * Magnus Hemmer Pihl @ 19. februar 2007 (v 1.2)
 * Removed local reference to TileSet, using instead the TileSet singleton instance.
 *
 * Magnus Hemmer Pihl @ 19. februar 2007 (v 1.1)
 * Updated draw()-method to properly retrieve direction and ID from its entity.
 * 
 * Magnus Hemmer Pihl @ 16. februar 2007 (v 1.0)
 * Created.
 *
 */

package game.visual;

import field.*;
import java.awt.*;
import java.io.IOException;
import java.io.File;
import javax.swing.ImageIcon;
import java.util.Iterator;

public class EntityRenderer
{
    private long animationDelay;
    private long lastUpdate;
    private int frameCounter;
    
    protected Entity entity;
    
    /**
     * Creates a new instance of EntityRenderer with a specified Entity to render.
     *
     * @param entity Entity to draw.
     */
    public EntityRenderer(Entity entity)
    {
        this.animationDelay = 100;
        this.lastUpdate = System.currentTimeMillis();
        this.frameCounter = 0;
        
        this.entity = entity;
    }
    
    /**
     * Draw the contents of the field on the graphics canvas.
     *
     * @param g Canvas to draw on.
     * @param offset x
     * @param offset y
     */
    public void draw(Graphics g, int offsetX, int offsetY)
    {
        Point position = entity.getPosition();
        int tileSize = TileSet.getInstance().getTileSize();
        
//        if(entity.isMoving())
//        {
            if((System.currentTimeMillis() - this.lastUpdate) > this.animationDelay)
            {
                this.frameCounter = (this.frameCounter+1)%2;
                this.lastUpdate = System.currentTimeMillis();
            }
//        }
        
        g.drawImage(
                TileSet.getInstance().getEntityTile(entity.getID(), entity.getDirection(), this.frameCounter),
                position.x * tileSize + offsetX,
                position.y * tileSize + offsetY,
                null);
    }
    
    /**
     * Return the entity that the renderer draws.
     *
     * @return Entity held by renderer.
     */
    public Entity getEntity()
    {
        return this.entity;
    }
}