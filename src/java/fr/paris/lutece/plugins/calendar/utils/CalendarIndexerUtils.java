package fr.paris.lutece.plugins.calendar.utils;

import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.event.ResourceEventManager;


/**
 * 
 * CalendarIndexerUtils
 * 
 */
public class CalendarIndexerUtils
{
    // Indexed resource type name
    public static final String CONSTANT_TYPE_RESOURCE = "CALENDAR_EVENT";

    /**
     * Warn that a action has been done.
     * @param nIdResource the document id
     * @param nIdTask the key of the action to do
     */
    public static void addIndexerAction( int nIdResource, int nIdTask )
    {
        ResourceEvent event = new ResourceEvent( );
        event.setIdResource( String.valueOf( nIdResource ) );
        event.setTypeResource( CONSTANT_TYPE_RESOURCE );
        switch ( nIdTask )
        {
        case IndexerAction.TASK_CREATE:
            ResourceEventManager.fireAddedResource( event );
            break;
        case IndexerAction.TASK_MODIFY:
            ResourceEventManager.fireUpdatedResource( event );
            break;
        case IndexerAction.TASK_DELETE:
            ResourceEventManager.fireDeletedResource( event );
            break;
        default:
            break;
        }
    }
}
