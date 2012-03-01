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

import fr.paris.lutece.plugins.calendar.business.category.Category;
import fr.paris.lutece.portal.service.image.ImageResource;

import java.sql.Timestamp;

import java.util.Collection;
import java.util.Date;


/**
 * SimpleEvent
 */
public class SimpleEvent implements Event
{
	private static final long serialVersionUID = -1675274063314004181L;
	
	// Variables declarations
    private int _nId;
    private Date _dateEvent;
    private Date _dateEnd;
    private String _strTitle;
    private String _strLocation;
    private String _strEventClass;
    private String _strDescription;
    private String _strDateTimeStart;
    private String _strDateTimeEnd;
    private String _strStatus;

    /* since version 3.0.0. */
    private int _nIdCalendar;
    private int _nPriority;
    private int _nPeriodicity;
    private int _nOccurrence;
    private String _strUrl;
    private String _strLocationTown;
    private String _strLocationZip;
    private String _strLocationAddress;
    private String _strMapUrl;
    private String _strLinkUrl;
    private int _nDocumentId;
    private String _strPageUrl;
    private ImageResource _imageRessource;
    private String[] _listTags;
    private String _strListTags;
    private int _nTopEvent;
    private String _strImageUrl;
    private String _searchType;
    private Timestamp _dateCreation;
    private Collection<Category> _listCategories;
    private String[] _listExcludedDays;

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
     * Sets the Date
     *
     * @param nId The id of the event
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the Date
     *
     * @return A Date object representing the event's date
     */
    public Date getDate(  )
    {
        return _dateEvent;
    }

    /**
     * Sets the Date
     *
     * @param strDate The Date
     */
    public void setDate( Date dateEvent )
    {
        _dateEvent = dateEvent;
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
     * Sets the Title
     *
     * @param strTitle The Title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
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
     * Sets the Location
     *
     * @param strLocation The Location
     */
    public void setLocation( String strLocation )
    {
        _strLocation = strLocation;
    }

    /**
     * Returns the EventClass
     *
     * @return The EventClass
     */
    public String getEventClass(  )
    {
        return _strEventClass;
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
     * Returns the Description
     *
     * @return The Description
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     *
     * @param strDescription The Description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
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
     * Sets the DateTimeStart
     *
     * @param strDateTimeStart The DateTimeStart
     */
    public void setDateTimeStart( String strDateTimeStart )
    {
        _strDateTimeStart = strDateTimeStart;
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
     * Sets the DateTimeEnd
     *
     * @param strDateTimeEnd The DateTimeEnd
     */
    public void setDateTimeEnd( String strDateTimeEnd )
    {
        _strDateTimeEnd = strDateTimeEnd;
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
     * Sets the Status
     *
     * @param strStatus The Status
     */
    public void setStatus( String strStatus )
    {
        _strStatus = strStatus;
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
     * Sets the Priority
     *
     * @param nPriority The Priority
     */
    public void setPriority( int nPriority )
    {
        _nPriority = nPriority;
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
     * Returns the periodicity
     *
     * @return The periodicity
     */
    public int getPeriodicity(  )
    {
        return _nPeriodicity;
    }

    /**
     * Sets the periodicity
     *
     * @param nPeriodicity The periodicity
     */
    public void setPeriodicity( int nPeriodicity )
    {
        _nPeriodicity = nPeriodicity;
    }

    /**
     * Returns the occurrence
     *
     * @return The occurrence
     */
    public int getOccurrence(  )
    {
        return _nOccurrence;
    }

    /**
     * Sets the occurrence
     *
     * @param nOccurrence The occurrence
     */
    public void setOccurrence( int nOccurrence )
    {
        _nOccurrence = nOccurrence;
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
     * Sets the Date End
     *
     * @param DateEnd The Date End
     */
    public void setDateEnd( Date DateEnd )
    {
        _dateEnd = DateEnd;
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
     * Returns the location Address
     *
     * @return The location Address
     */
    public String getLocationAddress(  )
    {
        return _strLocationAddress;
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
     * Returns the date end
     *
     * @return The date end
     */
    public String getMapUrl(  )
    {
        return _strMapUrl;
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
     * Returns the link Url
     *
     * @return The link Url
     */
    public String getLinkUrl(  )
    {
        return _strLinkUrl;
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
     * Returns the document id
     *
     * @return The document id
     */
    public int getDocumentId(  )
    {
        return _nDocumentId;
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
     * Returns the page Url
     *
     * @return The page Url
     */
    public String getPageUrl(  )
    {
        return _strPageUrl;
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
     * Returns the top Event
     *
     * @return The top Event
     */
    public int getTopEvent(  )
    {
        return _nTopEvent;
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
     * Returns the top Event
     *
     * @return The top Event
     */
    public String[] getTags(  )
    {
        return _listTags;
    }

    /**
     * Sets the tag list
     *
     * @param enumtags The tag list
     */
    public void setTags( String[] listTags )
    {
        _listTags = listTags;
    }

    /**
     * Returns the Tag list
     *
     * @return The Tag list
     */
    public String getListTags(  )
    {
        return _strListTags;
    }

    /**
     * Sets the tag list
     *
     * @param enumtags The tag list
     */
    public void setListTags( String strListTags )
    {
        _strListTags = strListTags;
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
     * Gets the calendar id
     *
     * @param  The calendar id
     */
    public int getIdCalendar(  )
    {
        return _nIdCalendar;
    }

    /**
     * Sets the calendar id
     *
     * @param  The calendar id
     */
    public void setIdCalendar( int idCalendar )
    {
        _nIdCalendar = idCalendar;
    }

    /**
     * Gets the event categories
     *
     * @param  the event categories
     */
    public Collection<Category> getListCategories(  )
    {
        return _listCategories;
    }

    /**
     * Sets the event categories
     *
     * @param  the event categories
     */
    public void setListCategories( Collection<Category> categories )
    {
        _listCategories = categories;
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
     * Returns the image Url
     *
     * @return The image Url
     */
    public String getImageUrl(  )
    {
        return _strImageUrl;
    }

    /**
     * Sets the image Url
     *
     * @param strImageUrl The image Url
     */
    public void setImageUrl( String strImageUrl )
    {
        _strImageUrl = strImageUrl;
    }

    /**
     * Returns the event date creation
     *
     * @return The event date creation
     */
    public Timestamp getDateCreation(  )
    {
        return _dateCreation;
    }

    /**
     * Sets the date event date creation
     *
     * @param dateCreation The event date creation
     */
    public void setDateCreation( Timestamp dateCreation )
    {
        _dateCreation = dateCreation;
    }
    
    /**
     * Get the list of excluded days
     * 
     * @return the list of excluded days
     */
    public String[] getExcludedDays(  )
    {
    	return _listExcludedDays;
    }
    
    /**
     * Set the list of excluded days
     * 
     * @param listExcludedDays the list of excluded days
     */
    public void setExcludedDays( String[] listExcludedDays )
    {
    	_listExcludedDays = listExcludedDays;
    }
}
