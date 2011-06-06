/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.calendar.business.category;

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This class provides Data Access methods for Category objects
 */
public final class CategoryDAO implements ICategoryDAO
{
    // Constants
    private static final String SQL_QUERY_MAX_PK = " SELECT MAX(id_category) FROM calendar_category ";
    private static final String SQL_QUERY_SELECT_BY_NAME = " SELECT id_category, description, icon_content, icon_mime_type, workgroup_key FROM calendar_category WHERE calendar_category_name = ? ";
    private static final String SQL_QUERY_SELECTALL = " SELECT id_category, calendar_category_name, description, icon_content, icon_mime_type, workgroup_key FROM calendar_category ORDER BY calendar_category_name";
    private static final String SQL_QUERY_INSERT = " INSERT INTO calendar_category ( id_category, calendar_category_name, description, icon_content, icon_mime_type, workgroup_key ) VALUES ( ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_SELECT = " SELECT calendar_category_name, description, icon_content, icon_mime_type ,workgroup_key FROM calendar_category WHERE id_category = ? ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM calendar_category WHERE id_category = ? ";
    private static final String SQL_QUERY_UPDATE = " UPDATE calendar_category SET calendar_category_name = ?, description = ?, icon_content = ?, icon_mime_type = ?, workgroup_key= ? WHERE id_category = ?";
    private static final String SQL_QUERY_DELETE_LINK_CATEGORY_CALENDAR = " DELETE FROM calendar_category WHERE id_category = ? ";
    private static final String SQL_QUERY_SELECTALL_ID_EVENT = " SELECT a.id_event FROM calendar_category_link a WHERE a.id_category = ? ";
    private static final String SQL_QUERY_DELETE_LINKS_CATEGORY = " DELETE FROM calendar_category_link WHERE id_category = ? ";
    private static final String SQL_QUERY_SELECT_COUNT_OF_EVENT_ID = " SELECT COUNT(*) FROM calendar_category_link WHERE id_category = ?";
    private static final String SQL_QUERY_SELECT_EVENT_CATEGORIES = " SELECT a.id_category, a.calendar_category_name, a.description, a.icon_content, a.icon_mime_type , a.workgroup_key" +
        " FROM calendar_category a, calendar_category_link b" +
        " WHERE a.id_category = b.id_category AND b.id_event = ?";

    // ImageResource queries
    private static final String SQL_QUERY_SELECT_RESOURCE_IMAGE = " SELECT icon_content, icon_mime_type FROM calendar_category WHERE id_category = ? ";

    ///////////////////////////////////////////////////////////////////////////////////////
    //Access methods to data

    /**
     * Load the list of Category
	 * @param plugin Plugin
     * @return The Collection of Category (empty collection is no result)
     */
    public Collection<Category> selectAll( Plugin plugin )
    {
        int nParam;
        Collection<Category> listCategory = new ArrayList<Category>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nParam = 0;

            Category category = new Category(  );
            category.setId( daoUtil.getInt( ++nParam ) );
            category.setName( daoUtil.getString( ++nParam ) );
            category.setDescription( daoUtil.getString( ++nParam ) );
            category.setIconContent( daoUtil.getBytes( ++nParam ) );
            category.setIconMimeType( daoUtil.getString( ++nParam ) );
            category.setWorkgroup( daoUtil.getString( ++nParam ) );

            listCategory.add( category );
        }

        daoUtil.free(  );

