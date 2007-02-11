/*
 * EditorMenu.java
 *
 * Created on 10. februar 2007, 22:01
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 10. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package editor;

import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author LMK
 */
public class EditorMenu extends JMenuBar {
    
    private LevelEditor editor;
    
    /** Creates a new instance of EditorMenu */
    public EditorMenu(LevelEditor editor) {
        this.editor = editor;
        
        String[][] menuStrings = {
            {"File","New level","Scan new level","Open...","-","Save","Save As...","-","Quit"},
            {"Skin","Nodes","Pac-Man","-","Open skin..."},
            {"Help","About...","Open help"}
        };
        
        char[][] mnemonics = {
            {'f','n','c','o','-','-','s','-','q'},
            {'s','n','p','-','o'},
            {'h','a','h'}
        };
        
        KeyStroke[][] keyStrokes = {
            {null, 
             KeyStroke.getKeyStroke('N',InputEvent.CTRL_MASK),
             KeyStroke.getKeyStroke('O',InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK),
             KeyStroke.getKeyStroke('0',InputEvent.CTRL_MASK),
             null,
             KeyStroke.getKeyStroke('S',InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK),
             KeyStroke.getKeyStroke('S',InputEvent.CTRL_MASK),
             null,                     
             KeyStroke.getKeyStroke('Q',InputEvent.CTRL_MASK)},
            {null, 
             KeyStroke.getKeyStroke('1',InputEvent.CTRL_MASK),
             KeyStroke.getKeyStroke('2',InputEvent.CTRL_MASK),
             null,
             KeyStroke.getKeyStroke('O',InputEvent.ALT_MASK)},
            {null, 
             null,
             KeyStroke.getKeyStroke("F1")}
        };                
        
        ActionListener[][] actionListener = {
            {null,
             new ActionListener() {public void actionPerformed(ActionEvent evt) {newLevel();}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {scanLevel();}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {openLevel();}},
             null,
             new ActionListener() {public void actionPerformed(ActionEvent evt) {saveLevel();}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {saveLevelAs();}},
             null, 
             new ActionListener() {public void actionPerformed(ActionEvent evt) {quit();}}},             
            {null,
             new ActionListener() {public void actionPerformed(ActionEvent evt) {setSkin("skins/nodes/");}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {setSkin("skins/pacman/");}},
             null,
             new ActionListener() {public void actionPerformed(ActionEvent evt) {openSkin();}}},
            {null,
             new ActionListener() {public void actionPerformed(ActionEvent evt) {about();}},
             new ActionListener() {public void actionPerformed(ActionEvent evt) {openHelp();}}}                      
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
            
            this.add(topMenu);
        }
        /*JMenuItem fileMenuOpen, fileMenuScan, fileMenuSave, fileMenuQuit;
        JMenuItem skinMenuNodes, skinMenuPacman, skinMenuOpen;
        JMenuItem helpMenuAbout, helpMenuHelp;*/                
    }
    
     public void newLevel() {this.editor.newLevel();}
     public void scanLevel() {this.editor.scanLevel();}
     public void openLevel() {this.editor.openLevel();}
     public void saveLevel() {this.editor.saveLevel();}
     public void saveLevelAs() {this.editor.saveLevelAs();}     
     public void quit() {this.editor.quit();}     
     public void setSkin(String path) {this.editor.setSkin(path);}
     public void openSkin() {this.editor.openSkin();}          
     public void about() {this.editor.about();}
     public void openHelp() {this.editor.openHelp();}     
}
