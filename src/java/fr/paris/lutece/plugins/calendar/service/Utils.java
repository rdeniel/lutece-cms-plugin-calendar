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
package fr.paris.lutece.plugins.calendar.service;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.OccurrenceEvent;
import fr.paris.lutece.plugins.calendar.business.SimpleAgenda;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Parser;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.ParserException;


/**
 * This class provides utils features to manipulate and convert calendars, date
 * as string, ...
 */
public final class Utils
{
    /**
     * Date pattern yyyyMMdd
     */
    private static String DATE_PATTERN = "yyyyMMdd";

    /**
     * Constructs a 8 digits date string code YYYYMMDD
     * @param nYear The Year
     * @param nMonth The month index (0-11)
     * @param nDay The day of the month (1-31)
     * @return The date string code
     */
    public static String getDate( int nYear, int nMonth, int nDay )
    {
        String strDate;
        strDate = "" + nYear;

        int nMonthIndex = nMonth + 1;
        strDate += ( ( nMonthIndex < 10 ) ? ( "0" + nMonthIndex ) : ( "" + nMonthIndex ) );
        strDate += ( ( nDay < 10 ) ? ( "0" + nDay ) : ( "" + nDay ) );

        return strDate;
    }

    /**
     * Constructs a 8 digits date string YYYYMMDD
     * @param calendar A calendar positionned on the date
     * @return The date code
     */
    public static String getDate( Calendar calendar )
    {
        return getDate( calendar.get( Calendar.YEAR ), calendar.get( Calendar.MONTH ),
                calendar.get( Calendar.DAY_OF_MONTH ) );
    }

    /**
     * Returns a the date code of today
     * @return The date code
     */
    public static String getDateToday( )
    {
        Calendar calendar = new GregorianCalendar( );

        return getDate( calendar );
    }

    /**
     * Returns the year from a date code
     * @param strDate The date code
     * @return The Year
     */
    public static int getYear( String strDate )
    {
        SimpleDateFormat format = new SimpleDateFormat( DATE_PATTERN );
        Date date = null;
        try
        {
            date = format.parse( strDate );
        }
        catch ( ParseException ex )
        {
            return -1;
        }
        GregorianCalendar calendar = new GregorianCalendar( );
        calendar.setTime( date );
        return calendar.get( Calendar.YEAR );
    }

    /**
     * Returns the month from a date code
     * @param strDate The date code
     * @return The month index (0 - 11)
     */
    public static int getMonth( String strDate )
    {
        SimpleDateFormat format = new SimpleDateFormat( DATE_PATTERN );
        Date date = null;
        try
        {
            date = format.parse( strDate );
        }
        catch ( ParseException ex )
        {
            return -1;
        }
        GregorianCalendar calendar = new GregorianCalendar( );
        calendar.setTime( date );
        return calendar.get( Calendar.MONTH );
    }

    /**
     * Returns the day of month from a date code
     * @param strDate The date code
     * @return The day
     */
    public static int getDay( String strDate )
    {
        SimpleDateFormat format = new SimpleDateFormat( DATE_PATTERN );
        Date date = null;
        try
        {
            date = format.parse( strDate );
        }
        catch ( ParseException ex )
        {
            return -1;
        }
        GregorianCalendar calendar = new GregorianCalendar( );
        calendar.setTime( date );
        return calendar.get( Calendar.DAY_OF_MONTH );
    }

