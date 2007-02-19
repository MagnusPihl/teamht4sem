/*
 * Field.java
 *
 * Created on 9. februar 2007, 09:48
 *
 * Company: HT++
 *
 * @author Lau Maack-Krommes
 * @version 1.7
 *
 * ******VERSION HISTORY****** 
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
     */
    public void placePacman(Point position) {
        this.placeEntityAt(position, 0);
    }
    
    public void placeGhost(Point position) {
        for (int i = 1; i < this.entities.length; i++) {
            if (this.entities[i] == null) {                
                this.placeEntityAt(position, i);
                this.hasChanged = true;
                return;
            }
        }
    }
    
    private boolean placeEntityAt(Point position, int id) {  
        Node node = this.getNodeAt(position);
        if ((node != null)&&(this.getEntityAt(position) == null)) {
            if (id == 0) {
                node.setPoints(0);
            }
            this.entities[id] = new EntityRenderer(new Entity(position, id));
            this.hasChanged = true;
            return true;
        }
        return false;
    }
    
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
     * Place a starting position for a ghost. If two ghosts have already been
     * placed, the first of the two will be replaced.
     *
     * @param position on the field;
     */
    /*public int placeGhost(Point position) {        
        this.hasChanged = true;
        
        if (this.entityStartPositions[1] != null) {
            this.entityStartPositions[2] = this.entityStartPositions[1];
        }
        
        this.entityStartPositions[1] = position;
        this.hasChanged = true;
    }*/
    
    /**
     * Get the starting position of the entity at index
     * 
     * @param index of entity. 0 is pacman, 1-2 are ghosts.
     * @return the Point at which the entity should start.
     */
    /*public Point getEntityAt(int index) {
        return this.entities[index];
    }*/
    
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
     * Number of entities.
     */
    /*public int getEntityCount() {
        return this.entityStartPositions.length;
    }*/
    
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
        Node node = this.getNodeAt(position);
        
        if (node != null) {
            node.removeAllConnections();
            this.nodes.remove(position);
        }
        
        this.hasChanged = true;
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
            
    public void drawField(Graphics g) {
        this.renderer.drawBaseTile(g);
        this.renderer.drawNodes(g);
        
        for (int i = 0; i < this.entities.length; i++) {
            if (this.entities[i] != null) {
                this.entities[i].draw(g);
            }
        }
    }
}