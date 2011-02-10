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
package fr.paris.lutece.plugins.calendar.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.plugins.calendar.business.CalendarFilter;
import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.business.category.Category;
import fr.paris.lutece.plugins.calendar.service.search.CalendarSearchService;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 * This class provides utils features to manipulate and convert XML calendar information
 */
public final class XMLUtils
{
    // The names of the XML tags    
    private static final String TAG_EVENTS = "events";
    private static final String TAG_AGENDA_EVENT = "event";
    private static final String TAG_AGENDA_EVENT_DATE = "event-date";
    private static final String TAG_AGENDA_EVENT_LOCATION = "event-location";
    private static final String TAG_AGENDA_EVENT_DESCRIPTION = "event-description";
    private static final String TAG_AGENDA_EVENT_DATE_TIME_START = "event-date-time-start";
    private static final String TAG_AGENDA_EVENT_DATE_TIME_END = "event-date-time-end";
    private static final String TAG_AGENDA_EVENT_CATEGORIES = "event-categories";
    private static final String TAG_AGENDA_EVENT_STATUS = "event-status";
    private static final String TAG_AGENDA_EVENT_SUMMARY = "event-summary";
    private static final String TAG_AGENDA_EVENT_URL = "event-url";
    private static final String TAG_AGENDA_EVENT_DATE_CREATION = "event-creation-date";

    /* Since version 3.0.0 */
    private static final String TAG_AGENDA_MONTH = "month";
    private static final String TAG_AGENDA_MONTH_LABEL = "month-label";
    private static final String TAG_AGENDA_NEXT_MONTH_LABEL = "month-next-label";
    private static final String TAG_AGENDA_ABOVE_MONTH_LABEL = "month-above-label";
    private static final String TAG_AGENDA_WEEKS = "weeks";
    private static final String TAG_AGENDA_WEEK = "week";
    private static final String TAG_AGENDA_DAY = "day";
    private static final String TAG_AGENDA_DAY_CODE = "day-code";
    private static final String TAG_AGENDA_DAY_LABEL = "day-label";
    private static final String TAG_WEEK_SHORTCUTS = "week-shortcuts";
    private static final String TAG_WEEK_SHORTCUT = "week-shortcut";
    private static final String TAG_WEEK_SHORTCUT_LABEL = "week-shortcut-label";
    private static final String TAG_WEEK_SHORTCUT_PERIOD = "week-shortcut-period";
    private static final String TAG_WEEK_SHORTCUT_DATE_START = "week-shortcut-date-start";
    private static final String TAG_WEEK_SHORTCUT_DATE_END = "week-shortcut-date-end";

    //PROPERTIES
    private static final String PROPERTY_SPACE = " ";
    private static final String PROPERTY_COMMA = ",";
    private static final String PROPERTY_CALENDAR_RSS_PATTERN = "calendar.rss.format.date";
    private static final String PROPERTY_EMPTY_DAY = "&nbsp;";

    //Html components
    private static final String BEGIN_TD_TAG = "<td class=";
    private static final String END_TD_TAG = "</td>";
    private static final String BEGIN_A_TAG = "<a href=\"";
    private static final String END_A_TAG = "</a>";
    private static final String BEGIN_BOLD_TAG = "<b>";
    private static final String END_BOLD_TAG = "</b>";

    // CSS Styles
    public static final String STYLE_CLASS_VIEW_MONTH_DAY = "calendar-view-month-day";
    public static final String STYLE_CLASS_VIEW_WEEK_DAY = "calendar-view-week-day";
    public static final String STYLE_CLASS_SMALLMONTH_DAY = "calendar-smallmonth-day";
    public static final String STYLE_CLASS_SUFFIX_OLD = "-old";
    public static final String STYLE_CLASS_SUFFIX_TODAY = "-today";
    public static final String STYLE_CLASS_SUFFIX_OFF = "-off";
    public static final String STYLE_CLASS_EMPTY_DAY = "calendar-smallmonth-day";

    //LOGGER    
    private static final String LOGGER_CALENDAR_EXPORT_XML_CONTENT = "lutece.debug.calendar.export.xmlContent";

    /**
     * Returns the Xml code of a calendar events
     *
     * @param strAgenda The id of the agenda
     * @param request request servlet request
     * @return the Xml code of the agenda content
     */
    public static String getAgendaXml( String strAgenda, HttpServletRequest request )
    {
        Agenda agenda = Utils.getAgendaWithOccurrences( strAgenda, request );
        List<SimpleEvent> listEvents = agenda.getEvents(  );

        return getXml( listEvents );
    }

