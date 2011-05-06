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
package fr.paris.lutece.plugins.calendar.modules.ical;

import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.portal.service.image.ImageResource;

import java.util.Date;


/**
 * This class implements the Event interface for  events using
 * the iCalendar format (RFC 2445).
 */
public class ICalEvent implements Event
{
	private static final long serialVersionUID = -5892714649982713744L;
	
	// Constants
    private static final int DATE_ONLY_LENGTH = 8;
    private static final int DATE_TIME_LENGTH = 15;

    // Variables
    private Date _dateEvent;
    private String _strDescription;
    private String _strLocation;
    private String _strStartHour;
    private String _strStartMinute;
    private String _strEndHour;
    private String _strEndMinute;
    private String _strTitle;
    private String _strEventClass;
    private String _strDateTimeStart;
    private String _strDateTimeEnd;
    private String _strCategories;
    private String _strStatus;
    private int _nPriority;
    private String _strUrl;
    private int _nPeriodicity;
    private int _nOccurrence;
    private String _strLocationTown;
    private String _strLocationZip;
    private String _strLocationAddress;
    private String _strMapUrl;
    private String _strLinkUrl;
    private int _nDocumentId;
    private String _strPageUrl;
    private ImageResource _imageRessource;
    private int _nTopEvent;
    private Date _dateEnd;
    private Date _dateCreation;
    private int _nId;
    private String _searchType;

    // Event interface implementation

    /**
     * Returns the id of the event
     *
     * @return The id
     */
    public int getId(  )
    {
        return _nId;
    }

    /**
     * Returns the Date
     *
     * @return The Date
     */
    public Date getDate(  )
    {
        return _dateEvent;
    }

    /**
     * Returns the Title
     *
     * @return The Title
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * Returns the EventClass
     *
     * @return The Evenet class
     */
    public String getEventClass(  )
    {
        return _strEventClass;
    }

    /**
     * Returns the Location
     *
     * @return The Location
     */
    public String getLocation(  )
    {
        return _strLocation;
    }

    /**
     * Returns the Description
     *
     * @return The Description
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * Returns the DateTimeStart
     *
     * @return The DateTimeStart
     */
    public String getDateTimeStart(  )
    {
        return _strDateTimeStart;
    }

    /**
     * Returns the DateTimeEnd
     *
     * @return The DateTimeEnd
     */
    public String getDateTimeEnd(  )
    {
        return _strDateTimeEnd;
    }

    /**
     * Returns the Categories
     *
     * @return The Categories
     */
    public String getCategories(  )
    {
        return _strCategories;
    }

    /**
     * Returns the Status
     *
     * @return The Status
     */
    public String getStatus(  )
    {
        return _strStatus;
    }

    /**
     * Returns the Priority
     *
     * @return The Priority
     */
    public int getPriority(  )
    {
        return _nPriority;
    }

    /**
     * Returns the Url
     *
     * @return The Url
     */
    public String getUrl(  )
    {
        return _strUrl;
    }

    public int getPeriodicity(  )
    {
        return _nPeriodicity;
    }

    public int getOccurrence(  )
    {
        return _nOccurrence;
    }

    /**
     * Returns the date end
     *
     * @return The date end
     */
    public Date getDateEnd(  )
    {
        return _dateEnd;
    }

    /**
     * Returns the location Town
     *
     * @return The location Town
     */
    public String getLocationTown(  )
    {
        return _strLocationTown;
    }

    /**
     * Returns the location Address
     *
     * @return The location Address
     */
    public String getLocationAddress(  )
    {
        return _strLocationAddress;
    }

    /**
     * Returns the date end
     *
     * @return The date end
     */
    public String getMapUrl(  )
    {
        return _strMapUrl;
    }

    /**
     * Returns the link Url
     *
     * @return The link Url
     */
    public String getLinkUrl(  )
    {
        return _strLinkUrl;
    }

    /**
     * Returns the document id
     *
     * @return The document id
     */
    public int getDocumentId(  )
    {
        return _nDocumentId;
    }

    /**
     * Returns the page Url
     *
     * @return The page Url
     */
    public String getPageUrl(  )
    {
        return _strPageUrl;
    }

