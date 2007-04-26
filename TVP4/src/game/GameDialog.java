/*
 * GameDialog.java
 *
 * Created on 24. april 2007, 09:24
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.1
 *
 *
 * ******VERSION HISTORY******
 *
 * MHP @ 26. april 2007 (v 1.1)
 * Changed font to use BitMapFont.
 * Added options for centering the text and the dialog.
 *
 * MBN @ 24. april 2007 (v 1.0)
 * Created
 *
 */

package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;


public class GameDialog{

    private static Image dialog = new ImageIcon("images/dialog.png").getImage();
    private static Image text;
    private static String lastText = "";
    public static boolean centerText = true;
    
    /** Creates a new instance of GameDialog */
    public GameDialog() {
    }
    
    public static void drawDialog(Graphics2D _g, int offX, int offY, String _text){
        _g.drawImage(dialog, offX, offY, null);
        
        if(_text != lastText)
        {
            text = renderText(_text);
            lastText = _text;
        }
        int centerXOffset = 0;
        int centerYOffset = 0;
        if(centerText == true)
        {
            centerXOffset = (475/2) - (text.getWidth(null)/2);
            centerYOffset = (160/2) - (text.getHeight(null)/2);
        }
        
        _g.drawImage(text, offX+25+centerXOffset, offY+40+centerYOffset, null);
    }
    
    public static void drawDialogCenter(Graphics2D _g, String _text)
    {
        drawDialog(_g, 400-(dialog.getWidth(null)/2), 300-(dialog.getHeight(null)/2), _text);
    }
    
    private static Image renderText(String _text)
    {
        return PacmanApp.getInstance().getFont().renderString(_text, 475);
    }
}
