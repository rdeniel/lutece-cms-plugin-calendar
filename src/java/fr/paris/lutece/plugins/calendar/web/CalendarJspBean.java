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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;

import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.CalendarSubscriber;
import fr.paris.lutece.plugins.calendar.business.CalendarSubscriberHome;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.business.OccurrenceEvent;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.business.category.Category;
import fr.paris.lutece.plugins.calendar.business.category.CategoryHome;
import fr.paris.lutece.plugins.calendar.business.parameter.CalendarParameterHome;
import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.AgendaService;
import fr.paris.lutece.plugins.calendar.service.AgendaSubscriberService;
import fr.paris.lutece.plugins.calendar.service.CalendarResourceIdService;
import fr.paris.lutece.plugins.calendar.service.EventImageResourceService;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.plugins.calendar.service.search.CalendarIndexer;
import fr.paris.lutece.plugins.calendar.utils.CalendarIndexerUtils;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.filesystem.DirectoryNotFoundException;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.sort.AttributeComparator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This class provides the user interface to manage calendars from the dataBase features ( manage, create, modify, remove)
 */
public class CalendarJspBean extends PluginAdminPageJspBean
{
    ////////////////////////////////////////////////////////////////////////////
    // Constants
    // Prefix of the properties related to this checkbox
    public static final String PROPERTY_TIME_INTERVAL_LIST = "calendar.interval.time";
    public static final String PROPERTY_TOP_EVENT_LIST = "calendar.topevent.values";
    public static final String PROPERTY_EVENT_STATUS_LIST = "calendar.event.status.list";
    public static final String PROPERTY_EVENT_STATUS_VALUES = "calendar.event.status.values";
    public static final String PROPERTY_TOP_EVENT_DEFAULT = "calendar.topevent.default";
    public static final String PROPERTY_EMAIL_NOTIFY = "calendar.email.notify";
    public static final String PROPERTY_TAGS_REGEXP = "[0-9\\p{L}\\p{M}\\s\\-\\_]*";
    private static final String PROPERTY_LIMIT_MAX_SUSCRIBER = "calendar.limit.max";
    private static final String PROPERTY_LIMIT_MIN_SUSCRIBER = "calendar.limit.min";

    // Right
    public static final String RIGHT_MANAGE_CALENDAR = "CALENDAR_MANAGEMENT";

    // Session attribute
    public static final String ATTRIBUTE_MODULE_DOCUMENT_TO_CALENDAR_CONTENT_FILE = "lutece_document_calendar_content_file";
    public static final String ATTRIBUTE_MODULE_DOCUMENT_TO_CALENDAR_MIME_TYPE_FILE = "lutece_document_calendar_mime_type_file";
    
    //Templates
    private static final String TEMPLATE_MANAGE_CALENDARS = "admin/plugins/calendar/manage_calendars.html";
    private static final String TEMPLATE_MANAGE_ADVANCED_PARAMETERS = "admin/plugins/calendar/manage_advanced_parameters.html";
    private static final String TEMPLATE_CREATE_CALENDAR = "admin/plugins/calendar/create_calendar.html";
    private static final String TEMPLATE_MODIFY_CALENDAR = "admin/plugins/calendar/modify_calendar.html";
    private static final String TEMPLATE_CREATE_EVENT = "admin/plugins/calendar/create_event.html";
    private static final String TEMPLATE_MODIFY_EVENT = "admin/plugins/calendar/modify_event.html";
    private static final String TEMPLATE_MODIFY_OCCURRENCE = "admin/plugins/calendar/modify_occurrence.html";
    private static final String TEMPLATE_MANAGE_SUBSCRIBERS = "admin/plugins/calendar/manage_subscribers.html";
    private static final String TEMPLATE_EVENT_LIST = "admin/plugins/calendar/event_list.html";
    private static final String TEMPLATE_OCCURRENCE_LIST = "admin/plugins/calendar/occurrence_list.html";
    private static final String MARK_INTERVAL_LIST = "interval_list";
    private static final String MARK_NUMBER_DAYS = "number_days";
    private static final String MARK_INTERVAL_TIME_SPAN = "time_span";
    private static final String MARK_TOP_EVENT_LIST = "top_event_list";
    private static final String MARK_TOP_EVENT_DEFAULT = "top_event_default";
    private static final String MARK_EVENT_STATUS_LIST = "event_status_list";
    private static final String MARK_DEFAULT_STATUS = "default_status";
    private static final String MARK_EMAIL_NOTIFY = "notify";
    private static final String MARK_INSERT_SERVICE_PAGE = "insert_service_page";
    private static final String MARK_INSERT_SERVICE_LINK_PAGE = "insert_service_link_page";
    private static final String MARK_WEBAPP_URL = "webapp_url";

    // Jsp Definition
    private static final String JSP_DO_REMOVE_CALENDAR = "jsp/admin/plugins/calendar/DoRemoveCalendar.jsp";
    private static final String JSP_DO_REMOVE_EVENT = "jsp/admin/plugins/calendar/DoRemoveEvent.jsp";
    private static final String JSP_DO_REMOVE_OCCURRENCE = "jsp/admin/plugins/calendar/DoRemoveOccurrence.jsp";
    private static final String JSP_DO_MODIFY_EVENT = "jsp/admin/plugins/calendar/DoModifyEvent.jsp";
    private static final String DO_MODIFY_EVENT = "DoModifyEvent.jsp?plugin_name=calendar&calendar_id=";
    private static final String JSP_MODIFY_CALENDAR = "jsp/admin/plugins/calendar/ModifyCalendar.jsp?plugin_name=calendar&calendar_id=";
    private static final String JSP_EVENT_LIST = "EventList.jsp?plugin_name=calendar&calendar_id=";
    private static final String JSP_EVENT_LIST2 = "jsp/admin/plugins/calendar/EventList.jsp?plugin_name=calendar&calendar_id=";
    private static final String JSP_OCCURRENCE_LIST = "jsp/admin/plugins/calendar/OccurrenceList.jsp?plugin_name=calendar&calendar_id=";
    private static final String JSP_OCCURRENCE_LIST2 = "OccurrenceList.jsp?plugin_name=calendar&calendar_id=";
    private static final String JSP_URL_DO_REMOVE_SUBSCRIBER = "jsp/admin/plugins/calendar/DoUnsubscribeCalendarAdmin.jsp";
    private static final String JSP_MANAGE_SUBSCRIBERS_LIST = "ManageSubscribers.jsp?calendar_id=";
    private static final String JSP_GET_DOCUMENT_INSERT_SERVICE = "modules/document/SelectDocument.jsp";
    private static final String JSP_GET_DOCUMENT_INSERT_LINK_SERVICE = "jsp/admin/insert/GetAvailableInsertServices.jsp";
    private static final String JSP_GET_INSERT_SERVICE = "jsp/admin/plugins/calendar/GetInsertService.jsp";
    private static final String JSP_GET_INSERT_LINK_SERVICE = "jsp/admin/plugins/calendar/GetInsertLinkService.jsp";
    private static final String JSP_MANAGE_CALENDAR = "jsp/admin/plugins/calendar/ManageCalendars.jsp";
    private static final String JSP_DO_MODIFY_OCCURRENCE_STATUS = "jsp/admin/plugins/calendar/DoModifyOccurrenceStatus.jsp";
    private static final String JSP_URL_MANAGE_ADVANCED_PARAMETERS = "jsp/admin/plugins/calendar/ManageAdvancedParameters.jsp?plugin_name=calendar";

    // Message keys
    private static final String MESSAGE_CONFIRM_REMOVE_CALENDAR = "calendar.message.confirmRemoveCalendar";
    private static final String MESSAGE_CONFIRM_REMOVE_EVENT = "calendar.message.confirmRemoveEvent";
    private static final String MESSAGE_CONFIRM_REMOVE_OCCURRENCE = "calendar.message.confirmRemoveOccurrence";
    private static final String MESSAGE_CONFIRM_REMOVE_ALL_OCCURRENCE = "calendar.message.confirmRemoveAllOccurrence";
    private static final String MESSAGE_CONFIRM_MODIFY_EVENT = "calendar.message.modifyEvent";
    private static final String EXT_IMAGE_FILES = ".png";
    private static final String MESSAGE_INVALID_OCCURRENCE_NUMBER = "calendar.message.invalidOccurrenceNumber";
    private static final String MESSAGE_INVALID_TAG = "calendar.message.invalidTagsInput";
    private static final String MESSAGE_CONFIRM_REMOVE_SUBSCRIBER = "calendar.message.confirmRemoveSubscriber";
    private static final String MESSAGE_PLUGIN_DOCUMENT_UNINSTALLED = "calendar.message.plugin-document.uninstalled";
    private static final String MESSAGE_CONFIRM_MODIFY_OCCURRENCES_STATUS = "calendar.message.confirmModifyOccurrenceStatus";
    private static final String MESSAGE_INVALID_NUMBER_FORMAT = "calendar.message.invalidNumBerFormat";
    
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private int _nDefaultItemsPerPage;
    private FileItem _EventItem;
    private String[] _EventCategories;
    private String _EventDescription;
    private HashMap<String, Object> _mapParameters;

    /**
     * This class is used to handle back office management of database calendars.
     */
    public CalendarJspBean(  )
    {
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( Constants.PROPERTY_EVENTS_PER_PAGE, 5 );
    }

