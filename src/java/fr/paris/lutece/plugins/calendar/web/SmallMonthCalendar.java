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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.business.MultiAgenda;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.plugins.calendar.service.search.CalendarSearchService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This class provides a Small Html Month calendar.
 */
public class SmallMonthCalendar
{
    // Templates
    private static final String TEMPLATE_VIEW_MONTH = "skin/plugins/calendar/small_month_calendar.html";
    private static final String TEMPLATE_WEEK = "skin/plugins/calendar/small_month_calendar_week.html";
    private static final String TEMPLATE_DAY = "skin/plugins/calendar/small_month_calendar_day.html";
    private static final String TEMPLATE_EMPTY_DAY = "skin/plugins/calendar/small_month_calendar_empty_day.html";

    /**
     * Provides a small HTML month calendar displaying days with links
     * @return The HTML code of the month.
     * @param options The options which contains displaying settings
     * @param strDate The code date defining the month to display
     * @param agenda An agenda to hilight some days.
     * @param bIsSelectedDay true if the date is the selected day, false otherwise
     */
    public static String getSmallMonthCalendar( String strDate, Agenda agenda, CalendarUserOptions options, boolean bIsSelectedDay )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        Calendar calendar = new GregorianCalendar(  );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), 1 );

        Calendar firstDayOfMonth = new GregorianCalendar(  );
        firstDayOfMonth.set( Utils.getYear( strDate ), Utils.getMonth( strDate ),
            calendar.getMinimum( Calendar.DAY_OF_MONTH ) );

        Date dFirstDayOfMonth = firstDayOfMonth.getTime(  );

        Calendar lastDayOfMonth = new GregorianCalendar(  );
        lastDayOfMonth.set( Utils.getYear( strDate ), Utils.getMonth( strDate ),
            calendar.getMaximum( Calendar.DAY_OF_MONTH ) );

        Date dLastDayOfMonth = lastDayOfMonth.getTime(  );

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
            Map<String, Object> weekModel = new HashMap<String, Object>(  );

            //HtmlTemplate tWeek = new HtmlTemplate( templateWeek );
            StringBuffer sbDays = new StringBuffer(  );

            for ( int i = 0; i < 7; i++ )
            {
                if ( ( ( ( i + 2 ) != nDayOfWeek ) && !bStarted ) || bDone )
                {
                    sbDays.append( AppTemplateService.getTemplate( TEMPLATE_EMPTY_DAY ).getHtml(  ) );

                    continue;
                }
                else
                {
                    bStarted = true;
                }

                if ( strDate.equals( Utils.getDate( calendar ) ) && bIsSelectedDay )
                {
                	sbDays.append( getDay( calendar, agenda, options, true ) );
                }
                else
                {
                	sbDays.append( getDay( calendar, agenda, options, false ) );
                }

                int nDay = calendar.get( Calendar.DAY_OF_MONTH );
                calendar.roll( Calendar.DAY_OF_MONTH, true );

                int nNewDay = calendar.get( Calendar.DAY_OF_MONTH );

                if ( nNewDay < nDay )
                {
                    bDone = true;
                }
            }

            weekModel.put( Constants.MARK_DAYS, sbDays.toString(  ) );
            sbWeeks.append( AppTemplateService.getTemplate( TEMPLATE_WEEK, options.getLocale(  ), weekModel ).getHtml(  ) );
        }

        model.put( Constants.MARK_MONTH_LABEL, Utils.getMonthLabel( strDate, options.getLocale(  ) ) );
        model.put( Constants.MARK_PREVIOUS, Utils.getPreviousMonth( strDate ) );
        model.put( Constants.MARK_DATE, strDate );
        model.put( Constants.MARK_NEXT, Utils.getNextMonth( strDate ) );
        model.put( Constants.MARK_DATE_START, dFirstDayOfMonth );
        model.put( Constants.MARK_DATE_END, dLastDayOfMonth );

        model.put( Constants.MARK_WEEKS, sbWeeks.toString(  ) );
        model.put( Constants.MARK_JSP_URL, AppPropertiesService.getProperty( Constants.PROPERTY_RUNAPP_JSP_URL ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_VIEW_MONTH, options.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Build the day view of a given
     * @return The HTML of a day
     * @param options The options which stores the display settings
     * @param calendar A calendar object positioned on a given day
     * @param agenda The agenda
     * @param bIsSelectedDay true if the date is the selected day, false otherwise
     */
    private static String getDay( Calendar calendar, Agenda agenda, CalendarUserOptions options, boolean bIsSelectedDay )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        String strDate = Utils.getDate( calendar );
        Date date = Utils.getDate( strDate );
        String strLinkClass = AppPropertiesService.getProperty( Constants.PROPERTY_SMALLCALENDAR_LINKCLASS_NO_EVENT );
        Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
        String[] strAgendaIds = null;
        if ( agenda instanceof fr.paris.lutece.plugins.calendar.business.MultiAgenda )
        {
        	MultiAgenda multiAgenda = ( MultiAgenda ) agenda;
        	if ( multiAgenda.getAgendaIds(  ) != null && multiAgenda.getAgendaIds(  ).length > 0 )
        	{
        		strAgendaIds = multiAgenda.getAgendaIds(  );
        	}
        }
        List<Event> listEvent;
        
        if ( agenda.hasEvents( strDate ) )
        {
        	listEvent = CalendarSearchService.getInstance(  )
        		.getSearchResults( strAgendaIds, null, "", date, date, LocalVariables.getRequest(  ), plugin );
        }
        else
        {
        	listEvent = new ArrayList<Event>(  );
        }
        
        if ( listEvent.size(  ) != 0 )
        {
            strLinkClass = AppPropertiesService.getProperty( Constants.PROPERTY_SMALLCALENDAR_LINKCLASS_HAS_EVENTS );
        }

        model.put( Constants.MARK_LINK_CLASS, strLinkClass );
        model.put( Constants.MARK_DATE, strDate );
        model.put( Constants.MARK_DAY, calendar.get( Calendar.DAY_OF_MONTH ) );
        model.put( Constants.MARK_DAY_CLASS, getDayClass( calendar, bIsSelectedDay ) );

        //model.put( Constants.MARK_JSP_URL, AppPropertiesService.getProperty( Constants.PROPERTY_RUNAPP_JSP_URL ) );

        //we only show link days with events on calendar
        if ( listEvent.size(  ) != 0 && !options.isShowSearchEngine(  ) )
        {
            UrlItem urlDay = new UrlItem( AppPathService.getPortalUrl(  ) );
            urlDay.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
            urlDay.addParameter( Constants.PARAMETER_DATE, strDate );
            model.put( Constants.MARK_JSP_URL, urlDay.getUrl(  ) );
        }
        else if ( listEvent.size(  ) != 0 && options.isShowSearchEngine(  ) )
        {
            UrlItem urlDay = new UrlItem( AppPathService.getPortalUrl(  ) );
            urlDay.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
            urlDay.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_DO_SEARCH );
            urlDay.addParameter( Constants.PARAMETER_DATE_START,
                DateUtil.getDateString( Utils.getDate( strDate ), options.getLocale(  ) ) );
            urlDay.addParameter( Constants.PARAMETER_DATE_END,
                DateUtil.getDateString( Utils.getDate( strDate ), options.getLocale(  ) ) );
            urlDay.addParameter( Constants.PARAMETER_PERIOD, Constants.PROPERTY_PERIOD_RANGE );
            model.put( Constants.MARK_JSP_URL, urlDay.getUrl(  ) );
        }
        else
        {
            model.put( Constants.MARK_JSP_URL, "" );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DAY, options.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Calculate the style class to render the day
     * @param calendar A calendar object positionned on the day to render
     * @param bIsSelectedDay true if the date is the selected day, false otherwise
     * @return A CSS style
     */
    public static String getDayClass( Calendar calendar, boolean bIsSelectedDay )
    {
        String strClass = Constants.STYLE_CLASS_SMALLMONTH_DAY;
        String strDate = Utils.getDate( calendar );
        String strToday = Utils.getDateToday(  );

        if ( CalendarHome.hasOccurrenceEvent( calendar, PluginService.getPlugin( Constants.PLUGIN_NAME ) ) )
        {
            strClass += Constants.STYLE_CLASS_SUFFIX_EVENT;
        }
        else if ( Utils.isDayOff( calendar ) )
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
        
        if ( bIsSelectedDay )
        {
        	strClass += Constants.SPACE + Constants.STYLE_CLASS_SELECTED_DAY;
        }

        return strClass;
    }
}
