/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.SimpleAgenda;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.resource.Resource;
import fr.paris.lutece.portal.service.resource.ResourceLoader;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.Collection;
import java.util.List;


/**
 *  This loader fetches agendas stored in a database
 */
public class AgendaLoaderDatabase implements ResourceLoader
{
    private Plugin _plugin;
    private String _strPluginName = "calendar";

    /**
     * The loader which fetches agendas from the database
     */
    public AgendaLoaderDatabase(  )
    {
        super(  );
    }

    /**
     * Gets all the agenda resources from the database
     * @return A collection of agenda resource
     */
    public Collection<AgendaResource> getResources(  )
    {
        if ( _plugin == null )
        {
            _plugin = PluginService.getPlugin( _strPluginName );
        }

        List<AgendaResource> listAgendaPages = CalendarHome.findAgendaResourcesList( _plugin );

        for ( AgendaResource agendaDatabasePage : listAgendaPages )
        {
            loadAgenda( agendaDatabasePage );
            listAgendaPages.add( agendaDatabasePage );
        }

        return listAgendaPages;
    }

    /**
     * Get a resource by its Id
     * @param strId The resource Id
     * @return The resource
     */
    public Resource getResource( String strId )
    {
        if ( _plugin == null )
        {
            _plugin = PluginService.getPlugin( _strPluginName );
        }

        AgendaResource agenda = new AgendaResource(  );

        try
        {
            agenda = CalendarHome.findAgendaResource( Integer.parseInt( strId ), _plugin );
            loadAgenda( agenda );
        }
        catch ( Exception e )
        {
            agenda = null;
        }

        return agenda;
    }

    /**
     * Return the agenda
     * @param agenda The agenda
     */
    public void loadAgenda( AgendaResource agenda )
    {
        SimpleAgenda a = new SimpleAgenda(  );

        for ( SimpleEvent event : CalendarHome.findEventsList( Integer.parseInt( agenda.getId(  ) ), 1, _plugin ) )
        {
            a.addEvent( event );
        }

        if ( a != null )
        {
            a.setName( agenda.getName(  ) );
            a.setKeyName( agenda.getId(  ) );
            agenda.setAgenda( a );
            agenda.setResourceType( AppPropertiesService.getProperty( Constants.PROPERTY_READ_WRITE ) );
        }
    }
}
