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
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import java.util.Map;


/**
 * 
 * CalendarParameterHome
 * 
 */
public final class CalendarParameterHome
{
    // Static variable pointed at the DAO instance
    private static ICalendarParameterDAO _dao = SpringContextService.getBean( "calendar.calendarParameterDAO" );

    /**
     * Default constructor
     */
    private CalendarParameterHome( )
    {
    }

    /**
     * Load the parameter value
     * @param strParameterKey the parameter key
     * @param plugin Plugin
     * @return The parameter value
     */
    public static ReferenceItem findByKey( String strParameterKey, Plugin plugin )
    {
        return _dao.load( strParameterKey, plugin );
    }

    /**
     * Update the parameter value
     * @param userParam The parameter key
     * @param plugin Plugin
     */
    public static void update( ReferenceItem userParam, Plugin plugin )
    {
        _dao.store( userParam, plugin );
    }

    /**
     * Find all parameters
     * @param plugin Plugin
     * @return all the parameters
     */
    public static Map<String, String> findAll( Plugin plugin )
    {
        return _dao.selectAll( plugin );
    }

    /**
     * Select all the parameters
     * @param plugin plugin
     * @return all the parameters
     */
    public static ReferenceList findParametersList( Plugin plugin )
    {
        return _dao.selectParametersList( plugin );
    }
}
