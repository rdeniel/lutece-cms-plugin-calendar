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
package fr.paris.lutece.plugins.calendar.service.search;

import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.business.OccurrenceEvent;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.service.EventComparator;
import fr.paris.lutece.plugins.calendar.service.EventImageResourceService;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.SearchResult;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import javax.servlet.http.HttpServletRequest;


/**
 * DocumentSearchService
 */
public class CalendarSearchService
{
    private static final String BEAN_SEARCH_ENGINE = "calendar.calendarLuceneSearchEngine";
    private static final String REGEX_ID_EVENT = "^[\\d]+_" + Constants.CALENDAR_SHORT_NAME + "$";
    private static final String REGEX_ID_DOCUMENT = "^[\\d]+_" + Constants.DOCUMENT_SHORT_NAME + "$";
    private static final String UNDERSCORE = "_";

    // Constants corresponding to the variables defined in the lutece.properties file
    private static CalendarSearchService _singleton;

    /**
     * Get the HelpdeskSearchService instance
     *
     * @return The {@link CalendarSearchService}
     */
    public static CalendarSearchService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new CalendarSearchService(  );
        }

        return _singleton;
    }

    /**
     * Return search results
     * @param arrayAgendaIds The calendar ids
     * @param arrayCategory the category ids
     * @param strQuery The search query
     * @param dateBegin The date begin
     * @param dateEnd The date end
     * @param request The {@link HttpServletRequest}
     * @return Results as a collection of SearchResult
     */
    public List<Event> getSearchResults( String[] arrayAgendaIds, String[] arrayCategory, String strQuery,
        Date dateBegin, Date dateEnd, HttpServletRequest request, Plugin plugin )
    {
        List<Event> listEvent = new ArrayList<Event>(  );
        HashMap<String, Event> hmListEvent = new HashMap<String, Event>(  );
        CalendarSearchEngine engine = (CalendarSearchEngine) SpringContextService.getPluginBean( plugin.getName(  ),
                BEAN_SEARCH_ENGINE );
        List<SearchResult> listResults = engine.getSearchResults( arrayAgendaIds, arrayCategory, strQuery, dateBegin,
                dateEnd, request );

        for ( SearchResult searchResult : listResults )
        {
            if ( ( ( searchResult.getId(  ) != null ) && searchResult.getId(  ).matches( REGEX_ID_EVENT ) ) ||
                    ( ( searchResult.getId(  ) != null ) && searchResult.getId(  ).matches( REGEX_ID_DOCUMENT ) ) )
            {
                try
                {
                    //Retrieve all occurences of an event
                    OccurrenceEvent occurence = CalendarHome.findOccurrence( Integer.parseInt( 
                                searchResult.getId(  ).substring( 0, searchResult.getId(  ).indexOf( UNDERSCORE ) ) ),
                            plugin );

                    //Retrieve the event related to the occurence
                    SimpleEvent event = CalendarHome.findEvent( occurence.getEventId(  ), plugin );
                    event.setUrl( searchResult.getUrl(  ) );
                    event.setType( searchResult.getType(  ) );
                    event.setImageUrl( EventImageResourceService.getInstance(  ).getResourceImageEvent( event.getId(  ) ) );

                    event.setDescription( searchResult.getSummary(  ) );

                    //if it is not already present stroring the event to the temp list
                    if ( !hmListEvent.containsKey( Integer.toString( event.getId(  ) ) ) )
                    {
                        hmListEvent.put( Integer.toString( event.getId(  ) ), event );
                    }
                }
                catch ( NullPointerException e )
                {
                	AppLogService.error( e );
                }
            }
        }
        
        //Adding the event to final list
        if ( !hmListEvent.isEmpty(  ) )
        {
            Collection<Event> collection = hmListEvent.values(  );
            listEvent = new ArrayList<Event>( collection );
            Collections.sort( listEvent, new EventComparator(  ) );
        }

        return listEvent;
    }
}
