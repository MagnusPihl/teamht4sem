/*
 * InSightController.java
 *
 * Created on 8. marts 2007, 17:57
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
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
     * Execute move
     */
    public int move() {
        if (this.nextDirection != Node.INVALID_DIRECTION) {            
            super.entity.setNode(super.entity.getNode().getNodeAt(this.nextDirection));
            this.entity.setDirection(this.nextDirection);            
            this.lastDirection = this.nextDirection;
            return this.nextDirection;
        }
        
        return Node.INVALID_DIRECTION;
    }
    
    /**
     * Calculate next move
     */
    public void calculateNextMove() {           
        this.nextDirection = this.targetInSight();        
        
        if (this.prey.getNode().equals(this.entity.getNode().getNodeAt(this.nextDirection))) {
            this.nextDirection = Node.INVALID_DIRECTION;
        } else if (this.nextDirection == Node.INVALID_DIRECTION) {            
            this.nextDirection = super.getNextDirection();
        } else {
            if ((RANDOM.nextInt(10) == 0) && 
                    (this.entity.getNode().getNodeAt(Node.getOpposite(this.nextDirection)) != null)) {
                this.nextDirection = Node.getOpposite(this.nextDirection);
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
    
    
    /**
     * No initialization neeeded.
     *
     * @param input manager to register keys with
     */
    public void init(InputManager _input) {}
    
    
    /**
     * No deinitialization neeeded.
     *
     * @param input manager to unregister keys with
     */
    public void deinit(InputManager _input) {}
    
}
