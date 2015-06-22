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

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.splitpane.WebSplitPane;
import com.alee.laf.tabbedpane.TabbedPaneStyle;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.alee.managers.focus.DefaultFocusTracker;
import com.alee.managers.focus.FocusManager;
import com.alee.managers.hotkey.Hotkey;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.managers.hotkey.HotkeyRunnable;
import com.alee.managers.language.LM;
import com.alee.utils.CollectionUtils;
import com.alee.utils.SwingUtils;
import com.alee.utils.swing.Customizer;
import com.alee.utils.swing.menu.PopupMenuGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data for single tabbed pane within document pane.
 * It basically contains tabbed pane and opened documents list.
 *
 * @author Mikle Garin
 * @see <a href="https://github.com/mgarin/weblaf/wiki/How-to-use-WebDocumentPane">How to use WebDocumentPane</a>
 * @see com.alee.extended.tab.WebDocumentPane
 */

public final class PaneData<T extends DocumentData> implements StructureData<T>, SwingConstants
{
    /**
     * Close icons.
     */
    private static final ImageIcon closeTabIcon = new ImageIcon ( PaneData.class.getResource ( "icons/close.png" ) );
    private static final ImageIcon closeTabRolloverIcon = new ImageIcon ( PaneData.class.getResource ( "icons/close-rollover.png" ) );

    /**
     * Actual tabbed pane component.
     */
    protected final WebTabbedPane tabbedPane;

    /**
     * Pane focus tracker.
     */
    protected final DefaultFocusTracker focusTracker;

    /**
     * Pane documents.
     */
    protected List<T> data = new ArrayList<T> ();

