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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.calendar.business.parameter.CalendarParameterHome;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.plugins.calendar.service.CalendarResourceIdService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.dashboard.admin.AdminDashboardComponent;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * 
 * DatabaseAdminDashboardComponent
 *
 */
public class CalendarAdminDashboardComponent extends AdminDashboardComponent
{	
	// TEMPLATES
	private static final String TEMPLATE_ADMIN_DASHBOARD = "admin/plugins/calendar/calendar_admindashboard.html";
	
	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String getDashboardData( AdminUser user, HttpServletRequest request )
	{
		if ( RBACService.isAuthorized( CalendarResourceIdService.RESOURCE_TYPE, 
        		RBAC.WILDCARD_RESOURCES_ID,	CalendarResourceIdService.PERMISSION_MANAGE, user ) )
		{
			Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
			Map<String, Object> model = new HashMap<String, Object>(  );
	    	model.put( Constants.MARK_CALENDAR_PARAMETERS, CalendarParameterHome.findAll( plugin ) );
			HtmlTemplate template = new HtmlTemplate(  );
			template = AppTemplateService.getTemplate( TEMPLATE_ADMIN_DASHBOARD, user.getLocale(  ), model );
			return template.getHtml(  );
		}
		
		return Constants.EMPTY_STRING;
	}
}
