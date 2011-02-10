<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../PortletAdminHeader.jsp" />

<jsp:useBean id="portletCalendar" scope="page" class="fr.paris.lutece.plugins.calendar.web.portlet.MiniCalendarPortletJspBean" />

<% portletCalendar.init( request, fr.paris.lutece.plugins.calendar.web.portlet.MiniCalendarPortletJspBean.RIGHT_MANAGE_ADMIN_SITE ); %>
<%= portletCalendar.getModify( request ) %>

<%@ include file="../../AdminFooter.jsp" %>