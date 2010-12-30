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
 */


package de.apric.ipfire.tray;

import de.apric.ipfire.tray.gui.ErrorDialog;
import java.util.Properties;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * IPFireTray application entry point: load properties and start the system tray icon
 *
 * @author apric
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            final Properties properties = SettingsManager.loadProperties(); // load the properties file first

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        new IPFireTray(properties); // launch the tray icon
                    } catch (Exception e) {
                        new ErrorDialog(e, true).setVisible(true);
                    }
                }
                
            });
            
//            new MySystemTray(properties); // launch the tray icon
        }

        /* handle all kinds of critical errors: */

        catch (NumberFormatException e){
            new ErrorDialog(new IllegalArgumentException("Check your configuration file for errors.", e), true).setVisible(true);
        }
        catch (Exception e){
            new ErrorDialog(e, true).setVisible(true);
        }
    }



}
