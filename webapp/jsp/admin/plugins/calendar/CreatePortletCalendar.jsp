<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="portletCalendar" scope="session" class="fr.paris.lutece.plugins.calendar.web.portlet.CalendarPortletJspBean" />

<% portletCalendar.init( request, fr.paris.lutece.plugins.calendar.web.portlet.CalendarPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ); %>
<%= portletCalendar.getCreate( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
