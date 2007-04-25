/*
 * RandomController.java
 *
 * Created on 5. marts 2007, 12:01
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.2
 *
 * ******VERSION HISTORY******
 * LMK @ 25. April 2007 (v 1.2)
 * Removed the move function and moved it to EntityController
 * Changed name from PreyAIController to RandomController
 * 
 * Magnus Hemmer Pihl @ 7. marts 2007 (v 1.1)
 * Modified move()-method to return an integer denoting direction of movement.
 *
 * LMK @ 5. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game.entitycontrol;

import game.*;
import field.*;
import java.awt.*;
import game.input.*;

/**
 *
 * @author LMK
 */
public class RandomController extends EntityController {
            
    
    /** Creates a new instance of PreyAIController */
    public RandomController(Entity _entity) {
        super(_entity);
    }    
    
    /**
     * Calculate next move
     */
    public void calculateNextMove() {        
        Node[] nodes = this.entity.getNode().getConnectedNodes(); 
        
        this.nextDirection = super.getNextDirection();
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
