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
package fr.paris.lutece.plugins.calendar.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.calendar.service.Utils;


/**
 * This Class provides an agenda that aggregate other agendas.
 */
public class MultiAgenda implements Agenda
{
	private static final long serialVersionUID = 564170210812312845L;
	private String _strName;
    private String _strKeyName;
    private List<Agenda> _listAgendas = new ArrayList<Agenda>(  );

    public MultiAgenda(  )
    {
    }
    
    /**
     * Indicates if the agenda gets events for a given date
     * @param strDate A date code
     * @return True if there is events, otherwise false
     */
    public boolean hasEvents( String strDate )
    {
        for ( int i = 0; i < _listAgendas.size(  ); i++ )
        {
            Agenda agenda = (Agenda) _listAgendas.get( i );

            if ( agenda != null && agenda.hasEvents( strDate ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Retrieves events for a given date
     * @param strDate A date code
     * @return A list of events
     */
    public List<Event> getEventsByDate( String strDate )
    {
        List<Event> list = new ArrayList<Event>(  );
        List<Event> listEvents = getEvents(  );

        for ( Event event : listEvents )
        {
            if ( Utils.getDate( event.getDate(  ) ).equals( strDate ) )
            {
                list.add( event );
            }
        }

        return list;
    }

    /**
     * Retrieves all events of the agenda
     * @return A list of events
     */
    public List<Event> getEvents(  )
    {
        List<Event> list = new ArrayList<Event>(  );

        for ( int i = 0; i < _listAgendas.size(  ); i++ )
        {
            Agenda agenda = (Agenda) _listAgendas.get( i );
            List<Event> listEvents = agenda.getEvents(  );

            for ( Event e : listEvents )
            {
                MultiAgendaEvent event = new MultiAgendaEvent( e, agenda.getKeyName(  ) );
                list.add( event );
            }
        }

        return list;
    }

    /**
     * Returns the name of the Agenda
     * @return The agenda's name
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * Defines the name of the Agenda
     * @param strName The agenda's name
     */
    public void setName( String strName )
    {
        _strName = strName;
    }

    /**
     * Add an agenda
     * @param agenda The agenda
     */
    public void addAgenda( Agenda agenda )
    {
        _listAgendas.add( agenda );
    }

    /**
     * Returns the KeyName
     *
     * @return The KeyName
     */
    public String getKeyName(  )
    {
        return _strKeyName;
    }

    /**
     * Sets the KeyName
     *
     * @param strKeyName The KeyName
     */
    public void setKeyName( String strKeyName )
    {
        _strKeyName = strKeyName;
    }

    /**
     * Gets agendas
     * @return A list that contain all agendas
     */
    public List<Agenda> getAgendas(  )
    {
        return _listAgendas;
    }

    /**
     * Fetches the events present between two dates
     * @param strDateBegin The start date
     * @param strDateEnd The end date
     * @return The events
     */
    public List<Event> getEventsByDate( Date dateBegin, Date dateEnd, Locale localeEnv )
    {
        List<Event> listEvents = getEvents(  );
        List<Event> list = new ArrayList<Event>(  );

        java.util.Calendar calendar = new GregorianCalendar(  );
        calendar.setTime( dateBegin );

        java.util.Calendar calendar1 = new GregorianCalendar(  );
        calendar1.setTime( dateEnd );

        while ( dateBegin.compareTo( dateEnd ) != 0 )
        {
            listEvents = getEventsByDate( Utils.getDate( calendar ) );

            if ( listEvents != null )
            {
                for ( Event event : listEvents )
                {
                    list.add( event );
                }
            }

            calendar.add( java.util.Calendar.DATE, 1 );
        }

        return list;
    }
    
    /**
     * Fetch agenda ids
     * @return agenda ids
     */
    public String[] getAgendaIds(  )
    {
    	String[] arrayAgendaIds = new String[_listAgendas.size(  )];
    	if ( !_listAgendas.isEmpty(  ) || !( _listAgendas.size(  ) == 1 && _listAgendas.get( 0 ) == null ) )
    	{
    		for ( int i = 0; i < _listAgendas.size(  ); i++ )
            {
                Agenda agenda = ( Agenda ) _listAgendas.get( i );
                if ( agenda != null )
                {
                	arrayAgendaIds[i] = agenda.getKeyName(  );
                }
        	}
    		return arrayAgendaIds;
    	}
    	
    	return null;
    }
}
