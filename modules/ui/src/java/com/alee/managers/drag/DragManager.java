/*
 * This file is part of WebLookAndFeel library.
 *
 * WebLookAndFeel library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WebLookAndFeel library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with WebLookAndFeel library.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.alee.managers.drag;

import com.alee.log.Log;
import com.alee.managers.glasspane.GlassPaneManager;
import com.alee.managers.glasspane.WebGlassPane;
import com.alee.utils.SwingUtils;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * This manager simplifies dragged items visual representation creation.
 * You can add customized representation support for DataFlavor by registering new DragViewHandler.
 * So far custom DataFlavor view will be displayed only within application window bounds.
 *
 * @author Mikle Garin
 */

public final class DragManager
{
    /**
     * todo 1. Move dragged object display to a separate transparent non-focusable window
     */

    /**
     * Drag view handlers map.
     */
    private static Map<DataFlavor, DragViewHandler> viewHandlers;

    /**
     * Dragged object representation variables.
     */
    private static WebGlassPane glassPane;
    private static Object data;
    private static BufferedImage view;
    private static DragViewHandler dragViewHandler;

    /**
     * Whether manager is initialized or not.
     */
    private static boolean initialized = false;

    /**
     * Initializes manager if it wasn't already initialized.
     */
    public static void initialize ()
    {
        // To avoid more than one initialization
        if ( !initialized )
        {
            // Remember that initialization happened
            initialized = true;

            // View handlers map
            viewHandlers = new HashMap<DataFlavor, DragViewHandler> ();

            final DragSourceAdapter dsa = new DragSourceAdapter ()
            {
                @Override
                public void dragEnter ( final DragSourceDragEvent dsde )
                {
                    // Deciding on enter what to display for this kind of data
                    final Transferable transferable = dsde.getDragSourceContext ().getTransferable ();
                    final DataFlavor[] flavors = transferable.getTransferDataFlavors ();
                    for ( final DataFlavor flavor : flavors )
                    {
                        if ( viewHandlers.containsKey ( flavor ) )
                        {
                            try
                            {
                                data = transferable.getTransferData ( flavor );
                                dragViewHandler = viewHandlers.get ( flavor );
                                view = dragViewHandler.getView ( data );

                                glassPane = GlassPaneManager.getGlassPane ( dsde.getDragSourceContext ().getComponent () );
                                glassPane.setPaintedImage ( view, getLocation ( glassPane ) );

                                break;
                            }
                            catch ( final Throwable e )
                            {
                                Log.error ( DragManager.class, e );
                            }
                        }
                    }
                }

                @Override
                public void dragMouseMoved ( final DragSourceDragEvent dsde )
                {
                    // Move displayed data
                    if ( view != null )
                    {
                        final WebGlassPane gp = GlassPaneManager.getGlassPane ( dsde.getDragSourceContext ().getComponent () );
                        if ( gp != glassPane )
                        {
                            glassPane.clearPaintedImage ();
                            glassPane = gp;
                        }
                        gp.setPaintedImage ( view, getLocation ( gp ) );
                    }
                }

                public Point getLocation ( final WebGlassPane gp )
                {
                    final Point mp = SwingUtils.getMousePoint ( gp );
                    final Point vp = dragViewHandler.getViewRelativeLocation ( data );
                    return new Point ( mp.x + vp.x, mp.y + vp.y );
                }

                @Override
                public void dragDropEnd ( final DragSourceDropEvent dsde )
                {
                    // Cleanup displayed data
                    if ( view != null )
                    {
                        glassPane.clearPaintedImage ();
                        glassPane = null;
                        data = null;
                        view = null;
                        dragViewHandler = null;
                    }
                }
            };
            DragSource.getDefaultDragSource ().addDragSourceListener ( dsa );
            DragSource.getDefaultDragSource ().addDragSourceMotionListener ( dsa );
        }
    }

    /**
     * Registers new DragViewHandler.
     *
     * @param dragViewHandler DragViewHandler to register
     */
    public static void registerViewHandler ( final DragViewHandler dragViewHandler )
    {
        viewHandlers.put ( dragViewHandler.getObjectFlavor (), dragViewHandler );
    }

    /**
     * Unregisters new DragViewHandler.
     *
     * @param dragViewHandler DragViewHandler to unregister
     */
    public static void unregisterViewHandler ( final DragViewHandler dragViewHandler )
    {
        viewHandlers.remove ( dragViewHandler.getObjectFlavor () );
    }
}