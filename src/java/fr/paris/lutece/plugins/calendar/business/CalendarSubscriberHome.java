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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.sql.Timestamp;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for Subscriber objects
 */
public final class CalendarSubscriberHome
{
    //Properties
    private static final String CONSTANT_EMPTY_STRING = "";

    // Static variable pointed at the DAO instance
    private static ICalendarSubscriberDAO _dao = (ICalendarSubscriberDAO) SpringContextService.getPluginBean( "calendar",
            "calendar.calendarSubscriberDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private CalendarSubscriberHome(  )
    {
    }

    /**
     * Create an instance of the Subscriber class
     *
     * @param subscriber the object to insert into the database
     * @param plugin the plugin
     * @return the instance created
     */
    public static CalendarSubscriber create( CalendarSubscriber subscriber, Plugin plugin )
    {
        _dao.insert( subscriber, plugin );

        return subscriber;
    }

    /**
     * Update of the subscriber's data specified in paramater
     *
     * @param subscriber the instance of class which contains the data to store
     * @param plugin the plugin
     * @return the instance  of the subscriber updated
     */
    public static CalendarSubscriber update( CalendarSubscriber subscriber, Plugin plugin )
    {
        _dao.store( subscriber, plugin );

        return subscriber;
    }

    /**
     * Remove the subscriber whose identifier is specified in parameter
     *
     * @param nSubscriberId the subscriber's identifier
     * @param plugin the plugin
     */
    public static void remove( int nSubscriberId, Plugin plugin )
    {
        _dao.delete( nSubscriberId, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an object Subscriber from its identifier
     *
     * @param nKey the primary key of the subscriber
     * @param plugin the plugin
     * @return an instance of the class
     */
    public static CalendarSubscriber findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * @param plugin the plugin
     * @return a collection of objects Subscriber
     */
    public static List<CalendarSubscriber> findAllSubsriber( Plugin plugin )
    {
        return _dao.selectAll( plugin );
    }

    /**
     * Returns a subscriber object from the email
     *
     * @param strEmail the subscriber's email
     * @param plugin the plugin
     * @return a subscriber object if it exists, null if not
     */
    public static CalendarSubscriber findByEmail( String strEmail, Plugin plugin )
    {
        return _dao.selectByEmail( strEmail, plugin );
    }

    /**
     * loads the list of subscribers for a calendar
     *
     * @param nCalendarId the calendar identifier
     * @param nBegin the rank of the first subscriber to return
     * @param nEnd the maximum number of suscribers to return
     * @param plugin the plugin
     * @return a collection of subscribers
     */
    public static List<CalendarSubscriber> findSubscribers( int nCalendarId, int nBegin, int nEnd, Plugin plugin )
    {
        return _dao.selectSubscribers( nCalendarId, CONSTANT_EMPTY_STRING, nBegin, nEnd, plugin );
    }

    /**
     * loads the list of subscribers for a calendar
     *
     * @param nCalendarId the calendar identifier
     * @param strSearchString gets all the subscribers if null or empty
     *         and gets the subscribers with email containing this string otherwise
     * @param nBegin the rank of the first subscriber to return
     * @param nEnd the maximum number of suscribers to return
     * @param plugin the plugin
     * @return a collection of subscribers
     */
    public static List<CalendarSubscriber> findSubscribers( int nCalendarId, String strSearchString, int nBegin,
        int nEnd, Plugin plugin )
    {
        return _dao.selectSubscribers( nCalendarId, strSearchString, nBegin, nEnd, plugin );
    }

    /**
     * loads the list of subscribers for a calendar
     *
     * @param nCalendarId the calendar identifier
     * @param plugin the plugin
     * @return a collection of subscribers
     */
    public static List<CalendarSubscriber> findSubscribers( int nCalendarId, Plugin plugin )
    {
        return _dao.selectSubscribers( nCalendarId, plugin );
    }

    /**
     * Returns, for a subscriber, the number of his subscriptions
     *
     * @param nSubscriberId the subscriber's identifier
     * @param plugin the plugin
     * @return the number of subscriptions
     */
    public static int findSubscriberNumber( int nAgendaId, Plugin plugin )
    {
        return _dao.selectSubscriberNumber( nAgendaId, plugin );
    }

    /**
     * loads the list of subscribers
     * @param plugin the plugin
     * @return a collection of subscribers
     */
    public static List<CalendarSubscriber> getSubscribersList( Plugin plugin )
    {
        return _dao.selectSubscribersList( plugin );
    }

    /**
     * removes an subscriber's inscription for a calendar
     *
     * @param nAgendaId the calendar identifier
     * @param nSubscriberId the subscriber identifier
     * @param plugin the Plugin
     */
    public static void removeSubscriber( int nSubscriberId, int nAgendaId, Plugin plugin )
    {
        _dao.deleteSubscriber( nSubscriberId, nAgendaId, plugin );
    }

    /**
     * insert a new subscriber for e calendar
     *
     * @param nCalendarId the calendar identifier
     * @param nSubscriberId the subscriber indentifier
     * @param plugin the Plugin
     * @param tToday the day
     */
    public static void addSubscriber( int nCalendarId, int nSubscriberId, Timestamp tToday, Plugin plugin )
    {
        _dao.insertSubscriber( nCalendarId, nSubscriberId, tToday, plugin );
    }
    
    /**
     * Check if the user is subscribed to any agenda
     * @param nSubscriberId the ID of the subscriber
     * @param plugin plugin
     * @return true if the user is subscribed to any agenda, false otherwise
     */
    public static boolean isUserSubscribed( int nSubscriberId, Plugin plugin )
    {
    	return _dao.isUserSubscribed( nSubscriberId, plugin );
    }
}
