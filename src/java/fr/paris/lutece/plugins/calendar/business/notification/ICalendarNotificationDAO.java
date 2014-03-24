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
package fr.paris.lutece.plugins.calendar.business.notification;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;


/**
 * 
 * IResourceKeyDAO
 * 
 */
public interface ICalendarNotificationDAO
{
    /**
     * Insert object CalendarNotification
     * @param calendarNotification The calendar notification to insert
     * @param plugin the plugin
     */
    void insert( CalendarNotification calendarNotification, Plugin plugin );

    /**
     * Update object CalendarNotification
     * @param calendarNotification the calendar notification
     * @param plugin the plugin
     */
    void store( CalendarNotification calendarNotification, Plugin plugin );

    /**
     * Load object CalendarNotification
     * @param strKey the key
     * @param plugin the plugin
     * @return ResourceKey
     * 
     */
    CalendarNotification load( String strKey, Plugin plugin );

    /**
     * Delete object CalendarNotification
     * @param strKey the key
     * @param plugin The plugin
     */
    void delete( String strKey, Plugin plugin );

    /**
     * Select notification expiry
     * @param plugin The plugin
     * @return The list of calendar notification
     */
    List<CalendarNotification> selectNotificationExpiry( Plugin plugin );
}
