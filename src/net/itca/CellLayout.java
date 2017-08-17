package net.itca;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmeeus1 on 9-8-2017.
 */
public class CellLayout implements LayoutManager2 {

    private int rows;
    private int cols;
    private Map<Component, CellLocation> compLocationMap; // map of the components added, and their locations..

    private Rectangle rect; // the rectangle of the owning component

    // Indicates wether or ont the size of the cells is known to the last known size of the container
    // If the container has been resized, dirty == true and we need to recalculate the layout of the components
    protected boolean dirty;

    public CellLayout(int rows, int cols) {
        if (rows < 0 || cols < 0) {
            throw new RuntimeException("Invalid params");
        }
        this.rows = rows;
        this.cols = cols;
        compLocationMap = new HashMap<Component, CellLocation>();
    }

    @Override
    public void addLayoutComponent(Component component, Object constraint) {
        // we divide the entire layout in the cells that we have available to us..
        // then we assign the component to the location in the constraints. It might cover multiple cells
        System.out.println("adding component");
        if (!(constraint instanceof CellLocation)) {
            throw new RuntimeException("Yo mate, this should really be a CellLayout.");
        }
        CellLocation requestedLocation = (CellLocation) constraint;
        compLocationMap.put(component, requestedLocation);
    }

    @Override
    public Dimension maximumLayoutSize(Container container) {
        return null;
    }

