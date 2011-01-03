/*
 * This file is part of IPFireTray.
 *
 * IPFireTray is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * IPFireTray is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IPFireTray.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 
 * inspired by:
 * TVBrowser <http://tvbrowser.svn.sourceforge.net/viewvc/tvbrowser/trunk/tvbrowser/src/tvbrowser/ui/tray/>
 */


package de.apric.ipfire.tray;

import de.apric.ipfire.tray.gui.ErrorDialog;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
//import javax.swing.SwingUtilities;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author apric
 */
public class TrayPopupMenu extends JPopupMenu {
    
    private final JDialog parent;

    
    /**
     * create the swing JPopupMenu
     */
    public TrayPopupMenu(final Properties properties) throws URISyntaxException {
        super();
        
        parent = new JDialog();
        parent.setSize(0, 0);
        parent.setUndecorated(true);
        parent.setAlwaysOnTop(true);
        parent.setVisible(false);
        
        initComponents(properties);
        addListeners();
        
        // @TODO: find out whether this is really necessary
        setVisible(true);
        setVisible(false);
    }
    
    private void initComponents(final Properties properties) throws URISyntaxException {
        
        /* "open admin web interface" item (only if supported): */
        if (Desktop.isDesktopSupported()) {
            final Desktop desktop = java.awt.Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                final JMenuItem itemWebInterface = new JMenuItem("Open Admin Web Interface");
                final URI uri = new java.net.URI("https://" + properties.getProperty("host") + ":" + properties.getProperty("port"));
                itemWebInterface.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(final ActionEvent e) {
                        try {
                            desktop.browse(uri);
                        } catch (IOException ex) {
                            new ErrorDialog(ex, false); // show non-critical error dialog
                        }
                    }
                });
                this.add(itemWebInterface);
                this.addSeparator();
            }
        }
        
        /* "exit" item: */
        final JMenuItem itemExit = new JMenuItem("Exit");
        itemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.exit(0);
            }
        });
        this.add(itemExit);
    }

    
    private void addListeners() {
        
        this.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {}

            @Override
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
                parent.setVisible(false);
            }

            @Override
            public void popupMenuCanceled(final PopupMenuEvent e) {}
        });
    }
    
    /**
     * @param eventPoint the point where the mouse event occured
     */
    public void showPopup(final Point eventPoint) {

        parent.setVisible(true);
        parent.toFront();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final Point p2 = computePopupCoords(eventPoint.x, eventPoint.y, getPreferredSize());
                show(parent, p2.x - parent.getLocation().x, p2.y - parent.getLocation().y);
            }
        });
    }
    
    
    /**
     * 
     * @param eventX
     * @param eventY
     * @param componentDimension
     * @return 
     */
    private Point computePopupCoords(final int eventX, final int eventY, final Dimension componentDimension) {
        
        int x = eventX;
        int y = eventY;
        
        if (eventX - componentDimension.width > 0) {
            x -= componentDimension.width;
        }
        if (eventY - componentDimension.height > 0) {
            y -= componentDimension.height;
        }
        return new Point(x, y);
    }
            

}
