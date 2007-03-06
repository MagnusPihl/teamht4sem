/*
 * KeyboardController.java
 *
 * Created on 5. marts 2007, 11:33
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
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
    }

    public void move()
    {
        Point pos = this.entity.getPosition();
        Node current_node = PacmanApp.getInstance().getGameScene().getField().getNodeAt(pos);
        Node next_node = current_node.getNodeAt(this.entity.getDirection());
        if(next_node != null)
            if(next_node.getEntity() == null)
            {
                current_node.setEntity(null);
                next_node.setEntity(this.entity);
                this.entity.setPosition(next_node.getPosition());
            }
    }

    public void calculateNextMove()
    {
        Node node = PacmanApp.getInstance().getGameScene().getField().getNodeAt(this.entity.getPosition());
        if(up.isPressed() && node.getNodeAt(Node.UP)!=null)
            this.entity.setDirection(Node.UP);
        if(right.isPressed() && node.getNodeAt(Node.RIGHT)!=null)
            this.entity.setDirection(Node.RIGHT);
        if(down.isPressed() && node.getNodeAt(Node.DOWN)!=null)
            this.entity.setDirection(Node.DOWN);
        if(left.isPressed() && node.getNodeAt(Node.LEFT)!=null)
            this.entity.setDirection(Node.LEFT);
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