    /**
     * Returns the top Event
     *
     * @return The top Event
     */
    public int getTopEvent(  )
    {
        return _nTopEvent;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Setters : scope package
    public void setPeriodicity( int nPeriodicity )
    {
        _nPeriodicity = nPeriodicity;
    }

    public void setOccurrence( int nOccurrence )
    {
        _nOccurrence = nOccurrence;
    }

    /**
     * Sets the DateTimeStart
     *
     * @param strDateTimeStart The Date
     */
    void setDateTimeStart( String strDateTimeStart )
    {
        if ( strDateTimeStart.length(  ) == DATE_ONLY_LENGTH )
        {
            _dateEvent = Utils.getDate( strDateTimeStart );
        }
        else if ( strDateTimeStart.length(  ) == DATE_TIME_LENGTH )
        {
            _dateEvent = Utils.getDate( strDateTimeStart );
            _strStartHour = strDateTimeStart.substring( 9, 11 );
            _strStartMinute = strDateTimeStart.substring( 11, 13 );
            _strDateTimeStart = _strStartHour + ":" + _strStartMinute;
        }
    }

    /**
     * Sets the DateTimeEnd
     *
     * @param strDateTimeEnd The Date
     */
    void setDateTimeEnd( String strDateTimeEnd )
    {
        if ( strDateTimeEnd.length(  ) == DATE_TIME_LENGTH )
        {
            _dateEvent = Utils.getDate( strDateTimeEnd );
            _strEndHour = strDateTimeEnd.substring( 9, 11 );
            _strEndMinute = strDateTimeEnd.substring( 11, 13 );
            _strDateTimeEnd = _strEndHour + ":" + _strEndMinute;
        }
    }

    /**
     * Sets the Title
     *
     * @param strTitle The Title
     */
    void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Sets the Location
     *
     * @param strLocation The Location
     */
    void setLocation( String strLocation )
    {
        _strLocation = strLocation;
    }

    /**
     * Sets the EventClass
     *
     * @param strEventClass The EventClass
     */
    public void setEventClass( String strEventClass )
    {
        _strEventClass = strEventClass;
    }

    /**
     * Sets the Description
     *
     * @param strDescription The Description
     */
    void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Sets the Categories
     *
     * @param strCategories The Categories
     */
    void setCategories( String strCategories )
    {
        _strCategories = strCategories;
    }

    /**
     * Sets the Status
     *
     * @param strStatus The Status
     */
    public void setStatus( String strStatus )
    {
        _strStatus = strStatus;
    }

    /**
     * Sets the Priority
     *
     * @param nPriority The Priority
     */
    public void setPriority( int nPriority )
    {
        _nPriority = nPriority;
    }

    /**
     * Sets the Url
     *
     * @param strUrl The Url
     */
    public void setUrl( String strUrl )
    {
        _strUrl = strUrl;
    }

    /**
     * Sets the Date End
     *
     * @param DateEnd The Date End
     */
    public void setDateEnd( Date DateEnd )
    {
        _dateEnd = DateEnd;
    }

    /**
     * Sets the location Town
     *
     * @param strLocationTown The location Town
     */
    public void setLocationTown( String strLocationTown )
    {
        _strLocationTown = strLocationTown;
    }

    /**
     * Returns the location Zip
     *
     * @return The location Zip
     */
    public String getLocationZip(  )
    {
        return _strLocationZip;
    }

    /**
     * Sets the location Zip
     *
     * @param strLocationZip The location Zip
     */
    public void setLocationZip( String strLocationZip )
    {
        _strLocationZip = strLocationZip;
    }

    /**
     * Sets the location Address
     *
     * @param strLocationAddress The location Address
     */
    public void setLocationAddress( String strLocationAddress )
    {
        _strLocationAddress = strLocationAddress;
    }

    /**
     * Sets the Map Url
     *
     * @param strMapUrl The Map Url
     */
    public void setMapUrl( String strMapUrl )
    {
        _strMapUrl = strMapUrl;
    }

    /**
     * Sets the link Url
     *
     * @param strLinkUrl The link Url
     */
    public void setLinkUrl( String strLinkUrl )
    {
        _strLinkUrl = strLinkUrl;
    }

    /**
     * Sets the document id
     *
     * @param strDocumentUrl The document id
     */
    public void setDocumentId( int nDocumentId )
    {
        _nDocumentId = nDocumentId;
    }

    /**
     * Sets the page Url
     *
     * @param strPageUrl The page Url
     */
    public void setPageUrl( String strPageUrl )
    {
        _strPageUrl = strPageUrl;
    }

    /**
     * Sets the top Event
     *
     * @param strTopEvent The top Event
     */
    public void setTopEvent( int strTopEvent )
    {
        _nTopEvent = strTopEvent;
    }

    /**
     * Sets the Date
     *
     * @param nId The id of the event
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the type
     *
     * @return The type
     */
    public String getType(  )
    {
        return _searchType;
    }

    /**
     * Sets the type
     *
     * @param  The type
     */
    public void setType( String type )
    {
        _searchType = type;
    }

    /**
     * Returns the ImageResource
     *
     * @return The ImageResource
     */
    public ImageResource getImageResource(  )
    {
        return _imageRessource;
    }

    /**
     * Sets the ImageResource
     *
     * @param ImageResource the ImageResource
     */
    public void setImageResource( ImageResource imageResource )
    {
        _imageRessource = imageResource;
    }

    /**
     * Returns the event date creation
     *
     * @return The event date creation
     */
    public Date getDateCreation(  )
    {
        return _dateCreation;
    }

    /**
     * Sets the date event date creation
     *
     * @param dateCreation The event date creation
     */
    public void setDateCreation( Date dateCreation )
    {
        _dateCreation = dateCreation;
    }

    /**
     * Return the id of calendar
     */
    public int getIdCalendar(  )
    {
        return 0;
    }
}
