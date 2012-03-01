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

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.calendar.business.MultiAgenda;


/**
 *  This interface describes the minimum implementation for a Calendar View
 *
 */
public interface CalendarView
{
    int TYPE_DAY = 0;
    int TYPE_WEEK = 1;
    int TYPE_MONTH = 2;

    /**
     * Returns the HTML view of the Month corresponding to the given date and displaying
     * events of a given agenda
     * @return The view in HTML
     * @param options The options
     * @param strDate The date code
     * @param agenda An agenda
     * @param request HttpServletRequest
     */
    String getCalendarView( String strDate, MultiAgenda agenda, CalendarUserOptions options, HttpServletRequest request );

    /**
     * Returns the next code date corresponding to the current view and the current date
     * @param strDate The current date code
     * @return The next code date
     */
    String getNext( String strDate );

    /**
     * Returns the previous code date corresponding to the current view and the current date
     * @param strDate The current date code
     * @return The previous code date
     */
    String getPrevious( String strDate );

    /**
     * Returns the view title
     * @return The view title
     * @param options The options
     * @param strDate The current date code
     */
    String getTitle( String strDate, CalendarUserOptions options );

    /**
     * Returns the view path
     * @return The view path
     * @param options The options
     * @param strDate The current date code
     */
    String getPath( String strDate, CalendarUserOptions options );

    /**
     * Returns the view type
     * @return The view type
     */
    int getType(  );
}
