/*
 * Astar.java
 *
 * Created on 9. januar 2007, 18:16
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 17. april 2007 (v 1.1)
 * Added fullSearch method.
 */

package game.entitycontrol;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import field.*;

/**
 *
 * @author LMK
 */
public class BreadthFirstAlgorithm implements SearchAlgorithm {            
    private LinkedList open;
    private Set closed;
    private Map parents;

    private Node currentNode;
    private Node tempNode;
    private Node[] connectedNodes;
        
    /** Creates a new instance of Astar */
    public BreadthFirstAlgorithm() {
        this.open = new LinkedList();
        this.closed = new TreeSet();
        this.parents = new HashMap();
    }
    
    public int search(Node from, Node to) {
        this.open.clear();
        this.closed.clear();
        this.parents.clear();
     
        this.currentNode = null;
        this.tempNode = null;
        this.connectedNodes = null;
        
        this.open.add(to);
        while (!this.open.isEmpty()) {                        
            this.currentNode = (Node)this.open.removeFirst();
            
            if (this.currentNode.equals(from)) {
                return from.connectedAt((Node)this.parents.get(from));
            } else {
                this.closed.add(currentNode);
                this.connectedNodes = this.currentNode.getConnectedNodes();
                
                for (int i = 0; i < Node.DIRECTION_COUNT; i++) {
                    this.tempNode = this.connectedNodes[i];
                    if (this.tempNode != null) {                    
                        if ((!this.open.contains(this.tempNode))&&(!this.closed.contains(this.tempNode))) {
                            if ((this.tempNode.holdsEntity())&&(!this.tempNode.equals(from))) {
                                this.closed.add(this.tempNode);
                            } else {
                                this.open.add(this.tempNode);
                                this.parents.put(this.tempNode, this.currentNode);                                
                            }
                        }                      
                    }
                }
            }
        }
        
        System.out.println("No path found");
        return Node.INVALID_DIRECTION;
    }    
    
    /**
     * Get an array of directions to move, to get from A to B.
     * Moves are in ascending order.
     */
    public int[] fullSearch(Node from, Node to) {
        int result = this.search(from,to);
        
        if (result != Node.INVALID_DIRECTION) {            
            int[] moves = new int[this.parents.size()];
            Node next = null;
            
            for (int i = 0; i < moves.length; i++) {            
                next = (Node)this.parents.get(from);
                if (next != null) {
                    moves[i] = from.connectedAt((Node)this.parents.get(from));
                }
                from = next;
            }
            return moves;
        }
     
        return null;        
    }
}