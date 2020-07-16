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
package fr.paris.lutece.plugins.calendar.business.portlet;

import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.CalendarService;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This class provides Data Access methods for CalendarPortlet objects
 */
public final class CalendarPortletDAO implements ICalendarPortletDAO
{
	// Constants
	private static final String SQL_QUERY_SELECT = "SELECT id_portlet FROM core_portlet WHERE id_portlet = ?";
	private static final String SQL_QUERY_SELECT_AGENDAS_BY_PORTLET = "select code_agenda_name from calendar_portlet where id_portlet = ?";
	private static final String SQL_QUERY_INSERT_AGENDA = "INSERT INTO calendar_portlet( id_portlet, code_agenda_name, date_begin, date_end ) VALUES ( ?,?,?,? )";
	private static final String SQL_QUERY_INSERT_AGENDA_DAYS = "INSERT INTO calendar_portlet( id_portlet, code_agenda_name, number_days ,date_begin , date_end ) VALUES ( ?,?,?,?,? )";
	private static final String SQL_QUERY_DELETE_AGENDA = " DELETE FROM calendar_portlet WHERE id_portlet = ? AND code_agenda_name = ?";
	private static final String SQL_QUERY_DELETE = "DELETE FROM calendar_portlet WHERE id_portlet = ?";
	private static final String SQL_QUERY_SELECT_AGENDAS_BY_PORTLET_BY_DATE = "SELECT date_begin,date_end FROM calendar_portlet WHERE id_portlet = ?";
	private static final String SQL_QUERY_BEGIN_DATE_BY_PORTLET = "SELECT date_begin FROM calendar_portlet where id_portlet = ?";
	private static final String SQL_QUERY_END_DATE_BY_PORTLET = "SELECT date_end FROM calendar_portlet where id_portlet = ?";
	private static final String SQL_QUERY_NUMBER_DAYS_BY_PORTLET = "SELECT number_days FROM calendar_portlet WHERE id_portlet = ?";

	///////////////////////////////////////////////////////////////////////////////////////
	//Access methods to data

	/**
	 * Inserts a new record in the table. Not implemented.
	 * 
	 * @param portlet the object to be inserted
	 */
	public void insert( Portlet portlet )
	{
		// Not implemented.
	}

