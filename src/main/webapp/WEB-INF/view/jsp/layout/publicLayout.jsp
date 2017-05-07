<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
  <head>
    <title>SIT</title>
    <sitemesh:write property='head'/>
  </head>
  <body>
  	<h1><a href="<c:url value="/" />">SIT</a></h1>
    <sitemesh:write property='body'/>
  </body>
</html>