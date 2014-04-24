/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import java.io.Serializable;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.portal.service.resource.Resource;
import fr.paris.lutece.portal.service.role.RoleRemovalListenerService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.service.workgroup.WorkgroupRemovalListenerService;


/**
 *  This class describes an agenda resource
 */
public class AgendaResource implements Serializable, Resource, AdminWorkgroupResource
{
	private static final long serialVersionUID = 5430301763071821176L;
	private static AgendaResourceWorkgroupRemovalListener _listenerWorkgroup;
    private static AgendaResourceRoleRemovalListener _listenerRole;

    // Variables declarations
    private String _strId;
    private String _strName;
    private String _strEventImage;
    private String _strEventPrefix;
    private String _strLoaderClassName;
    private String _strLoaderParameter;
    private String _strRole;
    private String _strRoleManager;
    private Agenda _agenda;
    private String _strAdminWorkgroup;
    private String _strResourceType;
    private boolean _bNotify;
    private int _nPeriodValidity;

    /**
     * Initialize the Agenda
     */
    public static void init(  )
    {
        // Create removal listeners and register them
        if ( _listenerWorkgroup == null )
        {
            _listenerWorkgroup = new AgendaResourceWorkgroupRemovalListener(  );
            WorkgroupRemovalListenerService.getService(  ).registerListener( _listenerWorkgroup );
        }

        if ( _listenerRole == null )
        {
            _listenerRole = new AgendaResourceRoleRemovalListener(  );
            RoleRemovalListenerService.getService(  ).registerListener( _listenerRole );
        }
    }

    /**
     * Returns the Id
     *
     * @return The Id
     */
    public String getId(  )
    {
        return _strId;
    }

    /**
     * Sets the Id
     *
     * @param strId The Id
     */
    public void setId( String strId )
    {
        _strId = strId;
    }

    /**
     * Returns the Name
     *
     * @return The Name
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * Sets the Name
     *
     * @param strName The Name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Returns the Resource Type
     *
     * @return The Resource Type
     */
    public String getResourceType(  )
    {
        return _strResourceType;
    }

    /**
     * Sets the Resource Type
     *
     * @param strResourceType The Resource Type
     */
    public void setResourceType( String strResourceType )
    {
        _strResourceType = strResourceType;
    }

    /**
     * Returns the EventImage
     *
     * @return The EventImage
     */
    public String getEventImage(  )
    {
        return _strEventImage;
    }

    /**
     * Sets the EventImage
     *
     * @param strEventImage The EventImage
     */
    public void setEventImage( String strEventImage )
    {
        _strEventImage = strEventImage;
    }

    /**
     * Returns the EventPrefix
     *
     * @return The EventPrefix
     */
    public String getEventPrefix(  )
    {
        return _strEventPrefix;
    }

    /**
     * Sets the EventPrefix
     *
     * @param strEventPrefix The EventPrefix
     */
    public void setEventPrefix( String strEventPrefix )
    {
        _strEventPrefix = strEventPrefix;
    }

    /**
     * Returns the LoaderClassName
     *
     * @return The LoaderClassName
     */
    public String getLoaderClassName(  )
    {
        return _strLoaderClassName;
    }

    /**
     * Sets the LoaderClassName
     *
     * @param strLoaderClassName The LoaderClassName
     */
    public void setLoaderClassName( String strLoaderClassName )
    {
        _strLoaderClassName = strLoaderClassName;
    }

    /**
     * Returns the LoaderParameter
     *
     * @return The LoaderParameter
     */
    public String getLoaderParameter(  )
    {
        return _strLoaderParameter;
    }

    /**
     * Sets the LoaderParameter
     *
     * @param strLoaderParameter The LoaderParameter
     */
    public void setLoaderParameter( String strLoaderParameter )
    {
        _strLoaderParameter = strLoaderParameter;
    }

    /**
     * Returns the Role
     *
     * @return The Role
     */
    public String getRole(  )
    {
        return _strRole;
    }

    /**
     * Sets the Role
     *
     * @param strRole The Role
     */
    public void setRole( String strRole )
    {
        _strRole = strRole;
    }

    /**
    * Returns the  Manager Role
    *
    * @return The ManagerRole
    */
    public String getRoleManager(  )
    {
        return _strRoleManager;
    }

    /**
     * Sets the Manager Role
     * @param strRoleManager The role of the manager of the agenda
     */
    public void setRoleManager( String strRoleManager )
    {
        _strRoleManager = strRoleManager;
    }

    /**
     * Returns the Agenda
     *
     * @return The Agenda
     */
    public Agenda getAgenda(  )
    {
        return _agenda;
    }

    /**
     * Sets the Agenda
     * @param agenda The Agenda
     */
    public void setAgenda( Agenda agenda )
    {
        _agenda = agenda;
    }

    /**
     * Returns the workgroup
     * @return The workgroup
     */
    public String getWorkgroup(  )
    {
        return _strAdminWorkgroup;
    }

    /**
     * Sets the workgroup
     * @param strAdminWorkgroup The string representation of the admin workgroup
     */
    public void setWorkgroup( String strAdminWorkgroup )
    {
        _strAdminWorkgroup = AdminWorkgroupService.normalizeWorkgroupKey( strAdminWorkgroup );
    }
    
    /**
     * Returns the _bNotify
     * @return The _bNotify
     */
    public boolean isNotify(  )
    {
        return _bNotify;
    }

    /**
     * Sets the _bNotify
     * @param notify the _bNotification to set
     */
    public void setNotify( Boolean notify )
    {
    	_bNotify = notify;
    }
    
    /**
     * Returns the period validity of mail notification
     * @return The period validity of mail notification
     */
    public int getPeriodValidity(  )
    {
        return _nPeriodValidity;
    }

    /**
     * Sets the the period validity of mail notification
     * @param nPeriodValidity The int period validity of mail notification
     */
    public void setPeriodValidity( int nPeriodValidity )
    {
    	_nPeriodValidity = nPeriodValidity;
    }
}
