<%@ page errorPage="../../ErrorPage.jsp"%>

<jsp:useBean id="category" scope="session" class="fr.paris.lutece.plugins.calendar.web.CalendarCategoryJspBean" />

<%
	category.init( request, fr.paris.lutece.plugins.calendar.web.CalendarCategoryJspBean.RIGHT_CATEGORY_MANAGEMENT);
	response.sendRedirect(category.doRemoveCategory(request));
%>



