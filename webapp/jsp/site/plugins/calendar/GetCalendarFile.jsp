<%@ page errorPage="../../ErrorPagePortal.jsp" %><%@ page import="fr.paris.lutece.plugins.calendar.web.CalendarDownloadFile" %>
<%@page import="fr.paris.lutece.portal.service.message.SiteMessageException"%>
<%@page import="fr.paris.lutece.portal.service.util.AppPathService"%>
<jsp:include page="../../PortalHeader.jsp" />
<%
CalendarDownloadFile file = new CalendarDownloadFile( );
try
{
file.downloadCalendarToFile( request, response );
    
}
catch( SiteMessageException lme )
{
	response.sendRedirect( AppPathService.getBaseUrl( request ) );
}
%>
