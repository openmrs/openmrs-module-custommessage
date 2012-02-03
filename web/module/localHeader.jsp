<%@ include file="/WEB-INF/view/module/reporting/include.jsp"%>
<openmrs:extensionPoint pointId="org.openmrs.admin.list" type="html">
	<openmrs:hasPrivilege privilege="${extension.requiredPrivilege}">
		<c:if test="${extension.class.name == 'org.openmrs.module.custommessage.extension.AdministrationPageExtension'}">
			<ul id="menu">
				<c:forEach items="${extension.links}" var="link" varStatus="status">
					<c:set var="linkSelected" value="${fn:contains(pageContext.request.requestURI, fn:substringBefore(link.key, '.'))}"/>
					<li class="<c:if test="${status.index == 0}">first</c:if> <c:if test="${linkSelected}">active</c:if>">
						<c:choose>
							<c:when test="${fn:startsWith(link.key, 'module/')}">
								<%-- Added for backwards compatibility for most links --%>
								<a href="${pageContext.request.contextPath}/${link.key}"><spring:message code="${link.value}"/></a>
							</c:when>
							<c:otherwise>
								<%-- Allows for external absolute links  --%>
								<a href='<c:url value="${link.key}"/>'><spring:message code='${link.value}'/></a>
							</c:otherwise>
						</c:choose>
					</li>
				</c:forEach>
			</ul>
		</c:if>
	</openmrs:hasPrivilege>
</openmrs:extensionPoint>