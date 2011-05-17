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
package fr.paris.lutece.plugins.calendar.business.portlet;

import java.util.Date;
import java.util.List;

import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;


/**
 * This class provides Data Access methods for CalendarPortlet objects
 */
public interface ICalendarPortletDAO extends IPortletInterfaceDAO
{
    /**
     * Associates a new agenda to a given portlet.
     * @param strAgendaId The identifier of the agenda
     * @param dDateBegin The beginning date
     * @param dDateEnd The end date
     * @param nPortletId the identifier of the portlet.
     */
    void insertAgendaInterval( int nPortletId, String strAgendaId, String dDateBegin, String dDateEnd );

    /**
     * De-associate an agenda from a given portlet.
     * @param strAgendaId The identifier of an agenda resource
     * @param nPortletId the identifier of the portlet.
     */
    void removeAgenda( int nPortletId, String strAgendaId );

    /**
     * Returns all the agendas associated with a given portlet.
     * @param nPortletId the identifier of the portlet.
     * @return a list of unfiltered agenda resources.
     */
    List<AgendaResource> findAgendasInPortlet( int nPortletId );

    /**
     * Finds the agenda which has events between the start and the end date
     * @param nPortletId The id of the portlet
     * @param dateBegin The starting date
     * @param dateEnd The finishing date
     * @return The agenda
     */
    List<AgendaResource> findAgendaBetween( int nPortletId, Date dateBegin, Date dateEnd );

    /**
     * The beginning date of event display
     * @param nPortletId The id of the portlet
     * @return The beginning date
     */
    Date getBeginDate( int nPortletId );

    /**
     * The finishing date of the event display
     * @param nPortletId The id of the portlet
     * @return The finishing date
     */
    Date getEndDate( int nPortletId );

    /**
     * Returns the number of following days the event should be displayed
     * @param nPortletId The id of the portlet
     * @return The number of days
     */
    int getRepetitionDays( int nPortletId );

    /**
     * Inserts a calendar which will be displayed for the future n days
     * @param nPortletId The id of the portlet
     * @param strAgendaId The id of the calendar
     * @param nDays The number of days the events are displayed
     */
    void insertCalendar( int nPortletId, String strAgendaId, int nDays );
}
