/*
 * HighScore.java
 *
 * Created on 6. marts 2007, 10:39
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 *
 * Administrator @ 6. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game;


public class HighScore {
    
    private String name;
    private int score;
    /** Creates a new instance of HighScore */
    public HighScore(String _name, int _score) {
        this.name = _name;
        this.score = _score;
    }
    
    public int getScore(){
        return this.score;
    }
    
    public String getName(){
        return this.name;
    }    
}
