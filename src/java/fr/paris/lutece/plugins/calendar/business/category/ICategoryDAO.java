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
package fr.paris.lutece.plugins.calendar.business.category;

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Collection;


/**
 * Interface for Category DAO
 */
public interface ICategoryDAO
{
    /**
     * Load the list of category
     * @param plugin Plugin
     * @return The Collection of category
     */
    Collection<Category> selectAll( Plugin plugin );

    /**
     * insert a new Category
     * @param plugin Plugin
     * @param category the new Category object
     */
    void insert( Category category, Plugin plugin );

    /**
     * Load the data of Category from the table
     * @param nIdCategory The id of the category
     * @param plugin Plugin
     * @return The Instance of the object Category
     */
    Category load( int nIdCategory, Plugin plugin );

    /**
     * Load the data of Category from the table
     * @param strCategoryName The name of the category
     * @param plugin Plugin
     * @return The Collection of Category
     */
    Collection<Category> selectByName( String strCategoryName, Plugin plugin );

    /**
     * Delete a record from the table
     * @param nIdCategory The identifier of the object Category
     * @param plugin Plugin
     */
    void delete( int nIdCategory, Plugin plugin );

    /**
     * Update the record in the table
     * @param category The instance of the Category to update
     * @param plugin Plugin
     */
    void store( Category category, Plugin plugin );

    /**
     * Return the number of Events linked to a category
     * @param nIdCategory The category id
     * @param plugin Plugin
     * @return count of id Event
     */
    int selectCountIdEvents( int nIdCategory, Plugin plugin );

    /**
     * Return the image resource corresponding to the category id
     * @param nCategoryId The Category id
     * @param plugin Plugin
     * @return The image resource
     */
    ImageResource loadImageResource( int nCategoryId, Plugin plugin );

    /**
     * Load the data of Category for an event from the table
     * @param nIdEvent The identifier of the event
     * @param plugin Plugin
     * @return The Instance of the object Category
     */
    public Collection<Category> selectByEvent( int nIdEvent, Plugin plugin );
}
