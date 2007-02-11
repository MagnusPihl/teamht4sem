/*
 * LevelEditor.java
 *
 * Created on 9. februar 2007, 22:29
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 9. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */

package editor;

import field.*;
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
    private ConfigPanel configPanel;
    private EditorMenu menu;
    private JFileChooser openSaveDialog, skinDialog;
    
    /** Creates a new instance of LevelEditor */
    public LevelEditor() {
        this.frame = new JFrame();
        this.saveFile = null;
        this.updateTitle();
        
        this.field = new Field();
        this.menu = new EditorMenu(this);
        this.editorPanel = new EditorPanel(field);
        this.configPanel = new ConfigPanel(editorPanel);        
        
        JScrollPane scrollPanel = new JScrollPane(editorPanel); //, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        frame.setPreferredSize(new Dimension(640, 480));
        
        frame.getContentPane().add(this.menu, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPanel, BorderLayout.CENTER);
        frame.getContentPane().add(configPanel, BorderLayout.WEST);
        
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        this.openSaveDialog = new JFileChooser();
        this.openSaveDialog.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {                
                return (f.getName().toLowerCase().endsWith(".lvl") && f.isFile());
            }
            public String getDescription() {
                return "Level files";
            }
        });
        
        this.skinDialog = new JFileChooser(new File("skins/"));
        this.skinDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        /*this.skinDialog.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return (f.isDirectory());
            }
            public String getDescription() {
                return "Skin folders";
            }
        });         */       
    }
    
    public static void main(String[] args) {
        new LevelEditor();
    }
    
    public void newLevel() {
        boolean commit = true;
        if (this.field.hasChanged()) {
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this.frame, 
                "Are you sure you wish to abandon the current level and start a new one?")) {
                commit = false;
            }            
        }
        
        if (commit) {
            this.field.clearField();
            this.saveFile = null;
            this.updateTitle();
            this.editorPanel.checkSize();
        }
    }
    
    public void scanLevel() {
        boolean commit = true;
        
        if (this.field.hasChanged()) {
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this.frame, 
                "Are you sure you wish to abandon the current level and start a new one?")) {
                commit = false;
            }
        }
        
        if (commit) {
            this.field.clearField();
        }
    }        
    
    public void openLevel() {        
        boolean commit = true;
        
        if (this.field.hasChanged()) {
            if (JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(this.frame, 
                "Are you sure you wish to open a new level?")) {
                commit = false;
            }
        }
                
        if (commit) {
            if (JFileChooser.APPROVE_OPTION == this.openSaveDialog.showOpenDialog(this.frame)) {
                File file = this.openSaveDialog.getSelectedFile();
                
                if (this.field.loadNodesFrom(file)) {
                    this.saveFile = file;
                    this.updateTitle();
                    this.editorPanel.checkSize();
                } else {
                    JOptionPane.showMessageDialog(this.frame, "Could not open level, file doesn't exist!");
                }
            }
        }
    }
    
    public void saveLevel() {
        if (this.saveFile != null) {                        
            if (!this.field.saveNodesTo(this.saveFile)) {                                                
                JOptionPane.showMessageDialog(this.frame, "Could not save level!");
            }
        } else {
            this.saveLevelAs();
        }
    }
    
    public void saveLevelAs() {
        if (JFileChooser.APPROVE_OPTION == this.openSaveDialog.showSaveDialog(this.frame)) {            
            //System.out.println(this.openSaveDialog.getSelectedFile());
            
            File file = this.openSaveDialog.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".lvl")) {
                file = new File(file.getAbsoluteFile() + ".lvl");
            }
            System.out.println(file.getAbsoluteFile());
            
            if (this.field.saveNodesTo(file)) {                                                
                this.saveFile = file;
                this.updateTitle();
            } else {                 
                JOptionPane.showMessageDialog(this.frame, "Could not save level=");
            }
        }
    }
    
    public void quit() {
        //insert code that checks whether user have saved since last change
        System.exit(0);
    }
    
    public void setSkin(String path) {
        this.editorPanel.loadTile(path);
        this.editorPanel.checkSize();
    }
    
    public void openSkin() {
        if (JFileChooser.APPROVE_OPTION == this.skinDialog.showOpenDialog(this.frame)) {
            this.editorPanel.loadTile(this.skinDialog.getSelectedFile());
            this.editorPanel.checkSize();
        }        
    }
    
    public void about() {}
    public void openHelp() {}
    
    public void updateTitle() {
        if (this.saveFile != null) {
            this.frame.setTitle("Pacman Level Editor - " + this.saveFile.getAbsoluteFile());
        } else {            
            this.frame.setTitle("Pacman Level Editor");
        }
    }
}