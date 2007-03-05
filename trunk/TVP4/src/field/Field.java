/*
 * Field.java
 *
 * Created on 9. februar 2007, 09:48
 *
 * Company: HT++
 *
 * @author Lau Maack-Krommes
 * @version 1.11
 *
 * ******VERSION HISTORY******   
 * LMK @ 05. marts 2007 (v 1.11)
 * Added offset coordinates to drawField()
 * LMK @ 20. februar 2007 (v 1.10)
 * Fixed getSize(), now returns (0,0) when field is empty instead of (1,1)
 * Magnus Hemmer Pihl @ 23. februar 2007 (v 1.9)
 * Added boolean returns to placePacman() and placeGhost() methods
 * LMK @ 20. februar 2007 (v 1.8)
 * Changed to use TileSet
 * Changed to hold Entities
 * Changed to use FieldRenderer
 * Added methods to draw
 * Added methods to remove and place entities
 * LMK @ 16. februar 2007 (v 1.7)
 * Added getEntityStartingPositon
 * LMK @ 14. februar 2007 (v 1.6)
 * Added entityStartingPositons
 * When addNoteAt is passed a point of an existing node it will now
 * readjust the points held by that node.
 * LMK @ 12. februar 2007 (v 1.5)
 * You can no longer add Nodes at position less than (0,0)
 * Added hasChanged
 * LMK @ 11. februar 2007 (v 1.4)
 * Fixed Field.getSize() bug
 * LMK @ 9. februar 2007 (v 1.3)
 * Added method addNodeAt(int x, int y, int points)
 * Changed Point to simple int x, int y
 * LMK @ 9. februar 2007 (v 1.2)
 * Made nodes private
 * LMK @ 9. februar 2007 (v 1.1)
 * Added method getSize()
 * LMK @ 9. februar 2007 (v 1.0)
 * Created
 *
 */

package field;

import game.visual.EntityRenderer;
import game.visual.FieldRenderer;
import java.awt.*;
import java.util.*;
import java.io.*;

public class Field implements Serializable{
        
    private FieldRenderer renderer;
    private EntityRenderer[] entities;
    private HashMap nodes;
    private transient boolean hasChanged;
    private int lastEntity;
    
    /** 
     * Creates a new empty field 
     */
    public Field() {
        this.renderer = new FieldRenderer(this);
        this.nodes = new HashMap();
        this.entities = new EntityRenderer[3];
        this.lastEntity = 0;
        this.hasChanged = false;
    }
    
    /**
     * Set the starting position of Pacman on the field
     *
     * @param position on the field;
     * @return tue if the entity has been placed
     */
    public boolean placePacman(Point position) {
        return this.placeEntityAt(position, 0);
    }
    
    /**
     * Place a ghost on the field. If the maximum amount of ghosts has been
     * placed no ghost is placed.
     *
     * @param position on the field;
     * @return tue if the entity has been placed
     */
    public boolean placeGhost(Point position) {
        for (int i = 1; i < this.entities.length; i++) {
            if (this.entities[i] == null) {                
                boolean result = this.placeEntityAt(position, i);
                this.hasChanged = true;
                return result;
            }
        }
        return false;
    }
    
    /**
     * Place entity on the field. The Entity is only placed, if a free node is
     * available at the point.
     *
     * @param position on the field
     * @param id of the entity(, must be unique)
     * @return tue if the entity has been placed
     */
    private boolean placeEntityAt(Point position, int id) {  
        Node node = this.getNodeAt(position);
        if ((node != null)&&(this.getEntityAt(position) == null)) {
            /*if (id == 0) {
                node.setPoints(0);
            }*/
            this.entities[id] = new EntityRenderer(new Entity(position, id));
            this.hasChanged = true;
            return true;
        }
        return false;
    }
    
