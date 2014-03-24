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

import fr.paris.lutece.plugins.calendar.business.category.Category;
import fr.paris.lutece.plugins.calendar.business.category.CategoryHome;
import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * This DAO class used to fetch the calendars in the database
 */
public class CalendarDAO implements ICalendarDAO
{
    private static final String SQL_QUERY_NEW_PK = " SELECT max( id_agenda ) FROM calendar_agendas ";
    private static final String SQL_QUERY_NEW_PK_EVENTS = " SELECT max( id_event ) FROM calendar_events ";
    private static final String SQL_QUERY_INSERT_AGENDA = " INSERT INTO calendar_agendas ( id_agenda, agenda_name, agenda_image, agenda_prefix, role ,role_manage, workgroup_key, is_notify, period_validity) VALUES ( ?, ?, ?, ?, ?, ? ,?, ?, ? ) ";
    private static final String SQL_QUERY_UPDATE_AGENDA = " UPDATE calendar_agendas SET agenda_name = ?, agenda_image = ?, agenda_prefix = ?, role = ?, role_manage = ?, workgroup_key = ?, is_notify = ?, period_validity = ? WHERE id_agenda = ?  ";
    private static final String SQL_QUERY_DELETE_AGENDA = " DELETE FROM calendar_agendas WHERE id_agenda = ?  ";
    private static final String SQL_QUERY_SELECT_AGENDA = "SELECT id_agenda, agenda_name, agenda_image, agenda_prefix, role, role_manage, workgroup_key, is_notify, period_validity FROM calendar_agendas WHERE id_agenda = ? ";
    private static final String SQL_QUERY_SELECTALL_AGENDAS = "SELECT id_agenda, agenda_name, agenda_image, agenda_prefix, role,role_manage, workgroup_key, is_notify, period_validity FROM calendar_agendas ORDER BY agenda_name";
    private static final String SQL_QUERY_INSERT_EVENT = " INSERT INTO calendar_events ( id_event, id_agenda, event_date, event_date_end, event_time_start, event_time_end, event_title, event_date_occurence, event_date_periodicity, event_date_creation, event_excluded_day ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_UPDATE_EVENT = " UPDATE calendar_events SET id_agenda =?, event_date = ?, event_date_end = ? , event_time_start = ?, event_time_end = ?, event_title = ?, event_date_occurence = ?, event_date_periodicity = ?, event_excluded_day = ? WHERE id_event = ? ";
    private static final String SQL_QUERY_UPDATE_EVENT_DATE = " UPDATE calendar_events SET event_date = ? WHERE id_event = ? AND id_agenda = ?";
    private static final String SQL_QUERY_DELETE_EVENT = " DELETE FROM calendar_events WHERE id_agenda = ? AND id_event= ? ";
    private static final String SQL_QUERY_SELECT_EVENT = "SELECT  id_agenda, event_date, event_date_end, event_time_start, event_time_end, event_title, event_date_occurence, event_date_periodicity, event_date_creation, event_excluded_day FROM calendar_events WHERE id_event= ? ";
    private static final String SQL_QUERY_SELECT_EVENTS = "SELECT id_event, id_agenda, event_date, event_date_end, event_time_start, event_time_end, event_title, event_date_occurence, event_date_periodicity, event_date_creation FROM calendar_events WHERE id_agenda = ? ORDER BY event_date ";
    private static final String SQL_QUERY_NUMBER_DAYS_BY_EVENT = "SELECT event_date_periodicity FROM calendar_events WHERE id_event=?";
    private static final String SQL_QUERY_SELECT_EVENTS_N_NEXT_DAYS = "SELECT DISTINCT ce.id_event, ce.id_agenda, ce.event_date, ce.event_date_end, ce.event_time_start, ce.event_time_end, ce.event_title, ce.event_date_occurence, ce.event_date_periodicity, ce.event_date_creation "
            + " FROM calendar_events ce INNER JOIN calendar_events_occurrences ceo ON ce.id_event = ceo.id_event "
            + " WHERE ceo.id_agenda = ? AND ceo.occurrence_date >= ? AND ceo.occurrence_date <= ? ORDER BY ce.event_title ";

    /* since version 3.0.0 */
    private static final String SQL_QUERY_NEW_PK_OCCURRENCE = " SELECT max( id_occurrence ) FROM calendar_events_occurrences ";
    private static final String SQL_QUERY_INSERT_OCCURRENCE = " INSERT INTO calendar_events_occurrences ( id_occurrence, id_event, id_agenda, occurrence_date, occurrence_time_start, occurrence_time_end, occurrence_title, occurrence_status) VALUES ( ?, ?, ?, ?, ?, ?, ?,? ) ";
    private static final String SQL_QUERY_UPDATE_EVENT_OCCURRENCE_NUMBER = " UPDATE calendar_events SET event_date_occurence = ? WHERE id_event = ? AND id_agenda = ?";
    private static final String SQL_QUERY_UPDATE_OCCURRENCE = " UPDATE calendar_events_occurrences SET occurrence_date = ?, occurrence_time_start = ?, occurrence_time_end = ?, occurrence_title = ?, occurrence_status = ? WHERE id_occurrence = ? ";
    private static final String SQL_QUERY_DELETE_ALL_OCCURRENCE = " DELETE FROM calendar_events_occurrences WHERE id_agenda = ? AND id_event= ? ";
    private static final String SQL_QUERY_DELETE_OCCURRENCE = " DELETE FROM calendar_events_occurrences WHERE id_occurrence = ? ";
    private static final String SQL_QUERY_SELECT_OCCURRENCE = "SELECT a.id_event, a.occurrence_date, b.event_date_end, a.occurrence_time_start, a.occurrence_time_end, a.occurrence_title, a.occurrence_status"
            + " FROM calendar_events_occurrences a, calendar_events b"
            + " WHERE a.id_occurrence = ? and a.id_event = b.id_event";
    private static final String SQL_QUERY_SELECT_OCCURRENCE_DATE_MIN = "SELECT MIN(occurrence_date) FROM calendar_events_occurrences WHERE id_event = ? ";
    private static final String SQL_QUERY_SELECT_OCCURRENCES = "SELECT a.id_occurrence, b.id_event , a.occurrence_date, b.event_date_end, a.occurrence_time_start, a.occurrence_time_end, a.occurrence_title, a.occurrence_status"
            + " FROM calendar_events_occurrences a, calendar_events b"
            + " WHERE a.id_agenda = ? and a.id_event = ? and a.id_event = b.id_event " + " ORDER BY a.occurrence_date ";
    private static final String SQL_QUERY_SELECT_ALL_OCCURRENCES = "SELECT a.id_occurrence, a.id_event , a.occurrence_date, b.event_date_end, a.occurrence_time_start, a.occurrence_time_end, a.occurrence_title, a.occurrence_status"
            + " FROM calendar_events_occurrences a, calendar_events b"
            + " WHERE a.id_agenda = ? and a.id_event = b.id_event ORDER BY a.occurrence_date ";
    private static final String SQL_QUERY_SELECT_ALL_OCCURRENCES2 = "SELECT a.id_occurrence, a.id_event , a.occurrence_date, b.event_date_end, a.occurrence_time_start, a.occurrence_time_end, a.occurrence_title, a.occurrence_status"
            + " FROM calendar_events_occurrences a, calendar_events b"
            + " WHERE a.id_agenda = ? and a.id_event = b.id_event ORDER BY a.id_occurrence ";
    private static final String SQL_QUERY_SELECT_NUMBER_OCCURRENCE = "SELECT count(id_event) FROM calendar_events_occurrences WHERE id_event=?";
    private static final String SQL_QUERY_NEW_PK_FEATURE = " SELECT max( id_feature ) FROM calendar_events_features ";
    private static final String SQL_QUERY_SELECT_FEATURE = " SELECT feature_description, feature_location, feature_location_town, feature_location_zip, feature_location_address, feature_map_url, feature_link_url, "
            + " document_id, feature_page_url, feature_top_event, feature_image, image_mime_type, feature_tags from calendar_events_features fe where fe.id_event = ? ";
    private static final String SQL_QUERY_INSERT_FEATURE = " INSERT INTO calendar_events_features ( id_feature , id_event , feature_description , feature_location, feature_location_town , feature_location_zip , "
            + " feature_location_address , feature_map_url , feature_link_url , document_id , feature_page_url , feature_top_event, feature_image, image_mime_type, feature_tags ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE_FEATURE = " DELETE FROM calendar_events_features WHERE id_event = ?  ";
    private static final String SQL_QUERY_UPDATE_FEATURE = " UPDATE calendar_events_features SET feature_description = ?, feature_location= ? , feature_location_town = ?, feature_location_zip = ?,"
            + "feature_location_address = ?, feature_map_url = ?, feature_link_url = ?, document_id = ?, feature_page_url = ?, feature_top_event = ?, feature_image = ?, image_mime_type = ?, feature_tags = ? WHERE  id_event = ?";
    private static final String SQL_QUERY_INSERT_LINK_CATEGORY_CALENDAR = " INSERT INTO calendar_category_link ( id_category, id_event ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_DELETE_LINK_CATEGORY_CALENDAR = " DELETE FROM calendar_category_link WHERE id_event = ?";
    private static final String SQL_QUERY_SELECT_EVENTS_BY_USER_LOGIN = " SELECT a.id_event, a.id_agenda, a.event_date, a.event_date_end, a.event_time_start, a.event_time_end, a.event_title, a.event_date_occurence, a.event_date_periodicity, a.event_date_creation "
            + " FROM calendar_events a INNER JOIN calendar_events_users b ON a.id_event = b.id_event "
            + " WHERE a.id_agenda = ? AND b.user_login = ? ORDER BY a.event_date ";
    private static final String SQL_QUERY_INSERT_EVENT_USER = " INSERT INTO calendar_events_users ( id_event, user_login ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE_EVENT_USER = " DELETE FROM calendar_events_users WHERE id_event = ? ";
    private static final String SQL_QUERY_SELECT_AGENDA_IDS = " SELECT id_agenda FROM calendar_agendas ORDER BY id_agenda ASC ";

