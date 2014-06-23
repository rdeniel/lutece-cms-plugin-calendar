<%@ page errorPage="../../ErrorPage.jsp"%>

<jsp:useBean id="calendarCategory" scope="session" class="fr.paris.lutece.plugins.calendar.web.CalendarCategoryJspBean" />

<%
calendarCategory.init( request, fr.paris.lutece.plugins.calendar.web.CalendarCategoryJspBean.RIGHT_CATEGORY_MANAGEMENT);
	response.sendRedirect(calendarCategory.doRemoveCategory(request));
%>



