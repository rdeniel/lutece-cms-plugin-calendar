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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.insert.InsertServiceJspBean;
import fr.paris.lutece.portal.web.insert.InsertServiceSelectionBean;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This class provides the user interface to insert a link to a document
 *
 */
public class CalendarServiceJspBean extends InsertServiceJspBean implements InsertServiceSelectionBean
{
    private static final long serialVersionUID = 2694692453596836769L;

    ////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String REGEX_ID = "^[\\d]+$";

    // Templates
    private static final String TEMPLATE_SELECTOR_CALENDAR = "admin/plugins/calendar/calendar_selector.html";
    private static final String TEMPLATE_SELECTOR_EVENT = "admin/plugins/calendar/event_selector.html";
    private static final String TEMPLATE_LINK = "admin/plugins/calendar/calendar_link.html";

    // JSP
    private static final String JSP_SELECT_EVENT = "SelectEvent.jsp";

    // Parameters
    private static final String PARAMETER_ALT = "alt";
    private static final String PARAMETER_TARGET = "target";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_INPUT = "input";

    //markers
    private static final String MARK_ALT = "alt";
    private static final String MARK_NAME = "name";
    private static final String MARK_URL = "url";
    private static final String MARK_TARGET = "target";
    private static final String MARK_INPUT = "input";

    // private
    private AdminUser _user;
    private String _input;
    private Plugin _plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );

    /**
     * Initialize data
     *
     * @param request The HTTP request
     */
    public void init( HttpServletRequest request )
    {
        _user = AdminUserService.getAdminUser( request );
        _input = request.getParameter( PARAMETER_INPUT );
    }

    /**
    * Entry point of the insert service
    *
    * @param request The Http Request
    * @return The html form.
     */
    public String getInsertServiceSelectorUI( HttpServletRequest request )
    {
        return getSelectCalendar( request );
    }

    /**
     * Return the html form for calendar selection.
     *
     * @param request The HTTP request
     * @return The html form of the page selection page
     */
    public String getSelectCalendar( HttpServletRequest request )
    {
        init( request );

        AdminUser user = AdminUserService.getAdminUser( request );

        Collection<AgendaResource> aResource = CalendarHome.findAgendaResourcesList( _plugin );

        aResource = AdminWorkgroupService.getAuthorizedCollection( aResource, user );

        Map<String, Object> model = getDefaultModel(  );

        model.put( Constants.MARK_CALENDARS_LIST, aResource );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECTOR_CALENDAR, _user.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Select and validate the specified calendar
     *
     * @param request The http request
     * @return The url of the portlet selection page
     */
    public String doSelectCalendar( HttpServletRequest request )
    {
        init( request );

        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );

        if ( ( strCalendarId == null ) || !strCalendarId.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Agenda agenda = Utils.getAgendaWithOccurrences( strCalendarId, request );

        if ( agenda == null )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        return getSelectCalendarUrl( strCalendarId );
    }

    /**
     * Get the url of the event selection page with the specified calendar id
     *
     * @param strCalendarId Id of the calendar
     * @return The url of the event selection page
     */
    private String getSelectCalendarUrl( String strCalendarId )
    {
        UrlItem url = new UrlItem( JSP_SELECT_EVENT );
        url.addParameter( Constants.PARAMETER_CALENDAR_ID, strCalendarId );
        url.addParameter( PARAMETER_INPUT, _input );

        return url.getUrl(  );
    }

    /**
     * Return the html form for event selection.
     *
     * @param request The HTTP request
     * @return The html form of the event selection page
     */
    public String getSelectEvents( HttpServletRequest request )
    {
        init( request );

        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );

        if ( ( strCalendarId == null ) || !strCalendarId.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        Agenda agenda = Utils.getAgendaWithOccurrences( strCalendarId, request );

        Map<String, Object> model = getDefaultModel(  );

        model.put( Constants.MARK_OCCURRENCES_LIST, agenda.getEvents(  ) );
        model.put( Constants.MARK_CALENDAR_ID, strCalendarId );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECTOR_EVENT, _user.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
    * Insert the specified url into HTML content
    *
    * @param request The HTTP request
    * @return The url
    */
    public String doInsertUrl( HttpServletRequest request )
    {
        init( request );

        String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
        String strTarget = request.getParameter( PARAMETER_TARGET );
        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );
        String strAlt = request.getParameter( PARAMETER_ALT );
        String strName = request.getParameter( PARAMETER_NAME );
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        if ( ( strEventId == null ) || !strEventId.matches( REGEX_ID ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        SimpleEvent event = CalendarHome.findEvent( Integer.parseInt( strEventId ), _plugin );

        UrlItem url = new UrlItem( AppPathService.getPortalUrl(  ) );
        url.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
        url.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_SHOW_RESULT );
        url.addParameter( Constants.PARAMETER_EVENT_ID, event.getId(  ) );
        url.addParameter( Constants.PARAMETER_DOCUMENT_ID, event.getDocumentId(  ) );
        url.addParameter( Constants.PARAM_AGENDA, strCalendarId );

        model.put( MARK_URL, url.getUrl(  ) );
        model.put( MARK_TARGET, strTarget );
        model.put( MARK_ALT, strAlt );
        model.put( MARK_NAME, ( strName.length(  ) == 0 ) ? event.getTitle(  ) : strName );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_LINK, null, model );

        return insertUrl( request, _input, StringEscapeUtils.escapeJavaScript( template.getHtml(  ) ) );
    }

    /**
     * Get the default model for selection templates
     *
     * @return The default model
     */
    private Map<String, Object> getDefaultModel(  )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_INPUT, _input );

        return model;
    }
}
