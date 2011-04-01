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
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.CalendarSubscriber;
import fr.paris.lutece.plugins.calendar.business.CalendarSubscriberHome;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.business.MultiAgenda;
import fr.paris.lutece.plugins.calendar.business.OccurrenceEvent;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.business.category.Category;
import fr.paris.lutece.plugins.calendar.business.category.CategoryHome;
import fr.paris.lutece.plugins.calendar.business.stylesheet.CalendarStyleSheetHome;
import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.AgendaService;
import fr.paris.lutece.plugins.calendar.service.AgendaSubscriberService;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.plugins.calendar.service.EventImageResourceService;
import fr.paris.lutece.plugins.calendar.service.EventListService;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.plugins.calendar.service.search.CalendarSearchService;
import fr.paris.lutece.portal.business.stylesheet.StyleSheet;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This class is the main class of the XPage application of the plugin calendar.
 *
 */
public class CalendarApp implements XPageApplication
{
    //Templates
    private static final String TEMPLATE_CALENDAR = "skin/plugins/calendar/calendar.html";
    private static final String TEMPLATE_CALENDAR_LEGEND = "skin/plugins/calendar/calendar_legend.html";
    private static final String TEMPLATE_CALENDAR_MANAGE_EVENTS = "skin/plugins/calendar/calendar_manage_events.html";
    private static final String TEMPLATE_CREATE_EVENT_FRONT = "skin/plugins/calendar/create_event_front.html";
    private static final String TEMPLATE_MODIFY_EVENT_FRONT = "skin/plugins/calendar/modify_event_front.html";
    private static final String TEMPLATE_SEND_NOTIFICATION_MAIL = "skin/plugins/calendar/notification_email.html";
    private static final String TEMPLATE_DO_SEARCH_EVENTS = "skin/plugins/calendar/search/dosearch_event_results.html";
    private static final String TEMPLATE_SHOW_RESULT = "skin/plugins/calendar/search/show_result.html";
    private static final String TEMPLATE_SEARCH_EVENTS = "skin/plugins/calendar/search/search_events.html";
    private static final String TEMPLATE_SUBSCRIPTION_FORM = "skin/plugins/calendar/subscription_form.html";
    private static final String TEMPLATE_EMAIL_FRIEND = "skin/plugins/calendar/email_friend.html";
    private static final String TEMPLATE_DOWNLOAD_CALENDAR = "skin/plugins/calendar/download_calendar.html";
    private static final String TEMPLATE_RSS_CALENDAR = "skin/plugins/calendar/rss_calendar.html";

    //Actions
    private static final String ACTION_MANAGE_EVENTS = "manage_events";
    private static final String ACTION_ADD_EVENT = "add_event";
    private static final String ACTION_MODIFY_EVENT = "modify_event";
    private static final String ACTION_REMOVE_EVENT = "remove_event";
    private static final String ACTION_DO_CREATE_EVENT = "do_create_event";
    private static final String ACTION_DO_MODIFY_EVENT = "do_modify_event";
    private static final String ACTION_DO_REMOVE_EVENT = "do_remove_event";

    //Properties
    private static final String PROPERTY_PLUGIN_NAME = "calendar";

    //private static final String PROPERTY_FEATURE_URL = "?page=calendar&action=do_search";
    private static final String PROPERTY_UTIL_DOCUMENT_CLASS = "fr.paris.lutece.plugins.calendar.modules.document.web.DocumentCalendarUtils";
    public static final String PROPERTY_INVALID_MAIL_TITLE_MESSAGE = "calendar.siteMessage.invalid_mail.title";
    public static final String PROPERTY_INVALID_MAIL_ERROR_MESSAGE = "calendar.siteMessage.invalid_mail.message";
    private static final String PROPERTY_EMAIL_FRIEND_OBJECT = "calendar.friend.email.object";

    //Front Messages
    private static final String PROPERTY_CONFIRM_REMOVE_TITLE_MESSAGE = "calendar.siteMessage.confirmRemove.title";
    private static final String PROPERTY_CONFIRM_REMOVE_ALERT_MESSAGE = "calendar.siteMessage.confirmRemove.alertMessage";
    private static final String PROPERTY_INVALID_DATE_TITLE_MESSAGE = "calendar.siteMessage.invalidDate.title";
    private static final String PROPERTY_INVALID_DATE_MESSAGE = "calendar.siteMessage.invalidDate.alertMessage";
    private static final String PROPERTY_INVALID_TIME_TITLE_MESSAGE = "calendar.siteMessage.invalidTime.title";
    private static final String PROPERTY_INVALID_TIME_MESSAGE = "calendar.siteMessage.invalidTime.alertMessage";
    private static final String PROPERTY_CAPTCHA_INVALID_MESSAGE = "calendar.siteMessage.invalid_captcha.message";
    private static final String PROPERTY_CAPTCHA_INVALID_TITLE_MESSAGE = "calendar.siteMessage.invalid_captcha.title";
    private static final String PROPERTY_WEBMASTER_EMAIL = "email.webmaster";
    private static final String JSP_PAGE_PORTAL = "jsp/site/Portal.jsp";
    private static final String JSP_PAGE_RSS = "jsp/site/plugins/calendar/GetCalendarFile.jsp";

    //JSP
    private static final String URL_JSP_RETURN_SEND_FRIEND_MAIL = "../../Portal.jsp?page=calendar&action=";

    //Mark
    private static final String MARK_BASE_URL = "base_url";
    private static final String MARK_SENDER_MESSAGE = "sender_message";
    
    //Session
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( Constants.PROPERTY_EVENTS_PER_PAGE, 10 );
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    private CaptchaSecurityService _captchaService;
    

