/*
 * Field.java
 *
 * Created on 9. februar 2007, 09:48
 *
 * Company: HT++
 *
 * @author Lau Maack-Krommes
 * @version 1.18
 *
 * ******VERSION HISTORY******
 * LMK @ 11. maj 2007 (v 1.18)
 * Added repaintNode
 * LMK @ 23. april 2007 (v 1.17)
 * Fixed so that hasChanged is set consistently.
 * Changed loading and saving HighScores to reflect changes in HighScore class.
 * LMK @ 11. april 2007 (v 1.16)
 * AddNodeAt methods now return Node when a new Node has been placed.
 * Magnus Hemmer Pihl @ 10. april 2007 (v 1.15.1)
 * Corrected missing end-bracket to close the class.
 * LMK @ 27. marts 2007 (v 1.15)
 * Altered load and save functions so that they no longer serializes
 * This should make them version independent.
 * Added method offsetNodes.
 * Magnus Hemmer Pihl @ 8. marts 2007 (v 1.14)
 * Removed dimension from drawField()-method.
 * Forced field to re-render every time a new field is loaded.
 * LMK @ 06. marts 2007 (v 1.13)
 * Added method getPointsLeft
 * Mikkel Nielsen @ 06. marts 2007 (v 1.12)
 * Added HighScoreList()
 * LMK @ 05. marts 2007 (v 1.11)
 * Added call to drawPoints to drawField method
 * Speeded up drawing by prerendering field
 * Positions are no longer stored in field but in Node
 * Added offset coordinates to drawField()
 * Refactored so that entities are now also saved in Node
 * Field is no longer serializable since it doesn't make sense to serialize the
 * whole object. Instead use loadFrom and SaveTo methods.
 * LMK @ 20. februar 2007 (v 1.10)
 * Fixed getSize(), now returns (0,0) when field is empty instead of (1,1)
 * Magnus Hemmer Pihl @ 23. februar 2007 (v 1.9)
 * Added boolean returns to placePacman() and placeGhost() methods
 * LMK @ 20. februar 2007 (v 1.8)
 * Changed to use TileSet
 * Changed to hold Entities
 * Changed to use FieldRenderer
 * Added methods to draw
 * Added methods to remove and place entities
 * LMK @ 16. februar 2007 (v 1.7)
 * Added getEntityStartingPositon
 * LMK @ 14. februar 2007 (v 1.6)
 * Added entityStartingPositons
 * When addNoteAt is passed a point of an existing node it will now
 * readjust the points held by that node.
 * LMK @ 12. februar 2007 (v 1.5)
 * You can no longer add Nodes at position less than (0,0)
 * Added hasChanged
 * LMK @ 11. februar 2007 (v 1.4)
 * Fixed Field.getSize() bug
 * LMK @ 9. februar 2007 (v 1.3)
 * Added method addNodeAt(int x, int y, int points)
 * Changed Point to simple int x, int y
 * LMK @ 9. februar 2007 (v 1.2)
 * Made nodes private
 * LMK @ 9. februar 2007 (v 1.1)
 * Added method getSize()
 * LMK @ 9. februar 2007 (v 1.0)
 * Created
 *
 */

package field;

import game.HighScore;
import game.visual.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import game.HighScoreList;

public class Field {
    
    private FieldRenderer renderer;
    private EntityRenderer[] entities;
    private java.util.List nodes;
    private boolean hasChanged;
    private int lastEntity;
    private HighScoreList highScores;
    private Image renderedField;
    
    /**
     * Creates a new empty field
     */
    public Field() {
        this.renderer = new FieldRenderer(this);
        this.nodes = new ArrayList();
        this.entities = new EntityRenderer[3];
        this.lastEntity = 0;
        this.hasChanged = false;
        this.highScores = new HighScoreList();
    }
    
    /**
     *  Returns the highscore-list
     *
     *  @return HighScoreList highscores
     */
    public HighScoreList getHighScores(){
        return this.highScores;
    }
    
    /**
     * Set the starting position of Pacman on the field
     *
     * @param position on the field;
     * @return true if the entity has been placed
     */
    public boolean placePacman(Point position) {
        return this.placeEntityAt(position, 0);
    }
    
