<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <spring:message code="login" var="login"/>
    <jsp:include page="/WEB-INF/jsp/components/head.jsp">
        <jsp:param name="title" value="${login}"/>
    </jsp:include>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navbar.jsp"/>
<main class="login-register-main">
    <div class="card mb-3 login-register-card" style="width: 25rem;">
        <div class="card-body">
            <h2 class="card-title mb-3">${login}</h2>
            <c:if test="${param.error != null}">
                <div class="alert alert-danger" role="alert">
                    <spring:message code="login.invalidcredentials"/>
                </div>
            </c:if>
            <form action="<c:url value="/auth/login"/>" method="post">
                <div class="mb-3">
                    <label for="login-email" class="form-label"><spring:message code="login.email"/></label>
                    <input type="email" class="form-control" name="email" id="login-email">
                </div>
                <div class="mb-3">
                    <label for="login-password" class="form-label"><spring:message code="login.password"/></label>
                    <input type="password" class="form-control" name="password" id="login-password">
                </div>
                <div class="mb-3 form-check">
                    <input type="checkbox" class="form-check-input" id="exampleCheck1">
                    <label class="form-check-label" for="exampleCheck1"><spring:message code="login.rememberme"/></label>
                </div>
                <input type="submit" class="btn btn-primary" id="login-submit" value='<spring:message code="login"/>'>
            </form>
            <p class="mt-3"><spring:message code="login.notregistered"/> <a href="<c:url value="/auth/register"/>"><spring:message code="login.signup"/></a></p>
        </div>
    </div>
</main>
</body>
</html>
