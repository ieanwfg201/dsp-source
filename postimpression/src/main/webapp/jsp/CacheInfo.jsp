<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<body>
    <c:set var="workflow" value="${applicationScope['workflow']}" />
    <c:set var="entityCache" value="${workflow.getCachePool().getCache(param.cache)}" />
    <c:choose>
        <c:when test="${empty param.id}">
            <c:set var="entityList" value="${entityCache.getAllEntities()}" />
            <c:forEach var="entity" items="${entityList}">
                <p><c:out value="${entity}" /></p>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <c:set var="entity" value="${entityCache.query(param.id)}" />
            <p><c:out value="${entity}" /></p>
        </c:otherwise>
    </c:choose>
</body>
