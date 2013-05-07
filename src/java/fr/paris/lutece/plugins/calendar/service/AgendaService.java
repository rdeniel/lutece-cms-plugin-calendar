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
package fr.paris.lutece.plugins.calendar.service;

import java.util.HashMap;
import java.util.Map;

import fr.paris.lutece.plugins.calendar.business.parameter.CalendarParameterHome;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.resource.ResourceService;


/**
 * This class provides agenda access services : loading, access to configuration parameters
 * @deprecated Use {@link CalendarService} Instead
 */
public final class AgendaService extends ResourceService
{
    private static final String PROPERTY_NAME = "calendar.agenda.service.name";
    private static final String PROPERTY_CACHE = "calendar.agenda.service.cache";
    private static final String PROPERTY_LOADERS = "calendar.agenda.service.loaders";
    private static AgendaService _singleton = new AgendaService(  );

    /** Creates a new instance of AgendaService */
    private AgendaService(  )
    {
        super(  );
        setNameKey( PROPERTY_NAME );
        setCacheKey( PROPERTY_CACHE );
    }

    /**
     * Initialize the Agenda service
     *
     */
    public void init(  )
    {
        AgendaResource.init(  );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     * @deprecated Use CalendarService Instead
     */
    public static AgendaService getInstance(  )
    {
        return _singleton;
    }

    /**
     * Returns the property key that contains the loaders list
     * @return A property key
     */
    protected String getLoadersProperty(  )
    {
        return PROPERTY_LOADERS;
    }

    /**
     * Load and deliver an agenda by its key name
     * @param strAgendaKeyName The agenda key name
     * @return An Agenda object
     * @deprecated Use CalendarService Instead
     */
    public AgendaResource getAgendaResource( String strAgendaKeyName )
    {
        return (AgendaResource) getResource( strAgendaKeyName );
    }
    
    /**
     * Build the advanced parameters management
     * @param user the current user
     * @return The model for the advanced parameters
     * @deprecated Use CalendarService Instead
     */
    public Map<String, Object> getManageAdvancedParameters( AdminUser user )
    {
    	Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
    	Map<String, Object> model = new HashMap<String, Object>(  );
    	
    	model.put( Constants.MARK_CALENDAR_PARAMETERS, CalendarParameterHome.findAll( plugin ) );
    	
    	return model;
    }
}
