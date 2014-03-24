/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import fr.paris.lutece.portal.business.stylesheet.StyleSheet;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;


/**
 * This class provides Data Access methods for StyleSheet objects
 */
public final class CalendarStyleSheetDAO implements ICalendarStyleSheetDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = " SELECT max(id_stylesheet) FROM calendar_export_stylesheets ";
    private static final String SQL_QUERY_SELECT = " SELECT a.description , a.file_name , a.source"
            + " FROM calendar_export_stylesheets a WHERE a.id_stylesheet = ? ";
    private static final String SQL_QUERY_SELECT_LIST = " SELECT a.id_stylesheet, a.description , a.file_name"
            + " FROM calendar_export_stylesheets a where a.description NOT LIKE '%rss%' ORDER BY a.description ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO calendar_export_stylesheets ( id_stylesheet , description , file_name, source ) "
            + " VALUES ( ?, ? ,?, ? )";
    private static final String SQL_QUERY_DELETE = " DELETE FROM calendar_export_stylesheets WHERE id_stylesheet = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE calendar_export_stylesheets SET id_stylesheet = ?, description = ?, file_name = ?, source = ? WHERE id_stylesheet = ?  ";
    private static final String SQL_QUERY_UPDATE_EXTENSION = " UPDATE calendar_export_stylesheets SET extension = ? WHERE id_stylesheet = ?  ";
    private static final String SQL_QUERY_SELECT_EXTENSION = " SELECT a.extension FROM calendar_export_stylesheets a WHERE a.id_stylesheet = ? ";
    private static final String SQL_QUERY_INSERT_EXTENSION = " UPDATE calendar_export_stylesheets SET extension = ? WHERE id_stylesheet = ?  ";

    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
     * Generates a new primary key
     * @param plugin The plugin
     * @return The new primary key
     */
    int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey;

        if ( !daoUtil.next( ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;

        daoUtil.free( );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     * @param stylesheet The StyleSheet object
     * @param strExtension the extension
     * @param plugin The plugin
     */
    public synchronized void insert( StyleSheet stylesheet, String strExtension, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        stylesheet.setId( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, stylesheet.getId( ) );
        daoUtil.setString( 2, stylesheet.getDescription( ) );
        daoUtil.setString( 3, stylesheet.getFile( ) );
        daoUtil.setBytes( 4, stylesheet.getSource( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );

        insertExtension( stylesheet.getId( ), strExtension, plugin );
    }

    /**
     * Load the data of Stylesheet from the table
     * @param nIdStylesheet the identifier of the Stylesheet to load
     * @param plugin Plugin
     * @return stylesheet
     */
    public StyleSheet load( int nIdStylesheet, Plugin plugin )
    {
        StyleSheet stylesheet = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdStylesheet );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            stylesheet = new StyleSheet( );
            stylesheet.setId( nIdStylesheet );
            stylesheet.setDescription( daoUtil.getString( 1 ) );
            stylesheet.setFile( daoUtil.getString( 2 ) );
            stylesheet.setSource( daoUtil.getBytes( 3 ) );
        }

        daoUtil.free( );

        return stylesheet;
    }

    /**
     * Delete the StyleSheet from the database whose identifier is specified
     * in parameter
     * @param nIdStylesheet the identifier of the StyleSheet to delete
     * @param plugin Plugin
     */
    public void delete( int nIdStylesheet, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdStylesheet );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Load the list of stylesheet
     * @param plugin Plugin
     * @return the list of the StyleSheet in form of a collection of StyleSheet
     *         objects
     */
    public Collection<StyleSheet> selectStyleSheetList( Plugin plugin )
    {
        Collection<StyleSheet> stylesheetList = new ArrayList<StyleSheet>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LIST, plugin );

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            StyleSheet stylesheet = new StyleSheet( );

            stylesheet.setId( daoUtil.getInt( 1 ) );
            stylesheet.setDescription( daoUtil.getString( 2 ) );
            stylesheet.setFile( daoUtil.getString( 3 ) );
            stylesheetList.add( stylesheet );
        }

        daoUtil.free( );

        return stylesheetList;
    }

    /**
     * Update the record in the table
     * @param stylesheet The stylesheet
     * @param plugin Plugin
     */
    public void store( StyleSheet stylesheet, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, stylesheet.getId( ) );
        daoUtil.setString( 2, stylesheet.getDescription( ) );
        daoUtil.setString( 3, stylesheet.getFile( ) );
        daoUtil.setBytes( 4, stylesheet.getSource( ) );
        daoUtil.setInt( 5, stylesheet.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Insertion of the extension of an export file in the database
     * @param nId the id
     * @param strExtension the extension file
     * @param plugin Plugin
     */
    public void insertExtension( int nId, String strExtension, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_EXTENSION, plugin );
        daoUtil.setString( 1, strExtension );
        daoUtil.setInt( 2, nId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Select the extension file related to the export stylesheet
     * @param nId the identifier of the stylesheet
     * @param plugin Plugin
     * @return the extension file
     */
    public String selectExtension( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EXTENSION, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery( );

        String strExtension = StringUtils.EMPTY;

        if ( daoUtil.next( ) )
        {
            strExtension = daoUtil.getString( 1 );
        }

        daoUtil.free( );

        return strExtension;
    }

    /**
     * Update the the extension file whose identifier is specified in parameter
     * @param nId the stylesheet id
     * @param strExtension the new extension file
     * @param plugin Plugin
     */
    public void updateExtension( int nId, String strExtension, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_EXTENSION, plugin );
        daoUtil.setString( 1, strExtension );
        daoUtil.setInt( 2, nId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }
}
