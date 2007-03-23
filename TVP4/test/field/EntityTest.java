/*
 * EntityTest.java
 * JUnit based test
 *
 * Created on 23. februar 2007, 09:05
 * @version 1.1
 *
 * The constraints of the speed and ID attributes need to be more precisely 
 * defined to make more specific tests. May they be negative fx.?
 * Similarly the some constraints on whether position in Node may be negative
 * should be considered.
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 23. marts 2007 (v 1.1)
 * Inserted testGetSetNode instead.
 * Inserted randomized tests.
 * Entity is now constructed during each test to ensure that each tests 
 * starts from scratch.
 */

package field;

import junit.framework.*;
import java.awt.Point;
import java.io.*;
import java.util.*;

/**
 *
 * @author Mikkel Nielsen
 */
public class EntityTest extends TestCase {
    private Entity ent;
    private Random rand;
    
    public static final int NUMBER_OF_TESTS = 1000;
    
    public EntityTest(String testName) {
        super(testName);
        rand = new Random();
    }

    protected void setUp() throws Exception {
        /*Point p = new Point(2,3);
        ent = new Entity(p,5);*/
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of get and setIsMoving method, of class field.Entity.
     */
    public void testGetSetIsMoving() {
        ent = new Entity(null, 0);        
        boolean expected;
        
        // isMoving should be false as default.
        assertFalse(ent.isMoving());
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = rand.nextBoolean();
            ent.setIsMoving(expected);
            
            //Testing if the set method worked
            assertEquals(expected, ent.isMoving());
        }
    }

    /**
     * Test of get and setDirection method, of class field.Entity.
     */
    public void testGetSetDirection() {
        ent = new Entity(null, 0);        
        int expected;
        int lastDirection = Node.UP;
       
        // setDirection should be north as default.
        assertEquals(Node.UP, ent.getDirection());
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = rand.nextInt();
            ent.setDirection(expected);
            
            if (Node.isValidDirection(expected)) {
                lastDirection = expected;
            }
            
            //Testing if the set method worked
            assertEquals(lastDirection, ent.getDirection());            
        }                        
    }
    
    /**
     * Test of get and setNode method, of class field.Entity.
     */
    public void testGetSetNode() {
        //some random elements are needed in these tests
        Node expected, lastNode;
        Entity ent1 = new Entity(null, 0);
        Entity ent = new Entity(null, 0);
                        
        assertNull(ent.getNode());
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = new Node(null);            
            ent = new Entity(expected, 0);
            
            assertEquals(expected, ent.getNode());
            assertEquals(expected.getEntity(), ent);
        }
        
        //check whether it is possible to set node on a node
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = new Node(null);            
            ent.setNode(expected);
            
            assertEquals(expected, ent.getNode());
            ent.setNode(null);
            assertNull(ent.getNode());
        }
              
        // check whether node references are removed from the previous node
        // and set correctly to the new node
        expected = new Node(null);
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            lastNode = expected;
            expected = new Node(null);
            ent.setNode(expected);
            
            assertNull(lastNode.getEntity());            
            assertEquals(expected, ent.getNode());
            assertEquals(ent, expected.getEntity());            
        }       
                
        //check that previously placed entities have their node references
        //removed
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {            
            expected = new Node(null);
            ent.setNode(expected);
            assertEquals(expected, ent.getNode());            
            assertEquals(ent, expected.getEntity());
            
            ent1.setNode(expected);            
            
            assertFalse(expected.equals(ent.getNode()));            
            assertFalse(ent.equals(expected.getEntity()));            
            assertEquals(expected, ent1.getNode());            
            assertEquals(ent1, expected.getEntity());                      
        }       
    }

    /**
     * Test of get and setPosition method, of class field.Entity.
     */
    public void testGetPosition() {
        Point expected = new Point(0,0);
        Node node = new Node(expected);
        ent = new Entity(node, 0);
                
        assertEquals(expected, ent.getPosition());
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = new Point(rand.nextInt(), rand.nextInt());
            node = new Node(expected);
            ent = new Entity(node, 0);
            
            assertEquals(expected, ent.getPosition());
        }
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = new Point(rand.nextInt(), rand.nextInt());
            node.setPosition(expected);
            
            assertEquals(expected, ent.getPosition());
        }                
    }

    /**
     * Test of get and setSpeed method, of class field.Entity.     
     */
    public void testGetSetSpeed() {        
        int expected;
               
        ent = new Entity(null, 0);
        // speed should be 0 as default
        assertEquals(0, ent.getSpeed());
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = rand.nextInt();
            ent.setSpeed(expected);
                        
            //Testing if the set method worked
            assertEquals(expected, ent.getSpeed());            
        } 
    }

    /**
     * Test of get and setID method, of class field.Entity.
     * Also test the constructor;
     */
    public void testGetSetID() {
        int expected;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = rand.nextInt();
            ent = new Entity(null, expected);
                        
            //Testing if the constructor worked
            assertEquals(expected, ent.getID());            
        }
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = rand.nextInt();
            ent.setID(expected);
                        
            //Testing if the set method worked
            assertEquals(expected, ent.getID());            
        }
    }
    
    public void testGetSetController() {
        //add code to test controller
        //assertTrue(false);                
    }
}
