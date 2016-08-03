<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<body>
    <c:set var="workflow" value="${applicationScope['workflow']}" />
    <c:set var="cache" value="${workflow.getCachePool().getCache(param.cache)}" />
    <c:set var="stats" value="${cache.getStats()}" />
    <c:out value="${stats}" escapeXml="false" />
</body>
