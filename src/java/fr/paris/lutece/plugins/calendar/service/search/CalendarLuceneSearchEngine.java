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

import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.BytesRef;


/**
 * LuceneSearchEngine
 */
public class CalendarLuceneSearchEngine implements CalendarSearchEngine
{
    private static final String OPEN_PARENTHESIS = "(";
    private static final String SPACE = " ";
    private static final String EMPTY_STRING = "";
    private static final String CLOSE_PARENTHESIS = ")";
    private static final String OR = "OR";
    private static final String PROPERTY_RESULTS_LIMIT = "calendar.indexer.results.limit";

    /**
     * Return search results
     * @param arrayAgendaIds The calendar ids
     * @param arrayCategory the category ids
     * @param strContent The search query
     * @param dateBegin The date begin
     * @param dateEnd The date end
     * @param request The {@link HttpServletRequest}
     * @return Results as a collection of SearchResult
     */
    public List<CalendarSearchResult> getSearchResults( String[] arrayAgendaIds, String[] arrayCategory,
            String strContent, Date dateBegin, Date dateEnd, HttpServletRequest request )
    {
        ArrayList<CalendarSearchItem> listResults = new ArrayList<CalendarSearchItem>( );

        if ( arrayAgendaIds == null || arrayAgendaIds.length == 0 )
        {
            return new ArrayList<CalendarSearchResult>( );
        }
        IndexSearcher searcher = null;

        //Filter filterRole = getFilterRoles( request );
        Filter filterRole = null;

        try
        {
            IndexReader ir = DirectoryReader.open( IndexationService.getDirectoryIndex( ) );
            searcher = new IndexSearcher( ir );

            Collection<String> queriesForSearchInContent = new ArrayList<String>( );
            Collection<String> queriesForSearchInTitle = new ArrayList<String>( );
            Collection<String> fieldsForSearchInContent = new ArrayList<String>( );
            Collection<String> fieldsForSearchInTitle = new ArrayList<String>( );
            Collection<BooleanClause.Occur> flagsForSearchInContent = new ArrayList<BooleanClause.Occur>( );
            Collection<BooleanClause.Occur> flagsForSearchInTitle = new ArrayList<BooleanClause.Occur>( );

            //Calendar Id
            if ( arrayAgendaIds.length > 0 )
            {
                String strQueryCalendar = OPEN_PARENTHESIS;
                int intMoreCalendar = 0;

                for ( String strAgendaId : arrayAgendaIds )
                {
                    strQueryCalendar += ( strAgendaId + "_" + Constants.CALENDAR_SHORT_NAME );
                    ++intMoreCalendar;

                    if ( ( arrayAgendaIds.length > 1 ) && ( intMoreCalendar < arrayAgendaIds.length ) )
                    {
                        strQueryCalendar += ( SPACE + OR + SPACE );
                    }
                }

                strQueryCalendar += CLOSE_PARENTHESIS;

                Query queryAgendaId = new TermQuery( new Term( Constants.FIELD_CALENDAR_ID, strQueryCalendar ) );
                queriesForSearchInContent.add( queryAgendaId.toString( ) );
                queriesForSearchInTitle.add( queryAgendaId.toString( ) );
                fieldsForSearchInContent.add( Constants.FIELD_CALENDAR_ID );
                flagsForSearchInContent.add( BooleanClause.Occur.MUST );
                fieldsForSearchInTitle.add( Constants.FIELD_CALENDAR_ID );
                flagsForSearchInTitle.add( BooleanClause.Occur.MUST );
            }

            //category Id
            if ( ( arrayCategory != null ) && ( arrayCategory.length > 0 ) )
            {
                String strQueryCategory = OPEN_PARENTHESIS;
                int intMoreCategory = 0;

                for ( String strCategoryId : arrayCategory )
                {
                    strQueryCategory += strCategoryId;
                    ++intMoreCategory;

                    if ( ( arrayCategory.length > 1 ) && ( intMoreCategory < arrayCategory.length ) )
                    {
                        strQueryCategory += ( SPACE + OR + SPACE );
                    }
                }

                strQueryCategory += CLOSE_PARENTHESIS;

                Query queryAgendaId = new TermQuery( new Term( Constants.FIELD_CATEGORY, strQueryCategory ) );
                queriesForSearchInContent.add( queryAgendaId.toString( ) );
                queriesForSearchInTitle.add( queryAgendaId.toString( ) );
                fieldsForSearchInContent.add( Constants.FIELD_CATEGORY );
                flagsForSearchInContent.add( BooleanClause.Occur.MUST );
                fieldsForSearchInTitle.add( Constants.FIELD_CATEGORY );
                flagsForSearchInTitle.add( BooleanClause.Occur.MUST );
            }

            //Type (=calendar)
            PhraseQuery queryType = new PhraseQuery( );
            queryType.add( new Term( SearchItem.FIELD_TYPE, Constants.PLUGIN_NAME ) );
            queriesForSearchInContent.add( queryType.toString( ) );
            queriesForSearchInTitle.add( queryType.toString( ) );
            fieldsForSearchInContent.add( SearchItem.FIELD_TYPE );
            flagsForSearchInContent.add( BooleanClause.Occur.MUST );
            fieldsForSearchInTitle.add( SearchItem.FIELD_TYPE );
            flagsForSearchInTitle.add( BooleanClause.Occur.MUST );

            //Content
            if ( ( strContent != null ) && !strContent.equals( EMPTY_STRING ) )
            {
                Query queryContent = new TermQuery( new Term( SearchItem.FIELD_CONTENTS, strContent ) );
                queriesForSearchInTitle.add( queryContent.toString( ) );
                fieldsForSearchInContent.add( SearchItem.FIELD_CONTENTS );
                flagsForSearchInContent.add( BooleanClause.Occur.MUST );
            }

            //Dates
            if ( ( dateBegin != null ) && ( dateEnd != null ) )
            {
                BytesRef strDateBegin = new BytesRef( Utils.getDate( dateBegin ) );
                BytesRef strDateEnd = new BytesRef( Utils.getDate( dateEnd ) );
                Query queryDate = new TermRangeQuery( SearchItem.FIELD_DATE, strDateBegin, strDateEnd, true, true );
                queriesForSearchInContent.add( queryDate.toString( ) );
                queriesForSearchInTitle.add( queryDate.toString( ) );
                fieldsForSearchInContent.add( SearchItem.FIELD_DATE );
                flagsForSearchInContent.add( BooleanClause.Occur.MUST );
                fieldsForSearchInTitle.add( SearchItem.FIELD_DATE );
                flagsForSearchInTitle.add( BooleanClause.Occur.MUST );
            }

            //Titre
            if ( ( strContent != null ) && !strContent.equals( EMPTY_STRING ) )
            {
                Query queryTitle = new TermQuery( new Term( SearchItem.FIELD_TITLE, strContent ) );
                queriesForSearchInContent.add( queryTitle.toString( ) );
                fieldsForSearchInTitle.add( SearchItem.FIELD_TITLE );
                flagsForSearchInTitle.add( BooleanClause.Occur.MUST );
            }

            //Search in contents
            Query queryMulti = MultiFieldQueryParser.parse( IndexationService.LUCENE_INDEX_VERSION,
                    queriesForSearchInContent.toArray( new String[queriesForSearchInContent.size( )] ),
                    fieldsForSearchInContent.toArray( new String[fieldsForSearchInContent.size( )] ),
                    flagsForSearchInContent.toArray( new BooleanClause.Occur[flagsForSearchInContent.size( )] ),
                    IndexationService.getAnalyser( ) );

            // Get results documents
            TopDocs hits = null;

            int nLimit = Integer.parseInt( AppPropertiesService.getProperty( PROPERTY_RESULTS_LIMIT ) );
            hits = searcher.search( queryMulti, filterRole, nLimit );

            for ( int i = 0; hits.totalHits > i; i++ )
            {
                ScoreDoc hit = hits.scoreDocs[i];
                Document document = searcher.doc( hit.doc );
                CalendarSearchItem si = new CalendarSearchItem( document );
                listResults.add( si );
            }

            //Search in titles
            Query queryMultiTitle = MultiFieldQueryParser.parse( IndexationService.LUCENE_INDEX_VERSION,
                    queriesForSearchInTitle.toArray( new String[queriesForSearchInTitle.size( )] ),
                    fieldsForSearchInTitle.toArray( new String[fieldsForSearchInTitle.size( )] ),
                    flagsForSearchInTitle.toArray( new BooleanClause.Occur[flagsForSearchInTitle.size( )] ),
                    IndexationService.getAnalyser( ) );

            // Get results documents
            TopDocs hitsTitle = null;

            hitsTitle = searcher.search( queryMultiTitle, filterRole, nLimit );

            for ( int i = 0; hitsTitle.totalHits > i; i++ )
            {
                ScoreDoc hit = hitsTitle.scoreDocs[i];
                Document document = searcher.doc( hit.doc );
                CalendarSearchItem si = new CalendarSearchItem( document );
                listResults.add( si );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return convertList( listResults );
    }

    /**
     * Convert the SearchItem list on SearchResult list
     * @param listSource The source list
     * @return The result list
     */
    private List<CalendarSearchResult> convertList( List<CalendarSearchItem> listSource )
    {
        List<CalendarSearchResult> listDest = new ArrayList<CalendarSearchResult>( );

        for ( CalendarSearchItem item : listSource )
        {
            CalendarSearchResult result = new CalendarSearchResult( );
            result.setId( item.getId( ) );

            try
            {
                result.setDate( DateTools.stringToDate( item.getDate( ) ) );
            }
            catch ( ParseException e )
            {
                AppLogService.error( "Bad Date Format for indexed item \"" + item.getTitle( ) + "\" : "
                        + e.getMessage( ) );
            }

            result.setUrl( item.getUrl( ) );
            result.setTitle( item.getTitle( ) );
            result.setSummary( item.getSummary( ) );
            result.setType( item.getType( ) );
            result.setHtmlSummary( item.getHtmlSummary( ) );
            listDest.add( result );
        }

        return listDest;
    }
}
