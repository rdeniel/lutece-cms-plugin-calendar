<%@ page errorPage="../../../ErrorPage.jsp" %>
<jsp:include page="../../../AdminHeader.jsp" />

<jsp:useBean id="stylesheet" scope="page" class="fr.paris.lutece.plugins.calendar.web.CalendarStyleSheetJspBean" />

<% stylesheet.init( request, fr.paris.lutece.plugins.calendar.web.CalendarStyleSheetJspBean.RIGHT_MANAGE_STYLESHEET ) ; %>
<%= stylesheet.getManageStyleSheet ( request )%>

<%@ include file="../../../AdminFooter.jsp" %>