	/**
	 * Deletes a record from the table.
	 * @param nPortletId the portlet id
	 */
	public void delete( int nPortletId )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
		{
			daoUtil.setInt( 1, nPortletId );
			daoUtil.executeUpdate( );
			daoUtil.free( );
		}
	}

	/**
	 * Loads the data of the portlet from the table.
	 * 
	 * @param nPortletId the portlet id
	 * @return the Portlet object
	 */
	public Portlet load( int nPortletId )
	{
		CalendarPortlet portlet = new CalendarPortlet( );

		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
		{

			daoUtil.setInt( 1, nPortletId );

			daoUtil.executeQuery( );

			if ( daoUtil.next( ) )
			{
				portlet.setId( nPortletId );
			}

			daoUtil.free( );
		}

		return portlet;
	}

	/**
	 * Updates the record in the table. Not implemented.
	 * 
	 * @param portlet
	 *            the instance of Portlet class to be updated
	 */
	public void store( Portlet portlet )
	{
		// Not implemented.
	}

	/**
	 * Associates a new agenda to a given portlet.
	 * @param strAgendaId The identifier of an agenda
	 * @param dDateBegin The beginning date
	 * @param dDateEnd The end date
	 * @param nPortletId The identifier of the portlet.
	 */
	public void insertAgendaInterval( int nPortletId, String strAgendaId, String dDateBegin, String dDateEnd )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_AGENDA ) )
		{
			daoUtil.setInt( 1, nPortletId );
			daoUtil.setString( 2, strAgendaId );
			daoUtil.setString( 3, dDateBegin );
			daoUtil.setString( 4, dDateEnd );

			daoUtil.executeUpdate( );
			daoUtil.free( );
		}
	}

	/**
	 * De-associate a agenda from a given portlet.
	 * @param strAgendaId The identifier of an agenda
	 * @param nPortletId The identifier of the portlet.
	 */
	public void removeAgenda( int nPortletId, String strAgendaId )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_AGENDA ) )
		{
			daoUtil.setInt( 1, nPortletId );
			daoUtil.setString( 2, strAgendaId );

			daoUtil.executeUpdate( );
			daoUtil.free( );
		}
	}

	/**
	 * Returns all the sendings associated with a given portlet.
	 * @param nPortletId the identifier of the portlet.
	 * @return a list of unfiltered agenda resources
	 */
	public List<AgendaResource> findAgendasInPortlet( int nPortletId )
	{
		CalendarService calendarService = SpringContextService.getBean( Constants.BEAN_CALENDAR_CALENDARSERVICE );
		List<AgendaResource> listSelectedAgendas = new ArrayList<>( );
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_AGENDAS_BY_PORTLET ) )
		{
			daoUtil.setInt( 1, nPortletId );
			daoUtil.executeQuery( );

			while ( daoUtil.next( ) )
			{
				AgendaResource agendaResource = calendarService.getAgendaResource( daoUtil.getString( 1 ) );
				if ( agendaResource != null )
				{
					listSelectedAgendas.add( agendaResource );
				}
			}

			daoUtil.free( );
		}

		return listSelectedAgendas;
	}

	/**
	 * Find the list of agenda having events between two dates
	 * @param nPortletId The id of the portlet
	 * @param dateBegin The start date of the events display
	 * @param dateEnd The end date of the event display
	 * @return The list of agenda
	 */
	public List<AgendaResource> findAgendaBetween( int nPortletId, Date dateBegin, Date dateEnd )
	{
		CalendarService calendarService = SpringContextService.getBean( Constants.BEAN_CALENDAR_CALENDARSERVICE );
		List<AgendaResource> listSelectedAgendas = new ArrayList<>( );
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_AGENDAS_BY_PORTLET_BY_DATE ) )
		{
			daoUtil.setInt( 1, nPortletId );
			daoUtil.executeQuery( );

			while ( daoUtil.next( ) )
			{
				AgendaResource agendaResource = calendarService.getAgendaResource( daoUtil.getString( 1 ) );
				if ( agendaResource != null )
				{
					listSelectedAgendas.add( agendaResource );
				}
			}

			daoUtil.free( );
		}

		return listSelectedAgendas;
	}

	/**
	 * Returns the beginning date when the events will be displayed
	 * @param nPortletId The id of the portlet
	 * @return The start date
	 */
	public Date getBeginDate( int nPortletId )
	{
		Date dateBegin = null;
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_BEGIN_DATE_BY_PORTLET ) )
		{
			daoUtil.setInt( 1, nPortletId );
			daoUtil.executeQuery( );

			while ( daoUtil.next( ) )
			{
				try
				{
					dateBegin = new Date( daoUtil.getDate( 1 ).getTime( ) );
				}
				catch ( NullPointerException e )
				{
					throw new AppException("Null Pointer Exception", e);
				}
			}

			daoUtil.free( );

			return dateBegin;
		}
	}

	/**
	 * Returns the end date of the events display
	 * @param nPortletId The id of the portlet
	 * @return The end date of event display
	 */
	public Date getEndDate( int nPortletId )
	{
		Date dateEnd = null;
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_END_DATE_BY_PORTLET ) )
		{
			daoUtil.setInt( 1, nPortletId );
			daoUtil.executeQuery( );

			while ( daoUtil.next( ) )
			{
				try
				{
					dateEnd = new Date( daoUtil.getDate( 1 ).getTime( ) );
				}
				catch ( NullPointerException e )
				{
					throw new AppException("Null Pointer Exception", e);
				}
			}

			daoUtil.free( );
		}

		return dateEnd;
	}

	/**
	 * Fetches the number of future days during which the events of the agenda
	 * will be displayed
	 * @param nPortletId The id of the portlet
	 * @return The number of days
	 */
	public int getRepetitionDays( int nPortletId )
	{
		int nNumberDays = 0;
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NUMBER_DAYS_BY_PORTLET ) )
		{
			daoUtil.setInt( 1, nPortletId );
			daoUtil.executeQuery( );

			while ( daoUtil.next( ) )
			{
				nNumberDays = daoUtil.getInt( 1 );
			}

			daoUtil.free( );
		}

		return nNumberDays;
	}

	/**
	 * Inserts a calendar which will be displayed into the portlet for the
	 * following n days
	 * @param nPortletId The id of the portlet
	 * @param strAgendaId The id of the agenda
	 * @param nDays The number of days the events will be displayed
	 */
	public void insertCalendar( int nPortletId, String strAgendaId, int nDays )
	{
		try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_AGENDA_DAYS ) )
		{
			daoUtil.setInt( 1, nPortletId );
			daoUtil.setString( 2, strAgendaId );
			daoUtil.setInt( 3, nDays );
			daoUtil.setString( 4, Constants.EMPTY_NULL );
			daoUtil.setString( 5, Constants.EMPTY_NULL );
			daoUtil.executeUpdate( );
			daoUtil.free( );
		}
	}
}
