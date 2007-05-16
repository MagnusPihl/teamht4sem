/*
 * EditorMenu.java
 *
 * Created on 10. februar 2007, 22:01
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.4
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 23. april 2007 (v 1.4)
 * Added High Score List to View menu
 * LMK @ 14. februar 2007 (v 1.3)
 * Keystroke now uses virtual keycodes instead of chars and strings.
 * Fixed noneworking open keystroke
 * LMK @ 14. februar 2007 (v 1.2)
 * Added draw buttons for ghosts and pacman
 * LMK @ 13. februar 2007 (v 1.1)
 * Added view menu and show hide grid and points.
 * Changed to support the change of LevelEditor into a singleton
 * LMK @ 10. februar 2007 (v 1.0)
 *
 */

package editor;

import game.SkinFileFilter;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 *
 * @author LMK
 */
public class EditorMenu extends JPanel {
    
    private JTextField pointsField;
    
    public static final String IMAGE_DIR = (new File("images").getAbsolutePath()) + File.separator;
    
    /** Creates a new instance of EditorMenu */
    public EditorMenu() {
        this.setLayout(new BorderLayout());
        JMenuBar menu = new JMenuBar();
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        String[] skins;
        File dir = new File("skins/");
        File skinList[] = dir.listFiles(new SkinFileFilter());
        skins = new String[skinList.length+3];
        skins[0] = "Skin";
        for(int i=0; i<skinList.length; i++)
            skins[i+1] = skinList[i].getName();
        skins[skinList.length+1] = "-";
        skins[skinList.length+2] = "Open Skin...";
        
        String[][] menuStrings = {
            {"File","New Level","Scan New Level","Open...","-","Save","Save As...","-","Quit"},
            {"Brush","Use Path Brush","Use Pacman Brush","Use Ghost Brush"},
            {"View","Show Grid","Show Points","-","Show High Score List"},
            skins,
            {"Help",/*"About...",*/"Open Help"}
        };
        
        char[] skinMnemonics = new char[skins.length];
        skinMnemonics[0] = 's';
        for(int i=0; i<skinList.length; i++)
            skinMnemonics[i+1] = skins[i+1].charAt(0);
        skinMnemonics[skinList.length+2] = 'o';
        
        char[][] mnemonics = {
            {'f','n','c','o','-','-','s','-','q'},
            {'b','p','m','g'},
            {'v','g','p','-','h'},
            skinMnemonics,
            {'h',/*'a',*/'h'}
        };
        
        KeyStroke[] skinKeys = new KeyStroke[skins.length];
        for(int i=0; i<skinList.length; i++) {
            if (i < 10) {
                skinKeys[i+1] = KeyStroke.getKeyStroke(i + 0x30, InputEvent.ALT_MASK);  
            } else {
                skinKeys[i+1] = null;
            }            
        }
        skinKeys[skinList.length+1] = null;
        skinKeys[skinList.length+2] = KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.ALT_MASK);
        
