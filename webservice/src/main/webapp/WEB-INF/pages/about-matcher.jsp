<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<body>
	<p>About the ${configName} configuration (contains ${total} records):</p>
		<c:forEach var="property" items="${properties}">
			<p>
				${property}<br />
			Transformers:
			<ul>
				<c:forEach var="entry" items="${transformers}">
					<c:if test="${entry.key == property}">
						<c:forEach var="t" items="${entry.value}">
							<li><c:out value="${t}" /></li>
						</c:forEach>
					</c:if>
				</c:forEach>
			</ul>
			<c:forEach var="entry" items="${matchers}">
				<c:if test="${entry.key == property}">
					Matcher: <c:out value="${entry.value}" />
				</c:if>
			</c:forEach>
			</p>
			<hr/>
		</c:forEach>
</body>
</html>