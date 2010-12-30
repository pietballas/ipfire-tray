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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * dynamic tray image class:
 * get image showing the connection speed graphs
 *
 * @author apric
 */
public class DynamicTrayImage {

    static final int TRAY_SIZE_X = (int) SystemTray.getSystemTray().getTrayIconSize().getWidth();
    static final int TRAY_SIZE_Y = (int) SystemTray.getSystemTray().getTrayIconSize().getHeight();

    static final BufferedImage image = new BufferedImage(TRAY_SIZE_X, TRAY_SIZE_Y, Image.SCALE_SMOOTH);
    static final Graphics2D g = image.createGraphics();

    static final LinkedList<Float> downValuesQueue   = new LinkedList<Float>();
    static final LinkedList<Float> upValuesQueue     = new LinkedList<Float>();

    static {
        /* set graphics rendering hints: */
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setBackground(Color.DARK_GRAY);
    }


    /**
     * get image showing the connection speed graphs
     *
     * @param currentDownKBpS
     * @param currentUpKBpS
     * @param maxDownKBpS
     * @param maxUpKBpS
     * @return updated image the size of a system tray icon
     */
    public static Image getDynamicSpeedImageV2(float currentDownKBpS, float currentUpKBpS, float maxDownKBpS, float maxUpKBpS){

        downValuesQueue.add(currentDownKBpS);
        upValuesQueue.add(currentUpKBpS);

        /* remove old values no longer visible: */
        if (downValuesQueue.size() > TRAY_SIZE_X) {
            downValuesQueue.removeFirst();
            upValuesQueue.removeFirst();
        }

        /* set background: */
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, TRAY_SIZE_X, TRAY_SIZE_Y);

        /* draw download: */
        g.setColor(Color.GREEN);
        int rx_i = TRAY_SIZE_X - downValuesQueue.size();
        for (float downKBpS: downValuesQueue){
            if (downKBpS > 0){
                final int lineHeight = (int) Math.min(downKBpS / maxDownKBpS * TRAY_SIZE_Y, TRAY_SIZE_Y);
                g.drawLine( rx_i, TRAY_SIZE_Y,
                            rx_i, TRAY_SIZE_Y - lineHeight);
            }
            /* draw a yellow bar in case of connection problems: */
            else if (downKBpS < 0){
                g.setColor(Color.YELLOW);
                g.drawLine(rx_i, 0, rx_i, TRAY_SIZE_Y - 1);
                g.setColor(Color.GREEN); // set green again
            }

            rx_i++;
        }

        /* draw upload: */
        g.setColor(new Color(1, 0, 0, 0.8f)); // red color, slightly transparent
        int tx_i = TRAY_SIZE_X - upValuesQueue.size();
        int previousTx_y = TRAY_SIZE_Y - (int) Math.min(upValuesQueue.getFirst() / maxUpKBpS * TRAY_SIZE_Y, TRAY_SIZE_Y);
        for (float upKBpS: upValuesQueue){
            if (upKBpS > 0){
                final int currentTx_y = TRAY_SIZE_Y - (int) Math.min(upKBpS / maxUpKBpS * TRAY_SIZE_Y, TRAY_SIZE_Y);
                g.drawLine( tx_i - 1,   previousTx_y,
                            tx_i,       currentTx_y);
                previousTx_y = currentTx_y;
            }
            /* we don't need to draw a second yellow line here in case of connection problems, if upload < 0 then also download < 0 */

            tx_i++;
        }

        return image;
    }

    

}
