package game.input;

/**
 * InputAction.java
 *
 * Created 2006 by LMK
 * Based on examples in "Devoloping Games in Java" [Brackeen]
 * by Brackeen, David
 * www.brackeen.com
 * Listing 3.5 GameAction.java, page 114-117
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 26. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */
public class InputAction {
    public static final byte DETECT_ALL_ACTIONS = 0x00;
    public static final byte DETECT_FIRST_ACTION = 0x01;

    public static final byte RELEASED = 0x00;
    public static final byte PRESSED = 0x01;
    public static final byte WAITING = 0x02;

    protected String name;
    protected byte behaviour;
    protected byte state;
    protected byte amount;

    /**
     * Create new InputAction that DETECT_ALL_ACTIONS with specified name.
     *
     * @param name of action.
     */
    public InputAction(String _name) {
        this(_name, DETECT_ALL_ACTIONS);
    }

    /**
     * Create new InputAction with specified behaviour and name.
     *
     * @param name of action.
     * @param behaviour. Either DETECT_ALL_ACTIONS or DETECT_FIRST_ACTION.
     */
    public InputAction(String _name, byte _behaviour) {
        this.name = _name;
        this.behaviour = _behaviour;
        this.reset();
    }

    /**
     * Reset
     */
    public synchronized void reset() {
        this.state = RELEASED;
        this.amount = 0;
    }

    /**
     * Simulate a single press and release
     */
    public synchronized void tap() {
        this.press();
        this.release();
    }

    /**
     * Press with amount 1
     */
    public synchronized void press() {
        this.press(1);
    }

    /**
     * Signal that a key was pressed a specified amount of times or that the
     * mouse was moved a specified distance. 
     * 
     * @param amount of presses.
     */
    public synchronized void press(int _amount) {
        if (state != WAITING) {
            this.amount += _amount;
            this.state = PRESSED;
        }
    }

    /**
     * Set state to released
     */
    public synchronized void release() {
        this.state = RELEASED;
    }

    /**
     * Check whether associated key is pressed.
     *
     * @return true if amount of presses isn't 0.
     */
    public synchronized boolean isPressed() {
        return (this.getAmount() != 0);
    }

    /**
     * Get name of input action
     *
     * @return name of action
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Set name of action
     *
     * @param name
     */
    public void setName(String _name) {
        this.name = _name;
    }

    /**
     * Get behaviour
     *
     * @return either DETECT_ALL_ACTIONS or DETECT_FIRST_ACTION.
     */
    public byte getBehaviour() {
        return this.behaviour;
    }

    /**
     * Get current state.
     *
     * @return either RELEASED, PRESSED or WAITING.
     */
    public byte getState() {
        return this.state;
    }

    /**
     * Get amount of presses.
     *
     * @return number of presses.  
     */
    public synchronized int getAmount() {
        int output = this.amount;
        if (output != 0) {
            if (this.state == RELEASED) {
                this.amount = 0;
            } else if (this.behaviour == DETECT_FIRST_ACTION) {
                this.state = WAITING;
                this.amount = 0;
            }
        }
        return output;
    }

}
