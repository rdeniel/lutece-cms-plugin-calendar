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
package fr.paris.lutece.plugins.calendar.service;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.service.resource.Resource;
import fr.paris.lutece.portal.service.resource.ResourceLoader;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.filesystem.DirectoryNotFoundException;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;


/**
 *  This loader fetches agendas stored in a property file
 */
public class AgendaLoaderProperties implements ResourceLoader
{
    private static final String PROPERTY_CALENDAR_FILES_PATH = "calendar.files.path";
    private static final String EXT_CALENDAR_FILES = ".properties";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_EVENT_IMAGE = "event.image";
    private static final String PROPERTY_EVENT_PREFIX = "event.prefix";
    private static final String PROPERTY_LOADER_CLASS = "loader.class";
    private static final String PROPERTY_LOADER_PARAMETER = "loader.parameter";
    private String _strFilesPath;

    /**
     * A loader importing agenda in properties files
     */
    public AgendaLoaderProperties(  )
    {
        super(  );
        _strFilesPath = AppPropertiesService.getProperty( PROPERTY_CALENDAR_FILES_PATH );
    }

    /**
     * Get the agenda represented by property files
     * @return Return the agenda resources
     */
    public Collection<AgendaResource> getResources(  )
    {
        String strRootDirectory = AppPathService.getWebAppPath(  );
        List<AgendaResource> listPages = new ArrayList<AgendaResource>(  );

        try
        {
            List<File> listFiles = FileSystemUtil.getFiles( strRootDirectory, _strFilesPath );

            for ( File file : listFiles )
            {
                String fileName = file.getName(  );

                if ( fileName.endsWith( EXT_CALENDAR_FILES ) )
                {
                    String strId = fileName.substring( 0, fileName.lastIndexOf( "." ) );
                    AgendaResource agenda = loadAgenda( file, strId );
                    listPages.add( agenda );
                }
            }
        }
        catch ( DirectoryNotFoundException e )
        {
            throw new AppException( e.getMessage(  ), e );
        }

        return listPages;
    }

    /**
     * Get a resource by its Id
     * @param strId The resource Id
     * @return The resource
     */
    public Resource getResource( String strId )
    {
        Resource resource = null;
        String strFilePath = AppPathService.getPath( PROPERTY_CALENDAR_FILES_PATH, strId + EXT_CALENDAR_FILES );
        File file = new File( strFilePath );

        if ( file.exists(  ) )
        {
            resource = loadAgenda( file, strId );
        }

        return resource;
    }

    /**
     * Return the page
     * @return The agenda
     * @param strId The id
     * @param file The File to load
     */
    private AgendaResource loadAgenda( File file, String strId )
    {
        AgendaResource agenda = new AgendaResource(  );
        Properties properties = new Properties(  );

        try
        {
            FileInputStream is = new FileInputStream( file );
            properties.load( is );

            agenda.setId( strId );
            agenda.setName( properties.getProperty( PROPERTY_NAME ) );
            agenda.setEventImage( properties.getProperty( PROPERTY_EVENT_IMAGE ) );
            agenda.setEventPrefix( properties.getProperty( PROPERTY_EVENT_PREFIX ) );
            agenda.setLoaderClassName( properties.getProperty( PROPERTY_LOADER_CLASS ) );
            agenda.setLoaderParameter( properties.getProperty( PROPERTY_LOADER_PARAMETER ) );

            try
            {
                AgendaLoader loader = (AgendaLoader) Class.forName( agenda.getLoaderClassName(  ) ).newInstance(  );
                Agenda a = loader.load( agenda.getLoaderParameter(  ) );

                if ( a != null )
                {
                    a.setName( agenda.getName(  ) );
                    a.setKeyName( agenda.getId(  ) );
                    agenda.setAgenda( a );
                    agenda.setResourceType( AppPropertiesService.getProperty( Constants.PROPERTY_READ_ONLY ) );
                }
            }
            catch ( ClassNotFoundException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( IllegalAccessException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( InstantiationException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return agenda;
    }
}