    // ImageResource queries
    private static final String SQL_QUERY_SELECT_RESOURCE_IMAGE = " SELECT feature_image, image_mime_type FROM calendar_events_features WHERE id_event = ? ";

    //Filter select
    private static final String SQL_QUERY_SELECT_BY_FILTER = "SELECT a.id_event, a.id_agenda, a.event_date, a.event_date_end, a.event_time_start, a.event_time_end, a.event_title, a.event_date_occurence, "
            + "a.event_date_periodicity, a.event_date_creation "
            + "FROM calendar_events a "
            + "LEFT OUTER JOIN calendar_category_link b ON a.id_event = b.id_event";
    private static final String SQL_FILTER_WHERE_CLAUSE = " WHERE ";
    private static final String SQL_FILTER_AND = " AND ";
    private static final String SQL_FILTER_CALENDAR = "a.id_agenda = ?";
    private static final String SQL_FILTER_CATEGORIES_BEGIN = " (";
    private static final String SQL_FILTER_CATEGORIES = " b.id_category = ? ";
    private static final String SQL_FILTER_CATEGORIES_OR = " OR ";
    private static final String SQL_FILTER_CATEGORIES_END = ") ";
    private static final String SQL_FILTER_ID_BEGIN = " (";
    private static final String SQL_FILTER_ID = " a.id_event = ? ";
    private static final String SQL_FILTER_ID_OR = " OR ";
    private static final String SQL_FILTER_ID_END = ") ";
    private static final String SQL_ORDER_BY_EVENTS = " ORDER BY a.event_date";
    private static final String SQL_FILTER_CALENDAR_ID = " a.id_agenda = ? ";
    private static final String SQL_FILTER_ASC = " ASC ";
    private static final String SQL_FILTER_DESC = " DESC ";

    //hasOccurrenceEvent
    private static final String SQL_QUERY_HAS_EVENT = "SELECT id_occurrence FROM calendar_events_occurrences WHERE occurrence_date = ?";

    //Top events
    private static final String SQL_QUERY_SELECT_TOP_EVENTS = "SELECT a.id_event, id_agenda, event_date, event_date_end, event_time_start, event_time_end, event_title, event_date_occurence, event_date_periodicity, event_date_creation"
            + " FROM calendar_events a, calendar_events_features b"
            + " WHERE a.id_event = b.id_event AND b.feature_top_event = 1 ORDER BY event_date ";

