package fr.paris.lutece.plugins.calendar.service;

import java.util.Comparator;
import java.util.Date;

import fr.paris.lutece.plugins.calendar.business.Event;

public class EventComparator implements Comparator<Event>{

	public int compare(Event ev1, Event ev2) 
	{
		Date dateEv1 = ev1.getDate(  );
		Date dateEv2 = ev2.getDate(  );
		if( dateEv1.before( dateEv2 ) )
		{
			return -1;
		}
		else if ( dateEv1.after( dateEv2 ) )
		{
			return 1;
		}
		
		return 0;
	}

}
