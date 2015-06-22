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

package com.alee.managers.plugin;

import com.alee.global.GlobalConstants;
import com.alee.log.Log;
import com.alee.managers.plugin.data.*;
import com.alee.utils.*;
import com.alee.utils.compare.Filter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Base class for any plugin manager you might want to create.
 *
 * @author Mikle Garin
 * @see <a href="https://github.com/mgarin/weblaf/wiki/How-to-use-PluginManager">How to use PluginManager</a>
 * @see com.alee.managers.plugin.Plugin
 */

public abstract class PluginManager<T extends Plugin>
{
    /**
     * todo 1. Allow direct plugin loading from specified file/url (including loading from within another jar)
     * todo 2. Allow to disable logging from this manager
     * todo 3. Make plugin operations thread-safe
     * todo 4. PluginManager -> Plugin -> dependencies on other plugins
     * todo 5. Translate/replace error messages? or not
     */

    /**
     * Plugins listeners.
     */
    protected List<PluginsListener<T>> listeners = new ArrayList<PluginsListener<T>> ( 1 );

    /**
     * Plugin checks lock object.
     */
    protected final Object checkLock = new Object ();

    /**
     * Detected plugins list.
     * All plugins with available descriptions will get into this list.
     */
    protected List<DetectedPlugin<T>> detectedPlugins;

    /**
     * Recently detected plugins list.
     * Contains plugins detected while last plugins check.
     */
    protected List<DetectedPlugin<T>> recentlyDetected;

    /**
     * Special filter to filter out unwanted plugins before their initialization.
     * It is up to developer to specify this filter and its conditions.
     */
    protected Filter<DetectedPlugin<T>> pluginFilter = null;

    /**
     * Whether should allow loading multiply plugins with the same ID or not.
     * In case this is set to false only the newest version of the same plugin will be loaded if more than one provided.
     */
    protected boolean allowSimilarPlugins = false;

    /**
     * Loaded and running plugins list.
     * This might be less than list of detected plugins in the end due to lots of different reasons.
     * Only those plugins which are actually loaded successfully are getting added here.
     */
    protected List<T> availablePlugins;

    /**
     * Map of plugins cached by their IDs.
     */
    protected Map<String, T> availablePluginsById = new HashMap<String, T> ();

    /**
     * Map of plugins cached by their classes.
     */
    protected Map<Class<? extends Plugin>, T> availablePluginsByClass = new HashMap<Class<? extends Plugin>, T> ();

    /**
     * Recently initialized plugins list.
     * Contains plugins initialized while last plugins check.
     */
    protected List<T> recentlyInitialized;

    /**
     * Plugins directory path.
     * It is either absolute path or relative to working directory path.
     */
    protected String pluginsDirectoryPath;

    /**
     * Whether plugins directory subfolders should be checked recursively or not.
     */
    protected boolean checkRecursively;

    /**
     * Plugin directory files filter.
     * By defauly "*.jar" and "*.plugin" files are accepted.
     */
    protected FileFilter fileFilter;

    /**
     * Whether should create new class loader for each loaded plugin or not.
     * Be aware that you might experience various issues with separate class loaders.
     */
    protected boolean createNewClassLoader = false;

    /**
     * Constructs new plugin manager.
     */
    public PluginManager ()
    {
        this ( null );
    }

    /**
     * Constructs new plugin manager.
     *
     * @param pluginsDirectoryPath plugins directory path
     */
    public PluginManager ( final String pluginsDirectoryPath )
    {
        this ( pluginsDirectoryPath, true );
    }

    /**
     * Constructs new plugin manager.
     *
     * @param pluginsDirectoryPath plugins directory path
     * @param checkRecursively     whether plugins directory subfolders should be checked recursively or not
     */
    public PluginManager ( final String pluginsDirectoryPath, final boolean checkRecursively )
    {
        super ();

        // User settings
        this.pluginsDirectoryPath = pluginsDirectoryPath;
        this.checkRecursively = checkRecursively;

        // Default file filter
        this.fileFilter = new FileFilter ()
        {
            @Override
            public boolean accept ( final File file )
            {
                final String name = file.getName ().toLowerCase ();
                return name.endsWith ( ".jar" ) || name.endsWith ( ".plugin" );
            }
        };

        // Runtime variables
        detectedPlugins = new ArrayList<DetectedPlugin<T>> ();
        availablePlugins = new ArrayList<T> ( detectedPlugins.size () );
    }

