package net.itca;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Main {

    private JPanel panel;

    public static void main(String[] args) {
        Main m = new Main();
    }

    public Main(){
        setup();
    }

    private void setup(){
        System.out.println("performing setup");
        final int rows = 20;
        final int cols = 20;
        final CellLayout cellLayout = new CellLayout(rows, cols);
        panel = new JPanel(cellLayout){
            /**
             * override the paint method to show the cells
             * @param g
             */
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                final Rectangle rect = g.getClipBounds();
                int x = rect.x;
                int y = rect.y;
                int width = rect.width;
                int height = rect.height;

                try {
                    int xSectionWidth = width / cols;
                    int ySectionHeight = height / rows;
                    g.setColor(Color.MAGENTA);

                    for (int col = 1; col < cols; col++) {
                        // the first one we do not draw, neither the last one
                        final int drawWidth = col * xSectionWidth;
                        g.drawLine(drawWidth, 0, drawWidth, height);
                    }

                    for (int row = 1; row < rows; row++) {
                        final int drawHeight = row * ySectionHeight;
                        g.drawLine(0, drawHeight, width, drawHeight);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    // always clean up..
                    g.dispose();
                }


            }
        };


        JPanel otherPanel = new JPanel(new BorderLayout());
        otherPanel.setBackground(Color.ORANGE);
        panel.add(otherPanel, new CellLayout.CellLocation(16, 16));
        panel.addMouseListener(new MouseInteractionListener());
        panel.addMouseMotionListener(new MouseLocationListener());

//        JPanel anOtherPanel = new JPanel(new BorderLayout());
//        anOtherPanel.setBackground(Color.RED);
//        panel.add(anOtherPanel, new CellLayout.CellLocation(8, 14));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.setSize(1000, 1000);
        frame.setVisible(true);


    }


    class MouseLocationListener implements MouseMotionListener{

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            // show symbols for the corner rectangles.
            // so we know that we can stretch and shrink the component
            final Point mouseLocation = mouseEvent.getPoint();
            final Component componentUnderMouse = panel.getComponentAt(mouseLocation);
            if (componentUnderMouse == null) {
                return;
            }

            final CellLayout layout = (CellLayout) panel.getLayout();
            final int cell = layout.getCellForLocation(mouseLocation);
            final CellLayout.CornerRectangle[] rects = layout.getCornerRectangles(cell);
            boolean inSomeRectangle = false;
            for (CellLayout.CornerRectangle rect : rects) {
                if (rect.getRectangle().contains(mouseLocation)) {
                    panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    inSomeRectangle = true;
                }
            }
            if (!inSomeRectangle) {
                panel.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    class MouseInteractionListener implements MouseListener {
        Component grabbedComponent = null;
        CellLayout.CornerRectangle grabbedCorner = null;
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {

        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            grabbedCorner = null; // we do not yet know if we have a corner.

            final Component component = panel.getComponentAt(mouseEvent.getPoint());
            if (component == null) {
                return;
            }

            grabbedComponent = component;

            // determine if we have a corner
            final Point mouseLocation = mouseEvent.getPoint();
            final CellLayout cellLayout = (CellLayout) panel.getLayout();
            final int cell = cellLayout.getCellForLocation(mouseLocation);
            final CellLayout.CornerRectangle[] rects = cellLayout.getCornerRectangles(cell);
            CellLayout.CornerRectangle cornerUnderMouse = null;
            for (CellLayout.CornerRectangle rect : rects) {
                if (rect.getRectangle().contains(mouseLocation)) {
                    grabbedCorner = rect;
                    break;
                }
            }

            ((JComponent) mouseEvent.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            if (grabbedComponent == null || grabbedComponent == panel) {
                ((JComponent) mouseEvent.getSource()).setCursor(Cursor.getDefaultCursor());
                return;
            }

            // drop it like it's hot.
            final LayoutManager layout = panel.getLayout();
            if (!(layout instanceof CellLayout)) {
                return; // Don't even.
            }

            final CellLayout cellLayout = (CellLayout) layout;

            final Point mouseLocation = mouseEvent.getPoint();
            if (grabbedCorner == null) {
                cellLayout.moveComponent(grabbedComponent, mouseLocation);
            } else {
                cellLayout.resizeComponent(grabbedComponent, mouseLocation, grabbedCorner);
            }


            ((JComponent) mouseEvent.getSource()).setCursor(Cursor.getDefaultCursor());
            panel.revalidate();
            panel.updateUI();
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    }



}