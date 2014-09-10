package common;

import common.annotations.NotNull;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.LinkedList;
import javax.swing.JComponent;

/**
 * This LayoutManager places components without any dependencies between each
 * other. Scaling the container will make every component scale proportionally.
 *
 * @author Michel Bartsch
 */
public class TotalScaleLayout implements LayoutManager
{
    /** The container to layout. */
    @NotNull
    private final Container parent;
    /** The containers preferred size. */
    @NotNull
    private final Dimension preferredSize;
    /** The containers minimum size. */
    @NotNull
    private final Dimension minimumSize;
    /** List of all components to layout on the container */
    private final LinkedList<TotalScaleComponent> comps = new LinkedList<TotalScaleComponent>();
    
    /**
     * Creates a new TotalScaleLayout.
     * 
     * @param parent     The container to layout.
     */
    public TotalScaleLayout(@NotNull Container parent)
    {
        this.parent = parent;
        preferredSize = parent.getPreferredSize();
        minimumSize = parent.getMinimumSize();
    }
    
    /**
     * This will add a Component to the container to be layout. Use this instead
     * of the add-method of the container itself.
     * 
     * @param x        Left-edge position on the container between 0 and 1.
     * @param y        Top-edge position on the container between 0 and 1.
     * @param width    Width on the container between 0 and 1.
     * @param height   Height on the container between 0 and 1.
     * @param comp     Component to be added.
     */
    public void add(double x, double y, double width, double height, @NotNull JComponent comp)
    {
        parent.add(comp);
        comps.add(new TotalScaleComponent(x, y, width, height, comp));
    }
    
    /**
     * Not supported because of own add-method.
     */
    @Override
    public void addLayoutComponent(@NotNull String name, @NotNull Component comp) {}

    /**
     * Gets called to remove Component.
     * 
     * @param comp      Component to remove.
     */
    @Override
    public void removeLayoutComponent(@NotNull Component comp)
    {
        for (TotalScaleComponent tscomp : comps) {
            if (tscomp.comp == comp) {
                comps.remove(tscomp);
                break;
            }
        }
    }

    /**
     * Because all of the Components`s sizes are adapted to the containers size
     * this will always return the size of the container when the layout was
     * created.
     * 
     * @param parent      Container this layout belongs to.
     * 
     * @return            This layouts preferred size.
     */
    @Override
    public Dimension preferredLayoutSize(@NotNull Container parent)
    {
        return preferredSize;
    }

    /**
     * Because all of the Components`s sizes are adapted to the containers size
     * this will always return the size of the container when the layout was
     * created.
     * 
     * @param parent      Container this layout belongs to.
     * 
     * @return            This layouts minimum size.
     */
    @Override
    public Dimension minimumLayoutSize(@NotNull Container parent)
    {
        return minimumSize;
    }

    /**
     * This gets called automatically to adapt position and size of all
     * components.
     * 
     * @param parent      Container to layout.
     */
    @Override
    public void layoutContainer(@NotNull Container parent)
    {
        Rectangle parentBounds = parent.getBounds();
        for (TotalScaleComponent comp : comps) {
            comp.comp.setBounds(
                    (int)(comp.x*parentBounds.width),
                    (int)(comp.y*parentBounds.height),
                    (int)(comp.width*parentBounds.width),
                    (int)(comp.height*parentBounds.height));
        }
    }
    
    /**
    * This class simply wraps some attributes like a struct.
    */
    class TotalScaleComponent
    {
        /** Left-edge position on the container between 0 and 1. */
        final double x;
        /** Top-edge position on the container between 0 and 1. */
        final double y;
        /** Width on the container between 0 and 1. */
        final double width;
        /** Height on the container between 0 and 1. */
        final double height;
        /** Component to be laid out on the container. */
        @NotNull
        final Component comp;
        
        /**
        * Creates a new TotalScaleComponent.
        * 
        * @param x        Left-edge position on the container between 0 and 1.
        * @param y        Top-edge position on the container between 0 and 1.
        * @param width    Width on the container between 0 and 1.
        * @param height   Height on the container between 0 and 1.
        * @param comp     Component to be laid out on the container.
        */
        TotalScaleComponent(double x, double y, double width, double height, @NotNull Component comp)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.comp = comp;
        }
    }
}