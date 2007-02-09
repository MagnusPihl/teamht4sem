/*
 * Field.java
 *
 * Created on 9. februar 2007, 09:48
 *
 * Company: HT++
 *
 * @author Lau Maack-Krommes
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 9. februar 2007 (v 1.0)
 * Created
 *
 */

package field;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Serializable;

public class Field implements Serializable {
        
    LinkedList nodes;
    
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
    public void addNodeAt(Point position) {
                 
        if (this.getNodeAt(position) == null) {
            this.nodes.add(new Node(
                    position, 
                    this.getNodeAt(new Point(position.x-1, position.y)),
                    this.getNodeAt(new Point(position.x+1, position.y)),
                    this.getNodeAt(new Point(position.x, position.y-1)),
                    this.getNodeAt(new Point(position.x, position.y+1)),
                    0));
        }
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
    public Node getNodeAt(Point position) {
        Node current = null;
        
        for (Iterator i = this.nodes.iterator(); i.hasNext();) {
            current = (Node)i.next();
            if (current.getPosition().equals(position)) {
                return current;
            }
        }
        
        return null;
    }
}
