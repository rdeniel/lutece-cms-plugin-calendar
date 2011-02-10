<%@ page errorPage="../../ErrorPage.jsp" %><%@ page import="fr.paris.lutece.plugins.calendar.web.CalendarDownloadFile" %><%
CalendarDownloadFile file = new CalendarDownloadFile( );
file.downloadCalendarToFile( request, response );%>