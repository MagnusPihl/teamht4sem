/*
 * Field.java
 *
 * Created on 9. februar 2007, 09:48
 *
 * Company: HT++
 *
 * @author Lau Maack-Krommes
 * @version 1.5
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 11. februar 2007 (v 1.5)
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

import java.awt.Point;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Iterator;
import java.io.*;

public class Field implements Serializable {
        
    private HashMap nodes;
    private transient boolean hasChanged;
    
    /** 
     * Creates a new empty field 
     */
    public Field() {
        this.nodes = new HashMap();
        this.hasChanged = false;
    }
    
    /**
     * Add note to field at position.
     *
     * @param position to add node.
     */
    public void addNodeAt(int x, int y) {
        this.addNodeAt(x, y, 0);                 
    }
    
    /**
     * Add note to field at position.
     *
     * @param position to add node.
     */    
    public void addNodeAt(Point position, int points) {                 
        if ((this.getNodeAt(position) == null)&&(0 <= position.x)&&(0 <= position.y)) {
            this.nodes.put(position, new Node(
                    this.getNodeAt(position.x-1, position.y),
                    this.getNodeAt(position.x+1, position.y),
                    this.getNodeAt(position.x, position.y-1),
                    this.getNodeAt(position.x, position.y+1),
                    points));
            
            this.hasChanged = true;
        }        
    }
    
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
    
    public void invertNodeAt(Point position) {
        if (this.getNodeAt(position) != null) {
            this.removeNodeAt(position.x, position.y);
        } else {
            this.addNodeAt(position.x, position.y);
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
    
    public void clearField() {
        this.nodes.clear();   
        this.hasChanged = false;
    }
    
    public boolean loadNodesFrom(File file) {
        ObjectInputStream in = null;
        boolean success = true;
            
        if (file.isFile()) {                                
            try {
                in = new ObjectInputStream(new FileInputStream(file));
                this.nodes = (HashMap)in.readObject();                
            } catch (Exception e) {
                success = false;
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
    
    public boolean saveNodesTo(File file) {
        ObjectOutputStream out = null;
        boolean success = true;
                                                  
        try {
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(this.nodes);                
        } catch (Exception e) {
            success = false;
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
    
    public boolean hasChanged() {
        return this.hasChanged;
    }
}