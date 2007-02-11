/*
 * NodeTest.java
 *
 * Created on 11. februar 2007, 17:05
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 * Magnus Hemmer Pihl @ 11. februar 2007 (v 1.0)
 * Created
 *
 */

package field;

import junit.framework.*;
import java.awt.Point;
import java.io.Serializable;

public class NodeTest extends TestCase {
    
    private Node node1;
    private Node node2;
    private Node node3;
    private Node node4;
    
    public NodeTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        node1 = new Node(new Point(0,0), null, null, null, null, 1);
        node2 = new Node(new Point(0,1), null, null, node1, null, 2);
        node3 = new Node(new Point(1,1), node2, null, null, null, 3);
        node4 = new Node(new Point(2,1), node3, null, null, null, 4);
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of isStraightPath method, of class field.Node.
     */
    public void testIsStraightPath() {
        System.out.println("isStraightPath");
        
        assertEquals(node1.isStraightPath(), false);
        assertEquals(node2.isStraightPath(), false);
        assertEquals(node3.isStraightPath(), true);
    }

    /**
     * Test of getPosition method, of class field.Node.
     */
    public void testGetPosition() {
        System.out.println("getPosition");
        
        assertEquals(node1.getPosition(), new Point(0,0));
        assertEquals(node2.getPosition(), new Point(0,1));
        assertEquals(node3.getPosition(), new Point(1,1));
        assertEquals(node4.getPosition(), new Point(2,1));
    }

    /**
     * Test of setPosition method, of class field.Node.
     */
    public void testSetPosition() {
        System.out.println("setPosition");
        
        node4.setPosition(new Point(5,5));
        assertEquals(node4.getPosition(), new Point(5,5));
        node4.setPosition(new Point(2,1));
        assertEquals(node4.getPosition(), new Point(2,1));
    }

    /**
     * Test of setPoints method, of class field.Node.
     */
    public void testSetPoints() {
        System.out.println("setPoints");
        
        node1.setPoints(10);
        assertEquals(node1.getPoints(), 10);
    }

    /**
     * Test of getPoints method, of class field.Node.
     */
    public void testGetPoints() {
        System.out.println("getPoints");
        
        assertEquals(node4.getPoints(), 4);
    }

    /**
     * Test of takePoints method, of class field.Node.
     */
    public void testTakePoints() {
        System.out.println("takePoints");
        
        int points = node2.takePoints();
        assertEquals(node2.pointsTaken(), true);
        assertEquals(points, 2);
    }

    /**
     * Test of pointsTaken method, of class field.Node.
     */
    public void testPointsTaken() {
        System.out.println("pointsTaken");
        
        node3.takePoints();
        assertEquals(node3.pointsTaken(), true);
        assertEquals(node4.pointsTaken(), false);
    }

    /**
     * Test of setPointTaken method, of class field.Node.
     */
    public void testSetPointTaken() {
        System.out.println("setPointTaken");
        
        assertEquals(node1.pointsTaken(), false);
        node1.setPointTaken(true);
        assertEquals(node1.pointsTaken(), true);
        node1.setPointTaken(false);
        assertEquals(node1.pointsTaken(), false);
    }

    /**
     * Test of setNodeAt method, of class field.Node.
     */
    public void testSetNodeAt() {
        System.out.println("setNodeAt");
        
        Node node5 = new Node(new Point(2,0));
        node4.setNodeAt(node5, Node.UP);
        assertEquals(node5.getDownNode(), node4);
    }

    /**
     * Test of getConnectedNodes method, of class field.Node.
     */
    public void testGetConnectedNodes() {
        System.out.println("getConnectedNodes");
        
        Node[] result = node2.getConnectedNodes();
        assertEquals(result[Node.UP], node1);
        assertEquals(result[Node.RIGHT], node3);
    }

    /**
     * Test of getNodeAt method, of class field.Node.
     */
    public void testGetNodeAt() {
        System.out.println("getNodeAt");
        
        assertEquals(node1.getNodeAt(Node.DOWN), node2);
        assertEquals(node2.getNodeAt(Node.RIGHT), node3);
    }

    /**
     * Test of removeAllConnections method, of class field.Node.
     */
    public void testRemoveAllConnections() {
        System.out.println("removeAllConnections");
        
        node1.removeAllConnections();
        assertEquals(node1.getDownNode(), null);
        assertEquals(node2.getUpNode(), null);
    }

    /**
     * Test of getOpposite method, of class field.Node.
     */
    public void testGetOpposite() {
        System.out.println("getOpposite");
        
        assertEquals(Node.getOpposite(Node.UP), Node.DOWN);
        assertEquals(Node.getOpposite(Node.LEFT), Node.RIGHT);
        assertEquals(Node.getOpposite(Node.DOWN), Node.UP);
        assertEquals(Node.getOpposite(Node.RIGHT), Node.LEFT);
    }

    /**
     * Test of isValidDirection method, of class field.Node.
     */
    public void testIsValidDirection() {
        System.out.println("isValidDirection");
        
        assertEquals(Node.isValidDirection(-1), false);
        assertEquals(Node.isValidDirection(0), true);
        assertEquals(Node.isValidDirection(1), true);
        assertEquals(Node.isValidDirection(2), true);
        assertEquals(Node.isValidDirection(3), true);
        assertEquals(Node.isValidDirection(4), false);
    }
    
}
