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
package fr.paris.lutece.plugins.calendar.web;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.plugins.calendar.business.CalendarSubscriber;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.business.MultiAgenda;
import fr.paris.lutece.plugins.calendar.business.OccurrenceEvent;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.business.category.Category;
import fr.paris.lutece.plugins.calendar.business.stylesheet.CalendarStyleSheetHome;
import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.AgendaSubscriberService;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.plugins.calendar.service.CalendarService;
import fr.paris.lutece.plugins.calendar.service.CategoryService;
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
import fr.paris.lutece.portal.service.page.PageNotFoundException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
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

import org.apache.commons.lang.StringUtils;


/**
 * This class is the main class of the XPage application of the plugin calendar.
 * 
 */
public class CalendarApp implements XPageApplication
{
    public static final String PROPERTY_INVALID_MAIL_TITLE_MESSAGE = "calendar.siteMessage.invalid_mail.title";
    public static final String PROPERTY_INVALID_MAIL_ERROR_MESSAGE = "calendar.siteMessage.invalid_mail.message";

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

    //Properties
    private static final String PROPERTY_PLUGIN_NAME = "calendar";

    //private static final String PROPERTY_FEATURE_URL = "?page=calendar&action=do_search";
    private static final String PROPERTY_UTIL_DOCUMENT_CLASS = "fr.paris.lutece.plugins.calendar.modules.document.web.DocumentCalendarUtils";
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
    private AgendaSubscriberService _agendaSubscriberService = AgendaSubscriberService.getInstance( );
    private CategoryService _categoryService = CategoryService.getInstance( );
    private EventListService _eventListService = SpringContextService
            .getBean( Constants.BEAN_CALENDAR_EVENTLISTSERVICE );
    private CalendarService _calendarService = SpringContextService.getBean( Constants.BEAN_CALENDAR_CALENDARSERVICE );

