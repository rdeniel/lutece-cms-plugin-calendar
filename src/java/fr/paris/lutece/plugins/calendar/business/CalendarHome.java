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
package fr.paris.lutece.plugins.calendar.business;

import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;

import java.util.Calendar;
import java.util.List;


/**
 * This class provides instances management methods (selectEventsList,
 * findByPrimaryKey, findAgendasList ...) for
 * Calendar objects ( AgendaResource, Events, ...)
 */
public final class CalendarHome
{
    // Static variable pointed at the DAO instance
    private static ICalendarDAO _dao = SpringContextService.getBean( "calendar.calendarDAO" );

    /**
     * Create a new CalendarHome
     */
    private CalendarHome( )
    {
    }

    /**
     * Insert a new agenda in the table calendar_agendas.
     * 
     * @param agenda The AgendaResource object
     * @param plugin The Plugin using this data access service
     */
    public static void createAgenda( AgendaResource agenda, Plugin plugin )
    {
        _dao.insertAgenda( agenda, plugin );
    }

    /**
     * Update the agenda in the table calendar_agendas
     * @param agenda The reference of AgendaResource
     * @param plugin The Plugin using this data access service
     */
    public static void updateAgenda( AgendaResource agenda, Plugin plugin )
    {
        _dao.storeAgenda( agenda, plugin );
    }

    /**
     * Delete an agenda from the table calendar_agendas
     * @param nAgendaId The agenda Id
     * @param plugin The Plugin using this data access service
     */
    public static void removeAgenda( int nAgendaId, Plugin plugin )
    {
        _dao.deleteAgenda( nAgendaId, plugin );
    }

    /**
     * Load the list of AgendaResources
     * 
     * @param plugin The plugin
     * @return The Collection of the AgendaResources
     */
    public static List<AgendaResource> findAgendaResourcesList( Plugin plugin )
    {
        return _dao.selectAgendaResourceList( plugin );
    }

    /**
     * Returns an instance of a AgendaResource whose identifier is specified in
     * parameter
     * 
     * @param nKey The Primary key of the contact
     * @param plugin The Plugin object
     * @return an instance of AgendaResource
     */
    public static AgendaResource findAgendaResource( int nKey, Plugin plugin )
    {
        return _dao.loadAgenda( nKey, plugin );
    }

    /**
     * Insert a new event in the table calendar_events.
     * @param event the event
     * @param plugin The Plugin using this data access service
     * @param strUserLogin user login
     * @throws fr.paris.lutece.portal.service.util.AppException AppException
     */
    public static void createEvent( SimpleEvent event, Plugin plugin, String strUserLogin ) throws AppException
    {
        _dao.insertEvent( event, plugin, strUserLogin );
    }

    /**
     * Update the event in the table calendar_event
     * @param event The reference of SimpleEvent
     * @param bPeriodiciteUpdated true if periodicite, false otherwise
     * @param plugin The Plugin using this data access service
     */
    public static void updateEvent( SimpleEvent event, boolean bPeriodiciteUpdated, Plugin plugin )
    {
        _dao.storeEvent( event, plugin, bPeriodiciteUpdated );
    }

    /**
     * Delete an Event from the table calendar_events
     * @param nEventId The event id
     * @param nAgendaId The agenda Id
     * @param plugin The Plugin using this data access service
     */
    public static void removeEvent( int nAgendaId, int nEventId, Plugin plugin )
    {
        _dao.deleteEvent( nAgendaId, nEventId, plugin );
    }

    /**
     * Load the data of SimpleEvent from the table
     * @return the instance of the SimpleEvent
     * @param nEventId The event id
     * @param plugin The plugin
     */
    public static SimpleEvent findEvent( int nEventId, Plugin plugin )
    {
        return _dao.loadEvent( nEventId, plugin );
    }

    /**
     * Load the list of Events
     * @return The Collection of the Events
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     */
    public static List<SimpleEvent> findEventsList( int nAgendaId, int nSortEvents, Plugin plugin )
    {
        return _dao.selectEventsList( nAgendaId, nSortEvents, plugin );
    }

    /**
     * Load the list of Events
     * @return The Collection of the Events
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     * @param strUserLogin user login
     */
    public static List<SimpleEvent> findEventsListByUserLogin( int nAgendaId, int nSortEvents, Plugin plugin,
            String strUserLogin )
    {
        return _dao.selectEventsListByUserLogin( nAgendaId, nSortEvents, plugin, strUserLogin );
    }

