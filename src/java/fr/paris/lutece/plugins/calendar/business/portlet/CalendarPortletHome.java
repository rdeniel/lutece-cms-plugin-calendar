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
package fr.paris.lutece.plugins.calendar.business.portlet;

import java.util.Date;
import java.util.List;

import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 * This class provides instances management methods for CalendarPortlet objects
 */
public class CalendarPortletHome extends PortletHome
{
    // This class implements the Singleton design pattern.
    private static CalendarPortletHome _singleton;

    // Static variable pointed at the DAO instance
    private static ICalendarPortletDAO _dao = (CalendarPortletDAO) SpringContextService.getPluginBean( "calendar",
            "calendar.calendarPortletDAO" );

    /**
     * Constructor
     */
    public CalendarPortletHome(  )
    {
        if ( _singleton == null )
        {
            _singleton = this;
        }
    }

    /**
     * Returns the identifier of the portlet type
     *
     * @return the portlet type identifier
     */
    public String getPortletTypeId(  )
    {
        String strCurrentClassName = this.getClass(  ).getName(  );
        String strPortletTypeId = PortletTypeHome.getPortletTypeId( strCurrentClassName );

        return strPortletTypeId;
    }

    /**
     * Returns the instance of  NewsLetterArchive Portlet
     *
     * @return the Archive Portlet instance
     */
    public static PortletHome getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new CalendarPortletHome(  );
        }

        return _singleton;
    }

    /**
     * Returns the instance of the portlet DAO singleton
     *
     * @return the instance of the DAO singleton
     */
    public IPortletInterfaceDAO getDAO(  )
    {
        return _dao;
    }

    /**
     * Associates a new agenda to a given portlet.
     * @param strAgendaId The identifier of the agenda
     * @param strDateBegin The beginning date
     * @param strDateEnd The end date
     * @param nPortletId The identifier of the portlet.
     */
    public static void insertAgendaInterval( int nPortletId, String strAgendaId, String strDateBegin, String strDateEnd )
    {
        _dao.insertAgendaInterval( nPortletId, strAgendaId, strDateBegin, strDateEnd );
    }

    /**
     * De-associate an agenda from a given portlet.
     * @param nPortletId the identifier of the portlet.
     * @param strAgenda the identifier of the agenda
     */
    public static void removeAgenda( int nPortletId, String strAgenda )
    {
        _dao.removeAgenda( nPortletId, strAgenda );
    }

    /**
     * De-associate an agenda from a given portlet.
     * @param nPortletId the identifier of the portlet.
     */
    public static void removeAllAgendas( int nPortletId )
    {
        _dao.delete( nPortletId );
    }

    /**
     * Returns all the agenda associated with a given portlet.
     * @param nPortletId the identifier of the portlet.
     * @return a list of agenda resources unfiltered
     */
    public static List<AgendaResource> findAgendasInPortlet( int nPortletId )
    {
        return _dao.findAgendasInPortlet( nPortletId );
    }

    /**
     * Returns all the agenda associated with a given portlet.
     * @return a list of agenda resources unfiltered
     * @param dateBegin The start date
     * @param dateEnd The end date
     * @param nPortletId the identifier of the portlet.
     */
    public static List<AgendaResource> findAgendaBetween( int nPortletId, Date dateBegin, Date dateEnd )
    {
        return _dao.findAgendaBetween( nPortletId, dateBegin, dateEnd );
    }

    /**
     * Associates a new calendar to a given portlet.
     * @param strAgendaId The id of the agenda
     * @param nDays The number of days
     * @param nPortletId the identifier of the portlet.
     */
    public static void insertCalendar( int nPortletId, String strAgendaId, int nDays )
    {
        _dao.insertCalendar( nPortletId, strAgendaId, nDays );
    }

    /**
     * Returns the beginning date of the display of corresponding events
     * @param nPortletId The id portlet
     * @return Returns the start date
     */
    public static Date getBeginDate( int nPortletId )
    {
        return _dao.getBeginDate( nPortletId );
    }

    /**
     * Returns the last date of the display
     * @param nPortletId The id of the portlet
     * @return The End date of the portlet display
     */
    public static Date getEndDate( int nPortletId )
    {
        return _dao.getEndDate( nPortletId );
    }

    /**
     * Returns the number of days within which the events will occur
     * @param nPortletId The id of the portlet
     * @return Returns the number of days
     */
    public static int getRepetitionDays( int nPortletId )
    {
        return _dao.getRepetitionDays( nPortletId );
    }
}
