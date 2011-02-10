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
package fr.paris.lutece.plugins.calendar.web.portlet;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.MissingFormatArgumentException;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.portlet.CalendarPortlet;
import fr.paris.lutece.plugins.calendar.business.portlet.CalendarPortletHome;
import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;


/**
 * This class provides the user interface to manage calendar interval portlets.
 */
public class CalendarPortletJspBean extends PortletJspBean
{
    // Prefix of the properties related to this checkbox
    public static final String PROPERTY_TIME_INTERVAL_LIST = "calendar.interval.time";

    // Prefix used to generate checkbox names
    private static final String PREFIX_CHECKBOX_NAME = "cbx_agenda_";

    // Bookmarks
    private static final String BOOKMARK_PAGE_ID = "@page_id@";
    private static final String BOOKMARK_PORTLET_ID = "@portlet_id@";

    // Parameters
    private static final String PARAMETER_PAGE_ID = "page_id";
    private static final String PARAMETER_PORTLET_ID = "portlet_id";
    private static final String PARAMETER_PORTLET_TYPE_ID = "portlet_type_id";
    private static final String PARAMETER_PERIODICITY = "periodicity";
    private static final String PARAMETER_TEXT_AGENDAS = "text_agendas";
    private static final String PARAMETER_DATE_BEGIN = "date_begin";
    private static final String PARAMETER_DATE_END = "date_end";
    private static final String PARAMETER_REPEATED_DAYS = "number_days";

    // Templates
    private static final String MARK_SELECTED_AGENDA_ID_LIST = "selected_agenda_id_list";
    private static final String MARK_AUTHORIZED_AGENDA_LIST = "authorized_agenda_list";
    private static final String MARK_FILE_AGENDAS = "text_agendas";
    private static final String MARK_INTERVAL_LIST = "interval_list";
    private static final String MARK_DATE_BEGIN = "date_begin";
    private static final String MARK_DATE_END = "date_end";
    private static final String MARK_NUMBER_DAYS = "number_days";
    private static final String MARK_BASE_URL = "base_url";
    private static final String MARK_INTERVAL_TIME_SPAN = "time_span";

    /**
     * Returns the creation form for the portlet
     *
     * @param request the HTML request
     * @return the HTML code for the page
     */
    public String getCreate( HttpServletRequest request )
    {
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strPortletTypeId = request.getParameter( PARAMETER_PORTLET_TYPE_ID );

        HtmlTemplate template = getCreateTemplate( strPageId, strPortletTypeId );

        return template.getHtml(  );
    }

