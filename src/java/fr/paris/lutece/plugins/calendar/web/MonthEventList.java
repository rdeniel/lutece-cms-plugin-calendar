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
package fr.paris.lutece.plugins.calendar.web;

import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.business.MultiAgenda;
import fr.paris.lutece.plugins.calendar.business.MultiAgendaEvent;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.plugins.calendar.service.search.CalendarSearchService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * An implementation of an EvenList, listing all events of a month
 */
public class MonthEventList implements EventList
{
    private static final String TEMPLATE_MONTH_EVENT_LIST = "skin/plugins/calendar/calendar_eventlist_month.html";
    private static final String TEMPLATE_MONTH_EVENT_LIST_DAY = "skin/plugins/calendar/calendar_eventlist_month_day.html";
    private static final String TEMPLATE_MONTH_EVENT_LIST_EVENT = "skin/plugins/calendar/calendar_eventlist_month_event.html";

    /**
     * Build an event list corresponding to the date, the agenda and privileges
     * stored in the HTTP request
     * @return The HTML code of the event list formatted
     * @param locale The locale
     * @param strMonthDate The date of the event list
     * @param agenda The multi agenda object
     * @param request the request
     */
    public String getEventList( String strMonthDate, MultiAgenda agenda, Locale locale, HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>( );
        int nYear = Utils.getYear( strMonthDate );
        int nMonth = Utils.getMonth( strMonthDate );
        StringBuffer sbDays = new StringBuffer( );

        for ( int nDay = 1; nDay < 32; nDay++ )
        {
            String strDate = Utils.getDate( nYear, nMonth, nDay );

            if ( agenda.hasEvents( strDate ) )
            {
                sbDays.append( getDay( strDate, agenda, locale, request ) );
            }
        }

        model.put( Constants.MARK_DAYS, sbDays.toString( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MONTH_EVENT_LIST, locale, model );

        return template.getHtml( );
    }

    /**
     * Build the day
     * @return The day formatted
     * @param locale The locale
     * @param strDate The date of the Day
     * @param agenda The agenda that contains events
     * @param request The request
     */
    private String getDay( String strDate, MultiAgenda agenda, Locale locale, HttpServletRequest request )
    {
        HashMap<String, Object> dayModel = new HashMap<String, Object>( );
        StringBuffer sbEvents = new StringBuffer( );
        Date date = Utils.getDate( strDate );
        Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
        List<Event> listIndexedEvents = CalendarSearchService.getInstance( ).getSearchResults( agenda.getAgendaIds( ),
                null, Constants.EMPTY_STRING, date, date, plugin );
        boolean bHasIndexedEvent = false;

        for ( Event event : listIndexedEvents )
        {
            MultiAgendaEvent multiAgendaEvent = new MultiAgendaEvent( event, String.valueOf( event.getIdCalendar( ) ) );
            HashMap<String, Object> eventModel = new HashMap<String, Object>( );
            HtmlUtils.fillEventTemplate( eventModel, multiAgendaEvent, strDate );

            HtmlTemplate tEvent = AppTemplateService.getTemplate( TEMPLATE_MONTH_EVENT_LIST_EVENT, locale, eventModel );
            sbEvents.append( tEvent.getHtml( ) );
            bHasIndexedEvent = true;
        }

        if ( bHasIndexedEvent )
        {
            dayModel.put( Constants.MARK_DAY, Utils.getDayLabel( strDate, locale ) );
            dayModel.put( Constants.MARK_EVENTS, sbEvents.toString( ) );
            HtmlTemplate tDay = AppTemplateService.getTemplate( TEMPLATE_MONTH_EVENT_LIST_DAY, locale, dayModel );

            return tDay.getHtml( );
        }

        return Constants.EMPTY_STRING;
    }
}
