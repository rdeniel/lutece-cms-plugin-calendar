<jsp:include page="../../insert/InsertServiceHeader.jsp" />
<jsp:useBean id="calendarServiceJspBean" scope="session" class="fr.paris.lutece.plugins.calendar.web.CalendarServiceJspBean" />
<% response.sendRedirect( calendarServiceJspBean.doSelectCalendar( request ) );%>