    /**
     * Returns the Xml code for RSS
     *
     * @param strAgenda The id of the agenda
     * @param request request servlet request
     * @return the Xml code of the agenda content
     */
    public static String getRssXml( CalendarFilter filter )
    {
        List<SimpleEvent> listEvents = CalendarHome.findEventsByFilter( filter,
                PluginService.getPlugin( Constants.PLUGIN_NAME ) );

        return getXml( listEvents );
    }

    /**
     * Returns the Xml code of the Calendar portlet without XML heading
     *
     * @param strAgenda The id of the agenda
     * @param request request servlet request
     * @return the Xml code of the agenda content
     */
    public static String getXml( List<SimpleEvent> listEvents )
    {
        if ( listEvents != null )
        {
            StringBuffer strXml = new StringBuffer(  );
            strXml.append( XmlUtil.getXmlHeader(  ) );

            // Generate the XML code for the agendas :
            XmlUtil.beginElement( strXml, TAG_EVENTS );

            Iterator i = listEvents.iterator(  );

            while ( i.hasNext(  ) )
            {
                SimpleEvent event = (SimpleEvent) i.next(  );

                XmlUtil.beginElement( strXml, TAG_AGENDA_EVENT );
                XmlUtil.addElement( strXml, TAG_AGENDA_EVENT_SUMMARY,
                    ( event.getTitle(  ) != null ) ? event.getTitle(  ) : "" );

                String strAddress = event.getLocationAddress(  );
                String strTown = event.getLocationTown(  );
                String strZip = event.getLocationZip(  );

                String strLocation = event.getLocation(  );

                if ( strAddress != null )
                {
                    strLocation += ( PROPERTY_SPACE + strAddress );
                }

                if ( strZip != null )
                {
                    strLocation += ( PROPERTY_SPACE + strZip );
                }

                if ( strTown != null )
                {
                    strLocation += ( PROPERTY_SPACE + strTown );
                }

                UrlItem urlEvent = new UrlItem( AppPathService.getPortalUrl(  ) );
                urlEvent.addParameter( XPageAppService.PARAM_XPAGE_APP, CalendarPlugin.PLUGIN_NAME );
                urlEvent.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_SHOW_RESULT );
                urlEvent.addParameter( Constants.PARAMETER_EVENT_ID, event.getId(  ) );
                urlEvent.addParameter( Constants.PARAM_AGENDA, event.getIdCalendar(  ) );

                String strCreationdate = Constants.EMPTY_STRING;

                if ( event.getDateCreation(  ) != null )
                {
                    SimpleDateFormat sdf = new SimpleDateFormat( AppPropertiesService.getProperty( 
                                PROPERTY_CALENDAR_RSS_PATTERN ), Locale.US );
                    strCreationdate = sdf.format( event.getDateCreation(  ) );
                }

                XmlUtil.addElement( strXml, TAG_AGENDA_EVENT_LOCATION, ( strLocation != null ) ? strLocation : "" );
                XmlUtil.addElement( strXml, TAG_AGENDA_EVENT_DESCRIPTION,
                    ( event.getDescription(  ) != null ) ? event.getDescription(  ) : "" );

                if ( event.getListCategories(  ) != null )
                {
                    Collection<Category> _listCategories = event.getListCategories(  );
                    String strCategories = PROPERTY_SPACE;

                    for ( Category category : _listCategories )
                    {
                        if ( strCategories.equals( PROPERTY_SPACE ) )
                        {
                            strCategories = category.getName(  );
                        }
                        else
                        {
                            strCategories += ( strCategories + PROPERTY_COMMA + category.getName(  ) );
                        }
                    }

                    XmlUtil.addElement( strXml, TAG_AGENDA_EVENT_CATEGORIES, strCategories.trim(  ) );
                }

                XmlUtil.addElement( strXml, TAG_AGENDA_EVENT_STATUS,
                    ( event.getStatus(  ) != null ) ? event.getStatus(  ) : "" );
                XmlUtil.addElement( strXml, TAG_AGENDA_EVENT_DATE_TIME_START,
                    ( event.getDateTimeStart(  ) != null ) ? event.getDateTimeStart(  ).replace( ":", "" ) : "" );
                XmlUtil.addElement( strXml, TAG_AGENDA_EVENT_DATE_TIME_END,
                    ( event.getDateTimeEnd(  ) != null ) ? event.getDateTimeEnd(  ).replace( ":", "" ) : "" );
                XmlUtil.addElement( strXml, TAG_AGENDA_EVENT_DATE, Utils.getDate( event.getDate(  ) ) );
                XmlUtil.addElementHtml( strXml, TAG_AGENDA_EVENT_URL, urlEvent.getUrl(  ) );
                XmlUtil.addElement( strXml, TAG_AGENDA_EVENT_DATE_CREATION, strCreationdate );

                XmlUtil.endElement( strXml, TAG_AGENDA_EVENT );
            }

            XmlUtil.endElement( strXml, TAG_EVENTS );

            if ( AppLogService.isDebugEnabled( LOGGER_CALENDAR_EXPORT_XML_CONTENT ) )
            {
                AppLogService.debug( LOGGER_CALENDAR_EXPORT_XML_CONTENT, strXml.toString(  ) );
            }

            return strXml.toString(  );
        }
        else
        {
            return Constants.EMPTY_STRING;
        }
    }

    /**
     * This method performs XSL Transformation.
     *
     * @param source The input XML document
     * @param stylesheet The XSL stylesheet
     * @param params parameters to apply to the XSL Stylesheet
     * @param outputProperties properties to use for the xsl transform. Will overload the xsl output definition.
     * @return The output document transformed
     * @throws Exception The exception
     */
    public static byte[] transformXMLToXSL( String strXml, InputStream baSource, Map<String, String> params,
        Properties outputProperties ) throws Exception
    {
        Source stylesheet = new StreamSource( baSource );
        StringReader srInputXml = new StringReader( strXml );
        StreamSource source = new StreamSource( srInputXml );

        try
        {
            TransformerFactory factory = TransformerFactory.newInstance(  );
            Transformer transformer = factory.newTransformer( stylesheet );

            if ( outputProperties != null )
            {
                transformer.setOutputProperties( outputProperties );
            }

            if ( params != null )
            {
                transformer.clearParameters(  );

                Iterator i = params.keySet(  ).iterator(  );

                while ( i.hasNext(  ) )
                {
                    String name = (String) i.next(  );
                    String value = (String) params.get( name );
                    transformer.setParameter( name, value );
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream(  );
            Result result = new StreamResult( out );
            transformer.transform( source, result );

            return out.toByteArray(  );
        }
        catch ( TransformerConfigurationException e )
        {
            String strMessage = e.getMessage(  );

            if ( e.getLocationAsString(  ) != null )
            {
                strMessage += ( "- location : " + e.getLocationAsString(  ) );
            }

            throw new Exception( "Error transforming document XSLT : " + strMessage, e.getCause(  ) );
        }
        catch ( TransformerFactoryConfigurationError e )
        {
            throw new Exception( "Error transforming document XSLT : " + e.getMessage(  ), e );
        }
        catch ( TransformerException e )
        {
            String strMessage = e.getMessage(  );

            if ( e.getLocationAsString(  ) != null )
            {
                strMessage += ( "- location : " + e.getLocationAsString(  ) );
            }

            throw new Exception( "Error transforming document XSLT : " + strMessage, e.getCause(  ) );
        }
        catch ( Exception e )
        {
            throw new Exception( "Error transforming document XSLT : " + e.getMessage(  ), e );
        }
    }

    public static String getXMLPortletCalendar( Locale local, Calendar cal, HttpServletRequest request )
    {
        StringBuffer strXml = new StringBuffer(  );

        String strDay = null;
        Calendar calendar = new GregorianCalendar(  );

        //Set the calendar in the beginning of the month
        calendar.set( cal.get( Calendar.YEAR ), cal.get( Calendar.MONTH ), 1 );

        int nDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );

        //If day of week is sunday: nDayOfWeek = 8
        if ( nDayOfWeek == 1 )
        {
            nDayOfWeek = 8;
        }

        Calendar calendar2 = new GregorianCalendar(  );
        calendar2.set( cal.get( Calendar.YEAR ), cal.get( Calendar.MONTH ), calendar.getMaximum( Calendar.DAY_OF_MONTH ) );

        //Beginning of the main xml block: month
        XmlUtil.beginElement( strXml, TAG_AGENDA_MONTH );

        String strBaseUrl = AppPathService.getPortalUrl(  );

        UrlItem urlMonth = new UrlItem( strBaseUrl );
        urlMonth.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
        urlMonth.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_DO_SEARCH );

        urlMonth.addParameter( Constants.PARAMETER_DATE_START, DateUtil.getDateString( calendar.getTime(  ), local ) );
        urlMonth.addParameter( Constants.PARAMETER_DATE_END, DateUtil.getDateString( calendar2.getTime(  ), local ) );
        urlMonth.addParameter( Constants.PARAMETER_PERIOD, Constants.PROPERTY_PERIOD_RANGE );

        String strMonthLabel = Utils.getMonthLabel( Utils.getDate( calendar ), local );

        String strUrlMonth = BEGIN_A_TAG + urlMonth.getUrl(  ) + "\">" + strMonthLabel + END_A_TAG;

        XmlUtil.addElementHtml( strXml, TAG_AGENDA_MONTH_LABEL, strUrlMonth );

        //Shortcut tags
        //Begenning of the xml block: week-shortcuts
        XmlUtil.beginElement( strXml, TAG_WEEK_SHORTCUTS );

        //Today shortcut 
        XmlUtil.beginElement( strXml, TAG_WEEK_SHORTCUT );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_LABEL,
            I18nService.getLocalizedString( Constants.PROPERTY_SHORTCUT_TODAY, local ) );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_PERIOD, Constants.PROPERTY_PERIOD_TODAY );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_DATE_START, DateUtil.getDateString( new Date(  ), local ) );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_DATE_END, DateUtil.getDateString( new Date(  ), local ) );
        XmlUtil.endElement( strXml, TAG_WEEK_SHORTCUT );

        //Tomorrow shortcut 
        Calendar calTomorrow = new GregorianCalendar(  );
        calTomorrow.add( Calendar.DATE, 1 );
        XmlUtil.beginElement( strXml, TAG_WEEK_SHORTCUT );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_LABEL,
            I18nService.getLocalizedString( Constants.PROPERTY_SHORTCUT_TOMORROW, local ) );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_PERIOD, Constants.PROPERTY_PERIOD_RANGE );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_DATE_START,
            DateUtil.getDateString( calTomorrow.getTime(  ), local ) );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_DATE_END, DateUtil.getDateString( calTomorrow.getTime(  ), local ) );
        XmlUtil.endElement( strXml, TAG_WEEK_SHORTCUT );

        //Week shortcut
        Date dateBeginWeek = null;
        Date dateEndWeek = null;

        Calendar calendarToday = new GregorianCalendar(  );
        Calendar calendarFirstDay = new GregorianCalendar(  );
        Calendar calendarLastDay = new GregorianCalendar(  );

        calendarFirstDay = calendarToday;
        calendarFirstDay.add( Calendar.DATE, Calendar.MONDAY - nDayOfWeek );
        calendarLastDay = (GregorianCalendar) calendarFirstDay.clone(  );
        calendarLastDay.add( Calendar.DATE, 6 );
        dateBeginWeek = calendarFirstDay.getTime(  );
        dateEndWeek = calendarLastDay.getTime(  );

        XmlUtil.beginElement( strXml, TAG_WEEK_SHORTCUT );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_LABEL,
            I18nService.getLocalizedString( Constants.PROPERTY_SHORTCUT_WEEK, local ) );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_PERIOD, Constants.PROPERTY_PERIOD_WEEK );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_DATE_START, DateUtil.getDateString( dateBeginWeek, local ) );
        XmlUtil.addElement( strXml, TAG_WEEK_SHORTCUT_DATE_END, DateUtil.getDateString( dateEndWeek, local ) );
        XmlUtil.endElement( strXml, TAG_WEEK_SHORTCUT );

        //Ending of the xml block: week-shortcuts
        XmlUtil.endElement( strXml, TAG_WEEK_SHORTCUTS );

        //Begenning of the xml block: weeks
        XmlUtil.beginElement( strXml, TAG_AGENDA_WEEKS );

        //Day label tags
        XmlUtil.addElement( strXml, TAG_AGENDA_DAY_LABEL,
            I18nService.getLocalizedString( Constants.PROPERTY_SHORTLABEL_MONDAY, local ) );
        XmlUtil.addElement( strXml, TAG_AGENDA_DAY_LABEL,
            I18nService.getLocalizedString( Constants.PROPERTY_SHORTLABEL_TUESDAY, local ) );
        XmlUtil.addElement( strXml, TAG_AGENDA_DAY_LABEL,
            I18nService.getLocalizedString( Constants.PROPERTY_SHORTLABEL_WEDNESDAY, local ) );
        XmlUtil.addElement( strXml, TAG_AGENDA_DAY_LABEL,
            I18nService.getLocalizedString( Constants.PROPERTY_SHORTLABEL_THURSDAY, local ) );
        XmlUtil.addElement( strXml, TAG_AGENDA_DAY_LABEL,
            I18nService.getLocalizedString( Constants.PROPERTY_SHORTLABEL_FRIDAY, local ) );
        XmlUtil.addElement( strXml, TAG_AGENDA_DAY_LABEL,
            I18nService.getLocalizedString( Constants.PROPERTY_SHORTLABEL_SATURDAY, local ) );
        XmlUtil.addElement( strXml, TAG_AGENDA_DAY_LABEL,
            I18nService.getLocalizedString( Constants.PROPERTY_SHORTLABEL_SUNDAY, local ) );

        //Check if the month is ended
        boolean bDone = false;

        //check if the month is started
        boolean bStarted = false;

        //While the month isn't over...
        while ( !bDone )
        {
            //Begenning of the xml block: week
            XmlUtil.beginElement( strXml, TAG_AGENDA_WEEK );

            for ( int i = 0; i < 7; i++ )
            {
                if ( ( ( ( i + 2 ) != nDayOfWeek ) && !bStarted ) || bDone )
                {
                    XmlUtil.beginElement( strXml, TAG_AGENDA_DAY );
                    strDay = BEGIN_TD_TAG + getDayClass( calendar ) + ">" + PROPERTY_EMPTY_DAY + END_TD_TAG;
                    XmlUtil.addElementHtml( strXml, TAG_AGENDA_DAY_CODE, strDay );
                    XmlUtil.endElement( strXml, TAG_AGENDA_DAY );

                    continue;
                }
                else
                {
                    bStarted = true;
                }

                //put parameters in the url
                UrlItem urlDay = new UrlItem( strBaseUrl );
                urlDay.addParameter( Constants.PARAMETER_DATE, 
                		DateUtil.getDateString( calendar.getTime(  ), local ) );

                //construct on url based on day
                String strUrlDay = new String(  );
                strUrlDay = BEGIN_A_TAG + urlDay.getUrl(  ) + "\">" +
                    Integer.toString( calendar.get( Calendar.DAY_OF_MONTH ) ) + END_A_TAG;

                XmlUtil.beginElement( strXml, TAG_AGENDA_DAY );

                Date date = Utils.getDate( Utils.getDate( calendar ) );
                Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
                
                String[] arrayCalendarIds = Utils.getCalendarIds( request );
                
                List<Event> listEvent = CalendarSearchService.getInstance(  ).
                	getSearchResults( arrayCalendarIds, null, "", date, date, LocalVariables.getRequest(  ), plugin ) ;
                if( listEvent.size(  ) != 0 )
                {
                    strDay = BEGIN_TD_TAG + Constants.STYLE_CLASS_SMALLMONTH_DAY + Constants.STYLE_CLASS_SUFFIX_EVENT +
                        ">" + BEGIN_BOLD_TAG + strUrlDay + END_BOLD_TAG + END_TD_TAG;
                }
                else
                {
                    strDay = BEGIN_TD_TAG + getDayClass( calendar ) + ">" +
                        Integer.toString( calendar.get( Calendar.DAY_OF_MONTH ) ) + END_TD_TAG;
                }

                XmlUtil.addElementHtml( strXml, TAG_AGENDA_DAY_CODE, strDay );

                XmlUtil.endElement( strXml, TAG_AGENDA_DAY );

                int nDay = calendar.get( Calendar.DAY_OF_MONTH );
                calendar.roll( Calendar.DAY_OF_MONTH, true );

                int nNewDay = calendar.get( Calendar.DAY_OF_MONTH );

                if ( nNewDay < nDay )
                {
                    bDone = true;
                }
            }

            //Ending of the xml block: week
            XmlUtil.endElement( strXml, TAG_AGENDA_WEEK );
        }

        //Ending of the xml block: weeks
        XmlUtil.endElement( strXml, TAG_AGENDA_WEEKS );

        //Ending of the xml block: month
        XmlUtil.endElement( strXml, TAG_AGENDA_MONTH );

        return strXml.toString(  );
    }

    /**
     * Calculate the style class to render the day
     * @param calendar A calendar object positionned on the day to render
     * @return A CSS style
     */
    private static String getDayClass( Calendar calendar )
    {
        String strClass = Constants.STYLE_CLASS_SMALLMONTH_DAY;
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
}
