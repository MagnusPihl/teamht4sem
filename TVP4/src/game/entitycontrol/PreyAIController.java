/*
 * PreyAIController.java
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

import game.*;
import field.*;
import java.awt.*;
import game.input.*;

/**
 *
 * @author LMK
 */
public class PreyAIController extends EntityController {
            
    int lastDirection;
    int nextDirection;
    
    /** Creates a new instance of PreyAIController */
    public PreyAIController(Entity _entity) {
        super(_entity);
        this.lastDirection = -1;
        this.nextDirection = -1;
    }
    
    /**
     * Execute move
     */
    public void move() {
        Point pos = this.entity.getPosition();        
        Node[] nodes = PacmanApp.getInstance().getGameScene().getField().getNodeAt(pos).getConnectedNodes();
        
        this.nextDirection = -1;
        for (int i = 0; i < nodes.length; i++) {
            if ((nodes[i] != null)&&(i != this.lastDirection)) {
                if (!nodes[i].holdsEntity()) {
                    nodes[i].setEntity(this.entity);
                    
                    switch (i) {
                        case Node.UP : pos.y--;
                        case Node.DOWN : pos.y++;
                        case Node.LEFT : pos.x--;
                        case Node.RIGHT : pos.x++;
                    }
                    this.nextDirection = i;
                    break;
                }
            }
        }
    }
    
    /**
     * Calculate next move
     */
    public void calculateNextMove() {
    
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
