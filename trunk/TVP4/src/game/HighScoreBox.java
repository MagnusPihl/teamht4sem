/*
 * HighScoreBox.java
 *
 * Created on 9. marts 2007, 02:13
 *
 * Company: HT++
 *
 * @author Mikkel Brøndsholm Nielsen
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * Administrator @ 9. marts 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class HighScoreBox extends JFrame{
    private JTextField nameField;
    private int position, score;
    /** Creates a new instance of HighScoreBox */
    public HighScoreBox(int _position, int _score) {
        super();
        this.position = _position;
        this.score = _score;
        JPanel workspace = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Congratulations! You have reached rank" +  _position + "\n\nPlease write your name underneath.");
        this.add(workspace);
        nameField = new JTextField();
        JButton submit = new JButton("Ok");
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendName();
            }
        });
        workspace.add(submit, BorderLayout.SOUTH);
        workspace.add(nameField, BorderLayout.CENTER);
        workspace.add(label, BorderLayout.NORTH);
        Dimension d = new Dimension(50,100);
        this.setPreferredSize(d);
        this.setSize(d);
        this.setVisible(true);
    }
    
    private void sendName(){
        PacmanApp.getInstance().getGameScene().getField().getHighScores().addHighScore(nameField.getText(), this.score, this.position);
        this.dispose();
    }
}
