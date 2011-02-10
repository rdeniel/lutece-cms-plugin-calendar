<%@ page errorPage="../../../ErrorPage.jsp" %>

<jsp:useBean id="stylesheet" scope="session" class="fr.paris.lutece.plugins.calendar.web.CalendarStyleSheetJspBean" />

<%
    stylesheet.init( request, fr.paris.lutece.plugins.calendar.web.CalendarStyleSheetJspBean.RIGHT_MANAGE_STYLESHEET  ) ;
    response.sendRedirect( stylesheet.doModifyStyleSheet( request ) );  
%>

