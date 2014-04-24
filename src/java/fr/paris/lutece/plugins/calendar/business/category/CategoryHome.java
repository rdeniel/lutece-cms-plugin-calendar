/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;


/**
 * This class provides instances management methods (create, find, ...) for
 * Category objects
 */
public final class CategoryHome
{
    // Static variable pointed at the DAO instance
    private static ICategoryDAO _dao = (ICategoryDAO) SpringContextService.getBean( "calendar.categoryDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private CategoryHome( )
    {
    }

    /**
     * Returns the category list
     * @param plugin Plugin
     * @return Collection of Category (empty collection is no result)
     */
    public static Collection<Category> findAll( Plugin plugin )
    {
        Collection<Category> categoryList = _dao.selectAll( plugin );

        return categoryList;
    }

    /**
     * Create a new Category
     * @param category The new Category
     * @param plugin Plugin
     */
    public static void create( Category category, Plugin plugin )
    {
        _dao.insert( category, plugin );
    }

    /**
     * Find the data of Category from the table
     * @param nIdCategory The id of the category
     * @param plugin Plugin
     * @return The Instance of the object Category of null if no category match
     */
    public static Category find( int nIdCategory, Plugin plugin )
    {
        return _dao.load( nIdCategory, plugin );
    }

    /**
     * Find the data of Category from the table
     * @param strCategoryName The id of the category
     * @param plugin Plugin
     * @return The Collection of Category (empty collection is no result)
     */
    public static Collection<Category> findByName( String strCategoryName, Plugin plugin )
    {
        return _dao.selectByName( strCategoryName, plugin );
    }

    /**
     * Remove a record from the table
     * @param nIdCategory The identifier of the object Category
     * @param plugin Plugin
     */
    public static void remove( int nIdCategory, Plugin plugin )
    {
        _dao.delete( nIdCategory, plugin );
    }

    /**
     * Update the record in the table
     * @param category The instance of the Category to update
     * @param plugin Plugin
     */
    public static void update( Category category, Plugin plugin )
    {
        _dao.store( category, plugin );
    }

    /**
     * Find the number of documents linked to a category
     * @param nIdCategory The category id
     * @param plugin Plugin
     * @return count of id document
     */
    public static int findCountIdEvents( int nIdCategory, Plugin plugin )
    {
        return _dao.selectCountIdEvents( nIdCategory, plugin );
    }

    /**
     * Return the image resource for the specified category id
     * @param nCategoryId The identifier of Category object
     * @param plugin Plugin
     * @return ImageResource
     */
    public static ImageResource getImageResource( int nCategoryId, Plugin plugin )
    {
        return _dao.loadImageResource( nCategoryId, plugin );
    }

    /**
     * Load the data of Category for an event from the table
     * @param nIdEvent The identifier of the event
     * @param plugin Plugin
     * @return The Instance of the object Category
     */
    public static Collection<Category> findByEvent( int nIdEvent, Plugin plugin )
    {
        return _dao.selectByEvent( nIdEvent, plugin );
    }
}
