/*
 * FieldTest.java
 *
 * Created on 9. februar 2007, 10:05
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 * Magnus Hemmer Pihl @ 11. februar 2007 (v 1.1)
 * Updated to match Field class version 1.3
 *
 * Magnus Hemmer Pihl @ 9. februar 2007 (v 1.0)
 * Created
 *
 */

package field;

import java.awt.Dimension;
import junit.framework.*;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Serializable;

public class FieldTest extends TestCase {
    
    private Field instance;
    
    public FieldTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        this.instance = new Field();
        this.instance.addNodeAt(10,10);
        this.instance.addNodeAt(11,10);
        this.instance.addNodeAt(12,10);
        this.instance.addNodeAt(12,11);
        this.instance.addNodeAt(12,12);
        this.instance.addNodeAt(11,12);
        this.instance.addNodeAt(10,12);
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of addNodeAt method, of class field.Field.
     */
    public void testAddNodeAt() {
        System.out.println("addNodeAt");
        
        this.instance.addNodeAt(10,11,2);
        Node node = this.instance.getNodeAt(10,11);
        
        assertEquals(node.getUpNode(), this.instance.getNodeAt(10,10));
        assertEquals(node.getPoints(), 2);
    }

    /**
     * Test of removeNodeAt method, of class field.Field.
     */
    public void testRemoveNodeAt() {
        System.out.println("removeNodeAt");
        
        this.instance.removeNodeAt(11,10);
        Node node1 = this.instance.getNodeAt(10,10);
        Node node2 = this.instance.getNodeAt(12,10);
        
        assertEquals(node1.getRightNode(), null);
        assertEquals(node2.getLeftNode(), null);
        assertEquals(this.instance.getNodeAt(11,10), null);
    }

    /**
     * Test of getNodeAt method, of class field.Field.
     */
    public void testGetNodeAt() {
        System.out.println("getNodeAt");
        
        assertEquals(this.instance.getNodeAt(11,12).getRightNode(), this.instance.getNodeAt(12,12));
    }
    
    /**
     * Test of getSize method, of class field.Field.
     */
    public void testGetSize() {
        System.out.println("getSize");
        
        assertEquals(this.instance.getSize(), new Dimension(3,3));
    }
}