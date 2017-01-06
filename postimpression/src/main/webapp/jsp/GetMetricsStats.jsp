<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
    <c:set var="workflow" value="${applicationScope['workflow']}" />
    <c:set var="metricsCache" value="${workflow.getCachePool().getCache('metrics_cache')}" />
    <c:out value="${metricsCache.toString()}" escapeXml="false" />