    /**
     * Returns the content of the page Contact. It is composed by a form which to capture the data to send a message to
     * a contact of the portal.
     *
     * @return the Content of the page Contact
     * @param request The http request
     * @param nMode The current mode
     * @param plugin The plugin object
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException A message exception treated on front
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws SiteMessageException
    {
        XPage page = new XPage(  );

        String strAction = request.getParameter( Constants.PARAMETER_ACTION );
        String strPluginName = request.getParameter( Constants.PARAMETER_PAGE );
        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );
        AgendaResource agendaResource = AgendaService.getInstance(  ).getAgendaResource( strCalendarId );
        plugin = PluginService.getPlugin( strPluginName );

        if ( ( strAction != null ) && strAction.equalsIgnoreCase( ACTION_MANAGE_EVENTS ) )
        {
            page.setContent( getManageEvents( strCalendarId, request, plugin ).getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MANAGE_EVENTS,
                    request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MANAGE_EVENTS,
                    request.getLocale(  ) ) );

            return page;
        }

        if ( ( strAction != null ) && strAction.equalsIgnoreCase( ACTION_ADD_EVENT ) )
        { //return unregistered form if unregistered user wants to acces application management form

            page.setContent( getCreateEvent( strCalendarId, request, plugin ).getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_CREATE_EVENT,
                    request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_CREATE_EVENT,
                    request.getLocale(  ) ) );

            return page;
        }

        if ( ( strAction != null ) && strAction.equalsIgnoreCase( ACTION_DO_CREATE_EVENT ) )
        {
            doCreateEvent( strCalendarId, request, plugin );
            page.setContent( getManageEvents( strCalendarId, request, plugin ).getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MANAGE_EVENTS,
                    request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MANAGE_EVENTS,
                    request.getLocale(  ) ) );

            return page;
        }

        if ( ( strAction != null ) && strAction.equalsIgnoreCase( ACTION_MODIFY_EVENT ) )
        {
        	if ( !verifiyUserAccess( request, plugin ) )
            {
            	SiteMessageService.setMessage( request, Messages.USER_ACCESS_DENIED, new String[] { Constants.EMPTY_STRING }, 
            			Constants.EMPTY_STRING, Constants.EMPTY_NULL, Constants.EMPTY_STRING, SiteMessage.TYPE_STOP );
            }
            page.setContent( getModifyEvent( request, plugin ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MODIFY_EVENT,
                    request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MODIFY_EVENT,
                    request.getLocale(  ) ) );

            return page;
        }

        if ( ( strAction != null ) && strAction.equalsIgnoreCase( ACTION_DO_MODIFY_EVENT ) )
        {
        	if ( !verifiyUserAccess( request, plugin ) )
            {
            	SiteMessageService.setMessage( request, Messages.USER_ACCESS_DENIED, new String[] { Constants.EMPTY_STRING }, 
            			Constants.EMPTY_STRING, Constants.EMPTY_NULL, Constants.EMPTY_STRING, SiteMessage.TYPE_STOP );
            }
            doModifyEvent( request, plugin );
            page.setContent( getManageEvents( strCalendarId, request, plugin ).getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MANAGE_EVENTS,
                    request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MANAGE_EVENTS,
                    request.getLocale(  ) ) );

            return page;
        }

        if ( ( strAction != null ) && strAction.equalsIgnoreCase( ACTION_DO_REMOVE_EVENT ) )
        {
            if ( !verifiyUserAccess( request, plugin ) )
            {
            	SiteMessageService.setMessage( request, Messages.USER_ACCESS_DENIED, new String[] { Constants.EMPTY_STRING }, 
            			Constants.EMPTY_STRING, Constants.EMPTY_NULL, Constants.EMPTY_STRING, SiteMessage.TYPE_STOP );
            }
            int nCalendarId = Integer.parseInt( strCalendarId );
        	int nEventId = Integer.parseInt( request.getParameter( Constants.PARAMETER_EVENT_ID ) );
        	
            CalendarHome.removeEvent( nCalendarId, nEventId, plugin );

            page.setContent( getManageEvents( strCalendarId, request, plugin ).getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MANAGE_EVENTS,
                    request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MANAGE_EVENTS,
                    request.getLocale(  ) ) );

            return page;
        }

        if ( ( strAction != null ) && strAction.equalsIgnoreCase( ACTION_REMOVE_EVENT ) )
        {
        	if ( !verifiyUserAccess( request, plugin ) )
            {
            	SiteMessageService.setMessage( request, Messages.USER_ACCESS_DENIED, new String[] { Constants.EMPTY_STRING }, 
            			Constants.EMPTY_STRING, Constants.EMPTY_NULL, Constants.EMPTY_STRING, SiteMessage.TYPE_STOP );
            }
        	getRemoveEvent( request );
        }

        //Get the calendar subscription page
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_GET_SUBSCRIPTION_PAGE ) )
        {
            Plugin pluginCalendar = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
            
            boolean bIsCaptchaEnabled = PluginService.isPluginEnable( Constants.PLUGIN_JCAPTCHA );

            //String strRunAppJspUrl = AppPropertiesService.getProperty( Constants.PROPERTY_RUNAPP_JSP_URL );
            String strBaseUrl = AppPathService.getBaseUrl( request );
            UrlItem url = new UrlItem( strBaseUrl + JSP_PAGE_PORTAL );
            url.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
            url.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_VERIFY_SUBSCRIBE );

            HashMap<String, Object> model = new HashMap<String, Object>(  );
            model.put( Constants.MARK_JSP_URL, url.getUrl(  ) );
            model.put( Constants.MARK_LOCALE, request.getLocale(  ) );
            model.put( Constants.MARK_CALENDARS_LIST, getListAgenda( request, pluginCalendar ) );
            model.put( Constants.MARK_IS_ACTIVE_CAPTCHA, bIsCaptchaEnabled );

            if ( bIsCaptchaEnabled )
            {
            	_captchaService = new CaptchaSecurityService(  );
	            model.put( Constants.MARK_CAPTCHA, _captchaService.getHtmlCode(  ) );
            }

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SUBSCRIPTION_FORM, request.getLocale(  ),
                    model );

            page.setContent( template.getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_SUBSCRIPTION_TITLE,
                    request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_SUBSCRIPTION_TITLE,
                    request.getLocale(  ) ) );

            return page;
        }

        //Get the "email to a friend" page
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_GET_FRIEND_EMAIL_PAGE ) )
        {
            String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
            strCalendarId = request.getParameter( Constants.PARAM_AGENDA );

            HashMap<String, Object> model = new HashMap<String, Object>(  );
            model.put( Constants.MARK_LOCALE, request.getLocale(  ) );
            model.put( Constants.MARK_EVENT_ID, Integer.parseInt( strEventId ) );
            model.put( Constants.MARK_CALENDAR_ID, Integer.parseInt( strCalendarId ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EMAIL_FRIEND, request.getLocale(  ), model );

            page.setContent( template.getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_EMAIL_FRIEND_TITLE,
                    request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_EMAIL_FRIEND_TITLE,
                    request.getLocale(  ) ) );

            return page;
        }

        //Get the download page
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_GET_DOWNLOAD_PAGE ) )
        {
            Plugin pluginCalendar = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
            HashMap<String, Object> model = new HashMap<String, Object>(  );
            model.put( Constants.MARK_LOCALE, request.getLocale(  ) );
            model.put( Constants.MARK_CALENDARS_LIST, getListAgenda( request, pluginCalendar ) );
            model.put( Constants.MARK_EXPORT_STYLESHEET_LIST, getExportSheetList( request, pluginCalendar ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DOWNLOAD_CALENDAR, request.getLocale(  ),
                    model );

            page.setContent( template.getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_DOWNLOAND_TITLE,
                    request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_DOWNLOAND_TITLE,
                    request.getLocale(  ) ) );

            return page;
        }

        //send a notification to a friend
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_SEND_FRIEND_EMAIL ) )
        {
            AgendaSubscriberService.getInstance(  ).sendFriendMail( request );
        }
        
        //Do verify subscription
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_VERIFY_SUBSCRIBE ) )
        {
	        String strEmail = request.getParameter( Constants.PARAMETER_EMAIL );
	        if ( _captchaService != null && !_captchaService.validate( request ) )
	        {
		        //invalid captcha
		        SiteMessageService.setMessage( request, PROPERTY_CAPTCHA_INVALID_MESSAGE,
		        		PROPERTY_CAPTCHA_INVALID_TITLE_MESSAGE, SiteMessage.TYPE_INFO );
	        }
	        else if( ( strEmail == null ) || !StringUtil.checkEmail( strEmail ) )
	        {
	        	SiteMessageService.setMessage( request, PROPERTY_INVALID_MAIL_ERROR_MESSAGE,
	        			PROPERTY_INVALID_MAIL_TITLE_MESSAGE, SiteMessage.TYPE_STOP );
	        }
	        else
	        {
	        	AgendaSubscriberService.getInstance(  ).doNotificationSubscription( request );
	        }
        }

        //Confirm unsubscription  
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_CONFIRM_UNSUBSCRIBE ) )
        {
            AgendaSubscriberService.getInstance(  ).doConfirmUnSubscribe( request );
        }

        //Do unsubscription
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_UNSUBSCRIBE ) )
        {
            AgendaSubscriberService.getInstance(  ).doUnSubscribe( request );
        }

        //Search action
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_SEARCH ) )
        {
            HashMap<String, Object> model = new HashMap<String, Object>(  );

            model.put( Constants.MARK_NB_ITEMS_PER_PAGE, "" + _nDefaultItemsPerPage );
            model.put( Constants.MARK_CALENDARS_LIST,
                getListAgenda( request, PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME ) ) );
            model.put( Constants.MARK_AGENDA, " " );
            model.put( Constants.MARK_LOCALE, request.getLocale(  ) );

            Collection<Category> categoryList = CategoryHome.findAll( plugin );
            model.put( Constants.MARK_CATEGORY_LIST, getReferenceListCategory( categoryList ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SEARCH_EVENTS, request.getLocale(  ), model );

            page.setContent( template.getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_SEARCH, request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_SEARCH,
                    request.getLocale(  ) ) );

            return page;
        }

        //Process search
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_DO_SEARCH ) )
        {
            Plugin pluginCalendar = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
            String strQuery = request.getParameter( Constants.PARAMETER_QUERY );

            //String strAgenda = request.getParameter( Constants.PARAM_AGENDA );
            String[] arrayCategory = request.getParameterValues( Constants.PARAMETER_CATEGORY );
            String[] arrayCalendar = request.getParameterValues( Constants.PARAMETER_CALENDAR_ID );

            String strDateBegin = request.getParameter( Constants.PARAMETER_DATE_START );
            String strDateEnd = request.getParameter( Constants.PARAMETER_DATE_END );
            String strPeriod = request.getParameter( Constants.PARAMETER_PERIOD );

            strPeriod = ( strPeriod == null ) ? Integer.toString( Constants.PROPERTY_PERIOD_NONE ) : strPeriod;

            String strAgenda = null;

            String strBaseUrl = AppPathService.getBaseUrl( request );
            UrlItem url = new UrlItem( JSP_PAGE_PORTAL );
            url.addParameter( Constants.PARAMETER_QUERY, strQuery == null ? Constants.EMPTY_STRING : strQuery );
            url.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
            url.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_DO_SEARCH );

            if ( arrayCalendar != null )
            {
                for ( String strAgendaId : arrayCalendar )
                {
                    url.addParameter( Constants.PARAMETER_CALENDAR_ID, strAgendaId );
                }
            }
            else
            {
            	arrayCalendar = Utils.getCalendarIds( request );
            }

            url.addParameter( Constants.PARAMETER_DATE_START, strDateBegin );
            url.addParameter( Constants.PARAMETER_DATE_END, strDateEnd );
            url.addParameter( Constants.PARAMETER_PERIOD, strPeriod );

            List<Event> listEvent = null;

            Date dateBegin = null;
            Date dateEnd = null;

            switch ( Integer.parseInt( strPeriod ) )
            {
                case Constants.PROPERTY_PERIOD_NONE:
                    break;

                case Constants.PROPERTY_PERIOD_TODAY:
                    dateBegin = dateEnd = new Date(  );
                    strDateBegin = strDateEnd = DateUtil.getDateString( new Date(  ), request.getLocale(  ) );

                    break;

                case Constants.PROPERTY_PERIOD_WEEK:

                    Calendar calendar = new GregorianCalendar(  );
                    Calendar calendarFirstDay = new GregorianCalendar(  );
                    Calendar calendarLastDay = new GregorianCalendar(  );

                    int nDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );

                    if ( nDayOfWeek == 1 )
                    {
                        nDayOfWeek = 8;
                    }

                    calendarFirstDay = calendar;
                    calendarFirstDay.add( Calendar.DATE, Calendar.MONDAY - nDayOfWeek );
                    calendarLastDay = (GregorianCalendar) calendarFirstDay.clone(  );
                    calendarLastDay.add( Calendar.DATE, 6 );
                    dateBegin = calendarFirstDay.getTime(  );
                    dateEnd = calendarLastDay.getTime(  );
                    strDateBegin = DateUtil.getDateString( dateBegin, request.getLocale(  ) );
                    strDateEnd = DateUtil.getDateString( dateEnd, request.getLocale(  ) );

                    break;

                case Constants.PROPERTY_PERIOD_RANGE:

                    if ( ( strDateBegin != null ) || ( strDateEnd != null ) )
                    {
                        if ( !strDateBegin.equals( "" ) )
                        {
                            dateBegin = DateUtil.formatDate( strDateBegin, request.getLocale(  ) );

                            if ( ( dateBegin == null ) || !Utils.isValidDate( dateBegin ) )
                            {
                                errorDateFormat( request );
                            }
                        }

                        if ( !strDateEnd.equals( "" ) )
                        {
                            dateEnd = DateUtil.formatDate( strDateEnd, request.getLocale(  ) );

                            if ( ( dateEnd == null ) || !Utils.isValidDate( dateEnd ) )
                            {
                                errorDateFormat( request );
                            }
                        }

                        if ( dateEnd.before( dateBegin ) )
                        {
                            errorDateFormat( request );
                        }
                    }
                    else
                    {
                        errorDateFormat( request );
                    }

                    break;
            }

            listEvent = CalendarSearchService.getInstance(  )
                                             .getSearchResults( arrayCalendar, arrayCategory, strQuery, dateBegin,
                    dateEnd, request, pluginCalendar );

            _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
            _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                    _nDefaultItemsPerPage );

            if ( listEvent == null )
            {
                listEvent = new ArrayList<Event>(  );
            }

            HashMap<String, Object> model = new HashMap<String, Object>(  );

            Paginator paginator = new Paginator( listEvent, _nItemsPerPage, url.getUrl(  ),
                    Constants.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

            //if one calendar is selected            
            if ( ( arrayCalendar != null ) && ( arrayCalendar.length == 1 ) )
            {
                strAgenda = arrayCalendar[0];
            }

            UrlItem urlSubscription = new UrlItem( strBaseUrl + JSP_PAGE_PORTAL );
            urlSubscription.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
            urlSubscription.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_GET_SUBSCRIPTION_PAGE );
            urlSubscription.addParameter( Constants.PARAM_AGENDA, strAgenda );

            UrlItem urlDownload = new UrlItem( strBaseUrl + JSP_PAGE_PORTAL );
            urlDownload.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
            urlDownload.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_GET_DOWNLOAD_PAGE );

            UrlItem urlRss = new UrlItem( strBaseUrl + JSP_PAGE_RSS );
            urlRss.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_RSS );

            if ( arrayCalendar != null )
            {
                for ( String strAgendaId : arrayCalendar )
                {
                    urlRss.addParameter( Constants.PARAMETER_CALENDAR_ID, strAgendaId );
                }
            }

            if ( arrayCategory != null )
            {
                for ( String strCategoryId : arrayCategory )
                {
                    urlRss.addParameter( Constants.PARAMETER_CATEGORY, strCategoryId );
                }
            }

            ReferenceList listAgendas = getListAgenda( request, pluginCalendar );

            if ( arrayCalendar != null )
            {
                listAgendas.checkItems( arrayCalendar );
            }

            Collection<Category> categoryList = CategoryHome.findAll( plugin );
            ReferenceList listCategorys = getReferenceListCategory( categoryList );

            if ( arrayCategory != null )
            {
                listCategorys.checkItems( arrayCategory );
            }

            // Evol List occurrences
            List<List<OccurrenceEvent>> listOccurrences = new ArrayList<List<OccurrenceEvent>>(  );

            for ( Event event : listEvent )
            {
                List<OccurrenceEvent> listOccurrence = CalendarHome.findOccurrencesList( event.getIdCalendar(  ),
                        event.getId(  ), 1, plugin );
                listOccurrences.add( listOccurrence );
            }

            CalendarUserOptions options = getUserOptions( request );
            options.setShowSearchEngine( Boolean.TRUE );
            
            boolean bIsSelectedDay = false;
            String strDate;
            if  ( strDateBegin != null && !strDateBegin.equals( Constants.EMPTY_STRING ) && !strDateBegin.equals( Constants.NULL ) )
            {
            	strDate = Utils.getDate( DateUtil.formatDateLongYear( strDateBegin, request.getLocale(  ) ) );
            	if ( strDateBegin.equals( strDateEnd ) )
	            {
            		bIsSelectedDay = true;
	            }
            }
            else
            {
            	strDate = Utils.getDate( new Date(  ) );
            }

            MultiAgenda agendaWithOccurences = getAgendaWithOccurrences( request );

            model.put( Constants.MARK_QUERY, ( strQuery != null ) ? strQuery : "" );
            model.put( Constants.MARK_SUBSCRIPTION_PAGE, urlSubscription.getUrl(  ) );
            model.put( Constants.MARK_DOWNLOAD_PAGE, urlDownload.getUrl(  ) );
            model.put( Constants.MARK_RSS_PAGE, urlRss.getUrl(  ) );
            model.put( Constants.MARK_DATE_START, ( strDateBegin != null ) ? strDateBegin : "" );
            model.put( Constants.MARK_DATE_END, ( strDateEnd != null ) ? strDateEnd : "" );
            model.put( Constants.MARK_PERIOD, ( strPeriod != null ) ? strPeriod : "" );
            model.put( Constants.MARK_EVENTS_LIST, paginator.getPageItems(  ) );
            model.put( Constants.MARK_PAGINATOR, paginator );
            model.put( Constants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
            model.put( Constants.MARK_CALENDARS_LIST, listAgendas );
            model.put( Constants.MARK_AGENDA, ( strAgenda != null ) ? strAgenda : "" );
            model.put( Constants.MARK_LOCALE, request.getLocale(  ) );
            model.put( Constants.MARK_CATEGORY_LIST, listCategorys );
            model.put( Constants.MARK_OCCURRENCES_LIST, listOccurrences );
            model.put( Constants.MARK_SMALL_MONTH_CALENDAR, SmallMonthCalendar.getSmallMonthCalendar( 
                    		strDate, agendaWithOccurences, options, bIsSelectedDay ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DO_SEARCH_EVENTS, request.getLocale(  ),
                    model );

            page.setContent( template.getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_SEARCH_RESULT,
                    request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_SEARCH,
                    request.getLocale(  ) ) );

            return page;
        }

        //Show the detail of a result search
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_SHOW_RESULT ) )
        {
            String strDocumentId = request.getParameter( Constants.PARAMETER_DOCUMENT_ID );
            String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
            Plugin pluginCalendar = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
            String strPathResult = "";

            if ( ( strEventId != null ) && !strEventId.equals( Constants.EMPTY_STRING ) )
            {
                int nEventId = -1;

                try
                {
                    nEventId = Integer.parseInt( strEventId );
                }
                catch ( NumberFormatException e )
                {
                	AppLogService.error( e );
                }

                SimpleEvent event = CalendarHome.findEvent( nEventId, pluginCalendar );

                HtmlTemplate template = new HtmlTemplate(  );

                if ( event != null )
                {
                    int nDocumentId = -1;

                    if ( ( strDocumentId != null ) && !strDocumentId.equals( Constants.EMPTY_STRING ) )
                    {
                        try
                        {
                            nDocumentId = Integer.parseInt( strDocumentId );
                        }
                        catch ( NumberFormatException e )
                        {
                        	AppLogService.error( e );
                        }
                    }
                    else if ( event.getDocumentId(  ) > -1 )
                    {
                        nDocumentId = event.getDocumentId(  );
                    }

                    String strTemplateDocument = "";
                    Plugin module = PluginService.getPlugin( Constants.PROPERTY_MODULE_CALENDAR );

                    if ( ( module != null ) && module.isInstalled(  ) && ( nDocumentId > 0 ) )
                    {
                        try
                        {
                            IAppUtils documentUtil = (IAppUtils) Class.forName( PROPERTY_UTIL_DOCUMENT_CLASS )
                                                                      .newInstance(  );
                            strTemplateDocument = documentUtil.getTemplateDocument( nDocumentId, request );
                        }
                        catch ( Exception e )
                        {
                        	AppLogService.error( e );
                        }
                    }

                    CalendarUserOptions options = getUserOptions( request );
                    options.setShowSearchEngine( Boolean.TRUE );

                    AgendaResource agenda = CalendarHome.findAgendaResource( event.getIdCalendar(  ), plugin );

                    MultiAgenda agendaWithOccurences = getAgendaWithOccurrences( request );

                    String strRole = agenda.getRole(  );
                    String strRoleManager = agenda.getRoleManager(  );

                    boolean bIsUserAuthorized = true;

                    if ( strRole != null && !Constants.EMPTY_STRING.equals( strRole ) && !Constants.PROPERTY_ROLE_NONE.equals( strRole ) )
                    {
                        if ( SecurityService.isAuthenticationEnable(  ) )
                        {
                            if ( !( SecurityService.getInstance(  ).isUserInRole( request, strRole ) || 
                            		SecurityService.getInstance(  ).isUserInRole( request, strRoleManager ) ) )
                            {
                            	bIsUserAuthorized = false;
                            }
                        }
                    }
                    
                    Map<String, Object> model = new HashMap<String, Object>(  );
                    
                    if ( bIsUserAuthorized )
                    {
                    	String strBaseUrl = AppPathService.getBaseUrl( request );
                        UrlItem urlEmailFriend = new UrlItem( strBaseUrl + JSP_PAGE_PORTAL );
                        urlEmailFriend.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
                        urlEmailFriend.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_GET_FRIEND_EMAIL_PAGE );
                        
                        UrlItem urlRss = new UrlItem( strBaseUrl + JSP_PAGE_RSS );
                        urlRss.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_RSS );
                                            
                        UrlItem urlSubscription = new UrlItem( strBaseUrl + JSP_PAGE_PORTAL );
                        urlSubscription.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
                        urlSubscription.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_GET_SUBSCRIPTION_PAGE );
                                            
                        UrlItem urlDownload = new UrlItem( strBaseUrl + JSP_PAGE_PORTAL );
                        urlDownload.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
                        urlDownload.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_GET_DOWNLOAD_PAGE );
                        
                        event.setImageUrl( EventImageResourceService.getInstance(  ).getResourceImageEvent( event.getId(  ) ) );
                        model.put( Constants.MARK_EVENT, event );
                        model.put( Constants.MARK_DOCUMENT, strTemplateDocument );
                        model.put( Constants.MARK_AGENDA, agenda );
                        model.put( Constants.MARK_EMAIL_FRIEND_PAGE, urlEmailFriend.getUrl(  ) );
                        model.put( Constants.MARK_RSS_PAGE, urlRss.getUrl(  ) );
                        model.put( Constants.MARK_SUBSCRIPTION_PAGE, urlSubscription.getUrl(  ) );
                        model.put( Constants.MARK_DOWNLOAD_PAGE, urlDownload.getUrl(  ) );
                    }
                    
                    model.put( Constants.MARK_IS_AUTHORIZED, bIsUserAuthorized );
                    model.put( Constants.MARK_SMALL_MONTH_CALENDAR, SmallMonthCalendar.getSmallMonthCalendar( 
                        		Utils.getDate( event.getDate(  ) ), agendaWithOccurences, options, true ) );
                    template = AppTemplateService.getTemplate( TEMPLATE_SHOW_RESULT, request.getLocale(  ), model );
                    strPathResult = event.getTitle(  );
                }

                page.setContent( template.getHtml(  ) );
                page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_SEARCH_RESULT,
                        request.getLocale(  ) ) );
                page.setPathLabel( strPathResult );

                return page;
            }
        }

        //Get the RSS form
        if ( ( strAction != null ) && strAction.equalsIgnoreCase( Constants.ACTION_RSS ) )
        {
            //Retrieve category list
            Collection<Category> categoryList = CategoryHome.findAll( plugin );
            Plugin pluginCalendar = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
            HashMap<String, Object> model = new HashMap<String, Object>(  );
            model.put( Constants.MARK_LOCALE, request.getLocale(  ) );
            model.put( Constants.MARK_CALENDARS_LIST, getListAgenda( request, pluginCalendar ) );
            model.put( Constants.MARK_CATEGORY_LIST, getReferenceListCategory( categoryList ) );
            model.put( Constants.MARK_CATEGORY_DEFAULT_LIST, "" );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RSS_CALENDAR, request.getLocale(  ), model );

            page.setContent( template.getHtml(  ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_RSS_TITLE, request.getLocale(  ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_RSS_TITLE, request.getLocale(  ) ) );

            return page;
        }

        // Gets calendar infos from the request parameters and session
        CalendarView view = getView( request );
        EventList eventlist = getEventList( view.getType(  ) );

        //MultiAgenda agendaWithEvents = getAgenda( request );
        MultiAgenda agendaWithOccurences = getAgendaWithOccurrences( request );
        String strDate = getDate( request );
        CalendarUserOptions options = getUserOptions( request );
        options.setShowSearchEngine( Boolean.TRUE );
        
        boolean bIsSelectedDay = false;
        if ( view.getType(  ) == CalendarView.TYPE_DAY )
        {
        	bIsSelectedDay = true;
        }

        // Load and fill the page template
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( Constants.MARK_PREVIOUS, view.getPrevious( strDate ) );
        model.put( Constants.MARK_NEXT, view.getNext( strDate ) );
        model.put( Constants.MARK_TITLE, view.getTitle( strDate, options ) );
        model.put( Constants.MARK_DATE, strDate );
        model.put( Constants.MARK_LEGEND, getLegend( request, agendaWithOccurences, options ) );
        model.put( Constants.MARK_VIEW_CALENDAR, view.getCalendarView( strDate, agendaWithOccurences, options, request ) );
        model.put( Constants.MARK_SMALL_MONTH_CALENDAR,
            SmallMonthCalendar.getSmallMonthCalendar( strDate, agendaWithOccurences, options, bIsSelectedDay ) );

        // Display event list if there is some events to display
        String strEventList = Constants.EMPTY_STRING;

        //get events
        if ( ( eventlist != null ) && ( agendaWithOccurences.getAgendas(  ).size(  ) != 0 ) )
        {
            strEventList = eventlist.getEventList( strDate, agendaWithOccurences, options.getLocale(  ), request );
        }

        model.put( Constants.MARK_EVENT_LIST, strEventList );

        String strRunAppJspUrl = AppPropertiesService.getProperty( Constants.PROPERTY_RUNAPP_JSP_URL );
        model.put( Constants.MARK_JSP_URL, strRunAppJspUrl );

        // Set XPage data
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CALENDAR, options.getLocale(  ), model );
        page.setContent( template.getHtml(  ) );
        page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_TITLE, options.getLocale(  ) ) +
            view.getTitle( strDate, options ) );
        page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PATH, options.getLocale(  ) ) +
            view.getPath( strDate, options ) );

        return page;
    }

    /**
     * Get the date from the request parameter
     * @param request The HTTP request
     * @return The string date code
     */
    private String getDate( HttpServletRequest request )
    {
        String strDate = request.getParameter( Constants.PARAM_DATE );

        if ( !Utils.isValid( strDate ) )
        {
            strDate = Utils.getDateToday(  );
        }

        return strDate;
    }

