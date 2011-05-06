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
package fr.paris.lutece.plugins.calendar.business;

import java.io.Serializable;
import java.util.Date;

import fr.paris.lutece.portal.service.image.ImageResource;


/**
 * This interface describes the minimum implementation for events
 */
public interface Event extends Serializable
{
    /**
     * Returns a date as an object java.util.Date
     * @return The date code
     */
    Date getDate(  );

    /**
     * Returns the id of the event
     *
     * @return The id
     */
    int getId(  );

    /**
     * Returns the Title
     *
     * @return The Title
     */
    String getTitle(  );

    /**
     * Returns the Location
     *
     * @return The Location
     */
    String getLocation(  );

    /**
     * Returns the EventClass
     *
     * @return The EventClass
     */
    String getEventClass(  );

    /**
     * Returns the Description
     *
     * @return The Description
     */
    String getDescription(  );

    /**
     * Returns the DateTimeStart
     *
     * @return The DateTimeStart
     */
    String getDateTimeStart(  );

    /**
     * Returns the DateEnd
     *
     * @return The DateEnd
     */
    String getDateTimeEnd(  );

    /**
     * Returns the Status
     *
     * @return The Status
     */
    String getStatus(  );

    /**
     * Returns the Priority
     *
     * @return The Priority
     */
    int getPriority(  );

    /**
     * Returns the Url
     *
     * @return The Url
     */
    String getUrl(  );

    /**
     * Returns the periodicity
     *
     * @return The periodicity
     */
    int getPeriodicity(  );

    /**
     * Returns the occurrence
     *
     * @return The occurrence
     */
    int getOccurrence(  );

    /**
     * Returns the date end
     *
     * @return The date end
     */
    Date getDateEnd(  );

    /**
     * Returns the location Town
     *
     * @return The location Town
     */
    String getLocationTown(  );

    /**
     * Returns the location Zip
     *
     * @return The location Zip
     */
    String getLocationZip(  );

    /**
     * Returns the location Address
     *
     * @return The location Address
     */
    String getLocationAddress(  );

    /**
     * Returns the date end
     *
     * @return The date end
     */
    String getMapUrl(  );

    /**
     * Returns the link Url
     *
     * @return The link Url
     */
    String getLinkUrl(  );

    /**
     * Returns the document id
     *
     * @return The document id
     */
    int getDocumentId(  );

    /**
     * Returns the page Url
     *
     * @return The page Url
     */
    String getPageUrl(  );

    /**
     * Returns the top Event
     *
     * @return The top Event
     */
    int getTopEvent(  );

    /**
     * Returns the ImageResource
     *
     * @return the ImageResource
     */
    ImageResource getImageResource(  );

    /**
     * Returns search type
     *
     * @return The search type
     */
    String getType(  );

    /**
     * Returns the creation date
     */
    Date getDateCreation(  );

    /**
     * Returns the id of calendar
     */
    int getIdCalendar(  );
}
