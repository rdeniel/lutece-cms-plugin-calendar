<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="calendar" scope="session" class="fr.paris.lutece.plugins.calendar.web.CalendarJspBean" />

<% calendar.init( request, fr.paris.lutece.plugins.calendar.web.CalendarJspBean.RIGHT_MANAGE_CALENDAR ); %>
<%= calendar.getManageSubscribers( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
