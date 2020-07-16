/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.calendar.modules.ical;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;


/**
 * This class is an implementation of an Agenda using
 * the iCalendar format (RFC 2445).
 */
public class ICalAgenda implements Agenda
{
    private static final long serialVersionUID = 331412235577651908L;
    private String _strName;
    private String _strKeyName;
    private Map<String, List<Event>> _mapDays = new HashMap<>( );
    private List<Event> _list = new ArrayList<>( );

    /**
     * Default constructor
     */
    public ICalAgenda( )
    {

    }

    /**
     * Returns the name of the Agenda
     * @return The agenda's name
     */
    public String getName( )
    {
        return _strName;
    }

    /**
     * Defines the name of the Agenda
     * @param strName The agenda's name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the KeyName
     * 
     * @return The KeyName
     */
    public String getKeyName( )
    {
        return _strKeyName;
    }

    /**
     * Sets the KeyName
     * 
     * @param strKeyName The KeyName
     */
    public void setKeyName( String strKeyName )
    {
        _strKeyName = strKeyName;
    }

    /**
     * Indicates if the agenda gets events for a given date
     * @param strDate A date code
     * @return True if there is events, otherwise false
     */
    public boolean hasEvents( String strDate )
    {
        return _mapDays.containsKey( strDate );
    }

    /**
     * Retrieves events for a given date
     * @param strDate A date code
     * @return A list of events
     */
    public List<Event> getEventsByDate( String strDate )
    {
        List<Event> listEvents = null;

        if ( hasEvents( strDate ) )
        {
            listEvents = _mapDays.get( strDate );
        }

        return listEvents;
    }

    /**
     * Retrieves all events of the agenda
     * @return A list of events
     */
    public List<Event> getEvents( )
    {
        _list.clear( );

        Set<String> listKey = _mapDays.keySet( );

        for ( String strDate : listKey )
        {
            List<Event> listEvents = getEventsByDate( strDate );

            for ( Event event : listEvents )
            {
                _list.add( event );
            }
        }

        return _list;
    }

    /**
     * Sets events of the agenda
     * @param calendar An iCal calendar
     */
    public void setEvents( Calendar calendar )
    {
        String strTraceEnable = AppPropertiesService.getProperty( Constants.PROPERTY_ICAL_TRACE_ENABLE );
        boolean bTrace = ( ( strTraceEnable != null ) && ( strTraceEnable.equals( "true" ) ) ) ? true : false;
        List<Component> listComponent = calendar.getComponents( );

        for ( Component component : listComponent )
        {
            if ( bTrace )
            {
                AppLogService.info( "Component [" + component.getName( ) + "]" );
            }

            if ( component.getName( ).equals( Component.VEVENT ) )
            {
                ICalEvent event = new ICalEvent( );
                List<Property> listProperty = component.getProperties( );

                for ( Property property : listProperty )
                {
                    if ( property.getName( ).equals( Property.SUMMARY ) )
                    {
                        event.setTitle( property.getValue( ) );
                    }
                    else if ( property.getName( ).equals( Property.DTSTART ) )
                    {
                        event.setDateTimeStart( property.getValue( ) );
                    }
                    else if ( property.getName( ).equals( Property.DTEND ) )
                    {
                        event.setDateTimeEnd( property.getValue( ) );
                    }
                    else if ( property.getName( ).equals( Property.LOCATION ) )
                    {
                        event.setLocation( property.getValue( ) );
                    }
                    else if ( property.getName( ).equals( Property.DESCRIPTION ) )
                    {
                        event.setDescription( property.getValue( ) );
                    }
                    else if ( property.getName( ).equals( Property.CLASS ) )
                    {
                        event.setEventClass( property.getValue( ) );
                    }
                    else if ( property.getName( ).equals( Property.CATEGORIES ) )
                    {
                        event.setCategories( property.getValue( ) );
                    }
                    else if ( property.getName( ).equals( Property.STATUS ) )
                    {
                        event.setStatus( property.getValue( ) );
                    }
                    else if ( property.getName( ).equals( Property.PRIORITY ) )
                    {
                        event.setPriority( Integer.parseInt( property.getValue( ) ) );
                    }
                    else if ( property.getName( ).equals( Property.URL ) )
                    {
                        event.setUrl( property.getValue( ) );
                    }

                    if ( bTrace )
                    {
                        AppLogService.info( "Property [" + property.getName( ) + ", " + property.getValue( ) + "]" );
                    }
                }

                addEvent( event );
            }
        }
    }

    /**
     * Add an event to the agenda
     * @param event The event to add
     */
    public void addEvent( Event event )
    {
        String strDate = Utils.getDate( event.getDate( ) );
        List<Event> listEvents = null;

        if ( hasEvents( strDate ) )
        {
            listEvents = getEventsByDate( strDate );
        }
        else
        {
            listEvents = new ArrayList<>( );
            _mapDays.put( strDate, listEvents );
        }

        listEvents.add( event );
    }

    /**
     * The events which occur between a start and end date
     * @param dateBegin The start date
     * @param dateEnd The end date
     * @param localeEnv The locale
     * @return The list of events
     */
    public List<Event> getEventsByDate( Date dateBegin, Date dateEnd, Locale localeEnv )
    {
        // TODO implementer locale   
        _list.clear( );

        java.util.Calendar calendar = new GregorianCalendar( );
        calendar.setTime( dateBegin );

        java.util.Calendar calendar1 = new GregorianCalendar( );
        calendar1.setTime( dateEnd );

        while ( !Utils.getDate( calendar ).equals( Utils.getDate( calendar1 ) ) )
        {
            List<Event> listEvents = getEventsByDate( Utils.getDate( calendar ) );

            if ( listEvents != null )
            {
                for ( Event event : listEvents )
                {
                    _list.add( event );
                }
            }

            calendar.add( java.util.Calendar.DATE, 1 );
        }

        return _list;
    }
}
