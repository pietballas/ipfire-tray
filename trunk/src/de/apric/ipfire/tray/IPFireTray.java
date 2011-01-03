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

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author apric
 */
public final class IPFireTray extends MouseAdapter {

    /* options: */
    private long refreshInterval = 0;
    private float maxDownKBpS = 0;
    private float maxUpKBpS = 0;
    
    private final IPFireDataProvider dataProvider;
    
    private final TrayPopupMenu popupMenu;
    private final TrayMouseAdapter trayMouseAdapter;
    private final TrayIconTooltip trayIconTooltip;

    /**
     * creates a tray icon (the main application) and adds it to the tray
     * 
     * @param validProperties
     * @throws Exception all critical errors will go up
     */
    public IPFireTray(final Properties validProperties) throws Exception {

        super();

        /* ensure system tray is supported: */
        final SystemTray sysTray = SystemTray.getSystemTray();
        if (!SystemTray.isSupported()) {
            throw new UnsupportedOperationException("System Tray not supported!");
        }

        dataProvider = new IPFireDataProvider(
                validProperties.getProperty("host"),
                Integer.parseInt(validProperties.getProperty("port")),
                validProperties.getProperty("user"),
                validProperties.getProperty("pass"));

        /* read properties: */
        refreshInterval = Long.parseLong(validProperties.getProperty("interval"));
        maxDownKBpS = Float.parseFloat(validProperties.getProperty("maxDownKBpS"));
        maxUpKBpS = Float.parseFloat(validProperties.getProperty("maxUpKBpS"));


        /* tray icon: */
        final float[] speedParams = dataProvider.getSpeedParams();
        final TrayIcon trayIcon = new TrayIcon(getDynamicIcon(speedParams[0], speedParams[1]));
        trayIcon.setImageAutoSize(false);

        trayMouseAdapter = new TrayMouseAdapter(this, trayIcon);
        trayIcon.addMouseListener(trayMouseAdapter);
        trayIcon.addMouseMotionListener(trayMouseAdapter);
        trayIconTooltip = new TrayIconTooltip();

        
        /* popup menu: */
        popupMenu = new TrayPopupMenu(validProperties);

        
        /* add tray icon to system tray: */
        sysTray.add(trayIcon);


        /* start update thread: */
        new Thread() {

            @Override
            public void run() {
                while (!this.isInterrupted()) {
                    try {
                        updateTray(trayIcon);
                        TimeUnit.MILLISECONDS.sleep(refreshInterval);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex); // will go up the ladder
                    }

                }
            }
        }.start();

    }

    /**
     * update tray icon and tooltip
     * 
     * @param trayIcon
     * @throws Exception
     */
    private void updateTray(final TrayIcon trayIcon) throws Exception {

        final float[] speedValues = dataProvider.getSpeedParams();

        trayIcon.setImage(getDynamicIcon(speedValues[0], speedValues[1]));

        // @TODO: optimize
        trayIconTooltip.setDownloadKBpS(speedValues[0]);
        trayIconTooltip.setUploadKBpS(speedValues[1]);
        trayIconTooltip.pack();
    }

    public void mouseStay(final MouseEvent e) {
        trayIconTooltip.showTooltip(((TrayMouseAdapter) e.getSource()).getEstimatedTopLeft());
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        trayIconTooltip.hideTooltip();
        if (e.isPopupTrigger()) {
            popupMenu.showPopup(e.getPoint());
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (e.isPopupTrigger()) {
            popupMenu.showPopup(e.getPoint());
        }
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        trayIconTooltip.hideTooltip();
    }

    /**
     * get tray icon image, based on speed values
     * 
     * @param currentDownKBpS
     * @param currentUpKBpS
     * @return the updated image showing the current speed graph
     */
    private Image getDynamicIcon(final float currentDownKBpS, final float currentUpKBpS) {
        return DynamicTrayImage.getDynamicSpeedImageV2(currentDownKBpS, currentUpKBpS, maxDownKBpS, maxUpKBpS);
    }
}
