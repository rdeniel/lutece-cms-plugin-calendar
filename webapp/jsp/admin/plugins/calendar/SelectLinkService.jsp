<%@page import="fr.paris.lutece.portal.service.insert.InsertService"%>
<jsp:include page="../../insert/InsertServiceHeader.jsp" />

<jsp:useBean id="calendarInsertLinkService" scope="session" class="fr.paris.lutece.portal.service.insert.InsertService" />

<%= calendarInsertLinkService.getActionBeanString(  ) %>