    /**
     * Get the calendar view from the request parameter or stored in the session
     * @param request The HTTP request
     * @return A Calendar View object
     */
    private CalendarView getView( HttpServletRequest request )
    {
        CalendarView view = null;
        String strView = request.getParameter( Constants.PARAM_VIEW );

        if ( strView != null )
        {
            if ( strView.equals( Constants.VIEW_DAY ) )
            {
                view = new DayCalendarView(  );
            }
            else if ( strView.equals( Constants.VIEW_WEEK ) )
            {
                view = new WeekCalendarView(  );
            }
            else if ( strView.equals( Constants.VIEW_MONTH ) )
            {
                view = new MonthCalendarView(  );
            }
            else
            {
                // Default view
                view = new MonthCalendarView(  );
            }

            HttpSession session = request.getSession( true );
            session.setAttribute( Constants.ATTRIBUTE_CALENDAR_VIEW, view );
        }
        else
        {
            HttpSession session = request.getSession(  );
            CalendarView viewCurrentSession = (CalendarView) session.getAttribute( Constants.ATTRIBUTE_CALENDAR_VIEW );

            if ( viewCurrentSession != null )
            {
                view = viewCurrentSession;
            }
            else
            {
                // Default view
                view = new MonthCalendarView(  );
            }
        }

        return view;
    }

