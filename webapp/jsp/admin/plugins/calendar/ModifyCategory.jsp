<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="calendarCategory" scope="session" class="fr.paris.lutece.plugins.calendar.web.CalendarCategoryJspBean" />

<% calendarCategory.init( request, fr.paris.lutece.plugins.calendar.web.CalendarCategoryJspBean.RIGHT_CATEGORY_MANAGEMENT ); %>
<%= calendarCategory.getModifyCategory( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
