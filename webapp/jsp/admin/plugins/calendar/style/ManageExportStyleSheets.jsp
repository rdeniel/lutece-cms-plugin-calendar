<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<jsp:useBean id="calendarStylesheet" scope="page" class="fr.paris.lutece.plugins.calendar.web.CalendarStyleSheetJspBean" />

<% calendarStylesheet.init( request, fr.paris.lutece.plugins.calendar.web.CalendarStyleSheetJspBean.RIGHT_MANAGE_STYLESHEET ) ; %>
<%= calendarStylesheet.getManageStyleSheet ( request )%>

<%@ include file="../../../AdminFooter.jsp" %>