    /**
     * Place a ghost on the field. If the maximum amount of ghosts has been
     * placed no ghost is placed.
     *
     * @param position on the field;
     * @return tue if the entity has been placed
     */
    public boolean placeGhost(Point position) {
        for (int i = 1; i < this.entities.length; i++) {
            if (this.entities[i] == null) {
                boolean result = this.placeEntityAt(position, i);
                this.hasChanged = true;
                return result;
            }
        }
        return false;
    }
    
    /**
     * Place entity on the field. The Entity is only placed, if a free node is
     * available at the point.
     *
     * @param position on the field
     * @param id of the entity(, must be unique)
     * @return tue if the entity has been placed
     */
    private boolean placeEntityAt(Point position, int id) {
        Node node = this.getNodeAt(position);
        if (node != null) {
            if (!node.holdsEntity()) {
                if (this.entities[id] != null) {
                    this.entities[id].getEntity().setNode(node);
                } else {
                    this.entities[id] = new EntityRenderer(new Entity(node, id));
                }
                this.hasChanged = true;
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get entity placed at positon
     *
     * @param position on the field
     * @return entity at position
     */
    public Entity getEntityAt(Point position) {
        EntityRenderer current = null;
        
        for (int i = 0; i < this.entities.length; i++) {
            current = this.entities[i];
            if (current != null) {
                if (current.getEntity().getPosition().equals(position)) {
                    return current.getEntity();
                }
            }
        }
        
        return null;
    }
    
    /**
     * Remove entity at position.
     *
     * @param position on field
     */
    public void removeEntityAt(Point position) {
        for (int i = 0; i < this.entities.length; i++) {
            if (this.entities[i] != null) {
                if (this.entities[i].getEntity().getPosition().equals(position)) {
                    this.entities[i].getEntity().setNode(null);
                    this.entities[i] = null;
                    this.hasChanged = true;
                }
            }
        }
    }
    
    /**
     * Replaces an entity with the one specified.
     *
     * @param id The entity (0-2) to replace.
     * @param entity The entity to insert.
     */
    public void setEntity(int id, Entity entity)
    {
        if(id>=0 && id<=2)
            this.entities[id] = new EntityRenderer(entity);
    }
    
    /**
     * Add note to field at position with 0 points held
     *
     * @param x coordinate of node position.
     * @param y coordinate of node position.
     * @return a reference to the node created. If a node already exists at the
     * specified position or could not be placed null is returned.
     */
    public Node addNodeAt(int x, int y) {
        return this.addNodeAt(x, y, 0);
    }
    
    /**
     * Add note to field at position. If a node is already present at the
     * passed position, the node will have it's points adjusted to match
     * the passed amount.
     *
     * @param position to add node.
     * @param points to be held by node.
     * @return a reference to the node created. If a node already exists at the
     * specified position or could not be placed null is returned.
     */
    public Node addNodeAt(Point position, int points) {
        if ((0 <= position.x)&&(0 <= position.y)) {
            Node current = this.getNodeAt(position);
            
            if (current == null) {
                current = new Node(
                        position,
                        this.getNodeAt(position.x-1, position.y),
                        this.getNodeAt(position.x+1, position.y),
                        this.getNodeAt(position.x, position.y-1),
                        this.getNodeAt(position.x, position.y+1),
                        points);
                this.nodes.add(current);                
                this.hasChanged = true;
                return current;
                
            } else {
                current.setPoints(points);
            }
            
            this.hasChanged = true;
        }
        return null;
    }
    
    /**
     * Same as calling #addNoteAt(Point, int)
     *
     * @param x coordinate of node position.
     * @param y coordinate of node position.
     * @param points to be held by node.     
     * @return a reference to the node created. If a node already exists at the
     * specified position or could not be placed null is returned.
     */
    public Node addNodeAt(int x, int y, int points) {
        return this.addNodeAt(new Point(x,y), points);
    }
    
    /**
     * Removes node at position and removes all connections to it.
     *
     * @param position of the node to remove.
     */
    public void removeNodeAt(Point position) {
        if (this.getEntityAt(position) == null) {
            Node node = this.getNodeAt(position);
            
            if (node != null) {
                node.removeAllConnections();
                this.nodes.remove(node);
            }
            
            this.hasChanged = true;
        }
    }
    
    /**
     * Removes node at the specified coordinates.
     * Same as calling #removeNodeAt(Point).
     *
     * @param x coordinate of node position.
     * @param y coordinate of node position.
     */
    public void removeNodeAt(int x, int y) {
        this.removeNodeAt(new Point(x,y));
    }
    
    /**
     * Get node associated with position on the field.
     *
     * @param x to search for.
     * @param y to search for.
     * @return The node at position. If no node is associated
     * NULL is returned.
     */
    public Node getNodeAt(int x, int y) {
        return this.getNodeAt(new Point(x,y));
    }
    
    /**
     * Get node associated with position on the field.
     *
     * @param position to search for.
     * @return The node at position. If no node is associated
     * NULL is returned.
     */
    public Node getNodeAt(Point position) {
        Node current = null;
        
        for (Iterator i = this.nodes.iterator(); i.hasNext();) {
            current = (Node)(i.next());
            if (current.getPosition().equals(position)) {
                return current;
            }
        }
        
        return null;
    }
    
    /**
     * If a node is available at the passed position it will be removed,
     * if none is available one is added.
     *
     * @param position to invert node.
     * @param points to be held by node.
     */
    public void invertNodeAt(Point position, int points) {
        if (this.getNodeAt(position) != null) {
            this.removeNodeAt(position);
        } else {
            this.addNodeAt(position, points);
        }
    }
    
    /**
     * Get the dimension of the field, based on the positions of the nodes.
     * Once the field has been created save the size, this function computes
     * the size anew every time it's called.
     *
     * @return The difference between dimension of the field.
     * 0,0 if the field is empty.
     */
    public Dimension getSize() {
        if (this.nodes.size() != 0) {
            int minX = 0;
            int maxX = 0;
            int minY = 0;
            int maxY = 0;
            Point current = null;
            
            for (Iterator i = this.nodes.iterator(); i.hasNext();) {
                current = ((Node)(i.next())).getPosition();
                if (current.getX() < minX) {
                    minX = (int)current.getX();
                }
                if (current.getX() > maxX) {
                    maxX = (int)current.getX();
                }
                if (current.getY() < minY) {
                    minY = (int)current.getY();
                }
                if (current.getY() > maxY) {
                    maxY = (int)current.getY();
                }
            }
            
            return new Dimension(maxX - minX + 1, maxY - minY +1);
        }
        
        return new Dimension(0,0);
    }
    
    /**
     * Retrieve the list of nodes at the field
     *
     * @return list of nodes on the field
     */
    public java.util.List getNodeList() {
        return this.nodes;
    }
    
    /**
     * Removes all nodes held by field. And clear entity start positions.
     */
    public void clearField() {
        //add code to clear entity positions
        this.nodes.clear();
        this.entities = new EntityRenderer[3];
        this.hasChanged = false;
    }
    
    /**
     * Get contents of field and entityStatingPositions from file
     *
     * @param file to load data from.
     */
    public boolean loadFrom(File file) {
        this.renderedField = null;
        ObjectInputStream in = null;
        boolean success = true;
        
        if (file.isFile()) {
            try {
                in = new ObjectInputStream(new FileInputStream(file));
                
                this.nodes.clear();
                this.highScores.clear();
                
                int nodeCount = in.readInt();
                this.entities = new EntityRenderer[in.readInt()];                
                int highScoreCount = in.readInt();
                {
                    int x;
                    int y;
                    for (int i = 0; i < nodeCount; i++) {
                        x = in.readInt();
                        y = in.readInt();
                        this.addNodeAt(new Point(x,y), in.readInt());
                    }
                }
                
                {
                    int id;
                    int x;
                    int y;
                    for (int i = 0; i < this.entities.length; i++) {
                        id = in.readInt();
                        x = in.readInt();
                        y = in.readInt();
                        if (id != -1) {
                            this.placeEntityAt(new Point(x,y), id);
                        }
                    }
                }
                
                {
                    String name;
                    for (int i = 0; i < highScoreCount; i++) {
                        name = in.readUTF();
                        this.highScores.add(new HighScore(name, in.readInt()));
                    }
                }
            } catch (Exception e) {
                success = false;
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (Exception e) {
                } finally {
                    in = null;
                }
            }
        }
        
        if (success) {
            this.hasChanged = false;
        }
        
        return success;
    }
    
    /**
     * Save contents of field and entityStatingPositions to file
     *
     * @param file to save data to.
     */
    public boolean saveTo(File file) {
        ObjectOutputStream out = null;
        boolean success = true;
        
        try {
            out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeInt(this.nodes.size());
            out.writeInt(this.entities.length);
            out.writeInt(this.highScores.size());
               
            {
                Node current = null;
                for (Iterator i = this.nodes.iterator(); i.hasNext();) {
                    current = (Node)i.next();
                    out.writeInt(current.getPosition().x);
                    out.writeInt(current.getPosition().y);
                    out.writeInt(current.getPoints());  
                }
            }
            
            for (int i = 0; i < this.entities.length; i++) {
                if (this.entities[i] != null) {
                    out.writeInt(i);
                    out.writeInt(this.entities[i].getEntity().getPosition().x);
                    out.writeInt(this.entities[i].getEntity().getPosition().y);
                } else {
                    out.writeInt(-1);
                    out.writeInt(0);
                    out.writeInt(0);
                }
            }
            {
                if (this.hasChanged) {
                    this.highScores.reset();
                }   
                
                HighScore current = null;
                for (Iterator i = this.highScores.iterator(); i.hasNext();) {
                    current = (HighScore)i.next();
                    out.writeUTF(current.getName());
                    out.writeInt(current.getScore());
                }
            }
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (Exception e) {
            } finally {
                out = null;
            }
        }
        
        if (success) {
            this.hasChanged = false;
        }
        
        return success;
    }
    
    /**
     * Check whether the Field has changed since last save/load.
     *
     * @return true if the state of the Field has changed.
     */
    public boolean hasChanged() {
        return this.hasChanged;
    }
    
    /**
     * Draw baseTile, nodes and entities
     *
     * @param g canvas to draw on.
     * @param offset x.
     * @param offset y.
     * @param area to draw.
     */
    public void drawField(Graphics g, int offsetX, int offsetY) {
        if ((this.hasChanged) || (this.renderedField == null)) {
            this.renderedField = this.renderer.render();
        }
        
        g.drawImage(this.renderedField, offsetX, offsetY, null);
        //this.renderer.drawPoints(g, offsetX, offsetY);
        
        for (int i = 0; i < this.entities.length; i++) {
            if (this.entities[i] != null) {
                this.entities[i].draw(g, offsetX, offsetY);
            }
        }
    }
        
    /**
     * Force repaint of a particular node. The repaint is done on
     * the internal prerendered image.
     *
     * @param node to repaint
     */
    public void repaintNode(Node node) {
        this.renderer.repaintNode(this.renderedField.getGraphics(), node);
    }
    
    /**
     * Get the renderer used to draw the field
     *
     * @return FieldRenderer
     */
    public FieldRenderer getRenderer() {
        return this.renderer;
    }
    
    /**
     * Get array of entity renderers
     *
     * @return EntityRenderer[]
     */
    public EntityRenderer[] getEntityRenderers() {
        return this.entities;
    }
    
    /**
     * Get total amount of points left on field.
     *
     * @result total points.
     */
    public int getPointsLeft() {
        int total = 0;
        Node current = null;
        
        for (Iterator i = this.nodes.iterator(); i.hasNext();) {
            current = (Node)(i.next());
            
            if (!current.pointsTaken()) {
                total += current.getPoints();
            }
        }
        
        return total;
    }
    
    /**
     * Move the entirety of nodes on field a distance along the x and y axises.
     * 
     * @param x axis offset
     * @param y axis offset
     */
    public void offsetNodes(int x, int y) {
        Node current;
        
        for (Iterator i = this.nodes.iterator(); i.hasNext();) {
            current = (Node)i.next();
            current.getPosition().translate(x,y);
        }
    }    
}
