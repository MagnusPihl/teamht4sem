/*
 * NodeTest.java
 *
 * Created on 11. februar 2007, 17:05
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.2
 *
 * It needs to be considered whether node needs to check 
 * whether position is null.
 * Also should points be allowed to be none positive.
 *
 * ******VERSION HISTORY******
 * LMK @ 23. marts 2007 (v 1.2)
 * Refactored tests
 * Randomized tests.
 * Entity is now constructed during each test to ensure that each tests
 * starts from scratch.
 *
 * Magnus Hemmer Pihl @ 11. februar 2007 (v 1.1)
 * Updated to no longer use Point coordinates.
 *
 * Magnus Hemmer Pihl @ 11. februar 2007 (v 1.0)
 * Created
 *
 */

package field;

import field.Node;
import junit.framework.*;
import java.awt.Point;
import java.util.*;

public class NodeTest extends TestCase {
    
    private Node node;
    private Node node2;
    private Node node3;
    private Node node4;
    
    private Random rand;
    public static final int NUMBER_OF_TESTS = 1000;
    
    public NodeTest(String testName) {
        super(testName);
        rand = new Random();
    }

    protected void setUp() throws Exception {
        /*node1 = new Node(null, null, null, null, 1);
        node2 = new Node(null, null, node1, null, 2);
        node3 = new Node(node2, null, null, null, 3);
        node4 = new Node(node3, null, null, null, 4);*/
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of isStraightPath method, of class field.Node.
     */
    public void testIsStraightPath() {
        node = new Node(null);
        Node[] nodes = new Node[4];
        boolean up, down, left, right;
        
        for (int i = 0; i < 4; i++) {
            nodes[i] = new Node(null);
        }
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            up = rand.nextBoolean();
            down = rand.nextBoolean();
            left = rand.nextBoolean();
            right = rand.nextBoolean();
            
            node.setNodeAt((up) ? nodes[0] : null, Node.UP);
            node.setNodeAt((right) ? nodes[1] : null, Node.RIGHT);
            node.setNodeAt((down) ? nodes[2] : null, Node.DOWN);
            node.setNodeAt((left) ? nodes[3] : null, Node.LEFT);
                        
            if ((up && down && !left && !right)||(!up && !down && left && right)) {
                assertTrue(node.isStraightPath());
            } else {
                assertFalse(node.isStraightPath());
            }           
        }        
    }

    /**
     * Test of get, and setPoints methods, of class field.Node.
     */
    public void testPoints() {
        node = new Node(null);
        int expected = 0;
        
        //check that the node is initialized to 0;
        assertEquals(expected, node.getPoints());
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = rand.nextInt();
            
            node.setPoints(expected);
            assertEquals(expected, node.getPoints());
        }
    }
    
    /**
     * Test of takePoints,isPointsTaken and setPointsTaken methods, of class field.Node.
     */
    public void testTakePoints() {
        node = new Node(null);
        int expected;
        boolean taken;
        
        //check that the node is initialized to 0;
        assertEquals(0, node.getPoints());
        assertFalse(node.pointsTaken());
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            taken = rand.nextBoolean();            
            expected = rand.nextInt();            
            
            node.setPointsTaken(taken);        
            node.setPoints(expected);
            assertEquals(taken, node.pointsTaken());            
            assertEquals(expected, node.getPoints());
            
            if (taken) {
                assertEquals(0, node.takePoints());
            } else {
                assertEquals(expected, node.takePoints());
            }
        }
    }

    /**
     * Test of get and setNodeAt method, of class field.Node.
     */
    public void testGetSetRemoveNodeAt() {
        node = new Node(null);        
        int dir;
        Node expected;
        Node lastNode;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {            
            dir = rand.nextInt();                
            expected = new Node(null);

            node.setNodeAt(expected, dir);                

            if (Node.isValidDirection(dir)) {
                assertEquals(expected, node.getNodeAt(dir));                    
            } else {
                assertNull(node.getNodeAt(dir));
            }

            node.setNodeAt(null, dir);                
            assertNull(node.getNodeAt(dir));                                            
        }  
    }

    /**
     * Test of getConnectedNodes method, of class field.Node.
     */
    public void testGetConnectedNodes() {
        System.out.println("getConnectedNodes");
        
        /*Node[] result = node2.getConnectedNodes();
        assertEquals(result[Node.UP], node1);
        assertEquals(result[Node.RIGHT], node3);*/
    }
    
    /**
     * Test of removeAllConnections method, of class field.Node.
     */
    public void testRemoveAllConnections() {
        node = new Node(null);        
        boolean dir;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            for (int j = 0; j < Node.DIRECTION_COUNT; j++) {
                dir = rand.nextBoolean();                
                node.setNodeAt((dir) ? new Node(null) : null, j);
                
                if (dir) {
                    assertNotNull(node.getNodeAt(j));
                } else {
                    assertNull(node.getNodeAt(j));
                }
            }
                                              
            node.removeAllConnections();        
            
            for (int j = 0; j < Node.DIRECTION_COUNT; j++) {
                assertNull(node.getNodeAt(j));
            }
        }  
    }

    /**
     * Test of getOpposite method, of class field.Node.
     */
    public void testGetOpposite() {
        node = new Node(null);
        int expected;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = rand.nextInt();
                        
            if (Node.isValidDirection(expected)) {
                switch (expected) {
                    case Node.UP : assertEquals(Node.DOWN, Node.getOpposite(expected)); break;
                    case Node.DOWN : assertEquals(Node.UP, Node.getOpposite(expected)); break;
                    case Node.LEFT : assertEquals(Node.RIGHT, Node.getOpposite(expected)); break;
                    case Node.RIGHT : assertEquals(Node.LEFT, Node.getOpposite(expected)); break;
                    default : assertTrue(false);
                }                
            } else {
                assertEquals(Node.INVALID_DIRECTION, Node.getOpposite(expected));
            }            
        }                  
    }

    /**
     * Test of isValidDirection method, of class field.Node.
     */
    public void testIsValidDirection() {
        node = new Node(null);
        int expected;
        
        for (int i = 0; i < NUMBER_OF_TESTS; i++) {
            expected = rand.nextInt();
                        
            if ((0 <= expected) && (expected < Node.DIRECTION_COUNT)) {
                assertTrue(node.isValidDirection(expected));                
            } else {
                assertFalse(node.isValidDirection(expected));
            }
        }  
    }    
}