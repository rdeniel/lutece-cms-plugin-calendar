package fr.paris.lutece.plugins.calendar.business.rss;

import fr.paris.lutece.plugins.calendar.business.CalendarFilter;
import fr.paris.lutece.plugins.calendar.business.CalendarHome;
import fr.paris.lutece.plugins.calendar.business.Event;
import fr.paris.lutece.plugins.calendar.service.AgendaResource;
import fr.paris.lutece.plugins.calendar.service.CalendarPlugin;
import fr.paris.lutece.plugins.calendar.web.Constants;
import fr.paris.lutece.portal.business.rss.FeedResource;
import fr.paris.lutece.portal.business.rss.FeedResourceItem;
import fr.paris.lutece.portal.business.rss.IFeedResource;
import fr.paris.lutece.portal.business.rss.IFeedResourceItem;
import fr.paris.lutece.portal.business.rss.ResourceRss;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * CalendarResourceRss
 */
public class CalendarResourceRss extends ResourceRss
{
    private static final String EMPTY_STRING = "";
    private static final String AGENDA_TITLE = "Calendar RSS";
    private static final String AGENDA_DESCRIPTION = "Calendar RSS";
    private static final String AGENDA_LINK = "portal.jsp";

    /**
     * verify that the resource exist
     * @return true if resource exist
     */
    public boolean checkResource( )
    {
        Plugin pluginCalendar = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );
        AgendaResource agenda = CalendarHome.findAgendaResource( this.getId( ), pluginCalendar );

        return ( agenda != null );
    }

    /**
     * verified that the resource contains the resource to be exploited
     * @return true if resourceRss content resource
     */
    public boolean contentResourceRss( )
    {
        Plugin pluginCalendar = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );

        return !CalendarHome.findAgendaResourcesList( pluginCalendar ).isEmpty( );
    }

    /**
     * {@inheritDoc}
     */
    public void deleteResourceRssConfig( int idResourceRss )
    {
    }

    /**
     * {@inheritDoc}
     */
    public void doSaveConfig( HttpServletRequest request, Locale locale )
    {
    }

    /**
     * {@inheritDoc}
     */
    public void doUpdateConfig( HttpServletRequest request, Locale locale )
    {
    }

    /**
     * {@inheritDoc}
     */
    public String doValidateConfigForm( HttpServletRequest request, Locale locale )
    {
        this.setDescription( AGENDA_DESCRIPTION );
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayCreateConfigForm( HttpServletRequest request, Locale locale )
    {
        return EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     */
    public String getDisplayModifyConfigForm( HttpServletRequest request, Locale locale )
    {
        return EMPTY_STRING;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getParameterToApply( HttpServletRequest request )
    {
        return null;
    }

    /**
     * Gets the feed with items
     * @return the {@link IFeedResource}
     */
    public IFeedResource getFeed( )
    {
        Plugin pluginCalendar = PluginService.getPlugin( CalendarPlugin.PLUGIN_NAME );

        //Agenda Rss
        IFeedResource resource = new FeedResource( );
        resource.setTitle( AGENDA_TITLE );
        resource.setDescription( AGENDA_DESCRIPTION );
        resource.setLink( AGENDA_LINK );

        CalendarFilter filter = new CalendarFilter( );
        List<Event> listEvent = CalendarHome.findEventsByFilter( filter, pluginCalendar );
        List<IFeedResourceItem> listItems = new ArrayList<IFeedResourceItem>( );

        //Description of  the events
        for ( Event simpleEvent : listEvent )
        {
            IFeedResourceItem item = new FeedResourceItem( );

            item.setTitle( ( simpleEvent.getTitle( ) != null ) ? simpleEvent.getTitle( ) : "" );

            item.setDescription( ( simpleEvent.getDescription( ) != null ) ? simpleEvent.getDescription( ) : "" );

            Date dateCreation = new Date( simpleEvent.getDateCreation( ).getTime( ) );
            item.setDate( dateCreation );

            UrlItem urlEvent = new UrlItem( AppPathService.getPortalUrl( ) );
            urlEvent.addParameter( XPageAppService.PARAM_XPAGE_APP, CalendarPlugin.PLUGIN_NAME );
            urlEvent.addParameter( Constants.PARAMETER_ACTION, Constants.ACTION_SHOW_RESULT );
            urlEvent.addParameter( Constants.PARAMETER_EVENT_ID, simpleEvent.getId( ) );
            urlEvent.addParameter( Constants.PARAM_AGENDA, simpleEvent.getIdCalendar( ) );
            item.setLink( urlEvent.getUrl( ) );

            listItems.add( item );
        }

        resource.setItems( listItems );

        return resource;

    }
}
