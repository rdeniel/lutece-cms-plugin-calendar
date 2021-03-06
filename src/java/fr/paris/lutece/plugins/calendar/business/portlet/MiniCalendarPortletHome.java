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
package fr.paris.lutece.plugins.calendar.business.portlet;

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 * This class provides instances management methods for Mini CalendarPortlet
 * objects
 */
public class MiniCalendarPortletHome extends PortletHome
{
    // This class implements the Singleton design pattern.
    private static MiniCalendarPortletHome _singleton;

    // Static variable pointed at the DAO instance
    private static IMiniCalendarPortletDAO _dao = SpringContextService.getBean( "calendar.miniCalendarPortletDAO" );

    /**
     * Constructor
     */
    public MiniCalendarPortletHome( )
    {
        if ( _singleton == null )
        {
            _singleton = this;
        }
    }

    /**
     * Returns the identifier of the portlet type
     * 
     * @return the portlet type identifier
     */
    public String getPortletTypeId( )
    {
        String strCurrentClassName = this.getClass( ).getName( );
        String strPortletTypeId = PortletTypeHome.getPortletTypeId( strCurrentClassName );

        return strPortletTypeId;
    }

    /**
     * Returns the instance of NewsLetterArchive Portlet
     * 
     * @return the Archive Portlet instance
     */
    public static PortletHome getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new MiniCalendarPortletHome( );
        }

        return _singleton;
    }

    /**
     * Returns the instance of the portlet DAO singleton
     * 
     * @return the instance of the DAO singleton
     */
    public IPortletInterfaceDAO getDAO( )
    {
        return _dao;
    }

    /**
     * Define top events are displayed or not
     * @return true if shows top event else false
     */
    public static boolean showTopEvent( )
    {
        return _dao.showTopEvent( );
    }

    /**
     * Define top events are displayed or not
     * @param top_event The top_event
     */
    public static void updateTopEvent( boolean top_event )
    {
        _dao.updateTopEvent( top_event );
    }
}
