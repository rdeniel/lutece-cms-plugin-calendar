<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="portletCalendar" scope="page" class="fr.paris.lutece.plugins.calendar.web.portlet.MiniCalendarPortletJspBean" />

<% portletCalendar.init( request, fr.paris.lutece.plugins.calendar.web.portlet.MiniCalendarPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ); %>
<%
	response.sendRedirect( portletCalendar.doCreate( request ) );
%>