    /**
     * Returns calendars management form
     *
     * @param request The Http request
     * @return Html form
     */
    public String getManageCalendars( HttpServletRequest request )
    {
        setPageTitleProperty( Constants.PROPERTY_PAGE_TITLE_MANAGE_CALENDARS );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        Map<String, Object> model = new HashMap<String, Object>(  );
        List<AgendaResource> listCalendar = CalendarHome.findAgendaResourcesList( getPlugin(  ) );
        listCalendar = (List) AdminWorkgroupService.getAuthorizedCollection( listCalendar, getUser(  ) );

        Paginator paginator = new Paginator( listCalendar, _nItemsPerPage, getHomeUrl( request ),
                Constants.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        
        boolean bPermissionAdvancedParameter = RBACService.isAuthorized( CalendarResourceIdService.RESOURCE_TYPE, 
        		RBAC.WILDCARD_RESOURCES_ID,	CalendarResourceIdService.PERMISSION_MANAGE, getUser(  ) );

        model.put( Constants.MARK_CALENDARS_LIST, listCalendar );
        model.put( Constants.MARK_PAGINATOR, paginator );
        model.put( Constants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( Constants.MARK_PERMISSION_ADVANCED_PARAMETER, bPermissionAdvancedParameter );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_CALENDARS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Return Calendar advanced parameters
     * @param request The Http request
     * @return Html form
     */
    public String getManageAdvancedParameters( HttpServletRequest request )
    	throws AccessDeniedException
    {
    	if ( !RBACService.isAuthorized( CalendarResourceIdService.RESOURCE_TYPE, 
        		RBAC.WILDCARD_RESOURCES_ID,	CalendarResourceIdService.PERMISSION_MANAGE, getUser(  ) ) )
    	{
    		throw new AccessDeniedException(  );
    	}
    	Map<String, Object> model = AgendaService.getInstance(  ).getManageAdvancedParameters( getUser(  ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_ADVANCED_PARAMETERS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the Calendar creation form
     *
     * @param request The Http request
     * @return Html creation form
     */
    public String getCreateCalendar( HttpServletRequest request )
    {
        setPageTitleProperty( Constants.PROPERTY_PAGE_TITLE_CREATE_CALENDAR );

        Map<String, Object> model = new HashMap<String, Object>(  );
        ReferenceList ref = null;

        try
        {
            AdminUser adminUser = getUser(  );
            Locale locale = getLocale(  );
            ref = AdminWorkgroupService.getUserWorkgroups( adminUser, locale );
        }
        catch ( RuntimeException e )
        {
            throw new AppException( e.getMessage(  ), e );
        }

        model.put( Constants.MARK_DOTS_LIST, getDotsList(  ) );
        model.put( Constants.MARK_WORKGROUPS_LIST, ref );
        model.put( Constants.MARK_ROLES_LIST, RoleHome.getRolesList(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_CALENDAR, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process Calendar creation
     *
     * @param request The Http request
     * @return URL
     */
    public String doCreateCalendar( HttpServletRequest request )
    {
        String strName = request.getParameter( Constants.PARAMETER_CALENDAR_NAME );
        String strImage = request.getParameter( Constants.PARAMETER_CALENDAR_IMAGE );
        String strWorkgroup = request.getParameter( Constants.PARAMETER_WORKGROUP );
        String strIsNotify = request.getParameter( Constants.PARAMETER_CALENDAR_NOTIFICATION );
        boolean bIsNotify = ( strIsNotify != null );
        String strPeriodValidity = request.getParameter( Constants.PARAMETER_CALENDAR_PERIOD );
        int nPeriodValidity = -1;

        // Mandatory field
        if ( strName.equals( Constants.EMPTY_STRING ) || ( strImage == null ) || ( bIsNotify && 
        		( strPeriodValidity == null || strPeriodValidity.equals( Constants.EMPTY_STRING ) ) ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }
        
        if ( bIsNotify )
    	{
        	if( strPeriodValidity.matches( Constants.REG_NUMBER ) )
        	{
        		nPeriodValidity = StringUtil.getIntValue( strPeriodValidity, -1 );
        	}
        	else
        	{
        		return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_NUMBER_FORMAT, AdminMessage.TYPE_STOP );
        	}
    	}

        AgendaResource calendar = new AgendaResource(  );
        calendar.setName( strName );
        calendar.setEventImage( strImage );
        calendar.setEventPrefix( request.getParameter( Constants.PARAMETER_CALENDAR_PREFIX ) );
        calendar.setRole( request.getParameter( Constants.PARAMETER_CALENDAR_ROLE ) );
        calendar.setWorkgroup( strWorkgroup );
        calendar.setRoleManager( request.getParameter( Constants.PARAMETER_CALENDAR_ROLE_MANAGER ) );
        calendar.setNotify( bIsNotify );
        calendar.setPeriodValidity( bIsNotify ? nPeriodValidity : -1 );
        CalendarHome.createAgenda( calendar, getPlugin(  ) );

        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_CALENDAR;
    }

    /**
     * Returns the form for calendar modification
     *
     * @param request The Http request
     * @return Html form
     */
    public String getModifyCalendar( HttpServletRequest request )
    {
        setPageTitleProperty( Constants.PROPERTY_PAGE_TITLE_MODIFY_CALENDAR );

        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        String strSortEvents = request.getParameter( Constants.PARAMETER_SORT_EVENTS );
        strSortEvents = ( strSortEvents != null ) ? strSortEvents : "1";

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( Constants.MARK_CALENDAR, CalendarHome.findAgendaResource( nCalendarId, getPlugin(  ) ) );
        model.put( Constants.MARK_WORKGROUPS_LIST, AdminWorkgroupService.getUserWorkgroups( getUser(  ), getLocale(  ) ) );
        model.put( Constants.MARK_DOTS_LIST, getDotsList(  ) );

        List<SimpleEvent> listEvents = CalendarHome.findEventsList( nCalendarId, Integer.parseInt( strSortEvents ),
                getPlugin(  ) );

        //paginator parameters
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        Paginator paginator = new Paginator( listEvents, _nItemsPerPage,
                JSP_MODIFY_CALENDAR + nCalendarId + "&sort_events=" + Integer.parseInt( strSortEvents ),
                Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        model.put( Constants.MARK_PAGINATOR, paginator );
        model.put( Constants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( Constants.MARK_EVENTS_LIST, paginator.getPageItems(  ) );
        model.put( Constants.MARK_EVENTS_SORT_LIST, getSortEventList(  ) );
        model.put( Constants.MARK_DEFAULT_SORT_EVENT, Integer.parseInt( strSortEvents ) );
        model.put( Constants.MARK_ROLES_LIST, RoleHome.getRolesList(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_CALENDAR, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process the Calendar modifications
     *
     * @param request The Http request
     * @return Html form
     */
    public String doModifyCalendar( HttpServletRequest request )
    {
        String strName = request.getParameter( Constants.PARAMETER_CALENDAR_NAME );
        String strWorkgroup = request.getParameter( Constants.PARAMETER_WORKGROUP );
        String strImage = request.getParameter( Constants.PARAMETER_CALENDAR_IMAGE );
        String strIsNotify = request.getParameter( Constants.PARAMETER_CALENDAR_NOTIFICATION );
        boolean bIsNotify = ( strIsNotify != null );
        String strPeriodValidity = request.getParameter( Constants.PARAMETER_CALENDAR_PERIOD );
        int nPeriodValidity = -1;

        // Mandatory field
        if ( strName.equals( Constants.EMPTY_STRING ) || ( strImage == null ) || ( bIsNotify && 
        		( strPeriodValidity == null || strPeriodValidity.equals( Constants.EMPTY_STRING ) ) ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }
        
        if ( bIsNotify )
    	{
        	if( strPeriodValidity.matches( Constants.REG_NUMBER ) )
        	{
        		nPeriodValidity = StringUtil.getIntValue( strPeriodValidity, -1 );
        	}
        	else
        	{
        		return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_NUMBER_FORMAT, AdminMessage.TYPE_STOP );
        	}
    	}

        AgendaResource calendar = CalendarHome.findAgendaResource( Integer.parseInt( request.getParameter( 
                        Constants.PARAMETER_CALENDAR_ID ) ), getPlugin(  ) );

        calendar.setName( strName );
        calendar.setEventImage( strImage );
        calendar.setEventPrefix( request.getParameter( Constants.PARAMETER_CALENDAR_PREFIX ) );
        calendar.setRole( request.getParameter( Constants.PARAMETER_CALENDAR_ROLE ) );
        calendar.setRoleManager( request.getParameter( Constants.PARAMETER_CALENDAR_ROLE_MANAGER ) );
        calendar.setWorkgroup( strWorkgroup );
        calendar.setNotify( bIsNotify );
        calendar.setPeriodValidity( bIsNotify ? nPeriodValidity : -1 );
        CalendarHome.updateAgenda( calendar, getPlugin(  ) );

        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_CALENDAR;
    }

    /**
     * Returns the confirmation to remove the calendar
     *
     * @param request The Http request
     * @return the confirmation page
     */
    public String getConfirmRemoveCalendar( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( JSP_DO_REMOVE_CALENDAR );
        url.addParameter( Constants.PARAMETER_CALENDAR_ID,
            Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CALENDAR, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Remove a calendar
     *
     * @param request The Http request
     * @return Html form
     */
    public String doRemoveCalendar( HttpServletRequest request )
    {
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        List<SimpleEvent> listEvents = CalendarHome.findEventsList( nCalendarId, 1, getPlugin(  ) );

        for ( SimpleEvent event : listEvents )
        {
        	List<OccurrenceEvent> listOccurencesEvent = CalendarHome.findOccurrencesList( nCalendarId, event.getId(  ), 1,
                    getPlugin(  ) );

            for ( OccurrenceEvent occ : listOccurencesEvent )
            {
                IndexationService.addIndexerAction( Integer.toString( occ.getId(  ) ),
                    AppPropertiesService.getProperty( CalendarIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );
                CalendarIndexerUtils.addIndexerAction( occ.getId(  ), IndexerAction.TASK_DELETE  );
            }
        	
            CalendarHome.removeEvent( nCalendarId, event.getId(  ), getPlugin(  ) );
        }

        CalendarHome.removeAgenda( nCalendarId, getPlugin(  ) );

        // Go to the parent page
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_CALENDAR;
    }

    /**
     * Returns the Event creation form
     *
     * @param request The Http request
     * @return Html creation form
     */
    public String getCreateEvent( HttpServletRequest request )
    {
        setPageTitleProperty( Constants.PROPERTY_PAGE_TITLE_CREATE_EVENT );

        //The defaut number of day for the list
        int nDays = 1;

        //Retrieve category list
        Collection<Category> categoryList = CategoryHome.findAll( getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        String strBooleanTimeSpan = "TRUE";
        model.put( MARK_INTERVAL_TIME_SPAN, strBooleanTimeSpan );
        model.put( Constants.MARK_CALENDAR_ID,
            Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) ) );
        model.put( Constants.MARK_LOCALE, getLocale(  ).getLanguage(  ) );
        model.put( MARK_INTERVAL_LIST, getIntervalList( request ) );
        model.put( MARK_NUMBER_DAYS, nDays );
        model.put( MARK_INTERVAL_LIST, getIntervalList( request ) );
        model.put( MARK_TOP_EVENT_LIST, getTopEventList(  ) );
        model.put( MARK_EMAIL_NOTIFY, AppPropertiesService.getProperty( PROPERTY_EMAIL_NOTIFY ) );
        model.put( MARK_TOP_EVENT_DEFAULT, AppPropertiesService.getProperty( PROPERTY_TOP_EVENT_DEFAULT ) );
        model.put( MARK_TOP_EVENT_DEFAULT, AppPropertiesService.getProperty( PROPERTY_TOP_EVENT_DEFAULT ) );
        model.put( MARK_INSERT_SERVICE_PAGE, JSP_GET_INSERT_SERVICE );
        model.put( MARK_INSERT_SERVICE_LINK_PAGE, JSP_GET_INSERT_LINK_SERVICE );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( Constants.MARK_CATEGORY_LIST, new HashMap<String, Object>(  ) );
        model.put( Constants.MARK_CATEGORY_DEFAULT_LIST, categoryList );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_EVENT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Return the list of Event linked with an agenda
     * @param request The request
     * @return The list of Event linked with an agenda
     */
    public String getEventList( HttpServletRequest request )
    {
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );

        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( Constants.MARK_CALENDAR_ID, nCalendarId );
        
        List<SimpleEvent> listEvents = null;
        
        // SORT
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );
        String strAscSort = null;
        if ( strSortedAttributeName != null && strSortedAttributeName.equals( Constants.PARAMETER_DATE ) )
        {
        	strAscSort = request.getParameter( Parameters.SORTED_ASC );

            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );
            int nIsAscSort = bIsAscSort? 1 : 0;
            listEvents = CalendarHome.findEventsList( nCalendarId, nIsAscSort, getPlugin(  ) );
        }
        else
        {
        	listEvents = CalendarHome.findEventsList( nCalendarId, 1, getPlugin(  ) );
        }
        
        if ( strSortedAttributeName != null )
        {
            strAscSort = request.getParameter( Parameters.SORTED_ASC );

            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );

            Collections.sort( listEvents, new AttributeComparator( strSortedAttributeName, bIsAscSort ) );
        }

        //paginator parameters
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );
        
        String strURL = AppPathService.getBaseUrl( request ) + JSP_EVENT_LIST2 + nCalendarId;
        UrlItem url = new UrlItem( strURL );
        url.addParameter( Constants.PARAMETER_CALENDAR_ID, nCalendarId );
        if ( strSortedAttributeName != null )
        {
        	url.addParameter( Parameters.SORTED_ATTRIBUTE_NAME, strSortedAttributeName );
        }
        
        if ( strAscSort != null )
        {
        	url.addParameter( Parameters.SORTED_ASC, strAscSort );
        }

        LocalizedPaginator paginator = new LocalizedPaginator( listEvents, _nItemsPerPage, url.getUrl(  ),
                Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );
        
        model.put( Constants.MARK_CALENDAR, CalendarHome.findAgendaResource( nCalendarId, getPlugin(  ) ) );
        model.put( Constants.MARK_PAGINATOR, paginator );
        model.put( Constants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( Constants.MARK_EVENTS_LIST, paginator.getPageItems(  ) );
        model.put( Constants.MARK_EVENTS_SORT_LIST, getSortEventList(  ) );
        model.put( Constants.MARK_ROLES_LIST, RoleHome.getRolesList(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EVENT_LIST, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Return the list of occurrence linked with an Event
     * @param request The request
     * @return The list of occurrence linked with an Event
     */
    public String getOccurrenceList( HttpServletRequest request )
    {
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
        int nIdEvent = Integer.parseInt( strEventId );

        SimpleEvent event = CalendarHome.findEvent( nIdEvent, getPlugin(  ) );
        int nDays = CalendarHome.getRepetitionDays( nIdEvent, getPlugin(  ) );
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        List<OccurrenceEvent> listOccurrences = null;

        // SORT
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );
        String strAscSort = null;
        if ( strSortedAttributeName != null && strSortedAttributeName.equals( Constants.PARAMETER_DATE ) )
        {
        	strAscSort = request.getParameter( Parameters.SORTED_ASC );

            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );
            int nIsAscSort = bIsAscSort? 1 : 0;
            listOccurrences = CalendarHome.findOccurrencesList( nCalendarId, event.getId(  ), nIsAscSort, getPlugin(  ) );
        }
        else
        {
        	listOccurrences = CalendarHome.findOccurrencesList( nCalendarId, event.getId(  ), 1, getPlugin(  ) );
        }
        
        if ( strSortedAttributeName != null )
        {
            strAscSort = request.getParameter( Parameters.SORTED_ASC );

            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );

            Collections.sort( listOccurrences, new AttributeComparator( strSortedAttributeName, bIsAscSort ) );
        }

        //paginator parameters
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );
        
        String strURL = AppPathService.getBaseUrl( request ) + JSP_OCCURRENCE_LIST + nCalendarId;
        UrlItem url = new UrlItem( strURL );
        url.addParameter( Constants.PARAMETER_EVENT_ID, event.getId(  ) );
        if ( strSortedAttributeName != null )
        {
        	url.addParameter( Parameters.SORTED_ATTRIBUTE_NAME, strSortedAttributeName );
        }
        
        if ( strAscSort != null )
        {
        	url.addParameter( Parameters.SORTED_ASC, strAscSort );
        }

        LocalizedPaginator paginator = new LocalizedPaginator( listOccurrences, _nItemsPerPage, url.getUrl(  ), 
        		Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );

        model.put( Constants.MARK_EVENT, event );
        model.put( Constants.MARK_CALENDAR_ID, nCalendarId );
        model.put( Constants.MARK_PAGINATOR, paginator );
        model.put( Constants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( Constants.MARK_OCCURRENCES_LIST, paginator.getPageItems(  ) );
        model.put( Constants.MARK_EVENTS_SORT_LIST, getSortEventList(  ) );
        model.put( MARK_INTERVAL_LIST, getIntervalList( request ) );
        model.put( MARK_NUMBER_DAYS, nDays );
        model.put( MARK_EVENT_STATUS_LIST, getStatusList( request ) );
        model.put( MARK_DEFAULT_STATUS, AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_DEFAULT ) );
        
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_OCCURRENCE_LIST, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process Event creation
     *
     * @param request The Http request
     * @return URL
     */
    public String doCreateEvent( HttpServletRequest request )
    {
        //Retrieving parameters from form
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        int nPeriodicity = Integer.parseInt( request.getParameter( Constants.PARAMETER_PERIODICITY ) );
        String strEventDateEnd = request.getParameter( Constants.PARAMETER_EVENT_DATE_END );
        String strTimeStart = request.getParameter( Constants.PARAMETER_EVENT_TIME_START );
        String strTimeEnd = request.getParameter( Constants.PARAMETER_EVENT_TIME_END );
        String strOccurrence = request.getParameter( Constants.PARAMETER_OCCURRENCE );
        String strEventTitle = request.getParameter( Constants.PARAMETER_EVENT_TITLE );
        String strDate = request.getParameter( Constants.PARAMETER_EVENT_DATE );
        String strNotify = request.getParameter( Constants.PARAMETER_NOTIFY );

        //Retrieve the features of an event from form
        String strDescription = request.getParameter( Constants.PARAMETER_DESCRIPTION );
        String strEventTags = request.getParameter( Constants.PARAMETER_EVENT_TAGS ).trim(  );
        String strLocationAddress = request.getParameter( Constants.PARAMETER_EVENT_LOCATION_ADDRESS ).trim(  );
        String strLocationTown = request.getParameter( Constants.PARAMETER_LOCATION_TOWN ).trim(  );
        String strLocation = request.getParameter( Constants.PARAMETER_LOCATION ).trim(  );
        String strLocationZip = request.getParameter( Constants.PARAMETER_LOCATION_ZIP ).trim(  );
        String strLinkUrl = request.getParameter( Constants.PARAMETER_EVENT_LINK_URL ).trim(  );
        String strDocumentId = request.getParameter( Constants.PARAMETER_EVENT_DOCUMENT_ID ).trim(  );
        String strPageUrl = request.getParameter( Constants.PARAMETER_EVENT_PAGE_URL ).trim(  );
        String strTopEvent = request.getParameter( Constants.PARAMETER_EVENT_TOP_EVENT ).trim(  );
        String strMapUrl = request.getParameter( Constants.PARAMETER_EVENT_MAP_URL ).trim(  );
        
        //Retrieving the excluded days
        String[] arrayExcludedDays = request.getParameterValues( Constants.PARAMETER_EXCLUDED_DAYS );

        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        FileItem item = mRequest.getFile( Constants.PARAMETER_EVENT_IMAGE );

        String[] arrayCategory = request.getParameterValues( Constants.PARAMETER_CATEGORY );

        //Categories
        List<Category> listCategories = new ArrayList<Category>(  );

        if ( arrayCategory != null )
        {
            for ( String strIdCategory : arrayCategory )
            {
                Category category = CategoryHome.find( Integer.parseInt( strIdCategory ), getPlugin(  ) );

                if ( category != null )
                {
                    listCategories.add( category );
                }
            }
        }

        String[] strTabEventTags = null;

        // Mandatory field
        if ( strDate.equals( "" ) || strEventTitle.equals( "" ) || strDescription.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        if ( ( strEventTags.length(  ) > 0 ) && !strEventTags.equals( "" ) )
        {
            //Test if there aren't special characters in tag list
            boolean isAllowedExp = Pattern.matches( PROPERTY_TAGS_REGEXP, strEventTags );

            if ( !isAllowedExp )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TAG, AdminMessage.TYPE_STOP );
            }

            strTabEventTags = strEventTags.split( "\\s" );
        }

        //Convert the date in form to a java.util.Date object
        Date dateEvent = DateUtil.formatDate( strDate, getLocale(  ) );

        if ( ( dateEvent == null ) || !Utils.isValidDate( dateEvent ) )
        {
            return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                AdminMessage.TYPE_STOP );
        }

        Date dateEndEvent = null;

        if ( !strEventDateEnd.equals( "" ) )
        {
            dateEndEvent = DateUtil.formatDate( strEventDateEnd, getLocale(  ) );

            if ( ( dateEndEvent == null ) || !Utils.isValidDate( dateEndEvent ) )
            {
                return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                    AdminMessage.TYPE_STOP );
            }
        }

        //the number of occurrence is 1 by default
        int nOccurrence = 1;

        if ( ( strOccurrence.length(  ) > 0 ) && !strOccurrence.equals( "" ) )
        {
            try
            {
                nOccurrence = Integer.parseInt( strOccurrence );
            }
            catch ( NumberFormatException e )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_OCCURRENCE_NUMBER,
                    AdminMessage.TYPE_STOP );
            }
        }

        int nDocumentId = -1;

        if ( ( strDocumentId.length(  ) > 0 ) && !strDocumentId.equals( "" ) )
        {
            try
            {
                nDocumentId = Integer.parseInt( strDocumentId );
            }
            catch ( NumberFormatException e )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_OCCURRENCE_NUMBER,
                    AdminMessage.TYPE_STOP );
            }
        }

        if ( !Utils.checkTime( strTimeStart ) || !Utils.checkTime( strTimeEnd ) )
        {
            return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_TIMEFORMAT,
                AdminMessage.TYPE_STOP );
        }

        //If a date end is chosen
        if ( ( Integer.parseInt( request.getParameter( Constants.PARAMETER_RADIO_PERIODICITY ) ) == 1 ) &&
                !strEventDateEnd.equals( "" ) )
        {
            if ( dateEndEvent.before( dateEvent ) )
            {
                return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATE_END_BEFORE,
                    AdminMessage.TYPE_STOP );
            }
            else
            {
                nPeriodicity = 0;
                nOccurrence = Utils.getOccurrenceWithinTwoDates( dateEvent, dateEndEvent, arrayExcludedDays );
            }
        }
        else if ( ( Integer.parseInt( request.getParameter( Constants.PARAMETER_RADIO_PERIODICITY ) ) == 1 ) &&
                strEventDateEnd.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                AdminMessage.TYPE_STOP );
        }

        //If a date end is not chosen
        if ( ( Integer.parseInt( request.getParameter( Constants.PARAMETER_RADIO_PERIODICITY ) ) == 0 ) &&
                strEventDateEnd.equals( "" ) )
        {
            dateEndEvent = Utils.getDateForward( dateEvent, nPeriodicity, nOccurrence, arrayExcludedDays );
            if ( dateEndEvent.before( dateEvent ) )
            {
            	nOccurrence = 0;
            	dateEndEvent = dateEvent;
            }
        }

        SimpleEvent event = new SimpleEvent(  );
        event.setDate( dateEvent );
        event.setDateEnd( dateEndEvent );
        event.setDateTimeStart( strTimeStart );
        event.setDateTimeEnd( strTimeEnd );
        event.setTitle( strEventTitle );
        event.setOccurrence( nOccurrence );
        event.setPeriodicity( nPeriodicity );
        event.setDescription( strDescription );

        if( item.getSize(  ) == 0 )
        {
        	HttpSession session = request.getSession( true );
    	    if( session.getAttribute( ATTRIBUTE_MODULE_DOCUMENT_TO_CALENDAR_CONTENT_FILE ) != null && session.getAttribute( ATTRIBUTE_MODULE_DOCUMENT_TO_CALENDAR_MIME_TYPE_FILE) != null  )
    	    {
    	    	ImageResource imageResource = new ImageResource(  );
                imageResource.setImage( ( byte[] ) session.getAttribute( ATTRIBUTE_MODULE_DOCUMENT_TO_CALENDAR_CONTENT_FILE ) );
                imageResource.setMimeType( (String) session.getAttribute( ATTRIBUTE_MODULE_DOCUMENT_TO_CALENDAR_MIME_TYPE_FILE ) );
                event.setImageResource( imageResource );
                session.removeAttribute( ATTRIBUTE_MODULE_DOCUMENT_TO_CALENDAR_CONTENT_FILE );
                session.removeAttribute( ATTRIBUTE_MODULE_DOCUMENT_TO_CALENDAR_MIME_TYPE_FILE );
    	    }
        }
        
        if ( item.getSize(  ) >= 1 )
        {
            ImageResource imageResource = new ImageResource(  );
            imageResource.setImage( item.get(  ) );
            imageResource.setMimeType( item.getContentType(  ) );
            event.setImageResource( imageResource );
        }

        event.setTags( strTabEventTags );
        event.setLocationAddress( strLocationAddress );
        event.setLocation( strLocation );
        event.setLocationTown( strLocationTown );
        event.setLocationZip( strLocationZip );
        event.setLinkUrl( strLinkUrl );
        event.setPageUrl( strPageUrl );
        event.setTopEvent( Integer.parseInt( strTopEvent ) );
        event.setMapUrl( strMapUrl );
        event.setDocumentId( nDocumentId );
        event.setListCategories( listCategories );
        event.setExcludedDays( arrayExcludedDays );

        CalendarHome.createEvent( nCalendarId, event, getPlugin(  ), Constants.EMPTY_NULL );

        List<OccurrenceEvent> listOccurencesEvent = CalendarHome.findOccurrencesList( nCalendarId, event.getId(  ), 1,
                getPlugin(  ) );

        for ( OccurrenceEvent occ : listOccurencesEvent )
        {
            IndexationService.addIndexerAction( Integer.toString( occ.getId(  ) ),
                AppPropertiesService.getProperty( CalendarIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_CREATE );
            CalendarIndexerUtils.addIndexerAction( occ.getId(  ), IndexerAction.TASK_CREATE  );
        }

        /*IndexationService.addIndexerAction( Constants.EMPTY_STRING + nCalendarId
                ,AppPropertiesService.getProperty( CalendarIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_CREATE );*/
        boolean isNotify = Boolean.parseBoolean( strNotify );

        if ( isNotify )
        {
            int nSubscriber = CalendarSubscriberHome.findSubscriberNumber( nCalendarId, getPlugin(  ) );

            if ( nSubscriber > 0 )
            {
                Collection<CalendarSubscriber> calendarSubscribers = CalendarSubscriberHome.findSubscribers( nCalendarId,
                        getPlugin(  ) );

                AgendaSubscriberService.getInstance(  )
                                       .sendSubscriberMail( request, calendarSubscribers, event, nCalendarId );
            }
        }

        return AppPathService.getBaseUrl(request) + JSP_EVENT_LIST2 + nCalendarId;
    }

    /**
     * Returns the form for event modification
     *
     * @param request The Http request
     * @return Html form
     */
    public String getModifyEvent( HttpServletRequest request )
    {
        setPageTitleProperty( Constants.PROPERTY_PAGE_TITLE_MODIFY_EVENT );

        //TODO add new parameters

        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
        int nIdEvent = Integer.parseInt( strEventId );

        int nDays = CalendarHome.getRepetitionDays( nIdEvent, getPlugin(  ) );

        SimpleEvent event = CalendarHome.findEvent( nIdEvent, getPlugin(  ) );
        event.setImageUrl( EventImageResourceService.getInstance(  ).getResourceImageEvent( event.getId(  ) ) );

        Map<String, Object> model = new HashMap<String, Object>(  );

        String strBooleanTimeSpan = "TRUE";

        if ( nDays <= 0 )
        {
            strBooleanTimeSpan = "FALSE";
        }

        // Generate the list of occurrences
        String strSortEvents = request.getParameter( Constants.PARAMETER_SORT_EVENTS );
        strSortEvents = ( strSortEvents != null ) ? strSortEvents : "1";

        Collection<Category> categoryDefaultList = CategoryHome.findAll( getPlugin(  ) );
        Collection<Category> categoryList = event.getListCategories(  );
        HashMap<String, Object> mapCategorySelected = new HashMap<String, Object>(  );

        for ( Category catSelected : categoryList )
        {
            mapCategorySelected.put( Constants.EMPTY_STRING + catSelected.getId(  ), catSelected.getName(  ) );
        }

        //Retrieve event category
        model.put( Constants.MARK_CATEGORY_DEFAULT_LIST, categoryDefaultList );
        //Retrieve category list
        model.put( Constants.MARK_CATEGORY_LIST, mapCategorySelected );

        model.put( MARK_INTERVAL_TIME_SPAN, strBooleanTimeSpan );
        // getDateForward() compute the date end depending on the periodicity and the occurrence number
        /*model.put( Constants.MARK_EVENT_DATE_END,
            Utils.getDateForward( event.getDate(  ), event.getPeriodicity(  ), event.getOccurrence(  ) ) );*/
        model.put( Constants.MARK_EVENT_DATE_END, event.getDateEnd(  ) );
        model.put( Constants.MARK_EVENT, event );
        model.put( Constants.MARK_CALENDAR_ID, nCalendarId );
        model.put( Constants.MARK_DEFAULT_SORT_EVENT, request.getParameter( Constants.PARAMETER_SORT_EVENTS ) );
        model.put( Constants.MARK_LOCALE, getLocale(  ).getLanguage(  ) );
        model.put( MARK_INTERVAL_LIST, getIntervalList( request ) );
        model.put( MARK_NUMBER_DAYS, nDays );
        model.put( Constants.MARK_EVENTS_SORT_LIST, getSortEventList(  ) );
        model.put( Constants.MARK_DEFAULT_SORT_EVENT, Integer.parseInt( strSortEvents ) );
        model.put( MARK_TOP_EVENT_LIST, getTopEventList(  ) );
        model.put( MARK_TOP_EVENT_DEFAULT, event.getTopEvent(  ) );
        model.put( MARK_INSERT_SERVICE_PAGE, JSP_GET_INSERT_SERVICE );
        model.put( MARK_INSERT_SERVICE_LINK_PAGE, JSP_GET_INSERT_LINK_SERVICE );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_EVENT, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Process the Event modifications
     *
     * @param request The Http request
     * @return Html form
     */
    public String doModifyEvent( HttpServletRequest request )
    {
        //Retrieving the event object to update
        SimpleEvent event = CalendarHome.findEvent( Integer.parseInt( request.getParameter( 
                        Constants.PARAMETER_EVENT_ID ) ), getPlugin(  ) );

        //Retrieving parameters from form
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );

        String strEventTitle = (String) _mapParameters.get( Constants.PARAMETER_EVENT_TITLE );
        String strTopEvent = (String) _mapParameters.get( Constants.PARAMETER_EVENT_TOP_EVENT );
        String strEventDate = (String) _mapParameters.get( Constants.PARAMETER_EVENT_DATE );

        boolean bPeriociteModify = false;

        // Mandatory field: the date, the title and the description fields
        if ( strEventDate.equals( "" ) || strEventTitle.equals( "" ) || _EventDescription.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        //Convert the date in form to a java.util.Date object
        Date dateEvent = DateUtil.formatDate( strEventDate, getLocale(  ) );
        Date dateEventOld = event.getDate(  );

        //If event date is diffrent, periodicite is re-initialized
        if ( dateEvent.getTime(  ) != dateEventOld.getTime(  ) )
        {
            bPeriociteModify = true;
        }

        if ( ( dateEvent == null ) || !Utils.isValidDate( dateEvent ) )
        {
            return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                AdminMessage.TYPE_STOP );
        }

        //Fields to Update
        event.setDate( dateEvent );
        event.setTitle( strEventTitle );
        event.setTopEvent( Integer.parseInt( strTopEvent ) );
        event.setDescription( _EventDescription );

        //String strOccurrence = request.getParameter( Constants.PARAMETER_OCCURRENCE );
        String strEventDateEnd = (String) _mapParameters.get( Constants.PARAMETER_EVENT_DATE_END );

        int nPeriodicity = event.getPeriodicity(  );

        Date dateEndEvent = null;

        if ( !strEventDateEnd.equals( "" ) )
        {
            dateEndEvent = DateUtil.formatDate( strEventDateEnd, getLocale(  ) );

            if ( ( dateEndEvent == null ) || !Utils.isValidDate( dateEndEvent ) )
            {
                return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                    AdminMessage.TYPE_STOP );
            }
        }

        //the number of occurrence is -1 by default
        int nOccurrence = -1;
        
        String[] arrayExcludedDays = ( String[] ) _mapParameters.get( Constants.PARAMETER_EXCLUDED_DAYS );
        boolean bExcludedDaysModified = false;
        if ( arrayExcludedDays == null && event.getExcludedDays(  ) != null && event.getExcludedDays(  ).length != 0 )
        {
        	bExcludedDaysModified = true;
        }
        else if ( arrayExcludedDays != null && ( event.getExcludedDays(  ) == null || 
        		( event.getExcludedDays(  ) != null && event.getExcludedDays(  ).length == 0 ) ) )
		{
        	bExcludedDaysModified = true;
		}
        else if ( arrayExcludedDays != null && event.getExcludedDays(  ) != null && 
        		arrayExcludedDays.length != event.getExcludedDays(  ).length )
        {
        	bExcludedDaysModified = true;
        }
        else if ( arrayExcludedDays != null && event.getExcludedDays(  ) != null && event.getExcludedDays(  ).length != 0 && 
        		arrayExcludedDays.length == event.getExcludedDays(  ).length )
        {
        	for ( int i = 0; i < arrayExcludedDays.length; i++ )
        	{
        		if ( !arrayExcludedDays[i].equals( event.getExcludedDays(  )[i] ) )
        		{
        			bExcludedDaysModified = true;
        			break;
        		}
        	}
        }

        //compute the occurrence number depending on the date end chosen
        //If a date end is chosen
        if ( !strEventDateEnd.equals( "" ) && ( event.getDateEnd(  ).getTime(  ) != dateEndEvent.getTime(  ) ) || 
        		bExcludedDaysModified )
        {
            if ( dateEndEvent.before( dateEvent ) )
            {
                return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATE_END_BEFORE,
                    AdminMessage.TYPE_STOP );
            }
            else
            {
                nPeriodicity = 0;
                nOccurrence = Utils.getOccurrenceWithinTwoDates( dateEvent, dateEndEvent, arrayExcludedDays );
                bPeriociteModify = true;
            }
        }
        else if ( strEventDateEnd.equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                AdminMessage.TYPE_STOP );
        }

        // Fields to Update
        if ( nOccurrence > 0 )
        {
            event.setOccurrence( nOccurrence );
        }

        event.setDateEnd( dateEndEvent );
        event.setPeriodicity( nPeriodicity );

        String strTimeStart = (String) _mapParameters.get( Constants.PARAMETER_EVENT_TIME_START );
        String strTimeEnd = (String) _mapParameters.get( Constants.PARAMETER_EVENT_TIME_END );
        String strEventTags = ( (String) _mapParameters.get( Constants.PARAMETER_EVENT_TAGS ) ).trim(  );
        String strLocationAddress = ( (String) _mapParameters.get( Constants.PARAMETER_EVENT_LOCATION_ADDRESS ) ).trim(  );
        String strLocation = ( (String) _mapParameters.get( Constants.PARAMETER_LOCATION ) ).trim(  );
        String strLocationTown = ( (String) _mapParameters.get( Constants.PARAMETER_LOCATION_TOWN ) ).trim(  );
        String strLocationZip = ( (String) _mapParameters.get( Constants.PARAMETER_LOCATION_ZIP ) ).trim(  );
        String strLinkUrl = ( (String) _mapParameters.get( Constants.PARAMETER_EVENT_LINK_URL ) ).trim(  );
        String strDocumentId = ( (String) _mapParameters.get( Constants.PARAMETER_EVENT_DOCUMENT_ID ) ).trim(  );
        String strPageUrl = ( (String) _mapParameters.get( Constants.PARAMETER_EVENT_PAGE_URL ) ).trim(  );
        String strMapUrl = ( (String) _mapParameters.get( Constants.PARAMETER_EVENT_MAP_URL ) ).trim(  );

        //Tags
        String[] strTabEventTags = null;

        if ( ( strEventTags.length(  ) > 0 ) && !strEventTags.equals( "" ) )
        {
            //Test if there aren't special characters in strEventTags
            boolean isAllowedExp = Pattern.matches( PROPERTY_TAGS_REGEXP, strEventTags );

            if ( !isAllowedExp )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_TAG, AdminMessage.TYPE_STOP );
            }

            strTabEventTags = strEventTags.split( "\\s" );
        }

