/*
 * Entity.java
 *
 * Created on 18. februar 2007, 13:19
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Administrator @ 18. februar 2007 (v 1.1)
 * __________ Changes ____________
 *
 * Administrator @ 18. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package field;

import java.awt.Point;

public class Entity {
    
    private boolean isMoving;
    private Point position;
    private int direction;
    private int speed;
    private int ID;
    private boolean isPacman;
    
    /** Creates a new instance of Entity */
    public Entity(Point _position, int _ID) {
        this.isMoving = false;
        this.position = _position;
        this.direction = Node.UP;
        this.speed = 0;
        this.ID = _ID;
    }
    
    public void setIsMoving(boolean _isMoving){this.isMoving = _isMoving;}
    public boolean isMoving(){return this.isMoving;}
    public int getDirection(){return this.direction;}
    public void setDirection(int dir){this.direction = dir;}
    public void setPosition(Point _position){this.position = _position;}
    public Point getPosition(){return this.position;}
    public void setSpeed(int _speed){this.speed = _speed;}
    public int getSpeed(){return this.speed;}
    public void setID(int _ID){this.ID = _ID;}
    public int getID(){return this.ID;}
}