    /**
     * Processes the creation of the portlet
     *
     * @param request the HTML request
     * @return the URL to redirect to
     */
    public String doCreate( HttpServletRequest request )
    {
        CalendarPortlet portlet = new CalendarPortlet(  );

        // Standard controls on the creation form
        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        int nIdPage = Integer.parseInt( strIdPage );

        String strStyleId = request.getParameter( Parameters.STYLE );

        if ( ( strStyleId == null ) || strStyleId.trim(  ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        setPortletCommonData( request, portlet );

        // mandatory field
        String strName = portlet.getName(  );

        if ( strName.trim(  ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        portlet.setPageId( nIdPage );

        // Creating portlet
        CalendarPortletHome.getInstance(  ).create( portlet );

        // Returns page with new created portlet
        return getPageUrl( portlet.getPageId(  ) );
    }

    /**
     * Returns the modification form for the portlet
     *
     * @param request the HTML request
     * @return the HTML code for the page
     */
    public String getModify( HttpServletRequest request )
    {
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        CalendarPortlet portlet = (CalendarPortlet) PortletHome.findByPrimaryKey( nPortletId );

        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        String strBaseUrl = AppPathService.getBaseUrl( request );

        HashMap model = new HashMap(  );
        model.put( BOOKMARK_PORTLET_ID, strPortletId );
        model.put( BOOKMARK_PAGE_ID, strIdPage );

        // Get the plugin for the portlet
        Plugin plugin = PluginService.getPlugin( portlet.getPluginName(  ) );
        
        String strBeginDate = Utils.getDate( CalendarPortletHome.getBeginDate( nPortletId, plugin ) );
        String strEndDate = Utils.getDate( CalendarPortletHome.getEndDate( nPortletId, plugin ) );
        int nDays = CalendarPortletHome.getRepetitionDays( nPortletId, plugin );

        // Get the list of authorized calendars depending on workgroup
        List<AgendaResource> listCalendar = CalendarHome.findAgendaResourcesList( plugin );

        List<AgendaResource> listAuthorizedAgendas = (List<AgendaResource>) AdminWorkgroupService.getAuthorizedCollection( listCalendar,
                getUser(  ) );

        //Add other agendas
        List<AgendaResource> listSelectedAgendas = CalendarPortletHome.findAgendasInPortlet( nPortletId, plugin );

        //List all non database agendas
        String strFileAgendas = "";

        for ( AgendaResource agenda : listSelectedAgendas )
        {
        	if( agenda != null )
        	{
	            try
	            {
	                Integer.parseInt( agenda.getId(  ) );
	            }
	            catch ( NumberFormatException e )
	            {
	                strFileAgendas += ( agenda.getId(  ) + "," );
	            }
        	}
        }

        if ( strFileAgendas.endsWith( "," ) )
        {
            strFileAgendas = strFileAgendas.substring( 0, strFileAgendas.length(  ) - 1 );
        }

        List<String> listSelectedAgendaId = new ArrayList<String>(  );

        for ( AgendaResource agenda : listSelectedAgendas )
        {
        	if( agenda != null )
        	{
        		listSelectedAgendaId.add( agenda.getId(  ) );
        	}
        }

        String strBooleanTimeSpan = "TRUE";

        if ( nDays <= 0 )
        {
            strBooleanTimeSpan = "FALSE";
        }

        model.put( MARK_INTERVAL_TIME_SPAN, strBooleanTimeSpan );
        model.put( MARK_AUTHORIZED_AGENDA_LIST, listAuthorizedAgendas );
        model.put( MARK_SELECTED_AGENDA_ID_LIST, listSelectedAgendaId );
        model.put( MARK_FILE_AGENDAS, strFileAgendas );
        model.put( MARK_INTERVAL_LIST, getIntervalList( request ) );
        model.put( MARK_DATE_BEGIN, strBeginDate );
        model.put( MARK_DATE_END, strEndDate );
        model.put( MARK_NUMBER_DAYS, nDays );
        model.put( MARK_BASE_URL, strBaseUrl );
        model.put( Constants.MARK_LOCALE, getLocale(  ).getLanguage(  ) );

        // Fill the specific part of the modify form
        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml(  );
    }

    /**
     * Processes the modification of the portlet
     *
     * @param request the HTTP request
     * @return the URL to redirect to
     */
    public String doModify( HttpServletRequest request )
    {
        // Use the id in the request to load the portlet
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = Integer.parseInt( strPortletId );
        CalendarPortlet portlet = (CalendarPortlet) PortletHome.findByPrimaryKey( nPortletId );

        // Standard controls on the creation form
        String strStyleId = request.getParameter( Parameters.STYLE );

        if ( ( strStyleId == null ) || strStyleId.trim(  ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        setPortletCommonData( request, portlet );

        // mandatory field
        String strName = portlet.getName(  );

        if ( strName.trim(  ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        portlet.update(  );

        String strDateBegin = request.getParameter( PARAMETER_DATE_BEGIN );
        String strDateEnd = request.getParameter( PARAMETER_DATE_END );
        String strDays = request.getParameter( PARAMETER_REPEATED_DAYS );

        int nDays = Integer.parseInt( strDays );
        boolean bIntervalPeriodicity = false;

        if ( Integer.parseInt( request.getParameter( PARAMETER_PERIODICITY ) ) == 1 )
        {
            bIntervalPeriodicity = true;

            try
            {
                //put the date in form yyyMMdd
                strDateBegin = String.format( "%3$2s%2$2s%1$2s", (Object[]) strDateBegin.split( "/" ) );
                strDateEnd = String.format( "%3$2s%2$2s%1$2s", (Object[]) strDateEnd.split( "/" ) );
            }
            catch ( MissingFormatArgumentException e )
            {
                return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                    AdminMessage.TYPE_STOP );
            }

            if ( !Utils.isValid( strDateBegin ) || !Utils.isValid( strDateEnd ) )
            {
                return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                    AdminMessage.TYPE_STOP );
            }
        }

        modifyCalendar( request, nPortletId, strDateBegin, strDateEnd, nDays, bIntervalPeriodicity );

        return getPageUrl( portlet.getPageId(  ) );
    }

    /**
     * Return the list of time intervals declared in properties file
     * @return A ReferenceList of time interval
     * @param request The HttpRequest
     */
    public static ReferenceList getIntervalList( HttpServletRequest request )
    {
        StringTokenizer st = new StringTokenizer( AppPropertiesService.getProperty( PROPERTY_TIME_INTERVAL_LIST ), "," );
        ReferenceList timeIntervalList = new ReferenceList(  );

        while ( st.hasMoreElements(  ) )
        {
            String strIntervalName = st.nextToken(  ).trim(  );
            String strDescription = I18nService.getLocalizedString( "calendar.interval." + strIntervalName +
                    ".description", request.getLocale(  ) );
            int nDays = AppPropertiesService.getPropertyInt( "calendar.interval." + strIntervalName + ".value", 7 );
            timeIntervalList.addItem( nDays, strDescription );
        }

        return timeIntervalList;
    }

    /**
     * Helper method to determine which database agenda were chosen for the portlet
     * in the modification form, and update the database accordingly.
     * @param nPortletId The id of the portlet
     * @param strDateBegin The start date
     * @param strDateEnd The end date
     * @param nDays The number of days calendar will be repeated
     * @param bIntervalPeriodicity A boolean to to determine which periodicity is used
     * @param request the HTTP request
     */
    private static void modifyCalendar( HttpServletRequest request, int nPortletId, String strDateBegin,
        String strDateEnd, int nDays, boolean bIntervalPeriodicity )
    {
        List<String> listChosenAgendas = new ArrayList<String>(  );

        Enumeration enumParameterNames = request.getParameterNames(  );

        while ( enumParameterNames.hasMoreElements(  ) )
        {
            String strParameterName = (String) enumParameterNames.nextElement(  );

            if ( strParameterName.startsWith( PREFIX_CHECKBOX_NAME ) )
            {
                String strAgendaId = strParameterName.substring( PREFIX_CHECKBOX_NAME.length(  ) );
                listChosenAgendas.add( strAgendaId );
            }
        }

        String strTextAgendas = request.getParameter( PARAMETER_TEXT_AGENDAS );

        if ( ( strTextAgendas != null ) && !strTextAgendas.equals( "" ) )
        {
            StringTokenizer st = new StringTokenizer( strTextAgendas, "," );

            while ( st.hasMoreElements(  ) )
            {
                String strAgenda = st.nextToken(  ).trim(  );
                listChosenAgendas.add( strAgenda );
            }
        }

        //Delete all agendas in portlet
        CalendarPortletHome.removeAllAgendas( nPortletId );

        // Get the plugin for the portlet
        Plugin plugin = PluginService.getPlugin( Constants.PLUGIN_NAME);
        // Add the chosen agendas
        for ( String strAgendaId : listChosenAgendas )
        {
            if ( bIntervalPeriodicity )
            {
                CalendarPortletHome.insertAgendaInterval( nPortletId, strAgendaId, strDateBegin, strDateEnd, plugin );
            }
            else
            {
                CalendarPortletHome.insertCalendar( nPortletId, strAgendaId, nDays, plugin );
            }
        }
    }
}
