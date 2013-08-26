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
package fr.paris.lutece.plugins.calendar.service.search;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.business.OccurrenceEvent;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.business.category.Category;
import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.demo.html.HTMLParser;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;


/**
 * CalendarIndexer
 */
public class CalendarIndexer implements SearchIndexer
{
    //properties
    public static final String PROPERTY_INDEXER_NAME = "calendar.indexer.name";
    public static final String SHORT_NAME = "cld";
    private static final String ENABLE_VALUE_TRUE = "1";
    private static final String PROPERTY_INDEXER_DESCRIPTION = "calendar.indexer.description";
    private static final String PROPERTY_INDEXER_VERSION = "calendar.indexer.version";
    private static final String PROPERTY_INDEXER_ENABLE = "calendar.indexer.enable";
    private static final String PROPERTY_DESCRIPTION_MAX_CHARACTERS = "calendar.description.max.characters";
    private static final String BLANK = " ";
    private static final String PROPERTY_DESCRIPTION_ETC = "...";
    private static final String JSP_SEARCH_CALENDAR = "jsp/site/Portal.jsp?page=calendar&action=search";

    /**
     * Index all documents
     * 
     * @throws IOException the exception
     * @throws InterruptedException the exception
     * @throws SiteMessageException the exception
     */
    public void indexDocuments( ) throws IOException, InterruptedException, SiteMessageException
    {
        String sRoleKey = "";

        for ( AgendaResource agenda : Utils.getAgendaResourcesWithOccurrences( ) )
        {
            sRoleKey = agenda.getRole( );

            String strAgenda = agenda.getId( );

            for ( Object oEvent : agenda.getAgenda( ).getEvents( ) )
            {
                indexSubject( oEvent, sRoleKey, strAgenda );
            }
        }
    }

    /**
     * Recursive method for indexing a calendar event
     * 
     * @throws IOException I/O Exception
     * @throws InterruptedException interruptedException
     */
    public void indexSubject( Object oEvent, String sRoleKey, String strAgenda ) throws IOException,
            InterruptedException
    {
        OccurrenceEvent occurrence = (OccurrenceEvent) oEvent;

        if ( occurrence.getStatus( ).equals(
                AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_CONFIRMED ) ) )
        {
            String strPortalUrl = AppPathService.getPortalUrl( );

            UrlItem urlEvent = new UrlItem( strPortalUrl );
            urlEvent.addParameter( XPageAppService.PARAM_XPAGE_APP, CalendarPlugin.PLUGIN_NAME );
            urlEvent.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_SHOW_RESULT );
            urlEvent.addParameter( Constants.PARAMETER_EVENT_ID, occurrence.getEventId( ) );
            urlEvent.addParameter( Constants.PARAM_AGENDA, strAgenda );

