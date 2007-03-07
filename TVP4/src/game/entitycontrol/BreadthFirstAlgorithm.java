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
        if (from == null) {
            System.out.println("hunter problem");
        } else {
            System.out.println("hunter okay");
        }
        
        if (to == null) {
            System.out.println("prey problem");
        } else {
            System.out.println("prey okay");
        }
        
        Set open = new TreeSet();
        Set closed = new TreeSet();
        Map parents = new TreeMap();
     
        Node currentNode = from;
        Node tempNode = null;
        Node[] connectedNodes = null;
        //Point[][] parents = new Point[size.width][size.height];
        
        open.add(from);
        //System.out.println(size);
        while (!open.isEmpty()) {                        
            currentNode = (Node)open.iterator().next();
            open.remove(currentNode);
            
            //System.out.println("Searching for " + currentPoint);
            if (currentNode.getPosition().equals(to.getPosition())) {
                //System.out.println("Found path");
                return this.getMove(parents, from, to);
            } else {
                closed.add(currentNode);
                connectedNodes = currentNode.getConnectedNodes();
                
                for (int i = 0; i < Node.DIRECTION_COUNT; i++) {
                    tempNode = connectedNodes[i];
                    
                    if ((!open.contains(tempNode))&&(!closed.contains(tempNode))) {
                        if (tempNode.holdsEntity()) {
                            closed.add(tempNode);
                        } else {
                            open.add(tempNode);
                            parents.put(tempNode, currentNode);
                        }
                    }                                
                }
            }
        }
        
        System.out.println("No path found");
        return Node.INVALID_DIRECTION;
    }
    
    public int getMove(Map parents, Node from, Node to) {
        boolean finished = false;
        Node lastNode = to;
        Node currentNode = to;
        
        while (currentNode != from) {            
            lastNode = currentNode;
            currentNode = (Node)parents.get(currentNode);            
        }
        
        return currentNode.connectedAt(lastNode);
    }
}