/*
 * InSightController.java
 *
 * Created on 8. marts 2007, 17:57
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 * ******VERSION HISTORY******
 * 
 * LMK @ 25. April 2007 (v 1.1)
 * Removed the move function and moved it to EntityController
 *
 * LMK @ 8. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game.entitycontrol;

import field.*;
import game.input.*;

public class InSightController extends EntityController {    
    
    private Entity prey;
    
    /** Creates a new instance of InSightController */
    public InSightController(Entity entity, Entity prey) {
        super(entity);        
        this.prey = prey;
    }   
    
    /**
     * Calculate next move
     */
    public void calculateNextMove() {           
        this.nextDirection = this.targetInSight();                
        Node[] nodes = this.entity.getNode().getConnectedNodes();
        
        /**
         * if (this.entity.getNode().getNodeAt(this.nextDirection) != null) && (this.prey.getNode()) && 
         *       ())) {
         */
        if (this.nextDirection == Node.INVALID_DIRECTION) {            
            this.nextDirection = super.getNextDirection();
        } else if (this.prey.getNode().equals(nodes[this.nextDirection])) {
            this.nextDirection = Node.INVALID_DIRECTION;
        } else {
            if (RANDOM.nextInt(20) == 0) {
                if (nodes[Node.getOpposite(this.nextDirection)] != null) {
                    if (nodes[Node.getOpposite(this.nextDirection)].getEntity() == null) {
                        this.nextDirection = Node.getOpposite(this.nextDirection);
                    }
                }
            }
        }
    }
    
    public int targetInSight() {
        Node current = null;
        Node last = null;
        //System.out.println(current);
                
        for (int i = 0; i < Node.DIRECTION_COUNT; i++) {            
            current = super.entity.getNode();
            while (current != null) {                                
                if (this.prey.getPosition().equals(current.getPosition())) {                    
                    return i;
                }                
                current = current.getNodeAt(i);
            }                        
        }
        
        return Node.INVALID_DIRECTION;
    }            
}
