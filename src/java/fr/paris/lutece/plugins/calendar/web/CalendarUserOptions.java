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

import java.util.Locale;


/**
 *  The options used for display
 */
public class CalendarUserOptions
{
    private boolean _bDayOffDisplayed;
    private Locale _locale;
    private boolean _bShowSearchEngine;

    /**
     * Returns the Locale
     *
     * @return The Locale
     */
    public Locale getLocale(  )
    {
        return _locale;
    }

    /**
     * Sets the Locale
     *
     * @param locale The Locale
     */
    public void setLocale( Locale locale )
    {
        _locale = locale;
    }

    /**
     * Returns the DayOffDisplayed
     *
     * @return The DayOffDisplayed
     */
    public boolean isDayOffDisplayed(  )
    {
        return _bDayOffDisplayed;
    }

    /**
     * Sets the DayOffDisplayed
     *
     * @param bDayOffDisplayed The DayOffDisplayed
     */
    public void setDayOffDisplayed( boolean bDayOffDisplayed )
    {
        _bDayOffDisplayed = bDayOffDisplayed;
    }

    /**
     * Returns bShowSearchEngine: true for showing search engine, false for showing old calendar
     *
     * @return The DayOffDisplayed
     */
    public boolean isShowSearchEngine(  )
    {
        return _bShowSearchEngine;
    }

    /**
     * Sets bShowSearchEngine to display calendar search engine
     *
     * @param bShowSearchEngine The bShowSearchEngine
     */
    public void setShowSearchEngine( boolean bShowSearchEngine )
    {
        _bShowSearchEngine = bShowSearchEngine;
    }
}