            org.apache.lucene.document.Document docSubject = null;
            try
            {
                docSubject = getDocument( occurrence, sRoleKey, urlEvent.getUrl( ), strAgenda );
            }
            catch ( Exception e )
            {
                String strMessage = "Agenda ID : " + strAgenda + " - Occurrence ID : " + occurrence.getId( );
                IndexationService.error( this, e, strMessage );
            }
            if ( docSubject != null )
            {
                IndexationService.write( docSubject );
            }
        }
    }

    /**
     * Get the calendar document
     * @param strDocument id of the subject to index
     * @return The list of lucene documents
     * @throws IOException the exception
     * @throws InterruptedException the exception
     * @throws SiteMessageException the exception
     */
    public List<Document> getDocuments( String strDocument ) throws IOException, InterruptedException,
            SiteMessageException
    {
        List<org.apache.lucene.document.Document> listDocs = new ArrayList<org.apache.lucene.document.Document>( );
        String strPortalUrl = AppPathService.getPortalUrl( );
        Plugin plugin = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );

        OccurrenceEvent occurrence = CalendarHome.findOccurrence( Integer.parseInt( strDocument ), plugin );
        if ( !occurrence.getStatus( ).equals(
                AppPropertiesService.getProperty( Constants.PROPERTY_EVENT_STATUS_CONFIRMED ) ) )
        {
            return null;
        }

        SimpleEvent event = CalendarHome.findEvent( occurrence.getEventId( ), plugin );

        AgendaResource agendaResource = CalendarHome.findAgendaResource( event.getIdCalendar( ), plugin );
        Utils.loadAgendaOccurrences( agendaResource, plugin );

        String sRoleKey = agendaResource.getRole( );
        Agenda agenda = agendaResource.getAgenda( );

        UrlItem urlEvent = new UrlItem( strPortalUrl );
        urlEvent.addParameter( XPageAppService.PARAM_XPAGE_APP, CalendarPlugin.PLUGIN_NAME );
        urlEvent.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_SHOW_RESULT );
        urlEvent.addParameter( Constants.PARAMETER_EVENT_ID, occurrence.getEventId( ) );
        urlEvent.addParameter( Constants.PARAM_AGENDA, agenda.getKeyName( ) );

        org.apache.lucene.document.Document docEvent = getDocument( occurrence, sRoleKey, urlEvent.getUrl( ),
                agenda.getKeyName( ) );

        listDocs.add( docEvent );

        return listDocs;
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of the
     * calendar list
     * @param occurrence The occurence event
     * @param strUrl the url of the subject
     * @param strRoleKey The role key
     * @param strAgenda the calendar id
     * @return A Lucene {@link Document} containing QuestionAnswer Data
     * @throws IOException The IO Exception
     * @throws InterruptedException The InterruptedException
     */
    public static org.apache.lucene.document.Document getDocument( OccurrenceEvent occurrence, String strRoleKey,
            String strUrl, String strAgenda ) throws IOException, InterruptedException
    {
        // make a new, empty document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document( );

        //add the id of the calendar
        doc.add( new Field( Constants.FIELD_CALENDAR_ID, strAgenda + "_" + Constants.CALENDAR_SHORT_NAME,
                Field.Store.NO, Field.Index.NOT_ANALYZED ) );

        //add the category of the event
        Collection<Category> arrayCategories = occurrence.getListCategories( );
        String strCategories = Constants.EMPTY_STRING;

        if ( arrayCategories != null )
        {
            Iterator<Category> i = arrayCategories.iterator( );

            while ( i.hasNext( ) )
                strCategories += ( i.next( ).getId( ) + BLANK );
        }

        doc.add( new Field( Constants.FIELD_CATEGORY, strCategories, Field.Store.NO, Field.Index.ANALYZED ) );

        doc.add( new Field( SearchItem.FIELD_ROLE, strRoleKey, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the question/answer, but is not searchable.
        doc.add( new Field( SearchItem.FIELD_URL, strUrl, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with question/answer, it is indexed, but it is not
        // tokenized prior to indexing.
        String strIdEvent = String.valueOf( occurrence.getId( ) );
        doc.add( new Field( SearchItem.FIELD_UID, strIdEvent + "_" + Constants.CALENDAR_SHORT_NAME, Field.Store.YES,
                Field.Index.NOT_ANALYZED ) );

        // Add the last modified date of the file a field named "modified".
        // Use a field that is indexed (i.e. searchable), but don't tokenize
        // the field into words.
        String strDate = Utils.getDate( occurrence.getDate( ) );
        doc.add( new Field( SearchItem.FIELD_DATE, strDate, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        String strContentToIndex = getContentToIndex( occurrence );
        StringReader readerPage = new StringReader( strContentToIndex );
        HTMLParser parser = new HTMLParser( readerPage );

        //the content of the event descriptionr is recovered in the parser because this one
        //had replaced the encoded caracters (as &eacute;) by the corresponding special caracter (as ?)
        Reader reader = parser.getReader( );
        int c;
        StringBuffer sb = new StringBuffer( );

        while ( ( c = reader.read( ) ) != -1 )
        {
            sb.append( String.valueOf( (char) c ) );
        }

        reader.close( );

        // Add the description as a summary field, so that index can be incrementally maintained.
        // This field is stored, but it is not indexed
        String strDescription = occurrence.getDescription( );
        strDescription = Utils.ParseHtmlToPlainTextString( strDescription );

        try
        {
            strDescription = strDescription.substring( 0,
                    AppPropertiesService.getPropertyInt( PROPERTY_DESCRIPTION_MAX_CHARACTERS, 200 ) )
                    + PROPERTY_DESCRIPTION_ETC;
        }
        catch ( StringIndexOutOfBoundsException e )
        {
        }
        catch ( NullPointerException e )
        {
        }

        doc.add( new Field( SearchItem.FIELD_SUMMARY, strDescription, Field.Store.YES, Field.Index.ANALYZED ) );

        // Add the tag-stripped contents as a Reader-valued Text field so it will
        // get tokenized and indexed.
        doc.add( new Field( SearchItem.FIELD_CONTENTS, sb.toString( ), Field.Store.NO, Field.Index.ANALYZED ) );

        // Add the subject name as a separate Text field, so that it can be searched
        // separately.
        doc.add( new Field( SearchItem.FIELD_TITLE, occurrence.getTitle( ), Field.Store.YES, Field.Index.ANALYZED ) );

        doc.add( new Field( SearchItem.FIELD_TYPE, CalendarPlugin.PLUGIN_NAME, Field.Store.YES,
                Field.Index.NOT_ANALYZED ) );

        // return the document
        return doc;
    }

    /**
     * Set the Content to index (Description, location)
     * @param event The Event
     * @return The content to index
     */
    private static String getContentToIndex( Event event )
    {
        StringBuffer sbContentToIndex = new StringBuffer( );
        //Do not index question here
        sbContentToIndex.append( event.getDescription( ) );
        sbContentToIndex.append( BLANK );
        sbContentToIndex.append( event.getLocationAddress( ) );
        sbContentToIndex.append( BLANK );
        sbContentToIndex.append( event.getLocationTown( ) );
        sbContentToIndex.append( BLANK );
        sbContentToIndex.append( event.getLocationZip( ) );

        return sbContentToIndex.toString( );
    }

    /**
     * Returns the indexer service name
     * @return the indexer service name
     */
    public String getName( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_NAME );
    }

    /**
     * Returns the indexer service version
     * @return the indexer service version
     */
    public String getVersion( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_VERSION );
    }

    /**
     * Returns the indexer service description
     * @return the indexer service description
     */
    public String getDescription( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_DESCRIPTION );
    }

    /**
     * Tells whether the service is enable or not
     * @return true if enable, otherwise false
     */
    public boolean isEnable( )
    {
        boolean bReturn = false;
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE );

        if ( ( strEnable != null )
                && ( strEnable.equalsIgnoreCase( Boolean.TRUE.toString( ) ) || strEnable.equals( ENABLE_VALUE_TRUE ) )
                && PluginService.isPluginEnable( CalendarPlugin.PLUGIN_NAME ) )
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getListType( )
    {
        List<String> listType = new ArrayList<String>( );
        listType.add( CalendarPlugin.PLUGIN_NAME );

        return listType;
    }

    /**
     * {@inheritDoc}
     */
    public String getSpecificSearchAppUrl( )
    {
        return JSP_SEARCH_CALENDAR;
    }
}
