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
package fr.paris.lutece.plugins.calendar.business.parameter;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * CalendarParameterDAO
 * 
 */
public class CalendarParameterDAO implements ICalendarParameterDAO
{
    private static final String SQL_QUERY_SELECT = " SELECT parameter_value FROM calendar_parameter WHERE parameter_key = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE calendar_parameter SET parameter_value = ? WHERE parameter_key = ? ";
    private static final String SQL_QUERY_SELECT_ALL = " SELECT parameter_key, parameter_value FROM calendar_parameter ORDER BY parameter_key";

    /**
     * Load the parameter value
     * @param strParameterKey the parameter key
     * @param plugin the plugin
     * @return The parameter value
     */
    public ReferenceItem load( String strParameterKey, Plugin plugin )
    {
        ReferenceItem userParam = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setString( 1, strParameterKey );
        daoUtil.executeQuery( );

        if ( daoUtil.next( ) )
        {
            userParam = new ReferenceItem( );
            userParam.setCode( strParameterKey );
            userParam.setName( daoUtil.getString( 1 ) );
        }

        daoUtil.free( );

        return userParam;
    }

    /**
     * Update the parameter value
     * @param userParam The parameter value
     * @param plugin The plugin
     */
    public void store( ReferenceItem userParam, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, userParam.getName( ) );
        daoUtil.setString( 2, userParam.getCode( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Select all the parameters
     * @param plugin Plugin
     * @return all the parameters
     */
    public Map<String, String> selectAll( Plugin plugin )
    {
        Map<String, String> parametersList = new HashMap<String, String>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            parametersList.put( daoUtil.getString( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );

        return parametersList;
    }

    /**
     * Select all the parameters
     * @param plugin plugin
     * @return all the parameters
     */
    public ReferenceList selectParametersList( Plugin plugin )
    {
        ReferenceList parametersList = new ReferenceList( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            parametersList.addItem( daoUtil.getString( 1 ), daoUtil.getString( 2 ) );
        }

        daoUtil.free( );

        return parametersList;
    }
}
