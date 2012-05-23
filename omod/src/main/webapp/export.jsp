<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Manage Custom Messages" otherwise="/login.htm" redirect="/module/custommessage/export.form" />
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<form method="post">
	<spring:message code="custommessage.chooseLocaleToExport"/>:<br/><br/>
	<select name="locale">
		<c:forEach items="${supportedLocales}" var="localeEntry">
			<option value="${localeEntry.value}">${localeEntry.key}</option>
		</c:forEach>
	</select>
	<br/><br/>
	<input type="submit" value="<spring:message code="custommessage.export"/>"/>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>