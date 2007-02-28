/*
 * TimedInputAction.java
 *
 * Created on 28. februar 2007, 09:57
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.1
 * 
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 28. februar 2007 (v 1.1)
 * Added get and set methods for holdTime and repeatTime.
 * Don't use these while action is registered as they aren't synchronized.
 */

package game.input;

/**
 *
 * @author LMK
 */
public class TimedInputAction extends InputAction{
    
    private long pressTime;
    private long holdTime;
    private long repeatTime;
    
    public static final int REPEATING = 0x03;
    
    /** 
     * Creates a new instance of TimedInputAction with a time to hold before
     * registering consequtive actions, and a repeat time betrween actions.
     *
     * @param name
     * @param time in milliseconds to hold before repeating.
     * @param time in milleseconds between repeats.
     */
    public TimedInputAction(String name, long holdTime, long repeatTime) {
        super(name, DETECT_ALL_ACTIONS);
        this.holdTime = holdTime;
        this.repeatTime = repeatTime;
    }
    
    
   
    /**
     * Signal that a key was pressed a specified amount of times or that the
     * mouse was moved a specified distance. The amount will only be registered
     * if the state is RELEASED. When PRESSED the amount will only be registered
     * if time equal to holdTime have passed. When REPEATING repeatTime must have
     * passed before amount is registered.
     * 
     * @param amount of presses.
     */
    public synchronized void press(int amount) {
        long currentTime = System.currentTimeMillis();
        if (state == RELEASED) {
            this.pressTime = currentTime;
            this.amount += amount;
            this.state = PRESSED;
        } else if (state == PRESSED) {
            if (this.pressTime + this.holdTime <= currentTime) {
                this.pressTime = currentTime;
                this.amount += amount;
                this.state = REPEATING;
            }
        } else if (state == REPEATING) {
            if (this.pressTime + this.repeatTime <= currentTime) {
                this.pressTime = currentTime;
                this.amount += amount;
                this.state = REPEATING;
            }
        }        
    }

    /**
     * Set state to released
     */
    public synchronized void release() {
        this.state = RELEASED;
    }

    /**
     * Check whether associated key or mousebutton is pressed.
     * Once read the amount will be set to 0.
     *
     * @return true if amount of presses isn't 0.
     */
    public synchronized boolean isPressed() {        
        return (this.getAmount() != 0);
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
     * Get amount of presses. Once read the amount will be set to 0.
     *
     * @return number of presses.  
     */
    public synchronized int getAmount() {        
        int output = this.amount;
        this.amount = 0;        
        return output;
    }
    
    /**
     * Set amount between first press and first repeat.
     *
     * @param time in milliseconds
     */
    public void setHoldTime(long time) {
        this.holdTime = time;
    }
    
    /**
     * Set amount between repeats.
     *
     * @param time in milliseconds
     */
    public void setRepeatTime(long time) {
        this.repeatTime = time;
    }
    
    /**
     * Get milliseconds between first press and repeats.
     */
    public long getHoldTime() {
        return this.holdTime;
    }
    
    /**
     * Get milliseconds between repeats.
     */
    public long getRepeatTime() {
        return this.repeatTime;
    }
}
