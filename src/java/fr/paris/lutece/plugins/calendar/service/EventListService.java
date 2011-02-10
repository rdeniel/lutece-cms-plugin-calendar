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
package fr.paris.lutece.plugins.calendar.service;

import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.plugins.calendar.web.EventList;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * This class provides an event list service.
 */
public class EventListService
{
    private static EventListService _singleton = new EventListService(  );

    /**
     * Gets the unique instance of the EventListService
     * @return the unique instance of the EventListService
     */
    public static EventListService getInstance(  )
    {
        return _singleton;
    }

    /**
     * Load an eventlist object from its keyname
     * @param strKeyName The keyname of the EventList
     * @return An EventList object
     */
    public EventList getEventList( String strKeyName )
    {
        EventList eventlist = null;
        String strClassKey = Constants.PROPERTY_EVENTLIST + strKeyName + Constants.SUFFIX_CLASS;
        String strClass = AppPropertiesService.getProperty( strClassKey );

        try
        {
            eventlist = (EventList) Class.forName( strClass ).newInstance(  );
        }
        catch ( ClassNotFoundException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( IllegalAccessException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( InstantiationException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return eventlist;
    }
}
