package game.input;

import java.awt.event.*;
import java.awt.*;
import javax.swing.ImageIcon;
import java.awt.Component;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.SwingUtilities;

/*
 * InputManager.java
 *
 * Created 2006 by LMK
 * Based on examples in "Devoloping Games in Java" [Brackeen]
 * by Brackeen, David
 * www.brackeen.com
 * Listing 3.6 InputManager.java, page 134-139
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 28. februar 2007 (v 1.1)
 * Added removeKeyAssociation() to make faster removal of associations.
 *
 */
public class InputManager implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    public static final Cursor INVISIBLE_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(0,0), "invisible");

    //events to catch
    public static final byte KEYBOARD_EVENTS = 0x01;
    public static final byte MOUSE_MOTION_EVENTS = 0x02;
    public static final byte MOUSE_WHEEL_EVENTS = 0x04;
    public static final byte MOUSE_BUTTON_EVENTS = 0x08;
    public static final byte MOUSE_EVENTS = (MOUSE_BUTTON_EVENTS | MOUSE_MOTION_EVENTS | MOUSE_WHEEL_EVENTS);
    public static final byte ALL_EVENTS = KEYBOARD_EVENTS | MOUSE_EVENTS;

    //mouse
    public static final int MOUSE_MOVE_LEFT = 0;
    public static final int MOUSE_MOVE_RIGHT = 1;
    public static final int MOUSE_MOVE_UP = 2;
    public static final int MOUSE_MOVE_DOWN = 3;
    public static final int MOUSE_WHEEL_UP = 4;
    public static final int MOUSE_WHEEL_DOWN = 5;
    public static final int MOUSE_BUTTON_1 = 6;
    public static final int MOUSE_BUTTON_2 = 7;
    public static final int MOUSE_BUTTON_3 = 8;
    protected static final int MOUSE_COMMAND_COUNT = 9;

    //keyboard
    protected static final int KEYBOARD_COMMAND_COUNT = 600;
    protected InputAction[] keyboardActions;
    protected InputAction[] mouseActions;

    protected Point mousePosition;
    protected Point screenCenter;
    protected Component screen;
    protected Robot robot;
    protected boolean isRecentering;
    protected byte events = 0;

    /**
     * Create input manager that catches events from supplied component.
     * 
     * @param _screen to catch events from.
     */
    public InputManager(Component _screen) {
        this(_screen, ALL_EVENTS);
    }
    
    /**
     * Create input manager that catches only specified events from supplied 
     * component.
     * 
     * @param _screen to catch events from.
     * @param _events to catch.
     */
    public InputManager(Component _screen, byte _events) {
        this.screen = _screen;
        this.mousePosition = new Point();
        this.screenCenter = new Point();
        this.setEventsToCatch(_events);
        this.removeAllAssociations();
        this.screen.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Specifies which events to catch.
     *
     * @param _events to catch. An or'ed combination events to catch.
     */
    public void setEventsToCatch(byte _events) {
        if ((_events & KEYBOARD_EVENTS) != 0) {
            if ((this.events & KEYBOARD_EVENTS) == 0) {
                this.screen.addKeyListener(this);
            }
        } else {
            if ((this.events & KEYBOARD_EVENTS) != 0) {
                this.screen.removeKeyListener(this);
            }
        }
        if ((_events & MOUSE_BUTTON_EVENTS) != 0) {
            if ((this.events & MOUSE_BUTTON_EVENTS) == 0) {
                this.screen.addMouseListener(this);
            }
        } else {
            if ((this.events & MOUSE_BUTTON_EVENTS) != 0) {
                this.screen.addMouseListener(this);
            }
        }
        if ((_events & MOUSE_MOTION_EVENTS) != 0) {
            if ((this.events & MOUSE_MOTION_EVENTS) == 0) {
                this.screen.addMouseMotionListener(this);
            }
        } else {
            if ((this.events & MOUSE_MOTION_EVENTS) != 0) {
                this.screen.removeMouseMotionListener(this);
            }
        }
        if ((_events & MOUSE_WHEEL_EVENTS) != 0) {
           if ((this.events & MOUSE_WHEEL_EVENTS) == 0) {
               this.screen.addMouseWheelListener(this);
           }
       } else {
           if ((this.events & MOUSE_WHEEL_EVENTS) != 0) {
               this.screen.removeMouseWheelListener(this);
           }
       }
    }

    /**
     * Set cursor. If not set an invisible cursor will be used.
     * 
     * @param cursor object.
     */
    public void setCursor(Cursor _cursor) {
        this.screen.setCursor(_cursor);
    }

    /**
     * Set mousemovement to be relative. When relative the mouse will be locked 
     * to the center of the screen.
     *
     * @param _mode. If true mouse movement is regarded relative.
     */
    public void setRelativeMouseMode(boolean _mode) {
        if (_mode == this.isRelativeMouseMode()) {
            return;
        }
        if (_mode) {
            try {
                this.robot = new Robot();
                this.recenterMouse();
            } catch (AWTException e) {
                this.robot = null;
            }
        } else {
            this.robot = null;
        }
    }

    /**
     * Check whether mousemovement is relative.
     *
     * @return true if mouse is locked to the center of the screen.
     */
    public boolean isRelativeMouseMode() {
        return (this.robot != null);
    }

    /**
     * Map an action to a key. Keycodes correspond to virtual keycodes from
     * KeyEvent
     *
     * @param action object to register events to.
     * @param virtual keycode.
     */
    public void mapToKey(InputAction _action, int _keyCode) {        
        this.keyboardActions[_keyCode] = _action;
    }

    /**
     * Map an action to a mouse event. Mouse codes are defined in this class.
     *
     * @param action object to register events to.
     * @param mouse code.
     */
    public void mapToMouse(InputAction _action, int _mouseCode) {
        this.mouseActions[_mouseCode] = _action;
    }

    /**
     * Remove supplied action from all associated events, both mouse and keyboard.
     *
     * @param action to remove.
     */
    public void removeAssociation(InputAction _action) {
        for (int i = 0; i < this.keyboardActions.length; i++) {
            if (this.keyboardActions[i] == _action) {
                this.keyboardActions[i] = null;
            }
        }
        for (int i = 0; i < this.mouseActions.length; i++) {
            if (this.mouseActions[i] == _action) {
                this.mouseActions[i] = null;
            }
        }
        _action.reset();
    }
    
    /**
     * Remove association based on keyCode. Faster than removeAssociation.
     *
     * @param _keyCode. KeyCode less than 600.
     */
    public void removeKeyAssociation(int _keyCode) {
        if (_keyCode < this.keyboardActions.length) {
            this.keyboardActions[_keyCode] = null;
        }    
    }
    
    /**
     * Removes all associations to actions.
     */
    public void removeAllAssociations() {
        this.keyboardActions = new InputAction[KEYBOARD_COMMAND_COUNT];
        this.mouseActions = new InputAction[MOUSE_COMMAND_COUNT];
    }

    /**
     * Get a list of names of keys and mouse command associated with the
     * supplied action
     *
     * @param action to find associations for.
     * @return list of associations.
     */
    public List getAssociations(InputAction _action) {
        List list = new ArrayList(3);
        for (int i = 0; i < this.keyboardActions.length; i++) {
            if (this.keyboardActions[i] == _action) {
                list.add(getKeyboardActionName(i));
            }
        }
        for (int i = 0; i < this.mouseActions.length; i++) {
            if (this.mouseActions[i] == _action) {
                list.add(getMouseActionName(i));
            }
        }
        return list;
    }

    /**
     * Reset all actions.
     */
    public void resetAllActions() {
        for (int i = 0; i < this.keyboardActions.length; i++) {
            if (this.keyboardActions[i] != null) {
                this.keyboardActions[i].reset();
            }
        }
        for (int i = 0; i < this.mouseActions.length; i++) {
            if (this.mouseActions[i] != null) {
                this.mouseActions[i].reset();
            }
        }

    }

    /**
     * Get name of key associated with code
     *
     * @param code to look for.
     * @return name of key.
     */
    public static String getKeyboardActionName(int _keyCode) {
        return KeyEvent.getKeyText(_keyCode);
    }

    /**
     * Get name of mouse action associated with code.
     *
     * @param code to look for.
     * @return name of mouse action.
     */
    public static String getMouseActionName(int _mouseCode) {
        switch (_mouseCode) {
            case MOUSE_MOVE_LEFT: return "Mouse move left";
            case MOUSE_MOVE_RIGHT: return "Mouse move right";
            case MOUSE_MOVE_UP: return "Mouse move up";
            case MOUSE_MOVE_DOWN: return "Mouse move down";
            case MOUSE_WHEEL_UP: return "Mouse wheel up";
            case MOUSE_WHEEL_DOWN: return "Mouse wheel down";
            case MOUSE_BUTTON_1: return "Mouse button 1";
            case MOUSE_BUTTON_2: return "Mouse button 2";
            case MOUSE_BUTTON_3: return "Mouse button 3";
            default: return "Unknown action";
        }
    }

    /**
     * Get position of mouse on screen.
     * 
     * @return position of mouse on screen.
     */
    public Point getMousePosition() {
        return this.mousePosition;
    }

    /**
     * Get mouse horizontal position.
     * 
     * @return horizontal position of mouse on screen.
     */
    public int getMouseX() {
        return this.mousePosition.x;
    }

    /**
     * Get mouse vertical position.
     * 
     * @return vertical position of mouse on screen.
     */
    public int getMouseY() {
        return this.mousePosition.y;
    }

    /**
     * Recenter mouse on screen.
     */
    protected synchronized void recenterMouse() {
        if ((this.robot != null)&&(this.screen.isShowing())) {
            this.screenCenter.x = this.screen.getWidth() / 2;
            this.screenCenter.y = this.screen.getHeight() / 2;
            SwingUtilities.convertPointToScreen(this.screenCenter, this.screen);
            this.isRecentering = true;
            this.robot.mouseMove(this.screenCenter.x, this.screenCenter.y);
        }
    }

    /**
     * Get action associated with event.
     *
     * @param keyboard event.
     * @return action associated with keycode.
     */
    private InputAction getKeyboardAction(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode < keyboardActions.length) {
            return keyboardActions[keyCode];
        } else {
            return null;
        }
    }

    /**
     * Get code associated with mouse event.
     *
     * @param mouse event.
     * @return mouse action code.
     */
    public static int getMouseButtonCode(MouseEvent e) {
        switch (e.getButton()) {
        case MouseEvent.BUTTON1:
            return MOUSE_BUTTON_1;
        case MouseEvent.BUTTON2:
            return MOUSE_BUTTON_2;
        case MouseEvent.BUTTON3:
            return MOUSE_BUTTON_3;
        default:
            return -1;
        }
    }

    /**
     * Get action associated with mouse event.
     *
     * @param mouse event
     * @return action associated with mouse
     */
    private InputAction getMouseAction(MouseEvent e) {
        int mouseCode = getMouseButtonCode(e);
        if (mouseCode != -1) {
            return mouseActions[mouseCode];
        } else {
            return null;
        }
    }

    /**
     * Catch, handle and consume key press. 
     *
     * @param key event
     */
    public void keyPressed(KeyEvent e) {
        InputAction action = this.getKeyboardAction(e);
        if (action != null) {
            action.press();
        }
        e.consume();
    }

    /**
     * Catch, handle and consume  key release.
     *
     * @param key event
     */
    public void keyReleased(KeyEvent e) {
        InputAction action = this.getKeyboardAction(e);
        if (action != null) {
            action.release();
        }
        e.consume();
    }

    /**
     * Ignores and consumes key type.
     *
     * @param key event
     */
    public void keyTyped(KeyEvent e) {
        e.consume();
    }

    /**
     * Catch, handle and consume mouse press. 
     *
     * @param mouse event
     */
    public void mousePressed(MouseEvent e) {
        InputAction action = this.getMouseAction(e);
        if (action != null) {
            action.press();
        }
        e.consume();
    }
    
    /**
     * Catch, handle and consume mouse release. 
     *
     * @param mouse event
     */
    public void mouseReleased(MouseEvent e) {
        InputAction action = this.getMouseAction(e);
        if (action != null) {
            action.release();
        }
        e.consume();
    }

    /**
     * Ignores and consumes mouse clicks.
     *
     * @param mouse event
     */
    public void mouseClicked(MouseEvent e) {        
        e.consume();
    }

    /**
     * Mouse enter is treated as mouse movement.
     *
     * @param mouse event
     */
    public void mouseEntered(MouseEvent e) {
        this.mouseMoved(e);
    }

    /**
     * Mouse exited is treated as mouse movement.
     *
     * @param mouse event
     */
    public void mouseExited(MouseEvent e) {
        this.mouseMoved(e);
    }
    
    /**
     * Mouse dragged is treated as mouse movement.
     *
     * @param mouse event
     */
    public void mouseDragged(MouseEvent e) {
        this.mouseMoved(e);
    }
    
    /**
     * Catch handle and consume mouse movement.
     *
     * @param mouse event
     */
    public synchronized void mouseMoved(MouseEvent e) {
        if ((this.isRecentering) &&
            (this.screenCenter.x == e.getX()) &&
            (this.screenCenter.y == e.getY())) {
            this.isRecentering = false;
        } else {
            int dx = e.getX();
            int dy = e.getY();
            this.mouseHelper(MOUSE_MOVE_LEFT, MOUSE_MOVE_RIGHT, dx);
            this.mouseHelper(MOUSE_MOVE_UP, MOUSE_MOVE_DOWN, dy);

            if (this.isRelativeMouseMode()) {
                this.recenterMouse();
            }
        }
        this.mousePosition.x = e.getX();
        this.mousePosition.y = e.getY();
        e.consume();
    }

    /**
     * Catch, handle and consume mouse wheel movement
     *
     * @param mouse wheel event.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseHelper(MOUSE_WHEEL_UP, MOUSE_WHEEL_DOWN, e.getWheelRotation());
        e.consume();
    }

    /**
     * Handle mouse and mouse wheel movement. Chooses between the two supplied
     * action codes based the amount supplied. If _amount is less than 0 
     * _codeNeg is used to find and handle action else _codePos is used.
     *
     * @param code of action to use if amount is negative.
     * @param code of action to use if amount is zero og positive.
     * @param amount.
     */
    public void mouseHelper(int _codeNeg, int _codePos, int _amount) {
        InputAction action;
        if (_amount < 0) {
            action = mouseActions[_codeNeg];
        } else {
            action = mouseActions[_codePos];
        }
        if (action != null) {
            action.press(Math.abs(_amount));
            action.release();
        }
    }
}
