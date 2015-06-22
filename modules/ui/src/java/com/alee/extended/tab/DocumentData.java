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

package com.alee.extended.tab;

import com.alee.managers.language.LM;

import javax.swing.*;
import java.awt.*;

/**
 * This class represents basic data for single document opened in WebDocumentPane.
 *
 * @author Mikle Garin
 * @see <a href="https://github.com/mgarin/weblaf/wiki/How-to-use-WebDocumentPane">How to use WebDocumentPane</a>
 * @see com.alee.extended.tab.WebDocumentPane
 * @see com.alee.extended.tab.PaneData
 */

public class DocumentData
{
    /**
     * todo 1. DocumentData listeners to update view properly
     */

    /**
     * Document ID.
     * This ID have to be unique only within single WebDocumentPane instance.
     */
    protected String id;

    /**
     * Document icon.
     * Used to display icon on the document tab.
     */
    protected Icon icon;

    /**
     * Document title.
     * This can be a plain text or a language key.
     * Whether this is a plain text or a language key will be determined automatically, you only have to provide it.
     */
    protected String title;

    /**
     * Document tab background color.
     * Used to color tab and tab content background.
     */
    protected Color background;

    /**
     * Whether this document is closeable or not.
     * All documents are closeable by default, but you may change that.
     */
    protected boolean closeable;

    /**
     * Whether this document is draggable or not.
     * All documents are draggable by default, but you may change that.
     */
    protected boolean draggable;

    /**
     * Document content.
     * A component that represents document tab content.
     */
    protected Component component;

    /**
     * Constructs new document.
     *
     * @param id        document ID
     * @param title     document title
     * @param component document content
     */
    public DocumentData ( final String id, final String title, final Component component )
    {
        this ( id, null, title, null, component );
    }

    /**
     * Constructs new document.
     *
     * @param id        document ID
     * @param icon      document icon
     * @param component document content
     */
    public DocumentData ( final String id, final Icon icon, final Component component )
    {
        this ( id, icon, null, null, component );
    }

    /**
     * Constructs new document.
     *
     * @param id        document ID
     * @param icon      document icon
     * @param title     document title
     * @param component document content
     */
    public DocumentData ( final String id, final Icon icon, final String title, final Component component )
    {
        this ( id, icon, title, null, component );
    }

    /**
     * Constructs new document.
     *
     * @param id         document ID
     * @param icon       document icon
     * @param title      document title
     * @param background document tab background color
     * @param component  document content
     */
    public DocumentData ( final String id, final Icon icon, final String title, final Color background, final Component component )
    {
        this ( id, icon, title, background, true, component );
    }

    /**
     * Constructs new document.
     *
     * @param id         document ID
     * @param icon       document icon
     * @param title      document title
     * @param background document tab background color
     * @param closeable  whether document is closeable or not
     * @param component  document content
     */
    public DocumentData ( final String id, final Icon icon, final String title, final Color background, final boolean closeable,
                          final Component component )
    {
        this ( id, icon, title, background, closeable, true, component );
    }

    /**
     * Constructs new document.
     *
     * @param id         document ID
     * @param icon       document icon
     * @param title      document title
     * @param background document tab background color
     * @param closeable  whether document is closeable or not
     * @param draggable  whether document is draggable or not
     * @param component  document content
     */
    public DocumentData ( final String id, final Icon icon, final String title, final Color background, final boolean closeable,
                          final boolean draggable, final Component component )
    {
        super ();
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.background = background;
        this.closeable = closeable;
        this.draggable = draggable;
        this.component = component;
    }

    /**
     * Returns document ID.
     *
     * @return document ID
     */
    public String getId ()
    {
        return id;
    }

    /**
     * Sets document ID.
     *
     * @param id new document ID
     */
    public void setId ( final String id )
    {
        this.id = id;
    }

    /**
     * Returns document icon.
     *
     * @return document icon
     */
    public Icon getIcon ()
    {
        return icon;
    }

    /**
     * Sets document icon.
     *
     * @param icon new document icon
     */
    public void setIcon ( final Icon icon )
    {
        this.icon = icon;
    }

    /**
     * Returns document title.
     *
     * @return document title
     */
    public String getTitle ()
    {
        return title;
    }

    /**
     * Returns translated document title.
     *
     * @return translated document title
     */
    public String getActualTitle ()
    {
        return title != null ? title : LM.get ( id );
    }

    /**
     * Sets document title.
     *
     * @param title new document title
     */
    public void setTitle ( final String title )
    {
        this.title = title;
    }

    /**
     * Returns document tab background color.
     *
     * @return document tab background color
     */
    public Color getBackground ()
    {
        return background;
    }

    /**
     * Sets document tab background color.
     *
     * @param background document tab background color
     */
    public void setBackground ( final Color background )
    {
        this.background = background;
    }

    /**
     * Returns whether document is closeable or not.
     *
     * @return true if document is closeable, false otherwise
     */
    public boolean isCloseable ()
    {
        return closeable;
    }

    /**
     * Sets whether document is closeable or not.
     *
     * @param closeable whether document is closeable or not
     */
    public void setCloseable ( final boolean closeable )
    {
        this.closeable = closeable;
    }

    /**
     * Returns whether document is draggable or not.
     *
     * @return true if document is draggable, false otherwise
     */
    public boolean isDraggable ()
    {
        return draggable;
    }

    /**
     * Sets whether document is draggable or not.
     *
     * @param draggable whether document is draggable or not
     */
    public void setDraggable ( final boolean draggable )
    {
        this.draggable = draggable;
    }

    /**
     * Returns document content.
     *
     * @return document content
     */
    public Component getComponent ()
    {
        return component;
    }

    /**
     * Sets document content.
     *
     * @param component new document content
     */
    public void setComponent ( final Component component )
    {
        this.component = component;
    }
}