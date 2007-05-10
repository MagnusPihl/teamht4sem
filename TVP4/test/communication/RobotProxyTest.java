/*
 * RobotProxyTest.java
 *
 * Created on 9. maj 2007, 17:14
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 9. maj 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package communication;

import field.*;
import java.util.*;
import junit.framework.*;

/**
 *
 * @author LMK
 */
public class RobotProxyTest extends TestCase{    
    
    private Random rand;
    public static final int NUMBER_OF_TESTS = 1000;
    
    public RobotProxyTest(String testName) {
        super(testName);
        rand = new Random();
    }

    protected void setUp() throws Exception {        
    }

    protected void tearDown() throws Exception {
    }     
    
    /**
     * Check that the correct receiver id is retreive from an adressHeader
     * when method getReceiver is run
     *
     * @param addressHeader
     * @result int with 4 least significant bits containing receiver ID.
     */
    public void testRotatePossibleDirections() {
        byte possibleDirections;
        byte turn;
        byte output;        
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            turn = (byte)this.rand.nextInt(Node.DIRECTION_COUNT);
            possibleDirections = (byte)(this.rand.nextInt() & 0x07);
            output = RobotProxy.rotatePossibleDirections(turn, possibleDirections);
            
            assertEquals(output & ~0x07, 0);
            switch (turn) {
                case Node.UP :                
                    //System.out.println("UP");
                    if ((possibleDirections & GameCommands.UP) == GameCommands.UP) {
                        assertEquals(output & GameCommands.FORWARD, GameCommands.FORWARD);
                    }                    
                    if ((possibleDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                        assertEquals(output & GameCommands.TURN_RIGHT, GameCommands.TURN_RIGHT);
                    }
                    if ((possibleDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                        assertEquals(output & GameCommands.TURN_LEFT, GameCommands.TURN_LEFT);
                    }
                    break;
                    
                case Node.LEFT:
                    //System.out.println("LEFT");
                    if ((possibleDirections & GameCommands.UP) == GameCommands.UP) {
                        assertEquals(output & GameCommands.TURN_RIGHT, GameCommands.TURN_RIGHT);
                    }                    
                    if ((possibleDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                        assertEquals(output & GameCommands.TURN_LEFT, GameCommands.TURN_LEFT);
                    }
                    if ((possibleDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                        assertEquals(output & GameCommands.FORWARD, GameCommands.FORWARD);
                    }
                    break;
                    
                case Node.RIGHT:
                    //System.out.println("RIGHT");
                    if ((possibleDirections & GameCommands.UP) == GameCommands.UP) {
                        assertEquals(output & GameCommands.TURN_LEFT, GameCommands.TURN_LEFT);
                    }                    
                    if ((possibleDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                        assertEquals(output & GameCommands.TURN_RIGHT, GameCommands.TURN_RIGHT);
                    }
                    if ((possibleDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                        assertEquals(output & GameCommands.FORWARD, GameCommands.FORWARD);
                    }
                    break;
                    
                case Node.DOWN:
                    //System.out.println("DOWN");
                    if ((possibleDirections & GameCommands.DOWN) == GameCommands.DOWN) {
                        assertEquals(output & GameCommands.FORWARD, GameCommands.FORWARD);
                    }                    
                    if ((possibleDirections & GameCommands.LEFT) == GameCommands.LEFT) {
                        assertEquals(output & GameCommands.TURN_RIGHT, GameCommands.TURN_RIGHT);
                    }
                    if ((possibleDirections & GameCommands.RIGHT) == GameCommands.RIGHT) {
                        assertEquals(output & GameCommands.TURN_LEFT, GameCommands.TURN_LEFT);
                    }
                    break;            
            }
        }                
    }    
}
