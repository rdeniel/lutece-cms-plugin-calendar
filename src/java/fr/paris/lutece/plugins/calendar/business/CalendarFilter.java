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

/**
 * Filter for calendar
 */
public class CalendarFilter
{
    private static final int ALL_INT = -1;

    // Variables declarations
    private int _nIdCalendar = ALL_INT;
    private int _nSortEvents = ALL_INT;
    private int[] _arrayCategoriesId;
    private int[] _arrayId;
    private int[] _arrayCalendarId;

    /**
     * @return the calendar id
     */
    public int getIdCalendar( )
    {
        return _nIdCalendar;
    }

    /**
     * @param idCalendar the _nIdCalendar to set
     */
    public void setIdCalendar( int idCalendar )
    {
        _nIdCalendar = idCalendar;
    }

    /**
     * @return the _nSortEvents
     */
    public int getSortEvents( )
    {
        return _nSortEvents;
    }

    /**
     * @param sortEvents the _nSortEvents to set
     */
    public void setSortEvents( int sortEvents )
    {
        _nSortEvents = sortEvents;
    }

    /**
     * @return the _arrayCategoriesId
     */
    public int[] getCategoriesId( )
    {
        return _arrayCategoriesId;
    }

    /**
     * @param arrayCategoriesId the _arrayCategoriesId to set
     */
    public void setCategoriesId( int[] arrayCategoriesId )
    {
        _arrayCategoriesId = arrayCategoriesId;
    }

    /**
     * @return the _arrayId
     */
    public int[] getIds( )
    {
        return _arrayId;
    }

    /**
     * @param arrayId the _arrayId to set
     */
    public void setIds( int[] arrayId )
    {
        _arrayId = arrayId;
    }

    /**
     * @return the _arrayId
     */
    public int[] getCalendarIds( )
    {
        return _arrayCalendarId;
    }

    /**
     * @param calendarId the _arrayId to set
     */
    public void setCalendarIds( int[] calendarId )
    {
        _arrayCalendarId = calendarId;
    }

    /**
     * Tell if the filter contains a criteria on the Category
     * @return True if the filter contains a criteria on the categories
     *         otherwise false
     */
    public boolean containsCategoriesCriteria( )
    {
        return ( ( _arrayCategoriesId != null ) && ( _arrayCategoriesId.length != 0 ) );
    }

    /**
     * Tell if the filter contains a criteria on the Id
     * @return True if the filter contains a criteria on the Ids otherwise false
     */
    public boolean containsIdsCriteria( )
    {
        return ( ( _arrayId != null ) && ( _arrayId.length != 0 ) );
    }

    /**
     * Tell if the filter contains a criteria on the calendar Ids
     * @return True if the filter contains a criteria on the calendar Ids
     *         otherwise false
     */
    public boolean containsCalendarIdsCriteria( )
    {
        return ( ( _arrayCalendarId != null ) && ( _arrayCalendarId.length != 0 ) );
    }

    /**
     * Tell if the filter contains a criteria on the calendars
     * @return True if the filter contains a criteria on the calendars otherwise
     *         false
     */
    public boolean containsCalendarCriteria( )
    {
        return ( _nIdCalendar != ALL_INT );
    }

    /**
     * Tell if the filter contains a criteria on the calendars
     * @return True if the filter contains a criteria on the calendars otherwise
     *         false
     */
    public boolean containsSortCriteria( )
    {
        return ( _nSortEvents != ALL_INT );
    }
}
