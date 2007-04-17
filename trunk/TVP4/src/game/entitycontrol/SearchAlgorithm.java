/*
 * SearchAlgorithm.java
 *
 * Created on 10. januar 2007, 23:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package game.entitycontrol;

import field.*;

/**
 *
 * @author LMK
 */
public interface SearchAlgorithm {        
    
    /**
     * Return direction in which to move next
     */
    public int search(Node from, Node to);        
    
    /**
     * Get an array of directions to move, to get from A to B.
     * Moves are in ascending order.
     */
    public int[] fullSearch(Node from, Node to);
}