    /**
     * Returns the content of the page Contact. It is composed by a form which
     * to capture the data to send a message to
     * a contact of the portal.
     * 
     * @return the Content of the page Contact
     * @param request The http request
     * @param nMode The current mode
     * @param plugin The plugin object
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException A
     *             message exception treated on front
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws SiteMessageException,
            UserNotSignedException
    {
        XPage page = null;

        String strAction = request.getParameter( Constants.PARAMETER_ACTION );
        String strPluginName = request.getParameter( Constants.PARAMETER_PAGE );
        if ( StringUtils.isBlank( strPluginName ) )
        {
            strPluginName = CalendarPlugin.PLUGIN_NAME;
        }
        plugin = PluginService.getPlugin( strPluginName );

        if ( StringUtils.isNotBlank( strAction ) )
        {
            if ( Constants.ACTION_MANAGE_EVENTS.equals( strAction ) )
            {
                page = getManageEventsPage( request, plugin );
            }
            else if ( Constants.ACTION_ADD_EVENT.equals( strAction ) )
            {
                page = getAddEventPage( request, plugin );
            }
            else if ( Constants.ACTION_DO_CREATE_EVENT.equals( strAction ) )
            {
                doCreateEvent( request, plugin );
                page = getManageEventsPage( request, plugin );
            }
            else if ( Constants.ACTION_MODIFY_EVENT.equals( strAction ) )
            {
                page = getModifyEventPage( request, plugin );
            }
            else if ( Constants.ACTION_DO_MODIFY_EVENT.equals( strAction ) )
            {
                doModifyEvent( request, plugin );
                page = getManageEventsPage( request, plugin );
            }
            else if ( Constants.ACTION_REMOVE_EVENT.equals( strAction ) )
            {
                getRemoveEvent( request, plugin );
            }
            else if ( Constants.ACTION_DO_REMOVE_EVENT.equals( strAction ) )
            {
                doRemoveEvent( request, plugin );
                page = getManageEventsPage( request, plugin );
            }
            else if ( Constants.ACTION_GET_SUBSCRIPTION_PAGE.equals( strAction ) )
            {
                page = getSubscriptionPage( request, plugin );
            }
            else if ( Constants.ACTION_GET_FRIEND_EMAIL_PAGE.equals( strAction ) )
            {
                page = getGetFriendEmailPage( request, plugin );
            }
            else if ( Constants.ACTION_GET_DOWNLOAD_PAGE.equals( strAction ) )
            {
                page = getDownloadPage( request, plugin );
            }
            else if ( Constants.ACTION_SEND_FRIEND_EMAIL.equals( strAction ) )
            {
                _agendaSubscriberService.sendFriendMail( request );
            }
            else if ( Constants.ACTION_VERIFY_SUBSCRIBE.equals( strAction ) )
            {
                doVerifySubscription( request, plugin );
            }
            else if ( Constants.ACTION_CONFIRM_UNSUBSCRIBE.equals( strAction ) )
            {
                getConfirmUnSubscribe( request );
            }
            else if ( Constants.ACTION_UNSUBSCRIBE.equals( strAction ) )
            {
                _agendaSubscriberService.doUnSubscribe( request, plugin );
            }
            else if ( Constants.ACTION_SEARCH.equals( strAction ) )
            {
                page = getSearchPage( request, plugin );
            }
            else if ( Constants.ACTION_DO_SEARCH.equals( strAction ) )
            {
                page = getSearchResultPage( request, plugin );
            }
            else if ( Constants.ACTION_SHOW_RESULT.equals( strAction ) )
            {
                page = getShowResultPage( request, plugin );
            }
            else if ( Constants.ACTION_RSS.equals( strAction ) )
            {
                page = getRssPage( request, plugin );
            }
        }

        if ( page == null )
        {
            page = getDefaultPage( request, plugin );
        }

        return page;
    }

    /**
     * Get the default XPage
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the html
     */
    private XPage getDefaultPage( HttpServletRequest request, Plugin plugin )
    {
        XPage page = new XPage( );
        // Gets calendar infos from the request parameters and session
        CalendarView view = getView( request );
        EventList eventlist = _eventListService.newEventList( view.getType( ) );

        MultiAgenda agendaWithOccurences = _calendarService.getMultiAgenda( request );
        String strDate = getDate( request );
        CalendarUserOptions options = getUserOptions( request );
        options.setShowSearchEngine( Boolean.TRUE );

        boolean bIsSelectedDay = false;
        if ( view.getType( ) == CalendarView.TYPE_DAY )
        {
            bIsSelectedDay = true;
        }

        // Load and fill the page template
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( Constants.MARK_PREVIOUS, view.getPrevious( strDate ) );
        model.put( Constants.MARK_NEXT, view.getNext( strDate ) );
        model.put( Constants.MARK_TITLE, view.getTitle( strDate, options ) );
        model.put( Constants.MARK_DATE, strDate );
        model.put( Constants.MARK_LEGEND, getLegend( request, agendaWithOccurences, options ) );
        model.put( Constants.MARK_VIEW_CALENDAR, view.getCalendarView( strDate, agendaWithOccurences, options, request ) );
        model.put( Constants.MARK_SMALL_MONTH_CALENDAR,
                SmallMonthCalendar.getSmallMonthCalendar( strDate, agendaWithOccurences, options, bIsSelectedDay ) );

        // Display event list if there is some events to display
        String strEventList = StringUtils.EMPTY;

        //get events
        if ( ( eventlist != null ) && ( agendaWithOccurences.getAgendas( ).size( ) != 0 ) )
        {
            strEventList = eventlist.getEventList( strDate, agendaWithOccurences, options.getLocale( ), request );
        }

        model.put( Constants.MARK_EVENT_LIST, strEventList );

        String strRunAppJspUrl = AppPropertiesService.getProperty( Constants.PROPERTY_RUNAPP_JSP_URL );
        model.put( Constants.MARK_JSP_URL, strRunAppJspUrl );

        // Set XPage data
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CALENDAR, options.getLocale( ), model );
        page.setContent( template.getHtml( ) );
        page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_TITLE, options.getLocale( ) )
                + view.getTitle( strDate, options ) );
        page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PATH, options.getLocale( ) )
                + view.getPath( strDate, options ) );

        return page;
    }

    /**
     * Get the XPage for managing event
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the html
     * @throws SiteMessageException message if error
     */
    private XPage getManageEventsPage( HttpServletRequest request, Plugin plugin ) throws SiteMessageException
    {
        XPage page = null;

        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );
        if ( StringUtils.isNotBlank( strCalendarId ) && StringUtils.isNumeric( strCalendarId ) )
        {
            page = new XPage( );
            int nCalendarId = Integer.parseInt( strCalendarId );

            // The sort function is not used yet
            /*
             * String strSortEvents = request.getParameter(
             * Constants.PARAMETER_SORT_EVENTS );
             * int nSortEvent = Constants.SORT_ASC;
             * if ( StringUtils.isNotBlank( strSortEvents ) &&
             * StringUtils.isNumeric( strSortEvents ) )
             * {
             * nSortEvent = Integer.parseInt( strSortEvents );
             * }
             */

            AgendaResource agenda = _calendarService.getAgendaResource( nCalendarId );

            List<SimpleEvent> listEvents = null;
            // Check security access
            if ( hasManagerRole( agenda, request ) )
            {
                listEvents = _eventListService.getSimpleEvents( nCalendarId, Constants.SORT_ASC );
            }
            else
            {
                LuteceUser user;
                try
                {
                    user = getUser( request );
                    if ( user != null )
                    {
                        listEvents = _eventListService.getSimpleEventsByUserLogin( nCalendarId, user );
                    }
                }
                catch ( UserNotSignedException ue )
                {
                    listEvents = new ArrayList<SimpleEvent>( );
                }
                catch ( PageNotFoundException pe )
                {
                    listEvents = new ArrayList<SimpleEvent>( );
                }
            }

            Map<String, Object> model = new HashMap<String, Object>( );

            //Fetch the name of the calendar to be modified
            model.put( Constants.MARK_CALENDAR, agenda );
            model.put( Constants.MARK_EVENT_LIST, listEvents );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CALENDAR_MANAGE_EVENTS,
                    request.getLocale( ), model );

            page.setContent( template.getHtml( ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MANAGE_EVENTS,
                    request.getLocale( ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MANAGE_EVENTS,
                    request.getLocale( ) ) );
        }
        return page;
    }

    /**
     * Return unregistered form if unregistered user wants to acces application
     * management form
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the html
     * @throws SiteMessageException message if error
     */
    private XPage getAddEventPage( HttpServletRequest request, Plugin plugin ) throws SiteMessageException
    {
        XPage page = null;
        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );
        if ( StringUtils.isNotBlank( strCalendarId ) && StringUtils.isNumeric( strCalendarId ) )
        {
            page = new XPage( );

            Map<String, Object> model = new HashMap<String, Object>( );

            model.put( Constants.MARK_CALENDAR_ID, strCalendarId );
            model.put( Constants.MARK_LOCALE, request.getLocale( ).getLanguage( ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_EVENT_FRONT, request.getLocale( ),
                    model );

            page.setContent( template.getHtml( ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_CREATE_EVENT,
                    request.getLocale( ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_CREATE_EVENT,
                    request.getLocale( ) ) );
        }

        return page;

    }

    /**
     * Get the XPage for modifying an event
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the hmlt form
     * @throws SiteMessageException message if error
     * @throws UserNotSignedException exception if user is not connected
     */
    private XPage getModifyEventPage( HttpServletRequest request, Plugin plugin ) throws SiteMessageException,
            UserNotSignedException
    {
        XPage page = null;
        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );
        String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
        if ( StringUtils.isNotBlank( strCalendarId ) && StringUtils.isNumeric( strCalendarId )
                && StringUtils.isNotBlank( strEventId ) && StringUtils.isNumeric( strEventId ) )
        {
            int nCalendarId = Integer.parseInt( strCalendarId );
            int nEventId = Integer.parseInt( strEventId );
            if ( verifiyUserAccess( request, nCalendarId, nEventId, plugin ) )
            {
                HashMap<String, Object> model = new HashMap<String, Object>( );
                model.put( Constants.MARK_EVENT, _eventListService.getEvent( nEventId, plugin ) );
                model.put( Constants.MARK_CALENDAR_ID, nCalendarId );
                model.put( Constants.MARK_DEFAULT_SORT_EVENT, request.getParameter( Constants.PARAMETER_SORT_EVENTS ) );
                model.put( Constants.MARK_LOCALE, request.getLocale( ).getLanguage( ) );

                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_EVENT_FRONT,
                        request.getLocale( ), model );

                page = new XPage( );
                page.setContent( template.getHtml( ) );
                page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MODIFY_EVENT,
                        request.getLocale( ) ) );
                page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_MODIFY_EVENT,
                        request.getLocale( ) ) );
            }
            else
            {
                SiteMessageService.setMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP );
            }
        }

        return page;
    }

    /**
     * The method calling the remove action
     * 
     * @param request The HttpRequest
     * @param plugin The plugin
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     *             Exception used by the front Message mechanism
     * @throws UserNotSignedException
     */
    private void getRemoveEvent( HttpServletRequest request, Plugin plugin ) throws SiteMessageException,
            UserNotSignedException
    {
        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );
        String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
        if ( StringUtils.isNotBlank( strCalendarId ) && StringUtils.isNumeric( strCalendarId )
                && StringUtils.isNotBlank( strEventId ) && StringUtils.isNumeric( strEventId ) )
        {
            int nCalendarId = Integer.parseInt( strCalendarId );
            int nEventId = Integer.parseInt( strEventId );
            if ( verifiyUserAccess( request, nCalendarId, nEventId, plugin ) )
            {
                UrlItem url = new UrlItem( JSP_PAGE_PORTAL );
                url.addParameter( Constants.PARAMETER_PAGE, PROPERTY_PLUGIN_NAME );
                url.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_DO_REMOVE_EVENT );
                url.addParameter( Constants.PARAMETER_CALENDAR_ID,
                        request.getParameter( Constants.PARAMETER_CALENDAR_ID ) );
                url.addParameter( Constants.PARAMETER_EVENT_ID, request.getParameter( Constants.PARAMETER_EVENT_ID ) );
                SiteMessageService.setMessage( request, PROPERTY_CONFIRM_REMOVE_ALERT_MESSAGE, null,
                        PROPERTY_CONFIRM_REMOVE_TITLE_MESSAGE, url.getUrl( ), null, SiteMessage.TYPE_CONFIRMATION );
            }
            else
            {
                SiteMessageService.setMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP );
            }
        }
    }

    /**
     * Get the XPage for subscribing
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the html
     */
    private XPage getSubscriptionPage( HttpServletRequest request, Plugin plugin )
    {
        XPage page = new XPage( );
        boolean bIsCaptchaEnabled = PluginService.isPluginEnable( Constants.PLUGIN_JCAPTCHA );

        String strBaseUrl = AppPathService.getBaseUrl( request );
        UrlItem url = new UrlItem( strBaseUrl + JSP_PAGE_PORTAL );
        url.addParameter( Constants.PARAMETER_PAGE, Constants.PLUGIN_NAME );
        url.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_VERIFY_SUBSCRIBE );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( Constants.MARK_JSP_URL, url.getUrl( ) );
        model.put( Constants.MARK_LOCALE, request.getLocale( ) );
        model.put( Constants.MARK_CALENDARS_LIST, getListAgenda( request, plugin ) );
        model.put( Constants.MARK_IS_ACTIVE_CAPTCHA, bIsCaptchaEnabled );

        if ( bIsCaptchaEnabled )
        {
            _captchaService = new CaptchaSecurityService( );
            model.put( Constants.MARK_CAPTCHA, _captchaService.getHtmlCode( ) );
        }

        HtmlTemplate template = AppTemplateService
                .getTemplate( TEMPLATE_SUBSCRIPTION_FORM, request.getLocale( ), model );

        page.setContent( template.getHtml( ) );
        page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_SUBSCRIPTION_TITLE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_SUBSCRIPTION_TITLE,
                request.getLocale( ) ) );

        return page;
    }

    /**
     * Get the XPage for sending a email to a friend
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the html
     */
    private XPage getGetFriendEmailPage( HttpServletRequest request, Plugin plugin )
    {
        XPage page = null;
        String strCalendarId = request.getParameter( Constants.PARAM_AGENDA );
        String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
        if ( StringUtils.isNotBlank( strCalendarId ) && StringUtils.isNumeric( strCalendarId )
                && StringUtils.isNotBlank( strEventId ) && StringUtils.isNumeric( strEventId ) )
        {
            page = new XPage( );
            Map<String, Object> model = new HashMap<String, Object>( );
            model.put( Constants.MARK_LOCALE, request.getLocale( ) );
            model.put( Constants.MARK_EVENT_ID, Integer.parseInt( strEventId ) );
            model.put( Constants.MARK_CALENDAR_ID, Integer.parseInt( strCalendarId ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_EMAIL_FRIEND, request.getLocale( ), model );

            page.setContent( template.getHtml( ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_EMAIL_FRIEND_TITLE,
                    request.getLocale( ) ) );
            page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_EMAIL_FRIEND_TITLE,
                    request.getLocale( ) ) );
        }

        return page;
    }

    /**
     * Get the XPage for downloading an event
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the html
     */
    private XPage getDownloadPage( HttpServletRequest request, Plugin plugin )
    {
        XPage page = new XPage( );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( Constants.MARK_LOCALE, request.getLocale( ) );
        model.put( Constants.MARK_CALENDARS_LIST, getListAgenda( request, plugin ) );
        model.put( Constants.MARK_EXPORT_STYLESHEET_LIST, getExportSheetList( request, plugin ) );

        HtmlTemplate template = AppTemplateService
                .getTemplate( TEMPLATE_DOWNLOAD_CALENDAR, request.getLocale( ), model );

        page.setContent( template.getHtml( ) );
        page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_DOWNLOAND_TITLE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_DOWNLOAND_TITLE, request.getLocale( ) ) );

        return page;
    }

    /**
     * Get the XPage for searching an event
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the html form
     */
    private XPage getSearchPage( HttpServletRequest request, Plugin plugin )
    {
        XPage page = new XPage( );
        Map<String, Object> model = new HashMap<String, Object>( );

        model.put( Constants.MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nDefaultItemsPerPage ) );
        model.put( Constants.MARK_CALENDARS_LIST, getListAgenda( request, plugin ) );
        model.put( Constants.MARK_AGENDA, Constants.SPACE );
        model.put( Constants.MARK_LOCALE, request.getLocale( ) );

        Collection<Category> categoryList = _categoryService.getCategories( plugin );
        model.put( Constants.MARK_CATEGORY_LIST, getReferenceListCategory( categoryList ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SEARCH_EVENTS, request.getLocale( ), model );

        page.setContent( template.getHtml( ) );
        page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_SEARCH, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_SEARCH, request.getLocale( ) ) );

        return page;
    }

    /**
     * Get the XPage for getting the search result
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the html
     * @throws SiteMessageException message if error
     */
    private XPage getSearchResultPage( HttpServletRequest request, Plugin plugin ) throws SiteMessageException
    {
        String strQuery = request.getParameter( Constants.PARAMETER_QUERY );

        String[] arrayCategory = request.getParameterValues( Constants.PARAMETER_CATEGORY );
        String[] arrayCalendar = request.getParameterValues( Constants.PARAMETER_CALENDAR_ID );

        String strDateBegin = request.getParameter( Constants.PARAMETER_DATE_START );
        String strDateEnd = request.getParameter( Constants.PARAMETER_DATE_END );
        String strPeriod = request.getParameter( Constants.PARAMETER_PERIOD );

        if ( StringUtils.isBlank( strPeriod ) || !StringUtils.isNumeric( strPeriod ) )
        {
            strPeriod = Integer.toString( Constants.PROPERTY_PERIOD_NONE );
        }

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
            dateBegin = dateEnd = new Date( );
            strDateBegin = strDateEnd = DateUtil.getDateString( new Date( ), request.getLocale( ) );

            break;

        case Constants.PROPERTY_PERIOD_WEEK:

            Calendar calendar = new GregorianCalendar( );
            Calendar calendarFirstDay = new GregorianCalendar( );
            Calendar calendarLastDay = new GregorianCalendar( );

            int nDayOfWeek = calendar.get( Calendar.DAY_OF_WEEK );

            if ( nDayOfWeek == 1 )
            {
                nDayOfWeek = 8;
            }

            calendarFirstDay = calendar;
            calendarFirstDay.add( Calendar.DATE, Calendar.MONDAY - nDayOfWeek );
            calendarLastDay = (GregorianCalendar) calendarFirstDay.clone( );
            calendarLastDay.add( Calendar.DATE, 6 );
            dateBegin = calendarFirstDay.getTime( );
            dateEnd = calendarLastDay.getTime( );
            strDateBegin = DateUtil.getDateString( dateBegin, request.getLocale( ) );
            strDateEnd = DateUtil.getDateString( dateEnd, request.getLocale( ) );

            break;

        case Constants.PROPERTY_PERIOD_RANGE:
            if ( StringUtils.isNotBlank( strDateBegin ) && StringUtils.isNotBlank( strDateEnd ) )
            {
                dateBegin = DateUtil.formatDate( strDateBegin, request.getLocale( ) );
                dateEnd = DateUtil.formatDate( strDateEnd, request.getLocale( ) );

                if ( dateBegin == null || !Utils.isValidDate( dateBegin ) || dateEnd == null
                        || !Utils.isValidDate( dateEnd ) )
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

        listEvent = CalendarSearchService.getInstance( ).getSearchResults( arrayCalendar, arrayCategory, strQuery,
                dateBegin, dateEnd, request, plugin );

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

        if ( listEvent == null )
        {
            listEvent = new ArrayList<Event>( );
        }

        Map<String, Object> model = new HashMap<String, Object>( );

        Paginator<Event> paginator = new Paginator<Event>( listEvent, _nItemsPerPage, url.getUrl( ),
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

        ReferenceList listAgendas = getListAgenda( request, plugin );

        if ( arrayCalendar != null )
        {
            listAgendas.checkItems( arrayCalendar );
        }

        Collection<Category> categoryList = _categoryService.getCategories( plugin );
        ReferenceList listCategorys = getReferenceListCategory( categoryList );

        if ( arrayCategory != null )
        {
            listCategorys.checkItems( arrayCategory );
        }

        // Evol List occurrences
        List<List<OccurrenceEvent>> listOccurrences = new ArrayList<List<OccurrenceEvent>>( );

        for ( Event event : listEvent )
        {
            List<OccurrenceEvent> listOccurrence = _eventListService.getOccurrenceEvents( event.getIdCalendar( ),
                    event.getId( ), Constants.SORT_ASC, plugin );
            listOccurrences.add( listOccurrence );
        }

        CalendarUserOptions options = getUserOptions( request );
        options.setShowSearchEngine( Boolean.TRUE );

        boolean bIsSelectedDay = false;
        String strDate;
        if ( StringUtils.isNotBlank( strDateBegin ) && !Constants.NULL.equals( strDateBegin ) )
        {
            strDate = Utils.getDate( DateUtil.formatDateLongYear( strDateBegin, request.getLocale( ) ) );
            if ( strDateBegin.equals( strDateEnd ) )
            {
                bIsSelectedDay = true;
            }
        }
        else
        {
            strDate = Utils.getDate( new Date( ) );
        }

        MultiAgenda agendaWithOccurences = _calendarService.getMultiAgenda( request );

        model.put( Constants.MARK_QUERY, ( StringUtils.isNotBlank( strQuery ) ) ? strQuery : StringUtils.EMPTY );
        model.put( Constants.MARK_SUBSCRIPTION_PAGE, urlSubscription.getUrl( ) );
        model.put( Constants.MARK_DOWNLOAD_PAGE, urlDownload.getUrl( ) );
        model.put( Constants.MARK_RSS_PAGE, urlRss.getUrl( ) );
        model.put( Constants.MARK_DATE_START, ( StringUtils.isNotBlank( strDateBegin ) ) ? strDateBegin
                : StringUtils.EMPTY );
        model.put( Constants.MARK_DATE_END, ( StringUtils.isNotBlank( strDateEnd ) ) ? strDateEnd : StringUtils.EMPTY );
        model.put( Constants.MARK_PERIOD, ( StringUtils.isNotBlank( strPeriod ) ) ? strPeriod : StringUtils.EMPTY );
        model.put( Constants.MARK_EVENTS_LIST, paginator.getPageItems( ) );
        model.put( Constants.MARK_PAGINATOR, paginator );
        model.put( Constants.MARK_NB_ITEMS_PER_PAGE, Integer.toString( _nItemsPerPage ) );
        model.put( Constants.MARK_CALENDARS_LIST, listAgendas );
        model.put( Constants.MARK_AGENDA, ( StringUtils.isNotBlank( strAgenda ) ) ? strAgenda : StringUtils.EMPTY );
        model.put( Constants.MARK_LOCALE, request.getLocale( ) );
        model.put( Constants.MARK_CATEGORY_LIST, listCategorys );
        model.put( Constants.MARK_OCCURRENCES_LIST, listOccurrences );
        model.put( Constants.MARK_SMALL_MONTH_CALENDAR,
                SmallMonthCalendar.getSmallMonthCalendar( strDate, agendaWithOccurences, options, bIsSelectedDay ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DO_SEARCH_EVENTS, request.getLocale( ), model );

        XPage page = new XPage( );
        page.setContent( template.getHtml( ) );
        page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_SEARCH_RESULT, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_SEARCH, request.getLocale( ) ) );

        return page;
    }

    /**
     * Get the XPage for getting the recording of an event
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the html
     */
    private XPage getShowResultPage( HttpServletRequest request, Plugin plugin )
    {
        XPage page = null;
        String strDocumentId = request.getParameter( Constants.PARAMETER_DOCUMENT_ID );
        String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
        String strPathResult = StringUtils.EMPTY;

        if ( StringUtils.isNotBlank( strEventId ) && StringUtils.isNumeric( strEventId ) )
        {
            int nEventId = Integer.parseInt( strEventId );

            SimpleEvent event = _eventListService.getEvent( nEventId, plugin );

            HtmlTemplate template = new HtmlTemplate( );

            if ( event != null )
            {
                int nDocumentId = -1;

                if ( StringUtils.isNotBlank( strDocumentId ) && StringUtils.isNumeric( strDocumentId ) )
                {
                    nDocumentId = Integer.parseInt( strDocumentId );
                }
                else if ( event.getDocumentId( ) > -1 )
                {
                    nDocumentId = event.getDocumentId( );
                }

                String strTemplateDocument = StringUtils.EMPTY;
                Plugin pluginModule = PluginService.getPlugin( Constants.PROPERTY_MODULE_CALENDAR );

                if ( ( pluginModule != null ) && pluginModule.isInstalled( ) && ( nDocumentId > 0 ) )
                {
                    try
                    {
                        IAppUtils documentUtil = (IAppUtils) Class.forName( PROPERTY_UTIL_DOCUMENT_CLASS )
                                .newInstance( );
                        strTemplateDocument = documentUtil.getTemplateDocument( nDocumentId, request );
                    }
                    catch ( Exception e )
                    {
                        AppLogService.error( e );
                    }
                }

                CalendarUserOptions options = getUserOptions( request );
                options.setShowSearchEngine( Boolean.TRUE );

                AgendaResource agenda = _calendarService.getAgendaResource( event.getIdCalendar( ) );

                MultiAgenda agendaWithOccurences = _calendarService.getMultiAgenda( request );

                String strRole = agenda.getRole( );

                boolean bIsUserAuthorized = false;

                if ( hasManagerRole( agenda, request ) || hasRole( strRole, request )
                        || Constants.PROPERTY_ROLE_NONE.equals( strRole ) )
                {
                    bIsUserAuthorized = true;
                }

                Map<String, Object> model = new HashMap<String, Object>( );

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

                    event.setImageUrl( EventImageResourceService.getInstance( ).getResourceImageEvent( event.getId( ) ) );
                    model.put( Constants.MARK_EVENT, event );
                    model.put( Constants.MARK_DOCUMENT, strTemplateDocument );
                    model.put( Constants.MARK_AGENDA, agenda );
                    model.put( Constants.MARK_EMAIL_FRIEND_PAGE, urlEmailFriend.getUrl( ) );
                    model.put( Constants.MARK_RSS_PAGE, urlRss.getUrl( ) );
                    model.put( Constants.MARK_SUBSCRIPTION_PAGE, urlSubscription.getUrl( ) );
                    model.put( Constants.MARK_DOWNLOAD_PAGE, urlDownload.getUrl( ) );
                }

                model.put( Constants.MARK_IS_AUTHORIZED, bIsUserAuthorized );
                model.put( Constants.MARK_SMALL_MONTH_CALENDAR, SmallMonthCalendar.getSmallMonthCalendar(
                        Utils.getDate( event.getDate( ) ), agendaWithOccurences, options, true ) );
                template = AppTemplateService.getTemplate( TEMPLATE_SHOW_RESULT, request.getLocale( ), model );
                strPathResult = event.getTitle( );
            }

            page = new XPage( );
            page.setContent( template.getHtml( ) );
            page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_TITLE_SEARCH_RESULT,
                    request.getLocale( ) ) );
            page.setPathLabel( strPathResult );
        }
        return page;
    }

    /**
     * Get the XPage for getting the rss
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @return the html
     */
    private XPage getRssPage( HttpServletRequest request, Plugin plugin )
    {
        Collection<Category> categoryList = _categoryService.getCategories( plugin );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( Constants.MARK_LOCALE, request.getLocale( ) );
        model.put( Constants.MARK_CALENDARS_LIST, getListAgenda( request, plugin ) );
        model.put( Constants.MARK_CATEGORY_LIST, getReferenceListCategory( categoryList ) );
        model.put( Constants.MARK_CATEGORY_DEFAULT_LIST, StringUtils.EMPTY );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RSS_CALENDAR, request.getLocale( ), model );

        XPage page = new XPage( );
        page.setContent( template.getHtml( ) );
        page.setTitle( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_RSS_TITLE, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( Constants.PROPERTY_PAGE_RSS_TITLE, request.getLocale( ) ) );

        return page;
    }

    /**
     * Performs confirm unsubscription process
     * @param request The http request
     * @throws SiteMessageException The error message handled by the front
     *             office
     */
    public void getConfirmUnSubscribe( HttpServletRequest request ) throws SiteMessageException
    {
        UrlItem urlItem = new UrlItem( request.getRequestURI( ) );
        urlItem.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_UNSUBSCRIBE );
        urlItem.addParameter( Constants.PARAMETER_EMAIL, request.getParameter( Constants.PARAMETER_EMAIL ) );
        urlItem.addParameter( Constants.PARAM_AGENDA, request.getParameter( Constants.PARAM_AGENDA ) );
        SiteMessageService.setMessage( request, Constants.PROPERTY_CONFIRM_UNSUBSCRIPTION_ALERT_MESSAGE, null,
                Constants.PROPERTY_CONFIRM_UNSUBSCRIPTION_TITLE_MESSAGE, urlItem.getUrl( ), null,
                SiteMessage.TYPE_CONFIRMATION );
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
            strDate = Utils.getDateToday( );
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
                view = new DayCalendarView( );
            }
            else if ( strView.equals( Constants.VIEW_WEEK ) )
            {
                view = new WeekCalendarView( );
            }
            else if ( strView.equals( Constants.VIEW_MONTH ) )
            {
                view = new MonthCalendarView( );
            }
            else
            {
                // Default view
                view = new MonthCalendarView( );
            }

            HttpSession session = request.getSession( true );
            session.setAttribute( Constants.ATTRIBUTE_CALENDAR_VIEW, view );
        }
        else
        {
            HttpSession session = request.getSession( );
            CalendarView viewCurrentSession = (CalendarView) session.getAttribute( Constants.ATTRIBUTE_CALENDAR_VIEW );

            if ( viewCurrentSession != null )
            {
                view = viewCurrentSession;
            }
            else
            {
                // Default view
                view = new MonthCalendarView( );
            }
        }

        return view;
    }

    /**
     * Get user options from the request parameter or stored in the session or
     * in cookies
     * @param request The HTTP request
     * @return A CalendarUserOptions
     */
    private CalendarUserOptions getUserOptions( HttpServletRequest request )
    {
        CalendarUserOptions options = new CalendarUserOptions( );
        options.setLocale( request.getLocale( ) );
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
        String strLegend = StringUtils.EMPTY;

        if ( multiAgenda != null && !multiAgenda.getAgendas( ).isEmpty( ) )
        {
            Map<String, Object> model = new HashMap<String, Object>( );

            List<Agenda> listAgendas = multiAgenda.getAgendas( );
            List<AgendaResource> listAgendaResource = new ArrayList<AgendaResource>( );

            for ( Agenda agenda : listAgendas )
            {
                if ( agenda != null )
                {
                    AgendaResource agendaResource = _calendarService.getAgendaResource( agenda.getKeyName( ) );
                    if ( agendaResource != null )
                    {
                        listAgendaResource.add( agendaResource );
                    }
                }
            }

            model.put( Constants.MARK_AGENDA_RESOURCE_LIST, listAgendaResource );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CALENDAR_LEGEND, options.getLocale( ),
                    model );
            strLegend = template.getHtml( );
        }

        return strLegend;
    }

    /**
     * The Creation action of an event
     * @param strCalendarId The identifier of the Calendar
     * @param request The HttpRequest
     * @param plugin The Plugin
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     *             Exception used by the front Message mechanism
     * @throws UserNotSignedException
     */
    private void doCreateEvent( HttpServletRequest request, Plugin plugin ) throws SiteMessageException
    {
        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );
        if ( StringUtils.isNotBlank( strCalendarId ) && StringUtils.isNumeric( strCalendarId ) )
        {
            //Creation of the event
            String strDate = request.getParameter( Constants.PARAMETER_EVENT_DATE );
            String strEventTitle = request.getParameter( Constants.PARAMETER_EVENT_TITLE );

            int nCalendarId = Integer.parseInt( strCalendarId );
            //Mandatory fields
            verifyFieldFilled( request, strDate );
            verifyFieldFilled( request, strEventTitle );

            //Convert the date in form to a java.util.Date object
            Date dateEvent = DateUtil.formatDate( strDate, request.getLocale( ) );

            if ( dateEvent == null )
            {
                errorDateFormat( request );
            }

            SimpleEvent event = new SimpleEvent( );
            event.setIdCalendar( nCalendarId );
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

            AgendaResource agenda = _calendarService.getAgendaResource( nCalendarId );

            // Set the event status
            if ( hasManagerRole( agenda, request ) )
            {
                // If the user has the manager role, then the event is automatically set as the default status (set in calendar.properties)
                event.setStatus( AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_DEFAULT ) );
            }
            else
            {
                // Otherwise, the event is set as tentative
                event.setStatus( AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_TENTATIVE ) );
            }

            LuteceUser user;
            try
            {
                user = getUser( request );
                _eventListService.doAddEvent( event, user, plugin );
            }
            catch ( PageNotFoundException e )
            {
                _eventListService.doAddEvent( event, null, plugin );
            }
            catch ( UserNotSignedException e )
            {
                _eventListService.doAddEvent( event, null, plugin );
            }

            // Send email to notify an event has been created if the event has the status 'confirmed'
            int nSubscriber = _agendaSubscriberService.getSubscriberNumber( nCalendarId, plugin );
            Collection<CalendarSubscriber> listSubscribers = new ArrayList<CalendarSubscriber>( );
            if ( nSubscriber > 0
                    && AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_CONFIRMED ).equals(
                            event.getStatus( ) ) )
            {
                listSubscribers = _agendaSubscriberService.getSubscribers( nCalendarId, plugin );
            }
            // Notify the webmaster an event has been created
            CalendarSubscriber webmaster = new CalendarSubscriber( );
            webmaster.setEmail( AppPropertiesService.getProperty( Constants.PROPERTY_WEBMASTER_EMAIL ) );
            listSubscribers.add( webmaster );
            _agendaSubscriberService.sendSubscriberMail( request, listSubscribers, event, nCalendarId );
        }
    }

    /**
     * Method modifying the event
     * 
     * @param request The request
     * @param plugin The plugin
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     *             Exception used by the front Message mechanism
     * @throws UserNotSignedException
     */
    private void doModifyEvent( HttpServletRequest request, Plugin plugin ) throws SiteMessageException,
            UserNotSignedException
    {
        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );
        String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
        if ( StringUtils.isNotBlank( strCalendarId ) && StringUtils.isNumeric( strCalendarId )
                && StringUtils.isNotBlank( strEventId ) && StringUtils.isNumeric( strEventId ) )
        {
            int nCalendarId = Integer.parseInt( strCalendarId );
            int nEventId = Integer.parseInt( strEventId );
            if ( verifiyUserAccess( request, nCalendarId, nEventId, plugin ) )
            {
                String strEventDate = request.getParameter( Constants.PARAMETER_EVENT_DATE );
                verifyFieldFilled( request, strEventDate );

                //Convert the date in form to a java.util.Date object
                Date dateEvent = DateUtil.formatDate( strEventDate, request.getLocale( ) );

                if ( dateEvent == null )
                {
                    errorDateFormat( request );
                }

                SimpleEvent event = _eventListService.getEvent( nEventId, plugin );
                AgendaResource agenda = _calendarService.getAgendaResource( nCalendarId );

                List<OccurrenceEvent> listOccurrenceEvents = _eventListService.getOccurrenceEvents( nCalendarId,
                        nEventId, Constants.SORT_ASC, plugin );

                // Check security access
                if ( hasManagerRole( agenda, request ) )
                {
                    for ( OccurrenceEvent occurrenceEvent : listOccurrenceEvents )
                    {
                        if ( event.getDate( ).equals( occurrenceEvent.getDate( ) ) )
                        {
                            event.setStatus( occurrenceEvent.getStatus( ) );
                            break;
                        }
                    }
                }
                else
                {
                    event.setStatus( AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_TENTATIVE ) );
                    for ( OccurrenceEvent occurrenceEvent : listOccurrenceEvents )
                    {
                        occurrenceEvent.setStatus( event.getStatus( ) );
                        _eventListService.doModifyOccurrenceEvent( occurrenceEvent, plugin );
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

                _eventListService.doModifySimpleEvent( event, true, getUser( request ), plugin );
            }
            else
            {
                SiteMessageService.setMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP );
            }
        }
    }

    /**
     * Remove an event
     * @param request {@link HttpServletRequest}
     * @param plugin {@link Plugin}
     * @throws UserNotSignedException exception if user is not connected
     * @throws SiteMessageException message if error
     */
    private void doRemoveEvent( HttpServletRequest request, Plugin plugin ) throws UserNotSignedException,
            SiteMessageException
    {
        String strCalendarId = request.getParameter( Constants.PARAMETER_CALENDAR_ID );
        String strEventId = request.getParameter( Constants.PARAMETER_EVENT_ID );
        if ( StringUtils.isNotBlank( strCalendarId ) && StringUtils.isNumeric( strCalendarId )
                && StringUtils.isNotBlank( strEventId ) && StringUtils.isNumeric( strEventId ) )
        {
            int nCalendarId = Integer.parseInt( strCalendarId );
            int nEventId = Integer.parseInt( strEventId );
            if ( verifiyUserAccess( request, nCalendarId, nEventId, plugin ) )
            {
                _eventListService.doRemoveEvent( nCalendarId, nEventId, getUser( request ), plugin );
            }
            else
            {
                SiteMessageService.setMessage( request, Messages.USER_ACCESS_DENIED, SiteMessage.TYPE_STOP );
            }
        }
    }

    private void doVerifySubscription( HttpServletRequest request, Plugin plugin ) throws SiteMessageException
    {
        String strEmail = request.getParameter( Constants.PARAMETER_EMAIL );
        if ( StringUtils.isNotBlank( strEmail ) && StringUtil.checkEmail( strEmail ) )
        {
            if ( _captchaService != null && !_captchaService.validate( request ) )
            {
                //invalid captcha
                SiteMessageService.setMessage( request, PROPERTY_CAPTCHA_INVALID_MESSAGE,
                        PROPERTY_CAPTCHA_INVALID_TITLE_MESSAGE, SiteMessage.TYPE_INFO );
            }
            else
            {
                String strAgenda = request.getParameter( Constants.PARAM_AGENDA );
                if ( StringUtils.isNotBlank( strAgenda ) && StringUtils.isNumeric( strAgenda ) )
                {
                    int nAgendaId = Integer.parseInt( strAgenda );
                    AgendaResource agenda = _calendarService.getAgendaResource( nAgendaId );
                    _agendaSubscriberService.doNotificationSubscription( agenda, request, plugin );
                }
            }
        }
        else
        {
            SiteMessageService.setMessage( request, PROPERTY_INVALID_MAIL_ERROR_MESSAGE,
                    PROPERTY_INVALID_MAIL_TITLE_MESSAGE, SiteMessage.TYPE_STOP );
        }
    }

    /**
     * Method verifies whether a mandatory field is filled
     * @param request The HttpRequest
     * @param strField The field to be checked
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     *             Exception used by the front Message mechanism
     */
    private void verifyFieldFilled( HttpServletRequest request, String strField ) throws SiteMessageException
    {
        if ( StringUtils.isBlank( strField ) )
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_STOP );
        }
    }

    /**
     * Verifiy if the current user has the right to manage the event
     * @param request HttpServletRequest
     * @param nCalendarId The id of the calendar
     * @param nEventId The id of the event
     * @param plugin Plugin
     * @return True if the user has the right, false otherwise
     * @throws UserNotSignedException
     */
    private boolean verifiyUserAccess( HttpServletRequest request, int nCalendarId, int nEventId, Plugin plugin )
            throws UserNotSignedException
    {
        boolean bIsVerified = false;
        AgendaResource agenda = _calendarService.getAgendaResource( nCalendarId );

        if ( hasManagerRole( agenda, request ) )
        {
            bIsVerified = true;
        }
        else
        {
            LuteceUser user = getUser( request );
            if ( user != null )
            {
                List<SimpleEvent> listEvents = _eventListService.getSimpleEventsByUserLogin( nCalendarId, user );
                for ( SimpleEvent event : listEvents )
                {
                    if ( event.getId( ) == nEventId )
                    {
                        bIsVerified = true;
                        break;
                    }
                }
            }
        }
        return bIsVerified;
    }

    /**
     * Verifies the date format
     * @param request The HttpRequest
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     *             Exception used by the front Message mechanism
     */
    private void errorDateFormat( HttpServletRequest request ) throws SiteMessageException
    {
        SiteMessageService.setMessage( request, PROPERTY_INVALID_DATE_MESSAGE, null,
                PROPERTY_INVALID_DATE_TITLE_MESSAGE, null, null, SiteMessage.TYPE_STOP );
    }

    /**
     * Verifies the time format
     * @param request The HttpRequest
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException
     *             Exception used by the front Message mechanism
     */
    private void errorTimeFormat( HttpServletRequest request ) throws SiteMessageException
    {
        SiteMessageService.setMessage( request, PROPERTY_INVALID_TIME_MESSAGE, null,
                PROPERTY_INVALID_TIME_TITLE_MESSAGE, null, null, SiteMessage.TYPE_STOP );
    }

    /**
     * Performs the subscription process
     * @param request The Http request
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException The
     *             error message thrown to the user
     */
    public void doSubscription( HttpServletRequest request ) throws SiteMessageException
    {
        AgendaSubscriberService.getInstance( ).doValidationSubscription( request );
    }

    /**
     * Send a mail to a friend
     * @param request The request
     * @return The next URL to redirect to
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException The
     *             error message thrown to the user
     */
    public String doSendToFriend( HttpServletRequest request ) throws SiteMessageException
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
        if ( StringUtils.isEmpty( strFriendEmail ) || StringUtils.isEmpty( strSenderFirstName )
                || StringUtils.isEmpty( strSenderLastName ) || StringUtils.isEmpty( strSenderEmail )
                || StringUtils.isEmpty( strSenderMessage ) )
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
        String strObject = I18nService.getLocalizedString( PROPERTY_EMAIL_FRIEND_OBJECT, request.getLocale( ) );
        String strBaseUrl = AppPathService.getBaseUrl( request );

        Map<String, Object> emailModel = new HashMap<String, Object>( );
        emailModel.put( MARK_SENDER_MESSAGE, strSenderMessage );
        emailModel.put( MARK_BASE_URL, strBaseUrl );
        emailModel.put( Constants.PARAMETER_EVENT_ID, strIdEvent );
        emailModel.put( Constants.PARAMETER_CALENDAR_ID, nIdCalendar );
        emailModel.put( Constants.MARK_ACTION, Constants.ACTION_SHOW_RESULT );

        HtmlTemplate templateAgenda = AppTemplateService.getTemplate( TEMPLATE_SEND_NOTIFICATION_MAIL,
                request.getLocale( ), emailModel );

        String strNewsLetterCode = templateAgenda.getHtml( );

        MailService.sendMailHtml( strFriendEmail, strSenderName, strSenderEmail, strObject, strNewsLetterCode );

        return URL_JSP_RETURN_SEND_FRIEND_MAIL + Constants.ACTION_SHOW_RESULT + "&" + Constants.PARAMETER_EVENT_ID
                + "=" + strIdEvent + "&" + Constants.PARAM_AGENDA + "=" + strIdCalendar;
    }

    /**
     * Return the list of agenda for the template html search
     * @param request The request
     * @param plugin The plugin
     * @return The reference list of agendas
     */
    public ReferenceList getListAgenda( HttpServletRequest request, Plugin plugin )
    {
        ReferenceList listAgendas = new ReferenceList( );

        for ( AgendaResource a : _calendarService.getAgendaResources( request ) )
            if ( a != null )
            {
                listAgendas.addItem( a.getId( ), a.getName( ) );
            }

        return listAgendas;
    }

    /**
     * Return the list of agenda for the template html search
     * @param request The request
     * @param plugin The plugin
     * @return The reference list of sheets
     */
    public ReferenceList getExportSheetList( HttpServletRequest request, Plugin plugin )
    {
        ReferenceList listSheets = new ReferenceList( );

        Collection<StyleSheet> collectionSheets = CalendarStyleSheetHome.getStyleSheetList( plugin );

        if ( collectionSheets != null )
        {
            Iterator<StyleSheet> i = collectionSheets.iterator( );

            while ( i.hasNext( ) )
            {
                StyleSheet sheet = i.next( );
                listSheets.addItem( sheet.getId( ), sheet.getDescription( ) );
            }
        }

        return listSheets;
    }

    /**
     * Return the list
     * @param collection The collection of categories
     * @return a refenceList
     */
    private ReferenceList getReferenceListCategory( Collection<Category> collection )
    {
        ReferenceList list = new ReferenceList( );

        if ( collection != null )
        {
            Iterator<Category> i = collection.iterator( );

            while ( i.hasNext( ) )
            {
                Category category = i.next( );
                list.addItem( category.getId( ), category.getName( ) );
            }
        }

        return list;
    }

    /**
     * Check whether the current user has the manager role or not
     * @param agenda the {@link AgendaResource}
     * @param request {@link HttpServletRequest}
     * @return true if the user has the manager role, false otherwise
     */
    private boolean hasManagerRole( AgendaResource agenda, HttpServletRequest request )
    {
        boolean bHasManagerRole = false;
        if ( agenda != null )
        {
            String strManagerRole = agenda.getRoleManager( );

            bHasManagerRole = hasRole( strManagerRole, request );
        }
        return bHasManagerRole;
    }

    /**
     * Check whether the current user has the given role or not
     * @param strRole the role
     * @param request {@link HttpServletRequest}
     * @return true if the user has the role, false otherwise
     */
    private boolean hasRole( String strRole, HttpServletRequest request )
    {
        boolean bHasRole = false;

        if ( StringUtils.isNotBlank( strRole ) && !Constants.PROPERTY_ROLE_NONE.equals( strRole )
                && SecurityService.isAuthenticationEnable( )
                && SecurityService.getInstance( ).isUserInRole( request, strRole ) )
        {
            bHasRole = true;
        }

        return bHasRole;
    }

    /**
     * Gets the user from the request
     * @param request The HTTP user
     * @return The Lutece User
     * @throws UserNotSignedException exception if user not connected
     * @throws PageNotFoundException If the authentication is not enabled
     */
    private LuteceUser getUser( HttpServletRequest request ) throws UserNotSignedException, PageNotFoundException
    {
        if ( SecurityService.isAuthenticationEnable( ) )
        {
            LuteceUser user = SecurityService.getInstance( ).getRemoteUser( request );

            if ( user == null )
            {
                throw new UserNotSignedException( );
            }

            return user;
        }
        throw new PageNotFoundException( );
    }
}
