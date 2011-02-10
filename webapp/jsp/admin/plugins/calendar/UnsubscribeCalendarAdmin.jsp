<%@ page errorPage="../../ErrorPage.jsp" %>


<jsp:useBean id="calendar" scope="request" class="fr.paris.lutece.plugins.calendar.web.CalendarJspBean" />

<% calendar.init( request, fr.paris.lutece.plugins.calendar.web.CalendarJspBean.RIGHT_MANAGE_CALENDAR ); %>
<% response.sendRedirect( calendar.getConfirmRemoveSubscriber( request ) ); %>