    /**
     * Constructs new PaneData for the specified WebDocumentPane.
     *
     * @param documentPane parent WebDocumentPane
     */
    public PaneData ( final WebDocumentPane<T> documentPane )
    {
        super ();

        // Creating tabbed pane
        tabbedPane = new WebTabbedPane ( TabbedPaneStyle.attached );
        tabbedPane.putClientProperty ( WebDocumentPane.DATA_KEY, this );

        // Customizing tabbed pane
        updateTabbedPaneCustomizer ( documentPane );

        // Some hotkeys
        HotkeyManager.registerHotkey ( tabbedPane, tabbedPane, Hotkey.CTRL_W, new HotkeyRunnable ()
        {
            @Override
            public void run ( final KeyEvent e )
            {
                closeSelected ();
            }
        } );

        // Tab drag
        DocumentDragHandler.install ( this );

        // Tab menu
        tabbedPane.addMouseListener ( new MouseAdapter ()
        {
            @Override
            public void mousePressed ( final MouseEvent e )
            {
                if ( SwingUtils.isMiddleMouseButton ( e ) )
                {
                    if ( getDocumentPane ().isCloseable () )
                    {
                        final int index = tabbedPane.getTabAt ( e.getPoint () );
                        if ( index != -1 )
                        {
                            final T document = get ( index );
                            if ( document.isCloseable () )
                            {
                                close ( document );
                            }
                        }
                    }
                }
                else if ( SwingUtils.isPopupTrigger ( e ) )
                {
                    // Checking that menu is enabled
                    final WebDocumentPane documentPane = getDocumentPane ();
                    if ( !documentPane.isTabMenuEnabled () )
                    {
                        return;
                    }

                    // Checking that user actually clicked on a tab
                    final int index = tabbedPane.getTabAt ( e.getPoint () );
                    if ( index == -1 )
                    {
                        return;
                    }

                    // Variables
                    final T document = get ( index );
                    final boolean csb = documentPane.isCloseable () && document.isCloseable ();
                    final boolean spb = data.size () > 1 && documentPane.isSplitEnabled ();
                    final boolean spl = tabbedPane.getParent () instanceof WebSplitPane;

                    // Creating popup menu
                    final PopupMenuGenerator pmg = new PopupMenuGenerator ( "document-pane-menu" );
                    pmg.setIconSettings ( PaneData.class, "icons/menu/", ".png" );
                    pmg.setLanguagePrefix ( "weblaf.ex.docpane" );
                    pmg.addItem ( "close", "close", Hotkey.CTRL_W, csb, new ActionListener ()
                    {
                        @Override
                        public void actionPerformed ( final ActionEvent e )
                        {
                            close ( get ( index ) );
                        }
                    } );
                    pmg.addSeparator ();
                    pmg.addItem ( "left", "left", spb, new ActionListener ()
                    {
                        @Override
                        public void actionPerformed ( final ActionEvent e )
                        {
                            split ( document, LEFT );
                        }
                    } );
                    pmg.addItem ( "right", "right", spb, new ActionListener ()
                    {
                        @Override
                        public void actionPerformed ( final ActionEvent e )
                        {
                            split ( document, RIGHT );
                        }
                    } );
                    pmg.addItem ( "top", "top", spb, new ActionListener ()
                    {
                        @Override
                        public void actionPerformed ( final ActionEvent e )
                        {
                            split ( document, TOP );
                        }
                    } );
                    pmg.addItem ( "bottom", "bottom", spb, new ActionListener ()
                    {
                        @Override
                        public void actionPerformed ( final ActionEvent e )
                        {
                            split ( document, BOTTOM );
                        }
                    } );
                    pmg.addSeparator ();
                    pmg.addItem ( "rotate", "rotate", spl, new ActionListener ()
                    {
                        @Override
                        public void actionPerformed ( final ActionEvent e )
                        {
                            rotate ();
                        }
                    } );
                    pmg.addItem ( "unsplit", "unsplit", spl, new ActionListener ()
                    {
                        @Override
                        public void actionPerformed ( final ActionEvent e )
                        {
                            merge ();
                        }
                    } );
                    pmg.addItem ( "unsplit", "unsplitall", spl, new ActionListener ()
                    {
                        @Override
                        public void actionPerformed ( final ActionEvent e )
                        {
                            mergeAll ();
                        }
                    } );

                    // Displaying popup menu
                    final WebPopupMenu menu = pmg.getPopupMenu ();
                    final Dimension mps = menu.getPreferredSize ();
                    final Rectangle bounds = tabbedPane.getBoundsAt ( index );
                    menu.show ( tabbedPane, bounds.x + bounds.width / 2 - mps.width / 2,
                            bounds.y + bounds.height - menu.getShadeWidth () + 5 );
                }
            }
        } );

        // Adding focus tracker
        focusTracker = new DefaultFocusTracker ( true )
        {
            @Override
            public void focusChanged ( final boolean focused )
            {
                if ( focused )
                {
                    activate ();
                }
            }
        };
        FocusManager.addFocusTracker ( tabbedPane, focusTracker );
    }