        return listCategory;
    }

    /**
     * Insert a new Category
     * @param category The object category to insert
     * @param plugin Plugin
     */
    public void insert( Category category, Plugin plugin )
    {
        int nParam = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setInt( ++nParam, getNewPrimaryKey( plugin ) );
        daoUtil.setString( ++nParam, category.getName(  ) );
        daoUtil.setString( ++nParam, category.getDescription(  ) );
        daoUtil.setBytes( ++nParam, category.getIconContent(  ) );
        daoUtil.setString( ++nParam, category.getIconMimeType(  ) );
        daoUtil.setString( ++nParam, category.getWorkgroup(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Auto increment the primary key for the new category
     * @param plugin Plugin
     * @return the new primary key for category
     */
    private int getNewPrimaryKey( Plugin plugin )
    {
        int nNewPrimaryKey = -1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_MAX_PK, plugin );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nNewPrimaryKey = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return ++nNewPrimaryKey;
    }

    /**
     * Load the data of Category from the table
     * @param nIdCategory The identifier of the category
     * @param plugin Plugin
     * @return The Instance of the object Category
     */
    public Category load( int nIdCategory, Plugin plugin )
    {
        int nParam;
        Category category = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdCategory );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nParam = 0;
            category = new Category(  );
            category.setId( nIdCategory );
            category.setName( daoUtil.getString( ++nParam ) );
            category.setDescription( daoUtil.getString( ++nParam ) );
            category.setIconContent( daoUtil.getBytes( ++nParam ) );
            category.setIconMimeType( daoUtil.getString( ++nParam ) );
            category.setWorkgroup( daoUtil.getString( ++nParam ) );
        }

        daoUtil.free(  );

        return category;
    }

    /**
     * Load the data of Category for an event from the table
     * @param nIdEvent The identifier of the event
     * @param plugin Plugin
     * @return The Instance of the object Category
     */
    public Collection<Category> selectByEvent( int nIdEvent, Plugin plugin )
    {
        int nParam;
        Collection<Category> listCategory = new ArrayList<Category>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_EVENT_CATEGORIES, plugin );
        daoUtil.setInt( 1, nIdEvent );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nParam = 0;

            Category category = new Category(  );
            category.setId( daoUtil.getInt( ++nParam ) );
            category.setName( daoUtil.getString( ++nParam ) );
            category.setDescription( daoUtil.getString( ++nParam ) );
            category.setIconContent( daoUtil.getBytes( ++nParam ) );
            category.setIconMimeType( daoUtil.getString( ++nParam ) );
            category.setWorkgroup( daoUtil.getString( ++nParam ) );

            listCategory.add( category );
        }

        daoUtil.free(  );

        return listCategory;
    }

    /**
     * Delete a record from the table
     * @param nIdCategory The identifier of the object Category
     * @param plugin Plugin
     */
    public void delete( int nIdCategory, Plugin plugin )
    {
        int nParam = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( ++nParam, nIdCategory );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the record in the table
     * @param category The instance of the Category to update
     * @param plugin Plugin
     */
    public void store( Category category, Plugin plugin )
    {
        int nParam = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setString( ++nParam, category.getName(  ) );
        daoUtil.setString( ++nParam, category.getDescription(  ) );
        daoUtil.setBytes( ++nParam, category.getIconContent(  ) );
        daoUtil.setString( ++nParam, category.getIconMimeType(  ) );
        daoUtil.setString( ++nParam, category.getWorkgroup(  ) );
        daoUtil.setInt( ++nParam, category.getId(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of Category from the table
     * @param strCategoryName The name of the category
     * @param plugin Plugin
     * @return The Collection of Category (empty collection is no result)
     */
    public Collection<Category> selectByName( String strCategoryName, Plugin plugin )
    {
        int nParam;
        Collection<Category> listCategory = new ArrayList<Category>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_NAME, plugin );
        daoUtil.setString( 1, strCategoryName );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nParam = 0;

            Category category = new Category(  );
            category.setId( daoUtil.getInt( ++nParam ) );
            category.setName( strCategoryName );
            category.setDescription( daoUtil.getString( ++nParam ) );
            category.setIconContent( daoUtil.getBytes( ++nParam ) );
            category.setIconMimeType( daoUtil.getString( ++nParam ) );
            category.setWorkgroup( daoUtil.getString( ++nParam ) );

            listCategory.add( category );
        }

        daoUtil.free(  );

        return listCategory;
    }

    /**
     * Delete a link between category and event
     * @param nIdCategory The identifier of the object Category
     * @param nIdEvent The id of document
     * @param plugin Plugin
     */
    public void deleteLinkCategoryEvent( int nIdCategory, int nIdEvent, Plugin plugin )
    {
        int nParam = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LINK_CATEGORY_CALENDAR, plugin );
        daoUtil.setInt( ++nParam, nIdEvent );
        daoUtil.setInt( ++nParam, nIdCategory );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Select a list of Id Events for a specified category
     * @param nIdCategory The category name
     * @param plugin Plugin
     * @return The array of Id Event
     */
    public int[] selectAllIdEvent( int nIdCategory, Plugin plugin )
    {
        Collection<Integer> listIdEvent = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID_EVENT, plugin );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            listIdEvent.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        // Convert ArrayList to Int[]
        int[] arrayIdEvent = new int[listIdEvent.size(  )];
        int i = 0;

        for ( Integer nIdDocument : listIdEvent )
        {
            arrayIdEvent[i++] = nIdDocument.intValue(  );
        }

        return arrayIdEvent;
    }

    /**
     * Delete all links for a category
     * @param nIdCategory The identifier of the object Category
     * @param plugin Plugin
     */
    public void deleteLinksCategory( int nIdCategory, Plugin plugin )
    {
        int nParam = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_LINKS_CATEGORY, plugin );
        daoUtil.setInt( ++nParam, nIdCategory );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Return the number of Events linked to a category
     * @param nIdCategory The category name
     * @param plugin Plugin
     * @return count of id event
     */
    public int selectCountIdEvents( int nIdCategory, Plugin plugin )
    {
        int nCountEventsId = -1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COUNT_OF_EVENT_ID, plugin );
        daoUtil.setInt( 1, nIdCategory );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nCountEventsId = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nCountEventsId;
    }

    /**
     * Return the image resource corresponding to the category id
     * @param nCategoryId The identifier of Category object
     * @param plugin Plugin
     * @return The image resource
     */
    public ImageResource loadImageResource( int nCategoryId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_RESOURCE_IMAGE, plugin );
        daoUtil.setInt( 1, nCategoryId );
        daoUtil.executeQuery(  );

        ImageResource image = null;

        if ( daoUtil.next(  ) )
        {
            image = new ImageResource(  );
            image.setImage( daoUtil.getBytes( 1 ) );
            image.setMimeType( daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return image;
    }
}
