/*
 * EntityTest.java
 * JUnit based test
 *
 * Created on 23. februar 2007, 09:05
 */

package field;

import junit.framework.*;
import java.awt.Point;
import java.io.*;

/**
 *
 * @author Mikkel Nielsen
 */
public class EntityTest extends TestCase {
    private Entity ent;
    
    public EntityTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        Point p = new Point(2,3);
        ent = new Entity(p,5);
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of get and setIsMoving method, of class field.Entity.
     */
    public void testGetSetIsMoving() {
        // isMoving should be false as default.
        assertFalse(ent.isMoving());
        
        ent.setIsMoving(true);
        //Testing if the set method worked
        assertTrue(ent.isMoving());
    }

    /**
     * Test of get and setDirection method, of class field.Entity.
     */
    public void testSetDirection() {
        // setDirection should be north as default.
        assertEquals(Node.UP, ent.getDirection());
        
        ent.setDirection(Node.LEFT);
        
        //Testing if the set method worked by 
        assertEquals(Node.LEFT, ent.getDirection());
    }

    /**
     * Test of get and setPosition method, of class field.Entity.
     */
    public void testSetPosition() {
        // position was set to 2,3 in constructor
        assertEquals(new Point(2,3), ent.getPosition());
        
        ent.setPosition(new Point(8,1));
        
        //Testing if both x and y is correct after change 
        assertEquals(8, ent.getPosition().x);
        assertEquals(1, ent.getPosition().y);
    }

    /**
     * Test of get and setSpeed method, of class field.Entity.
     */
    public void testSetSpeed() {
        // speed should be 0 as default
        assertEquals(0, ent.getSpeed());
        
        ent.setSpeed(199);
        
        //Testing if the speed change is correct 
        assertEquals(199, ent.getSpeed());
    }

    /**
     * Test of get and setID method, of class field.Entity.
     */
    public void testSetID() {
        // ID was set to 5 in constructor
        assertEquals(5, ent.getID());
        
        ent.setID(8);
        
        //Testing if the ID change is correct 
        assertEquals(8, ent.getID());
    }
}
