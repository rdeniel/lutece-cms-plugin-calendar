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
package fr.paris.lutece.plugins.calendar.business.notification;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * class TaskCommentConfig
 * 
 */
public class CalendarNotificationDAO implements ICalendarNotificationDAO
{
	private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = " SELECT key_email, email, id_agenda,date_expiry "
			+ " FROM calendar_notify_key WHERE key_email=?";
	private static final String SQL_QUERY_INSERT = " INSERT INTO calendar_notify_key( "
			+ " key_email, email, id_agenda, date_expiry)" + " VALUES (?,?,?,?)";
	private static final String SQL_QUERY_UPDATE = "UPDATE calendar_notify_key "
			+ " SET key_email = ?, email = ?, id_agenda = ?,date_expiry = ? WHERE key_email = ?";
	private static final String SQL_QUERY_DELETE = " DELETE FROM calendar_notify_key WHERE key_email = ? ";
	private static final String SQL_QUERY_SELECT_EXPIRY = " SELECT key_email, email, id_agenda,date_expiry FROM calendar_notify_key WHERE date_expiry < NOW(  )";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void insert( CalendarNotification calendarNotification, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
		{

			int nPos = 0;

			daoUtil.setString( ++nPos, calendarNotification.getKey( ) );
			daoUtil.setString( ++nPos, calendarNotification.getEmail( ) );
			daoUtil.setInt( ++nPos, calendarNotification.getIdAgenda( ) );
			daoUtil.setTimestamp( ++nPos, calendarNotification.getDateExpiry( ) );

			daoUtil.executeUpdate( );
			daoUtil.free( );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void store( CalendarNotification calendarNotification, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
		{

			int nPos = 0;

			daoUtil.setString( ++nPos, calendarNotification.getKey( ) );
			daoUtil.setString( ++nPos, calendarNotification.getEmail( ) );
			daoUtil.setInt( ++nPos, calendarNotification.getIdAgenda( ) );
			daoUtil.setTimestamp( ++nPos, calendarNotification.getDateExpiry( ) );

			daoUtil.setString( ++nPos, calendarNotification.getKey( ) );
			daoUtil.executeUpdate( );
			daoUtil.free( );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CalendarNotification load( String strKey, Plugin plugin )
	{
		CalendarNotification calendarNotification = null;
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
		{

			daoUtil.setString( 1, strKey );
			daoUtil.executeQuery( );

			int nPos = 0;

			if ( daoUtil.next( ) )
			{
				calendarNotification = new CalendarNotification( );
				calendarNotification.setKey( daoUtil.getString( ++nPos ) );
				calendarNotification.setEmail( daoUtil.getString( ++nPos ) );
				calendarNotification.setIdAgenda( daoUtil.getInt( ++nPos ) );
				calendarNotification.setDateExpiry( daoUtil.getTimestamp( ++nPos ) );
			}

			daoUtil.free( );
		}

		return calendarNotification;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete( String strKey, Plugin plugin )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
		{

			daoUtil.setString( 1, strKey );
			daoUtil.executeUpdate( );
			daoUtil.free( );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CalendarNotification> selectNotificationExpiry( Plugin plugin )
	{
		int nPos = 0;
		List<CalendarNotification> listNotificationExpiry = new ArrayList<>( );
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EXPIRY, plugin ) )
		{
			daoUtil.executeQuery( );

			while ( daoUtil.next( ) )
			{
				nPos = 0;

				CalendarNotification calendarNotification = new CalendarNotification( );
				calendarNotification.setKey( daoUtil.getString( ++nPos ) );
				calendarNotification.setEmail( daoUtil.getString( ++nPos ) );
				calendarNotification.setIdAgenda( daoUtil.getInt( ++nPos ) );
				calendarNotification.setDateExpiry( daoUtil.getTimestamp( ++nPos ) );
				listNotificationExpiry.add( calendarNotification );
			}

			daoUtil.free( );
		}

		return listNotificationExpiry;
	}
}