    /**
     * Returns the month as a formatted string corresponding to the date code
     * @return The month label
     * @param locale The locale used for display settings
     * @param strDate The date code
     */
    public static String getMonthLabel( String strDate, Locale locale )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), 1 );

        String strFormat = AppPropertiesService.getProperty( Constants.PROPERTY_LABEL_FORMAT_MONTH );
        DateFormat formatDate = new SimpleDateFormat( strFormat, locale );
        String strLabel = formatDate.format( calendar.getTime( ) );

        return strLabel;
    }

    /**
     * Returns the Week as a formatted string corresponding to the date code
     * @return The week label
     * @param locale The locale used for display settings
     * @param strDate The date code
     */
    public static String getWeekLabel( String strDate, Locale locale )
    {
        Calendar calendar = new GregorianCalendar( );
        Calendar calendarFirstDay = new GregorianCalendar( );
        Calendar calendarLastDay = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );

        int nDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );

        if ( nDayOfWeek == 1 )
        {
            nDayOfWeek = 8;
        }

        calendarFirstDay = calendar;
        calendarFirstDay.add( Calendar.DATE, Calendar.MONDAY - nDayOfWeek );
        calendarLastDay = (GregorianCalendar) calendarFirstDay.clone( );
        calendarLastDay.add( Calendar.DATE, 6 );

        String strFormat = AppPropertiesService.getProperty( Constants.PROPERTY_LABEL_FORMAT_DATE_OF_DAY );
        DateFormat formatDate = new SimpleDateFormat( strFormat, locale );
        String strLabelFirstDay = formatDate.format( calendarFirstDay.getTime( ) );
        String strLabelLastDay = formatDate.format( calendarLastDay.getTime( ) );
        calendarFirstDay.clear( );
        calendarLastDay.clear( );

        return strLabelFirstDay + "-" + strLabelLastDay;
    }

    /**
     * Returns the first monday of a week as a formatted string corresponding to
     * the date code
     * @param strDate The date code
     * @return The first day label
     */
    public static Calendar getFirstDayOfWeek( String strDate )
    {
        Calendar calendar = new GregorianCalendar( );
        Calendar calendarFirstDay = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );

        int nDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );

        if ( nDayOfWeek == 1 )
        {
            nDayOfWeek = 8;
        }

        calendarFirstDay = calendar;
        calendarFirstDay.add( Calendar.DATE, Calendar.MONDAY - nDayOfWeek );

        return calendarFirstDay;
    }

    /**
     * Returns the day as an international formatted string corresponding to the
     * date code
     * @return The day as a string
     * @param locale The locale used for display settings
     * @param strDate The date code
     */
    public static String getDayLabel( String strDate, Locale locale )
    {
        Calendar calendar = new GregorianCalendar( locale );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );

        String strFormat = AppPropertiesService.getProperty( Constants.PROPERTY_LABEL_FORMAT_DAY );
        DateFormat formatDate = new SimpleDateFormat( strFormat, locale );

        return formatDate.format( calendar.getTime( ) );
    }

    /**
     * Returns the day as an international formatted string corresponding to the
     * date code
     * @return The day as a string
     * @param locale The locale used for display settings
     * @param strDate The date code
     */
    public static String getWeekDayLabel( String strDate, Locale locale )
    {
        Calendar calendar = new GregorianCalendar( locale );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );

        String strFormat = AppPropertiesService.getProperty( Constants.PROPERTY_LABEL_FORMAT_WEEK_DAY );
        DateFormat formatDate = new SimpleDateFormat( strFormat, locale );

        return formatDate.format( calendar.getTime( ) );
    }

    /**
     * Returns a date code corresponding to a calendar roll of one month
     * @param strDate The date code
     * @return A date code one month later
     */
    public static String getNextMonth( String strDate )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );
        calendar.add( Calendar.MONTH, 1 );

        return getDate( calendar );
    }

    /**
     * Returns a date code corresponding to a calendar roll of one month
     * backward
     * @param strDate The date code
     * @return A new date code one month earlier
     */
    public static String getPreviousMonth( String strDate )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );
        calendar.add( Calendar.MONTH, -1 );

        return getDate( calendar );
    }

    /**
     * Returns a date code corresponding to a calendar roll of one week backward
     * @param strDate The date code
     * @return A new date code one month earlier
     */
    public static String getPreviousWeek( String strDate )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );
        calendar.add( Calendar.DATE, -7 );

        return getDate( calendar );
    }

    /**
     * Returns a date code corresponding to a calendar roll of one week forward
     * @param strDate The date code
     * @return A new date code one month earlier
     */
    public static String getNextWeek( String strDate )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );
        calendar.add( Calendar.DATE, 7 );

        return getDate( calendar );
    }

    /**
     * Returns a date code corresponding to a calendar roll of one day forward
     * @param strDate The date code
     * @return A new date code one month earlier
     */
    public static String getNextDay( String strDate )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );
        calendar.add( Calendar.DATE, 1 );

        return getDate( calendar );
    }

    /**
     * Returns a date code corresponding to a calendar roll of one day backward
     * @param strDate The date code
     * @return A new date code one month earlier
     */
    public static String getPreviousDay( String strDate )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );
        calendar.add( Calendar.DATE, -1 );

        return getDate( calendar );
    }

    /**
     * Checks a date code
     * @param strDate The date code
     * @return True if valid otherwise false
     */
    public static boolean isValid( String strDate )
    {
        if ( strDate == null )
        {
            return false;
        }

        if ( strDate.length( ) != 8 )
        {
            return false;
        }

        int nYear;
        int nMonth;
        int nDay;

        try
        {
            nYear = getYear( strDate );
            nMonth = getMonth( strDate );
            nDay = getDay( strDate );
        }
        catch ( NumberFormatException e )
        {
            return false;
        }

        if ( ( nYear < 1900 ) || ( nYear > 2100 ) )
        {
            return false;
        }

        if ( ( nMonth < 0 ) || ( nMonth > 11 ) )
        {
            return false;
        }

        if ( ( nDay < 1 ) || ( nDay > 31 ) )
        {
            return false;
        }

        return true;
    }

    /**
     * Checks if the day if Off (ie: Sunday) or not
     * @param calendar A calendar object positionned on the day to check
     * @return True if the day if Off, otherwise false
     */
    public static boolean isDayOff( Calendar calendar )
    {
        int nDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );

        if ( ( nDayOfWeek == Calendar.SATURDAY ) || ( nDayOfWeek == Calendar.SUNDAY ) )
        {
            return true;
        }

        // Add other checks here
        return false;
    }

    /**
     * Return a boolean: if the time is well formed return true, else return
     * false
     * @return a boolean
     * @param strTime The time
     */
    public static boolean checkTime( String strTime )
    {
        boolean bCheck = false;

        if ( strTime.equals( "" ) )
        {
            bCheck = true;
        }
        else if ( strTime.length( ) == 5 )
        {
            try
            {
                int nHour = Integer.parseInt( strTime.substring( 0, 2 ) );
                int nMinute = Integer.parseInt( strTime.substring( 3, 5 ) );

                if ( ( strTime.charAt( 2 ) == ':' ) && ( nHour < 25 ) && ( nMinute < 60 ) )
                {
                    bCheck = true;
                }
            }
            catch ( NumberFormatException e )
            {
                bCheck = false;
            }
        }

        return bCheck;
    }

    /**
     * Returns a date code corresponding to a calendar roll of n days forward
     * @return A new date code one month earlier
     * @param n number of days to roll
     * @param strDate The date code
     */
    public static String getDayAfter( String strDate, int n )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );
        calendar.add( Calendar.DATE, n );

        return getDate( calendar );
    }

    /* Added in version 2.1.1 */
    /**
     * Returns a date code corresponding to a calendar roll of n days forward
     * @return A new date code with n days forward
     * @param n number of days to roll
     * @param dateDayAfter The date
     */
    public static Date getDayAfter( Date dateDayAfter, int n )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.setTime( dateDayAfter );
        calendar.add( Calendar.DATE, n );

        return calendar.getTime( );
    }

    /**
     * Returns a date code corresponding to a calendar roll of n days forward
     * @return A new date code one month earlier
     * @param dateDay the reference date
     * @param nPeriodicity the frequence of an occurrence day, week, month
     * @param nOccurrence the number of occurrences
     * @param arrayExcludedDays list of excluded days
     */
    public static Date getDateForward( Date dateDay, int nPeriodicity, int nOccurrence, String[] arrayExcludedDays )
    {
        int nOccurrenceDiff;
        int nOccurrenceInit = nOccurrence;
        Calendar calendar = new GregorianCalendar( );
        calendar.setTime( dateDay );

        // All days are excluded
        if ( arrayExcludedDays != null && arrayExcludedDays.length == 7 )
        {
            calendar.add( Calendar.DATE, -1 );
            return calendar.getTime( );
        }

        //the very first occurrence is omitted for the final count
        if ( nOccurrenceInit != 0 )
        {
            nOccurrenceInit -= 1;
        }

        switch ( nPeriodicity )
        {
        case Constants.PARAM_DAY:
            calendar.add( Calendar.DATE, nOccurrenceInit );
            do
            {
                nOccurrenceDiff = getOccurrenceWithinTwoDates( dateDay, calendar.getTime( ), arrayExcludedDays );
                if ( nOccurrenceDiff < nOccurrence )
                {
                    calendar.add( Calendar.DATE, nOccurrence - nOccurrenceDiff );
                }
            }
            while ( nOccurrenceDiff < nOccurrence && nOccurrenceDiff != 0 );

            break;

        case Constants.PARAM_WEEK:
            String strDate = getDate( dateDay );
            if ( !isDayExcluded( getDayOfWeek( strDate ), arrayExcludedDays ) )
            {
                calendar.add( Calendar.DATE, nOccurrenceInit * 7 );
            }

            break;

        case Constants.PARAM_MONTH:
            calendar.add( Calendar.MONTH, nOccurrenceInit );
            do
            {
                nOccurrenceDiff = getOccurrenceWithinTwoDates( dateDay, calendar.getTime( ), arrayExcludedDays );
                if ( nOccurrenceDiff < nOccurrence )
                {
                    calendar.add( Calendar.MONTH, nOccurrence - nOccurrenceDiff );
                }
            }
            while ( nOccurrenceDiff < nOccurrence && nOccurrenceDiff != 0 );

            break;

        default:
            calendar.add( Calendar.DATE, nOccurrenceInit );
            do
            {
                nOccurrenceDiff = getOccurrenceWithinTwoDates( dateDay, calendar.getTime( ), arrayExcludedDays );
                if ( nOccurrenceDiff < nOccurrence )
                {
                    calendar.add( Calendar.DATE, nOccurrence - nOccurrenceDiff );
                }
            }
            while ( nOccurrenceDiff < nOccurrence && nOccurrenceDiff != 0 );

            break;
        }

        return calendar.getTime( );
    }

    /**
     * Constructs a Date object from YYYYMMDD
     * @param strDate a 8 digits date string YYYYMMDD
     * @return The date code
     */
    public static Date getDate( String strDate )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDate ), Utils.getMonth( strDate ), Utils.getDay( strDate ) );

        return calendar.getTime( );
    }

    /**
     * Constructs a digit string from a date object
     * @param dateDigit The date code
     * @return strDate a 8 digits date string YYYYMMDD
     */
    public static String getDate( Date dateDigit )
    {
        if ( dateDigit == null )
        {
            return "";
        }
        Calendar calendar = new GregorianCalendar( );
        calendar.setTime( dateDigit );

        return getDate( calendar );
    }

    /**
     * Returns a date code corresponding to a calendar roll of one month
     * @param strDateRef The date reference
     * @param nCptDate The number of month to add
     * @return A date code incremented with strDateRef parameter
     */
    public static String getNextMonth( String strDateRef, int nCptDate )
    {
        Calendar calendar = new GregorianCalendar( );
        calendar.set( Utils.getYear( strDateRef ), Utils.getMonth( strDateRef ), Utils.getDay( strDateRef ) );
        calendar.add( Calendar.MONTH, nCptDate );

        return getDate( calendar );
    }

    /**
     * Returns a date code corresponding to a calendar roll of one month
     * @param dateStart The date start
     * @param dateEnd The date end
     * @param arrayExcludedDays list of excluded days
     * @return A date code incremented with strDateRef parameter
     */
    public static int getOccurrenceWithinTwoDates( Date dateStart, Date dateEnd, String[] arrayExcludedDays )
    {
        int cptDate = 0;
        Calendar calendar1 = new GregorianCalendar( );
        Calendar calendar2 = new GregorianCalendar( );
        calendar1.setTime( dateStart );
        calendar2.setTime( dateEnd );

        if ( calendar1.equals( calendar2 ) )
        {
            String strDate = getDate( dateStart );
            if ( isDayExcluded( getDayOfWeek( strDate ), arrayExcludedDays ) )
            {
                return cptDate;
            }
            return ++cptDate;
        }

        while ( !calendar1.after( calendar2 ) )
        {
            if ( !isDayExcluded( calendar1.get( Calendar.DAY_OF_WEEK ), arrayExcludedDays ) )
            {
                ++cptDate;
            }
            calendar1.add( Calendar.DATE, 1 );
        }

        return cptDate;
    }

    /**
     * Get a specified agenda from database with events
     * @param strAgenda The name of the agenda to get
     * @param request The HTTP request
     * @return An agenda object
     */
    public static Agenda getAgendaWithEvents( String strAgenda, HttpServletRequest request )
    {
        CalendarService calendarService = (CalendarService) SpringContextService
                .getBean( Constants.BEAN_CALENDAR_CALENDARSERVICE );
        Agenda agenda = null;

        if ( strAgenda != null )
        {
            AgendaResource agendaResource = calendarService.getAgendaResource( strAgenda );
            Agenda a = null;

            if ( agendaResource != null )
            {
                a = agendaResource.getAgenda( );
            }

            if ( a != null )
            {
                // Check security access
                String strRole = agendaResource.getRole( );

                if ( StringUtils.isNotBlank( strRole ) && ( request != null )
                        && ( !Constants.PROPERTY_ROLE_NONE.equals( strRole ) ) )
                {
                    if ( SecurityService.isAuthenticationEnable( ) )
                    {
                        if ( SecurityService.getInstance( ).isUserInRole( request, strRole ) )
                        {
                            agenda = a;
                        }
                    }
                }
                else
                {
                    agenda = a;
                }
            }
        }
        else
        {
            agenda = new SimpleAgenda( );
        }

        return agenda;
    }

    /**
     * Get a specified agenda from database with occurrences
     * @param strAgenda The agenda to get
     * @param request The HTTP request
     * @return An agenda object
     */
    public static Agenda getAgendaWithOccurrences( String strAgenda, HttpServletRequest request )
    {
        Agenda agenda = null;

        if ( strAgenda != null )
        {
            for ( AgendaResource agendaResource : getAgendaResourcesWithOccurrences( ) )
            {
                if ( agendaResource.getAgenda( ).getKeyName( ).equals( strAgenda ) )
                {
                    Agenda a = agendaResource.getAgenda( );

                    if ( a != null )
                    {
                        // Check security access
                        String strRole = agendaResource.getRole( );

                        if ( StringUtils.isNotBlank( strRole ) && ( request != null )
                                && ( !Constants.PROPERTY_ROLE_NONE.equals( strRole ) ) )
                        {
                            if ( SecurityService.isAuthenticationEnable( ) )
                            {
                                if ( SecurityService.getInstance( ).isUserInRole( request, strRole ) )
                                {
                                    agenda = a;
                                }
                            }
                        }
                        else
                        {
                            agenda = a;
                        }
                    }
                }
            }
        }
        else
        {
            agenda = new SimpleAgenda( );
        }

        return agenda;
    }

    /**
     * Get a specified agenda from database with occurrences
     * @param strAgenda The agenda to get
     * @param request The HTTP request
     * @return An agenda object
     */
    public static Agenda getAgendaWithOccurrencesOrderedbyId( String strAgenda, HttpServletRequest request )
    {
        Agenda agenda = null;

        if ( strAgenda != null )
        {
            for ( AgendaResource agendaResource : getAgendaResourcesWithOccurrencesIds( ) )
            {
                if ( agendaResource.getAgenda( ).getKeyName( ).equals( strAgenda ) )
                {
                    Agenda a = agendaResource.getAgenda( );

                    if ( a != null )
                    {
                        // Check security access
                        String strRole = agendaResource.getRole( );

                        if ( StringUtils.isNotBlank( strRole ) && ( request != null )
                                && ( !Constants.PROPERTY_ROLE_NONE.equals( strRole ) ) )
                        {
                            if ( SecurityService.isAuthenticationEnable( ) )
                            {
                                if ( SecurityService.getInstance( ).isUserInRole( request, strRole ) )
                                {
                                    agenda = a;
                                }
                            }
                        }
                        else
                        {
                            agenda = a;
                        }
                    }
                }
            }
        }
        else
        {
            agenda = new SimpleAgenda( );
        }

        return agenda;
    }

    /**
     * Get all agendas from database checkin the security
     * @param request The request
     * @return An agenda object
     */
    public static List<AgendaResource> getAgendaResourcesWithOccurrences( HttpServletRequest request )
    {
        Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );

        List<AgendaResource> listCalendar = CalendarHome.findAgendaResourcesList( plugin );
        List<AgendaResource> listAuthaurizedAgenda = new ArrayList<AgendaResource>( );

        for ( AgendaResource a : listCalendar )
        {
            if ( a != null )
            {
                // Check security access
                String strRole = a.getRole( );

                if ( StringUtils.isNotBlank( strRole ) && ( request != null )
                        && ( !Constants.PROPERTY_ROLE_NONE.equals( strRole ) ) )
                {
                    if ( SecurityService.isAuthenticationEnable( ) )
                    {
                        if ( SecurityService.getInstance( ).isUserInRole( request, strRole ) )
                        {
                            listAuthaurizedAgenda.add( a );
                        }
                    }
                }
                else
                {
                    listAuthaurizedAgenda.add( a );
                }
            }
        }

        for ( AgendaResource a : listAuthaurizedAgenda )
            loadAgendaOccurrences( a, plugin );

        return listAuthaurizedAgenda;
    }

    /**
     * Get the multi agenda from database
     * @return An agenda object
     */
    public static List<AgendaResource> getAgendaResourcesWithOccurrences( )
    {
        Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );

        List<AgendaResource> listCalendar = CalendarHome.findAgendaResourcesList( plugin );

        for ( AgendaResource a : listCalendar )
            loadAgendaOccurrences( a, plugin );

        return listCalendar;
    }

    /**
     * Get the multi agenda from database
     * @return An agenda object
     */
    public static List<AgendaResource> getAgendaResourcesWithEvents( )
    {
        Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );

        List<AgendaResource> listCalendar = CalendarHome.findAgendaResourcesList( plugin );

        for ( AgendaResource a : listCalendar )
            loadAgendaEvents( a, plugin );

        return listCalendar;
    }

    /**
     * Get the multi agenda from database with occurrences ordered by id
     * @return An agenda object
     */
    public static List<AgendaResource> getAgendaResourcesWithOccurrencesIds( )
    {
        Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );

        List<AgendaResource> listCalendar = CalendarHome.findAgendaResourcesList( plugin );

        for ( AgendaResource a : listCalendar )
            loadAgendaOccurrencesOrderedById( a, plugin );

        return listCalendar;
    }

    /**
     * Return the agenda
     * @param agenda The agenda
     * @param plugin The plugin
     */
    public static void loadAgendaOccurrences( AgendaResource agenda, Plugin plugin )
    {
        SimpleAgenda a = new SimpleAgenda( );

        for ( OccurrenceEvent occurrence : CalendarHome.findOccurrencesList( Integer.parseInt( agenda.getId( ) ), 1,
                plugin ) )
        {
            a.addEvent( occurrence );
        }

        a.setName( agenda.getName( ) );
        a.setKeyName( agenda.getId( ) );
        agenda.setAgenda( a );
        agenda.setResourceType( AppPropertiesService.getProperty( Constants.PROPERTY_READ_WRITE ) );
    }

    /**
     * Return the occurrences of an agenda ordered by id
     * @param agenda The agenda
     * @param plugin The plugin
     */
    public static void loadAgendaOccurrencesOrderedById( AgendaResource agenda, Plugin plugin )
    {
        SimpleAgenda a = new SimpleAgenda( );

        for ( OccurrenceEvent occurrence : CalendarHome.findOccurrencesByIdList( Integer.parseInt( agenda.getId( ) ),
                plugin ) )
        {
            a.addEvent( occurrence );
        }

        a.setName( agenda.getName( ) );
        a.setKeyName( agenda.getId( ) );
        agenda.setAgenda( a );
        agenda.setResourceType( AppPropertiesService.getProperty( Constants.PROPERTY_READ_WRITE ) );
    }

    /**
     * Return the agenda
     * @param agenda The agenda
     * @param plugin The plugin
     */
    public static void loadAgendaEvents( AgendaResource agenda, Plugin plugin )
    {
        SimpleAgenda a = new SimpleAgenda( );

        for ( SimpleEvent event : CalendarHome.findEventsList( Integer.parseInt( agenda.getId( ) ), 1, plugin ) )
        {
            a.addEvent( event );
        }
        a.setName( agenda.getName( ) );
        a.setKeyName( agenda.getId( ) );
        agenda.setAgenda( a );
        agenda.setResourceType( AppPropertiesService.getProperty( Constants.PROPERTY_READ_WRITE ) );
    }

    /**
     * Checks a date code
     * @param dateEvent The date code
     * @return True if valid otherwise false
     */
    public static boolean isValidDate( Date dateEvent )
    {
        return isValid( getDate( dateEvent ) );
    }

    /**
     * Parse a HTML string into plain text
     * @param strHTML The HTMl to parse
     * @return The Plain text describing the given HTML
     */
    public static String ParseHtmlToPlainTextString( String strHTML )
    {
        StringBuilder sbText = new StringBuilder( );

        try
        {
            Lexer lexer = new Lexer( strHTML );
            Parser parser = new Parser( lexer );
            NodeIterator i = parser.elements( );

            while ( i.hasMoreNodes( ) )
                sbText.append( i.nextNode( ).toPlainTextString( ) );
        }
        catch ( ParserException e )
        {
            AppLogService.error( "Parsing html to plain text error : " + e.getMessage( ), e );
        }

        return sbText.toString( );
    }

    /**
     * Get all calendar ids that the user is authorized to visualize
     * @param request HttpServletRequest
     * @return String array
     */
    public static String[] getCalendarIds( HttpServletRequest request )
    {
        String[] arrayCalendarIds = null;
        CalendarService calendarService = (CalendarService) SpringContextService
                .getBean( Constants.BEAN_CALENDAR_CALENDARSERVICE );

        List<AgendaResource> listCalendar = calendarService.getAgendaResources( request );
        List<String> listCalendarIds = new ArrayList<String>( );

        for ( AgendaResource a : listCalendar )
        {
            // Check security access
            String strRole = a.getRole( );

            if ( StringUtils.isNotBlank( strRole ) && ( request != null )
                    && ( !Constants.PROPERTY_ROLE_NONE.equals( strRole ) ) )
            {
                if ( SecurityService.isAuthenticationEnable( ) )
                {
                    if ( SecurityService.getInstance( ).isUserInRole( request, strRole ) )
                    {
                        listCalendarIds.add( a.getAgenda( ).getKeyName( ) );
                    }
                }
            }
            else
            {
                listCalendarIds.add( a.getAgenda( ).getKeyName( ) );
            }
        }
        if ( listCalendarIds.size( ) != 0 )
        {
            arrayCalendarIds = new String[listCalendarIds.size( )];
            for ( int i = 0; i < listCalendarIds.size( ); i++ )
            {
                arrayCalendarIds[i] = listCalendarIds.get( i );
            }
        }

        return arrayCalendarIds;
    }

    /**
     * Get the day of week of a given date using the pattern
     * {@link #DATE_PATTERN }
     * @param strDate The date to parse
     * @return The day of week of the given date, or -1 if the date could not be
     *         parsed
     */
    public static int getDayOfWeek( String strDate )
    {
        SimpleDateFormat format = new SimpleDateFormat( DATE_PATTERN );
        Date date = null;
        try
        {
            date = format.parse( strDate );
        }
        catch ( ParseException ex )
        {
            return -1;
        }
        GregorianCalendar calendar = new GregorianCalendar( );
        calendar.setTime( date );
        return calendar.get( Calendar.DAY_OF_WEEK );
    }

    /**
     * Check if a day is in the list of excluded days
     * @param nDayOfWeek the day to check
     * @param arrayExcludedDays the array of excluded days
     * @return true if it is excluded, false otherwise
     */
    public static boolean isDayExcluded( int nDayOfWeek, String[] arrayExcludedDays )
    {
        if ( arrayExcludedDays == null || arrayExcludedDays.length == 0 )
        {
            return false;
        }

        for ( int i = 0; i < arrayExcludedDays.length; i++ )
        {
            if ( StringUtils.isNotBlank( arrayExcludedDays[i] ) && StringUtils.isNumeric( arrayExcludedDays[i] ) )
            {
                int nExcludedDay = Integer.parseInt( arrayExcludedDays[i] );
                if ( nDayOfWeek == nExcludedDay )
                {
                    return true;
                }
            }
        }
        return false;
    }
}
