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
package fr.paris.lutece.plugins.calendar.business;

import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppException;

import java.util.Calendar;
import java.util.List;


/**
 * AN interface used to access the calendar business layer
 */
public interface ICalendarDAO
{
    /**
     * Delete an Event from the table calendar_events
     * @param nEventId The id of the event
     * @param nAgendaId The Agenda Id
     * @param plugin The Plugin using this data access service
     */
    void deleteEvent( int nAgendaId, int nEventId, Plugin plugin );

    /**
     * Delete a Agenda from the table calendar_agendas
     *
     * @param nAgendaId The agenda Id
     * @param plugin The Plugin using this data access service
     */
    void deleteAgenda( int nAgendaId, Plugin plugin );

    /**
     * Insert a new event in the table calendar_events.
     * @param event The event
     * @param plugin The Plugin using this data access service
     * @param strUserLogin user login
     * @throws fr.paris.lutece.portal.service.util.AppException An AppException error
     */
    void insertEvent( SimpleEvent event, Plugin plugin, String strUserLogin )
        throws AppException;

    /**
     * Insert a new agenda in the table calendar_agendas.
     *
     *
     * @param agenda The AgendaResource object
     * @param plugin The Plugin using this data access service
     */
    void insertAgenda( AgendaResource agenda, Plugin plugin );

    /**
     * Load the data of AgendaResource from the table
     *
     * @return the instance of the AgendaResource
     * @param nId The identifier of AgendaResource
     * @param plugin The plugin
     */
    AgendaResource loadAgenda( int nId, Plugin plugin );

    /**
     * Load the data of SimpleEvent from the table
     * @return the instance of the SimpleEvent
     * @param nEventId The id of the event
     * @param plugin The plugin
     */
    SimpleEvent loadEvent( int nEventId, Plugin plugin );

    /**
     * Load the list of AgendaResources
     *
     *
     * @param plugin The plugin
     * @return The Collection of the AgendaResources
     */
    List<AgendaResource> selectAgendaResourceList( Plugin plugin );

    /**
     * Load the list of Events
     * @return The Collection of the Events
     * @param nSortEvents Parameter used for event sorting
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     */
    List<SimpleEvent> selectEventsList( int nAgendaId, int nSortEvents, Plugin plugin );
    
    /**
     * Load the list of Events
     * @return The Collection of the Events
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     * @param strUserLogin user login
     */
    List<SimpleEvent> selectEventsListByUserLogin( int nAgendaId, int nSortEvents, Plugin plugin, String strUserLogin);

    /**
     * Update the agenda in the table calendar_agendas
     *
     * @param agenda The reference of AgendaResource
     * @param plugin The Plugin using this data access service
     */
    void storeAgenda( AgendaResource agenda, Plugin plugin );

    /**
     * Update the event in the table calendar_event
     *
     * @param event The reference of SimpleEvent
     * @param plugin The Plugin using this data access service
     * @param boolean 1 if periodicite is to Update, 0 if not
     * @throws fr.paris.lutece.portal.service.util.AppException An AppException
     */
    void storeEvent( SimpleEvent event, Plugin plugin, boolean periodiciteUpdated )
        throws AppException;

    /**
     * Returns the number of following days the event should be displayed
     * @param nEventId The id of the event
	 * @param plugin Plugin
     * @return The number of days
     */
    int getRepetitionDays( int nEventId, Plugin plugin );

    /**
     * Load the list of Occurrences
     * @return The Collection of the Occurrences
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     * @param nEventId The identifier of an event
     *
     */
    List<OccurrenceEvent> selectOccurrencesList( int nAgendaId, int nEventId, int nSortEvents, Plugin plugin );

    /**
     * Load the list of Occurrences
     * @return The Collection of the Occurrences
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     *
     */
    List<OccurrenceEvent> selectOccurrencesList( int nAgendaId, int nSortEvents, Plugin plugin );

    /**
     * Load the list of Occurrences ordered by occurence id
     * @return The Collection of the Occurrences
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     *
     */
    List<OccurrenceEvent> selectOccurrencesByIdList( int nAgendaI, Plugin plugin );

    /**
     * Load the data of SimpleEvent from the table
     * @return the instance of the SimpleEvent
     * @param nEventId The id of the event
     * @param plugin The plugin
     */
    OccurrenceEvent loadOccurrence( int nOccurenceId, Plugin plugin );

    /**
     * Update the occurrence in the table calendar_events_occurrences
     *
     * @param occurrence The reference of OccurrenceEvent
     * @param plugin The Plugin using this data access service
     */
    void storeOccurrence( OccurrenceEvent occurrence, Plugin plugin );

    /**
     * Delete an Event from the table calendar_events_occurrences
     * @param nEventId The id of the occurrence
     * @param nAgendaId The agenda Id
     * @param plugin The Plugin using this data access service
     */
    void deleteAllOccurrence( int nAgendaId, int nEventId, Plugin plugin );

    /**
     * Delete an Event from the table calendar_events_occurrences
     * @param nEventId The id of the occurrence
     * @param nOccurrenceId The occurrence Id
     * @param plugin The Plugin using this data access service
     */
    void deleteOccurrence( int nOccurrenceId, int nEventId, int nAgendaId, Plugin plugin );

    /**
     * UPDATE the occurrence number from the table calendar_events
     * @param nEventId The id of the occurrence
     * @param nAgendaId The agenda Id
     * @param plugin The Plugin using this data access service
     * @param nNewNumberOccurrence the new number of occurrence for the event
     */
    void updateNumberOccurrence( int nEventId, int nAgendaId, Plugin plugin, int nNewNumberOccurrence );

    /**
     * Return the occurrence number for an event
     * @param nEventId The id of the event
	 * @param plugin Plugin
     * @return the occurrence number
     */
    int getOccurrenceNumber( int nEventId, Plugin plugin );

    /**
     * Return the image resource corresponding to the category id
     * @param nEventId The event id
	 * @param plugin Plugin
     * @return The image resource
     */
    ImageResource loadImageResource( int nEventId, Plugin plugin );

    /**
     * Load the list of Events
     *
     * @return The Collection of the Events
     * @param plugin The plugin
     * @param filter The CalendarFilter Object
     */
    List<Event> selectByFilter( CalendarFilter filter, Plugin plugin );

    /**
     * Load the list of top Events
     * @param plugin The plugin
     */
    List<SimpleEvent> selectTopEventsList( Plugin plugin );

    /**
     * Return 1 if the day contains an event 0 otherwise
     * @param calendar The day
     * @param plugin The plugin
     * @return 1 if the day contains an event 0 otherwise
     */
    boolean hasOccurenceEvent( Calendar calendar, Plugin plugin );
    
    /**
     * Delete the link between event and user
     * @param nEventId ID event
     * @param plugin plugin
     */
    void deleteEventUser( int nEventId, Plugin plugin );

    /**
     * Load the list of events
     * @param nAgendaId the agenda ID
     * @param nSortEvents An integer used for sorting issues 
     * @param nNextDays the number of days
     * @param plugin plugin
     * @return the list of events
     */
    List<SimpleEvent> selectEventsList( int nAgendaId, int nSortEvents, int nNextDays, Plugin plugin );
    
    /**
     * Get the list of calendar IDs
     * @param plugin {@link Plugin}
     * @return the list of calendar ids
     */
    List<Integer> selectCalendarIds( Plugin plugin );
}