    /**
     * Gets the eventlist associated to the current view type
     * @param nViewType The View type
     * @return Return the eventlist defined in the calendar.properties
     */
    private EventList getEventList( int nViewType )
    {
        EventList eventlist = null;
        String strEventListKeyName = null;

        switch ( nViewType )
        {
            case CalendarView.TYPE_DAY:
                strEventListKeyName = AppPropertiesService.getProperty( Constants.PROPERTY_EVENTLIST_VIEW_DAY );

                break;

            case CalendarView.TYPE_WEEK:
                strEventListKeyName = AppPropertiesService.getProperty( Constants.PROPERTY_EVENTLIST_VIEW_WEEK );

                break;

            case CalendarView.TYPE_MONTH:
                strEventListKeyName = AppPropertiesService.getProperty( Constants.PROPERTY_EVENTLIST_VIEW_MONTH );

                break;

            default:
        }

        if ( strEventListKeyName != null )
        {
            eventlist = EventListService.getInstance(  ).getEventList( strEventListKeyName );
        }

        return eventlist;
    }

    /**
     * Get the agenda from the request parameter or stored in the session
     * @param request The HTTP request
     * @return An agenda object
     */
    private MultiAgenda getAgenda( HttpServletRequest request )
    {
        MultiAgenda agenda = null;
        String[] strAgendas = request.getParameterValues( Constants.PARAM_AGENDA );

        if ( strAgendas != null )
        {
            MultiAgenda agendaCombined = new MultiAgenda(  );

            for ( int i = 0; i < strAgendas.length; i++ )
            {
                String strAgendaKeyName = strAgendas[i];
                AgendaResource agendaResource = AgendaService.getInstance(  ).getAgendaResource( strAgendaKeyName );
                Agenda a = null;

                if ( agendaResource != null )
                {
                    a = agendaResource.getAgenda(  );
                }

                if ( a != null )
                {
                    // Check security access
                    String strRole = AgendaService.getInstance(  ).getAgendaResource( strAgendaKeyName ).getRole(  );

                    if ( ( strRole != null ) && ( !strRole.equals( "" ) ) &&
                            ( !strRole.equals( Constants.PROPERTY_ROLE_NONE ) ) )
                    {
                        if ( SecurityService.isAuthenticationEnable(  ) )
                        {
                            if ( SecurityService.getInstance(  ).isUserInRole( request, strRole ) )
                            {
                                agendaCombined.addAgenda( a );
                            }
                        }
                    }
                    else
                    {
                        agendaCombined.addAgenda( a );
                    }
                }
            }

            agenda = agendaCombined;

            HttpSession session = request.getSession( true );
            session.setAttribute( Constants.ATTRIBUTE_CALENDAR_AGENDA, agenda );
        }
        else
        {
            HttpSession session = request.getSession(  );
            MultiAgenda agendaCurrentSession = (MultiAgenda) session.getAttribute( Constants.ATTRIBUTE_CALENDAR_AGENDA );

            if ( agendaCurrentSession != null )
            {
                agenda = agendaCurrentSession;
            }
            else
            {
                agenda = new MultiAgenda(  );
            }
        }

        return agenda;
    }