        KeyStroke[][] keyStrokes = {
            {null, 
             KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK),
             KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK),
             KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK),
             null,
             KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK),
             KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK),
             null,                     
             KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK)},
            {null,
             KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK ),
             KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_MASK ),
             KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_MASK )},
            {null,
             KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK),
             KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK),
             null,             
             KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK)},
            skinKeys,
            {null, 
             //null,
             KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0)}
        };
        
        ActionListener[] skinActions = new ActionListener[skins.length];
        skinActions[0] = null;
        for(int i=1; i<skins.length; i++)
        {
            final String name = "skins/"+skins[i]+"/";
            skinActions[i] = new ActionListener()
                {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().setSkin(name);}};
        }
        skinActions[skinList.length+2] = new ActionListener()
                {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().openSkin();}};
        
        ActionListener[][] actionListener = {
            {null,
             new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().newLevel();}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().scanLevel();}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().openLevel();}},
             null,
             new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().saveLevel();}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().saveLevelAs();}},
             null, 
             new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().quit();}}},  
            {null,
             new ActionListener() {public void actionPerformed(ActionEvent evt) {
                 LevelEditor.getInstance().getEditorPanel().setBrush(new NodeBrush(LevelEditor.getInstance().getEditorPanel()));}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {
                 LevelEditor.getInstance().getEditorPanel().setBrush(new PacmanBrush(LevelEditor.getInstance().getEditorPanel()));}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {
                 LevelEditor.getInstance().getEditorPanel().setBrush(new GhostBrush(LevelEditor.getInstance().getEditorPanel()));}}},
            {null,
             new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().showHideGrid();}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().showHidePoints();}},
             null,
             new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().showHighScoreDialog();}}},
            skinActions,
            {null,
             //new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().about();}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {LevelEditor.getInstance().openHelp();}}}                      
        };
        
        //JMenu[] topMenus = new JMenu[menuStrings.length];
        JMenu topMenu = null;
        JMenuItem menuItem = null;
        
        for (int i = 0; i < menuStrings.length; i++) {
            topMenu = new JMenu(menuStrings[i][0]);
            topMenu.setMnemonic(mnemonics[i][0]);
            
            for (int j = 1; j < menuStrings[i].length; j++) {
                if (!menuStrings[i][j].equals("-")) {
                    menuItem = new JMenuItem(menuStrings[i][j]);                    
                    menuItem.setMnemonic(mnemonics[i][j]);
                    menuItem.setAccelerator(keyStrokes[i][j]);
                    menuItem.addActionListener(actionListener[i][j]);
                    topMenu.add(menuItem);
                } else {
                    topMenu.add(new JSeparator());
                }
            }
            
            menu.add(topMenu);
        }
        
        //toolbar        
        JButton gridBtn = new JButton(new ImageIcon(IMAGE_DIR + "grid.png"));
        gridBtn.setToolTipText("Show/hide grid");
        gridBtn.addActionListener(actionListener[2][1]);
        toolbar.add(gridBtn);
        
        
        JButton pointsBtn = new JButton(new ImageIcon(IMAGE_DIR + "points.png"));
        pointsBtn.setToolTipText("Show/hide points");
        pointsBtn.addActionListener(actionListener[2][2]);
        toolbar.add(pointsBtn);
        
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        
        JButton brushBtn = new JButton(new ImageIcon(IMAGE_DIR + "brush.png"));
        brushBtn.setToolTipText("Use path brush");
        brushBtn.addActionListener(actionListener[1][1]);
        toolbar.add(brushBtn);
        
        /*JButton lineBtn = new JButton(new ImageIcon(IMAGE_DIR + "line.png"));
        lineBtn.setToolTipText("Draw lines");
        //pointsBtn.addActionListener(actionListener[1][2]);
        toolbar.add(lineBtn);*/
                
        JButton pacmanBtn = new JButton(new ImageIcon(IMAGE_DIR + "pacman.png"));
        pacmanBtn.setToolTipText("Use pacman brush");
        pacmanBtn.addActionListener(actionListener[1][2]);
        toolbar.add(pacmanBtn);
        
        JButton ghostBtn = new JButton(new ImageIcon(IMAGE_DIR + "ghosts.png"));
        ghostBtn.setToolTipText("Use ghosts brush");
        ghostBtn.addActionListener(actionListener[1][3]);
        toolbar.add(ghostBtn);
        
        toolbar.add(new JSeparator(JSeparator.VERTICAL));
        toolbar.add(new JLabel("Points:"));
        
        pointsField = new JTextField("10");
        pointsField.setHorizontalAlignment(JTextField.RIGHT);
        pointsField.setPreferredSize(new Dimension(50,22));
        pointsField.setToolTipText("Number of points to be added to nodes");
        toolbar.add(pointsField);
        
        this.add(menu, BorderLayout.NORTH);
        this.add(toolbar, BorderLayout.SOUTH);
    }
    
    /**
     * Get amount of points specified in the points text field.
     * If the text held in the text field is not a number 0 is returned.
     */
    public int getPoints() {
        try {
            return Integer.parseInt(pointsField.getText());
        } catch (Exception e) {
            return 0;
        }
    }    
}