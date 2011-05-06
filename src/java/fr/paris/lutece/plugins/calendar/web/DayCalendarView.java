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
package fr.paris.lutece.plugins.calendar.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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


/**
 * This class provides a calendar view by Day.
 */
public class DayCalendarView implements CalendarView
{
    private static final String TEMPLATE_VIEW_DAY = "skin/plugins/calendar/calendar_view_day.html";
    private static final String TEMPLATE_VIEW_DAY_EVENT = "skin/plugins/calendar/calendar_view_day_event.html";

    /**
     * Returns the HTML view of the Month corresponding to the given date and displaying
     * events of a given agenda
     * @return The view in HTML
     * @param options The options
     * @param strDate The date code
     * @param agenda An agenda
     */
    public String getCalendarView( String strDate, MultiAgenda agenda, CalendarUserOptions options, HttpServletRequest request )
    {
        Map<String, Object> dayModel = new HashMap<String, Object>(  );
        StringBuffer sbEvents = new StringBuffer(  );

        if ( agenda.hasEvents( strDate ) )
        {
            Date date = Utils.getDate( strDate );
            Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
            List<Event> listIndexedEvents = CalendarSearchService.getInstance(  )
            	.getSearchResults( agenda.getAgendaIds(  ), null, "", date, date, request, plugin );
            
            for ( Event event : listIndexedEvents )
            {
            	MultiAgendaEvent multiAgendaEvent = new MultiAgendaEvent( event, String.valueOf( event.getIdCalendar(  ) ) );
            	Map<String, Object> eventModel = new HashMap<String, Object>(  );
                HtmlUtils.fillEventTemplate( eventModel, multiAgendaEvent, strDate );

                HtmlTemplate tEvent = AppTemplateService.getTemplate( TEMPLATE_VIEW_DAY_EVENT, options.getLocale(  ),
                        eventModel );
                sbEvents.append( tEvent.getHtml(  ) );
            }
        }

        dayModel.put( Constants.MARK_EVENTS, sbEvents.toString(  ) );

        HtmlTemplate tDay = AppTemplateService.getTemplate( TEMPLATE_VIEW_DAY, options.getLocale(  ), dayModel );

        return tDay.getHtml(  );
    }

    /**
     * Returns the next code date corresponding to the current view and the current date
     * @param strDate The current date code
     * @return The next code date
     */
    public String getNext( String strDate )
    {
        return Utils.getNextDay( strDate );
    }

    /**
     * Returns the previous code date corresponding to the current view and the current date
     * @param strDate The current date code
     * @return The previous code date
     */
    public String getPrevious( String strDate )
    {
        return Utils.getPreviousDay( strDate );
    }

    /**
     * Returns the view title
     * @return The view title
     * @param options The options
     * @param strDate The current date code
     */
    public String getTitle( String strDate, CalendarUserOptions options )
    {
        return Utils.getDayLabel( strDate, options.getLocale(  ) );
    }

    /**
     * Returns the view path
     * @return The view path
     * @param options The options
     * @param strDate The current date code
     */
    public String getPath( String strDate, CalendarUserOptions options )
    {
        return getTitle( strDate, options );
    }

    /**
     * Returns the view type
     * @return The view type
     */
    public int getType(  )
    {
        return CalendarView.TYPE_DAY;
    }
}
