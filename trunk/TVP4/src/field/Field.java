/*
 * Field.java
 *
 * Created on 9. februar 2007, 09:48
 *
 * Company: HT++
 *
 * @author Lau Maack-Krommes
 * @version 1.3
 *
 *
 * ******VERSION HISTORY******
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
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Serializable;

public class Field implements Serializable {
        
    private LinkedList nodes;
    
    /** 
     * Creates a new empty field 
     */
    public Field() {
        this.nodes = new LinkedList();
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
    public void addNodeAt(int x, int y, int points) {
                 
        if (this.getNodeAt(x,y) == null) {
            this.nodes.add(new Node(
                    new Point(x,y), 
                    this.getNodeAt(x-1, y),
                    this.getNodeAt(x+1, y),
                    this.getNodeAt(x, y-1),
                    this.getNodeAt(x, y+1),
                    points));
        }
    }
    
    /**
     * Removes node at position and removes all connections to it.
     * 
     * @param position of the node to remove.
     */
    public void removeNodeAt(int x, int y) {
        Node node = this.getNodeAt(x, y);
        
        if (node != null) {
            node.removeAllConnections();
            this.nodes.remove(node);
        }
    }
    
    /**
     * Get node associated with position on the field. 
     * 
     * @param position to search for.
     * @return The node at position. If no node is associated
     * NULL is returned.
     */
    public Node getNodeAt(int x, int y) {
        Point position = new Point(x, y);
        Node current = null;
        
        for (Iterator i = this.nodes.iterator(); i.hasNext();) {
            current = (Node)i.next();
            if (current.getPosition().equals(position)) {
                return current;
            }
        }
        
        return null;
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
        
        for (Iterator i = this.nodes.iterator(); i.hasNext();) {
            current = ((Node)i.next()).getPosition();
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
        
        return new Dimension(maxX - minX, maxY - minY);        
    }
    
    /**
     * Retrieve the list of nodes at the field
     * 
     * @return list of nodes on the field
     */
    public LinkedList getNodeList() {
        return this.nodes;
    }
}