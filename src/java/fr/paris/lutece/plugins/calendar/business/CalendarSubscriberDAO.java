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
package fr.paris.lutece.plugins.calendar.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for subscriber objects
 */
public final class CalendarSubscriberDAO implements ICalendarSubscriberDAO
{
    // Constants
    private static final String SQL_QUERY_INSERT = "INSERT INTO calendar_subscriber_details ( id_subscriber , email ) VALUES ( ?, ? )";
    private static final String SQL_QUERY_DELETE = "DELETE FROM calendar_subscriber_details WHERE id_subscriber = ? ";
    private static final String SQL_QUERY_SELECT = "SELECT email FROM calendar_subscriber_details WHERE id_subscriber = ? ";
    private static final String SQL_QUERY_SELECT_ALL = "SELECT id_subscriber, email FROM calendar_subscriber_details ";
    private static final String SQL_QUERY_SELECT_SUBSCRIBERS_LIST = "SELECT id_subscriber , email FROM calendar_subscriber_details ";
    private static final String SQL_QUERY_SELECT_BY_EMAIL = "SELECT id_subscriber , email FROM calendar_subscriber_details WHERE email = ? ";
    private static final String SQL_QUERY_SELECT_SUBSCRIBERS_BY_CALENDAR = "SELECT a.id_subscriber , a.email, b.date_subscription FROM calendar_subscriber_details a, calendar_subscriber b WHERE a.id_subscriber = b.id_subscriber AND b.id_agenda = ? ";
    private static final String SQL_QUERY_SELECT_SUBSCRIBERS = " SELECT a.id_subscriber , a.email, b.date_subscription FROM calendar_subscriber_details a, calendar_subscriber b WHERE a.id_subscriber = b.id_subscriber AND b.id_agenda = ? AND a.email LIKE ? ORDER BY a.email LIMIT ? OFFSET ? ";
    private static final String SQL_QUERY_COUNT_CALENDARS_NBR_SUBSCRIBERS = "SELECT count(*) FROM calendar_subscriber_details a, calendar_subscriber b WHERE a.id_subscriber = b.id_subscriber AND b.id_agenda = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE calendar_subscriber SET email = ? WHERE id_subscriber = ?";
    private static final String SQL_QUERY_CHECK_PRIMARY_KEY = "SELECT id_subscriber FROM calendar_subscriber_details WHERE id_subscriber = ?";
    private static final String SQL_QUERY_NEW_PRIMARY_KEY = "SELECT max(id_subscriber) FROM calendar_subscriber_details ";
    private static final String SQL_QUERY_DELETE_FROM_SUBSCRIBER = "DELETE FROM calendar_subscriber WHERE id_agenda = ? and id_subscriber = ? ";
    private static final String SQL_QUERY_CHECK_IS_REGISTERED = "SELECT id_agenda FROM calendar_subscriber WHERE  id_subscriber = ?  AND id_agenda = ?";
    private static final String SQL_QUERY_INSERT_SUBSCRIBER = "INSERT INTO calendar_subscriber (  id_subscriber, id_agenda ,date_subscription ) VALUES ( ?, ?, ? )";
    private static final String SQL_QUERY_CHECK_IS_REGISTERED_TO_ANY_CALENDAR = "SELECT id_agenda FROM calendar_subscriber WHERE id_subscriber = ? LIMIT 1";
    
    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
     * Insert a new record in the table.
     *
     * @param subscriber the object to be inserted
     * @param plugin the Plugin
     */
    public void insert( CalendarSubscriber subscriber, Plugin plugin )
    {
        int nNewPrimaryKey = newPrimaryKey( plugin );
        subscriber.setId( nNewPrimaryKey );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setInt( 1, subscriber.getId(  ) );
        daoUtil.setString( 2, subscriber.getEmail(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Delete a record from the table
     *
     * @param nId the subscriber's identifier
     * @param plugin the Plugin
     */
    public void delete( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * loads data from a subscriber's identifier
     *
     * @param nId the subscriber's identifier
     * @param plugin the Plugin
     * @return an object Subscriber
     */
    public CalendarSubscriber load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        CalendarSubscriber subscriber = new CalendarSubscriber(  );

        if ( daoUtil.next(  ) )
        {
            subscriber.setId( nId );
            subscriber.setEmail( daoUtil.getString( 1 ) );
        }

        daoUtil.free(  );

        return subscriber;
    }

    /**
     * Update the record in the table
     *
     * @param subscriber the instance of subscriber class to be updated
     * @param plugin the Plugin
     */
    public void store( CalendarSubscriber subscriber, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setString( 1, subscriber.getEmail(  ) );
        daoUtil.setInt( 2, subscriber.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Check the unicity of a primary key
     *
     * @param nKey the primary key to be checked
     * @param plugin the Plugin
     * @return true if the key exists, false if not
     */
    boolean checkPrimaryKey( int nKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        if ( !daoUtil.next(  ) )
        {
            daoUtil.free(  );

            return false;
        }

        daoUtil.free(  );

        return true;
    }

    /**
     * Generates a new primary key
     *
     * @param plugin the Plugin
     * @return the new primary key
     */
    int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PRIMARY_KEY, plugin );

        int nKey;

        daoUtil.executeQuery(  );

        if ( !daoUtil.next(  ) )
        {
            // If the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;

        daoUtil.free(  );

        return nKey;
    }

    /**
     * Loads the list of subscribers
     *
     * @param plugin the Plugin
     * @return a collection of objects Subscriber
     */
    public List<CalendarSubscriber> selectAll( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );
        daoUtil.executeQuery(  );

        List<CalendarSubscriber> list = new ArrayList<CalendarSubscriber>(  );

        while ( daoUtil.next(  ) )
        {
            CalendarSubscriber subscriber = new CalendarSubscriber(  );
            subscriber.setId( daoUtil.getInt( 1 ) );
            subscriber.setEmail( daoUtil.getString( 2 ) );
            list.add( subscriber );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Finds a subscriber from his email
     *
     * @param strEmail the subscriber's email
     * @param plugin the Plugin
     * @return a subscriber object if it exists, null if not
     */
    public CalendarSubscriber selectByEmail( String strEmail, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_EMAIL, plugin );
        daoUtil.setString( 1, strEmail.toLowerCase(  ) );
        daoUtil.executeQuery(  );

        CalendarSubscriber subscriber = null;

        if ( daoUtil.next(  ) )
        {
            subscriber = new CalendarSubscriber(  );
            subscriber.setId( daoUtil.getInt( 1 ) );
            subscriber.setEmail( daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return subscriber;
    }

    /**
     * loads the list of subscribers for a calendar
     *
     * @param nCalendarId the Calendar identifier
     * @param plugin the Plugin
     * @return a collection of subscribers
     */
    public List<CalendarSubscriber> selectSubscribers( int nCalendarId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_SUBSCRIBERS_BY_CALENDAR, plugin );
        daoUtil.setInt( 1, nCalendarId );
        daoUtil.executeQuery(  );

        List<CalendarSubscriber> list = new ArrayList<CalendarSubscriber>(  );

        while ( daoUtil.next(  ) )
        {
            CalendarSubscriber subscriber = new CalendarSubscriber(  );
            subscriber.setId( daoUtil.getInt( 1 ) );
            subscriber.setEmail( daoUtil.getString( 2 ) );
            subscriber.setDateSubscription( daoUtil.getTimestamp( 3 ) );
            list.add( subscriber );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * loads the list of subscribers for a Calendar
     *
     * @param nCalendarId the Calendar identifier
     * @param strSearchString gets all the subscribers if null or empty
     *         and gets the subscribers whith an email containing this string otherwise
     * @param nBegin the rank of the first subscriber to return
     * @param nEnd the maximum number of suscribers to return
     * @param plugin the Plugin
     * @return a collection of subscribers
     */
    public List<CalendarSubscriber> selectSubscribers( int nCalendarId, String strSearchString, int nBegin, int nEnd,
        Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_SUBSCRIBERS, plugin );

        daoUtil.setInt( 1, nCalendarId );
        daoUtil.setString( 2, "%" + strSearchString + "%" );
        daoUtil.setInt( 3, nEnd );
        daoUtil.setInt( 4, nBegin );

        daoUtil.executeQuery(  );

        List<CalendarSubscriber> list = new ArrayList<CalendarSubscriber>(  );

        while ( daoUtil.next(  ) )
        {
            CalendarSubscriber subscriber = new CalendarSubscriber(  );
            subscriber.setId( daoUtil.getInt( 1 ) );
            subscriber.setEmail( daoUtil.getString( 2 ) );
            subscriber.setDateSubscription( daoUtil.getTimestamp( 3 ) );
            list.add( subscriber );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Returns, for a subscriber, the number of his subscriptions
     *
     * @param nSubscriberId the subscriber's identifier
     * @param plugin the Plugin
     * @return the number of subscriptions
     */
    public int selectSubscriberNumber( int nSubscriberId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_CALENDARS_NBR_SUBSCRIBERS, plugin );

        int nCount;

        daoUtil.setInt( 1, nSubscriberId );

        daoUtil.executeQuery(  );

        if ( !daoUtil.next(  ) )
        {
            // If the table is empty
            nCount = 0;
        }

        nCount = daoUtil.getInt( 1 );

        daoUtil.free(  );

        return nCount;
    }

    /**
     * loads the list of subscribers
     *
     * @param plugin the Plugin
     * @return a collection of subscribers
     */
    public List<CalendarSubscriber> selectSubscribersList( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_SUBSCRIBERS_LIST, plugin );

        daoUtil.executeQuery(  );

        List<CalendarSubscriber> list = new ArrayList<CalendarSubscriber>(  );

        while ( daoUtil.next(  ) )
        {
            CalendarSubscriber subscriber = new CalendarSubscriber(  );
            subscriber.setId( daoUtil.getInt( 1 ) );
            subscriber.setEmail( daoUtil.getString( 2 ) );
            list.add( subscriber );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Remove the subscriber's inscription to a calendar
     *
     * @param nCalendarId the calendar identifier
     * @param nSubscriberId the subscriber identifier
     * @param plugin the Plugin
     */
    public void deleteSubscriber( int nCalendarId, int nSubscriberId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_FROM_SUBSCRIBER, plugin );

        daoUtil.setInt( 1, nCalendarId );
        daoUtil.setInt( 2, nSubscriberId );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Insert a new subscriber for a calendar
     *
     * @param nCalendarId the calendar identifier
     * @param nSubscriberId the subscriber identifier
     * @param tToday The day
     * @param plugin the Plugin
     */
    public void insertSubscriber( int nCalendarId, int nSubscriberId, Timestamp tToday, Plugin plugin )
    {
        // Check if the subscriber is yet registered for the calendar
        if ( isRegistered( nCalendarId, nSubscriberId, plugin ) )
        {
            return;
        }

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_SUBSCRIBER, plugin );

        daoUtil.setInt( 1, nSubscriberId );
        daoUtil.setInt( 2, nCalendarId );
        daoUtil.setTimestamp( 3, tToday );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * check if the subscriber is not yet registered to a calendar
     *
     * @param nCalendarId the calendar identifier
     * @param nSubscriberId the subscriber identifier
     * @param plugin the Plugin
     * @return true if he is registered and false if not
     */
    public boolean isRegistered( int nCalendarId, int nSubscriberId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_IS_REGISTERED, plugin );

        daoUtil.setInt( 1, nSubscriberId );
        daoUtil.setInt( 2, nCalendarId );

        daoUtil.executeQuery(  );

        if ( !daoUtil.next(  ) )
        {
            daoUtil.free(  );

            return false;
        }

        daoUtil.free(  );

        return true;
    }
    
    /**
     * Check if the user is subscribed to any agenda
     * @param nSubscriberId the ID of the subscriber
     * @param plugin plugin
     * @return true if the user is subscribed to any calendar, false otherwise
     */
    public boolean isUserSubscribed( int nSubscriberId, Plugin plugin )
    {
    	boolean bIsSubscribed = false;
    	DAOUtil daoUtil = new DAOUtil( SQL_QUERY_CHECK_IS_REGISTERED_TO_ANY_CALENDAR, plugin );

        daoUtil.setInt( 1, nSubscriberId );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
        	bIsSubscribed = true;
        }
        daoUtil.free(  );

        return bIsSubscribed;
    }
}
