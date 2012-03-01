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

import fr.paris.lutece.plugins.calendar.business.CalendarFilter;
import fr.paris.lutece.plugins.calendar.business.stylesheet.CalendarStyleSheetHome;
import fr.paris.lutece.plugins.calendar.service.XMLUtils;
import fr.paris.lutece.portal.business.stylesheet.StyleSheet;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.stylesheet.StyleSheetJspBean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * This class provides the user interface to manage calendars from the dataBase features ( manage, create, modify, remove)
 */
public class CalendarDownloadFile
{
    //Properties
    private static final String PROPERTY_FILE_EXPORT_NAME = "agenda";
    private static final String PROPERTY_RSS_FILE_NAME = "rss";
    private static final String PROPERTY_CALENDAR_RSS_FILE_ID = "calendar.rss.xls.id";

    //Messages
    private static final String PROPERTY_ERROR_MESSAGE = "calendar.message.errorDownloadFile";

    public void downloadCalendarToFile( HttpServletRequest request, HttpServletResponse response )
        throws SiteMessageException
    {
        byte[] result = null;
        String strAgenda = request.getParameter( Constants.PARAM_AGENDA );
        String strStyleSheetId = request.getParameter( Constants.PARAMETER_STYLESHEET_ID );
        String strRss = request.getParameter( Constants.PARAMETER_ACTION );
        String strXml = Constants.EMPTY_STRING;
        String strEventSort = request.getParameter( Constants.PARAMETER_SORT_EVENTS );
        String[] arrayCategory = request.getParameterValues( Constants.PARAMETER_CATEGORY );
        String[] arrayCalendar = request.getParameterValues( Constants.PARAMETER_CALENDAR_ID );
        int[] arrayCategoryIds = null;
        int[] arrayCalendarIds = null;

        // Mandatory field
        if ( ( strAgenda != null ) && strAgenda.equals( "none" ) )
        {
            SiteMessageService.setMessage( request, Messages.MANDATORY_FIELDS, SiteMessage.TYPE_STOP );
        }

        //Category ids
        if ( arrayCategory != null )
        {
            if ( arrayCategory.length != 0 )
            {
                arrayCategoryIds = new int[arrayCategory.length];
            }

            int i = 0;

            for ( String strIdCategory : arrayCategory )
            {
                if ( strIdCategory != null )
                {
                    arrayCategoryIds[i++] = Integer.parseInt( strIdCategory );
                }
            }
        }

        //Calendar ids
        if ( arrayCalendar != null )
        {
            if ( !arrayCalendar[0].equals( Constants.PROPERTY_CALENDAR_NONE ) ) //FIXME
            {
                arrayCalendarIds = new int[arrayCalendar.length];
            }

            int i = 0;

            for ( String strId : arrayCalendar )
            {
                if ( strId != null )
                {
                    arrayCalendarIds[i++] = Integer.parseInt( strId );
                }
            }
        }

        CalendarFilter filter = new CalendarFilter(  );

        if ( strRss != null )
        {
            filter.setCategoriesId( arrayCategoryIds );
            filter.setCalendarIds( arrayCalendarIds );
            filter.setSortEvents( ( strEventSort != null ) ? Integer.parseInt( strEventSort ) : ( -1 ) );
            strXml = XMLUtils.getRssXml( filter );
        }
        else
        {
            strXml = XMLUtils.getAgendaXml( strAgenda, request );
        }

        try
        {
            String strFileName = Constants.EMPTY_STRING;
            ByteArrayInputStream baSource = null;

            Plugin plugin = PluginService.getPlugin( Constants.PLUGIN_NAME );
            
            if ( strStyleSheetId != null )
            {
                int nStyleSheetId = Integer.parseInt( strStyleSheetId );
                StyleSheet stylesheet = CalendarStyleSheetHome.findByPrimaryKey( nStyleSheetId, plugin );
                String strAgendaExtension = CalendarStyleSheetHome.getExtension( nStyleSheetId, plugin );
                baSource = new ByteArrayInputStream( stylesheet.getSource(  ) );
                strFileName = PROPERTY_FILE_EXPORT_NAME + "." + strAgendaExtension;
                response.setHeader( "Content-disposition", "attachement; filename=\"" + strFileName + "\"" );
            }
            else if ( strRss != null )
            {
                int nStyleSheetId = AppPropertiesService.getPropertyInt( PROPERTY_CALENDAR_RSS_FILE_ID, 1 );
                StyleSheet stylesheet = CalendarStyleSheetHome.findByPrimaryKey( nStyleSheetId, plugin );
                String strAgendaExtension = CalendarStyleSheetHome.getExtension( nStyleSheetId, plugin );
                baSource = new ByteArrayInputStream( stylesheet.getSource(  ) );
                strFileName = PROPERTY_RSS_FILE_NAME + "." + strAgendaExtension;
                response.setHeader( "Content-disposition", "inline; filename=\"" + strFileName + "\"" );
            }

            if ( baSource != null )
            {
                HttpSession session = request.getSession(  );
                ServletContext context = session.getServletContext(  );
                String strMimetype = context.getMimeType( strFileName );
                response.setContentType( ( strMimetype != null ) ? strMimetype : "application/octet-stream" );
                result = XMLUtils.transformXMLToXSL( strXml, baSource, null, null );
            }

            OutputStream os = response.getOutputStream(  );
            os.write( result );
            os.flush(  );
            os.close(  );
        }
        catch ( IOException e )
        {
            throw new AppException( I18nService.getLocalizedString( PROPERTY_ERROR_MESSAGE, request.getLocale(  ) ) );
        }
        catch ( Exception e )
        {
            throw new AppException( I18nService.getLocalizedString( PROPERTY_ERROR_MESSAGE, request.getLocale(  ) ) );
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
         * @throws ServletException the servlet Exception
         * @throws IOException the io exception
     */
    public void downloadXSLFileFromDatabase( HttpServletRequest request, HttpServletResponse response )
    {
        AdminUser user = AdminUserService.getAdminUser( request );

        if ( ( user != null ) && ( user.checkRight( StyleSheetJspBean.RIGHT_MANAGE_STYLESHEET ) ) )
        {
            String strStyleSheetId = request.getParameter( Parameters.STYLESHEET_ID );

            try
            {
                if ( strStyleSheetId != null )
                {
                    int nStyleSheetId = Integer.parseInt( strStyleSheetId );

                    Plugin plugin = PluginService.getPlugin( Constants.PLUGIN_NAME );
                    
                    StyleSheet stylesheet = CalendarStyleSheetHome.findByPrimaryKey( nStyleSheetId, plugin );

                    HttpSession session = request.getSession(  );
                    ServletContext context = session.getServletContext(  );
                    String strMimetype = context.getMimeType( stylesheet.getFile(  ) );
                    response.setContentType( ( strMimetype != null ) ? strMimetype : "application/octet-stream" );
                    response.setHeader( "Content-Disposition",
                        "attachement; filename=\"" + stylesheet.getFile(  ) + "\"" );

                    OutputStream out = response.getOutputStream(  );
                    out.write( stylesheet.getSource(  ) );
                    out.flush(  );
                    out.close(  );
                }
            }
            catch ( IOException e )
            {
                throw new AppException( I18nService.getLocalizedString( PROPERTY_ERROR_MESSAGE, request.getLocale(  ) ) );
            }
        }
    }
}