    /**
     * Insert a new agenda in the table calendar_agendas.
     * 
     * @param agenda The AgendaResource object
     * @param plugin The Plugin using this data access service
     */
    public void insertAgenda( AgendaResource agenda, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_AGENDA, plugin );
        agenda.setId( String.valueOf( getNewPrimaryKey( plugin, SQL_QUERY_NEW_PK ) ) );
        daoUtil.setInt( 1, Integer.parseInt( agenda.getId( ) ) );
        daoUtil.setString( 2, agenda.getName( ) );
        daoUtil.setString( 3, agenda.getEventImage( ) );
        daoUtil.setString( 4, agenda.getEventPrefix( ) );
        daoUtil.setString( 5, agenda.getRole( ) );
        daoUtil.setString( 6, agenda.getRoleManager( ) );
        daoUtil.setString( 7, agenda.getWorkgroup( ) );
        daoUtil.setBoolean( 8, agenda.isNotify( ) );
        daoUtil.setInt( 9, agenda.getPeriodValidity( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Update the agenda in the table calendar_agendas
     * @param agenda The reference of AgendaResource
     * @param plugin The Plugin using this data access service
     */
    public void storeAgenda( AgendaResource agenda, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_AGENDA, plugin );
        daoUtil.setString( 1, agenda.getName( ) );
        daoUtil.setString( 2, agenda.getEventImage( ) );
        daoUtil.setString( 3, agenda.getEventPrefix( ) );
        daoUtil.setString( 4, agenda.getRole( ) );
        daoUtil.setString( 5, agenda.getRoleManager( ) );
        daoUtil.setString( 6, agenda.getWorkgroup( ) );
        daoUtil.setBoolean( 7, agenda.isNotify( ) );
        daoUtil.setInt( 8, agenda.getPeriodValidity( ) );
        daoUtil.setInt( 9, Integer.parseInt( agenda.getId( ) ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Delete an agenda from the table calendar_agendas
     * @param nAgendaId The Agenda Id
     * @param plugin The Plugin using this data access service
     */
    public void deleteAgenda( int nAgendaId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_AGENDA, plugin );
        daoUtil.setInt( 1, nAgendaId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Insert a new event in the table calendar_events.
     * @param event The event to be inserted
     * @param plugin The Plugin using this data access service
     * @param strUserLogin user login
     */
    public void insertEvent( SimpleEvent event, Plugin plugin, String strUserLogin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_EVENT, plugin );
        event.setId( getNewPrimaryKey( plugin, SQL_QUERY_NEW_PK_EVENTS ) );
        daoUtil.setInt( 1, event.getId( ) );
        daoUtil.setInt( 2, event.getIdCalendar( ) );
        daoUtil.setDate( 3, new java.sql.Date( event.getDate( ).getTime( ) ) );
        daoUtil.setDate( 4, new java.sql.Date( event.getDateEnd( ).getTime( ) ) );
        daoUtil.setString( 5, event.getDateTimeStart( ) );
        daoUtil.setString( 6, event.getDateTimeEnd( ) );
        daoUtil.setString( 7, event.getTitle( ) );
        daoUtil.setInt( 8, event.getOccurrence( ) );
        daoUtil.setInt( 9, event.getPeriodicity( ) );
        daoUtil.setTimestamp( 10, new java.sql.Timestamp( new java.util.Date( ).getTime( ) ) );
        String[] arrayExcludedDays = event.getExcludedDays( );
        if ( arrayExcludedDays != null && arrayExcludedDays.length != 0 )
        {
            StringBuilder sbExcludedDays = new StringBuilder( );
            for ( int i = 0; i < arrayExcludedDays.length - 1; i++ )
            {
                sbExcludedDays.append( arrayExcludedDays[i] + Constants.COMMA );
            }
            sbExcludedDays.append( arrayExcludedDays[arrayExcludedDays.length - 1] );
            daoUtil.setString( 11, sbExcludedDays.toString( ) );
        }
        else
        {
            daoUtil.setString( 11, Constants.EMPTY_STRING );
        }
        daoUtil.executeUpdate( );
        daoUtil.free( );

        //Occurrence storage on database 
        insertOccurrence( event, plugin );
        //Feature storage on database
        insertFeature( plugin, event );
        //Link the event with selected categories
        insertLinkCategories( event.getListCategories( ), event.getId( ), plugin );

        if ( StringUtils.isNotBlank( strUserLogin ) )
        {
            daoUtil = new DAOUtil( SQL_QUERY_INSERT_EVENT_USER, plugin );

            daoUtil.setInt( 1, event.getId( ) );
            daoUtil.setString( 2, strUserLogin );
            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

    /**
     * Update the event in the table calendar_event
     * @param event The reference of SimpleEvent
     * @param plugin The Plugin using this data access service
     * @param bPeriodiciteUpdated true if periodicite, false otherwise
     */
    public void storeEvent( SimpleEvent event, Plugin plugin, boolean bPeriodiciteUpdated )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_EVENT, plugin );
        daoUtil.setInt( 1, event.getIdCalendar( ) );
        daoUtil.setDate( 2, new java.sql.Date( event.getDate( ).getTime( ) ) );

        if ( event.getDateEnd( ) != null )
        {
            daoUtil.setDate( 3, new java.sql.Date( event.getDateEnd( ).getTime( ) ) );
        }
        else
        {
            daoUtil.setDate( 3, null );
        }

        daoUtil.setString( 4, event.getDateTimeStart( ) );
        daoUtil.setString( 5, event.getDateTimeEnd( ) );
        daoUtil.setString( 6, event.getTitle( ) );
        daoUtil.setInt( 7, event.getOccurrence( ) );
        daoUtil.setInt( 8, event.getPeriodicity( ) );
        String[] arrayExcludedDays = event.getExcludedDays( );
        if ( arrayExcludedDays != null && arrayExcludedDays.length != 0 )
        {
            StringBuilder sbExcludedDays = new StringBuilder( );
            for ( int i = 0; i < arrayExcludedDays.length - 1; i++ )
            {
                sbExcludedDays.append( arrayExcludedDays[i] + Constants.COMMA );
            }
            sbExcludedDays.append( arrayExcludedDays[arrayExcludedDays.length - 1] );
            daoUtil.setString( 9, sbExcludedDays.toString( ) );
        }
        else
        {
            daoUtil.setString( 9, Constants.EMPTY_STRING );
        }
        daoUtil.setInt( 10, event.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );

        if ( bPeriodiciteUpdated )
        {
            deleteAllOccurrence( event.getIdCalendar( ), event.getId( ), plugin );
            insertOccurrence( event, plugin );
        }

        //and so do the features
        updateFeature( plugin, event );

        //Link the event with selected categories
        deleteLinkCategories( plugin, event.getId( ) );
        insertLinkCategories( event.getListCategories( ), event.getId( ), plugin );
    }

    /**
     * Delete an Event from the table calendar_events
     * @param nEventId The id of the event
     * @param nAgendaId The agenda Id
     * @param plugin The Plugin using this data access service
     */
    public void deleteEvent( int nAgendaId, int nEventId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_EVENT, plugin );
        daoUtil.setInt( 1, nAgendaId );
        daoUtil.setInt( 2, nEventId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
        //the list of occurrences is deleted when the event is deleted 
        deleteAllOccurrence( nAgendaId, nEventId, plugin );
        deleteFeature( plugin, nEventId );
        deleteLinkCategories( plugin, nEventId );
        deleteEventUser( nEventId, plugin );
    }

    /**
     * Load the data of AgendaResource from the table
     * 
     * 
     * @return the instance of the AgendaResource
     * @param nId The identifier of AgendaResource
     * @param plugin The plugin
     */
    public AgendaResource loadAgenda( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_AGENDA, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery( );

        AgendaResource agenda = null;

        if ( daoUtil.next( ) )
        {
            agenda = new AgendaResource( );
            agenda.setId( String.valueOf( daoUtil.getInt( 1 ) ) );
            agenda.setName( daoUtil.getString( 2 ) );
            agenda.setEventImage( daoUtil.getString( 3 ) );
            agenda.setEventPrefix( daoUtil.getString( 4 ) );
            agenda.setRole( daoUtil.getString( 5 ) );
            agenda.setRoleManager( daoUtil.getString( 6 ) );
            agenda.setWorkgroup( daoUtil.getString( 7 ) );
            agenda.setNotify( daoUtil.getBoolean( 8 ) );
            agenda.setPeriodValidity( daoUtil.getInt( 9 ) );
        }

        daoUtil.free( );

        return agenda;
    }

    /**
     * Load the list of AgendaResources
     * 
     * @param plugin The plugin
     * @return The Collection of the AgendaResources
     */
    public List<AgendaResource> selectAgendaResourceList( Plugin plugin )
    {
        List<AgendaResource> agendaList = new ArrayList<AgendaResource>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_AGENDAS, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            AgendaResource agenda = new AgendaResource( );
            agenda.setId( String.valueOf( daoUtil.getInt( 1 ) ) );
            agenda.setName( daoUtil.getString( 2 ) );
            agenda.setEventImage( daoUtil.getString( 3 ) );
            agenda.setEventPrefix( daoUtil.getString( 4 ) );
            agenda.setRole( daoUtil.getString( 5 ) );
            agenda.setRoleManager( daoUtil.getString( 6 ) );
            agenda.setWorkgroup( daoUtil.getString( 7 ) );
            agenda.setNotify( daoUtil.getBoolean( 8 ) );
            agenda.setPeriodValidity( daoUtil.getInt( 9 ) );

            agendaList.add( agenda );
        }

        daoUtil.free( );

        return agendaList;
    }

    /**
     * Load the data of SimpleEvent from the table
     * @return the instance of the SimpleEvent
     * @param nEventId The id of the event
     * @param plugin The plugin
     */
    public SimpleEvent loadEvent( int nEventId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EVENT, plugin );
        daoUtil.setInt( 1, nEventId );
        daoUtil.executeQuery( );

        SimpleEvent event = null;

        if ( daoUtil.next( ) )
        {
            event = new SimpleEvent( );
            event.setId( nEventId );
            event.setIdCalendar( daoUtil.getInt( 1 ) );
            event.setDate( new java.util.Date( daoUtil.getDate( 2 ).getTime( ) ) );

            if ( daoUtil.getDate( 2 ) != null )
            {
                event.setDateEnd( new java.util.Date( daoUtil.getDate( 3 ).getTime( ) ) );
            }

            event.setDateTimeStart( daoUtil.getString( 4 ) );
            event.setDateTimeEnd( daoUtil.getString( 5 ) );
            event.setTitle( daoUtil.getString( 6 ) );
            event.setOccurrence( daoUtil.getInt( 7 ) );
            event.setPeriodicity( daoUtil.getInt( 8 ) );
            event.setDateCreation( daoUtil.getTimestamp( 9 ) );
            if ( daoUtil.getString( 10 ) != null )
            {
                String[] listExcludedDays = daoUtil.getString( 10 ).split( Constants.COMMA );
                event.setExcludedDays( listExcludedDays );
            }
            else
            {
                String[] listExcludedDays = {};
                event.setExcludedDays( listExcludedDays );
            }
        }

        daoUtil.free( );

        if ( event != null )
        {
            getFeature( plugin, event.getId( ), event );
            event.setListCategories( CategoryHome.findByEvent( event.getId( ), plugin ) );
        }

        return event;
    }

    /**
     * Load the list of Events
     * @return The Collection of the Events
     * @param nSortEvents An integer used for sorting
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     */
    public List<SimpleEvent> selectEventsList( int nAgendaId, int nSortEvents, Plugin plugin )
    {
        List<SimpleEvent> eventList = new ArrayList<SimpleEvent>( );
        String strSortEvents = null;

        if ( nSortEvents == 1 )
        {
            strSortEvents = "ASC";
        }
        else
        {
            strSortEvents = "DESC";
        }

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EVENTS + strSortEvents, plugin );
        daoUtil.setInt( 1, nAgendaId );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            SimpleEvent event = new SimpleEvent( );
            event.setId( daoUtil.getInt( 1 ) );
            event.setIdCalendar( daoUtil.getInt( 2 ) );
            event.setDate( new java.util.Date( daoUtil.getDate( 3 ).getTime( ) ) );
            event.setDateEnd( new java.util.Date( daoUtil.getDate( 4 ).getTime( ) ) );
            event.setDateTimeStart( daoUtil.getString( 5 ) );
            event.setDateTimeEnd( daoUtil.getString( 6 ) );
            event.setTitle( daoUtil.getString( 7 ) );
            event.setOccurrence( daoUtil.getInt( 8 ) );
            event.setPeriodicity( daoUtil.getInt( 9 ) );
            event.setDateCreation( daoUtil.getTimestamp( 10 ) );
            getFeature( plugin, event.getId( ), event );
            event.setListCategories( CategoryHome.findByEvent( event.getId( ), plugin ) );
            eventList.add( event );
        }

        daoUtil.free( );

        return eventList;
    }

