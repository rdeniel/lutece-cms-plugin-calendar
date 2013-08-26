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
package fr.paris.lutece.plugins.calendar.business;

public class OccurrenceEvent extends SimpleEvent
{
    private static final long serialVersionUID = 4979315059166094219L;
    private int _nEventId;

    /**
     * Default constructor
     */
    public OccurrenceEvent( )
    {
    }

    /**
     * Constructor
     * @param event The event
     * @param nOccurrenceId The id of an occurrence
     */
    public OccurrenceEvent( SimpleEvent event, int nOccurrenceId )
    {
        setDate( event.getDate( ) );
        setEventClass( event.getEventClass( ) );
        setTitle( event.getTitle( ) );
        setDescription( event.getDescription( ) );
        setDateTimeStart( event.getDateTimeStart( ) );
        setDateTimeEnd( event.getDateTimeEnd( ) );
        setStatus( event.getStatus( ) );
        setPriority( event.getPriority( ) );
        setUrl( event.getUrl( ) );
        setLocation( event.getLocation( ) );
        setId( nOccurrenceId );
        setEventId( event.getId( ) );
        setListCategories( event.getListCategories( ) );
    }

    /**
     * Returns the id of the event
     * 
     * @return The id
     */
    public int getEventId( )
    {
        return _nEventId;
    }

    /**
     * Sets the id of the event
     * 
     * @param eventId The id of the event
     */
    public void setEventId( int eventId )
    {
        _nEventId = eventId;
    }
}
