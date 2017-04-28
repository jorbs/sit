<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<form action="<c:url value="uploadReceiptFiles" />" method="post" enctype="multipart/form-data">
	<p>Upload de notas de corretagem</p>
	<input type="file" name="receiptFile" multiple="multiple" />
	<input type="submit" value="Submit" />
</form>

<a href="<c:url value="/receipt/list" />">Notas de Corretagem</a>

<h1>Posições</h1>

<table border="1">
	<tr>
		<th>Ativo</th>
		<th>Quantidade Compra</th>
		<th>Quantidade Venda</th>
		<th>Preço Compra</th>
		<th>Preço Venda</th>
		<th>Situação</th>
		<th>Lucro/Prejuí­zo</th>
	</tr>
	<c:forEach var="position" items="${positions}">
		<c:set var="invertedPosition" value="${position.invertedPosition}"/>
		<tr>
			<td><a href="<c:url value="/index?stock=${position.stock}"/>">${position.stock}</a></td>
			<td>${position.quantity}</td>
			<td>${invertedPosition.quantity}</td>
			<td><fmt:formatNumber value="${position.averagePrice}" type="currency" currencySymbol="R$ "/></td>
			<td><fmt:formatNumber value="${invertedPosition.averagePrice}" type="currency" currencySymbol="R$ "/></td>
			<td>${position.quantity eq invertedPosition.quantity ? 'Encerrada' : 'Aberta'}</td>
			<td><fmt:formatNumber value="${position.balance}" type="currency" currencySymbol="R$ "/></td>
		</tr>
	</c:forEach>
</table>