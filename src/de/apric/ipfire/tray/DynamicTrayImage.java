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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.image.BufferedImage;
import java.util.Deque;
import java.util.LinkedList;

/**
 * dynamic tray image class:
 * get image showing the connection speed graphs
 *
 * @author apric
 */
public class DynamicTrayImage {

    public static final int TRAY_SIZE_X = (int) SystemTray.getSystemTray().getTrayIconSize().getWidth();
    public static final int TRAY_SIZE_Y = (int) SystemTray.getSystemTray().getTrayIconSize().getHeight();

    private static final BufferedImage IMAGE = new BufferedImage(TRAY_SIZE_X, TRAY_SIZE_Y, Image.SCALE_SMOOTH);
    private static final Graphics2D G2D = IMAGE.createGraphics();

    private static final Deque<Float> DOWNVALUES    = new LinkedList<Float>();
    private static final Deque<Float> UPVALUES      = new LinkedList<Float>();

    static {
        /* set graphics rendering hints: */
        G2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        G2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        G2D.setBackground(Color.DARK_GRAY);
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
    public static Image getDynamicSpeedImageV2(final float currentDownKBpS, final float currentUpKBpS, final float maxDownKBpS, final float maxUpKBpS) {

        DOWNVALUES.add(currentDownKBpS);
        UPVALUES.add(currentUpKBpS);

        /* remove old values no longer visible: */
        if (DOWNVALUES.size() > TRAY_SIZE_X) {
            DOWNVALUES.removeFirst();
            UPVALUES.removeFirst();
        }

        /* set background: */
        G2D.setColor(Color.DARK_GRAY);
        G2D.fillRect(0, 0, TRAY_SIZE_X, TRAY_SIZE_Y);

        /* draw download: */
        G2D.setColor(Color.GREEN);
        int rx_i = TRAY_SIZE_X - DOWNVALUES.size();
        for (float downKBpS : DOWNVALUES) {
            if (downKBpS > 0) {
                final int lineHeight = (int) Math.min(downKBpS / maxDownKBpS * TRAY_SIZE_Y, TRAY_SIZE_Y);
                G2D.drawLine(rx_i, TRAY_SIZE_Y,
                            rx_i, TRAY_SIZE_Y - lineHeight);
            }
            /* draw a yellow bar in case of connection problems: */
            else if (downKBpS < 0) {
                G2D.setColor(Color.YELLOW);
                G2D.drawLine(rx_i, 0, rx_i, TRAY_SIZE_Y - 1);
                G2D.setColor(Color.GREEN); // set green again
            }

            rx_i++;
        }

        /* draw upload: */
        G2D.setColor(new Color(1, 0, 0, 0.8f)); // red color, slightly transparent
        int tx_i = TRAY_SIZE_X - UPVALUES.size();
        int previousTx_y = TRAY_SIZE_Y - (int) Math.min(UPVALUES.getFirst() / maxUpKBpS * TRAY_SIZE_Y, TRAY_SIZE_Y);
        for (float upKBpS : UPVALUES) {
            if (upKBpS > 0) {
                final int currentTx_y = TRAY_SIZE_Y - (int) Math.min(upKBpS / maxUpKBpS * TRAY_SIZE_Y, TRAY_SIZE_Y);
                G2D.drawLine(tx_i - 1,   previousTx_y,
                            tx_i,       currentTx_y);
                previousTx_y = currentTx_y;
            }
            /* we don't need to draw a second yellow line here in case of connection problems, if upload < 0 then also download < 0 */

            tx_i++;
        }

        return IMAGE;
    }


}
