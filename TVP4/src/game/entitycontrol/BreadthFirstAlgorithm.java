/*
 * Astar.java
 *
 * Created on 9. januar 2007, 18:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
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
    
    /** Creates a new instance of Astar */
    public BreadthFirstAlgorithm() {
    }
    
    public int search(Node from, Node to) {
        LinkedList open = new LinkedList();
        Set closed = new TreeSet();
        Map parents = new HashMap();
     
        Node currentNode = null;
        Node tempNode = null;
        Node[] connectedNodes = null;
        
        open.add(to);
        while (!open.isEmpty()) {                        
            currentNode = (Node)open.removeFirst();
            
            if (currentNode.equals(from)) {
                return from.connectedAt((Node)parents.get(from));
            } else {
                closed.add(currentNode);
                connectedNodes = currentNode.getConnectedNodes();
                
                for (int i = 0; i < Node.DIRECTION_COUNT; i++) {
                    tempNode = connectedNodes[i];
                    if (tempNode != null) {                    
                        if ((!open.contains(tempNode))&&(!closed.contains(tempNode))) {
                            if ((tempNode.holdsEntity())&&(!tempNode.equals(from))) {
                                closed.add(tempNode);
                            } else {
                                open.add(tempNode);
                                parents.put(tempNode, currentNode);                                
                            }
                        }                      
                    }
                }
            }
        }
        
        System.out.println("No path found");
        return Node.INVALID_DIRECTION;
    }
    
}