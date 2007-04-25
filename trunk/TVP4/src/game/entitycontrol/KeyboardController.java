/*
 * KeyboardController.java
 *
 * Created on 5. marts 2007, 11:33
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.3
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 25. April 2007 (v 1.3)
 * Changed nextDirection to nextDirection to match other controller classes
 * Removed the move function and moved it to EntityController
 *
 * Magnus Hemmer Pihl @ 17. april 2007 (v 1.2)
 * Altered input code so that direction is only updated when the entity is about to move.
 *
 * Magnus Hemmer Pihl @ 7. marts 2007 (v 1.1)
 * Modified move() to return an integer denoting direction of movement.
 *
 * Magnus Hemmer Pihl @ 5. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game.entitycontrol;

import field.Entity;
import field.Node;
import game.PacmanApp;
import game.input.InputAction;
import game.input.InputManager;
import java.awt.Point;
import java.awt.event.KeyEvent;

public class KeyboardController extends EntityController
{
    private int upkey, downkey, leftkey, rightkey;
    private InputAction up, down, left, right;        
    
    /** Creates a new instance of KeyboardController */
    public KeyboardController(Entity _entity)
    {
        super(_entity);
        
        this.up = new InputAction("Move up");
        this.down = new InputAction("Move down");
        this.left = new InputAction("Move left");
        this.right = new InputAction("Move right");
        
        this.upkey = KeyEvent.VK_UP;
        this.downkey = KeyEvent.VK_DOWN;
        this.leftkey = KeyEvent.VK_LEFT;
        this.rightkey = KeyEvent.VK_RIGHT;
        
        this.nextDirection = entity.getDirection();
    }
    
    public KeyboardController(Entity _entity, int _up, int _right, int _down, int _left)
    {
        super(_entity);
        
        this.up = new InputAction("Move up");
        this.down = new InputAction("Move down");
        this.left = new InputAction("Move left");
        this.right = new InputAction("Move right");
        
        this.upkey = _up;
        this.downkey = _down;
        this.leftkey = _left;
        this.rightkey = _right;
        
        this.nextDirection = entity.getDirection();
    }

    public void calculateNextMove()
    {
        Node node = PacmanApp.getInstance().getGameScene().getField().getNodeAt(this.entity.getPosition());
        if(up.isPressed() && node.getNodeAt(Node.UP)!=null)
            this.nextDirection = Node.UP;
        if(right.isPressed() && node.getNodeAt(Node.RIGHT)!=null)
            this.nextDirection = Node.RIGHT;
        if(down.isPressed() && node.getNodeAt(Node.DOWN)!=null)
            this.nextDirection = Node.DOWN;
        if(left.isPressed() && node.getNodeAt(Node.LEFT)!=null)
            this.nextDirection = Node.LEFT;
    }
    
    public void init(InputManager _input)
    {
        _input.mapToKey(up, this.upkey);
        _input.mapToKey(down, this.downkey);
        _input.mapToKey(left, this.leftkey);
        _input.mapToKey(right, this.rightkey);
    }
    
    public void deinit(InputManager _input)
    {
        _input.removeKeyAssociation(this.upkey);
        _input.removeKeyAssociation(this.downkey);
        _input.removeKeyAssociation(this.leftkey);
        _input.removeKeyAssociation(this.rightkey);
    }
}