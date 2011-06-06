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
package fr.paris.lutece.plugins.calendar.modules.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import fr.paris.lutece.plugins.calendar.business.Agenda;
import fr.paris.lutece.plugins.calendar.business.SimpleAgenda;
import fr.paris.lutece.plugins.calendar.business.SimpleEvent;
import fr.paris.lutece.plugins.calendar.service.AgendaLoader;
import fr.paris.lutece.plugins.calendar.service.Utils;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;


/**
 * A basic implementation of an Agenda Loader.
 * This loader constructs agenda by reading events in a text file.
 * Each line of the text file should contains an event defined as follow :
 * [date formatted as YYYYMMDD] [description]
 */
public class TextAgendaLoader implements AgendaLoader
{
    private static final int MINIMUM_LINE_LENGTH = 10;

    /** Creates a new instance of TextAgendaLoader */
    public TextAgendaLoader(  )
    {
    }

    /**
     * Load an agenda using its name
     * @return An agenda object
     * @param strParameter The parameter of the agenada
     */
    public Agenda load( String strParameter )
    {
        SimpleAgenda agenda = new SimpleAgenda(  );
        String strFile = AppPathService.getWebAppPath(  ) + strParameter;
        File file = new File( strFile );
        parseFileContent( file, agenda );

        return agenda;
    }

    /**
     * Parse a text file to extracts events.
     * @param agenda The agenda
     * @param file The agenda text file.
     */
    private void parseFileContent( File file, SimpleAgenda agenda )
    {
        BufferedReader input = null;

        try
        {
            //use buffering
            //this implementation reads one line at a time
            //FileReader always assumes default encoding is OK!
            input = new BufferedReader( new FileReader( file ) );

            String strLine = null;

            while ( ( strLine = input.readLine(  ) ) != null )
            {
                if ( strLine.length(  ) > MINIMUM_LINE_LENGTH )
                {
                    String strDate = getDate( strLine );

                    if ( Utils.isValid( strDate ) )
                    {
                        String strEvent = getEvent( strLine );
                        SimpleEvent event = new SimpleEvent(  );
                        event.setDate( Utils.getDate( strDate ) );
                        event.setTitle( strEvent );
                        agenda.addEvent( event );
                    }
                }
            }
        }
        catch ( FileNotFoundException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        finally
        {
            try
            {
                if ( input != null )
                {
                    //flush and close both "input" and its underlying FileReader
                    input.close(  );
                }
            }
            catch ( IOException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
        }
    }

    /**
     * Extracts the date from the line
     * @param strLine A line of the text file
     * @return the date code
     */
    private String getDate( String strLine )
    {
        return strLine.substring( 0, 8 );
    }

    /**
     * Extracts the event description from the line
     * @param strLine A line of the text file
     * @return the event description
     */
    private String getEvent( String strLine )
    {
        return strLine.substring( 9 );
    }
}
