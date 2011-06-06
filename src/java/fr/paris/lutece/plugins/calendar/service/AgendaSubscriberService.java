/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.calendar.business.CalendarSubscriber;
import fr.paris.lutece.plugins.calendar.business.CalendarSubscriberHome;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.business.notification.CalendarNotification;
import fr.paris.lutece.plugins.calendar.business.notification.CalendarNotificationHome;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;


/**
 * The class responsible for the subscription and unsubscription process
 */
public class AgendaSubscriberService
{
    public static final String JSP_URL_DO_UNSUBSCRIBE = "/jsp/site/plugins/calendar/DoUnsubscribeCalendar.jsp";

    //Messages
    private static final String PROPERTY_INVALID_MAIL_TITLE_MESSAGE = "calendar.siteMessage.invalid_mail.title";
    private static final String PROPERTY_INVALID_MAIL_ERROR_MESSAGE = "calendar.siteMessage.invalid_mail.message";
    private static final String PROPERTY_SUBSCRIPTION_OK_TITLE_MESSAGE = "calendar.siteMessage.subscription_ok.title";
    private static final String PROPERTY_SUBSCRIPTION_OK_ALERT_MESSAGE = "calendar.siteMessage.subscription_ok.message";
    private static final String PROPERTY_UNSUBSCRIPTION_OK_TITLE_MESSAGE = "calendar.siteMessage.unsubscription_ok.title";
    private static final String PROPERTY_UNSUBSCRIPTION_OK_ALERT_MESSAGE = "calendar.siteMessage.unsubscription_ok.message";
    private static final String PROPERTY_NO_CALENDAR_CHOSEN_TITLE_MESSAGE = "calendar.siteMessage.no_calendar_chosen.title";
    private static final String PROPERTY_NO_CALENDAR_CHOSEN_ERROR_MESSAGE = "calendar.siteMessage.no_calendar_chosen.message";
    private static final String PROPERTY_EXPIRATION_SUBSCRIPTION_TITLE_MESSAGE = "calendar.siteMessage.expiration_subscription.title";
    private static final String PROPERTY_EXPIRATION_SUBSCRIPTION_ALERT_MESSAGE = "calendar.siteMessage.expiration_subscription.message";
    private static final String PROPERTY_INVALID_KEY_SUBSCRIPTION_TITLE_MESSAGE = "calendar.siteMessage.invalid_key_subscription.title";
    private static final String PROPERTY_INVALID_KEY_SUBSCRIPTION_ALERT_MESSAGE = "calendar.siteMessage.invalid_key_subscription.message";
    private static final String PROPERTY_SUBSCRIPTION_MAIL_SEND_TITLE_MESSAGE = "calendar.siteMessage.mail_send_subscription.title";
    private static final String PROPERTY_SUBSCRIPTION_MAIL_SEND_ALERT_MESSAGE = "calendar.siteMessage.mail_send_subscription.message";
    private static final String PROPERTY_REDIRECTION_TITLE_MESSAGE = "calendar.siteMessage.redirection_subscription.title";
    private static final String PROPERTY_REDIRECTION_ALERT_MESSAGE = "calendar.siteMessage.redirection_subscription.message";

    //Templates
    private static final String TEMPLATE_SEND_NOTIFICATION_MAIL = "skin/plugins/calendar/notification_email.html";
    private static final String TEMPLATE_NOTIFY_SUBSCRIPTION_MAIL = "skin/plugins/calendar/subscription_notification_mail.html";
    private static final String MARK_SUBSCRIBER_EMAIL = "subscriber_email";
    private static final String MARK_BASE_URL = "base_url";
    private static final String MARK_SENDER_MESSAGE = "sender_message";
    private static final String MARK_EMAIL_CONTENT = "email_content";
    private static final String MARK_UNSUBSCRIBE_LINK = "unsubscribe_link";
    private static final String MARK_EVENT = "event";
    private static final String MARK_LINK = "link";
    private static final String MARK_MESSAGE = "message";