    /**
     * Returns name of the plugin descriptor file.
     * This file should contain serialized PluginInformation.
     *
     * @return name of the plugin descriptor file
     */
    protected String getPluginDescriptorFile ()
    {
        return "plugin.xml";
    }

    /**
     * Returns name of the plugin logo file.
     * Logo should be placed near the plugin descriptor file.
     *
     * @return name of the plugin logo file
     */
    protected String getPluginLogoFile ()
    {
        return "logo.png";
    }

    /**
     * Returns accepted by this manager plugin type.
     * In case {@code null} is returned this manager accepts any plugin type.
     *
     * @return accepted by this manager plugin type
     */
    protected String getAcceptedPluginType ()
    {
        return null;
    }

    /**
     * Registers programmatically loaded plugin within this PluginManager.
     * This call will add the specified plugin into available plugins list.
     * It will also create a custom DetectedPlugin data based on provided information.
     *
     * @param plugin plugin to register
     */
    public void registerPlugin ( final T plugin )
    {
        registerPlugin ( plugin, plugin.getPluginInformation (), plugin.getPluginLogo () );
    }

    /**
     * Registers programmatically loaded plugin within this PluginManager.
     * This call will add the specified plugin into available plugins list.
     * It will also create a custom DetectedPlugin data based on provided information.
     *
     * @param plugin      plugin to register
     * @param information about this plugin
     * @param logo        plugin logo
     */
    public void registerPlugin ( final T plugin, final PluginInformation information, final ImageIcon logo )
    {
        final String prefix = "[" + information + "] ";
        Log.info ( this, prefix + "Initializing pre-loaded plugin..." );

        // Creating base detected plugin information
        final DetectedPlugin<T> detectedPlugin = new DetectedPlugin<T> ( null, null, information, logo );
        detectedPlugin.setStatus ( PluginStatus.loaded );
        detectedPlugin.setPlugin ( plugin );
        plugin.setPluginManager ( PluginManager.this );
        plugin.setDetectedPlugin ( detectedPlugin );

        // Saving plugin
        detectedPlugins.add ( detectedPlugin );
        availablePlugins.add ( plugin );
        availablePluginsById.put ( plugin.getId (), plugin );
        availablePluginsByClass.put ( plugin.getClass (), plugin );

        Log.info ( this, prefix + "Pre-loaded plugin initialized" );

        // Informing
        firePluginsInitialized ( Arrays.asList ( plugin ) );
    }

    /**
     * Performs plugins search within the specified plugins directory.
     * This call might be performed as many times as you like.
     * It will simply ignore plugins detected before and will process newly found plugins appropriately.
     */
    public void checkPlugins ()
    {
        checkPlugins ( pluginsDirectoryPath, checkRecursively );
    }

    /**
     * Performs plugins search within the specified plugins directory.
     * This call might be performed as many times as you like.
     * It will simply ignore plugins detected before and will process newly found plugins appropriately.
     *
     * @param checkRecursively whether plugins directory subfolders should be checked recursively or not
     */
    public void checkPlugins ( final boolean checkRecursively )
    {
        checkPlugins ( pluginsDirectoryPath, checkRecursively );
    }

    /**
     * Performs plugins search within the specified plugins directory.
     * This call might be performed as many times as you like.
     * It will simply ignore plugins detected before and will process newly found plugins appropriately.
     *
     * @param pluginsDirectoryPath plugins directory path
     */
    public void checkPlugins ( final String pluginsDirectoryPath )
    {
        checkPlugins ( pluginsDirectoryPath, checkRecursively );
    }

