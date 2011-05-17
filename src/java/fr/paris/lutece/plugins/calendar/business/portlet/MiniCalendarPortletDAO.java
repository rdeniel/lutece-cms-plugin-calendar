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
package fr.paris.lutece.plugins.calendar.business.portlet;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 * This class provides Data Access methods for CalendarPortlet objects
 */
public final class MiniCalendarPortletDAO implements IMiniCalendarPortletDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_portlet FROM core_portlet WHERE id_portlet = ?";
    private static final String SQL_QUERY_SELECT_TOP_EVENT = "SELECT top_event FROM calendar_mini_portlet";
    private static final String SQL_QUERY_UPDATE_TOP_EVENT = "UPDATE calendar_mini_portlet SET top_event= ?";

    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
     * Inserts a new record in the table. Not implemented.
     *
     * @param portlet    the object to be inserted
     */
    public void insert( Portlet portlet )
    {
        // Not implemented.
    }

    /**
     * Deletes a record from the table.
     *
     * @param nPortletId the portlet id
     *
     */
    public void delete( int nPortletId )
    {
        // Not implemented.
    }

    /**
     * Loads the data of the portlet from the table.
     *
     * @param nPortletId the portlet id
     * @return the Portlet object
     */
    public Portlet load( int nPortletId )
    {
        MiniCalendarPortlet portlet = new MiniCalendarPortlet(  );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT );

        daoUtil.setInt( 1, nPortletId );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            portlet.setId( nPortletId );
        }

        daoUtil.free(  );

        return portlet;
    }

    /**
     * Updates the record in the table. Not implemented.
     *
     * @param portlet
     *            the instance of Portlet class to be updated
     */
    public void store( Portlet portlet )
    {
        // Not implemented.
    }

    /**
     * Define top events are displayed or not
     * @return true if shows top event else false
     */
    public boolean showTopEvent(  )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_TOP_EVENT );
        daoUtil.executeQuery(  );

        boolean bTopEvent = false;

        while ( daoUtil.next(  ) )
        {
            bTopEvent = daoUtil.getBoolean( 1 );
        }

        daoUtil.free(  );

        return bTopEvent;
    }

    /**
     * Define top events are displayed or not
     * @param plugin The plugin
     * @param top_event The top_event
     * @return true if shows top event else false
     */
    public void updateTopEvent( boolean top_event )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_TOP_EVENT );
        daoUtil.setBoolean( 1, top_event );
        daoUtil.executeUpdate(  );
    }
}
