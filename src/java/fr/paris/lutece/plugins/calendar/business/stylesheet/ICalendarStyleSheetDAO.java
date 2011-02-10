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
package fr.paris.lutece.plugins.calendar.business.stylesheet;

import java.util.Collection;

import fr.paris.lutece.portal.business.stylesheet.StyleSheet;
import fr.paris.lutece.portal.service.plugin.Plugin;


/**
 *
 * @author LEVY
 */
public interface ICalendarStyleSheetDAO
{
    /**
     * Delete the StyleSheet from the database whose identifier is specified
     * in parameter
     * @param nIdStylesheet the identifier of the StyleSheet to delete
     * @param plugin Plugin
     */
    void delete( int nIdStylesheet, Plugin plugin );

    /**
     * Insert a new record in the table.
     * @param stylesheet The StyleSheet object
     * @param strExtension the export file extension
     * @param plugin Plugin
     */
    void insert( StyleSheet stylesheet, String strExtension, Plugin plugin );

    /**
     * Load the data of Stylesheet from the table
     * @param nIdStylesheet the identifier of the Stylesheet to load
     * @param plugin Plugin
     * @return stylesheet
     */
    StyleSheet load( int nIdStylesheet, Plugin plugin );

    /**
     * Load the list of stylesheet
     * @param nModeId The Mode identifier
     * @param plugin Plugin
     * @return the list of the StyleSheet in form of a collection of StyleSheet objects
     */
    Collection<StyleSheet> selectStyleSheetList( Plugin plugin );

    /**
     * Update the record in the table
     * @param stylesheet The stylesheet to store
     * @param plugin Plugin
     */
    void store( StyleSheet stylesheet, Plugin plugin );

    /**
     * Insertion of the extension of an export file in the database
     * @param strExtension the extension file
     * @param plugin Plugin
     */
    void insertExtension( int nId, String strExtension, Plugin plugin );

    /**
     * Select the extension file related to the export stylesheet
     * @param nId the identifier of the stylesheet
     * @param plugin Plugin
     * @return the extension file
     */
    String selectExtension( int nId, Plugin plugin );

    /**
     * Update the the extension file whose identifier is specified in parameter
     * @param nId the stylesheet id
     * @param strExtension the new extension file
     * @param plugin Plugin
     */
    void updateExtension( int nId, String strExtension, Plugin plugin );
}
