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
package fr.paris.lutece.plugins.calendar.business.notification;

import java.sql.Timestamp;


/**
 *
 * CalendarNotification
 *
 */
public class CalendarNotification
{
    private String _strKey;
    private String _strEmail;
    private int _nIdAgenda;
    private Timestamp _tDateExpiry;

    /**
     * Get ID Agenda
     * @return id Agenda
     */
    public int getIdAgenda(  )
    {
        return _nIdAgenda;
    }

    /**
     * Set id Agenda
     * @param idAgenda id Agenda
     */
    public void setIdAgenda( int idAgenda )
    {
    	_nIdAgenda = idAgenda;
    }

    /**
    * Get Key
    * @return string key
    */
    public String getKey(  )
    {
        return _strKey;
    }

    /**
     * Set string key
     * @param strKey string key
     */
    public void setKey( String strKey )
    {
        _strKey = strKey;
    }
    
    /**
    * Get email
    * @return string email
    */
    public String getEmail(  )
    {
        return _strEmail;
    }

    /**
     * Set string email
     * @param strEmail string email
     */
    public void setEmail( String strEmail )
    {
        _strEmail = strEmail;
    }

    /**
    * Get date expiry
    * @return date expiry
    */
    public Timestamp getDateExpiry(  )
    {
        return _tDateExpiry;
    }

    /**
    * Set date expiry
    * @param tDateExpiry date expiry
    */
    public void setDateExpiry( Timestamp tDateExpiry )
    {
        _tDateExpiry = tDateExpiry;
    }
}
