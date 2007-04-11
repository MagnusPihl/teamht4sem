/*
 * HighScoreList.java
 *
 * Created on 6. marts 2007, 09:51
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Administrator @ 6. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;


public class HighScoreList extends ArrayList implements Serializable {
    
    /** Creates a new instance of HighScoreList */
    public HighScoreList() {
        super();
        reset();
    }
    
    public void reset(){
        this.addHighScore("NewPlayer", 0, 0);
        this.addHighScore("NewPlayer", 0, 1);
        this.addHighScore("NewPlayer", 0, 2);
        this.addHighScore("NewPlayer", 0, 3);
        this.addHighScore("NewPlayer", 0, 4);
        this.addHighScore("NewPlayer", 0, 5);
        this.addHighScore("NewPlayer", 0, 6);
        this.addHighScore("NewPlayer", 0, 7);
        this.addHighScore("NewPlayer", 0, 8);
        this.addHighScore("NewPlayer", 0, 9);
    }
    
    public boolean addHighScore(String name, int score, int position){
        HighScore highScore = new HighScore(name, score);
        if(position <= 9 && position >=0){
            this.add(position, highScore);
            if(this.size() > 10){
                this.remove(this.size()-1);
            }
            return true;
        }
        else{ return false;}
    }
    
    public int isHighScore(int score){
        int index = 0;
        for(Iterator iter = this.iterator(); iter.hasNext(); index++){
            HighScore hScore = (HighScore) iter.next();
            if(score >= hScore.getScore()){
                return index;
            }
        }
        return -1;
    }
    
    private HighScore getHighScore(int i){
        return (HighScore) this.get(i);
    }
    
    public String scoreToString(){
        String scoreAsString = "";
        int index = 1;
        for(Iterator iter = this.iterator(); iter.hasNext();index++){
            HighScore hScore = (HighScore) iter.next();
            if(index >=1 && index <=10){
                scoreAsString += hScore.getScore() + "\n";
            }
        }
        return scoreAsString;
    }
    
    public String namesToString(){
        String namesAsString = "";
        int index = 1;
        for(Iterator iter = this.iterator(); iter.hasNext();index++){
            HighScore hScore = (HighScore) iter.next();
            if(index >=1 && index <=10){
                namesAsString += index + ". " + hScore.getName() + "\n";
            }
        }
        return namesAsString;
    }
}