    /**
     * Load the list of Occurrences linked with an event
     * @return The Collection of the Occurrences
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     * @param nEventId The identifier of an event
     * 
     */
    public static List<OccurrenceEvent> findOccurrencesList( int nAgendaId, int nEventId, int nSortEvents, Plugin plugin )
    {
        return _dao.selectOccurrencesList( nAgendaId, nEventId, nSortEvents, plugin );
    }

    /**
     * Load the list of Occurrences
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     * @return The Collection of the Occurrences
     * 
     */
    public static List<OccurrenceEvent> findOccurrencesList( int nAgendaId, int nSortEvents, Plugin plugin )
    {
        return _dao.selectOccurrencesList( nAgendaId, nSortEvents, plugin );
    }

    /**
     * Load the list of Occurrences ordered by occurrences id
     * @return The Collection of the Occurrences
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     * 
     */
    public static List<OccurrenceEvent> findOccurrencesByIdList( int nAgendaId, Plugin plugin )
    {
        return _dao.selectOccurrencesByIdList( nAgendaId, plugin );
    }

    /**
     * Load the data of Occurrence from the table
     * @return the instance of the OccurrenceEvent
     * @param nOccurrenceId The occurrence id
     * @param plugin The plugin
     */
    public static OccurrenceEvent findOccurrence( int nOccurrenceId, Plugin plugin )
    {
        return _dao.loadOccurrence( nOccurrenceId, plugin );
    }

    /**
     * Update the occurrence in the table calendar_events_occurrences
     * 
     * @param occurrence The reference of OccurrenceEvent
     * @param plugin The Plugin using this data access service
     */
    public static void updateOccurrence( OccurrenceEvent occurrence, Plugin plugin )
    {
        _dao.storeOccurrence( occurrence, plugin );
    }

    /**
     * Returns the number of days within which the events will occur
     * @param nEventId The id of the event
     * @param plugin Plugin
     * @return Returns the number of days
     */
    public static int getRepetitionDays( int nEventId, Plugin plugin )
    {
        return _dao.getRepetitionDays( nEventId, plugin );
    }

    /**
     * Returns the number of occurrence for an event
     * @param nEventId The id of the event
     * @return Returns the number of occurrence
     * @param plugin The Plugin using this data access service
     */
    public static int getOccurrenceNumber( int nEventId, Plugin plugin )
    {
        return _dao.getOccurrenceNumber( nEventId, plugin );
    }

    /**
     * Delete an Event from the table calendar_events
     * @param nOccurrenceId The occurrence id
     * @param nEventId The identifier of an event
     * @param nAgendaId The id of the agenda
     * @param plugin The Plugin using this data access service
     */
    public static void removeOccurrence( int nOccurrenceId, int nEventId, int nAgendaId, Plugin plugin )
    {
        _dao.deleteOccurrence( nOccurrenceId, nEventId, nAgendaId, plugin );
    }

    /**
     * Return the image resource for the specified category id
     * @param nCategoryId The identifier of Category object
     * @param plugin Plugin
     * @return ImageResource
     */
    public static ImageResource getImageResource( int nCategoryId, Plugin plugin )
    {
        return _dao.loadImageResource( nCategoryId, plugin );
    }

    /**
     * Load the list of Events
     * 
     * @return The Collection of the Events
     * @param plugin The plugin
     * @param filter The CalendarFilter Object
     */
    public static List<Event> findEventsByFilter( CalendarFilter filter, Plugin plugin )
    {
        return _dao.selectByFilter( filter, plugin );
    }

    /**
     * Load the list of top Events
     * @param plugin The plugin
     * @return The list of events
     */
    public static List<SimpleEvent> findTopEventList( Plugin plugin )
    {
        return _dao.selectTopEventsList( plugin );
    }

    /**
     * Return 1 if the day contains an event 0 otherwise
     * @param calendar The day
     * @param plugin The plugin
     * @return 1 if the day contains an event 0 otherwise
     */
    public static boolean hasOccurrenceEvent( Calendar calendar, Plugin plugin )
    {
        return _dao.hasOccurenceEvent( calendar, plugin );
    }

    /**
     * Load the list of events
     * @param nAgendaId the agenda ID
     * @param nSortEvents An integer used for sorting issues
     * @param nNextDays the number of days
     * @param plugin plugin
     * @return the list of events
     */
    public static List<SimpleEvent> findEventsList( int nAgendaId, int nSortEvents, int nNextDays, Plugin plugin )
    {
        return _dao.selectEventsList( nAgendaId, nSortEvents, nNextDays, plugin );
    }

    /**
     * Get the list of calendar IDs
     * @param plugin {@link Plugin}
     * @return the list of calendar ids
     */
    public static List<Integer> findCalendarIds( Plugin plugin )
    {
        return _dao.selectCalendarIds( plugin );
    }
}
