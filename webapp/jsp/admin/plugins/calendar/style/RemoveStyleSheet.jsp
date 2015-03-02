<%@ page errorPage="../../../ErrorPage.jsp" %>

<jsp:useBean id="calendarStylesheet" scope="session" class="fr.paris.lutece.plugins.calendar.web.CalendarStyleSheetJspBean" />

<% 
    calendarStylesheet.init( request,  fr.paris.lutece.plugins.calendar.web.CalendarStyleSheetJspBean.RIGHT_MANAGE_STYLESHEET  ) ; 
    response.sendRedirect( calendarStylesheet.getRemoveStyleSheet( request ));
%>