    /**
     * Get the agenda from the request parameter or stored in the session
     * @param request The HTTP request
     * @return An agenda object
     */
    private MultiAgenda getAgendaWithOccurrences( HttpServletRequest request )
    {
        MultiAgenda agenda = null;
        String[] strAgendas = request.getParameterValues( Constants.PARAM_AGENDA );

        if ( strAgendas != null )
        {
            MultiAgenda agendaCombined = new MultiAgenda(  );

            for ( int i = 0; i < strAgendas.length; i++ )
            {
                String strAgendaKeyName = strAgendas[i];

                Agenda a = Utils.getAgendaWithOccurrences( strAgendaKeyName, request );

                agendaCombined.addAgenda( a );
            }

            agenda = agendaCombined;

            HttpSession session = request.getSession( true );
            session.setAttribute( Constants.ATTRIBUTE_CALENDAR_AGENDA_OCCURRENCES, agenda );
        }
        else
        {
        	/*
            HttpSession session = request.getSession(  );
            MultiAgenda agendaCurrentSession = (MultiAgenda) session.getAttribute( Constants.ATTRIBUTE_CALENDAR_AGENDA_OCCURRENCES );

            if ( agendaCurrentSession != null )
            {
                agenda = agendaCurrentSession;
            }
            else
            {
                agenda = new MultiAgenda(  );
            }*/
        	Plugin pluginCalendar = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
        	List<AgendaResource> listAgendaResources = getAgendaResources( request, pluginCalendar );
        	MultiAgenda agendaCombined = new MultiAgenda(  );
        	for ( AgendaResource agendaResource : listAgendaResources )
        	{
        		Agenda a = Utils.getAgendaWithOccurrences( agendaResource.getAgenda(  ).getKeyName(  ), request );

                agendaCombined.addAgenda( a );
        	}
        	
        	agenda = agendaCombined;

            HttpSession session = request.getSession( true );
            session.setAttribute( Constants.ATTRIBUTE_CALENDAR_AGENDA_OCCURRENCES, agenda );
        }

        return agenda;
    }

    /**
     * Get the agenda from the request parameter or stored in the session
     * @param request The HTTP request
     * @return An agenda object
     */
    private List<AgendaResource> getAgendaResources( HttpServletRequest request, Plugin plugin )
    {
        List<AgendaResource> agendaCombined = new ArrayList<AgendaResource>(  );

        List<AgendaResource> listCalendar = Utils.getAgendaResourcesWithOccurrences(  );

        for ( AgendaResource a : listCalendar )
        {
            // Check security access
            String strRole = a.getRole(  );
            String strRoleManager = a.getRoleManager(  );

            if ( ( strRole != null ) && ( !strRole.equals( "" ) ) && ( !strRole.equals( Constants.PROPERTY_ROLE_NONE ) ) )
            {
                if ( SecurityService.isAuthenticationEnable(  ) )
                {
                    if ( SecurityService.getInstance(  ).isUserInRole( request, strRole ) || 
                    		SecurityService.getInstance(  ).isUserInRole( request, strRoleManager ) )
                    {
                        agendaCombined.add( a );
                    }
                }
            }
            else
            {
                agendaCombined.add( a );
            }
        }

        return agendaCombined;
    }