    /* new functions since version 3.0.0 */

    /**
     * Generates a new primary key
     * @param plugin The Plugin using this data access service
     * @param strSqlQuery an sql querry to execute
     * @return The new primary key
     */
    int getNewPrimaryKey( Plugin plugin, String strSqlQuery )
    {
        DAOUtil daoUtil = new DAOUtil( strSqlQuery, plugin );
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
     * Insert a new set of occurrence in the table calendar_events_occurrences.
     * @param event The event to be inserted
     * @param plugin The Plugin using this data access service
     */
    public void insertOccurrence( SimpleEvent event, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_OCCURRENCE, plugin );

        //set the default idOccurrence
        int nIdOccurrence = 1;
        int i = 0;
        int index = 0;

        //set the date reference
        String strDateReference = Utils.getDate( event.getDate( ) );

        //Set the occurrence default status
        String strDefaultStatus = AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_DEFAULT_STATUS );

        while ( index < event.getOccurrence( ) )
        {
            if ( event.getExcludedDays( ) != null && event.getExcludedDays( ).length == 7 )
            {
                break;
            }

            Date date = new java.sql.Date( event.getDate( ).getTime( ) );
            String strDate = Utils.getDate( date );
            if ( !Utils.isDayExcluded( Utils.getDayOfWeek( strDate ), event.getExcludedDays( ) ) )
            {
                nIdOccurrence = getNewPrimaryKey( plugin, SQL_QUERY_NEW_PK_OCCURRENCE );
                daoUtil.setInt( 1, nIdOccurrence );
                daoUtil.setInt( 2, event.getId( ) );
                daoUtil.setInt( 3, event.getIdCalendar( ) );
                daoUtil.setDate( 4, date );
                daoUtil.setString( 5, event.getDateTimeStart( ) );
                daoUtil.setString( 6, event.getDateTimeEnd( ) );
                daoUtil.setString( 7, event.getTitle( ) );
                daoUtil.setString( 8, StringUtils.isNotBlank( event.getStatus( ) ) ? event.getStatus( )
                        : strDefaultStatus );
                daoUtil.executeUpdate( );
                index++;
            }
            i++;

            event = getNextOccurrence( event, strDateReference, i );
        }

