/*
 * LevelEditor.java
 *
 * Created on 9. februar 2007, 22:29
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.2
 *
 *
 * ******VERSION HISTORY******
 * LMK @ 16. februar 2007 (v 1.2)
 * Refactored new, save, open and scan functions
 * Created confirmClearField()
 * LMK @ 13. februar 2007 (v 1.1)
 * Changed into singleton
 * Added javadoc
 * Added getPoints()
 * LMK @ 9. februar 2007 (v 1.0)
 *
 */

package editor;

import field.*;
import field.Field;
import game.visual.TileSet;
import java.awt.*;
import javax.swing.*;
import java.io.*;

/**
 *
 * @author LMK
 */
public class LevelEditor {
    
    private File saveFile;
    private JFrame frame;
    private Field field;
    private EditorPanel editorPanel;
    private StatusBar statusBar;
    private EditorMenu menu;
    private JFileChooser openSaveDialog, skinDialog;
    
    private static LevelEditor instance = new LevelEditor();
    
    /** Creates a new instance of LevelEditor */
    private LevelEditor() {
        this.frame = new JFrame();
        this.saveFile = null;
        this.updateTitle();
        
        this.field = new Field();
        this.menu = new EditorMenu();
        this.editorPanel = new EditorPanel(field);
        this.statusBar = new StatusBar(editorPanel);    
        
        
        JScrollPane scrollPanel = new JScrollPane(editorPanel); //, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.setPreferredSize(new Dimension(640, 480));
        
        frame.getContentPane().add(menu, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPanel, BorderLayout.CENTER);
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
        
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        this.openSaveDialog = new JFileChooser();
        this.openSaveDialog.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {                
                return ((f.getName().toLowerCase().endsWith(".lvl") && f.isFile()) || (f.isDirectory()));
            }
            public String getDescription() {
                return "Level files";
            }
        });
        
        this.skinDialog = new JFileChooser(new File("skins/"));
        this.skinDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }
    
    /** 
     * Get the current LevelEditor object
     *
     * @return LevelEditor
     */
    public static LevelEditor getInstance() {
        return instance;
    }
    
    /**
     * Start application
     */
    public static void main(String[] args) {
        LevelEditor.getInstance();
    }
    
    /**
     * Ask user whether he wants to save the current level before he continues
     * with the current action (such as new, scan or open). 
     *
     * @return true if the user does not press cancel or closes the dialog.
     */
    public boolean confirmClearField() {
        int result;
        
        if (this.field.hasChanged()) {
            result = JOptionPane.showConfirmDialog(this.frame, 
                "Do you wish to save the current level before you continue?");
            if (JOptionPane.OK_OPTION == result) {
                this.saveLevel();
                return true;
            }            
            if (JOptionPane.NO_OPTION == result) {
                return true;
            }
            if ((JOptionPane.CANCEL_OPTION == result) || 
                (JOptionPane.CLOSED_OPTION == result)) {;
            }
        }
        
        return true;
    }
    
    /**
     * Create a new level.
     */
    public void newLevel() {
        if (this.confirmClearField()) {
            this.field.clearField();
            this.saveFile = null;
            this.updateTitle();
            this.editorPanel.checkSize();
        }
    }
    
    /**
     * Scan physical level using robots.
     */
    public void scanLevel() {
        if (this.confirmClearField()) {
            this.field.clearField();
            this.saveFile = null;
            this.updateTitle();
            this.editorPanel.checkSize();
            //scan code
        }
    }        
    
    /**
     * Open a previously saved level.
     */
    public void openLevel() {        
        if (this.confirmClearField()) {
            if (JFileChooser.APPROVE_OPTION == this.openSaveDialog.showOpenDialog(this.frame)) {
                File file = this.openSaveDialog.getSelectedFile();
                
                if (this.field.loadFrom(file)) {
                    this.saveFile = file;
                    this.updateTitle();
                    this.editorPanel.checkSize();
                } else {
                    JOptionPane.showMessageDialog(this.frame, "Could not open level, file doesn't exist!");
                }
            }
        }            
    }
    
    /**
     * Save level to the currently selected save file. If no file is selected
     * ask user to select a file.
     */
    public void saveLevel() {
        if (this.saveFile != null) {                        
            if (!this.field.saveTo(this.saveFile)) {                                                
                JOptionPane.showMessageDialog(this.frame, "Could not save level!");
            }
        } else {
            this.saveLevelAs();
        }
    }
    
    /**
     * Ask user to select a file in wich to save the current level.
     */
    public void saveLevelAs() {
        if (JFileChooser.APPROVE_OPTION == this.openSaveDialog.showSaveDialog(this.frame)) {            
            //System.out.println(this.openSaveDialog.getSelectedFile());
            
            File file = this.openSaveDialog.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".lvl")) {
                file = new File(file.getAbsoluteFile() + ".lvl");
            }
            //System.out.println(file.getAbsoluteFile());
                                                           
            this.saveFile = file;
            this.updateTitle();
            this.saveLevel();            
        }
    }
    
    /**
     * Exit the level editor. 
     * Should ask user whether he wants to save before quitting.
     */
    public void quit() {
        //insert code that checks whether user have saved since last change
        System.exit(0);
    }
    
    /**
     * Use skin from directory.
     *
     * @param path to directory where skin files are stored.
     */
    public void setSkin(String path) {
        TileSet.getInstance().loadTileSet(new File(path));
        this.editorPanel.checkSize();
    }
    
    /**
     * Ask user to select directory in wich skin files to be used are stored.
     */
    public void openSkin() {
        if (JFileChooser.APPROVE_OPTION == this.skinDialog.showOpenDialog(this.frame)) {
            this.setSkin(this.skinDialog.getSelectedFile().getAbsolutePath());
        }        
    }
    
    /**
     * Inverts the current grid view setting. If the grid is visible it is
     * made invisible and vice versa.
     */
    public void showHideGrid() {       
        this.editorPanel.setGridVisible(!this.editorPanel.isGridVisible());
        this.editorPanel.checkSize();
    }
    
    /**
     * Inverts the current point view setting. If the points are visible on the field 
     * they are made invisible and vice versa.
     */
    public void showHidePoints() {
        this.editorPanel.setPointsVisible(!this.editorPanel.isPointsVisible());
        this.editorPanel.checkSize();
    }
    
    /**
     * Show about dialog.
     */
    public void about() {}
    
    /**
     * Open help file.
     */
    public void openHelp() {}
    
    /**
     * Updates the titlebar to reflect the currently open file.
     */
    public void updateTitle() {
        if (this.saveFile != null) {
            this.frame.setTitle("Pacman Level Editor - " + this.saveFile.getAbsoluteFile());
        } else {            
            this.frame.setTitle("Pacman Level Editor");
        }
    }
        
    /**
     * Get amount of points to be added to nodes.
     *
     * @return points specified in menu.
     */
    public int getPoints() {
        return this.menu.getPoints();
    }
    
    /**
     * Get the EditorPanel
     * 
     * @return EditorPanel
     */
    public EditorPanel getEditorPanel() {
        return this.editorPanel;
    }        
}