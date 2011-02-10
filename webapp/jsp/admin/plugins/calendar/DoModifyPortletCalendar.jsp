<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="portletCalendar" scope="session" class="fr.paris.lutece.plugins.calendar.web.portlet.CalendarPortletJspBean" />

<% portletCalendar.init( request, fr.paris.lutece.plugins.calendar.web.portlet.CalendarPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ); %>
<%
	response.sendRedirect( portletCalendar.doModify( request ) );
%>
