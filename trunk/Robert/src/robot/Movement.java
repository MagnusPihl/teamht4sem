package robot;
import josx.platform.rcx.Motor;
/*
 * Movement.java
 *
 * Created on 19. marts 2007, 12:26
 *
 */

/**
 *
 * @author Christian Holm, 5601
 */

public class Movement {

    public static void LightOn(){
        Motor.B.forward();
    }
    public static void LightOff(){
        Motor.B.stop();
    }
    
    /**
     * Robot drives forward
     *
     * @author Christian Holm
     */
    public static void forward() {
        Motor.A.forward();
        Motor.C.backward();
    }
    
    /**
     * Robot drives in revese direction
     *
     * @author Christian Holm
     */
    public static void backward() {
        Motor.A.backward();
        Motor.C.forward();
    }
    
    /**
     * Start turning left using one motor
     *
     * @author Christian Holm
     */    
    public static void left() {
        Motor.A.flt();
        Motor.C.backward();
    }
    
    /**
     * Start turning right using one motor
     *
     * @author Christian Holm
     */
    public static void right() {
        Motor.A.forward();
        Motor.C.flt();
    }
    
    /**
     * Start turning left using one motor
     *
     * @author Christian Holm
     */    
    public static void backwardLeft() {
        Motor.A.backward();
        Motor.C.flt();
    }
    
    /**
     * Start turning right using one motor
     *
     * @author Christian Holm
     */
    public static void backwardRight() {
        Motor.A.flt();
        Motor.C.forward();
    }    
    
    /**
     * Start turning left using one motor
     *
     * @author Christian Holm
     */    
/*    public static void left2() {
        Motor.A.backward();
        Motor.C.flt();
    }*/
    
    /**
     * Start turning right using one motor
     *
     * @author Christian Holm
     */
/*    public static void right2() {
        Motor.A.flt();
        Motor.C.forward();
    }*/
    
    /**
     * Puts the motor in coast mode, meaning that it's possible to rotate the motor freely
     *
     * @author Christian Holm
     */
    public static void coast() {
        Motor.A.flt();
        Motor.C.flt();
    }
    
    /**
     * Stop all movement of the motors.
     * Put the motors in beake mode
     *
     * @author Christian Holm
     */    
    public static void stop() {
        Motor.A.stop();
        Motor.C.stop();
    }
    
    /**
     * Start turning right on the spot
     *
     * @author Christian Holm
     */
    public static void sharpRight() {
        Motor.A.forward();
        Motor.C.forward();
    }
    
    /**
     * Start turning left on the spot
     *
     * @author Christian Holm
     */
    public static void sharpLeft() {
        Motor.A.backward();
        Motor.C.backward();
    }
}