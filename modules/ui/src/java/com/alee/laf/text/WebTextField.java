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

package com.alee.laf.text;

import com.alee.extended.painter.Painter;
import com.alee.global.StyleConstants;
import com.alee.laf.WebLookAndFeel;
import com.alee.managers.language.LanguageManager;
import com.alee.managers.language.LanguageMethods;
import com.alee.managers.language.updaters.LanguageUpdater;
import com.alee.managers.settings.DefaultValue;
import com.alee.managers.settings.SettingsManager;
import com.alee.managers.settings.SettingsMethods;
import com.alee.managers.settings.SettingsProcessor;
import com.alee.utils.ReflectUtils;
import com.alee.utils.SizeUtils;
import com.alee.utils.SwingUtils;
import com.alee.utils.laf.ShapeProvider;
import com.alee.utils.swing.FontMethods;
import com.alee.utils.swing.SizeMethods;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;

/**
 * User: mgarin Date: 28.06.11 Time: 1:13
 */

public class WebTextField extends JTextField
        implements ShapeProvider, LanguageMethods, SettingsMethods, FontMethods<WebTextField>, SizeMethods<WebTextField>
{
    public WebTextField ()
    {
        super ();
    }

    public WebTextField ( final boolean drawBorder )
    {
        super ();
        setDrawBorder ( drawBorder );
    }

    public WebTextField ( final String text )
    {
        super ( text );
    }

    public WebTextField ( final String text, final boolean drawBorder )
    {
        super ( text );
        setDrawBorder ( drawBorder );
    }

    public WebTextField ( final int columns )
    {
        super ( columns );
    }

    public WebTextField ( final int columns, final boolean drawBorder )
    {
        super ( columns );
        setDrawBorder ( drawBorder );
    }

    public WebTextField ( final String text, final int columns )
    {
        super ( text, columns );
    }

    public WebTextField ( final String text, final int columns, final boolean drawBorder )
    {
        super ( text, columns );
        setDrawBorder ( drawBorder );
    }

    public WebTextField ( final Document doc, final String text, final int columns )
    {
        super ( doc, text, columns );
    }

    public WebTextField ( final Document doc, final String text, final int columns, final boolean drawBorder )
    {
        super ( doc, text, columns );
        setDrawBorder ( drawBorder );
    }

    /**
     * Additional component methods
     */

    public void clear ()
    {
        setText ( "" );
    }

    /**
     * UI methods
     */

    public boolean isDrawBorder ()
    {
        return getWebUI ().isDrawBorder ();
    }

    public void setDrawBorder ( final boolean drawBorder )
    {
        getWebUI ().setDrawBorder ( drawBorder );
    }

    public boolean isDrawFocus ()
    {
        return getWebUI ().isDrawFocus ();
    }

    public void setDrawFocus ( final boolean drawFocus )
    {
        getWebUI ().setDrawFocus ( drawFocus );
    }

    public JComponent getLeadingComponent ()
    {
        return getWebUI ().getLeadingComponent ();
    }

    public void setLeadingComponent ( final JComponent leadingComponent )
    {
        getWebUI ().setLeadingComponent ( leadingComponent );
    }

    public JComponent getTrailingComponent ()
    {
        return getWebUI ().getTrailingComponent ();
    }

    public void setTrailingComponent ( final JComponent trailingComponent )
    {
        getWebUI ().setTrailingComponent ( trailingComponent );
    }

    public void setMargin ( final int top, final int left, final int bottom, final int right )
    {
        setMargin ( new Insets ( top, left, bottom, right ) );
    }

    public void setMargin ( final int spacing )
    {
        setMargin ( spacing, spacing, spacing, spacing );
    }

    public void setFieldMargin ( final Insets margin )
    {
        getWebUI ().setFieldMargin ( margin );
    }

    public void setFieldMargin ( final int top, final int left, final int bottom, final int right )
    {
        setFieldMargin ( new Insets ( top, left, bottom, right ) );
    }

    public void setFieldMargin ( final int spacing )
    {
        setFieldMargin ( spacing, spacing, spacing, spacing );
    }

    public Insets getFieldMargin ()
    {
        return getWebUI ().getFieldMargin ();
    }

    public int getRound ()
    {
        return getWebUI ().getRound ();
    }

    public void setRound ( final int round )
    {
        getWebUI ().setRound ( round );
    }

    public boolean isDrawShade ()
    {
        return getWebUI ().isDrawShade ();
    }

    public void setDrawShade ( final boolean drawShade )
    {
        getWebUI ().setDrawShade ( drawShade );
    }

    public int getShadeWidth ()
    {
        return getWebUI ().getShadeWidth ();
    }

    public void setShadeWidth ( final int shadeWidth )
    {
        getWebUI ().setShadeWidth ( shadeWidth );
    }

    public boolean isDrawBackground ()
    {
        return getWebUI ().isDrawBackground ();
    }

    public void setDrawBackground ( final boolean drawBackground )
    {
        getWebUI ().setDrawBackground ( drawBackground );
    }

    public boolean isWebColored ()
    {
        return getWebUI ().isWebColored ();
    }

    public void setWebColored ( final boolean webColored )
    {
        getWebUI ().setWebColored ( webColored );
    }

    public Painter getPainter ()
    {
        return getWebUI ().getPainter ();
    }

    public void setPainter ( final Painter painter )
    {
        getWebUI ().setPainter ( painter );
    }

    public String getInputPrompt ()
    {
        return getWebUI ().getInputPrompt ();
    }

    public void setInputPrompt ( final String inputPrompt )
    {
        getWebUI ().setInputPrompt ( inputPrompt );
    }

    public Font getInputPromptFont ()
    {
        return getWebUI ().getInputPromptFont ();
    }

    public void setInputPromptFont ( final Font inputPromptFont )
    {
        getWebUI ().setInputPromptFont ( inputPromptFont );
    }

    public Color getInputPromptForeground ()
    {
        return getWebUI ().getInputPromptForeground ();
    }

    public void setInputPromptForeground ( final Color inputPromptForeground )
    {
        getWebUI ().setInputPromptForeground ( inputPromptForeground );
    }

    public int getInputPromptPosition ()
    {
        return getWebUI ().getInputPromptPosition ();
    }

    public void setInputPromptPosition ( final int inputPromptPosition )
    {
        getWebUI ().setInputPromptPosition ( inputPromptPosition );
    }

    public boolean isHideInputPromptOnFocus ()
    {
        return getWebUI ().isHideInputPromptOnFocus ();
    }

    public void setHideInputPromptOnFocus ( final boolean hideInputPromptOnFocus )
    {
        getWebUI ().setHideInputPromptOnFocus ( hideInputPromptOnFocus );
    }

    @Override
    public Shape provideShape ()
    {
        return getWebUI ().provideShape ();
    }

    public WebTextFieldUI getWebUI ()
    {
        return ( WebTextFieldUI ) getUI ();
    }

    @Override
    public void updateUI ()
    {
        if ( getUI () == null || !( getUI () instanceof WebTextFieldUI ) )
        {
            try
            {
                setUI ( ( WebTextFieldUI ) ReflectUtils.createInstance ( WebLookAndFeel.textFieldUI ) );
            }
            catch ( Throwable e )
            {
                e.printStackTrace ();
                setUI ( new WebTextFieldUI () );
            }
        }
        else
        {
            setUI ( getUI () );
        }
        invalidate ();
    }

    /**
     * Language methods
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLanguage ( final String key, final Object... data )
    {
        LanguageManager.registerComponent ( this, key, data );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLanguage ( final Object... data )
    {
        LanguageManager.updateComponent ( this, data );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLanguage ( final String key, final Object... data )
    {
        LanguageManager.updateComponent ( this, key, data );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLanguage ()
    {
        LanguageManager.unregisterComponent ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLanguageSet ()
    {
        return LanguageManager.isRegisteredComponent ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLanguageUpdater ( final LanguageUpdater updater )
    {
        LanguageManager.registerLanguageUpdater ( this, updater );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeLanguageUpdater ()
    {
        LanguageManager.unregisterLanguageUpdater ( this );
    }

    /**
     * Settings methods
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSettings ( final String key )
    {
        SettingsManager.registerComponent ( this, key );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends DefaultValue> void registerSettings ( final String key, final Class<T> defaultValueClass )
    {
        SettingsManager.registerComponent ( this, key, defaultValueClass );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSettings ( final String key, final Object defaultValue )
    {
        SettingsManager.registerComponent ( this, key, defaultValue );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSettings ( final String group, final String key )
    {
        SettingsManager.registerComponent ( this, group, key );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends DefaultValue> void registerSettings ( final String group, final String key, final Class<T> defaultValueClass )
    {
        SettingsManager.registerComponent ( this, group, key, defaultValueClass );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSettings ( final String group, final String key, final Object defaultValue )
    {
        SettingsManager.registerComponent ( this, group, key, defaultValue );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSettings ( final String key, final boolean loadInitialSettings, final boolean applySettingsChanges )
    {
        SettingsManager.registerComponent ( this, key, loadInitialSettings, applySettingsChanges );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends DefaultValue> void registerSettings ( final String key, final Class<T> defaultValueClass,
                                                            final boolean loadInitialSettings, final boolean applySettingsChanges )
    {
        SettingsManager.registerComponent ( this, key, defaultValueClass, loadInitialSettings, applySettingsChanges );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSettings ( final String key, final Object defaultValue, final boolean loadInitialSettings,
                                   final boolean applySettingsChanges )
    {
        SettingsManager.registerComponent ( this, key, defaultValue, loadInitialSettings, applySettingsChanges );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends DefaultValue> void registerSettings ( final String group, final String key, final Class<T> defaultValueClass,
                                                            final boolean loadInitialSettings, final boolean applySettingsChanges )
    {
        SettingsManager.registerComponent ( this, group, key, defaultValueClass, loadInitialSettings, applySettingsChanges );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSettings ( final String group, final String key, final Object defaultValue, final boolean loadInitialSettings,
                                   final boolean applySettingsChanges )
    {
        SettingsManager.registerComponent ( this, group, key, defaultValue, loadInitialSettings, applySettingsChanges );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSettings ( final SettingsProcessor settingsProcessor )
    {
        SettingsManager.registerComponent ( this, settingsProcessor );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterSettings ()
    {
        SettingsManager.unregisterComponent ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadSettings ()
    {
        SettingsManager.loadComponentSettings ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveSettings ()
    {
        SettingsManager.saveComponentSettings ( this );
    }

    /**
     * Font methods
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setPlainFont ()
    {
        return SwingUtils.setPlainFont ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setPlainFont ( final boolean apply )
    {
        return SwingUtils.setPlainFont ( this, apply );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPlainFont ()
    {
        return SwingUtils.isPlainFont ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setBoldFont ()
    {
        return SwingUtils.setBoldFont ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setBoldFont ( final boolean apply )
    {
        return SwingUtils.setBoldFont ( this, apply );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBoldFont ()
    {
        return SwingUtils.isBoldFont ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setItalicFont ()
    {
        return SwingUtils.setItalicFont ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setItalicFont ( final boolean apply )
    {
        return SwingUtils.setItalicFont ( this, apply );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isItalicFont ()
    {
        return SwingUtils.isItalicFont ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setFontStyle ( final boolean bold, final boolean italic )
    {
        return SwingUtils.setFontStyle ( this, bold, italic );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setFontStyle ( final int style )
    {
        return SwingUtils.setFontStyle ( this, style );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setFontSize ( final int fontSize )
    {
        return SwingUtils.setFontSize ( this, fontSize );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField changeFontSize ( final int change )
    {
        return SwingUtils.changeFontSize ( this, change );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFontSize ()
    {
        return SwingUtils.getFontSize ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setFontSizeAndStyle ( final int fontSize, final boolean bold, final boolean italic )
    {
        return SwingUtils.setFontSizeAndStyle ( this, fontSize, bold, italic );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setFontSizeAndStyle ( final int fontSize, final int style )
    {
        return SwingUtils.setFontSizeAndStyle ( this, fontSize, style );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setFontName ( final String fontName )
    {
        return SwingUtils.setFontName ( this, fontName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFontName ()
    {
        return SwingUtils.getFontName ( this );
    }

    /**
     * Styled field short creation methods
     */

    public static WebTextField createWebTextField ()
    {
        return createWebTextField ( StyleConstants.drawBorder );
    }

    public static WebTextField createWebTextField ( final boolean drawBorder )
    {
        return createWebTextField ( drawBorder, StyleConstants.bigRound );
    }

    public static WebTextField createWebTextField ( final boolean drawBorder, final int round )
    {
        return createWebTextField ( drawBorder, round, StyleConstants.shadeWidth );
    }

    public static WebTextField createWebTextField ( final boolean drawBorder, final int round, final int shadeWidth )
    {
        final WebTextField webTextField = new WebTextField ();
        webTextField.setDrawBorder ( drawBorder );
        webTextField.setRound ( round );
        webTextField.setShadeWidth ( shadeWidth );
        return webTextField;
    }

    /**
     * Size methods.
     */

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPreferredWidth ()
    {
        return SizeUtils.getPreferredWidth ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setPreferredWidth ( final int preferredWidth )
    {
        return SizeUtils.setPreferredWidth ( this, preferredWidth );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPreferredHeight ()
    {
        return SizeUtils.getPreferredHeight ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setPreferredHeight ( final int preferredHeight )
    {
        return SizeUtils.setPreferredHeight ( this, preferredHeight );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinimumWidth ()
    {
        return SizeUtils.getMinimumWidth ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setMinimumWidth ( final int minimumWidth )
    {
        return SizeUtils.setMinimumWidth ( this, minimumWidth );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinimumHeight ()
    {
        return SizeUtils.getMinimumHeight ( this );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebTextField setMinimumHeight ( final int minimumHeight )
    {
        return SizeUtils.setMinimumHeight ( this, minimumHeight );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize ()
    {
        return SizeUtils.getPreferredSize ( this, super.getPreferredSize () );
    }
}