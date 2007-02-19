/*
 * EntityRenderer.java
 *
 * Created on 16. februar 2007, 10:40
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 *
 * Magnus Hemmer Pihl @ 19. februar 2007 (v 1.1)
 * Updated draw()-method to properly retrieve direction and ID from its entity.
 * Magnus Hemmer Pihl @ 16. februar 2007 (v 1.0)
 * Created.
 *
 */

package field;

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
    protected TileSet tileSet;
    
    /**
     * Creates a new instance of EntityRenderer with a specified Entity to render
     * and a TileSet containing the images to paint with.
     *
     * @param entity Entity to draw.
     * @param tileSet TileSet containing images.
     */
    public EntityRenderer(Entity entity, TileSet tileSet)
    {
        this.animationDelay = 500;
        this.lastUpdate = System.currentTimeMillis();
        this.frameCounter = 0;
        
        this.entity = entity;
        this.tileSet = tileSet;
    }
    
    /**
     * Draw the contents of the field on the graphics canvas.
     *
     * @param g Canvas to draw on.
     */
    public void draw(Graphics g)
    {
        Point position = entity.getPosition();
        
        if((System.currentTimeMillis() - this.lastUpdate) > this.animationDelay)
        {
            this.frameCounter = (this.frameCounter+1)%2;
            this.lastUpdate = System.currentTimeMillis();
        }
        
        g.drawImage(
                this.tileSet.getEntityTile(entity.getID(), entity.getDirection(), this.frameCounter),
                position.x * this.tileSet.getTileSize(),
                position.y * this.tileSet.getTileSize(),
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
    
    /**
     * Set the TileSet used to render
     *
     * @param tileSet TileSet to render entity with.
     */
    public void setTileSet(TileSet tileSet)
    {
        this.tileSet = tileSet;
    }
}