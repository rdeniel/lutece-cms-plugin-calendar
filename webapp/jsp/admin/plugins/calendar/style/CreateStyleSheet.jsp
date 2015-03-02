<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<jsp:useBean id="calendarStylesheet" scope="session" class="fr.paris.lutece.plugins.calendar.web.CalendarStyleSheetJspBean" />

<% calendarStylesheet.init( request, fr.paris.lutece.plugins.calendar.web.CalendarStyleSheetJspBean.RIGHT_MANAGE_STYLESHEET  ) ; %>
<%= calendarStylesheet.getCreateStyleSheet ( request )%>

<%@ include file="../../../AdminFooter.jsp" %>