    /**
     * Performs plugins search within the specified plugins directory.
     * This call might be performed as many times as you like.
     * It will simply ignore plugins detected before and will process newly found plugins appropriately.
     *
     * @param pluginsDirectoryPath plugins directory path
     * @param checkRecursively     whether plugins directory subfolders should be checked recursively or not
     */
    public void checkPlugins ( final String pluginsDirectoryPath, final boolean checkRecursively )
    {
        synchronized ( checkLock )
        {
            // Ignore check if check path is not specified
            if ( pluginsDirectoryPath == null )
            {
                return;
            }

            // Informing about plugins check start
            firePluginsCheckStarted ( pluginsDirectoryPath, checkRecursively );

            // Collecting plugins information
            if ( collectPluginsInformation ( pluginsDirectoryPath, checkRecursively ) )
            {
                // Informing about newly detected plugins
                firePluginsDetected ( recentlyDetected );

                Log.info ( this, "Initializing plugins..." );

                // Initializing plugins
                initializePlugins ();

                // Sorting plugins according to their initialization strategies
                applyInitializationStrategy ();

                // Properly sorting recently initialized plugins
                Collections.sort ( recentlyInitialized, new Comparator<T> ()
                {
                    @Override
                    public int compare ( final T o1, final T o2 )
                    {
                        final Integer i1 = availablePlugins.indexOf ( o1 );
                        final Integer i2 = availablePlugins.indexOf ( o2 );
                        return i1.compareTo ( i2 );
                    }
                } );

                // Informing about new plugins initialization
                firePluginsInitialized ( recentlyInitialized );

                Log.info ( this, "Plugins initialization finished" );
            }

            // Informing about plugins check end
            firePluginsCheckEnded ( pluginsDirectoryPath, checkRecursively );
        }
    }

    /**
     * Collects information about available plugins.
     *
     * @return true if operation succeeded, false otherwise
     */
    protected boolean collectPluginsInformation ( final String pluginsDirectoryPath, final boolean checkRecursively )
    {
        if ( pluginsDirectoryPath != null )
        {
            Log.info ( this, "Collecting plugins information..." );
            recentlyDetected = new ArrayList<DetectedPlugin<T>> ();
            return collectPluginsInformationImpl ( new File ( pluginsDirectoryPath ), checkRecursively );
        }
        else
        {
            Log.error ( this, "Plugins directory is not yet specified" );
            return false;
        }
    }

    /**
     * Collects information about available plugins.
     *
     * @param dir plugins directory
     * @return true if operation succeeded, false otherwise
     */
    protected boolean collectPluginsInformationImpl ( final File dir, final boolean checkRecursively )
    {
        // Checking all files
        final File[] files = dir.listFiles ( fileFilter );
        if ( files != null )
        {
            for ( final File file : files )
            {
                collectPluginInformation ( file );
            }
        }

        // Checking sub-directories recursively
        if ( checkRecursively )
        {
            final File[] subfolders = dir.listFiles ( GlobalConstants.DIRECTORIES_FILTER );
            if ( subfolders != null )
            {
                for ( final File subfolder : subfolders )
                {
                    collectPluginsInformationImpl ( subfolder, checkRecursively );
                }
            }
        }

        return true;
    }

    /**
     * Tries to collect plugin information from the specified file.
     * This call will simply be ignored if this is not a plugin file or if something goes wrong.
     *
     * @param file plugin file to process
     */
    protected void collectPluginInformation ( final File file )
    {
        try
        {
            final String pluginDescriptor = getPluginDescriptorFile ();
            final String pluginLogo = getPluginLogoFile ();
            final ZipFile zipFile = new ZipFile ( file );
            final Enumeration entries = zipFile.entries ();
            while ( entries.hasMoreElements () )
            {
                final ZipEntry entry = ( ZipEntry ) entries.nextElement ();
                if ( entry.getName ().endsWith ( pluginDescriptor ) )
                {
                    // Reading plugin information
                    final InputStream inputStream = zipFile.getInputStream ( entry );
                    final PluginInformation info = XmlUtils.fromXML ( inputStream );
                    inputStream.close ();

                    // Reading plugin icon
                    final ZipEntry logoEntry = new ZipEntry ( ZipUtils.getZipEntryFileLocation ( entry ) + pluginLogo );
                    final InputStream logoInputStream = zipFile.getInputStream ( logoEntry );
                    final ImageIcon logo;
                    if ( logoInputStream != null )
                    {
                        logo = new ImageIcon ( ImageIO.read ( logoInputStream ) );
                        logoInputStream.close ();
                    }
                    else
                    {
                        logo = null;
                    }

                    // Checking whether we have already detected this plugin or not
                    if ( !wasDetected ( file.getParent (), file.getName () ) )
                    {
                        final DetectedPlugin<T> plugin = new DetectedPlugin<T> ( file.getParent (), file.getName (), info, logo );
                        detectedPlugins.add ( plugin );
                        recentlyDetected.add ( plugin );
                        Log.info ( this, "Plugin detected: " + info );
                    }

                    break;
                }
            }
            zipFile.close ();
        }
        catch ( final IOException e )
        {
            Log.error ( this, e );
        }
    }