    //properties
    private static final String PROPERTY_UNSUBSCRIBE_LINK = "calendar.unsubscribe.link";
    private static final String PROPERTY_SENDER_NAME = "calendar.sender.name";
    private static final String PROPERTY_SENDER_EMAIL = "calendar.sender.email";
    private static final String PROPERTY_EMAIL_SUBSCRIBER_CONTENT = "calendar.subscriber.email.content";
    private static final String PROPERTY_EMAIL_SUBSCRIBER_OBJECT = "calendar.subscriber.email.object";
    private static final String PROPERTY_EMAIL_FRIEND_OBJECT = "calendar.friend.email.object";
    private static final String PROPERTY_SUBSCRIBE_HTML_LINK = "calendar.notification.subscription.link";
    private static final String PROPERTY_SUBSCRIBE_HTML_MESSAGE = "calendar.notification.subscription.message";
    private static final String PROPERTY_SUBSCRIBE_HTML_OBJECT = "calendar.notification.subscription.object";
    
    //Parameters
    private static final String HTML_LINK_CLOSE = "</a>";
    private static final String HTML_LINK_OPEN_1 = "<a href=\"";
    private static final String HTML_LINK_OPEN_2 = "\" >";

    //JSP
    private static final String URL_JSP_RETURN_SEND_FRIEND_MAIL = "../../Portal.jsp?page=calendar&action=get_friend_email_page";
    private static final String URL_JSP_SUBSCRIPTION_NOTIFICATION = "jsp/site/plugins/calendar/DoSubscriptionCalendar.jsp";
    private static final String URL_JSP_SUBSCRIPTION_REDIRECTION = "plugins/calendar/DoSubscriptionCalendar.jsp";
    private static final String URL_JSP_PAGE_PORTAL = "jsp/site/Portal.jsp";

    /**
     * The registration service
     */
    private static AgendaSubscriberService _singleton;

    /**
    * Constructor
    */
    private AgendaSubscriberService(  )
    {
        if ( _singleton == null )
        {
            _singleton = this;
        }
    }

