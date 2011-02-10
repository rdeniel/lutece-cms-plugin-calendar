<%@ page errorPage="../../ErrorPagePortal.jsp" %>
<%@page import="fr.paris.lutece.portal.service.message.SiteMessageException"%>
<%@page import="fr.paris.lutece.portal.service.util.AppPathService"%>
<jsp:include page="../../PortalHeader.jsp" />



<jsp:useBean id="agendaApp" scope="request" class="fr.paris.lutece.plugins.calendar.web.CalendarApp" />

<%
	String result;
    try
	{
    	result = agendaApp.doSendToFriend( request );
    	response.sendRedirect( result );
	}
    catch( SiteMessageException lme )
	{
		response.sendRedirect( AppPathService.getBaseUrl( request ) );
	}
    
%>