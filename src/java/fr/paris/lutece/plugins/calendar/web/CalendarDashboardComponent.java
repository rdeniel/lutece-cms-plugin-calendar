/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.CalendarSubscriberHome;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.business.parameter.CalendarParameterHome;
import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.portal.business.right.Right;
import fr.paris.lutece.portal.business.right.RightHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.DashboardComponent;
import fr.paris.lutece.portal.service.database.AppConnectionService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;


/**
 * Calendar Dashboard Component
 * This component displays calendars and events
 */
public class CalendarDashboardComponent extends DashboardComponent
{
	private static final int ZONE_1 = 1;
	private static final String EMPTY_STRING = "";
    private static final String TEMPLATE_DASHBOARD_ZONE_1 = "/admin/plugins/calendar/calendar_dashboard_zone_1.html";
    private static final String TEMPLATE_DASHBOARD_OTHER_ZONE = "/admin/plugins/calendar/calendar_dashboard_other_zone.html";

    /**
     * The HTML code of the component
     * @param user The Admin User
	 * @param request HttpServletRequest
     * @return The dashboard component
     */
    public String getDashboardData( AdminUser user, HttpServletRequest request )
    {
        Right right = RightHome.findByPrimaryKey( getRight(  ) );
        Plugin plugin = PluginService.getPlugin( right.getPluginName(  ) );
		if ( !( plugin.getDbPoolName(  ) != null && 
				!AppConnectionService.NO_POOL_DEFINED.equals( plugin.getDbPoolName(  ) ) ) ) 
		{
			return EMPTY_STRING;
		}
        UrlItem url = new UrlItem( right.getUrl(  ) );
        url.addParameter( CalendarPlugin.PLUGIN_NAME, right.getPluginName(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        List<AgendaResource> calendarsList = getCalendarsList( user, plugin );
        ReferenceItem nbEvents = CalendarParameterHome.findByKey( Constants.PARAMETER_DASHBOARD_NB_EVENTS, plugin );
        int nNbEvents = StringUtil.getIntValue( nbEvents.getName(  ), 0 );
        
        model.put( Constants.MARK_CALENDARS_LIST, calendarsList );
        model.put( Constants.MARK_EVENT_LIST, getEventsList( calendarsList, plugin ) );
        model.put( Constants.MARK_NB_EVENTS_MAX, nNbEvents );
        model.put( Constants.MARK_NB_SUBSCRIBERS_LIST, getNbSubscribersList( calendarsList, plugin ) );
        model.put( Constants.MARK_URL, url.getUrl(  ) );
        model.put( Constants.MARK_ICON, plugin.getIconUrl(  ) );

        HtmlTemplate t = AppTemplateService.getTemplate( getTemplateDashboard(  ), user.getLocale(  ), model );

        return t.getHtml(  );
    }
    
    /**
     * Get the list of calendars
     * @param user the current user
     * @param plugin Plugin
     * @return the list of calendars
     */
    private List<AgendaResource> getCalendarsList( AdminUser user, Plugin plugin )
    {
    	List<AgendaResource> calendarsList = CalendarHome.findAgendaResourcesList( plugin );
        return (List<AgendaResource>) AdminWorkgroupService.getAuthorizedCollection( calendarsList, user );
    }
    
    /**
     * Get the list of numbers subscribers
     * @param calendarsList the list of calendars
     * @param plugin Plugin
     * @return the list of numbers of subscribers
     */
    private Map<String, String> getNbSubscribersList( List<AgendaResource> calendarsList, Plugin plugin )
    {
    	Map<String, String> nbSubscribersList = new HashMap<String, String>(  );
    	for ( AgendaResource calendar : calendarsList )
    	{
    		int nIdCalendar = StringUtil.getIntValue( calendar.getId(  ), 0 );
    		int nNbSubscribers = CalendarSubscriberHome.findSubscriberNumber( nIdCalendar, plugin );
    		nbSubscribersList.put( calendar.getId(  ), nNbSubscribers + Constants.EMPTY_STRING );
    	}
    	return nbSubscribersList;
    }
    
    /**
     * get the list of events
     * @param calendarsList the list of calendars
     * @param plugin Plugin
     * @return the list of events
     */
    private List<SimpleEvent> getEventsList( List<AgendaResource> calendarsList, Plugin plugin )
    {
    	ReferenceItem nextDays = CalendarParameterHome.findByKey( Constants.PARAMETER_DASHBOARD_N_NEXT_DAYS, plugin );
    	int nNextDays = StringUtil.getIntValue( nextDays.getName(  ), 0 );
    	List<SimpleEvent> eventsList = new ArrayList<SimpleEvent>(  );
    	for ( AgendaResource calendar : calendarsList )
    	{
    		int nIdCalendar = StringUtil.getIntValue( calendar.getId(  ), 0 );
    		eventsList.addAll( CalendarHome.findEventsList( nIdCalendar, 1, nNextDays, plugin ) );
    	}
    	return eventsList;
    }
    
    /**
     * Get the template
     * @return the template
     */
    private String getTemplateDashboard(  )
    {
    	if ( getZone(  ) == ZONE_1 )
    	{
    		return TEMPLATE_DASHBOARD_ZONE_1;
    	}
    	
    	return TEMPLATE_DASHBOARD_OTHER_ZONE;
    }
}
