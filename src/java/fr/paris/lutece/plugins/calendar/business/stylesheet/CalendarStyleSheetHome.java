/*
 * Copyright (c) 2002-2009, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.calendar.business.stylesheet;

import java.util.Collection;

import fr.paris.lutece.portal.business.stylesheet.StyleSheet;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 * This class provides instances management methods (create, find, ...) for Stylesheet objects
 */
public final class CalendarStyleSheetHome
{
    // Static variable pointed to the DAO instance
    private static ICalendarStyleSheetDAO _dao = (ICalendarStyleSheetDAO) SpringContextService.getPluginBean( "calendar",
            "calendar.calendarStyleSheetDAO" );

    /**
     * Creates a new StyleSheetHome object.
     */
    private CalendarStyleSheetHome(  )
    {
    }

    /**
     * Creation of an instance of a Stylesheet file in the database
     * @param stylesheet An instance of a stylesheet which contains the informations to store
     * @param plugin Plugin
     * @return The instance of the stylesheet which has been created.
     */
    public static StyleSheet create( StyleSheet stylesheet, String strExtension, Plugin plugin )
    {
        _dao.insert( stylesheet, strExtension, plugin );

        return stylesheet;
    }

    /**
     * Deletes the StylesSheet whose identifier is specified in parameter
     * @param nId the identifier of the stylesheet to delete
     * @param plugin Plugin
     */
    public static void remove( int nId, Plugin plugin )
    {
        _dao.delete( nId, plugin );
    }

    /**
     * Update the StylesSheet whose identifier is specified in parameter
     * @param stylesheet the stylesheet to update
     * @param plugin Plugin
     */
    public static void update( StyleSheet stylesheet, Plugin plugin )
    {
        _dao.store( stylesheet, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a stylesheet file whose identifier is specified in parameter
     * @param nKey the stylesheet primary key
     * @param plugin Plugin
     * @return the instance of the styleSheet whose identifier is the nKey
     */
    public static StyleSheet findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Returns a collection of StyleSheet objet
     * @param nModeId The mode identifier
     * @param plugin Plugin
     * @return A collection of StyleSheet object
     */
    public static Collection<StyleSheet> getStyleSheetList( Plugin plugin )
    {
        return _dao.selectStyleSheetList( plugin );
    }

    /**
     * Insertion of the extension of an export file in the database
     * @param strExtension the extension file
     * @param plugin Plugin
     */
    public static void insertExtension( int nId, String strExtension, Plugin plugin )
    {
        _dao.insertExtension( nId, strExtension, plugin );
    }

    /**
     * Select the extension file related to the export stylesheet
     * @param nId the identifier of the stylesheet
     * @param plugin Plugin
     * @return the extension file
     */
    public static String getExtension( int nId, Plugin plugin )
    {
        return _dao.selectExtension( nId, plugin );
    }

    /**
     * Update the the extension file whose identifier is specified in parameter
     * @param nId the stylesheet id
     * @param strExtension the new extension file
     * @param plugin Plugin
     */
    public static void updateExtension( int nId, String strExtension, Plugin plugin )
    {
        _dao.updateExtension( nId, strExtension, plugin );
    }
}
