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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.OccurrenceEvent;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.service.cache.EventListCacheService;
import fr.paris.lutece.plugins.calendar.web.CalendarView;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.plugins.calendar.web.EventList;
import fr.paris.lutece.portal.service.cache.ICacheKeyService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.PortalJspBean;


/**
 * This class provides an event list service.
 */
public class EventListService
{
	// CONSTANTS
	private static final String KEY_CALENDAR = "calendar";
	
	// VARIABLES
	private ICacheKeyService _cksEventList;
    private EventListCacheService _cacheEventList = EventListCacheService.getInstance(  );
    private CalendarService _calendarService;

    /**
     * Private constructor
     */
    public EventListService(  )
    {
    	init(  );
    }
    
    /**
     * Init the service
     */
    private void init(  )
    {
    	_cacheEventList.initCache(  );
    }

    /**
     * Load an eventlist object from its keyname
     * @param strKeyName The keyname of the EventList
     * @return An EventList object
     */
    public EventList newEventList( int nViewType )
    {
    	EventList eventlist = null;
        String strEventListKeyName = StringUtils.EMPTY;

        switch ( nViewType )
        {
            case CalendarView.TYPE_DAY:
                strEventListKeyName = AppPropertiesService.getProperty( Constants.PROPERTY_EVENTLIST_VIEW_DAY );

                break;

            case CalendarView.TYPE_WEEK:
                strEventListKeyName = AppPropertiesService.getProperty( Constants.PROPERTY_EVENTLIST_VIEW_WEEK );

                break;

            case CalendarView.TYPE_MONTH:
                strEventListKeyName = AppPropertiesService.getProperty( Constants.PROPERTY_EVENTLIST_VIEW_MONTH );

                break;

            default:
        }

        if ( StringUtils.isNotEmpty( strEventListKeyName ) )
        {
        	String strClassKey = Constants.PROPERTY_EVENTLIST + strEventListKeyName + Constants.SUFFIX_CLASS;
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
        }

        return eventlist;
    }
    
    /**
     * Get the list of SimpleEvent. If the cache is enable, then it will
     * retrieve from the cache.
     * @param nAgendaId the agenda ID
     * @param nSortEvents 1 if it must be sorted ascendly, 0 otherwis
     * @return a list of {@link SimpleEvent}
     */
    public List<SimpleEvent> getSimpleEvents( int nAgendaId, int nSortEvents )
    {
    	String strKey = getKey( nAgendaId );
    	List<SimpleEvent> listEvents = (List<SimpleEvent>) _cacheEventList.getFromCache( strKey );
    	if ( listEvents == null )
    	{
    		Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
    		listEvents = CalendarHome.findEventsList( nAgendaId, nSortEvents, plugin );
    		_cacheEventList.putInCache( strKey, listEvents );
    	}
    	
    	return listEvents;
    }
    
    /**
     * Get the list of SimpleEvent from the user login. If the cache is enable, then it will
     * retrieve from the cache.
     * @param nAgendaId the agenda ID
     * @param user the {@link LuteceUser}
     * @return a list of {@link SimpleEvent}
     */
    public List<SimpleEvent> getSimpleEventsByUserLogin( int nAgendaId, LuteceUser user )
    {
    	String strKey = getKey( nAgendaId, user );
    	List<SimpleEvent> listEvents = (List<SimpleEvent>) _cacheEventList.getFromCache( strKey );
    	if ( listEvents == null )
    	{
    		Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
    		listEvents = CalendarHome.findEventsListByUserLogin( nAgendaId, Constants.SORT_ASC, plugin, user.getName(  ) );
    		_cacheEventList.putInCache( strKey, listEvents );
    	}
    	
    	return listEvents;
    }
    
    /**
     * Get the list of OccurrenceEvent. If the cache is enable, then it will
     * retrieve from the cache.
     * @param nAgendaId the agenda ID
     * @param nEventId the event ID
     * @param nIsAscSort 1 if it must be sorted ascendly, 0 otherwis
     * @param plugin {@link Plugin}
     * @return a list of {@link OccurrenceEvent}
     */
    public List<OccurrenceEvent> getOccurrenceEvents( int nAgendaId, int nEventId, int nIsAscSort, Plugin plugin )
    {
    	return CalendarHome.findOccurrencesList( nAgendaId, nEventId, nIsAscSort, plugin );
    }
    
