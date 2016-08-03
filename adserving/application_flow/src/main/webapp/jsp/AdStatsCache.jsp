<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<body>
    <c:set var="workflow" value="${applicationScope['workflow']}" />
    <c:set var="adStatsCache" value="${workflow.getCachePool().getCache('ad_stats_cache')}" />
    <p><c:out value="${adStatsCache.toString()}" /></p>
</body>