    /**
     * Fetches the singleton instance
     * @return The singleton instance
     */
    public static AgendaSubscriberService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new AgendaSubscriberService(  );
        }

        return _singleton;
    }

    /**
     * Performs the subscription process
     * Throw a SiteMessage
     * @param request The Http request
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException The error message thrown to the user
     */
    public void doSubscription( HttpServletRequest request )
        throws SiteMessageException
    {
        String strEmail = request.getParameter( Constants.PARAMETER_EMAIL );
        String strAgenda = request.getParameter( Constants.PARAM_AGENDA );
        String strPluginName = request.getParameter( Constants.PARAMETER_PLUGIN_NAME );
        strPluginName = ( !( strPluginName == null ) ) ? strPluginName : Constants.PLUGIN_NAME;

        if ( ( strEmail == null ) || !StringUtil.checkEmail( strEmail ) )
        {
            SiteMessageService.setMessage( request, PROPERTY_INVALID_MAIL_ERROR_MESSAGE,
                PROPERTY_INVALID_MAIL_TITLE_MESSAGE, SiteMessage.TYPE_STOP );
        }

        Plugin plugin = PluginService.getPlugin( strPluginName );

        if ( ( strAgenda == null ) || ( strAgenda.equals( "" ) ) )
        {
            SiteMessageService.setMessage( request, PROPERTY_NO_CALENDAR_CHOSEN_ERROR_MESSAGE,
                PROPERTY_NO_CALENDAR_CHOSEN_TITLE_MESSAGE, SiteMessage.TYPE_STOP );
        }

        //Checks if a subscriber with the same email address doesn't exist yet
        CalendarSubscriber subscriber = CalendarSubscriberHome.findByEmail( strEmail, plugin );

        if ( subscriber == null )
        {
            // The email doesn't exist, so create a new subcriber
            subscriber = new CalendarSubscriber(  );
            subscriber.setEmail( strEmail );
            CalendarSubscriberHome.create( subscriber, plugin );
        }
        CalendarSubscriberHome.addSubscriber( Integer.parseInt( strAgenda ), subscriber.getId(  ),
                new Timestamp( new Date(  ).getTime(  ) ), plugin );

        SiteMessageService.setMessage( request, PROPERTY_SUBSCRIPTION_OK_ALERT_MESSAGE,
            PROPERTY_SUBSCRIPTION_OK_TITLE_MESSAGE, SiteMessage.TYPE_INFO );
    }

    /**
     * Performs unsubscription process
     * Throw a SiteMessage
     * @param request The http request
     * @throws fr.paris.lutece.portal.service.message.SiteMessageException The error message handled by the front office
     */
    public void doUnSubscribe( HttpServletRequest request, Plugin plugin )
        throws SiteMessageException
    {
        String strEmail = request.getParameter( Constants.PARAMETER_EMAIL );
        String strAgenda = request.getParameter( Constants.PARAM_AGENDA );
        if ( StringUtils.isNotBlank( strEmail ) )
        {
        	if ( StringUtils.isNotBlank( strAgenda ) && StringUtils.isNumeric( strAgenda ) )
            {
        		CalendarSubscriber subscriber = CalendarSubscriberHome.findByEmail( strEmail, plugin );

                CalendarSubscriberHome.removeSubscriber( subscriber.getId(  ), Integer.parseInt( strAgenda ), plugin );
                if ( !CalendarSubscriberHome.isUserSubscribed( subscriber.getId(  ), plugin ) )
                {
                	CalendarSubscriberHome.remove( subscriber.getId(  ), plugin );
                }

                SiteMessageService.setMessage( request, PROPERTY_UNSUBSCRIPTION_OK_ALERT_MESSAGE,
                    PROPERTY_UNSUBSCRIPTION_OK_TITLE_MESSAGE, SiteMessage.TYPE_INFO );
            }
        	else
        	{
        		 SiteMessageService.setMessage( request, PROPERTY_NO_CALENDAR_CHOSEN_ERROR_MESSAGE,
                         PROPERTY_NO_CALENDAR_CHOSEN_TITLE_MESSAGE, SiteMessage.TYPE_STOP );
        	}
        }
        else
        {
        	SiteMessageService.setMessage( request, PROPERTY_INVALID_MAIL_ERROR_MESSAGE,
                    PROPERTY_INVALID_MAIL_TITLE_MESSAGE, SiteMessage.TYPE_STOP );
        }
    }

    /**
     * Send the mail
     * @param strRecipientTo Subscriber email
     * @param strSenderName The sender name.
     * @param strSenderEmail The sender email address.
     * @param strSubject The message subject.
     * @param strMessage The message.
     */
    public void doSendMail( String strRecipientTo, String strSenderName, String strSenderEmail, String strSubject,
        String strMessage )
    {
        MailService.sendMailHtml( strRecipientTo, strSenderName, strSenderEmail, strSubject, strMessage );
    }

    /**
     * Send an event to a list of subscribers
     * @param request the http request
     * @param listSubscribers a Collection<CalendarSubscriber>
     * @param event the event
     */
    public void sendSubscriberMail( HttpServletRequest request, Collection<CalendarSubscriber> listSubscribers,
        Event event, int nCalendarId )
    {
        String strUnsubscribelink = AppPropertiesService.getProperty( PROPERTY_UNSUBSCRIBE_LINK );
        String strSenderName = AppPropertiesService.getProperty( PROPERTY_SENDER_NAME );
        String strSenderEmail = AppPropertiesService.getProperty( PROPERTY_SENDER_EMAIL );
        String strContent = I18nService.getLocalizedString( PROPERTY_EMAIL_SUBSCRIBER_CONTENT, request.getLocale(  ) );
        String strObject = I18nService.getLocalizedString( PROPERTY_EMAIL_SUBSCRIBER_OBJECT, request.getLocale(  ) );

        String strBaseUrl = AppPathService.getBaseUrl( request );

        HashMap<String, Object> emailModel = new HashMap<String, Object>(  );

        for ( CalendarSubscriber subscriber : listSubscribers )
        {
            emailModel.put( MARK_UNSUBSCRIBE_LINK, strUnsubscribelink );
            emailModel.put( MARK_EMAIL_CONTENT, strContent );
            emailModel.put( MARK_BASE_URL, strBaseUrl );
            emailModel.put( MARK_EVENT, event );
            emailModel.put( Constants.MARK_CALENDAR_ID, nCalendarId );
            emailModel.put( Constants.MARK_EVENT_ID, event.getId(  ) );
            emailModel.put( MARK_SUBSCRIBER_EMAIL, subscriber.getEmail(  ) );
            emailModel.put( Constants.PARAMETER_ACTION, Constants.ACTION_SHOW_RESULT );

            HtmlTemplate templateAgenda = AppTemplateService.getTemplate( TEMPLATE_SEND_NOTIFICATION_MAIL,
                    request.getLocale(  ), emailModel );
            String strNewsLetterCode = templateAgenda.getHtml(  );

            MailService.sendMailHtml( subscriber.getEmail(  ), strSenderName, strSenderEmail, strObject,
                strNewsLetterCode );
            emailModel.clear(  );
        }
    }

    /**
     * Send the calendar to a friend
     * @param request the http request
     */
    public String sendFriendMail( HttpServletRequest request )
        throws SiteMessageException
    {
        //Form parameters
        String strFriendEmail = request.getParameter( Constants.PARAMETER_SENDER_FRIEND_EMAIL );
        String strSenderFirstName = request.getParameter( Constants.PARAMETER_SENDER_FIRST_NAME );
        String strSenderLastName = request.getParameter( Constants.PARAMETER_SENDER_LAST_NAME );
        String strSenderEmail = request.getParameter( Constants.PARAMETER_SENDER_EMAIL );
        String strSenderMessage = request.getParameter( Constants.PARAMETER_SENDER_MESSAGE );

        // Mandatory field
        if ( StringUtils.isNotBlank( strFriendEmail ) && StringUtils.isNotBlank( strSenderFirstName ) && 
        		StringUtils.isNotBlank( strSenderLastName ) && StringUtils.isNotBlank( strSenderEmail ) && 
        		StringUtils.isNotBlank( strSenderMessage ) )
        {
        	if ( StringUtil.checkEmail( strFriendEmail ) )
            {
        		if ( StringUtil.checkEmail( strSenderEmail ) )
                {
        			String strSenderName = strSenderFirstName + Constants.SPACE + strSenderLastName;

                    //Properties
                    String strObject = I18nService.getLocalizedString( PROPERTY_EMAIL_FRIEND_OBJECT, request.getLocale(  ) );
                    String strBaseUrl = AppPathService.getBaseUrl( request );

                    HashMap<String, Object> emailModel = new HashMap<String, Object>(  );
                    emailModel.put( MARK_SENDER_MESSAGE, strSenderMessage );
                    emailModel.put( MARK_BASE_URL, strBaseUrl );

                    HtmlTemplate templateAgenda = AppTemplateService.getTemplate( TEMPLATE_SEND_NOTIFICATION_MAIL,
                            request.getLocale(  ), emailModel );

                    String strNewsLetterCode = templateAgenda.getHtml(  );

                    MailService.sendMailHtml( strFriendEmail, strSenderName, strSenderEmail, strObject, strNewsLetterCode );
                }
        		else
        		{
        			Object[] args = { strSenderEmail };
                    SiteMessageService.setMessage( request, PROPERTY_INVALID_MAIL_ERROR_MESSAGE, args,
                        PROPERTY_INVALID_MAIL_TITLE_MESSAGE, SiteMessage.TYPE_STOP );
        		}
            }
        	else
        	{
        		Object[] args = { strFriendEmail };
                SiteMessageService.setMessage( request, PROPERTY_INVALID_MAIL_ERROR_MESSAGE, args,
                    PROPERTY_INVALID_MAIL_TITLE_MESSAGE, SiteMessage.TYPE_STOP );
        	}
        }
        else
        {
        	SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, Messages.MANDATORY_FIELDS,
                    SiteMessage.TYPE_STOP );
        }

        return URL_JSP_RETURN_SEND_FRIEND_MAIL;
    }
    
    /**
     * Notify the subscription
     * @param request HttpServletRequest
     * @throws SiteMessageException site message exception
     */
    public void doNotificationSubscription( AgendaResource agenda, HttpServletRequest request, Plugin plugin )
    	throws SiteMessageException
    {
		String strEmail = request.getParameter( Constants.PARAMETER_EMAIL );
		
		CalendarNotification calendarNotification = new CalendarNotification ();
		//Generate key
		UUID key = java.util.UUID.randomUUID(  );
		calendarNotification.setKey( key.toString(  ) );
		calendarNotification.setEmail( strEmail );
		calendarNotification.setIdAgenda( Integer.parseInt( agenda.getId(  ) ) );
		Calendar calendar = GregorianCalendar.getInstance(  );
		if( agenda.isNotify(  ) )
		{
			calendar.add( Calendar.DAY_OF_MONTH, agenda.getPeriodValidity(  ) );
		}
		else
		{
			calendar.add( Calendar.DAY_OF_MONTH, 1 );
		}
		calendarNotification.setDateExpiry( new Timestamp( calendar.getTimeInMillis(  ) ) );
		CalendarNotificationHome.create( calendarNotification, plugin );
		if(  agenda.isNotify(  ) )
		{
	        String strSenderName = AppPropertiesService.getProperty( PROPERTY_SENDER_NAME );
	        String strSenderEmail = AppPropertiesService.getProperty( PROPERTY_SENDER_EMAIL );
	        String strObject = I18nService.getLocalizedString( PROPERTY_SUBSCRIBE_HTML_OBJECT, request.getLocale(  ) );
	        
            String strBaseUrl = AppPathService.getBaseUrl( request ) + URL_JSP_SUBSCRIPTION_NOTIFICATION;
            UrlItem url = new UrlItem( strBaseUrl );
            url.addParameter( Constants.MARK_KEY, key.toString(  ) );
            
			String strlink = I18nService.getLocalizedString( PROPERTY_SUBSCRIBE_HTML_LINK , request.getLocale(  ) );
			String strMessage = I18nService.getLocalizedString( PROPERTY_SUBSCRIBE_HTML_MESSAGE, request.getLocale(  ) );
			String linkHtml = HTML_LINK_OPEN_1 + url.getUrl(  ) + HTML_LINK_OPEN_2 + strlink + HTML_LINK_CLOSE;
	     
	        HashMap<String, String> emailModel = new HashMap<String, String>(  );
	        emailModel.put( MARK_MESSAGE, strMessage );
	        emailModel.put( MARK_LINK, linkHtml );

            HtmlTemplate templateAgenda = AppTemplateService.getTemplate( TEMPLATE_NOTIFY_SUBSCRIPTION_MAIL,
                    request.getLocale(  ), emailModel );
            String strEmailCode = templateAgenda.getHtml(  );

            MailService.sendMailHtml( strEmail, strSenderName, strSenderEmail, strObject,
            		strEmailCode );
            emailModel.clear(  );
            
            SiteMessageService.setMessage( request, PROPERTY_SUBSCRIPTION_MAIL_SEND_ALERT_MESSAGE,
            		PROPERTY_SUBSCRIPTION_MAIL_SEND_TITLE_MESSAGE, SiteMessage.TYPE_INFO );
		}
		else
		{
			ServletConfig config = LocalVariables.getConfig(  );
			HttpServletResponse response = LocalVariables.getResponse(  );
			try
			{
				String strAdresse = URL_JSP_SUBSCRIPTION_REDIRECTION + Constants.INTERROGATION_MARK + 
						Constants.MARK_KEY + Constants.EQUAL + key;
				response.sendRedirect( strAdresse );
			}
			catch( Exception e )
			{
	            SiteMessageService.setMessage( request, PROPERTY_REDIRECTION_TITLE_MESSAGE,
	            		PROPERTY_REDIRECTION_ALERT_MESSAGE, SiteMessage.TYPE_INFO );
			}
			LocalVariables.setLocal( config, request, response );
		}
    }
    
    /**
     * Validate subscription
     * @param request HttpServletRequest
     * @throws SiteMessageException site message exception
     */
    public void doValidationSubscription( HttpServletRequest request )
    	throws SiteMessageException
    {
    	String strKey = request.getParameter( Constants.MARK_KEY );    	
        String strPluginName = request.getParameter( Constants.PARAMETER_PLUGIN_NAME );
        strPluginName = !( strPluginName == null ) ? strPluginName : Constants.PLUGIN_NAME;

        Plugin plugin = PluginService.getPlugin( strPluginName );
    	CalendarNotification calendarNotification = CalendarNotificationHome.findByPrimaryKey( strKey, plugin );
    	
    	if ( calendarNotification != null )
    	{
			Calendar calendar = GregorianCalendar.getInstance(  );
			Timestamp todaydate = new Timestamp( calendar.getTimeInMillis(  ));
			if ( todaydate.before( calendarNotification.getDateExpiry(  ) ) )
			{
		    	String strEmail = calendarNotification.getEmail();
		        int nIdAgenda = calendarNotification.getIdAgenda();
		    	
		        //Checks if a subscriber with the same email address doesn't exist yet
		        CalendarSubscriber subscriber = CalendarSubscriberHome.findByEmail( strEmail, plugin );
		
		        if ( subscriber == null )
		        {
		            // The email doesn't exist, so create a new subcriber
		            subscriber = new CalendarSubscriber(  );
		            subscriber.setEmail( strEmail );
		            CalendarSubscriberHome.create( subscriber, plugin );
		        }
		        CalendarSubscriberHome.addSubscriber( nIdAgenda, subscriber.getId(  ),
		                new Timestamp( new Date(  ).getTime(  ) ), plugin );
		
		        CalendarNotificationHome.remove( strKey, plugin);
		        
		        SiteMessageService.setMessage( request, PROPERTY_SUBSCRIPTION_OK_ALERT_MESSAGE, SiteMessage.TYPE_INFO, 
		        		AppPathService.getBaseUrl( request ) + URL_JSP_PAGE_PORTAL, 
		        		PROPERTY_SUBSCRIPTION_OK_TITLE_MESSAGE, null );
			}
			else
			{
				SiteMessageService.setMessage( request, PROPERTY_EXPIRATION_SUBSCRIPTION_ALERT_MESSAGE, SiteMessage.TYPE_ERROR, 
		        		AppPathService.getBaseUrl( request ) + URL_JSP_PAGE_PORTAL, 
		        		PROPERTY_EXPIRATION_SUBSCRIPTION_TITLE_MESSAGE, null );
			}
    	}
    	else
    	{
    		SiteMessageService.setMessage( request, PROPERTY_INVALID_KEY_SUBSCRIPTION_ALERT_MESSAGE, SiteMessage.TYPE_ERROR, 
	        		AppPathService.getBaseUrl( request ) + URL_JSP_PAGE_PORTAL, 
	        		PROPERTY_INVALID_KEY_SUBSCRIPTION_TITLE_MESSAGE, null );
    	}
    }

    public int getSubscriberNumber( int nCalendarId, Plugin plugin )
    {
    	return CalendarSubscriberHome.findSubscriberNumber( nCalendarId, plugin );
    }
    
    public Collection<CalendarSubscriber> getSubscribers( int nCalendarId, Plugin plugin )
    {
    	return CalendarSubscriberHome.findSubscribers( nCalendarId, plugin );
    }
}
