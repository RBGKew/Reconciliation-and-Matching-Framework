<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
	<ul>
   <c:forEach var="availableMatcher" items="${availableMatchers}">
      <li><a href="/about/${availableMatcher}">${availableMatcher}</a></li>
   </c:forEach>
   </ul>
</body>
</html>