    /**
     * Get entity placed at positon
     *
     * @param position on the field
     * @return entity at position
     */
    public Entity getEntityAt(Point position) {
        EntityRenderer current = null;
        
        for (int i = 0; i < this.entities.length; i++) {
            current = this.entities[i];
            if (current != null) {
                if (current.getEntity().getPosition().equals(position)) {
                    return current.getEntity();
                }
            }
        }
        
        return null;
    }       
    
    /**
     * Remove entity at position.
     *
     * @param position on field
     */
    public void removeEntityAt(Point position) {        
        for (int i = 0; i < this.entities.length; i++) {
            if (this.entities[i] != null) {
                if (this.entities[i].getEntity().getPosition().equals(position)) {
                    this.entities[i] = null;
                }
            }
        }
    }   
    
    /**
     * Add note to field at position with 0 points held
     *
     * @param x coordinate of node position.
     * @param y coordinate of node position.
     */
    public void addNodeAt(int x, int y) {
        this.addNodeAt(x, y, 0);                 
    }
    
    /**
     * Add note to field at position. If a node is already present at the
     * passed position, the node will have it's points adjusted to match
     * the passed amount.
     * 
     * @param position to add node.
     * @param points to be held by node.
     */    
    public void addNodeAt(Point position, int points) {    
        if ((0 <= position.x)&&(0 <= position.y)) {
            Node current = this.getNodeAt(position);
            
            if (current == null) {
                this.nodes.put(position, new Node(
                        this.getNodeAt(position.x-1, position.y),
                        this.getNodeAt(position.x+1, position.y),
                        this.getNodeAt(position.x, position.y-1),
                        this.getNodeAt(position.x, position.y+1),
                        points));
                
            } else {
                current.setPoints(points);
            }
            
            this.hasChanged = true;
        }
    }
    
    /**
     * Same as calling #addNoteAt(Point, int)
     * 
     * @param x coordinate of node position.
     * @param y coordinate of node position.
     * @param points to be held by node.
     */
    public void addNodeAt(int x, int y, int points) {
        this.addNodeAt(new Point(x,y), points);
    }
    
    /**
     * Removes node at position and removes all connections to it.
     * 
     * @param position of the node to remove.
     */
    public void removeNodeAt(Point position) {
        if (this.getEntityAt(position) == null) {        
            Node node = this.getNodeAt(position);
            
            if (node != null) {
                node.removeAllConnections();
                this.nodes.remove(position);
            }

            this.hasChanged = true;
        }
    }
    
    /**
     * Removes node at the specified coordinates. 
     * Same as calling #removeNodeAt(Point).
     *
     * @param x coordinate of node position.
     * @param y coordinate of node position.
     */
    public void removeNodeAt(int x, int y) {
        this.removeNodeAt(new Point(x,y));
    }
    
    /**
     * Get node associated with position on the field. 
     * 
     * @param x to search for.
     * @param y to search for.
     * @return The node at position. If no node is associated
     * NULL is returned.
     */
    public Node getNodeAt(int x, int y) {                
        return (Node)this.nodes.get(new Point(x,y));
    }
    
    /**
     * Get node associated with position on the field. 
     * 
     * @param position to search for.
     * @return The node at position. If no node is associated
     * NULL is returned.
     */
    public Node getNodeAt(Point position) {                
        return (Node)this.nodes.get(position);
    }
    
    /**
     * If a node is available at the passed position it will be removed, 
     * if none is available one is added.
     *
     * @param position to invert node.
     * @param points to be held by node.
     */
    public void invertNodeAt(Point position, int points) {
        if (this.getNodeAt(position) != null) {
            this.removeNodeAt(position);
        } else {
            this.addNodeAt(position, points);
        }
    }
    
