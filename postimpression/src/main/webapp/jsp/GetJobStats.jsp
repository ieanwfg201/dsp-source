<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <c:set var="workflow" value="${applicationScope['workflow']}" />
    <c:set var="stats" value="${workflow.getJobStats()}" />
    <c:out value="${stats}" escapeXml="false" />