    /**
     * Updates tabbed pane customizer.
     *
     * @param documentPane parent WebDocumentPane
     */
    protected void updateTabbedPaneCustomizer ( final WebDocumentPane<T> documentPane )
    {
        final Customizer<WebTabbedPane> customizer = documentPane.getTabbedPaneCustomizer ();
        if ( customizer != null )
        {
            customizer.customize ( tabbedPane );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Component getComponent ()
    {
        return getTabbedPane ();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaneData<T> findClosestPane ()
    {
        return this;
    }

    /**
     * Returns parent WebDocumentPane.
     *
     * @return parent WebDocumentPane
     */
    public WebDocumentPane getDocumentPane ()
    {
        return SwingUtils.getFirstParent ( tabbedPane, WebDocumentPane.class );
    }

    /**
     * Returns actual tabbed pane component.
     *
     * @return actual tabbed pane component
     */
    public WebTabbedPane getTabbedPane ()
    {
        return tabbedPane;
    }

    /**
     * Returns pane documents.
     *
     * @return pane documents
     */
    public List<T> getData ()
    {
        return data;
    }

    /**
     * Returns pane documents count.
     *
     * @return pane documents count
     */
    public int count ()
    {
        return data.size ();
    }

    /**
     * Returns whether specified document is in this pane or not.
     *
     * @param document document to look for
     * @return true if specified document is in this pane, false otherwise
     */
    public boolean contains ( final T document )
    {
        return contains ( document.getId () );
    }

    /**
     * Returns whether document with the specified ID is in this pane or not.
     *
     * @param documentId ID of the document to look for
     * @return true if document with the specified ID is in this pane, false otherwise
     */
    public boolean contains ( final String documentId )
    {
        for ( final T document : data )
        {
            if ( document.getId ().equals ( documentId ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds new document into this pane.
     *
     * @param document document to add
     */
    public void add ( final T document )
    {
        add ( document, -1 );
    }

    /**
     * Adds new document into this pane at the specified index.
     *
     * @param document document to add
     * @param index    index to add at
     */
    public void add ( final T document, final int index )
    {
        final int i = index != -1 ? index : tabbedPane.getTabCount ();
        data.add ( i, document );
        tabbedPane.insertTab ( "", document.getIcon (), document.getComponent (), null, i );
        tabbedPane.setBackgroundAt ( i, document.getBackground () );
        tabbedPane.setTabComponentAt ( i, createTabComponent ( document ) );
    }

    /**
     * Returns new tab component.
     *
     * @param document document to create tab component for
     * @return new tab component
     */
    protected JComponent createTabComponent ( final T document )
    {
        final WebPanel tabPanel = new WebPanel ( new BorderLayout ( 2, 0 ) );
        tabPanel.setOpaque ( false );

        // Creating title component
        final String title = document.getTitle ();
        if ( title != null )
        {
            if ( LM.contains ( title ) )
            {
                // Tab with translated title and icon
                tabPanel.add ( WebLabel.createTranslatedLabel ( document.getIcon (), title ), BorderLayout.CENTER );
            }
            else
            {
                // Tab with simple title and icon
                tabPanel.add ( new WebLabel ( title, document.getIcon () ), BorderLayout.CENTER );
            }
        }
        else
        {
            // Tab with icon only
            tabPanel.add ( new WebLabel ( document.getIcon () ), BorderLayout.CENTER );
        }

        if ( getDocumentPane ().isCloseable () && document.isCloseable () )
        {
            final WebButton closeButton = new WebButton ( closeTabIcon, closeTabRolloverIcon );
            closeButton.setUndecorated ( true );
            closeButton.setFocusable ( false );
            closeButton.addActionListener ( new ActionListener ()
            {
                @Override
                public void actionPerformed ( final ActionEvent e )
                {
                    close ( document );
                }
            } );
            tabPanel.add ( closeButton, BorderLayout.LINE_END );
        }

        return tabPanel;
    }

    /**
     * Opens document in this pane.
     * Unlike add method this one informs listeners about opened document.
     *
     * @param document document to open
     */
    public void open ( final T document )
    {
        // Opening document
        add ( document );

        // Informing listeners about document open event
        getDocumentPane ().fireDocumentOpened ( document, this, indexOf ( document ) );
    }

    /**
     * Returns pane document at the specified index.
     *
     * @param index document index
     * @return pane document at the specified index
     */
    public T get ( final int index )
    {
        return data.get ( index );
    }

    /**
     * Returns pane document with the specified ID.
     *
     * @param id document ID
     * @return pane document with the specified ID
     */
    public T get ( final String id )
    {
        for ( final T document : data )
        {
            if ( document.getId ().equals ( id ) )
            {
                return document;
            }
        }
        return null;
    }

    /**
     * Returns pane's selected document.
     *
     * @return pane's selected document
     */
    public T getSelected ()
    {
        final int index = tabbedPane.getSelectedIndex ();
        return index != -1 ? data.get ( index ) : null;
    }

    /**
     * Sets pane's selected document ID.
     *
     * @param id ID of the document to select
     */
    public void setSelected ( final String id )
    {
        setSelected ( indexOf ( id ) );
    }

    /**
     * Sets selected document.
     *
     * @param document document to select
     */
    public void setSelected ( final T document )
    {
        setSelected ( indexOf ( document ) );
    }

    /**
     * Sets selected document index.
     *
     * @param index index of the document to select
     */
    public void setSelected ( final int index )
    {
        tabbedPane.setSelectedIndex ( index );
    }

    /**
     * Returns index of the document with the specified ID.
     *
     * @param id document ID
     * @return index of the document with the specified ID
     */
    public int indexOf ( final String id )
    {
        return indexOf ( get ( id ) );
    }

    /**
     * Returns index of the specified document.
     *
     * @param document document
     * @return index of the specified document
     */
    public int indexOf ( final T document )
    {
        return data.indexOf ( document );
    }

    /**
     * Closes document at the specified index in the active pane.
     *
     * @param index index of the document to close
     */
    public boolean remove ( final int index )
    {
        return remove ( get ( index ) );
    }

    /**
     * Closes document with the specified ID.
     *
     * @param id ID of the document to close
     */
    public boolean remove ( final String id )
    {
        return remove ( get ( id ) );
    }

    /**
     * Closes the specified document.
     *
     * @param document document to close
     */
    public boolean remove ( final T document )
    {
        if ( document != null )
        {
            final int index = indexOf ( document );
            if ( index != -1 )
            {
                tabbedPane.remove ( index );
                data.remove ( document );
                return true;
            }
        }
        return false;
    }

    /**
     * Closes all document in this group.
     */
    public void closeAll ()
    {
        for ( final T document : CollectionUtils.copy ( data ) )
        {
            close ( document );
        }
    }

    /**
     * Closes document at the specified index in the active pane.
     *
     * @param index index of the document to close
     */
    public boolean close ( final int index )
    {
        return close ( get ( index ) );
    }

    /**
     * Closes document with the specified ID.
     *
     * @param id ID of the document to close
     */
    public boolean close ( final String id )
    {
        return close ( get ( id ) );
    }

    /**
     * Closes the specified document.
     *
     * @param document document to close
     */
    public boolean close ( final T document )
    {
        if ( document != null )
        {
            final WebDocumentPane documentPane = getDocumentPane ();
            final int index = indexOf ( document );

            // Informing listeners about document closing event
            if ( documentPane.fireDocumentClosing ( document, this, index ) )
            {
                // Closing document and fixing view
                final boolean removed = remove ( document );
                mergeIfEmpty ();

                // Informing listeners about document close event
                documentPane.fireDocumentClosed ( document, this, index );

                return removed;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Closes selected document.
     */
    public void closeSelected ()
    {
        final T selected = getSelected ();
        if ( selected != null )
        {
            close ( selected );
        }
    }

    /**
     * Activates this pane within WebDocumentPane.
     */
    public void activate ()
    {
        final WebDocumentPane pane = getDocumentPane ();
        if ( pane != null )
        {
            pane.activate ( PaneData.this );
        }
    }

    /**
     * Splits the specified document into a separate pane.
     *
     * @param document  document to split
     * @param direction split direction
     */
    public void split ( final T document, final int direction )
    {
        final WebDocumentPane pane = getDocumentPane ();
        if ( pane != null )
        {
            pane.split ( PaneData.this, document, direction );
        }
    }

    /**
     * Merges this pane with its neighbour if it is empty.
     */
    public void mergeIfEmpty ()
    {
        if ( count () == 0 )
        {
            merge ();
        }
    }

    /**
     * Changes parent split orientation if this pane is located within a split.
     */
    public void rotate ()
    {
        final Container parent = tabbedPane.getParent ();
        if ( parent instanceof WebSplitPane )
        {
            final SplitData<T> splitData = WebDocumentPane.getData ( ( WebSplitPane ) parent );
            splitData.changeSplitOrientation ();
        }
    }

    /**
     * Merges this pane with its neighbour.
     */
    public void merge ()
    {
        final WebDocumentPane documentPane = getDocumentPane ();
        if ( documentPane != null )
        {
            documentPane.merge ( this );
        }
    }

    /**
     * Merges all panes within WebDocumentPane.
     */
    public void mergeAll ()
    {
        final WebDocumentPane pane = getDocumentPane ();
        if ( pane != null )
        {
            pane.merge ( pane.getStructureRoot () );
        }
    }
}