    /**
     * Get the event from a given event ID.
     * @param nEventId the event ID
     * @param plugin {@link Plugin}
     * @return a {@link SimpleEvent}
     */
    public SimpleEvent getEvent( int nEventId, Plugin plugin )
    {
    	return CalendarHome.findEvent( nEventId, plugin );
    }
    
    /**
     * Get the number repitition days of an event from a given event ID
     * @param nEventId the event id
     * @param plugin {@link Plugin}
     * @return the number of repitition day
     */
    public int getRepititionDays( int nEventId, Plugin plugin )
    {
    	return CalendarHome.getRepetitionDays( nEventId, plugin );
    }
    
    /**
     * Add an event and reset the caches
     * @param event the event
     * @param user the {@link LuteceUser}
     * @param plugin {@link Plugin}
     */
    public void doAddEvent( SimpleEvent event, LuteceUser user, Plugin plugin )
    {
    	String strUserLogin = ( user == null ) ? StringUtils.EMPTY : user.getName(  );
    	CalendarHome.createEvent( event, plugin, strUserLogin );
    	
    	// Reset caches
    	_cacheEventList.removeCache( getKey( event.getIdCalendar(  ) ) );
    	if ( user != null )
    	{
    		_cacheEventList.removeCache( getKey( event.getIdCalendar(  ), user ) );
    	}
    	_calendarService.removeCache( event.getIdCalendar(  ) );
    }
    
    /**
     * Modify an OccurrenceEvent and reset the caches
     * @param occurrenceEvent the event
     * @param plugin {@link Plugin}
     */
    public void doModifyOccurrenceEvent( OccurrenceEvent occurrenceEvent, Plugin plugin )
    {
    	CalendarHome.updateOccurrence( occurrenceEvent, plugin );
    	
    	// Reset caches
    	_calendarService.removeCache( occurrenceEvent.getIdCalendar(  ) );
    }
    
    /**
     * Modify a SimpleEvent and reset the caches
     * @param event the event
     * @param bPeriociteModify true if the periodicity has to be updated, false otherwise
     * @param user the {@link LuteceUser}
     * @param plugin {@link Plugin}
     */
    public void doModifySimpleEvent( SimpleEvent event, boolean bPeriociteModify, LuteceUser user, Plugin plugin )
    {
    	CalendarHome.updateEvent( event, bPeriociteModify, plugin );
    	
    	// Reset caches
    	_cacheEventList.removeCache( getKey( event.getIdCalendar(  ) ) );
    	if ( user != null )
    	{
    		_cacheEventList.removeCache( getKey( event.getIdCalendar(  ), user ) );
    	}
    	_calendarService.removeCache( event.getIdCalendar(  ) );
    }
    
    /**
     * Remove an event and reset the caches
     * @param nAgendaId the agenda ID
     * @param nEventId the event ID
     * @param user the {@link LuteceUser}
     * @param plugin {@link Plugin}
     */
    public void doRemoveEvent( int nAgendaId, int nEventId, LuteceUser user, Plugin plugin )
    {
    	CalendarHome.removeEvent( nAgendaId, nEventId, plugin );
    	// Reset caches
    	_cacheEventList.removeCache( getKey( nAgendaId ) );
    	if ( user != null )
    	{
    		_cacheEventList.removeCache( getKey( nAgendaId, user ) );
    	}
    }
    
    // SETTERS
    
    /**
     * Set the agenda cache key service
     * @param cacheKeyService the cache key service
     */
    public void setEventListCacheKeyService( ICacheKeyService cacheKeyService )
    {
    	_cksEventList = cacheKeyService;
    }
    
    public void setCalendarService( CalendarService calendarService )
    {
    	_calendarService = calendarService;
    }
    
    // CACHE KEYS
    
    /**
     * Get the cache key for the agenda
     * @param nId the ID of the agenda
     * @return the key
     */
    private String getKey( int nAgendaId )
    { 
        Map<String, String> mapParams = new HashMap<String, String>(  );
        mapParams.put( KEY_CALENDAR, Integer.toString( nAgendaId ) );

        return _cksEventList.getKey( mapParams, PortalJspBean.MODE_HTML, null );
    }
    
    /**
     * Get the cache key for the agenda
     * @param nId the ID of the agenda
     * @return the key
     */
    private String getKey( int nAgendaId, LuteceUser user )
    { 
        Map<String, String> mapParams = new HashMap<String, String>(  );
        mapParams.put( KEY_CALENDAR, Integer.toString( nAgendaId ) );

        return _cksEventList.getKey( mapParams, PortalJspBean.MODE_HTML, user );
    }
}
