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
package fr.paris.lutece.plugins.calendar.web;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.calendar.business.MultiAgendaEvent;
import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.plugins.calendar.service.CalendarService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 * An Utility class to manipulate html code.
 */
public class HtmlUtils
{
    private static final String HTML_START = "<";
    private static final String HTML_BR = "<br />";
    private static final String LINEFEED = "\\n";

    /**
     * Remove the part of the string that contains HTML
     * @param strSource The source string
     * @return the cleaned string
     */
    public static String removeHtml( String strSource )
    {
        String strReturn = strSource;
        int nPos = strSource.indexOf( HTML_START );

        if ( nPos >= 0 )
        {
            strReturn = strSource.substring( 0, nPos );
        }

        return strReturn;
    }

    /**
     * Convert CR into HTML <br />
     * @param strSource The string to convert
     * @return The converted string
     */
    public static String convertCR( String strSource )
    {
        String strReturn = StringUtils.EMPTY;

        if ( StringUtils.isNotBlank( strSource ) )
        {
            strReturn = strSource.replaceAll( LINEFEED, HTML_BR );
        }

        return strReturn;
    }

    /**
     * Fill a template with events info
     * @param model The model used to fill events
     * @param event The event
     * @param strDate The current date
     */
    public static void fillEventTemplate( Map<String, Object> model, MultiAgendaEvent event, String strDate )
    {
    	CalendarService calendarService = (CalendarService) SpringContextService.getPluginBean( CalendarPlugin.PLUGIN_NAME, 
        		Constants.BEAN_CALENDAR_CALENDARSERVICE );
    	int nShortTitleLength = AppPropertiesService.getPropertyInt( Constants.PROPERTY_EVENT_SHORT_TITLE_LENGTH, 18 );
        StringBuilder sbTitle = new StringBuilder(  );
        String strImage = StringUtils.EMPTY;
        
        AgendaResource agenda = calendarService.getAgendaResource( event.getAgenda(  ) );
        if ( agenda != null )
        {
        	sbTitle.append( agenda.getEventPrefix(  ) );
        	sbTitle.append( Constants.SPACE );
        	strImage = agenda.getEventImage(  );
        }
        
        if ( StringUtils.isNotBlank( event.getDateTimeStart(  ) ) )
        {
        	sbTitle.append( event.getDateTimeStart(  ) );
        }
        sbTitle.append( Constants.INDENT );
        if ( StringUtils.isNotBlank( event.getDateTimeEnd(  ) ) )
        {
        	sbTitle.append( event.getDateTimeEnd(  ) );
        }
        sbTitle.append( Constants.SPACE );
        
        sbTitle.append( removeHtml( event.getTitle(  ) ) );

        String strShortTitle = sbTitle.toString(  );

        if ( strShortTitle.length(  ) > nShortTitleLength )
        {
        	StringBuilder sbShortTitle = new StringBuilder(  );
        	sbShortTitle.append( strShortTitle.substring( 0, nShortTitleLength ) );
        	sbShortTitle.append( AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_SHORT_TITLE_END ) );
        	strShortTitle = sbShortTitle.toString(  );
        }

        String strDescription = ( event.getDescription(  ) != null ) ? event.getDescription(  ) : StringUtils.EMPTY;
        String strLocation = ( event.getLocation(  ) != null ) ? event.getLocation(  ) : StringUtils.EMPTY;
        String strUrl = ( event.getUrl(  ) != null ) ? event.getUrl(  ) : StringUtils.EMPTY;

        model.put( Constants.MARK_AGENDA, event.getAgenda(  ) );
        model.put( Constants.MARK_EVENT_TITLE, sbTitle.toString(  ) );
        model.put( Constants.MARK_EVENT_SHORT_TITLE, strShortTitle );
        model.put( Constants.MARK_EVENT_DESCRIPTION, strDescription );
        model.put( Constants.MARK_EVENT_LOCATION, strLocation );
        model.put( Constants.MARK_EVENT_URL, strUrl );
        model.put( Constants.MARK_EVENT_IMAGE, strImage );
        model.put( Constants.MARK_DATE, strDate );
    }
}
