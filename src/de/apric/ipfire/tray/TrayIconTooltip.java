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
 * JDownloader "jdtrayicon" plugin: <http://jdownloader.org>
 */


package de.apric.ipfire.tray;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * the small tooltip being displayed when hovering over the tray icon
 * shows the current speed values
 *
 * @author apric
 */
public final class TrayIconTooltip extends JWindow {

    private Point point;

    private JLabel titleLabel;
    private JLabel downLabel;
    private JLabel upLabel;
    private JLabel downValue;
    private JLabel upValue;
    private JLabel downKBpSLabel;
    private JLabel upKBpSLabel;

    /**
     * create the tooltip (a JWindow)
     */
    public TrayIconTooltip() {

        super();

        final JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.setOpaque(true);
        panel.setBackground(new Color(0xb9cee9));
        panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, panel.getBackground().darker()));

        addContent(panel);

        this.setVisible(false);
        this.setAlwaysOnTop(true);
        this.add(panel);

        this.pack();
    }

    /**
     * display the tooltip
     *
     * @param point the point the mouse event occurred
     */
    public void showTooltip(final Point p) {

        this.point = p;

        adjustLocation();
        setVisible(true);
        toFront();
    }

    /**
     * hide tooltip
     */
    public void hideTooltip() {
        new Runnable() {

            @Override
            public void run() {
                if (isVisible()) {
                    setVisible(false);
                }
            }
        }.run();
    }


    /**
     * set tooltip location automatically
     */
    private void adjustLocation() {
        new Runnable() {

            @Override
            public void run() {
                final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                final int limitX = (int) screenSize.getWidth() / 2;
                final int limitY = (int) screenSize.getHeight() / 2;
                final Point pp = point;
                if (pp.x <= limitX) {
                    if (pp.y <= limitY) {
                        setLocation(pp.x, pp.y);
                    } else {
                        setLocation(pp.x, pp.y - getHeight());
                    }
                } else {
                    if (pp.y <= limitY) {
                        setLocation(pp.x - getWidth(), pp.y);
                    } else {
                        setLocation(pp.x - getWidth(), pp.y - getHeight());
                    }
                }
            }
        }.run();
    }

    /**
     * add components to panel
     * @param panel
     */
    private void addContent(final JPanel panel) {

        /* Netbeans Matisse generated code: */

        titleLabel = new javax.swing.JLabel();
        downLabel = new javax.swing.JLabel();
        upLabel = new javax.swing.JLabel();
        downValue = new javax.swing.JLabel();
        upValue = new javax.swing.JLabel();
        downKBpSLabel = new javax.swing.JLabel();
        upKBpSLabel = new javax.swing.JLabel();

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        titleLabel.setText("IPFireTray");

        downLabel.setText("down:");
        upLabel.setText("up:");

        downValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        downValue.setText("jLabel4");

        upValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        upValue.setText("jLabel5");

        downKBpSLabel.setText("KB/s");
        upKBpSLabel.setText("KB/s");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
        panel.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addGap(51, 51, 51))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(downLabel)
                            .addComponent(upLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(upValue, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(upKBpSLabel))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(downValue, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(downKBpSLabel)))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(titleLabel)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(downLabel)
                    .addComponent(downKBpSLabel)
                    .addComponent(downValue))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(upLabel)
                    .addComponent(upKBpSLabel)
                    .addComponent(upValue))
                .addContainerGap())
        );
    }

    /**
     * set "title" label
     * @param title
     */
    public void setTitle(final String title) {
        this.titleLabel.setText(title);
    }

    /**
     * set "down" value
     * @param download
     */
    public void setDownloadKBpS(final float download) {
        if (download >= 0) {
            downValue.setText(String.format("%5.1f", download));
        }
        else {
            downValue.setText("n/a");
        }
    }

    /**
     * set "up" value
     * @param upload
     */
    public void setUploadKBpS(final float upload) {
        if (upload >= 0) {
            upValue.setText(String.format("%5.1f", upload));
        }
        else {
            upValue.setText("n/a");
        }
    }
}