    /**
     * Get user options from the request parameter or stored in the session or in cookies
     * @param request The HTTP request
     * @return A CalendarUserOptions
     */
    private CalendarUserOptions getUserOptions( HttpServletRequest request )
    {
        CalendarUserOptions options = new CalendarUserOptions(  );
        options.setLocale( request.getLocale(  ) );
        options.setDayOffDisplayed( true );

        return options;
    }

    /**
     * Build the legend of all agenda selected
     * @param multiAgenda A multi agenda
     * @param options Options storing display settings
     * @param request The http request
     * @return The HTML code of the Legend
     */
    private String getLegend( HttpServletRequest request, MultiAgenda multiAgenda, CalendarUserOptions options )
    {
        String strLegend = "";

        if ( ( multiAgenda != null ) && !multiAgenda.getAgendas(  ).isEmpty(  ) )
        {
            HashMap<String, Object> model = new HashMap<String, Object>(  );

            List<Agenda> listAgendas = multiAgenda.getAgendas(  );
            List<AgendaResource> listAgendaResource = new ArrayList<AgendaResource>(  );

            for ( Agenda agenda : listAgendas )
            {
            	if ( agenda != null )
            	{
            		AgendaResource agendaResource = AgendaService.getInstance(  ).getAgendaResource( agenda.getKeyName(  ) );
                    listAgendaResource.add( agendaResource );
            	}
            }

            model.put( Constants.MARK_AGENDA_RESOURCE_LIST, listAgendaResource );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CALENDAR_LEGEND, options.getLocale(  ),
                    model );
            strLegend = template.getHtml(  );
        }