        //Categories
        List<Category> listCategories = new ArrayList<Category>(  );

        if ( _EventCategories != null )
        {
            for ( String strIdCategory : _EventCategories )
            {
                Category category = CategoryHome.find( Integer.parseInt( strIdCategory ), getPlugin(  ) );

                if ( category != null )
                {
                    listCategories.add( category );
                }
            }
        }

        // Hours
        if ( !Utils.checkTime( strTimeStart ) || !Utils.checkTime( strTimeEnd ) )
        {
            return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_TIMEFORMAT,
                AdminMessage.TYPE_STOP );
        }

        // Document
        int nDocumentId = -1;

        if ( ( strDocumentId.length(  ) > 0 ) && !strDocumentId.equals( "" ) )
        {
            try
            {
                nDocumentId = Integer.parseInt( strDocumentId );
            }
            catch ( NumberFormatException e )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_OCCURRENCE_NUMBER,
                    AdminMessage.TYPE_STOP );
            }
        }

        event.setTags( strTabEventTags );
        event.setDocumentId( nDocumentId );
        event.setListCategories( listCategories );
        event.setDateTimeStart( strTimeStart );
        event.setDateTimeEnd( strTimeEnd );
        event.setLocation( strLocation );
        event.setLocationAddress( strLocationAddress );
        event.setLocationTown( strLocationTown );
        event.setLocationZip( strLocationZip );
        event.setLinkUrl( strLinkUrl );
        event.setPageUrl( strPageUrl );
        event.setMapUrl( strMapUrl );
        event.setExcludedDays( arrayExcludedDays );

        if ( _EventItem.getSize(  ) >= 1 )
        {
            ImageResource imageResource = new ImageResource(  );
            imageResource.setImage( _EventItem.get(  ) );
            imageResource.setMimeType( _EventItem.getContentType(  ) );
            event.setImageResource( imageResource );
        }

        CalendarHome.updateEvent( nCalendarId, event, bPeriociteModify, getPlugin(  ) );

        List<OccurrenceEvent> listOccurencesEvent = CalendarHome.findOccurrencesList( nCalendarId, event.getId(  ), 1,
                getPlugin(  ) );

        for ( OccurrenceEvent occ : listOccurencesEvent )
        {
            //Update occurence
            if ( occ.getDateTimeStart(  ).equals( Constants.EMPTY_STRING ) )
            {
                occ.setDateTimeStart( event.getDateTimeStart(  ) );
            }

            if ( occ.getDateTimeEnd(  ).equals( Constants.EMPTY_STRING ) )
            {
                occ.setDateTimeEnd( event.getDateTimeEnd(  ) );
            }

            CalendarHome.updateOccurrence( occ, nCalendarId, getPlugin(  ) );

            // Index Occurrence
            IndexationService.addIndexerAction( Integer.toString( occ.getId(  ) ),
                AppPropertiesService.getProperty( CalendarIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_MODIFY );
            CalendarIndexerUtils.addIndexerAction( occ.getId(  ), IndexerAction.TASK_MODIFY  );
        }

        _mapParameters = null;

        return JSP_EVENT_LIST + nCalendarId + "&" + Constants.PARAMETER_SORT_EVENTS + "=" +
        request.getParameter( Constants.PARAMETER_SORT_EVENTS );
    }

    /**
     * Process the occurrence modifications
     *
     * @param request The Http request
     * @return Html form
     */
    public String doModifyOccurrence( HttpServletRequest request )
    {
        //TODO add new parameters
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        String strEventDate = request.getParameter( Constants.PARAMETER_EVENT_DATE );
        String strStatus = request.getParameter( Constants.PARAMETER_EVENT_STATUS ).trim(  );
        String strTimeStart = request.getParameter( Constants.PARAMETER_EVENT_TIME_START );
        String strTimeEnd = request.getParameter( Constants.PARAMETER_EVENT_TIME_END );

        // Mandatory field
        if ( strEventDate.equals( "" ) || request.getParameter( Constants.PARAMETER_EVENT_TITLE ).equals( "" ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }

        //Convert the date in form to a java.util.Date object
        Date dateEvent = DateUtil.formatDate( strEventDate, getLocale(  ) );

        if ( ( dateEvent == null ) || !Utils.isValidDate( dateEvent ) )
        {
            return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                AdminMessage.TYPE_STOP );
        }

        if ( !Utils.checkTime( strTimeStart ) || !Utils.checkTime( strTimeEnd ) )
        {
            return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_TIMEFORMAT,
                AdminMessage.TYPE_STOP );
        }

        OccurrenceEvent occurrence = CalendarHome.findOccurrence( Integer.parseInt( request.getParameter( 
                        Constants.PARAMETER_OCCURRENCE_ID ) ), getPlugin(  ) );

        occurrence.setDate( dateEvent );
        occurrence.setStatus( strStatus );
        occurrence.setDateTimeStart( strTimeStart );
        occurrence.setDateTimeEnd( strTimeEnd );
        occurrence.setTitle( request.getParameter( Constants.PARAMETER_EVENT_TITLE ) );

        CalendarHome.updateOccurrence( occurrence, nCalendarId, getPlugin(  ) );

        SimpleEvent event = CalendarHome.findEvent( occurrence.getEventId(  ), getPlugin(  ) );
        List<OccurrenceEvent> listOccurrenceEvent = CalendarHome.findOccurrencesList( event.getIdCalendar(  ),
                event.getId(  ), 1, getPlugin(  ) );

        // Just one occurrence
        if ( listOccurrenceEvent.size(  ) == 1 )
        {
            event.setDate( occurrence.getDate(  ) );
            event.setDateEnd( occurrence.getDate(  ) );
        }

        // First occurrence
        else if ( listOccurrenceEvent.get( 0 ).getId(  ) == occurrence.getId(  ) )
        {
            event.setDate( occurrence.getDate(  ) );
        }

        // Last occurrence
        else if ( listOccurrenceEvent.get( listOccurrenceEvent.size(  ) - 1 ).getId(  ) == occurrence.getId(  ) )
        {
            event.setDateEnd( occurrence.getDate(  ) );
        }

        CalendarHome.updateEvent( nCalendarId, event, false, getPlugin(  ) );

        // Incremental indexation - Modify
        IndexationService.addIndexerAction( Integer.toString( occurrence.getId(  ) ),
            AppPropertiesService.getProperty( CalendarIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_MODIFY );

        CalendarIndexerUtils.addIndexerAction( occurrence.getId(  ), IndexerAction.TASK_MODIFY  );
        
        return JSP_OCCURRENCE_LIST2 + nCalendarId + "&" + Constants.PARAMETER_SORT_EVENTS + "=" +
        request.getParameter( Constants.PARAMETER_SORT_EVENTS ) + "&" + Constants.PARAMETER_EVENT_ID + "=" +
        occurrence.getEventId(  );
    }

    /**
     * Modify Periodicity of Occurrence
     * @param request The request
     * @return Html form
     */
    public String doModifyOccurrencePeriodicity( HttpServletRequest request )
    {
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        String strOccurrence = request.getParameter( Constants.PARAMETER_OCCURRENCE );
        int nPeriodicity = Integer.parseInt( request.getParameter( Constants.PARAMETER_PERIODICITY ) );
        String strIdEvent = request.getParameter( Constants.PARAMETER_EVENT_ID );
        int nIdEvent = Integer.parseInt( strIdEvent );
        String strSortEvents = request.getParameter( Constants.PARAMETER_SORT_EVENTS );
        strSortEvents = ( strSortEvents != null ) ? strSortEvents : "1";
        String[] arrayExcludedDays = request.getParameterValues( Constants.PARAMETER_EXCLUDED_DAYS );

        SimpleEvent event = CalendarHome.findEvent( nIdEvent, PluginService.getPlugin( Constants.PLUGIN_NAME ) );

        //the number of occurrence is 1 by default
        int nOccurrence = 1;

        //Retrieving the occurrence from form
        if ( ( strOccurrence.length(  ) > 0 ) && !strOccurrence.equals( "" ) )
        {
            try
            {
                nOccurrence = Integer.parseInt( strOccurrence );
            }
            catch ( NumberFormatException e )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_OCCURRENCE_NUMBER,
                    AdminMessage.TYPE_STOP );
            }
        }
        event.setExcludedDays( arrayExcludedDays );

        Date dateEndEvent = Utils.getDateForward( event.getDate(  ), nPeriodicity, nOccurrence, event.getExcludedDays(  ) );

        // Fields to Update
        event.setOccurrence( nOccurrence );
        event.setDateEnd( dateEndEvent );
        event.setPeriodicity( nPeriodicity );
        CalendarHome.updateEvent( nCalendarId, event, true, getPlugin(  ) );

        List<OccurrenceEvent> listOccurencesEvent = CalendarHome.findOccurrencesList( nCalendarId, event.getId(  ), 1,
                getPlugin(  ) );

        for ( OccurrenceEvent occ : listOccurencesEvent )
        {
            // Index Occurrence - Add
            IndexationService.addIndexerAction( Integer.toString( occ.getId(  ) ),
                AppPropertiesService.getProperty( CalendarIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_CREATE );
            CalendarIndexerUtils.addIndexerAction( occ.getId(  ), IndexerAction.TASK_CREATE  );
        }

        return JSP_OCCURRENCE_LIST2 + nCalendarId + "&" + Constants.PARAMETER_SORT_EVENTS + "=" + strSortEvents + "&" +
        Constants.PARAMETER_EVENT_ID + "=" + strIdEvent;
    }

    /**
     * Returns the form for occurrence modification
     *
     * @param request The Http request
     * @return Html form
     */
    public String getModifyOccurrence( HttpServletRequest request )
    {
        setPageTitleProperty( Constants.PROPERTY_PAGE_TITLE_MODIFY_OCCURRENCE );

        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        String strEventId = request.getParameter( Constants.PARAMETER_OCCURRENCE_ID );
        int nIdOccurrence = Integer.parseInt( strEventId.trim(  ) );

        OccurrenceEvent occurrence = CalendarHome.findOccurrence( nIdOccurrence, getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( Constants.MARK_OCCURRENCE, occurrence );
        model.put( Constants.MARK_CALENDAR_ID, nCalendarId );
        model.put( MARK_EVENT_STATUS_LIST, getStatusList( request ) );
        model.put( MARK_DEFAULT_STATUS, occurrence.getStatus(  ) );
        model.put( Constants.MARK_DEFAULT_SORT_EVENT, request.getParameter( Constants.PARAMETER_SORT_EVENTS ) );
        model.put( Constants.MARK_LOCALE, getLocale(  ).getLanguage(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_OCCURRENCE, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the confirmation to remove the event
     *
     * @param request The Http request
     * @return the confirmation page
     */
    public String getConfirmRemoveEvent( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( JSP_DO_REMOVE_EVENT );

        url.addParameter( Constants.PARAMETER_CALENDAR_ID,
            Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) ) );
        url.addParameter( Constants.PARAMETER_EVENT_ID,
            Integer.parseInt( request.getParameter( Constants.PARAMETER_EVENT_ID ) ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_EVENT, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Remove a event
     *
     * @param request The Http request
     * @return Html form
     */
    public String doRemoveEvent( HttpServletRequest request )
    {
    	
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        Event event = CalendarHome.findEvent( Integer.parseInt( request.getParameter( Constants.PARAMETER_EVENT_ID ) ),
                getPlugin(  ) );
        List<OccurrenceEvent> listOccurencesEvent = CalendarHome.findOccurrencesList( nCalendarId, event.getId(  ), 1,
                getPlugin(  ) );

        for ( OccurrenceEvent occ : listOccurencesEvent )
        {
            IndexationService.addIndexerAction( Integer.toString( occ.getId(  ) ),
                AppPropertiesService.getProperty( CalendarIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );
            CalendarIndexerUtils.addIndexerAction( occ.getId(  ), IndexerAction.TASK_DELETE  );
        }

        CalendarHome.removeEvent( nCalendarId,
            Integer.parseInt( request.getParameter( Constants.PARAMETER_EVENT_ID ) ), getPlugin(  ) );

        //Go to the event list
        return JSP_EVENT_LIST + nCalendarId;
    }

    /**
     * Return a list of calendar dots
     *
     * @return A list of icons
     */
    private ReferenceList getDotsList(  )
    {
        String strDotsPath = AppPropertiesService.getProperty( Constants.PROPERTY_CALENDAR_DOTS_PATH );
        String strRootDirectory = AppPathService.getWebAppPath(  );
        ReferenceList listDots = new ReferenceList(  );

        try
        {
            List<File> listFiles = FileSystemUtil.getFiles( strRootDirectory, "/" + strDotsPath );

            for ( File file : listFiles )
            {
                String strFileName = file.getName(  );

                if ( strFileName.endsWith( EXT_IMAGE_FILES ) )
                {
                    String strPathFile = strDotsPath + strFileName;
                    listDots.addItem( strPathFile, strPathFile );
                }
            }
        }
        catch ( DirectoryNotFoundException e )
        {
            throw new AppException( e.getMessage(  ), e );
        }

        return listDots;
    }

    /**
     * Return the list [(1, ascendant),(2,descendant)] that is used to sort the events date
     *
     * @return a refenceList
     */
    private ReferenceList getSortEventList(  )
    {
        ReferenceList list = new ReferenceList(  );
        list.addItem( 1, I18nService.getLocalizedString( Constants.PROPERTY_SORT_EVENTS + 1, getLocale(  ) ) );
        list.addItem( 2, I18nService.getLocalizedString( Constants.PROPERTY_SORT_EVENTS + 2, getLocale(  ) ) );

        return list;
    }

    /**
     * Return the list of time intervals declared in properties file
     * @return A ReferenceList of time interval
     * @param request The HttpRequest
     */
    public ReferenceList getIntervalList( HttpServletRequest request )
    {
        StringTokenizer st = new StringTokenizer( AppPropertiesService.getProperty( PROPERTY_TIME_INTERVAL_LIST ), "," );
        ReferenceList timeIntervalList = new ReferenceList(  );

        while ( st.hasMoreElements(  ) )
        {
            String strIntervalName = st.nextToken(  ).trim(  );
            String strDescription = I18nService.getLocalizedString( "calendar.interval.periodicity." + strIntervalName +
                    ".description", getLocale(  ) );
            int nDays = AppPropertiesService.getPropertyInt( "calendar.interval." + strIntervalName + ".value", 7 );
            timeIntervalList.addItem( nDays, strDescription );
        }

        return timeIntervalList;
    }

    /**
     * Return the list [(0, no),(2,yes)]
     *
     * @return a refenceList
     */
    private ReferenceList getTopEventList(  )
    {
        ReferenceList list = new ReferenceList(  );
        StringTokenizer st = new StringTokenizer( I18nService.getLocalizedString( PROPERTY_TOP_EVENT_LIST, getLocale(  ) ),
                "," );
        int i = 0;

        while ( st.hasMoreElements(  ) )
            list.addItem( i++, st.nextToken(  ).trim(  ) );

        return list;
    }

    /**
     * Return the list
     *
     * @return a refenceList
     */
    private ReferenceList getStatusList( HttpServletRequest request )
    {
        ReferenceList list = new ReferenceList(  );
        StringTokenizer stStatus = new StringTokenizer( AppPropertiesService.getProperty( PROPERTY_EVENT_STATUS_LIST ), Constants.COMMA );

        while ( stStatus.hasMoreElements(  ) )
        {
            String strStatus = stStatus.nextToken(  ).trim(  );
            String strStatusValue = I18nService.getLocalizedString( "calendar.event.status." + strStatus + ".value",
                    getLocale(  ) );

            list.addItem( strStatus, strStatusValue );
        }

        return list;
    }

    /**
     * Return the list
     *
     * @return a refenceList
     */
    private ReferenceList getReferenceListCategory( Collection<Category> collection )
    {
        ReferenceList list = new ReferenceList(  );

        //list.addItem( Constants.PROPERTY_CALENDAR_NONE, " " );
        if ( collection != null )
        {
            Iterator<Category> i = collection.iterator(  );

            while ( i.hasNext(  ) )
            {
                Category category = (Category) i.next(  );
                list.addItem( category.getId(  ), category.getName(  ) );
            }
        }

        return list;
    }

    /**
     * Returns the confirmation to modify an event
     *
     * @param request The Http request
     * @return the confirmation page
     */
    public String getConfirmModifyEvent( HttpServletRequest request )
    {
        HashMap<String, Object> mapParameters = new HashMap<String, Object>(  );
        Date dateEvent = null;
        Date dateEndEvent = null;

        mapParameters.put( Constants.PARAMETER_EVENT_ID, request.getParameter( Constants.PARAMETER_EVENT_ID ) );
        mapParameters.put( Constants.PARAMETER_CALENDAR_ID, request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        mapParameters.put( Constants.PARAMETER_TYPE_BOX, request.getParameter( Constants.PARAMETER_TYPE_BOX ) );

        if ( request.getParameter( Constants.PARAMETER_EVENT_TITLE ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_TITLE, request.getParameter( Constants.PARAMETER_EVENT_TITLE ) );
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_DATE ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_DATE, request.getParameter( Constants.PARAMETER_EVENT_DATE ) );
            dateEvent = DateUtil.formatDate( request.getParameter( Constants.PARAMETER_EVENT_DATE ),
                    getLocale(  ) );
            
            if ( ( dateEvent == null ) || !Utils.isValidDate( dateEvent ) )
            {
                return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                    AdminMessage.TYPE_STOP );
            }
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_DATE_END ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_DATE_END,
                request.getParameter( Constants.PARAMETER_EVENT_DATE_END ) );
            dateEndEvent = DateUtil.formatDate( request.getParameter( Constants.PARAMETER_EVENT_DATE_END ),
                    getLocale(  ) );
            if ( ( dateEndEvent == null ) || !Utils.isValidDate( dateEndEvent ) )
            {
                return AdminMessageService.getMessageUrl( request, Constants.PROPERTY_MESSAGE_DATEFORMAT,
                    AdminMessage.TYPE_STOP );
            }
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_TIME_START ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_TIME_START,
                request.getParameter( Constants.PARAMETER_EVENT_TIME_START ) );
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_TIME_END ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_TIME_END,
                request.getParameter( Constants.PARAMETER_EVENT_TIME_END ) );
        }

        if ( request.getParameter( Constants.PARAMETER_RADIO_PERIODICITY ) != null )
        {
            mapParameters.put( Constants.PARAMETER_RADIO_PERIODICITY,
                request.getParameter( Constants.PARAMETER_RADIO_PERIODICITY ) );
        }

        if ( request.getParameter( Constants.PARAMETER_PERIODICITY ) != null )
        {
            mapParameters.put( Constants.PARAMETER_PERIODICITY, request.getParameter( Constants.PARAMETER_PERIODICITY ) );
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_LOCATION_ADDRESS ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_LOCATION_ADDRESS,
                request.getParameter( Constants.PARAMETER_EVENT_LOCATION_ADDRESS ) );
        }

        if ( request.getParameter( Constants.PARAMETER_LOCATION ) != null )
        {
            mapParameters.put( Constants.PARAMETER_LOCATION, request.getParameter( Constants.PARAMETER_LOCATION ) );
        }

        if ( request.getParameter( Constants.PARAMETER_LOCATION_TOWN ) != null )
        {
            mapParameters.put( Constants.PARAMETER_LOCATION_TOWN,
                request.getParameter( Constants.PARAMETER_LOCATION_TOWN ) );
        }

        if ( request.getParameter( Constants.PARAMETER_LOCATION_ZIP ) != null )
        {
            mapParameters.put( Constants.PARAMETER_LOCATION_ZIP,
                request.getParameter( Constants.PARAMETER_LOCATION_ZIP ) );
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_LINK_URL ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_LINK_URL,
                request.getParameter( Constants.PARAMETER_EVENT_LINK_URL ) );
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_DOCUMENT_ID ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_DOCUMENT_ID,
                request.getParameter( Constants.PARAMETER_EVENT_DOCUMENT_ID ) );
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_PAGE_URL ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_PAGE_URL,
                request.getParameter( Constants.PARAMETER_EVENT_PAGE_URL ) );
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_TOP_EVENT ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_TOP_EVENT,
                request.getParameter( Constants.PARAMETER_EVENT_TOP_EVENT ) );
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_MAP_URL ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_MAP_URL,
                request.getParameter( Constants.PARAMETER_EVENT_MAP_URL ) );
        }

        if ( request.getParameter( Constants.PARAMETER_OCCURRENCE ) != null )
        {
            mapParameters.put( Constants.PARAMETER_OCCURRENCE, request.getParameter( Constants.PARAMETER_OCCURRENCE ) );
        }

        if ( request.getParameter( Constants.PARAMETER_EVENT_TAGS ) != null )
        {
            mapParameters.put( Constants.PARAMETER_EVENT_TAGS, request.getParameter( Constants.PARAMETER_EVENT_TAGS ) );
        }

        if ( request.getParameter( Constants.PARAMETER_SORT_EVENTS ) != null )
        {
            mapParameters.put( Constants.PARAMETER_SORT_EVENTS, request.getParameter( Constants.PARAMETER_SORT_EVENTS ) );
        }
        
        if ( request.getParameterValues( Constants.PARAMETER_EXCLUDED_DAYS ) != null )
        {
        	mapParameters.put( Constants.PARAMETER_EXCLUDED_DAYS, request.getParameterValues( Constants.PARAMETER_EXCLUDED_DAYS ) );
        }

        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;

        if ( mRequest.getFile( Constants.PARAMETER_EVENT_IMAGE ) != null )
        {
            _EventItem = mRequest.getFile( Constants.PARAMETER_EVENT_IMAGE );
        }

        if ( request.getParameterValues( Constants.PARAMETER_CATEGORY ) != null )
        {
            _EventCategories = request.getParameterValues( Constants.PARAMETER_CATEGORY );
        }

        if ( request.getParameter( Constants.PARAMETER_DESCRIPTION ) != null )
        {
            _EventDescription = request.getParameter( Constants.PARAMETER_DESCRIPTION );
        }
        int nEventId = Integer.parseInt( request.getParameter( Constants.PARAMETER_EVENT_ID ) );
        Event event = CalendarHome.findEvent( nEventId, getPlugin(  ) );

        _mapParameters = mapParameters;

        return DO_MODIFY_EVENT + request.getParameter( Constants.PARAMETER_CALENDAR_ID ) + "&" +
        Constants.PARAMETER_EVENT_ID + "=" + request.getParameter( Constants.PARAMETER_EVENT_ID ) + "&" +
        Constants.PARAMETER_SORT_EVENTS + "=" + request.getParameter( Constants.PARAMETER_SORT_EVENTS ) + "&" +
        Constants.PARAMETER_TYPE_BOX + "=" + request.getParameter( Constants.PARAMETER_TYPE_BOX );
    }

    /**
     * Returns the confirmation to remove occurrence
     *
     * @param request The Http request
     * @return the confirmation page
     */
    public String getConfirmRemoveOccurrence( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( JSP_DO_REMOVE_OCCURRENCE );
        url.addParameter( Constants.PARAMETER_CALENDAR_ID,
            Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) ) );
        url.addParameter( Constants.PARAMETER_EVENT_ID, request.getParameter( Constants.PARAMETER_EVENT_ID ) );
        url.addParameter( Constants.PARAMETER_SORT_EVENTS, request.getParameter( Constants.PARAMETER_SORT_EVENTS ) );
        url.addParameter( Constants.PARAMETER_OCCURRENCE_ID, request.getParameter( Constants.PARAMETER_OCCURRENCE_ID ) );

        String[] tableCBXOccurrence = request.getParameterValues( Constants.PARAMETER_CBX_OCCURRENCE );
        String strCBXOccurrence = "";

        //TODO In this version of core we cannot use url.addParameter(String strName, String[] strValues)
        //so we put the table parameters values to a string separated with token ";"
        if ( tableCBXOccurrence != null )
        {
            for ( int i = 0; i < tableCBXOccurrence.length; i++ )
                strCBXOccurrence += ( tableCBXOccurrence[i] + Constants.COMMA );

            url.addParameter( Constants.PARAMETER_CBX_OCCURRENCE, strCBXOccurrence );
        }

        if ( tableCBXOccurrence == null )
        {
            tableCBXOccurrence = new String[0];
        }

        return AdminMessageService.getMessageUrl( request,
            ( tableCBXOccurrence.length > 1 ) ? MESSAGE_CONFIRM_REMOVE_ALL_OCCURRENCE : MESSAGE_CONFIRM_REMOVE_OCCURRENCE,
            url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Remove a occurrence
     *
     * @param request The Http request
     * @return Html form
     */
    public String doRemoveOccurrence( HttpServletRequest request )
    {
        int nOccurrenceId;
        String strOccurrenceId = request.getParameter( Constants.PARAMETER_OCCURRENCE_ID );
        String strCBXOccurrence = request.getParameter( Constants.PARAMETER_CBX_OCCURRENCE );
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        int nEventId = Integer.parseInt( request.getParameter( Constants.PARAMETER_EVENT_ID ) );
        SimpleEvent event = CalendarHome.findEvent( nEventId, PluginService.getPlugin( Constants.PLUGIN_NAME ) );
        int nOccurrence = event.getOccurrence(  );

        String[] tableCBXOccurrence = new String[0];

        if ( ( strCBXOccurrence != null ) && !strCBXOccurrence.equals( "null" ) &&
                !strCBXOccurrence.equals( Constants.EMPTY_STRING ) )
        {
            tableCBXOccurrence = strCBXOccurrence.split( Constants.COMMA );
        }

        if ( ( strOccurrenceId != null ) && !strOccurrenceId.equals( "null" ) &&
                !strOccurrenceId.equals( Constants.EMPTY_STRING ) )
        {
            nOccurrenceId = Integer.parseInt( strOccurrenceId );
            CalendarHome.removeOccurrence( nOccurrenceId, nEventId, nCalendarId, getPlugin(  ) );
            nOccurrence--;

            //Incremental indexation - Delete
            IndexationService.addIndexerAction( Integer.toString( nOccurrenceId ),
                AppPropertiesService.getProperty( CalendarIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );
            CalendarIndexerUtils.addIndexerAction( nOccurrenceId, IndexerAction.TASK_DELETE  );
        }
        else if ( tableCBXOccurrence.length > 0 )
        {
            for ( int i = 0; tableCBXOccurrence.length > i; i++ )
            {
            	int nOccId = Integer.parseInt( tableCBXOccurrence[i] );
                CalendarHome.removeOccurrence( nOccId, nEventId, nCalendarId, getPlugin(  ) );
                nOccurrence--;
                //Incremental indexation - Delete
                IndexationService.addIndexerAction( tableCBXOccurrence[i],
                    AppPropertiesService.getProperty( CalendarIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );
                CalendarIndexerUtils.addIndexerAction( nOccId, IndexerAction.TASK_DELETE  );
            }
        }

        List<OccurrenceEvent> listOccurrence = CalendarHome.findOccurrencesList( nCalendarId, nEventId, 1,
                PluginService.getPlugin( Constants.PLUGIN_NAME ) );

        if ( !listOccurrence.isEmpty(  ) )
        {
            OccurrenceEvent firstOccurrenceEvent = listOccurrence.get( 0 );
            OccurrenceEvent lastOccurrenceEvent = listOccurrence.get( listOccurrence.size(  ) - 1 );

            event.setDate( firstOccurrenceEvent.getDate(  ) );
            event.setDateEnd( lastOccurrenceEvent.getDate(  ) );
        }

        event.setOccurrence( nOccurrence );
        CalendarHome.updateEvent( nCalendarId, event, false, PluginService.getPlugin( Constants.PLUGIN_NAME ) );

        // Go to the parent page          
        return JSP_OCCURRENCE_LIST2 + nCalendarId + "&" + Constants.PARAMETER_SORT_EVENTS + "=" +
        request.getParameter( Constants.PARAMETER_SORT_EVENTS ) + "&" + Constants.PARAMETER_EVENT_ID + "=" +
        request.getParameter( Constants.PARAMETER_EVENT_ID );
    }

    /**
     * Builds the newsletter's subscribers management page
     *
     * @param request The HTTP request
     * @return the html code for newsletter's subscribers management page
     */
    public String getManageSubscribers( HttpServletRequest request )
    {
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );

        setPageTitleProperty( Constants.PROPERTY_PAGE_TITLE_MANAGE_SUBSCRIBERS );

        Map<String, Object> model = new HashMap<String, Object>(  );

        model.put( Constants.MARK_CALENDAR_ID, nCalendarId );

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        String strSearchString = request.getParameter( Constants.PARAMETER_SUBSCRIBER_SEARCH );

        if ( strSearchString == null )
        {
            strSearchString = Constants.EMPTY_STRING;
        }

        // get a list of subscribers
        List<CalendarSubscriber> calendarSubscribers = CalendarSubscriberHome.findSubscribers( nCalendarId,
                strSearchString, Integer.parseInt( AppPropertiesService.getProperty( PROPERTY_LIMIT_MIN_SUSCRIBER ) ),
                Integer.parseInt( AppPropertiesService.getProperty( PROPERTY_LIMIT_MAX_SUSCRIBER ) ), getPlugin(  ) );

        UrlItem url = new UrlItem( request.getRequestURI(  ) );
        url.addParameter( Constants.PARAMETER_CALENDAR_ID, Integer.toString( nCalendarId ) );

        LocalizedPaginator paginator = new LocalizedPaginator( calendarSubscribers, _nItemsPerPage, url.getUrl(  ),
                Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex, getLocale(  ) );

        model.put( Constants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( Constants.MARK_PAGINATOR, paginator );
        model.put( Constants.MARK_SUBSCRIBERS_LIST, paginator.getPageItems(  ) );
        model.put( Constants.MARK_SEARCH_STRING, strSearchString );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_SUBSCRIBERS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Manages the removal form of a newsletter whose identifier is in the http
     * request
     *
     * @param request  The Http request
     * @return the html code to confirm
     */
    public String getConfirmRemoveSubscriber( HttpServletRequest request )
    {
        UrlItem urlItem = new UrlItem( JSP_URL_DO_REMOVE_SUBSCRIBER );
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        int nSubscriberId = Integer.parseInt( request.getParameter( Constants.PARAMETER_SUBSCRIBER_ID ) );
        urlItem.addParameter( Constants.PARAMETER_CALENDAR_ID, Integer.toString( nCalendarId ) );
        urlItem.addParameter( Constants.PARAMETER_SUBSCRIBER_ID, Integer.toString( nSubscriberId ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_SUBSCRIBER, urlItem.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Processes the unregistration of a subscriber for a newsletter
     *
     * @param request The Http request
     * @return the jsp URL to display the form to manage newsletters
     */
    public String doUnregistration( HttpServletRequest request )
    {
        /* parameters */
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        int nSubscriberId = Integer.parseInt( request.getParameter( Constants.PARAMETER_SUBSCRIBER_ID ) );

        //Checks if a subscriber with the same email address doesn't exist yet
        CalendarSubscriber subscriber = CalendarSubscriberHome.findByPrimaryKey( nSubscriberId, getPlugin(  ) );

        if ( subscriber != null )
        {
            CalendarSubscriberHome.removeSubscriber( nCalendarId, nSubscriberId, getPlugin(  ) );
            if ( !CalendarSubscriberHome.isUserSubscribed( nSubscriberId, getPlugin(  ) ) )
            {
            	CalendarSubscriberHome.remove( nSubscriberId, getPlugin(  ) );
            }
        }

        return JSP_MANAGE_SUBSCRIBERS_LIST + nCalendarId;
    }

    /**
     * Get the document insert service if the plugin is installed
     *
     * @param request The Http request
     * @return the jsp URL for displaying the document insert service or an error message if plugin is uninstalled
     */
    public String getInsertService( HttpServletRequest request )
    {
        Plugin module = PluginService.getPlugin( Constants.PROPERTY_MODULE_CALENDAR );

        if ( ( module != null ) && module.isInstalled(  ) )
        {
            UrlItem url = new UrlItem( JSP_GET_DOCUMENT_INSERT_SERVICE );
            url.addParameter( Constants.PARAMETER_INPUT, Constants.PARAMETER_EVENT_DOCUMENT_ID );

            return url.getUrl(  );
        }
        else
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_PLUGIN_DOCUMENT_UNINSTALLED,
                AdminMessage.TYPE_STOP );
        }
    }

    /**
     * Get the link insert service
     *
     * @param request The Http request
     * @return the jsp URL for displaying the link insert service
     */
    public String getInsertLinkService( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_GET_DOCUMENT_INSERT_LINK_SERVICE + "?" +
        Constants.PARAMETER_INPUT + "=" + Constants.PARAMETER_EVENT_PAGE_URL + "&" + Constants.PARAMETER_SELECTED_TEXT +
        "=" + Constants.EMPTY_STRING;
    }

    /**
     * Get modify occurrence list, whether it is removing or modifying an occurrence
     * @param request HttpServletRequest
     * @return the html code to confirm
     */
    public String getModifyOccurrenceList( HttpServletRequest request )
    {
    	String strRemove = request.getParameter( Constants.PARAMETER_REMOVE_OCCURRENCES );
    	if ( strRemove != null )
    	{
    		return getConfirmRemoveOccurrence( request );
    	}
    	
    	return getConfirmModifyOccurrenceStatus( request );
    }
    
    /**
     * Returns the confirmation to modify occurrences status
     *
     * @param request The Http request
     * @return the confirmation page
     */
    public String getConfirmModifyOccurrenceStatus( HttpServletRequest request )
    {
        UrlItem url = new UrlItem( JSP_DO_MODIFY_OCCURRENCE_STATUS );
        url.addParameter( Constants.PARAMETER_CALENDAR_ID,
            Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) ) );
        url.addParameter( Constants.PARAMETER_SORT_EVENTS, request.getParameter( Constants.PARAMETER_SORT_EVENTS ) );
        url.addParameter( Constants.PARAMETER_EVENT_ID, request.getParameter( Constants.PARAMETER_EVENT_ID ) );
        url.addParameter( Constants.PARAMETER_EVENT_STATUS, request.getParameter( Constants.PARAMETER_EVENT_STATUS ) );

        String[] tableCBXOccurrence = request.getParameterValues( Constants.PARAMETER_CBX_OCCURRENCE );
        String strCBXOccurrence = Constants.EMPTY_STRING;

        if ( tableCBXOccurrence != null )
        {
            for ( int i = 0; i < tableCBXOccurrence.length; i++ )
                strCBXOccurrence += ( tableCBXOccurrence[i] + Constants.COMMA );

            url.addParameter( Constants.PARAMETER_CBX_OCCURRENCE, strCBXOccurrence );
        }

        if ( tableCBXOccurrence == null )
        {
            tableCBXOccurrence = new String[0];
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_MODIFY_OCCURRENCES_STATUS,
            url.getUrl(  ), AdminMessage.TYPE_CONFIRMATION );
    }
    
    /**
     * Modify occurrencesStatus
     * @param request HttpServletRequest
     * @return the return Jsp
     */
    public String doModifyOccurrenceStatus( HttpServletRequest request )
    {
        String strCBXOccurrence = request.getParameter( Constants.PARAMETER_CBX_OCCURRENCE );
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        String strEventStatus = request.getParameter( Constants.PARAMETER_EVENT_STATUS );

        String[] tableCBXOccurrence = new String[0];

        if ( ( strCBXOccurrence != null ) && !strCBXOccurrence.equals( Constants.NULL ) &&
                !strCBXOccurrence.equals( Constants.EMPTY_STRING ) )
        {
            tableCBXOccurrence = strCBXOccurrence.split( Constants.COMMA );
        }
        for ( int i = 0; i < tableCBXOccurrence.length; i++ )
        {
        	int nIdOccurrenceEvent = Integer.parseInt( tableCBXOccurrence[i] );
        	OccurrenceEvent occurrence = CalendarHome.findOccurrence( nIdOccurrenceEvent, getPlugin(  ) );
        	occurrence.setStatus( strEventStatus );
        	CalendarHome.updateOccurrence( occurrence, nCalendarId, getPlugin(  ) );
            //Incremental indexation - Modify
            IndexationService.addIndexerAction( tableCBXOccurrence[i],
                AppPropertiesService.getProperty( CalendarIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_MODIFY );
            CalendarIndexerUtils.addIndexerAction( nIdOccurrenceEvent, IndexerAction.TASK_MODIFY  );
        }
        
        // Go to the parent page          
        return JSP_OCCURRENCE_LIST2 + nCalendarId + Constants.AMPERSAND + Constants.PARAMETER_SORT_EVENTS + Constants.EQUAL +
        request.getParameter( Constants.PARAMETER_SORT_EVENTS ) + Constants.AMPERSAND + Constants.PARAMETER_EVENT_ID + Constants.EQUAL +
        request.getParameter( Constants.PARAMETER_EVENT_ID );
    }

    /**
     * Modify parameters values 
     * @param request HttpServletRequest
     * @return the Jsp return
     * @throws AccessDeniedException access denied
     */
    public String doModifyCalendarParameterValues( HttpServletRequest request )
    	throws AccessDeniedException
    {
    	if ( !RBACService.isAuthorized( CalendarResourceIdService.RESOURCE_TYPE, 
        		RBAC.WILDCARD_RESOURCES_ID,	CalendarResourceIdService.PERMISSION_MANAGE, getUser(  ) ) )
    	{
    		throw new AccessDeniedException(  );
    	}

    	ReferenceList listParams = CalendarParameterHome.findParametersList( getPlugin(  ) );
    	for ( ReferenceItem param : listParams )
    	{
    		String strParamValue = request.getParameter( param.getCode(  ) );
        	if ( strParamValue == null || strParamValue.equals( Constants.EMPTY_STRING ) )
        	{
        		strParamValue = Constants.ZERO;
        	}
        	if ( !strParamValue.matches( Constants.REG_NUMBER ) )
        	{
        		return AdminMessageService.getMessageUrl( request, MESSAGE_INVALID_NUMBER_FORMAT,
                        AdminMessage.TYPE_STOP );
        	}
        	param.setName( strParamValue );
        	CalendarParameterHome.update( param, getPlugin(  ) );
    	}
    	
    	return AppPathService.getBaseUrl( request ) + JSP_URL_MANAGE_ADVANCED_PARAMETERS;
    }
}
