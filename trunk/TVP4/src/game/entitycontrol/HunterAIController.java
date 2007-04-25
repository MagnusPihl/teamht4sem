/*
 * HunterAIController.java
 *
 * Created on 5. marts 2007, 12:01
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 5. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game.entitycontrol;

import field.*;
import game.input.*;

/**
 *
 * @author LMK
 */
public class HunterAIController extends EntityController {
    
    Entity prey;
    SearchAlgorithm algorithm;
    
    /** Creates a new instance of HunterAIController */
    public HunterAIController(Entity _entity, Entity _prey) {
        super(_entity);        
        this.prey = _prey;
        this.algorithm = new BreadthFirstAlgorithm();
    }       
    
    /**
     * Calculate next move
     */
    public void calculateNextMove() {           
        this.nextDirection = this.algorithm.search(super.entity.getNode(), this.prey.getNode());                
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
