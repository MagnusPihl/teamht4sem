/*
 * FieldTest.java
 *
 * Created on 9. februar 2007, 10:05
 *
 * Company: HT++
 *
 * @author Magnus Hemmer Pihl
 * @version 1.3
 *
 *
 * ******VERSION HISTORY******
 * Magnus Hemmer Pihl @ 23. februar 2007 (v 1.3)
 * Added tests for placePacman, placeGhost, removeEntityAt, invertNodeAt, clearField, saveTo, loadFrom.
 *
 * Magnus Hemmer Pihl @ 11. februar 2007 (v 1.2)
 * Corrected expected return value from getSize().
 *
 * Magnus Hemmer Pihl @ 11. februar 2007 (v 1.1)
 * Updated to match Field class version 1.3
 *
 * Magnus Hemmer Pihl @ 9. februar 2007 (v 1.0)
 * Created
 *
 */

package field;

import java.awt.Dimension;
import java.io.File;
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
        this.instance.addNodeAt(0,0);
        this.instance.addNodeAt(1,0);
        this.instance.addNodeAt(2,0);
        this.instance.addNodeAt(2,1);
        this.instance.addNodeAt(2,2);
        this.instance.addNodeAt(1,2);
        this.instance.addNodeAt(0,2);
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of addNodeAt method, of class field.Field.
     */
    public void testAddNodeAt() {
        System.out.println("addNodeAt");
        
        this.instance.addNodeAt(0,1,2);
        Node node = this.instance.getNodeAt(0,1);
        
        assertEquals(node.getUpNode(), this.instance.getNodeAt(0,0));
        assertEquals(node.getPoints(), 2);
    }

    /**
     * Test of removeNodeAt method, of class field.Field.
     */
    public void testRemoveNodeAt() {
        System.out.println("removeNodeAt");
        
        this.instance.removeNodeAt(1,0);
        Node node1 = this.instance.getNodeAt(0,0);
        Node node2 = this.instance.getNodeAt(2,0);
        
        assertEquals(node1.getRightNode(), null);
        assertEquals(node2.getLeftNode(), null);
        assertEquals(this.instance.getNodeAt(1,0), null);
    }

    /**
     * Test of getNodeAt method, of class field.Field.
     */
    public void testGetNodeAt() {
        System.out.println("getNodeAt");
        
        assertEquals(this.instance.getNodeAt(1,2).getRightNode(), this.instance.getNodeAt(2,2));
    }
    
    /**
     * Test of getSize method, of class field.Field.
     */
    public void testGetSize() {
        System.out.println("getSize");
        
        assertEquals(this.instance.getSize(), new Dimension(3,3));
    }
    
    public void testPlacePacman()
    {
        System.out.println("placePacman");
        
        Point p = new Point(2,2);
        assertEquals(this.instance.placePacman(p), true);
        assertEquals(this.instance.getEntityAt(p).getID(), 0);
        
        p = new Point(-2,-2);
        assertEquals(this.instance.placePacman(p), false);
        
        p = new Point(10001,10000);
        assertEquals(this.instance.placePacman(p), false);
    }
    
    public void testPlaceGhost()
    {
        System.out.println("placeGhost");
        
        Point p = new Point(2,1);
        assertEquals(this.instance.placeGhost(p), true);
        assertTrue(this.instance.getEntityAt(p).getID() == 1 || this.instance.getEntityAt(p).getID() == 2);
        
        p = new Point(-2,-3);
        assertEquals(this.instance.placeGhost(p), false);
        
        p = new Point(10000,10000);
        assertEquals(this.instance.placeGhost(p), false);
    }
    
    public void testRemoveEntityAt()
    {
        System.out.println("removeEntityAt");
        
        Point p = new Point(0,0);
        assertEquals(this.instance.placeGhost(p), true);
        assertNotNull(this.instance.getEntityAt(p));
        this.instance.removeEntityAt(p);
        assertNull(this.instance.getEntityAt(p));
    }
    
    public void testInvertNodeAt()
    {
        System.out.println("invertNodeAt");
        
        Point p = new Point(5,5);
        assertNull(this.instance.getNodeAt(p));
        this.instance.invertNodeAt(p,1);
        assertNotNull(this.instance.getNodeAt(p));
        this.instance.invertNodeAt(p,1);
        assertNull(this.instance.getNodeAt(p));
    }
    
    public void testClearField()
    {
        System.out.println("clearField");
        
        this.instance.clearField();
        assertNull(this.instance.getNodeAt(0,0));
        assertEquals(this.instance.getSize(), new Dimension(0,0));
        assertNull(this.instance.getEntityAt(new Point(2,1)));
        assertNull(this.instance.getEntityAt(new Point(2,2)));
    }
    
    public void testSaveLoad()
    {
        System.out.println("saveTo and loadFrom");
        
        Field f1 = new Field();
        f1.addNodeAt(0,0);
        f1.addNodeAt(0,1);
        f1.addNodeAt(0,2);
        f1.addNodeAt(1,0);
        f1.addNodeAt(2,0);
        f1.addNodeAt(2,1);
        f1.addNodeAt(2,2);
        f1.addNodeAt(1,2);
        f1.placePacman(new Point(2,0));
        f1.placeGhost(new Point(0,0));
        f1.placeGhost(new Point(2,2));
        
        assertNotNull(f1.getNodeAt(0,0));
        assertNotNull(f1.getNodeAt(0,1));
        assertNotNull(f1.getNodeAt(0,2));
        assertNotNull(f1.getNodeAt(1,0));
        assertNotNull(f1.getNodeAt(2,0));
        assertNotNull(f1.getNodeAt(2,1));
        assertNotNull(f1.getNodeAt(2,2));
        assertNotNull(f1.getNodeAt(1,2));
        assertNotNull(f1.getEntityAt(new Point(2,0)));
        assertNotNull(f1.getEntityAt(new Point(0,0)));
        assertNotNull(f1.getEntityAt(new Point(2,2)));
        
        f1.saveTo(new File("testSave.map"));
        
        Field f2 = new Field();
        f2.loadFrom(new File("testSave.map"));
        
        assertNotNull(f2.getNodeAt(0,0));
        assertNotNull(f2.getNodeAt(0,1));
        assertNotNull(f2.getNodeAt(0,2));
        assertNotNull(f2.getNodeAt(1,0));
        assertNotNull(f2.getNodeAt(2,0));
        assertNotNull(f2.getNodeAt(2,1));
        assertNotNull(f2.getNodeAt(2,2));
        assertNotNull(f2.getNodeAt(1,2));
        assertNotNull(f2.getEntityAt(new Point(2,0)));
        assertNotNull(f2.getEntityAt(new Point(0,0)));
        assertNotNull(f2.getEntityAt(new Point(2,2)));
    }
}