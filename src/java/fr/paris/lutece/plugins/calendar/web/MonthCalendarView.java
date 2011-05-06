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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;


/**
 * This class provides a calendar view by Month.
 */
public class MonthCalendarView implements CalendarView
{
    private static final String TEMPLATE_VIEW_MONTH = "skin/plugins/calendar/calendar_view_month.html";
    private static final String TEMPLATE_VIEW_WEEK = "skin/plugins/calendar/calendar_view_month_week.html";
    private static final String TEMPLATE_VIEW_DAY = "skin/plugins/calendar/calendar_view_month_day.html";
    private static final String TEMPLATE_VIEW_DAY_EVENT = "skin/plugins/calendar/calendar_view_month_event.html";
    private static final String TEMPLATE_VIEW_EMPTY_DAY = "skin/plugins/calendar/calendar_view_month_empty_day.html";

    /**
     * Returns the HTML view of the Month corresponding to the given date and displaying
     * events of a given agenda
     * @return The view in HTML
     * @param options The options
     * @param strDate The date code
     * @param agenda An agenda
     * @param request HttpServletRequest
     */
    public String getCalendarView( String strDate, MultiAgenda agenda, CalendarUserOptions options, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        HtmlTemplate tEmptyDay = AppTemplateService.getTemplate( TEMPLATE_VIEW_EMPTY_DAY );

        Calendar calendar = new GregorianCalendar(  );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), 1 );

        int nDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );

        if ( nDayOfWeek == 1 )
        {
            nDayOfWeek = 8;
        }

        StringBuffer sbWeeks = new StringBuffer(  );

        boolean bDone = false;
        boolean bStarted = false;

        while ( !bDone )
        {
            StringBuffer sbDays = new StringBuffer(  );

            for ( int i = 0; i < 7; i++ )
            {
                if ( ( ( ( i + 2 ) != nDayOfWeek ) && !bStarted ) || bDone )
                {
                    sbDays.append( tEmptyDay.getHtml(  ) );

                    continue;
                }
                else
                {
                    bStarted = true;
                }

                sbDays.append( getDay( calendar, agenda, options, request ) );

                int nDay = calendar.get( Calendar.DAY_OF_MONTH );
                calendar.roll( Calendar.DAY_OF_MONTH, true );

                int nNewDay = calendar.get( Calendar.DAY_OF_MONTH );

                if ( nNewDay < nDay )
                {
                    bDone = true;
                }
            }

            model.put( Constants.MARK_DAYS, sbDays.toString(  ) );

            HtmlTemplate weekTemplate = AppTemplateService.getTemplate( TEMPLATE_VIEW_WEEK, options.getLocale(  ), model );
            sbWeeks.append( weekTemplate.getHtml(  ) );
        }

        model.put( Constants.MARK_WEEKS, sbWeeks.toString(  ) );
        model.put( Constants.MARK_MONTH_LABEL, Utils.getMonthLabel( strDate, options.getLocale(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_VIEW_MONTH, options.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Build the day view of a given
     * @return The HTML of a day
     * @param options The options
     * @param calendar A calendar object positioned on a given day
     * @param agenda The agenda
     * @param request HttpServletRequest
     */
    private String getDay( Calendar calendar, MultiAgenda agenda, CalendarUserOptions options, HttpServletRequest request )
    {
        Map<String, Object> dayModel = new HashMap<String, Object>(  );
        StringBuffer sbEvents = new StringBuffer(  );
        String strDate = Utils.getDate( calendar );

        if ( agenda.hasEvents( strDate ) )
        {
            Date date = Utils.getDate( Utils.getDate( calendar ) );
            Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
            List<Event> listIndexedEvents = CalendarSearchService.getInstance(  )
            	.getSearchResults( agenda.getAgendaIds(  ), null, "", date, date, request, plugin );
            
            for ( Event event : listIndexedEvents )
            {
    			MultiAgendaEvent multiAgendaEvent = new MultiAgendaEvent( event, String.valueOf( event.getIdCalendar(  ) ) );
            	Map<String, Object> eventModel = new HashMap<String, Object>(  );
                HtmlUtils.fillEventTemplate( eventModel, multiAgendaEvent, strDate );
                eventModel.put( Constants.MARK_JSP_URL,
                    AppPropertiesService.getProperty( Constants.PROPERTY_RUNAPP_JSP_URL ) );

                HtmlTemplate tEvent = AppTemplateService.getTemplate( TEMPLATE_VIEW_DAY_EVENT, options.getLocale(  ),
                        eventModel );
                sbEvents.append( tEvent.getHtml(  ) );
            }
        }

        String strDateLink = Utils.getDate( calendar );
        dayModel.put( Constants.MARK_DAY_LINK, strDateLink );
        dayModel.put( Constants.MARK_DAY_CLASS, getDayClass( calendar ) );
        dayModel.put( Constants.MARK_DAY, calendar.get( Calendar.DAY_OF_MONTH ) );
        dayModel.put( Constants.MARK_EVENTS, sbEvents.toString(  ) );
        dayModel.put( Constants.MARK_DATE, strDate );

        //we only show link on the calendar for days with events
        if ( agenda.hasEvents( strDate ) )
        {
            dayModel.put( Constants.MARK_JSP_URL, AppPropertiesService.getProperty( Constants.PROPERTY_RUNAPP_JSP_URL ) );
        }
        else
        {
            dayModel.put( Constants.MARK_JSP_URL, "" );
        }

        HtmlTemplate tDay = AppTemplateService.getTemplate( TEMPLATE_VIEW_DAY, options.getLocale(  ), dayModel );

        return tDay.getHtml(  );
    }

    /**
     * Calculate the style class to render the day
     * @param calendar A calendar object positionned on the day to render
     * @return A CSS style
     */
    private String getDayClass( Calendar calendar )
    {
        String strClass = Constants.STYLE_CLASS_VIEW_MONTH_DAY;
        String strDate = Utils.getDate( calendar );
        String strToday = Utils.getDateToday(  );

        if ( Utils.isDayOff( calendar ) )
        {
            strClass += Constants.STYLE_CLASS_SUFFIX_OFF;
        }
        else if ( strDate.compareTo( strToday ) < 0 )
        {
            strClass += Constants.STYLE_CLASS_SUFFIX_OLD;
        }
        else if ( strDate.equals( strToday ) )
        {
            strClass += Constants.STYLE_CLASS_SUFFIX_TODAY;
        }

        return strClass;
    }

    /**
     * Returns the next code date corresponding to the current view and the current date
     * @param strDate The current date code
     * @return The next code date
     */
    public String getNext( String strDate )
    {
        return Utils.getNextMonth( strDate );
    }

    /**
     * Returns the previous code date corresponding to the current view and the current date
     * @param strDate The current date code
     * @return The previous code date
     */
    public String getPrevious( String strDate )
    {
        return Utils.getPreviousMonth( strDate );
    }

    /**
     * Returns the view title
     * @return The view title
     * @param options The options
     * @param strDate The current date code
     */
    public String getTitle( String strDate, CalendarUserOptions options )
    {
        String strTitle = Utils.getMonthLabel( strDate, options.getLocale(  ) );

        return strTitle;
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
        return CalendarView.TYPE_MONTH;
    }
}
