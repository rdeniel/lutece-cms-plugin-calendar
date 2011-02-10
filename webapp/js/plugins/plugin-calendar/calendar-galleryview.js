function loadScriptForGalleryView1() 
{
	$.ajaxSetup({async: false});
	$.getScript("js/plugins/plugin-calendar/jquery.easing.1.3.js");
	$.getScript("js/plugins/plugin-calendar/jquery.timers-1.2.js");
	$.getScript("js/plugins/plugin-calendar/jquery-galleryview-1.1/jquery.galleryview-1.1.js");
	$.ajaxSetup({async: true});
}

/**********************************************************************************************/

$(document).ready(function () 
{
	/* http://spaceforaname.com/panels.html */
	if( document.getElementById("top-event") != null )
	{
		loadScriptForGalleryView1();
		$('#top-event').galleryView({
			panel_width: 600,
			panel_height: 285,
			transition_speed: 1500,
			transition_interval: 5000,
			nav_theme: 'dark',
			border: '1px solid #666666',
			pause_on_hover: true
		});
	}


});