        daoUtil.free( );
    }

    /**
     * Update the occurrence in the table calendar_events_occurrences
     * 
     * @param occurrence The reference of OccurrenceEvent
     * @param plugin The Plugin using this data access service
     */
    public void storeOccurrence( OccurrenceEvent occurrence, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_OCCURRENCE, plugin );
        daoUtil.setDate( 1, new java.sql.Date( occurrence.getDate( ).getTime( ) ) );
        daoUtil.setString( 2, occurrence.getDateTimeStart( ) );
        daoUtil.setString( 3, occurrence.getDateTimeEnd( ) );
        daoUtil.setString( 4, occurrence.getTitle( ) );
        daoUtil.setString( 5, occurrence.getStatus( ) );
        daoUtil.setInt( 6, occurrence.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );

        Date newDateEvent = selectOccurrenceDateMin( occurrence.getEventId( ), plugin );
        updateDateEvent( occurrence.getEventId( ), occurrence.getIdCalendar( ), plugin, newDateEvent );
    }

    /**
     * Load the list of Occurrences related with a given calendar and event
     * @return The Collection of the Occurrences
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     * @param nEventId The identifier of an event
     * 
     */
    public List<OccurrenceEvent> selectOccurrencesList( int nAgendaId, int nEventId, int nSortEvents, Plugin plugin )
    {
        List<OccurrenceEvent> occurrenceList = new ArrayList<OccurrenceEvent>( );
        String strSortEvents = null;

        if ( nSortEvents == 1 )
        {
            strSortEvents = "ASC";
        }
        else
        {
            strSortEvents = "DESC";
        }

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_OCCURRENCES + strSortEvents, plugin );
        daoUtil.setInt( 1, nAgendaId );
        daoUtil.setInt( 2, nEventId );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            OccurrenceEvent occurrence = new OccurrenceEvent( );
            occurrence.setId( daoUtil.getInt( 1 ) );
            occurrence.setEventId( daoUtil.getInt( 2 ) );
            occurrence.setDate( new java.util.Date( daoUtil.getDate( 3 ).getTime( ) ) );
            occurrence.setDateEnd( new java.util.Date( daoUtil.getDate( 4 ).getTime( ) ) );
            occurrence.setDateTimeStart( daoUtil.getString( 5 ) );
            occurrence.setDateTimeEnd( daoUtil.getString( 6 ) );
            occurrence.setTitle( daoUtil.getString( 7 ) );
            occurrence.setStatus( daoUtil.getString( 8 ) );
            getFeature( plugin, occurrence.getEventId( ), occurrence );
            occurrence.setListCategories( CategoryHome.findByEvent( occurrence.getEventId( ), plugin ) );
            occurrenceList.add( occurrence );
        }

        daoUtil.free( );

        return occurrenceList;
    }

    /**
     * Load the list of all Occurrences
     * @return The Collection of the Occurrences
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     * 
     */
    public List<OccurrenceEvent> selectOccurrencesList( int nAgendaId, int nSortEvents, Plugin plugin )
    {
        List<OccurrenceEvent> occurrenceList = new ArrayList<OccurrenceEvent>( );
        String strSortEvents = null;

        if ( nSortEvents == 1 )
        {
            strSortEvents = "ASC";
        }
        else
        {
            strSortEvents = "DESC";
        }

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_OCCURRENCES + strSortEvents, plugin );
        daoUtil.setInt( 1, nAgendaId );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            OccurrenceEvent occurrence = new OccurrenceEvent( );
            occurrence.setId( daoUtil.getInt( 1 ) );
            occurrence.setEventId( daoUtil.getInt( 2 ) );
            occurrence.setDate( new java.util.Date( daoUtil.getDate( 3 ).getTime( ) ) );
            occurrence.setDateEnd( new java.util.Date( daoUtil.getDate( 4 ).getTime( ) ) );
            occurrence.setDateTimeStart( daoUtil.getString( 5 ) );
            occurrence.setDateTimeEnd( daoUtil.getString( 6 ) );
            occurrence.setTitle( daoUtil.getString( 7 ) );
            occurrence.setStatus( daoUtil.getString( 8 ) );
            getFeature( plugin, occurrence.getEventId( ), occurrence );
            occurrence.setListCategories( CategoryHome.findByEvent( occurrence.getEventId( ), plugin ) );
            occurrenceList.add( occurrence );
        }

        daoUtil.free( );

        return occurrenceList;
    }

    /**
     * Load the list of all Occurrences of a given calendar
     * @return The Collection of the Occurrences
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     * 
     */
    public List<OccurrenceEvent> selectOccurrencesByIdList( int nAgendaId, Plugin plugin )
    {
        List<OccurrenceEvent> occurrenceList = new ArrayList<OccurrenceEvent>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL_OCCURRENCES2, plugin );
        daoUtil.setInt( 1, nAgendaId );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            OccurrenceEvent occurrence = new OccurrenceEvent( );
            occurrence.setId( daoUtil.getInt( 1 ) );
            occurrence.setEventId( daoUtil.getInt( 2 ) );
            occurrence.setDate( new java.util.Date( daoUtil.getDate( 3 ).getTime( ) ) );
            occurrence.setDateEnd( new java.util.Date( daoUtil.getDate( 4 ).getTime( ) ) );
            occurrence.setDateTimeStart( daoUtil.getString( 5 ) );
            occurrence.setDateTimeEnd( daoUtil.getString( 6 ) );
            occurrence.setTitle( daoUtil.getString( 7 ) );
            occurrence.setStatus( daoUtil.getString( 8 ) );
            getFeature( plugin, occurrence.getEventId( ), occurrence );
            occurrenceList.add( occurrence );
        }

        daoUtil.free( );

        return occurrenceList;
    }

    /**
     * Load the data of SimpleEvent from the table
     * @return the instance of the OccurrenceEvent
     * @param nOccurenceId The id of the occurence
     * @param plugin The plugin
     */
    public OccurrenceEvent loadOccurrence( int nOccurenceId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_OCCURRENCE, plugin );
        daoUtil.setInt( 1, nOccurenceId );
        daoUtil.executeQuery( );

        OccurrenceEvent occurrence = null;

        if ( daoUtil.next( ) )
        {
            occurrence = new OccurrenceEvent( );
            occurrence.setId( nOccurenceId );
            occurrence.setEventId( daoUtil.getInt( 1 ) );
            occurrence.setDate( new java.util.Date( daoUtil.getDate( 2 ).getTime( ) ) );
            occurrence.setDateEnd( new java.util.Date( daoUtil.getDate( 3 ).getTime( ) ) );
            occurrence.setDateTimeStart( daoUtil.getString( 4 ) );
            occurrence.setDateTimeEnd( daoUtil.getString( 5 ) );
            occurrence.setTitle( daoUtil.getString( 6 ) );
            occurrence.setStatus( daoUtil.getString( 7 ) );
            getFeature( plugin, occurrence.getEventId( ), occurrence );
        }

        daoUtil.free( );

        return occurrence;
    }

    /**
     * Delete an Event from the table calendar_events_occurrences
     * @param nEventId The id of the occurrence
     * @param nAgendaId The agenda Id
     * @param plugin The Plugin using this data access service
     */
    public void deleteAllOccurrence( int nAgendaId, int nEventId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL_OCCURRENCE, plugin );
        daoUtil.setInt( 1, nAgendaId );
        daoUtil.setInt( 2, nEventId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Delete an occurrence from the table calendar_events_occurrences
     * @param nOccurrenceId The id of the occurrence
     * @param nEventId the event id
     * @param nAgendaId The agenda Id
     * @param plugin The Plugin using this data access service
     */
    public void deleteOccurrence( int nOccurrenceId, int nEventId, int nAgendaId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_OCCURRENCE, plugin );
        daoUtil.setInt( 1, nOccurrenceId );
        daoUtil.executeUpdate( );
        daoUtil.free( );

        /*
         * int nNewNumberOccurrence = getOccurrenceNumber( nEventId );
         * 
         * updateNumberOccurrence(nEventId, nAgendaId, plugin,
         * nNewNumberOccurrence );
         * 
         * if( nNewNumberOccurrence != 0 ){
         * Date newDateEvent = selectOccurrenceDateMin( nEventId, plugin );
         * updateDateEvent( nEventId, nAgendaId, plugin, newDateEvent );
         * }
         */
    }

    /**
     * UPDATE the event date from the table calendar_events
     * @param nEventId The id of the occurrence
     * @param nAgendaId The agenda Id
     * @param plugin The Plugin using this data access service
     * @param newDateEvent the new java.sql.Date object
     */
    public void updateDateEvent( int nEventId, int nAgendaId, Plugin plugin, Date newDateEvent )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_EVENT_DATE, plugin );
        daoUtil.setDate( 1, newDateEvent );
        daoUtil.setInt( 2, nEventId );
        daoUtil.setInt( 3, nAgendaId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * SELECT the minimum date from a set of occurrences from table
     * calendar_events_occurrences
     * @param nIdEvent The id of the occurrence
     * @param plugin The Plugin using this data access service
     * @return The selected date
     */
    public Date selectOccurrenceDateMin( int nIdEvent, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_OCCURRENCE_DATE_MIN, plugin );
        daoUtil.setInt( 1, nIdEvent );
        daoUtil.executeQuery( );

        Date newDateEvent = null;

        while ( daoUtil.next( ) )
        {
            newDateEvent = daoUtil.getDate( 1 );
        }

        daoUtil.free( );

        return newDateEvent;
    }

    /**
     * UPDATE the occurrence number from the table calendar_events
     * @param nEventId The id of the occurrence
     * @param nAgendaId The agenda Id
     * @param nNewNumberOccurrence the new number occurences
     * @param plugin The Plugin using this data access service
     */
    public void updateNumberOccurrence( int nEventId, int nAgendaId, Plugin plugin, int nNewNumberOccurrence )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_EVENT_OCCURRENCE_NUMBER, plugin );
        daoUtil.setInt( 1, nNewNumberOccurrence );
        daoUtil.setInt( 2, nEventId );
        daoUtil.setInt( 3, nAgendaId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Return the frequency of an event
     * @param nEventId The id of the event
     * @param plugin Plugin
     * @return the event frequency
     */
    public int getRepetitionDays( int nEventId, Plugin plugin )
    {
        int nNumberDays = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NUMBER_DAYS_BY_EVENT, plugin );
        daoUtil.setInt( 1, nEventId );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            nNumberDays = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nNumberDays;
    }

    /**
     * Return the occurrence number for an event
     * @param nEventId The id of the event
     * @param plugin Plugin
     * @return the occurrence number
     */
    public int getOccurrenceNumber( int nEventId, Plugin plugin )
    {
        int nNumberDays = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_NUMBER_OCCURRENCE, plugin );
        daoUtil.setInt( 1, nEventId );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            nNumberDays = daoUtil.getInt( 1 );
        }

        daoUtil.free( );

        return nNumberDays;
    }

    /**
     * Return the next occurrence of an event regarding the frequency choisen
     * @param occurrence an Event instance to be updated
     * @param strDateRef The reference date
     * @param nCptDate The number to add
     * @return SimpleEvent object with the date updated
     */
    public SimpleEvent getNextOccurrence( SimpleEvent occurrence, String strDateRef, int nCptDate )
    {
        int nPeriodicity = occurrence.getPeriodicity( );
        String strDateOccrurrence = Utils.getDate( occurrence.getDate( ) );
        String strNewDateOccurrence = "";

        switch ( nPeriodicity )
        {
        case Constants.PARAM_DAY:
            strNewDateOccurrence = Utils.getNextDay( strDateOccrurrence );
            occurrence.setDate( Utils.getDate( strNewDateOccurrence ) );

            break;

        case Constants.PARAM_WEEK:
            strNewDateOccurrence = Utils.getNextWeek( strDateOccrurrence );
            occurrence.setDate( Utils.getDate( strNewDateOccurrence ) );

            break;

        case Constants.PARAM_MONTH:
            strNewDateOccurrence = Utils.getNextMonth( strDateRef, nCptDate );
            occurrence.setDate( Utils.getDate( strNewDateOccurrence ) );

            break;

        default:
            strNewDateOccurrence = Utils.getNextDay( strDateOccrurrence );
            occurrence.setDate( Utils.getDate( strNewDateOccurrence ) );

            break;
        }

        return occurrence;
    }

    /**
     * Insert feature
     * @param plugin the plugin
     * @param event the event
     */
    private void insertFeature( Plugin plugin, SimpleEvent event )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_FEATURE, plugin );
        int nIdFeature = getNewPrimaryKey( plugin, SQL_QUERY_NEW_PK_FEATURE );
        daoUtil.setInt( 1, nIdFeature );
        daoUtil.setInt( 2, event.getId( ) );
        daoUtil.setString( 3, event.getDescription( ) );
        daoUtil.setString( 4, event.getLocation( ) );
        daoUtil.setString( 5, event.getLocationTown( ) );
        daoUtil.setString( 6, event.getLocationZip( ) );
        daoUtil.setString( 7, event.getLocationAddress( ) );
        daoUtil.setString( 8, event.getMapUrl( ) );
        daoUtil.setString( 9, event.getLinkUrl( ) );
        daoUtil.setInt( 10, event.getDocumentId( ) );
        daoUtil.setString( 11, event.getPageUrl( ) );
        daoUtil.setInt( 12, event.getTopEvent( ) );

        ImageResource imageResource = event.getImageResource( );

        if ( imageResource != null )
        {
            daoUtil.setBytes( 13, imageResource.getImage( ) );
            daoUtil.setString( 14, imageResource.getMimeType( ) );
        }
        else
        {
            daoUtil.setBytes( 13, null );
            daoUtil.setString( 14, null );
        }

        String[] listTags = event.getTags( );
        StringBuffer strTags = new StringBuffer( );

        if ( listTags != null )
        {
            for ( String tags : listTags )
            {
                strTags.append( tags + Constants.SPACE );
            }
        }

        daoUtil.setString( 15, strTags.toString( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Update feature
     * @param plugin the plugin
     * @param event the event
     */
    private void updateFeature( Plugin plugin, SimpleEvent event )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_FEATURE, plugin );
        daoUtil.setString( 1, event.getDescription( ) );
        daoUtil.setString( 2, event.getLocation( ) );
        daoUtil.setString( 3, event.getLocationTown( ) );
        daoUtil.setString( 4, event.getLocationZip( ) );
        daoUtil.setString( 5, event.getLocationAddress( ) );
        daoUtil.setString( 6, event.getMapUrl( ) );
        daoUtil.setString( 7, event.getLinkUrl( ) );
        daoUtil.setInt( 8, event.getDocumentId( ) );
        daoUtil.setString( 9, event.getPageUrl( ) );
        daoUtil.setInt( 10, event.getTopEvent( ) );

        ImageResource imageResource = event.getImageResource( );

        if ( imageResource != null )
        {
            daoUtil.setBytes( 11, imageResource.getImage( ) );
            daoUtil.setString( 12, imageResource.getMimeType( ) );
        }
        else
        {
            daoUtil.setBytes( 11, null );
            daoUtil.setString( 12, null );
        }

        StringBuffer strTags = new StringBuffer( );
        String[] listTags = event.getTags( );

        if ( listTags != null )
        {
            for ( String tags : listTags )
            {
                strTags.append( tags + Constants.SPACE );
            }
        }

        daoUtil.setString( 13, strTags.toString( ) );
        daoUtil.setInt( 14, event.getId( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Delete feature
     * @param plugin the plugin
     * @param nEventId the event id
     */
    private void deleteFeature( Plugin plugin, int nEventId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_FEATURE, plugin );
        daoUtil.setInt( 1, nEventId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Get a feature
     * @param plugin the plugin
     * @param nIdEvent the event id
     * @param event the event
     */
    private void getFeature( Plugin plugin, int nIdEvent, SimpleEvent event )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_FEATURE, plugin );
        daoUtil.setInt( 1, nIdEvent );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            event.setDescription( daoUtil.getString( 1 ) );
            event.setLocation( daoUtil.getString( 2 ) );
            event.setLocationTown( daoUtil.getString( 3 ) );
            event.setLocationZip( daoUtil.getString( 4 ) );
            event.setLocationAddress( daoUtil.getString( 5 ) );
            event.setMapUrl( daoUtil.getString( 6 ) );
            event.setLinkUrl( daoUtil.getString( 7 ) );
            event.setDocumentId( daoUtil.getInt( 8 ) );
            event.setPageUrl( daoUtil.getString( 9 ) );
            event.setTopEvent( daoUtil.getInt( 10 ) );

            ImageResource imageResource = new ImageResource( );
            imageResource.setImage( daoUtil.getBytes( 11 ) );
            imageResource.setMimeType( daoUtil.getString( 12 ) );
            event.setImageResource( imageResource );

            if ( daoUtil.getString( 13 ) != null )
            {
                String strTags = daoUtil.getString( 13 );
                String[] listTags = strTags.split( Constants.SPACE );
                event.setListTags( strTags );
                event.setTags( listTags );
            }
        }

        daoUtil.free( );
    }

    /**
     * Insert links between Category and id event
     * @param listCategory The list of Category
     * @param nIdEvent The id of event
     * @param plugin Plugin
     * 
     */
    private void insertLinkCategories( Collection<Category> listCategory, int nIdEvent, Plugin plugin )
    {
        if ( listCategory != null )
        {
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_LINK_CATEGORY_CALENDAR, plugin );

            for ( Category category : listCategory )
            {
                daoUtil.setInt( 1, category.getId( ) );
                daoUtil.setInt( 2, nIdEvent );
                daoUtil.executeUpdate( );
            }

            daoUtil.free( );
        }
    }

    /**
     * Delete link category
     * @param plugin the plugin
     * @param nEventId the event id
     */
    private void deleteLinkCategories( Plugin plugin, int nEventId )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LINK_CATEGORY_CALENDAR, plugin );
        daoUtil.setInt( 1, nEventId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Return the image resource corresponding to the event id
     * @param nCategoryId The identifier of the category
     * @param plugin Plugin
     * @return The image resource
     */
    public ImageResource loadImageResource( int nCategoryId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_RESOURCE_IMAGE, plugin );
        daoUtil.setInt( 1, nCategoryId );
        daoUtil.executeQuery( );

        ImageResource image = null;

        if ( daoUtil.next( ) )
        {
            image = new ImageResource( );
            image.setImage( daoUtil.getBytes( 1 ) );
            image.setMimeType( daoUtil.getString( 2 ) );
        }

        daoUtil.free( );

        return image;
    }

    /**
     * Load the list of Events
     * 
     * @return The Collection of the Events
     * @param plugin The plugin
     * @param filter The CalendarFilter Object
     */
    public List<Event> selectByFilter( CalendarFilter filter, Plugin plugin )
    {
        List<Event> eventList = new ArrayList<Event>( );
        DAOUtil daoUtil = getDaoFromFilter( SQL_QUERY_SELECT_BY_FILTER, filter, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            SimpleEvent event = new SimpleEvent( );
            event.setId( daoUtil.getInt( 1 ) );
            event.setIdCalendar( daoUtil.getInt( 2 ) );
            event.setDate( new java.util.Date( daoUtil.getDate( 3 ).getTime( ) ) );
            event.setDateEnd( new java.util.Date( daoUtil.getDate( 4 ).getTime( ) ) );
            event.setDateTimeStart( daoUtil.getString( 5 ) );
            event.setDateTimeEnd( daoUtil.getString( 6 ) );
            event.setTitle( daoUtil.getString( 7 ) );
            event.setOccurrence( daoUtil.getInt( 8 ) );
            event.setPeriodicity( daoUtil.getInt( 9 ) );
            event.setDateCreation( daoUtil.getTimestamp( 10 ) );
            getFeature( plugin, event.getId( ), event );
            eventList.add( event );
        }

        daoUtil.free( );

        return eventList;
    }

    /**
     * Return a dao initialized with the specified filter
     * @param strQuerySelect the query
     * @param filter the DocumentFilter object
     * @param plugin Plugin
     * @return the DaoUtil
     */
    private DAOUtil getDaoFromFilter( String strQuerySelect, CalendarFilter filter, Plugin plugin )
    {
        StringBuffer sbSQL = new StringBuffer( strQuerySelect );
        StringBuffer sbWhere = new StringBuffer( ( filter.containsCalendarCriteria( ) ) ? SQL_FILTER_CALENDAR
                : StringUtils.EMPTY );

        if ( filter.containsCategoriesCriteria( ) )
        {
            StringBuffer sbCategories = new StringBuffer( SQL_FILTER_CATEGORIES_BEGIN );

            for ( int i = 0; i < filter.getCategoriesId( ).length; i++ )
            {
                sbCategories.append( SQL_FILTER_CATEGORIES );

                if ( ( i + 1 ) < filter.getCategoriesId( ).length )
                {
                    sbCategories.append( SQL_FILTER_CATEGORIES_OR );
                }
            }

            sbCategories.append( SQL_FILTER_CATEGORIES_END );
            if ( StringUtils.isNotBlank( sbWhere.toString( ) ) )
            {
                sbWhere.append( SQL_FILTER_AND );
            }
            sbWhere.append( sbCategories.toString( ) );
        }

        if ( filter.containsIdsCriteria( ) )
        {
            StringBuffer sbIds = new StringBuffer( SQL_FILTER_ID_BEGIN );

            for ( int i = 0; i < filter.getIds( ).length; i++ )
            {
                sbIds.append( SQL_FILTER_ID );

                if ( ( i + 1 ) < filter.getIds( ).length )
                {
                    sbIds.append( SQL_FILTER_ID_OR );
                }
            }

            sbIds.append( SQL_FILTER_ID_END );
            if ( StringUtils.isNotBlank( sbWhere.toString( ) ) )
            {
                sbWhere.append( SQL_FILTER_AND );
            }
            sbWhere.append( sbIds.toString( ) );
        }

        if ( filter.containsCalendarIdsCriteria( ) )
        {
            StringBuffer sbCalendarIds = new StringBuffer( SQL_FILTER_ID_BEGIN );

            for ( int i = 0; i < filter.getCalendarIds( ).length; i++ )
            {
                sbCalendarIds.append( SQL_FILTER_CALENDAR_ID );

                if ( ( i + 1 ) < filter.getCalendarIds( ).length )
                {
                    sbCalendarIds.append( SQL_FILTER_ID_OR );
                }
            }

            sbCalendarIds.append( SQL_FILTER_ID_END );
            if ( StringUtils.isNotBlank( sbWhere.toString( ) ) )
            {
                sbWhere.append( SQL_FILTER_AND );
            }
            sbWhere.append( sbCalendarIds.toString( ) );
        }

        if ( StringUtils.isNotBlank( sbWhere.toString( ) ) )
        {
            sbSQL.append( SQL_FILTER_WHERE_CLAUSE );
            sbSQL.append( sbWhere.toString( ) );
        }

        int nSortEvents = filter.containsSortCriteria( ) ? filter.getSortEvents( ) : 0;
        String strSortEvents;

        if ( nSortEvents == 1 )
        {
            strSortEvents = SQL_FILTER_ASC;
        }
        else
        {
            strSortEvents = SQL_FILTER_DESC;
        }

        sbSQL.append( SQL_ORDER_BY_EVENTS );
        sbSQL.append( strSortEvents );
        AppLogService.debug( "Sql query filter : " + sbSQL.toString( ) );

        DAOUtil daoUtil = new DAOUtil( sbSQL.toString( ), plugin );
        int nIndex = 1;

        if ( filter.containsCategoriesCriteria( ) )
        {
            for ( int nCategoryId : filter.getCategoriesId( ) )
            {
                daoUtil.setInt( nIndex, nCategoryId );
                AppLogService.debug( "Param" + nIndex + " (getCategoriesId) = " + nCategoryId );
                nIndex++;
            }
        }

        if ( filter.containsIdsCriteria( ) )
        {
            for ( int nId : filter.getIds( ) )
            {
                daoUtil.setInt( nIndex, nId );
                AppLogService.debug( "Param" + nIndex + " (getIds) = " + nId );
                nIndex++;
            }
        }

        if ( filter.containsCalendarIdsCriteria( ) )
        {
            for ( int nId : filter.getCalendarIds( ) )
            {
                daoUtil.setInt( nIndex, nId );
                AppLogService.debug( "Param" + nIndex + " (getCalendarIds) = " + nId );
                nIndex++;
            }
        }

        return daoUtil;
    }

    /**
     * Load the list of Events
     * @return The Collection of the Events
     * @param plugin The plugin
     */
    public List<SimpleEvent> selectTopEventsList( Plugin plugin )
    {
        List<SimpleEvent> eventList = new ArrayList<SimpleEvent>( );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_TOP_EVENTS, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            SimpleEvent event = new SimpleEvent( );
            event.setId( daoUtil.getInt( 1 ) );
            event.setIdCalendar( daoUtil.getInt( 2 ) );
            event.setDate( new java.util.Date( daoUtil.getDate( 3 ).getTime( ) ) );
            event.setDateEnd( new java.util.Date( daoUtil.getDate( 4 ).getTime( ) ) );
            event.setDateTimeStart( daoUtil.getString( 5 ) );
            event.setDateTimeEnd( daoUtil.getString( 6 ) );
            event.setTitle( daoUtil.getString( 7 ) );
            event.setOccurrence( daoUtil.getInt( 8 ) );
            event.setPeriodicity( daoUtil.getInt( 9 ) );
            event.setDateCreation( daoUtil.getTimestamp( 10 ) );
            getFeature( plugin, event.getId( ), event );
            eventList.add( event );
        }

        daoUtil.free( );

        return eventList;
    }

    /**
     * Return 1 if the day contains an event 0 otherwise
     * @param calendar The day
     * @param plugin The plugin
     * @return 1 if the day contains an event 0 otherwise
     */
    public boolean hasOccurenceEvent( Calendar calendar, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_HAS_EVENT, plugin );
        boolean isOccurrence = false;
        String date = Utils.getDate( calendar );
        java.util.Date dateEvent = Utils.getDate( date );
        daoUtil.setDate( 1, new java.sql.Date( dateEvent.getTime( ) ) );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            isOccurrence = true;
        }

        daoUtil.free( );

        return isOccurrence;
    }

    /**
     * Load the list of Events
     * @return The Collection of the Events
     * @param nSortEvents An integer used for sorting issues
     * @param plugin The plugin
     * @param nAgendaId The identifier of the agenda
     * @param strUserLogin The user login
     */
    public List<SimpleEvent> selectEventsListByUserLogin( int nAgendaId, int nSortEvents, Plugin plugin,
            String strUserLogin )
    {
        List<SimpleEvent> eventList = new ArrayList<SimpleEvent>( );
        String strSortEvents = null;

        if ( nSortEvents == 1 )
        {
            strSortEvents = "ASC";
        }
        else
        {
            strSortEvents = "DESC";
        }

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EVENTS_BY_USER_LOGIN + strSortEvents, plugin );
        daoUtil.setInt( 1, nAgendaId );
        daoUtil.setString( 2, strUserLogin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            SimpleEvent event = new SimpleEvent( );
            event.setId( daoUtil.getInt( 1 ) );
            event.setIdCalendar( daoUtil.getInt( 2 ) );
            event.setDate( new java.util.Date( daoUtil.getDate( 3 ).getTime( ) ) );
            event.setDateEnd( new java.util.Date( daoUtil.getDate( 4 ).getTime( ) ) );
            event.setDateTimeStart( daoUtil.getString( 5 ) );
            event.setDateTimeEnd( daoUtil.getString( 6 ) );
            event.setTitle( daoUtil.getString( 7 ) );
            event.setOccurrence( daoUtil.getInt( 8 ) );
            event.setPeriodicity( daoUtil.getInt( 9 ) );
            event.setDateCreation( daoUtil.getTimestamp( 10 ) );
            getFeature( plugin, event.getId( ), event );
            event.setListCategories( CategoryHome.findByEvent( event.getId( ), plugin ) );
            eventList.add( event );
        }

        daoUtil.free( );

        return eventList;
    }

    /**
     * Delete the link between event and user
     * @param nEventId ID event
     * @param plugin plugin
     */
    public void deleteEventUser( int nEventId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_EVENT_USER, plugin );
        daoUtil.setInt( 1, nEventId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Load the list of events
     * @param nAgendaId the agenda ID
     * @param nSortEvents An integer used for sorting issues
     * @param nNextDays the number of days
     * @param plugin plugin
     * @return the list of events
     */
    public List<SimpleEvent> selectEventsList( int nAgendaId, int nSortEvents, int nNextDays, Plugin plugin )
    {
        List<SimpleEvent> eventList = new ArrayList<SimpleEvent>( );
        String strSortEvents = null;

        if ( nSortEvents == 1 )
        {
            strSortEvents = "ASC";
        }
        else
        {
            strSortEvents = "DESC";
        }

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EVENTS_N_NEXT_DAYS + strSortEvents, plugin );
        daoUtil.setInt( 1, nAgendaId );

        String strDate = Utils.getDateToday( );
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );

        daoUtil.setString( 2, Utils.getDate( calendar ) );

        calendar.add( Calendar.DATE, nNextDays );

        daoUtil.setString( 3, Utils.getDate( calendar ) );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            SimpleEvent event = new SimpleEvent( );
            event.setId( daoUtil.getInt( 1 ) );
            event.setIdCalendar( daoUtil.getInt( 2 ) );
            event.setDate( new java.util.Date( daoUtil.getDate( 3 ).getTime( ) ) );
            event.setDateEnd( new java.util.Date( daoUtil.getDate( 4 ).getTime( ) ) );
            event.setDateTimeStart( daoUtil.getString( 5 ) );
            event.setDateTimeEnd( daoUtil.getString( 6 ) );
            event.setTitle( daoUtil.getString( 7 ) );
            event.setOccurrence( daoUtil.getInt( 8 ) );
            event.setPeriodicity( daoUtil.getInt( 9 ) );
            event.setDateCreation( daoUtil.getTimestamp( 10 ) );
            getFeature( plugin, event.getId( ), event );
            event.setListCategories( CategoryHome.findByEvent( event.getId( ), plugin ) );
            eventList.add( event );
        }

        daoUtil.free( );

        return eventList;
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> selectCalendarIds( Plugin plugin )
    {
        List<Integer> listIds = new ArrayList<Integer>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_AGENDA_IDS, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            listIds.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free( );

        return listIds;
    }
}