        return strLegend;
    }

    /**
     * Returns events management popup
     *
     * @param request The Http request
     * @return Html form
     */
    public String getManageEvents( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        //Filtering by management role and resource type
        //List all the agendas
        CalendarUserOptions options = getUserOptions( request );
        MultiAgenda multiAgenda = getAgenda( request );
        List<Agenda> listAgendas = multiAgenda.getAgendas(  );
        List<AgendaResource> listAgendaResource = new ArrayList<AgendaResource>(  );
        String strReadWrite = AppPropertiesService.getProperty( Constants.PROPERTY_READ_WRITE );

        for ( Agenda agenda : listAgendas )
        {
            AgendaResource agendaResource = AgendaService.getInstance(  ).getAgendaResource( agenda.getKeyName(  ) );
            String strRessourceType = agendaResource.getResourceType(  );
            String strManagerRole = agendaResource.getRoleManager(  );

            if ( strRessourceType.equals( strReadWrite ) )
            {
                // Check security access
                if ( ( strManagerRole != null ) && ( !strManagerRole.equals( "" ) ) &&
                        ( !strManagerRole.equals( Constants.PROPERTY_ROLE_NONE ) ) )
                {
                    if ( SecurityService.isAuthenticationEnable(  ) )
                    {
                        if ( SecurityService.getInstance(  ).isUserInRole( request, strManagerRole ) )
                        {
                            listAgendaResource.add( agendaResource );
                        }
                    }
                }
            }
        }

        model.put( Constants.MARK_AGENDA_RESOURCE_LIST, listAgendaResource );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CALENDAR_MANAGE_EVENTS, options.getLocale(  ),
                model );
        template.getHtml(  );

        return template.getHtml(  );
    }

    /**
     * Returns the list of agenda resources which can be managed by a
     * an identified user
     *
     * @param request The Http request
     * @return A list of agenda resources
     */
    public List<AgendaResource> getListEvents( HttpServletRequest request )
    {
        MultiAgenda multiAgenda = getAgenda( request );
        List<Agenda> listAgendas = multiAgenda.getAgendas(  );
        List<AgendaResource> listAgendaResource = new ArrayList(  );
        String strReadWrite = AppPropertiesService.getProperty( Constants.PROPERTY_READ_WRITE );

        for ( Agenda agenda : listAgendas )
        {
            String strRessourceType = null;
            AgendaResource agendaResource = AgendaService.getInstance(  ).getAgendaResource( agenda.getKeyName(  ) );

            if ( agendaResource.getResourceType(  ) != null )
            {
                strRessourceType = agendaResource.getResourceType(  );
            }

            String strManagerRole = agendaResource.getRoleManager(  );

            if ( strRessourceType.equals( strReadWrite ) )
            {
                // Check security access
                if ( ( strManagerRole != null ) && ( !strManagerRole.equals( "" ) ) )
                {
                    if ( SecurityService.isAuthenticationEnable(  ) )
                    {
                        if ( SecurityService.getInstance(  ).isUserInRole( request, strManagerRole ) )
                        {
                            listAgendaResource.add( agendaResource );
                        }
                    }
                }
            }
        }

        return listAgendaResource;
    }

    /**
     * Returns the management of events related to a specific calendar
     * @param strCalendarId The identifier of the calendar
     * @param request The HttpRequest
     * @param plugin The Plugin
     * @return The managemant form of events
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException The exception will be treated and handled by the front Message Service
     */
    private HtmlTemplate getManageEvents( String strCalendarId, HttpServletRequest request, Plugin plugin )
        throws SiteMessageException
    {
        String strSortEvents = request.getParameter( Constants.PARAMETER_SORT_EVENTS );
        strSortEvents = ( strSortEvents != null ) ? strSortEvents : Constants.ONE;
        List<SimpleEvent> listEvents = new ArrayList<SimpleEvent>(  );
        AgendaResource agenda = CalendarHome.findAgendaResource( Integer.parseInt( strCalendarId ), plugin );
        
        String strManagerRole = agenda.getRoleManager(  );
        
        // Check security access
        if ( strManagerRole != null && !strManagerRole.equals( Constants.EMPTY_STRING ) &&
                !strManagerRole.equals( Constants.PROPERTY_ROLE_NONE )  && 
                SecurityService.isAuthenticationEnable(  ) &&
                SecurityService.getInstance(  ).isUserInRole( request, strManagerRole ) )
        {
        	listEvents = CalendarHome.findEventsList( Integer.parseInt( strCalendarId ),
                    Integer.parseInt( strSortEvents ), plugin );
        }
        else
        {
        	if ( SecurityService.isAuthenticationEnable(  ) )
        	{
        		LuteceUser user = null;
    			try 
    			{
    				user = SecurityService.getInstance(  ).getRemoteUser( request );
    			} 
    			catch ( UserNotSignedException e ) 
    			{
    				AppLogService.error( e );
    			}

    			if ( user != null && user.getName(  ) != null && !user.getName(  ).equals( Constants.EMPTY_STRING ) )
    			{
    				listEvents = CalendarHome.findEventsListByUserLogin( Integer.parseInt( strCalendarId ),
    						Integer.parseInt( strSortEvents ), plugin, user.getName(  ) );
    			}
        	}
        }
        
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        
        //Fetch the name of the calendar to be modified
        model.put( Constants.MARK_CALENDAR, agenda );
        model.put( Constants.MARK_EVENT_LIST, listEvents );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CALENDAR_MANAGE_EVENTS, request.getLocale(  ),
                model );

        return template;
    }

    /**
     * The form used to add an event to a calendar
     * @param strCalendarId The identifier of the calendar
     * @param request The HttpRequest
     * @param plugin The Plugin
     * @return The addition form
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException The exception which is treated by the front Message mechanism
     */
    private HtmlTemplate getCreateEvent( String strCalendarId, HttpServletRequest request, Plugin plugin )
        throws SiteMessageException
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        model.put( Constants.MARK_CALENDAR_ID, strCalendarId );
        model.put( Constants.MARK_LOCALE, request.getLocale(  ).getLanguage(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_EVENT_FRONT, request.getLocale(  ),
                model );

        return template;
    }

    /**
     * Method calling the modification form of an event
     * @return
     * @param request The HttpRequest object
     * @param plugin The plugin
     */
    private String getModifyEvent( HttpServletRequest request, Plugin plugin )
    	throws SiteMessageException
    {
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        int nEventId = Integer.parseInt( request.getParameter( Constants.PARAMETER_EVENT_ID ) );
        
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( Constants.MARK_EVENT, CalendarHome.findEvent( nEventId, plugin ) );
        model.put( Constants.MARK_CALENDAR_ID, nCalendarId );
        model.put( Constants.MARK_DEFAULT_SORT_EVENT, request.getParameter( Constants.PARAMETER_SORT_EVENTS ) );
        model.put( Constants.MARK_LOCALE, request.getLocale(  ).getLanguage(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_EVENT_FRONT, request.getLocale(  ),
                model );

        return template.getHtml(  );
    }

    /**
     * The Creation action of an event
     * @param strCalendarId The identifier of the Calendar
     * @param request The HttpRequest
     * @param plugin The Plugin
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException Exception used by the front Message mechanism
     */
    private void doCreateEvent( String strCalendarId, HttpServletRequest request, Plugin plugin )
        throws SiteMessageException
    {
        //Creation of the event
        String strDate = request.getParameter( Constants.PARAMETER_EVENT_DATE );
        String strEventTitle = request.getParameter( Constants.PARAMETER_EVENT_TITLE );

        int nCalendarId = Integer.parseInt( strCalendarId );
        //Mandatory fields
        verifyFieldFilled( request, strDate );
        verifyFieldFilled( request, strEventTitle );

        //Convert the date in form to a java.util.Date object
        Date dateEvent = DateUtil.formatDate( strDate, request.getLocale(  ) );

        if ( dateEvent == null )
        {
            errorDateFormat( request );
        }

        SimpleEvent event = new SimpleEvent(  );
        event.setDate( dateEvent );
        event.setDateEnd( dateEvent );

        String strTimeStart = request.getParameter( Constants.PARAMETER_EVENT_TIME_START );
        String strTimeEnd = request.getParameter( Constants.PARAMETER_EVENT_TIME_END );

        if ( !Utils.checkTime( strTimeStart ) || !Utils.checkTime( strTimeEnd ) )
        {
            errorTimeFormat( request );
        }

        event.setDateTimeStart( strTimeStart );
        event.setDateTimeEnd( strTimeEnd );
        event.setTitle( request.getParameter( Constants.PARAMETER_EVENT_TITLE ) );
        event.setDescription( Constants.EMPTY_STRING );
        event.setOccurrence( 1 );
        
        AgendaResource agenda = CalendarHome.findAgendaResource( Integer.parseInt( strCalendarId ), plugin );
        
        String strManagerRole = agenda.getRoleManager(  );
        event.setStatus( AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_TENTATIVE ) );
        // Check security access
        if ( ( strManagerRole != null ) && ( !strManagerRole.equals( Constants.EMPTY_STRING ) ) &&
                !strManagerRole.equals( Constants.PROPERTY_ROLE_NONE )  && 
                SecurityService.isAuthenticationEnable(  ) &&
                SecurityService.getInstance(  ).isUserInRole( request, strManagerRole ) )
        {
            event.setStatus( AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_DEFAULT ) );
        }
        
        LuteceUser user = null;
        String strUserLogin = null;
        if ( SecurityService.isAuthenticationEnable(  ) )
        {
        	try 
    		{
    			user = SecurityService.getInstance(  ).getRemoteUser( request );
    		} 
    		catch ( UserNotSignedException e ) 
    		{
    			AppLogService.error( e );
    		}
    		if ( user != null && user.getName(  ) != null && !user.getName(  ).equals( Constants.EMPTY_STRING ) )
    		{
    			strUserLogin = user.getName(  );
    		}
        }
		
        CalendarHome.createEvent( nCalendarId, event, plugin, strUserLogin );
        
        // Send email to notify an event has been created if the event has the status 'confirmed'
        int nSubscriber = CalendarSubscriberHome.findSubscriberNumber( nCalendarId, plugin );
        Collection<CalendarSubscriber> listSubscribers = new ArrayList<CalendarSubscriber>(  );
        if ( nSubscriber > 0 && 
        		event.getStatus(  ).equals( AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_CONFIRMED ) ) )
        {
            listSubscribers = CalendarSubscriberHome.findSubscribers( nCalendarId, plugin );
        }
        // Notify the webmaster an event has been created
    	CalendarSubscriber webmaster = new CalendarSubscriber(  );
    	webmaster.setEmail( AppPropertiesService.getProperty( PROPERTY_WEBMASTER_EMAIL ) );
    	listSubscribers.add( webmaster );
    	AgendaSubscriberService.getInstance(  )
    		.sendSubscriberMail( request, listSubscribers, event, nCalendarId );
    }

    /**
     * Method modifying the event
     *
     * @param request The request
     * @param plugin The plugin
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException Exception used by the front Message mechanism
     */
    private void doModifyEvent( HttpServletRequest request, Plugin plugin )
        throws SiteMessageException
    {
        int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
        String strEventDate = request.getParameter( Constants.PARAMETER_EVENT_DATE );
        int nEventId = Integer.parseInt( strEventId );

        verifyFieldFilled( request, strEventDate );

        //Convert the date in form to a java.util.Date object
        Date dateEvent = DateUtil.formatDate( strEventDate, request.getLocale(  ) );

        if ( dateEvent == null )
        {
            errorDateFormat( request );
        }

        SimpleEvent event = CalendarHome.findEvent( nEventId, plugin );
        List<OccurrenceEvent> listOccurrenceEvents = CalendarHome.findOccurrencesList( 
        		nCalendarId, nEventId, Integer.parseInt( Constants.ONE ), plugin );
        
        
        AgendaResource agenda = CalendarHome.findAgendaResource( nCalendarId, plugin );
        
        String strManagerRole = agenda.getRoleManager(  );
        // Check security access
        if ( strManagerRole != null && !strManagerRole.equals( Constants.EMPTY_STRING ) &&
                !strManagerRole.equals( Constants.PROPERTY_ROLE_NONE )  && 
                SecurityService.isAuthenticationEnable(  ) &&
                SecurityService.getInstance(  ).isUserInRole( request, strManagerRole ) )
        {
        	for ( OccurrenceEvent occurrenceEvent : listOccurrenceEvents )
            {
            	if ( event.getDate(  ).equals( occurrenceEvent.getDate(  ) ) )
            	{
            		event.setStatus( occurrenceEvent.getStatus(  ) );
            		break;
            	}
            }
        }
        else
        {
        	event.setStatus( AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_TENTATIVE ) );
        	for ( OccurrenceEvent occurrenceEvent : listOccurrenceEvents )
            {
            	occurrenceEvent.setStatus( event.getStatus(  ) );
            	CalendarHome.updateOccurrence( occurrenceEvent, nCalendarId, plugin );
            }
        }
        
        event.setDate( dateEvent );

        String strTimeStart = request.getParameter( Constants.PARAMETER_EVENT_TIME_START );
        String strTimeEnd = request.getParameter( Constants.PARAMETER_EVENT_TIME_END );

        if ( !Utils.checkTime( strTimeStart ) || !Utils.checkTime( strTimeEnd ) )
        {
            errorTimeFormat( request );
        }

        event.setDateTimeStart( strTimeStart );
        event.setDateTimeEnd( strTimeEnd );
        event.setTitle( request.getParameter( Constants.PARAMETER_EVENT_TITLE ) );

        CalendarHome.updateEvent( nCalendarId, event, true, plugin );
    }

    /**
     * Method verifies whether a mandatory field is filled
     * @param request The HttpRequest
     * @param strField The field to be checked
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException Exception used by the front Message mechanism
     */
    private void verifyFieldFilled( HttpServletRequest request, String strField )
        throws SiteMessageException
    {
        if ( ( strField == Constants.EMPTY_NULL ) || strField.equals( Constants.EMPTY_STRING ) )
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, new String[] { Constants.EMPTY_STRING }, 
            		Constants.EMPTY_STRING, null, Constants.EMPTY_STRING, SiteMessage.TYPE_STOP );
        }
    }
    
    /**
     * Verifiy if the current user has the right to manage the event
     * @param request HttpServletRequest
     * @param plugin Plugin
     * @return True if the user has the right, false otherwise
     */
    private boolean verifiyUserAccess( HttpServletRequest request, Plugin plugin )
    {
    	int nCalendarId = Integer.parseInt( request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
    	int nEventId = Integer.parseInt( request.getParameter( Constants.PARAMETER_EVENT_ID ) );
    	AgendaResource agenda = CalendarHome.findAgendaResource( nCalendarId, plugin );
        
        String strManagerRole = agenda.getRoleManager(  );
    	
    	if ( ( strManagerRole != null ) && ( !strManagerRole.equals( Constants.EMPTY_STRING ) ) &&
                !strManagerRole.equals( Constants.PROPERTY_ROLE_NONE )  && 
                SecurityService.isAuthenticationEnable(  ) &&
                SecurityService.getInstance(  ).isUserInRole( request, strManagerRole ) )
        {
    		return true;
        }
    	else
    	{
    		if ( SecurityService.isAuthenticationEnable(  ) )
    		{
    			//return unregistered form if unregistered user wants to access application management form
                LuteceUser user = null;
    			try 
    			{
    				user = SecurityService.getInstance(  ).getRemoteUser( request );
    			} 
    			catch ( UserNotSignedException e ) 
    			{
    				AppLogService.error( e );
    			}

                if ( ( user != null ) && ( user.getName(  ) != null ) && !user.getName(  ).equals( Constants.EMPTY_STRING ) )
                {
    				List<SimpleEvent> listEvents = CalendarHome.findEventsListByUserLogin( 
    						nCalendarId, Integer.parseInt( Constants.ONE ), plugin, user.getName(  ) );
    	        	for ( SimpleEvent event : listEvents )
    	        	{
    	        		if ( event.getId(  ) == nEventId )
    	        		{
    	        			return true;
    	        		}
    	        	}
                }
    		}
    	}
    	return false;
    }

    /**
     * Verifies the date format
     * @param request The HttpRequest
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException Exception used by the front Message mechanism
     */
    private void errorDateFormat( HttpServletRequest request )
        throws SiteMessageException
    {
        SiteMessageService.setMessage( request, PROPERTY_INVALID_DATE_MESSAGE, null,
            PROPERTY_INVALID_DATE_TITLE_MESSAGE, null, null, SiteMessage.TYPE_STOP );
    }

    /**
     * Verifies the time format
     * @param request The HttpRequest
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException Exception used by the front Message mechanism
     */
    private void errorTimeFormat( HttpServletRequest request )
        throws SiteMessageException
    {
        SiteMessageService.setMessage( request, PROPERTY_INVALID_TIME_MESSAGE, null,
            PROPERTY_INVALID_TIME_TITLE_MESSAGE, null, null, SiteMessage.TYPE_STOP );
    }
        
    /**
     * The method calling the remove action
     *
     * @param request The HttpRequest
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException Exception used by the front Message mechanism
     */
    private void getRemoveEvent( HttpServletRequest request )
        throws SiteMessageException
    {
        UrlItem url = new UrlItem( JSP_PAGE_PORTAL );
        url.addParameter( Constants.PARAMETER_PAGE, PROPERTY_PLUGIN_NAME );
        url.addParameter( Constants.PARAMETER_ACTION, ACTION_DO_REMOVE_EVENT );
        url.addParameter( Constants.PARAMETER_CALENDAR_ID, request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
        url.addParameter( Constants.PARAMETER_EVENT_ID, request.getParameter( Constants.PARAMETER_EVENT_ID ) );
        SiteMessageService.setMessage( request, PROPERTY_CONFIRM_REMOVE_ALERT_MESSAGE, null,
            PROPERTY_CONFIRM_REMOVE_TITLE_MESSAGE, url.getUrl(  ), null, SiteMessage.TYPE_CONFIRMATION );
    }

    /**
     * Performs the subscription process
     * @param request The Http request
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException The error message thrown to the user
     */
    public void doSubscription( HttpServletRequest request )
        throws SiteMessageException
    {
        AgendaSubscriberService.getInstance(  ).doValidationSubscription( request );
    }

    /**
     * Performs unsubscription process
     * @param request The http request
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException The error message handled by the front office
     */
    public void doUnSubscribe( HttpServletRequest request )
        throws SiteMessageException
    {
        AgendaSubscriberService.getInstance(  ).doUnSubscribe( request );
    }

    /**
     * Send a mail to a friend
     * @param request The request
     */
    public String doSendToFriend( HttpServletRequest request )
        throws SiteMessageException
    {
        //Form parameters
        String strIdEvent = request.getParameter( Constants.PARAMETER_EVENT_ID );
        String strIdCalendar = request.getParameter( Constants.PARAMETER_CALENDAR_ID );
        String strFriendEmail = request.getParameter( Constants.PARAMETER_SENDER_FRIEND_EMAIL );
        String strSenderFirstName = request.getParameter( Constants.PARAMETER_SENDER_FIRST_NAME );
        String strSenderLastName = request.getParameter( Constants.PARAMETER_SENDER_LAST_NAME );
        String strSenderEmail = request.getParameter( Constants.PARAMETER_SENDER_EMAIL );
        String strSenderMessage = request.getParameter( Constants.PARAMETER_SENDER_MESSAGE );
        int nIdCalendar = Integer.parseInt( strIdCalendar );

        // Mandatory field
        if ( strFriendEmail.equals( "" ) || strSenderFirstName.equals( "" ) || strSenderLastName.equals( "" ) ||
                strSenderEmail.equals( "" ) || strSenderMessage.equals( "" ) )
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, Messages.MANDATORY_FIELDS,
                SiteMessage.TYPE_STOP );
        }

        if ( ( strFriendEmail == null ) || !StringUtil.checkEmail( strFriendEmail ) )
        {
            Object[] args = { strFriendEmail };
            SiteMessageService.setMessage( request, PROPERTY_INVALID_MAIL_ERROR_MESSAGE, args,
                PROPERTY_INVALID_MAIL_TITLE_MESSAGE, SiteMessage.TYPE_STOP );
        }

        if ( ( strSenderEmail == null ) || !StringUtil.checkEmail( strSenderEmail ) )
        {
            Object[] args = { strSenderEmail };
            SiteMessageService.setMessage( request, PROPERTY_INVALID_MAIL_ERROR_MESSAGE, args,
                PROPERTY_INVALID_MAIL_TITLE_MESSAGE, SiteMessage.TYPE_STOP );
        }

        String strSenderName = strSenderFirstName + Constants.SPACE + strSenderLastName;

        //Properties
        String strObject = I18nService.getLocalizedString( PROPERTY_EMAIL_FRIEND_OBJECT, request.getLocale(  ) );
        String strBaseUrl = AppPathService.getBaseUrl( request );
        String strPluginName = Constants.PLUGIN_NAME;

        Map<String, Object> emailModel = new HashMap<String, Object>(  );
        emailModel.put( MARK_SENDER_MESSAGE, strSenderMessage );
        emailModel.put( MARK_BASE_URL, strBaseUrl );
        emailModel.put( Constants.PARAMETER_EVENT_ID, strIdEvent );
        emailModel.put( Constants.PARAMETER_CALENDAR_ID, nIdCalendar );
        emailModel.put( Constants.MARK_ACTION, Constants.ACTION_SHOW_RESULT );

        HtmlTemplate templateAgenda = AppTemplateService.getTemplate( TEMPLATE_SEND_NOTIFICATION_MAIL,
                request.getLocale(  ), emailModel );

        String strNewsLetterCode = templateAgenda.getHtml(  );

        MailService.sendMailHtml( strFriendEmail, strSenderName, strSenderEmail, strObject, strNewsLetterCode );

        return URL_JSP_RETURN_SEND_FRIEND_MAIL + Constants.ACTION_SHOW_RESULT + "&" + Constants.PARAMETER_EVENT_ID +
        "=" + strIdEvent + "&" + Constants.PARAM_AGENDA + "=" + strIdCalendar;
    }

    /**
     * Return the list of agenda for the template html search
     */
    public ReferenceList getListAgenda( HttpServletRequest request, Plugin plugin )
    {
        ReferenceList listAgendas = new ReferenceList(  );

        for ( AgendaResource a : getAgendaResources( request, plugin ) )
            if ( a != null )
            {
                listAgendas.addItem( a.getId(  ), a.getName(  ) );
            }

        return listAgendas;
    }

    /**
     * Return the list of agenda for the template html search
     */
    public ReferenceList getExportSheetList( HttpServletRequest request, Plugin plugin )
    {
        ReferenceList listSheets = new ReferenceList(  );

        Collection<StyleSheet> collectionSheets = CalendarStyleSheetHome.getStyleSheetList( plugin );

        if ( collectionSheets != null )
        {
            Iterator<StyleSheet> i = collectionSheets.iterator(  );

            while ( i.hasNext(  ) )
            {
                StyleSheet sheet = (StyleSheet) i.next(  );
                listSheets.addItem( sheet.getId(  ), sheet.getDescription(  ) );
            }
        }

        return listSheets;
    }

    /**
     * Return the list
     *
     * @return a refenceList
     */
    private ReferenceList getReferenceListCategory( Collection<Category> collection )
    {
        ReferenceList list = new ReferenceList(  );

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
}
