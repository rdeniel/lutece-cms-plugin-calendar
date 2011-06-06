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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.MultiAgenda;
import fr.paris.lutece.plugins.calendar.business.parameter.CalendarParameterHome;
import fr.paris.lutece.plugins.calendar.service.cache.CalendarCacheService;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.cache.ICacheKeyService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.PortalJspBean;

/**
 * CalendarService
 */
public class CalendarService
{
	// CONSTANTS
	private static final String NONE = "-";
	private static final String SERVICE_NAME = "Calendar Cache Service";
	
	// CACHE KEYS
	private static final String KEY_CALENDAR = "calendar";
    
    // VARIABLES
    private ICacheKeyService _cksCalendar;
    private CalendarCacheService _cacheCalendar = CalendarCacheService.getInstance(  );

    /** Creates a new instance of CalendarService */
    public CalendarService(  )
    {
        init(  );
    }

    /**
     * Initialize the CalendarService
     */
    private void init(  )
    {
        _cacheCalendar.initCache(  );
    }
    
    /**
     * Get the service name
     * @return the service name
     */
    public String getName(  )
    {
        return SERVICE_NAME;
    }
    
    /**
     * Set the agenda cache key service
     * @param cacheKeyService the cache key service
     */
    public void setCalendarCacheKeyService( ICacheKeyService cacheKeyService )
    {
    	_cksCalendar = cacheKeyService;
    }
    
    /**
     * Get the agenda
     * @param strAgendaKeyName the agenda key name
     * @return The AgendaResource object
     */
    public AgendaResource getAgendaResource( String strAgendaKeyName )
    {
    	AgendaResource agenda = null;
    	if ( StringUtils.isNotBlank( strAgendaKeyName ) && StringUtils.isNumeric( strAgendaKeyName ) )
    	{
    		int nAgendaId = Integer.parseInt( strAgendaKeyName );
    		agenda = getAgendaResource( nAgendaId );
    	}
    	return agenda;
    }
    
    /**
     * Returns the agenda
     * @param nAgendaId the ID agenda
     * @return The AgendaResource object
     */
    public AgendaResource getAgendaResource( int nAgendaId )
    {
        String strKey = getKey( nAgendaId );
        
        AgendaResource agenda = (AgendaResource) _cacheCalendar.getFromCache( strKey );

        if ( agenda == null )
        {
        	Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
            agenda = CalendarHome.findAgendaResource( nAgendaId, plugin );
            Utils.loadAgendaOccurrences( agenda, plugin );
            _cacheCalendar.putInCache( strKey, agenda );
        }
		
        return agenda;
    }
    
    /**
     * Return the list of agenda IDs
     * @return the list of agenda IDs
     */
    public List<Integer> getAgendaIds(  )
    {
    	String strKey = getKey(  );
    	List<Integer> listIds = (List<Integer>) _cacheCalendar.getFromCache( strKey );
    	if ( listIds == null )
    	{
    		Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
    		listIds = CalendarHome.findCalendarIds( plugin );
    		_cacheCalendar.putInCache( strKey, listIds );
    	}
    	
    	return listIds;
    }
    
    /**
     * Get the multiAgenda. Only the agenda the user has access to are put
     * in the multiagenda (same role of the agenda, or the user has the role manager)
     * @param request {@link HttpServletRequest}
     * @return the multiagenda
     */
    public MultiAgenda getMultiAgenda( HttpServletRequest request )
    {
    	MultiAgenda multiAgenda = new MultiAgenda(  );
    	for ( int nAgendaId : getAgendaIds(  ) )
    	{
    		AgendaResource agenda = getAgendaResource( nAgendaId );

            if ( agenda != null && agenda.getAgenda(  ) != null )
            {
                // Check security access
                String strRole = agenda.getRole(  );

                if ( StringUtils.isNotBlank( strRole ) && request != null &&
                        !Constants.PROPERTY_ROLE_NONE.equals( strRole ) )
                {
                    if ( SecurityService.isAuthenticationEnable(  ) )
                    {
                        if ( SecurityService.getInstance(  ).isUserInRole( request, strRole ) )
                        {
                            multiAgenda.addAgenda( agenda.getAgenda(  ) );
                        }
                    }
                }
                else
                {
                	multiAgenda.addAgenda( agenda.getAgenda(  ) );
                }
            }
    	}
    	return multiAgenda;
    }
    
