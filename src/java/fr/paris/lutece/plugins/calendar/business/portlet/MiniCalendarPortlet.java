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
package fr.paris.lutece.plugins.calendar.business.portlet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.plugins.calendar.service.EventImageResourceService;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.plugins.calendar.service.XMLUtils;
import fr.paris.lutece.plugins.calendar.service.search.CalendarSearchService;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 * This class represents the business object CalendarPortlet.
 */
public class MiniCalendarPortlet extends Portlet
{
    // The names of the XML tags
    private static final String TAG_EVENTS = "events";
    private static final String TAG_URL = "url";
    private static final String TAG_EVENT_ID = "event-id";
    private static final String TAG_AGENDA_ID = "agenda-id";
    private static final String TAG_TOP_EVENTS = "top-events";
    private static final String TAG_TOP_EVENT = "top-event";
    private static final String TAG_TOP_EVENT_TITLE = "top-event-title";
    private static final String TAG_EVENT = "event";
    private static final String TAG_EVENT_TITLE = "event-title";
    private static final String TAG_EVENT_DATETIME_BEGIN = "event-datetime-begin";
    private static final String TAG_EVENT_DESCRIPTION = "event-description";
    private static final String TAG_DATE = "date";
    
    //JSP
    private static final String JSP_PORTAL_URL = "jsp/site/Portal.jsp";

    //Commons
    private static final String POINT_INTERROGATION = "?";
    private static final String EGAL = "=";
    private static final String PARAM_PAGE_ID = "page_id";

    //Session variable
    private static Calendar _cal = null;

    /**
     * Sets the name of the plugin associated with this portlet.
     *
     * @param strPluginName The plugin name.
     */
    public void setPluginName( String strPluginName )
    {
        super.setPluginName( strPluginName );
    }

    /**
     * Returns the Xml code of the Archive portlet with XML heading
     *
     * @param request The HTTP servlet request
     * @return the Xml code of the Archive portlet
     */
    public String getXmlDocument( HttpServletRequest request )
    {
        return XmlUtil.getXmlHeader(  ) + getXml( request );
    }

