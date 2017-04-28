<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<form action="<c:url value="uploadReceiptFiles" />" method="post" enctype="multipart/form-data">
	<input type="file" name="receiptFile" multiple="multiple" />
	<input type="submit" value="Submit" />
</form>

<table>
	<thead>
		<tr>
			<th>Data</th>
			<th>Número</th>
			<th>Corretora</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${receipts}" var="receipt">
			<tr>
				<td><fmt:formatDate value="${receipt.issued_at}" pattern="dd/MM/yyyy" /></td>
				<td>${receipt.number}</td>
				<td>${receipt.broker.name}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>