    @Override
    public float getLayoutAlignmentX(Container container) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container container) {
        return 0;
    }

    @Override
    public void invalidateLayout(Container container) {

    }

    @Override
    public void addLayoutComponent(String s, Component component) {

    }

    @Override
    public void removeLayoutComponent(Component component) {
        compLocationMap.remove(component);
    }

    @Override
    public Dimension preferredLayoutSize(Container container) {
        return null;
    }

    @Override
    public Dimension minimumLayoutSize(Container container) {
        return null;
    }

    private int getCellWidth(){
        return rect.width / cols;
    }

    private int getCellHeight(){
        return rect.height / rows;
    }

    @Override
    public void layoutContainer(Container container) {
        // this is called once the JPanel using this LayoutManager has changed size

        // find out the bounds in which we work..
        final int x = container.getX();
        final int y = container.getY();
        final int width = container.getWidth();
        final int height = container.getHeight();
        rect = new Rectangle(x, y, width, height);
        final Component[] components = container.getComponents();

        for (Component component : components) {
            final CellLocation cellLocation = compLocationMap.get(component);
            // should never be null because we will throw exceptions!
            final Point startPos = getStartPosition(cellLocation.startCell);
            final Point endPos = getEndPosition(cellLocation.endCell);

            int startX = startPos.x;
            int startY = startPos.y;
            int w = endPos.x - startPos.x;
            int h = endPos.y - startPos.y;

            System.out.println(String.format("startx: %d - starty: %d\n endx: %d - endy: %d",startX,startY,w,h));

            component.setBounds(startX, startY, w, h);
        }
    }

    /**
     * Gets the upper left corner of the cell..
     * @param cell
     * @param rect
     * @return
     */
    private Point getStartPosition(int cell){
        // divide the total rectangle in terms of cells/rows
        // todo: move this to one location in its own cosy method.

        int x = rect.x;
        int y = rect.y;
        int w = rect.width;
        int h = rect.height;

        int sectionWidth = w / cols;
        int sectionHeight = h / cols;

        // determine the cell by moving a point along..
        // essentially, we are going to trace an intersection :-)
        int startX = (cell % cols) * sectionWidth;
        int startY = (cell / rows) * sectionHeight; // integer division will floor for us.
        return new Point(startX, startY);
    }


    /**
     * Gets the bottom right corner of the cell..
     * We do this by finding the start position, and calculating the width/height of the cell and adjusting for this
     * @param cell
     * @param rectangle
     * @return
     */
    private Point getEndPosition(int cell) {
        final Point startPoint = getStartPosition(cell);
        int x = startPoint.x;
        int y = startPoint.y;

        int xSectionWidth = rect.width / cols;
        int ySectionWith = rect.height / cols;

        return new Point(x + xSectionWidth, y + ySectionWith);
    }

    // get the corner rectangles for a component
    public CornerRectangle[] getCornerRectangles(Component component) {
        final CellLocation compLocation = compLocationMap.get(component);

        final int cellWidth = getCellWidth();
        final int cellHeight = getCellHeight();

        final int rectangleWidth = cellWidth / 4;
        final int rectangleHeight = cellHeight / 4;


        // we need to get the 4 points made up by this component
        // the start cell is the first one.
        CellLocation cellLocation = compLocationMap.get(component);
        Point leftTop = getStartPosition(cellLocation.getStartCell());
        Point rightBottom= getEndPosition(cellLocation.getEndCell());
        // from this we can determine the missing corners
        Point rightTop = new Point(rightBottom.x, leftTop.y);
        Point leftBottom = new Point(leftTop.x, rightBottom.y);


        Rectangle r1 = new Rectangle(leftTop.x, leftTop.y, rectangleWidth, rectangleHeight);
        Rectangle r2 = new Rectangle(rightTop.x - rectangleWidth, rightTop.y, rectangleWidth, rectangleHeight);
        Rectangle r3 = new Rectangle(leftBottom.x, leftBottom.y - rectangleHeight, rectangleWidth, rectangleHeight);
        Rectangle r4 = new Rectangle(rightBottom.x - rectangleWidth, rightBottom.y - rectangleHeight, rectangleWidth, rectangleHeight);

        // Assign the rectangles to the appropriate corner
        CornerRectangle cr1 = new CornerRectangle(CORNER.LT, r1);
        CornerRectangle cr2 = new CornerRectangle(CORNER.RT, r2);
        CornerRectangle cr3 = new CornerRectangle(CORNER.LB, r3);
        CornerRectangle cr4 = new CornerRectangle(CORNER.RB, r4);


        return new CornerRectangle[]{cr1, cr2, cr3, cr4};

    }


    /**
     *
     *    R1_____________R2
     *      |__|      |__|
     *      |            |
     *      |__        __|
     *   R3 |__|______|__|R4
     *
     * @param cell
     * @return
     */
    public CornerRectangle[] getCornerRectangles(int cell){

        final Point p1 = getStartPosition(cell);
        final Point p4 = getEndPosition(cell);

        // from this, we can determine r2 and r4
        Point p2 = new Point(p4.x, p1.y);
        Point p3 = new Point(p1.x, p4.y);

        // now create small rectangles around the corners..
        final int cellWidth = getCellWidth();
        final int cellHeight = getCellHeight();

        // height and with for the corner rectangles. Might have to play with these settings.
        final int rectangleWidth = cellWidth / 4;
        final int rectangleHeight = cellHeight / 4;

        Rectangle r1 = new Rectangle(p1.x, p1.y, rectangleWidth, rectangleHeight);
        Rectangle r2 = new Rectangle(p2.x - rectangleWidth , p2.y, rectangleWidth, rectangleHeight);
        Rectangle r3 = new Rectangle(p3.x, p3.y - rectangleHeight, rectangleWidth, rectangleHeight);
        Rectangle r4 = new Rectangle(p4.x - rectangleWidth, p4.y - rectangleHeight, rectangleWidth, rectangleHeight);
        CornerRectangle cr1 = new CornerRectangle(CORNER.LT, r1);
        CornerRectangle cr2 = new CornerRectangle(CORNER.RT, r2);
        CornerRectangle cr3 = new CornerRectangle(CORNER.LB, r3);
        CornerRectangle cr4 = new CornerRectangle(CORNER.RB, r4);
        return new CornerRectangle[] {cr1, cr2, cr3, cr4};
    }

    public int getCellForLocation(Point location){

        int xLocation = location.x;
        int yLocation = location.y;

        int x = rect.x;
        int y = rect.y;
        int w = rect.width;
        int h = rect.height;

        int cellWidth = rect.width / cols;
        int cellHeight = rect.height / rows;

        int cellX = location.x / cellWidth; // this is the index in the columns

        // we need to adjust it for the amount of rows that have passed.
        // so that is: cellLocation = cellX + (rows passed * cells per row)
        int actualCell = cellX + (cols * (yLocation / cellHeight));

        return actualCell;
    }

    public CellLocation getCellLocation(Component component) {
        return compLocationMap.get(component);
    }

    /**
     * Resize the component based on the drop point + which corner was dragged..
     * @param component
     * @param mouseDropPoint
     * @param cornerRectangle
     */
    public void resizeComponent(Component component, Point dropPoint, CornerRectangle cornerRectangle) {
        // determine the grabbed corner
        // find the end location
        // determine which action to take
        final CORNER corner = cornerRectangle.getCorner();
        switch (corner) {
            case LT:
                resizeFromLeftTop(component, dropPoint);
                break;
            case RT:
                resizeFromRightTop(component, dropPoint);
                break;
            case LB:
                resizeFromLeftBottom(component, dropPoint);
                break;
            case RB:
                resizeFromRightBottom(component, dropPoint);
                break;
        }
    }

    private void resizeFromLeftBottom(Component component, Point dropPoint) {
        final int dropCell = getCellForLocation(dropPoint);
        final CellLocation cellLocation = compLocationMap.get(component);


        final int startCell = cellLocation.getStartCell();
        final Point startPosition = getStartPosition(startCell);
        final Point dropPosition = getStartPosition(dropCell);

        // change the start cell (new x position)
        final Point alteredStartPosition = new Point(dropPoint.x, startPosition.y);
        final int newStartCell = getCellForLocation(alteredStartPosition);
        cellLocation.setStartCell(newStartCell);

        // change the end cell (new y position)
        final int endCell = cellLocation.getEndCell();
        final Point endPosition = getStartPosition(endCell); // we get the upper-left corner of the last cell (otherwise we stretch too far)
        final Point altereEndPosition = new Point(endPosition.x, dropPoint.y);
        final int newEndCell = getCellForLocation(altereEndPosition);
        cellLocation.setEndCell(newEndCell);

    }

    private void resizeFromRightTop(Component component, Point dropPoint) {
        // figure out how to change the start / end cell.
        final int dropCell = getCellForLocation(dropPoint);
        final CellLocation cellLocation = compLocationMap.get(component);

        // find out how the dropcell relates to the cellLocation cells..

        // change our start position
        final int startCell = cellLocation.getStartCell();
        final Point startPosition = getStartPosition(startCell);
        final Point dropPosition = getStartPosition(dropCell);

        final Point alteredStartPosition = new Point(startPosition.x, dropPosition.y);
        final int newStartCell = getCellForLocation(alteredStartPosition);
        cellLocation.setStartCell(newStartCell);

        // change our end position
        final int endCell = cellLocation.getEndCell();
        final Point endPosition = getStartPosition(endCell);
        final Point alteredEndPosition = new Point(dropPoint.x, endPosition.y);
        final int newEndCell = getCellForLocation(alteredEndPosition);
        cellLocation.setEndCell(newEndCell);

    }

    private void resizeFromRightBottom(Component component, Point dropPoint) {
        final int dropCell = getCellForLocation(dropPoint);
        final CellLocation cellLocation = compLocationMap.get(component);
        cellLocation.setEndCell(dropCell);
    }

    private void resizeFromLeftTop(Component component, Point dropPoint) {
        final int dropCell = getCellForLocation(dropPoint);
        final CellLocation cellLocation = compLocationMap.get(component);
        cellLocation.setStartCell(dropCell);
    }

    public void moveComponent(Component component, Point dropPoint) {
        final CellLocation currentLocation = compLocationMap.get(component);
        final int newStartCell = getCellForLocation(dropPoint);
        System.out.println(newStartCell);
        // assume that the  cell is of size 1 to start with.
        int newEndCell =  newStartCell;
        // find the size of the cell, to restore the size after moving
        if (currentLocation.getStartCell() != currentLocation.getEndCell()) {
            int diff = currentLocation.getEndCell() - currentLocation.getStartCell();
            newEndCell = newStartCell + diff;
        }

        removeLayoutComponent(component);
        final CellLocation newLocation = new CellLocation(newStartCell, newEndCell);
        addLayoutComponent(component, newLocation);
    }

    public static class CellLocation{
        private int startCell;
        private int endCell;

        public CellLocation(int start,int end) {
            this.startCell = start;
            this.endCell = end;
        }

        public int getStartCell(){
            return startCell;
        }

        public int getEndCell(){
            return endCell;
        }

        public void setStartCell(int cell) {
            this.startCell = cell;
        }

        public void setEndCell(int cell) {
            this.endCell = cell;
        }
    }

    enum CORNER{
        LT, // Left-top
        RT, // Right-top
        LB, // Left-botton
        RB; // Right-bottom
    }

    static class CornerRectangle{
        private CORNER corner;
        private Rectangle rectangle;
        public CornerRectangle(CORNER corner, Rectangle rectangle) {
            this.corner = corner;
            this.rectangle = rectangle;
        }
        public Rectangle getRectangle(){
            return rectangle;
        }

        public CORNER getCorner() {
            return corner;
        }
    }

    // Stretch + MouseLocation work together for the extend/shrink function
    // MouseLocation will indicate the 'icon' to notify the user that shrink/extend kcan start
    // MouseInteraction will do the action of actually stretching / shrinking / moving the component :-)
    class MouseLocationListener implements MouseMotionListener{

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {

        }

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
            // draw imaginary rectangles..
            // get the current cell and the bounds
            final Point mouseLocation = mouseEvent.getPoint();
            final int cell = getCellForLocation(mouseLocation);
            final CornerRectangle[] rects = getCornerRectangles(cell);
            final JComponent component = (JComponent) (mouseEvent.getSource());
            boolean inSomeRectangle = false;
            for (CornerRectangle rect : rects) {
                if (rect.getRectangle().contains(mouseLocation)) {
                    component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    inSomeRectangle = true;
                }
            }
            if (!inSomeRectangle) {
                component.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    class MouseInteractionListener implements MouseListener{

        private Component grabbedComponent;
        private CornerRectangle grabbedCorner;

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {

        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            // get the cornerRectangle

            // release the old data
            grabbedComponent = null;
            grabbedCorner = null;

            final Point mouseLocation = mouseEvent.getPoint();
            final int cell = getCellForLocation(mouseLocation);
            final CornerRectangle[] rects = getCornerRectangles(cell);
            CornerRectangle cornerUnderMouse = null;
            for (CornerRectangle rect : rects) {
                if (rect.getRectangle().contains(mouseLocation)) {
                    cornerUnderMouse = rect;
                }
            }

            grabbedComponent = (JComponent) mouseEvent.getSource();
            if (cornerUnderMouse == null) {
                // move the component
            } else {
                // resize the component
                grabbedCorner = cornerUnderMouse;
            }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            final JComponent component = (JComponent) mouseEvent.getSource();
            final Point mouseLocation = mouseEvent.getPoint();
            if (grabbedCorner == null) {
                moveComponent(component, mouseLocation);
            } else {
                resizeComponent(component, mouseLocation, grabbedCorner);
            }
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {

        }
    }

}
