    /**
     * Returns the Xml code of the Calendar portlet without XML heading
     *
     * @param request The HTTP servlet request
     * @return the Xml code of the Archive portlet content
     */
    public String getXml( HttpServletRequest request )
    {
        StringBuffer strXml = new StringBuffer(  );
        Locale local;

        if ( _cal == null )
        {
            _cal = new GregorianCalendar(  );
        }

        if ( request != null )
        {
            local = request.getLocale(  );

            if ( ( request.getParameter( Constants.PARAMETER_MONTH ) != null ) &&
                    ( request.getParameter( Constants.PARAMETER_MONTH ) ).equals( Constants.PARAMETER_NEXT ) )
            {
                _cal.add( Calendar.MONTH, 1 );
            }
            else if ( ( request.getParameter( Constants.PARAMETER_MONTH ) != null ) &&
                    ( request.getParameter( Constants.PARAMETER_MONTH ) ).equals( Constants.PARAMETER_PREV ) )
            {
                _cal.add( Calendar.MONTH, -1 );
            }

            XmlUtil.addElement( strXml, TAG_URL,
                AppPathService.getBaseUrl( request ) + JSP_PORTAL_URL + POINT_INTERROGATION + PARAM_PAGE_ID + EGAL );
        }
        else
        {
            local = Locale.getDefault(  );
        }

        //Load the xml calendar	    	
        strXml.append( XMLUtils.getXMLPortletCalendar( local, _cal, request ) );

        //Top event section            
        Plugin plugin = PluginService.getPlugin( Constants.PLUGIN_NAME );

        boolean top_event = MiniCalendarPortletHome.showTopEvent( plugin );

        if ( top_event )
        {
            XmlUtil.beginElement( strXml, TAG_TOP_EVENTS );

            List<SimpleEvent> listEvents = CalendarHome.findTopEventList( plugin );
            Iterator<SimpleEvent> i = listEvents.iterator(  );

            while ( i.hasNext(  ) )
            {
                XmlUtil.beginElement( strXml, TAG_TOP_EVENT );

                SimpleEvent event = (SimpleEvent) i.next(  );
                XmlUtil.addElement( strXml, TAG_TOP_EVENT_TITLE,
                    ( event.getTitle(  ) != null ) ? event.getTitle(  ) : "" );
                XmlUtil.addElement( strXml, TAG_EVENT_ID, event.getId(  ) );
                XmlUtil.addElement( strXml, TAG_AGENDA_ID, event.getIdCalendar(  ) );
                XmlUtil.endElement( strXml, TAG_TOP_EVENT );
                //Register the image
                EventImageResourceService.getInstance(  ).getResourceImageEvent( event.getId(  ) );
            }

            XmlUtil.endElement( strXml, TAG_TOP_EVENTS );
        }

        String strDateBegin = request.getParameter( Constants.PARAMETER_DATE );
        
        if ( strDateBegin != null && !strDateBegin.equals( Constants.EMPTY_STRING ) )
        {
            String[] arrayCategory = null;
            String[] arrayCalendar = Utils.getCalendarIds( request );
            String strQuery = StringUtils.EMPTY;
            List<Event> listEvent = null;
            Plugin pluginCalendar = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
            
            Date dateBegin = DateUtil.formatDate( strDateBegin, request.getLocale(  ) );;
            Date dateEnd = dateBegin;
            
            listEvent = CalendarSearchService.getInstance(  )
            .getSearchResults( arrayCalendar, arrayCategory, strQuery, dateBegin,
            		dateEnd, request, pluginCalendar );
            if ( listEvent != null )
            {
            	//Sort events by DateTimeStart using bubble sort
            	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            	boolean bisModification;
            	Date date1;
            	Date date2;
            	Event temporaryEvent;
            	do
            	{
            		bisModification = true;
            		for(int j=0; j<listEvent.size()-1;j++)
            		{
            			try{
            				
            		        date1 = sdf.parse(listEvent.get(j).getDateTimeStart());
            			}
            			catch(Exception e)
            			{
            				date1 = new Date(0);
            			}
            			try{
            				
            		        date2 = sdf.parse(listEvent.get(j+1).getDateTimeStart());
            			}
            			catch(Exception e)
            			{
            				date2 =  new Date(0);
            			}
            			if(date1.after(date2))
            			{
            				temporaryEvent = listEvent.get(j+1);
            				listEvent.set(j+1, listEvent.get(j));
            				listEvent.set(j, temporaryEvent);
            				bisModification = false;
            			}
            			
            		}
            		
            	}while(!bisModification);
            
            	XmlUtil.beginElement( strXml, TAG_EVENTS );
            	XmlUtil.addElement( strXml, TAG_DATE, strDateBegin);
            	for ( Event event : listEvent )
            	{
            		XmlUtil.beginElement( strXml, TAG_EVENT );
            		XmlUtil.addElement( strXml, TAG_EVENT_TITLE,
                            ( event.getTitle(  ) != null ) ? event.getTitle(  ) : "" );
            		XmlUtil.addElement( strXml, TAG_EVENT_DATETIME_BEGIN,
                            ( event.getDateTimeStart() != null ) ? event.getDateTimeStart(  ) : "" );
            		XmlUtil.addElement( strXml, TAG_EVENT_DESCRIPTION,
                            ( event.getDescription() != null ) ? event.getDescription() : "" );
            		XmlUtil.addElement( strXml, TAG_EVENT_ID,event.getId());
            		XmlUtil.endElement( strXml, TAG_EVENT );
            		
            	}
            	XmlUtil.endElement( strXml, TAG_EVENTS );
            }
        }
        
        String str = addPortletTags( strXml );

        return str;
    }

    /**
     * Updates the current instance of the CalendarPortlet object
     */
    public void update(  )
    {
        MiniCalendarPortletHome.getInstance(  ).update( this );
    }

    /**
     * Removes the current instance of the CalendarPortlet object
     */
    public void remove(  )
    {
        MiniCalendarPortletHome.getInstance(  ).remove( this );
    }
}