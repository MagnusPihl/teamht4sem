/*
 * GameDialog.java
 *
 * Created on 24. april 2007, 09:24
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
 * MBN @ 24. april 2007 (v 1.0)
 * Created
 *
 */

package game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;


public class GameDialog{

    private Image dialog;
    
    /** Creates a new instance of GameDialog */
    public GameDialog() {
        String path = (new File("images/dialog.png")).getAbsolutePath();
        this.dialog = (new ImageIcon(path)).getImage();
    }
    
    public void drawDialog(Graphics2D _g, int offX, int offY, String txt){
        _g.drawImage(this.dialog, offX, offY, null);
        _g.drawString(txt, offX + 50, offY + 50);
    }
}
