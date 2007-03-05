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
import game.input.InputAction;
import game.input.InputManager;
import java.awt.event.KeyEvent;

public class KeyboardController extends EntityController
{
    private InputAction up, down, left, right;
    
    /** Creates a new instance of KeyboardController */
    public KeyboardController(Entity _entity)
    {
        super(_entity);
        
        this.up = new InputAction("Move up");
        this.down = new InputAction("Move down");
        this.left = new InputAction("Move left");
        this.right = new InputAction("Move right");
    }

    public void move()
    {
        if(up.isPressed())
            this.entity.setDirection(0);
        if(right.isPressed())
            this.entity.setDirection(1);
        if(down.isPressed())
            this.entity.setDirection(2);
        if(left.isPressed())
            this.entity.setDirection(3);
    }

    public void calculateNextMove()
    {
    }
    
    public void init(InputManager _input)
    {
        _input.mapToKey(up, KeyEvent.VK_UP);
        _input.mapToKey(down, KeyEvent.VK_DOWN);
        _input.mapToKey(left, KeyEvent.VK_LEFT);
        _input.mapToKey(right, KeyEvent.VK_RIGHT);
    }
    
    public void deinit(InputManager _input)
    {
        _input.removeKeyAssociation(KeyEvent.VK_UP);
        _input.removeKeyAssociation(KeyEvent.VK_DOWN);
        _input.removeKeyAssociation(KeyEvent.VK_LEFT);
        _input.removeKeyAssociation(KeyEvent.VK_RIGHT);
    }
}