    /**
     * Get the dimension of the field, based on the positions of the nodes.
     * Once the field has been created save the size, this function computes
     * the size anew every time it's called.
     * 
     * @return The difference between dimension of the field. 
     * 0,0 if the field is empty.
     */
    public Dimension getSize() {
        if (this.nodes.size() != 0) {
            int minX = 0;
            int maxX = 0;
            int minY = 0;
            int maxY = 0;
            Point current = null;

            for (Iterator i = this.nodes.keySet().iterator(); i.hasNext();) {
                current = (Point)i.next();
                if (current.getX() < minX) {
                    minX = (int)current.getX();
                }
                if (current.getX() > maxX) {
                    maxX = (int)current.getX();
                }
                if (current.getY() < minY) {
                    minY = (int)current.getY();
                }
                if (current.getY() > maxY) {
                    maxY = (int)current.getY();
                }
            }
        
            return new Dimension(maxX - minX + 1, maxY - minY +1);        
        }
        
        return new Dimension(0,0);
    }
    
    /**
     * Retrieve the list of nodes at the field
     * 
     * @return list of nodes on the field
     */
    public HashMap getNodeList() {
        return this.nodes;
    }
    
    /**
     * Removes all nodes held by field. And clear entity start positions.
     */
    public void clearField() {
        //add code to clear entity positions
        this.nodes.clear();   
        this.entities = new EntityRenderer[3];
        this.hasChanged = false;
    }
    
    /**
     * Get contents of field and entityStatingPositions from file
     *
     * @param file to load data from.
     */
    public boolean loadFrom(File file) {
        ObjectInputStream in = null;
        boolean success = true;
            
        if (file.isFile()) {                                
            try {
                in = new ObjectInputStream(new FileInputStream(file));
                this.entities = new EntityRenderer[in.readInt()];
                Object current = null;
                
                for (int i = 0; i < this.entities.length; i++) {
                    current = in.readObject();
                    if (current != null) {
                        this.entities[i] = new EntityRenderer((Entity)current);
                    }
                }
            
                this.nodes = (HashMap)in.readObject();                
            } catch (Exception e) {
                success = false;
                e.printStackTrace(); 
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                } finally {
                    in = null;
                }
            }    
        }        
        
        if (success) {
            this.hasChanged = false;
        }
        
        return success;
    }
        
    /**
     * Save contents of field and entityStatingPositions to file
     *
     * @param file to save data to.
     */
    public boolean saveTo(File file) {
        ObjectOutputStream out = null;
        boolean success = true;
                                                  
        try {
            out = new ObjectOutputStream(new FileOutputStream(file));
            
            out.writeInt(this.entities.length);
            
            for (int i = 0; i < this.entities.length; i++) {
                if (this.entities[i] != null) {
                    out.writeObject(this.entities[i].getEntity());
                } else {
                    out.writeObject(null);
                }
            }
            
            out.writeObject(this.nodes);                
        } catch (Exception e) {
            success = false;
            e.printStackTrace(); 
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            } finally {
                out = null;
            }
        }    
        
        if (success) {
            this.hasChanged = false;
        }
        
        return success;
    }
    
    /**
     * Check whether the Field has changed since last save/load.
     * 
     * @return true if the state of the Field has changed.
     */
    public boolean hasChanged() {
        return this.hasChanged;
    }
    
    /**
     * Draw baseTile, nodes and entities
     * 
     * @param g canvas to draw on.
     * @param offset x.
     * @param offset y.
     * @param area to draw.
     */
    public void drawField(Graphics g, int offsetX, int offsetY, Dimension size) {
        this.renderer.drawBaseTile(g, offsetX, offsetY, size);
        this.renderer.drawNodes(g, offsetX, offsetY);
        
        for (int i = 0; i < this.entities.length; i++) {
            if (this.entities[i] != null) {
                this.entities[i].draw(g, offsetX, offsetY);
            }
        }
    }
    
    /**
     * Get the renderer used to draw the field
     *
     * @return FieldRenderer
     */
    public FieldRenderer getRenderer() {
        return this.renderer;
    }
    
    /**
     * Get array of entity renderers
     *
     * @return EntityRenderer[]
     */
    public EntityRenderer[] getEntityRenderers() {
        return this.entities;
    }
}