    /**
     * Get the list of agendas. Only the agenda the user has access to are put
     * in the multiagenda (same role of the agenda, or the user has the role manager)
     * @param request {@link HttpServletRequest}
     * @return the list of agenda
     */
    public List<AgendaResource> getAgendaResources( HttpServletRequest request )
    {
    	List<AgendaResource> listAgendas = new ArrayList<AgendaResource>(  );
    	for ( int nAgendaId : getAgendaIds(  ) )
    	{
    		AgendaResource agenda = getAgendaResource( nAgendaId );

            if ( agenda != null && agenda.getAgenda(  ) != null )
            {
                // Check security access
                String strRole = agenda.getRole(  );

                if ( StringUtils.isNotBlank( strRole ) && request != null &&
                        !Constants.PROPERTY_ROLE_NONE.equals( strRole ) )
                {
                    if ( SecurityService.isAuthenticationEnable(  ) )
                    {
                        if ( SecurityService.getInstance(  ).isUserInRole( request, strRole ) )
                        {
                        	listAgendas.add( agenda );
                        }
                    }
                }
                else
                {
                	listAgendas.add( agenda );
                }
            }
    	}
    	return listAgendas;
    }
    
    /**
     * Get the list of agenda resources. Returns only the AgendaResource
     * that the AdminUser has the right to (WorkingGroup wise).
     * @param user the {@link AdminUser}
     * @param plugin {@link Plugin}
     * @return a list of {@AgendaResource}
     */
    public List<AgendaResource> getAgendaResources( AdminUser user, Plugin plugin )
    {
    	List<AgendaResource> listAgendas = CalendarHome.findAgendaResourcesList( plugin );
    	listAgendas = (List<AgendaResource>) AdminWorkgroupService.getAuthorizedCollection( listAgendas, user );
        
        return listAgendas;
    }
    
    /**
     * Get the calendar default parameters
     * @param plugin {@link Plugin}
     * @return a map that defines the plugin-calendar parameters
     */
    public Map<String, String> getCalendarParameters( Plugin plugin )
    {
    	return CalendarParameterHome.findAll( plugin );
    }
    
    /**
     * Create an agenda and reset the caches.
     * @param agenda the {@link AgendaResource}
     * @param plugin {@link Plugin}
     */
    public void doCreateAgenda( AgendaResource agenda, Plugin plugin )
    {
    	CalendarHome.createAgenda( agenda, plugin );
    	
    	// Reset cache
    	removeCache(  );
    }
    
    /**
     * Modify an agenda and reset the caches.
     * @param agenda {@link AgendaResource}
     * @param plugin {@link Plugin}
     */
    public void doModifyAgenda( AgendaResource agenda, Plugin plugin )
    {
    	CalendarHome.updateAgenda( agenda, plugin );
    	
    	// Reset caches
    	removeCache(  );
    	removeCache( agenda.getId(  ) );
    }
    
    /**
     * Remove an agenda and reset the caches.
     * @param nAgendaId the agenda ID
     * @param plugin {@link Plugin}
     */
    public void doRemoveAgenda( int nAgendaId, Plugin plugin )
    {
    	CalendarHome.removeAgenda( nAgendaId, plugin );
    	
    	// Reset caches
    	removeCache(  );
    	removeCache( nAgendaId );
    }
    
    /**
     * Reset cache
     */
    public void resetCache(  )
    {
    	_cacheCalendar.resetCache(  );
    }
    
    /**
     * Reset the cache from the given agenda ID
     * @param strAgendaId the agenda ID
     */
    public void removeCache( String strAgendaId )
    {
    	if ( StringUtils.isNotBlank( strAgendaId ) && StringUtils.isNumeric( strAgendaId ) )
    	{
    		int nAgendaId = Integer.parseInt( strAgendaId );
    		removeCache( nAgendaId );
    	}
    }
    
    /**
     * Reset the cache from the given agenda ID
     * @param nAgendaId the agenda ID
     */
    public void removeCache( int nAgendaId )
    {
    	_cacheCalendar.removeCache( getKey( nAgendaId ) );
    }
    
    /**
     * Reset the cache
     */
    public void removeCache(  )
    {
    	_cacheCalendar.removeCache( getKey(  ) );
    }
    
    /**
     * Get the cache size
     * @return the cache size
     */
    public int getCacheSize(  )
    {
    	return _cacheCalendar.getCacheSize(  );
    }
    
    // CACHE KEYS

    /**
     * Get the cache key for the agenda
     * @param nId the ID of the agenda
     * @return the key
     */
    private String getKey( int nAgendaId )
    { 
        Map<String, String> mapParams = new HashMap<String, String>(  );
        mapParams.put( KEY_CALENDAR, Integer.toString( nAgendaId ) );

        return _cksCalendar.getKey( mapParams, PortalJspBean.MODE_HTML, null );
    }
    
    /**
     * Get the cache key for the agenda
     * @return the key
     */
    private String getKey(  )
    {
    	Map<String, String> mapParams = new HashMap<String, String>(  );
        mapParams.put( KEY_CALENDAR, NONE );

        return _cksCalendar.getKey( mapParams, PortalJspBean.MODE_HTML, null );
    }
}
