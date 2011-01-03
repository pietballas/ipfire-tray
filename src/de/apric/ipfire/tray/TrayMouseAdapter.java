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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author apric
 */
public final class TrayMouseAdapter extends MouseAdapter {

        private final IPFireTray delegate;
        private boolean mouseover;
        private Thread mouseLocationObserver;
        // private TrayIcon trayIcon;
        private Point min;
        private Point max;
        private final Dimension size;
        private MouseEvent lastEvent;
        private final Component dummy;
        private static final int TOOLTIP_DELAY = 300;

        public TrayMouseAdapter(final IPFireTray ipfireTray, final TrayIcon trayIcon) {

            super();

            delegate = ipfireTray;
            dummy = new Component() {
                private static final long serialVersionUID = 1L;
            };
            size = trayIcon.getSize();
        }

        @Override
        public void mouseClicked(final MouseEvent e) {
            delegate.mouseClicked(e);
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            mouseover = true;
            final long enterTime = System.currentTimeMillis();
            mouseLocationObserver = new Thread() {

                @Override
                public void run() {
                    try {
                        boolean mouseStay = false;
                        MouseEvent me = null;

                        while (true) {
                            final Point point = MouseInfo.getPointerInfo().getLocation();
                            
                            if (isOver(point)) {
                                if ((System.currentTimeMillis() - enterTime) >= TOOLTIP_DELAY && !mouseStay) {
                                    mouseStay = true;
                                    me = new MouseEvent(dummy, 0, System.currentTimeMillis(), 0, point.x, point.y, 0, false);
                                    me.setSource(TrayMouseAdapter.this);
                                    delegate.mouseStay(me);
                                }
                            } else {
                                me = new MouseEvent(dummy, 0, System.currentTimeMillis(), 0, point.x, point.y, 0, false);
                                me.setSource(lastEvent.getSource());
                                mouseExited(me);

                                return;
                            }

                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
//                        e.printStackTrace();
                        return;
                    } finally {
                        mouseLocationObserver = null;
                    }
                }
            };
            mouseLocationObserver.start();

            delegate.mouseEntered(e);

        }

        @Override
        public void mouseExited(final MouseEvent e) {
            mouseover = false;
            min = null;
            max = null;
            delegate.mouseExited(e);
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            delegate.mousePressed(e);

        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            delegate.mouseReleased(e);

        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            delegate.mouseDragged(e);

        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            lastEvent = e;
            /**
             * the more the user moves over the tray, the better we know it's
             * location *
             */
            if (this.min == null) {
                this.min = new Point(e.getPoint().x, e.getPoint().y);
                this.max = new Point(e.getPoint().x, e.getPoint().y);
            } else {
                min.x = Math.min(e.getPoint().x, min.x);
                min.y = Math.min(e.getPoint().y, min.y);
                max.x = Math.max(e.getPoint().x, max.x);
                max.y = Math.max(e.getPoint().y, max.y);
                // System.out.println(min+" - "+max);
            }

            if (this.mouseover) {
                delegate.mouseMoved(e);
            } else {
                this.mouseEntered(e);
            }
        }

        public Point getEstimatedTopLeft() {
            final int midx = (max.x + min.x) / 2;
            final int midy = (max.y + min.y) / 2;

            return new Point(midx - size.width / 2, midy - size.height / 2);
        }

        /**
         * Passt die iconsize in die festgestellte geschätzte position ein. und
         * prüft ob point darin ist
         * 
         * @param point
         * @return
         */
        protected boolean isOver(final Point point) {
            final int midx = (max.x + min.x) / 2;
            final int midy = (max.y + min.y) / 2;

            final int width = Math.min(size.width, max.x - min.x);
            final int height = Math.min(size.height, max.y - min.y);

            final int minx = midx - width / 2;
            final int miny = midy - height / 2;
            final int maxx = midx + width / 2;
            final int maxy = midy + height / 2;
            
            if (point.x >= minx && point.x <= maxx
                && point.y >= miny && point.y <= maxy) {
                    return true;
            }
            
            return false;
        }
    }