    /**
     * Returns whether this plugin file was already detected before or not.
     *
     * @param pluginFolder plugin directory
     * @param pluginFile   plugin file
     * @return true if this plugin file was already detected before, false otherwise
     */
    protected boolean wasDetected ( final String pluginFolder, final String pluginFile )
    {
        for ( final DetectedPlugin<T> plugin : detectedPlugins )
        {
            if ( plugin.getPluginFile () != null && plugin.getPluginFolder ().equals ( pluginFolder ) &&
                    plugin.getPluginFile ().equals ( pluginFile ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes detected earlier plugins.
     */
    protected void initializePlugins ()
    {
        // Map to store plugin libraries
        final Map<String, Map<PluginLibrary, PluginInformation>> pluginLibraries =
                new HashMap<String, Map<PluginLibrary, PluginInformation>> ();

        // List to collect newly initialized plugins
        // This is required to properly inform about newly loaded plugins later
        recentlyInitialized = new ArrayList<T> ();

        // Initializing detected plugins
        final String acceptedPluginType = getAcceptedPluginType ();
        for ( final DetectedPlugin<T> dp : detectedPlugins )
        {
            // Skip plugins we have already tried to initialize
            if ( dp.getStatus () != PluginStatus.detected )
            {
                continue;
            }

            final File pluginFile = new File ( dp.getPluginFolder (), dp.getPluginFile () );
            final PluginInformation info = dp.getInformation ();
            final String prefix = "[" + FileUtils.getRelativePath ( pluginFile, new File ( pluginsDirectoryPath ) ) + "] [" + info + "] ";
            try
            {
                // Checking plugin type as we don't want (for example) to load server plugins on client side
                if ( acceptedPluginType != null && ( info.getType () == null || !info.getType ().equals ( acceptedPluginType ) ) )
                {
                    Log.warn ( this, prefix + "Plugin of type \"" + info.getType () + "\" cannot be loaded, " +
                            "required plugin type is \"" + acceptedPluginType + "\"" );
                    dp.setStatus ( PluginStatus.failed );
                    dp.setFailureCause ( "Wrong type" );
                    dp.setExceptionMessage ( "Detected plugin type: " + info.getType () + "\", " +
                            "required plugin type: \"" + acceptedPluginType + "\"" );
                    continue;
                }

                // Checking that this is latest plugin version of all available
                // Usually there shouldn't be different versions of the same plugin but everyone make mistakes
                if ( isDeprecatedVersion ( dp ) )
                {
                    Log.warn ( this, prefix + "This plugin is deprecated, newer version loaded instead" );
                    dp.setStatus ( PluginStatus.failed );
                    dp.setFailureCause ( "Deprecated" );
                    dp.setExceptionMessage ( "This plugin is deprecated, newer version loaded instead" );
                    continue;
                }

                // Checking that this plugin version is not yet loaded
                // This might occur in case the same plugin appears more than once in different files
                if ( isSameVersionAlreadyLoaded ( dp, detectedPlugins ) )
                {
                    Log.warn ( this, prefix + "Plugin is duplicate, it will be loaded from another file" );
                    dp.setStatus ( PluginStatus.failed );
                    dp.setFailureCause ( "Duplicate" );
                    dp.setExceptionMessage ( "This plugin is duplicate, it will be loaded from another file" );
                    continue;
                }

                // Checking that plugin filter accepts this plugin
                if ( getPluginFilter () != null && !getPluginFilter ().accept ( dp ) )
                {
                    Log.info ( this, prefix + "Plugin was not accepted by plugin filter" );
                    dp.setStatus ( PluginStatus.failed );
                    dp.setFailureCause ( "Filtered" );
                    dp.setExceptionMessage ( "Plugin was not accepted by plugin filter" );
                    continue;
                }

                // Now loading the plugin
                Log.info ( this, prefix + "Initializing plugin..." );
                dp.setStatus ( PluginStatus.loading );

                // Collecting plugin and its libraries JAR paths
                final List<URL> jarPaths = new ArrayList<URL> ( 1 + info.getLibrariesCount () );
                jarPaths.add ( pluginFile.toURI ().toURL () );
                if ( info.getLibraries () != null )
                {
                    for ( final PluginLibrary library : info.getLibraries () )
                    {
                        final File file = new File ( dp.getPluginFolder (), library.getFile () );
                        if ( file.exists () )
                        {
                            // Adding library URI to path
                            jarPaths.add ( file.toURI ().toURL () );

                            // Saving library information for futher checks
                            Map<PluginLibrary, PluginInformation> libraries = pluginLibraries.get ( library.getId () );
                            if ( libraries == null )
                            {
                                libraries = new HashMap<PluginLibrary, PluginInformation> ( 1 );
                                pluginLibraries.put ( library.getId (), libraries );
                            }
                            libraries.put ( library, info );
                        }
                        else
                        {
                            Log.warn ( this, prefix + "Unable to locate library: " + library.getFile () );
                        }
                    }
                }

                try
                {
                    // Choosing class loader
                    final ClassLoader cl = getClass ().getClassLoader ();
                    final ClassLoader classLoader;
                    if ( createNewClassLoader || !( cl instanceof URLClassLoader ) )
                    {
                        // Create new class loader
                        classLoader = URLClassLoader.newInstance ( jarPaths.toArray ( new URL[ jarPaths.size () ] ), cl );
                    }
                    else
                    {
                        // Use current class loader
                        classLoader = cl;
                        for ( final URL url : jarPaths )
                        {
                            ReflectUtils.callMethodSafely ( classLoader, "addURL", url );
                        }
                    }

                    // Loading plugin
                    final Class<?> pluginClass = classLoader.loadClass ( info.getMainClass () );
                    final T plugin = ReflectUtils.createInstance ( pluginClass );
                    plugin.setPluginManager ( PluginManager.this );
                    plugin.setDetectedPlugin ( dp );

                    // Saving initialized plugin
                    availablePlugins.add ( plugin );
                    availablePluginsById.put ( plugin.getId (), plugin );
                    availablePluginsByClass.put ( plugin.getClass (), plugin );
                    recentlyInitialized.add ( plugin );

                    Log.info ( this, prefix + "Plugin initialized" );
                    dp.setStatus ( PluginStatus.loaded );
                    dp.setPlugin ( plugin );
                }
                catch ( final Throwable e )
                {
                    Log.error ( this, prefix + "Unable to initialize plugin", e );
                    dp.setStatus ( PluginStatus.failed );
                    dp.setFailureCause ( "Internal exception" );
                    dp.setException ( e );
                }
            }
            catch ( final Throwable e )
            {
                Log.error ( this, prefix + "Unable to initialize plugin data", e );
                dp.setStatus ( PluginStatus.failed );
                dp.setFailureCause ( "Data exception" );
                dp.setException ( e );
            }
        }

        // Checking for same/similar libraries used within plugins
        boolean warned = false;
        for ( final Map.Entry<String, Map<PluginLibrary, PluginInformation>> libraries : pluginLibraries.entrySet () )
        {
            final Map<PluginLibrary, PluginInformation> sameLibraries = libraries.getValue ();
            if ( sameLibraries.size () > 1 )
            {
                final String title = sameLibraries.keySet ().iterator ().next ().getTitle ();
                final StringBuilder sb = new StringBuilder ( "Library [ " ).append ( title ).append ( " ] was found in plugins: " );
                for ( final Map.Entry<PluginLibrary, PluginInformation> library : sameLibraries.entrySet () )
                {
                    final PluginInformation plugin = library.getValue ();
                    final String libraryVersion = library.getKey ().getVersion ();
                    sb.append ( "[ " ).append ( plugin.toString () ).append ( ", version " ).append ( libraryVersion ).append ( " ] " );
                }
                Log.warn ( this, sb.toString () );
                warned = true;
            }
        }
        if ( warned )
        {
            Log.warn ( this, "Make sure the same library usafe within different plugins was actually your intent" );
        }
    }

    /**
     * Returns whether the list of detected plugins contain a newer version of the specified plugin or not.
     *
     * @param plugin plugin to compare with other detected plugins
     * @return true if the list of detected plugins contain a newer version of the specified plugin, false otherwise
     */
    public boolean isDeprecatedVersion ( final DetectedPlugin<T> plugin )
    {
        return isDeprecatedVersion ( plugin, detectedPlugins );
    }

    /**
     * Returns whether the list of detected plugins contain a newer version of the specified plugin or not.
     *
     * @param plugin          plugin to compare with other detected plugins
     * @param detectedPlugins list of detected plugins
     * @return true if the list of detected plugins contain a newer version of the specified plugin, false otherwise
     */
    public boolean isDeprecatedVersion ( final DetectedPlugin<T> plugin, final List<DetectedPlugin<T>> detectedPlugins )
    {
        final PluginInformation pluginInfo = plugin.getInformation ();
        for ( final DetectedPlugin detectedPlugin : detectedPlugins )
        {
            if ( detectedPlugin != plugin )
            {
                final PluginInformation detectedPluginInfo = detectedPlugin.getInformation ();
                if ( detectedPluginInfo.getId ().equals ( pluginInfo.getId () ) &&
                        detectedPluginInfo.getVersion () != null && pluginInfo.getVersion () != null &&
                        detectedPluginInfo.getVersion ().isNewerThan ( pluginInfo.getVersion () ) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the list of detected plugins contain the same version of the specified plugin or not.
     *
     * @param plugin          plugin to compare with other detected plugins
     * @param detectedPlugins list of detected plugins
     * @return true if the list of detected plugins contain the same version of the specified plugin, false otherwise
     */
    protected boolean isSameVersionAlreadyLoaded ( final DetectedPlugin<T> plugin, final List<DetectedPlugin<T>> detectedPlugins )
    {
        final PluginInformation pluginInfo = plugin.getInformation ();
        for ( final DetectedPlugin detectedPlugin : detectedPlugins )
        {
            if ( detectedPlugin != plugin )
            {
                final PluginInformation detectedPluginInfo = detectedPlugin.getInformation ();
                if ( detectedPluginInfo.getId ().equals ( pluginInfo.getId () ) &&
                        ( detectedPluginInfo.getVersion () == null && pluginInfo.getVersion () == null ||
                                detectedPluginInfo.getVersion ().isSame ( pluginInfo.getVersion () ) ) &&
                        detectedPlugin.getStatus () == PluginStatus.loaded )
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sorting plugins according to their initialization strategies.
     */
    protected void applyInitializationStrategy ()
    {
        // Skip if no available plugins
        if ( availablePlugins.size () == 0 )
        {
            return;
        }

        // Splitting plugins by initial groups
        final List<T> beforeAll = new ArrayList<T> ( availablePlugins.size () );
        final List<T> middle = new ArrayList<T> ( availablePlugins.size () );
        final List<T> afterAll = new ArrayList<T> ( availablePlugins.size () );
        for ( final T plugin : availablePlugins )
        {
            final InitializationStrategy strategy = plugin.getInitializationStrategy ();
            if ( strategy.getId ().equals ( InitializationStrategy.ALL_ID ) )
            {
                switch ( strategy.getType () )
                {
                    case before:
                    {
                        beforeAll.add ( plugin );
                        break;
                    }
                    case any:
                    {
                        middle.add ( plugin );
                        break;
                    }
                    case after:
                    {
                        afterAll.add ( plugin );
                        break;
                    }
                }
            }
            else
            {
                middle.add ( plugin );
            }
        }

        // todo Sort only recently initialized plugins
        // todo Already initialized plugins should not be sorted again, that is useless and might break initial initialization order
        if ( middle.size () == 0 )
        {
            // Combining all plugins into single list
            availablePlugins.clear ();
            availablePlugins.addAll ( beforeAll );
            availablePlugins.addAll ( afterAll );
        }
        else
        {
            // Sorting middle plugins properly
            final List<T> sortedMiddle = new ArrayList<T> ( middle );
            for ( final T plugin : middle )
            {
                final InitializationStrategy strategy = plugin.getInitializationStrategy ();
                final String id = strategy.getId ();
                if ( !plugin.getId ().equals ( id ) )
                {
                    final int oldIndex = sortedMiddle.indexOf ( plugin );
                    for ( int index = 0; index < sortedMiddle.size (); index++ )
                    {
                        if ( sortedMiddle.get ( index ).getId ().equals ( id ) )
                        {
                            switch ( strategy.getType () )
                            {
                                case before:
                                {
                                    sortedMiddle.remove ( oldIndex );
                                    if ( oldIndex < index )
                                    {
                                        sortedMiddle.add ( index - 1, plugin );
                                    }
                                    else
                                    {
                                        sortedMiddle.add ( index, plugin );
                                    }
                                    break;
                                }
                                case after:
                                {
                                    sortedMiddle.remove ( oldIndex );
                                    if ( oldIndex < index )
                                    {
                                        sortedMiddle.add ( index, plugin );
                                    }
                                    else
                                    {
                                        sortedMiddle.add ( index + 1, plugin );
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }

            // Combining all plugins into single list
            availablePlugins.clear ();
            availablePlugins.addAll ( beforeAll );
            availablePlugins.addAll ( sortedMiddle );
            availablePlugins.addAll ( afterAll );
        }
    }

    /**
     * Returns list of detected plugins.
     *
     * @return list of detected plugins
     */
    public List<DetectedPlugin<T>> getDetectedPlugins ()
    {
        return detectedPlugins;
    }

    /**
     * Returns list of available loaded plugins.
     *
     * @return list of available loaded plugins
     */
    public List<T> getAvailablePlugins ()
    {
        return availablePlugins;
    }

    /**
     * Returns available plugin instance by its ID.
     *
     * @param pluginId plugin ID
     * @return available plugin instance by its ID
     */
    public <P extends T> P getPlugin ( final String pluginId )
    {
        return ( P ) availablePluginsById.get ( pluginId );
    }

    /**
     * Returns available plugin instance by its class.
     *
     * @param pluginClass plugin class
     * @return available plugin instance by its class
     */
    public <P extends T> P getPlugin ( final Class<P> pluginClass )
    {
        return ( P ) availablePluginsByClass.get ( pluginClass );
    }

    /**
     * Returns amount of detected plugins.
     *
     * @return amount of detected loaded plugins
     */
    public int getDetectedPluginsAmount ()
    {
        return getDetectedPlugins ().size ();
    }

    /**
     * Returns amount of successfully loaded plugins.
     *
     * @return amount of successfully loaded plugins
     */
    public int getLoadedPluginsAmount ()
    {
        return getAvailablePlugins ().size ();
    }

    /**
     * Returns amount of plugins which have failed to load.
     * There might be a lot of reasons why they failed to load - exception, broken JAR, missing libraries etc.
     * Simply check the log or retrieve failure cause from DetectedPlugin to understand what happened.
     *
     * @return amount of plugins which have failed to load
     */
    public int getFailedPluginsAmount ()
    {
        return getDetectedPlugins ().size () - getAvailablePlugins ().size ();
    }

    /**
     * Returns plugins directory path.
     *
     * @return plugins directory path
     */
    public String getPluginsDirectoryPath ()
    {
        return pluginsDirectoryPath;
    }

    /**
     * Sets plugins directory path.
     *
     * @param path new plugins directory path
     */
    public void setPluginsDirectoryPath ( final String path )
    {
        this.pluginsDirectoryPath = path;
    }

    /**
     * Returns whether plugins directory subfolders should be checked recursively or not.
     *
     * @return true if plugins directory subfolders should be checked recursively, false otherwise
     */
    public boolean isCheckRecursively ()
    {
        return checkRecursively;
    }

    /**
     * Sets whether plugins directory subfolders should be checked recursively or not.
     *
     * @param checkRecursively whether plugins directory subfolders should be checked recursively or not
     */
    public void setCheckRecursively ( final boolean checkRecursively )
    {
        this.checkRecursively = checkRecursively;
    }

    /**
     * Returns plugins directory file filter.
     *
     * @return plugins directory file filter
     */
    public FileFilter getFileFilter ()
    {
        return fileFilter;
    }

    /**
     * Sets plugins directory file filter.
     * Note that setting this filter will not have any effect on plugins which are already initialized.
     *
     * @param filter plugins directory file filter
     */
    public void setFileFilter ( final FileFilter filter )
    {
        this.fileFilter = filter;
    }

    /**
     * Returns whether should create new class loader for each loaded plugin or not.
     *
     * @return true if should create new class loader for each loaded plugin, false otherwise
     */
    public boolean isCreateNewClassLoader ()
    {
        return createNewClassLoader;
    }

    /**
     * Sets whether should create new class loader for each loaded plugin or not.
     *
     * @param createNewClassLoader whether should create new class loader for each loaded plugin or not
     */
    public void setCreateNewClassLoader ( final boolean createNewClassLoader )
    {
        this.createNewClassLoader = createNewClassLoader;
    }

    /**
     * Returns special filter that filters out unwanted plugins before their initialization.
     *
     * @return special filter that filters out unwanted plugins before their initialization
     */
    public Filter<DetectedPlugin<T>> getPluginFilter ()
    {
        return pluginFilter;
    }

    /**
     * Sets special filter that filters out unwanted plugins before their initialization.
     *
     * @param pluginFilter special filter that filters out unwanted plugins before their initialization
     */
    public void setPluginFilter ( final Filter<DetectedPlugin<T>> pluginFilter )
    {
        this.pluginFilter = pluginFilter;
    }

    /**
     * Returns whether should allow loading multiply plugins with the same ID or not.
     *
     * @return true if should allow loading multiply plugins with the same ID, false otherwise
     */
    public boolean isAllowSimilarPlugins ()
    {
        return allowSimilarPlugins;
    }

    /**
     * Sets whether should allow loading multiply plugins with the same ID or not.
     *
     * @param allow whether should allow loading multiply plugins with the same ID or not
     */
    public void setAllowSimilarPlugins ( final boolean allow )
    {
        this.allowSimilarPlugins = allow;
    }

    /**
     * Adds plugins listener.
     *
     * @param listener new plugins listener
     */
    public void addPluginsListener ( final PluginsListener<T> listener )
    {
        listeners.add ( listener );
    }

    /**
     * Removes plugins listener.
     *
     * @param listener plugins listener to remove
     */
    public void removePluginsListener ( final PluginsListener<T> listener )
    {
        listeners.remove ( listener );
    }

    /**
     * Informs about plugins check operation start.
     *
     * @param directory checked plugins directory path
     * @param recursive whether plugins directory subfolders are checked recursively or not
     */
    public void firePluginsCheckStarted ( final String directory, final boolean recursive )
    {
        for ( final PluginsListener<T> listener : CollectionUtils.copy ( listeners ) )
        {
            listener.pluginsCheckStarted ( directory, recursive );
        }
    }

    /**
     * Informs about plugins check operation end.
     *
     * @param directory checked plugins directory path
     * @param recursive whether plugins directory subfolders are checked recursively or not
     */
    public void firePluginsCheckEnded ( final String directory, final boolean recursive )
    {
        for ( final PluginsListener<T> listener : CollectionUtils.copy ( listeners ) )
        {
            listener.pluginsCheckEnded ( directory, recursive );
        }
    }

    /**
     * Informs about newly detected plugins.
     *
     * @param plugins newly detected plugins list
     */
    public void firePluginsDetected ( final List<DetectedPlugin<T>> plugins )
    {
        for ( final PluginsListener<T> listener : CollectionUtils.copy ( listeners ) )
        {
            listener.pluginsDetected ( CollectionUtils.copy ( plugins ) );
        }
    }

    /**
     * Informs about newly initialized plugins.
     *
     * @param plugins newly initialized plugins list
     */
    public void firePluginsInitialized ( final List<T> plugins )
    {
        for ( final PluginsListener<T> listener : CollectionUtils.copy ( listeners ) )
        {
            listener.pluginsInitialized ( CollectionUtils.copy ( plugins ) );
        }
    }
}