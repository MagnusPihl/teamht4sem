package game.system;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsConfiguration;
import java.awt.DisplayMode;
import java.awt.Window;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/*
 * ScreenManager.java
 *
 * Created 2006 by LMK
 * Based on examples in "Devoloping Games in Java"
 * by Brackeen, David
 *
 * Company: HT++
 *
 * @author LMK
 * @version 1.0
 *
 *
 * ******VERSION HISTORY******
 *
 * LMK @ 26. februar 2007 (v 1.0)
 * __________ Changes ____________
 *
 */
public class ScreenManager {
    private GraphicsDevice device;

    /**
     * Create new ScreenManager using default screen device.
     */
    public ScreenManager() {
        this.device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    /**
     * Get display modes supported by screen.
     *
     * @return DisplayMode[] containing available modes.
     */
    public DisplayMode[] getDisplayModes() {
        return this.device.getDisplayModes();
    }

    /**
     * Get available display mode from a list of user supplied modes.
     *
     * @param _modes to check compability for.
     * @return First supported display mode available. 
     * NULL if none of the supplied modes are supported.
     */
    public DisplayMode getCompatibleMode(DisplayMode[] _modes) {
        DisplayMode compatibleModes[] = this.device.getDisplayModes();

        for (int i = 0; i < _modes.length; i++) {
            for (int j = 0; j < compatibleModes.length; j++) {
                if (modesMatch(_modes[i],compatibleModes[j])) {
                    return _modes[i];
                }
            }
        }
        return null;
    }

    /**
     * Make screen switch to supplied display mode
     *
     * @param _mode to switch to.
     */
    public void setFullScreen(DisplayMode _mode) {
        JFrame frame = new JFrame();
        frame.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
            public void keyTyped(KeyEvent e) {}

        });
                
        frame.setUndecorated(true);
        frame.setIgnoreRepaint(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.device.setFullScreenWindow(frame);

        if ((_mode != null)&&(this.device.isDisplayChangeSupported())) {
            try {
                this.device.setDisplayMode(_mode);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        //Use dual pageflipping
        frame.createBufferStrategy(2);
    }

    /**
     * Get page to render on.
     *
     * @return Graphic2D to draw on.
     */
    public Graphics2D getGraphics() {
        Window window = this.device.getFullScreenWindow();

        if (window != null) {
            BufferStrategy strategy = window.getBufferStrategy();
            return (Graphics2D)strategy.getDrawGraphics();
        } else {
            return null;
        }
    }

    /**
     * Flip pages
     */
    public void update() {
        Window window = this.device.getFullScreenWindow();

        if (window != null) {
            BufferStrategy strategy = window.getBufferStrategy();
            if (!strategy.contentsLost()) {
                strategy.show();
            }
        }
        Toolkit.getDefaultToolkit();
    }

    /**
     * Get window
     *
     * @return get window.
     */
    public Window getFullScreenWindow() {
        return this.device.getFullScreenWindow();
    }

    /**
     * Return the width of the fullscreen widow.
     *
     * @return width of screen.
     */
    public int getWidth() {
        Window window = this.device.getFullScreenWindow();
        if (window != null) {
            return window.getWidth();
        } else {
            return 0;
        }
    }

    /**
     * Return the height of the fullscreen widow.
     *
     * @return height of screen.
     */
    public int getHeight() {
        Window window = this.device.getFullScreenWindow();
        if (window != null) {
            return window.getHeight();
        } else {
            return 0;
        }
    }

    /**
     * Restore screen to windows default resolution and dispose
     * render window.
     */
    public void restoreScreen() {
        Window window = this.device.getFullScreenWindow();
        if (window != null) {
            window.dispose();
        }
        this.device.setFullScreenWindow(null);
    }

    /**
     * Create image compatible with the display device.
     *
     * @param width of image
     * @param height of image
     * @param transparency mode
     * @return image. May be null if screen couldn't be initialized.
     */
    public BufferedImage createCompatibleImage(int _width, int _height, int _transparency) {
        Window window = this.device.getFullScreenWindow();
        if (window != null) {
            GraphicsConfiguration config = window.getGraphicsConfiguration();
            return config.createCompatibleImage(_width, _height, _transparency);
        }
        return null;
    }

    /**
     * Check whether two display modes match.
     *
     * @param _first mode.
     * @param _second mode.
     * @return true if the display modes match.
     */
    public static boolean modesMatch(DisplayMode _first, DisplayMode _second) {
        if ((_first.getHeight() != _second.getHeight())&&
            (_first.getWidth() != _second.getWidth())) {
            return false;
        }

        if ((_first.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI) &&
            (_second.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI) &&
            (_first.getBitDepth() != _second.getBitDepth()))   {
            return false;
        }

        if ((_first.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN) &&
            (_second.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN) &&
            (_first.getRefreshRate() != _second.getRefreshRate())) {
            return false;
        }

        return true;
    }
}
