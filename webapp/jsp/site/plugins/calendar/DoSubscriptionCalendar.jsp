<%@ page errorPage="../../ErrorPage.jsp" %>
<%@page import="fr.paris.lutece.portal.service.message.SiteMessageException"%>
<%@page import="fr.paris.lutece.portal.service.util.AppPathService"%>
<jsp:include page="../../PortalHeader.jsp" />

<jsp:useBean id="agendaApp" scope="request" class="fr.paris.lutece.plugins.calendar.web.CalendarApp" />

<%
	/* This method is used to catch the front messages */
    try
	{
    	agendaApp.doSubscription( request );
	}
    catch( SiteMessageException lme )
	{
		response.sendRedirect( AppPathService.getBaseUrl( request ) );
	}
%>
