/*
 * PreyAIController.java
 *
 * Created on 5. marts 2007, 12:01
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
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
public class PreyAIController extends EntityController {
            
    int lastDirection;
    int nextDirection;
    
    /** Creates a new instance of PreyAIController */
    public PreyAIController(Entity _entity) {
        super(_entity);
        this.lastDirection = Node.INVALID_DIRECTION;
        this.nextDirection = Node.INVALID_DIRECTION;
    }
    
    /**
     * Execute move
     */
    public int move() {
        if (this.nextDirection != Node.INVALID_DIRECTION) {
            Point pos = this.entity.getPosition();
            Node node = PacmanApp.getInstance().getGameScene().getField().getNodeAt(pos);
            node.setEntity(null);
            node.getConnectedNodes()[this.nextDirection].setEntity(this.entity);
            this.entity.setNode(node.getConnectedNodes()[this.nextDirection]);
            this.entity.setDirection(this.nextDirection);
            this.lastDirection = this.nextDirection;
            return this.entity.getDirection();
        }
        return -1;
    }
    
    /**
     * Calculate next move
     */
    public void calculateNextMove() {
        Point pos = this.entity.getPosition();        
        Node[] nodes = PacmanApp.getInstance().getGameScene().getField().getNodeAt(pos).getConnectedNodes();
        
        this.nextDirection = Node.INVALID_DIRECTION;
        for (int i = 0; i < nodes.length; i++) {
            if ((nodes[i] != null)&&(i != Node.getOpposite(this.lastDirection))) {
                if (!nodes[i].holdsEntity()) {
                    this.nextDirection = i;                    
                    break;
                }
            }
        }
        
        if (this.nextDirection == Node.INVALID_DIRECTION) {
            this.nextDirection = Node.getOpposite(this.lastDirection);
        }
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
