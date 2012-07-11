<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Manage Custom Messages" otherwise="/login.htm" redirect="/module/custommessage/index.form" />
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<c:set var="DO_NOT_INCLUDE_JQUERY" value="true"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/custommessage/jquery-ui/css/redmond/jquery-ui-1.7.2.custom.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/custommessage/dataTables/css/page.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/custommessage/dataTables/css/table.css"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/custommessage/dataTables/css/custom.css"/>

<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/custommessage/jquery-1.7.2.min.js"/>
<openmrs:htmlInclude file="${pageContext.request.contextPath}/moduleResources/custommessage/jquery-ui/js/jquery-ui-1.7.2.custom.min.js"/>
<openmrs:htmlInclude file='${pageContext.request.contextPath}/moduleResources/custommessage/dataTables/jquery.dataTables.min.js'/>

<style>
	.clickedRow {background-color:yellow;}
</style>

<script type="text/javascript">
	jQuery(document).ready(function() {
		jQuery('#codeList').dataTable({
		    "bPaginate": true,
		    "iDisplayLength": 20,
		    "bLengthChange": false,
		    "bFilter": true,
		    "bSort": false,
		    "bInfo": true,
		    "bAutoWidth": true,
		    "bSortable": false
		});
		
		jQuery("#editButton").click(function(event) {
			jQuery(".customMessageInput").removeAttr('disabled').css('color', 'black');
			jQuery("#saveButton").show();
			jQuery("#cancelButton").show();
			jQuery("#editButton").hide();
		});
		
		jQuery("#cancelButton").click(function(event) {
			viewTranslation(null, jQuery("#selectedCode").html());
		});
		
		jQuery("#saveButton").click(function(event) {
			var url = '${pageContext.request.contextPath}/module/custommessage/saveMessagesForCode.form';
			url += '?code=' + jQuery("#selectedCode").html();
			<c:forEach items="${supportedLocales}" var="localeEntry">
				url += "&id${localeEntry.value}=";
				url += jQuery("#customId${localeEntry.value}").val();
				url += "&message${localeEntry.value}=";
				url += jQuery("#customMessage${localeEntry.value}").val();
			</c:forEach>
			jQuery.post(url, function(data) {
				viewTranslation(null, jQuery("#selectedCode").html());
			});
		});
	});

	function viewTranslation(element, code) {
		if (element) {
			jQuery(".codeRow").removeClass('clickedRow');
			jQuery(element).addClass('clickedRow');
		}
		jQuery("#noCodeSelectedSection").hide();
		jQuery("#saveButton").hide();
		jQuery("#cancelButton").hide();
		jQuery("#editButton").show();
		jQuery("#codeSelectedSection").show();
		jQuery("#selectedCode").html(code);
		jQuery(".customMessageInput").attr('disabled', 'disabled').css('color', 'blue');
		$.getJSON('${pageContext.request.contextPath}/module/custommessage/getMessagesForCode.form?code=' + code, function(data) {
			<c:forEach items="${supportedLocales}" var="localeEntry">
				var defaultVal = data['defaults']['${localeEntry.value}'];
				jQuery("#defaultMessage${localeEntry.value}").html(defaultVal == null ? '' : defaultVal);
				var customVal = data['customs']['${localeEntry.value}'];
				jQuery("#customId${localeEntry.value}").val(customVal == null ? '' : customVal.id);
				jQuery("#customMessage${localeEntry.value}").val(customVal == null ? '' : customVal.message);
				jQuery("#customMessageDisplay${localeEntry.value}").val(customVal == null ? '' : customVal.message);
			</c:forEach>
		});
	}
</script>

<form method="get" id="searchForm">
	<spring:message code="custommessage.show"/>
	<select name="missingInLocale">
		<option value=""><spring:message code="custommessage.allCodes"/></option>
		<c:forEach items="${supportedLocales}" var="localeEntry">
			<option value="${localeEntry.value}"<c:if test="${localeEntry.value == missingInLocale}"> selected</c:if>>
				<spring:message code="custommessage.missingInLocale"/>: ${localeEntry.key}
			</option>
		</c:forEach>
	</select>
	&nbsp;&nbsp;
	<spring:message code="custommessage.containingText"/>: <input type="text" name="matchingText" size="50" value="${matchingText}"/>
	<input type="submit" value="<spring:message code="general.submit"/>"/>
</form>
<hr/>
<table style="width:100%;">
	<tr>
		<td style="width:25%;" valign="top">
			<table id="codeList" style="border:1px solid #8FABC7; width:100%;">
				<thead><tr><th style="background-color:#8FABC7; color:white;"><spring:message code="custommessage.code"/></th></tr></thead>
				<tbody>
					<c:forEach items="${codes}" var="code">
						<tr><td class="codeRow" style="cursor:pointer;" onclick="javascript:viewTranslation(this, '${code}');">${code}</td></tr>
					</c:forEach>
				</tbody>
			</table>
		</td>
		<td style="width:75%; padding-left:25px; padding-top:25px;" valign="top">
			<span id="noCodeSelectedSection">
				<spring:message code="custommessage.clickOnACodeToView"/>
			</span>
			<span id="codeSelectedSection" style="display:none;">
				<b class="boxHeader" id="selectedCode">${code}</b>
				<div class="box">
					<table cellpadding="4" style="padding:10px; width:100%;">
						<c:forEach items="${supportedLocales}" var="localeEntry">
							<tr><td style="font-weight:bold;" colspan="2">${localeEntry.key}:</td></tr>
							<tr>
								<td valign="top" style="white-space:nowrap;"><spring:message code="custommessage.defaultValue"/>:</td>
								<td valign="top" style="width:100%;" id="defaultMessage${localeEntry.value}"></td>
							</tr>
							<tr>
								<td valign="top" style="white-space:nowrap;"><spring:message code="custommessage.customValue"/>:</td>
								<td valign="top" style="width:100%; padding-bottom:20px;">
									<input type="hidden" id="customId${localeEntry.value}" value=""/>
									<textarea class="customMessageInput" id="customMessage${localeEntry.value}" rows="2" style="width:100%;"></textarea>
								</td>
							</tr>
						</c:forEach>
					</table>
					<input type="button" value="<spring:message code="custommessage.edit"/>" id="editButton"/>	
					<input type="button" value="<spring:message code="custommessage.save"/>" id="saveButton"/>
					&nbsp;&nbsp;
					<input type="button" value="<spring:message code="custommessage.cancel"/>" id="cancelButton"/>	
				</div>
			</span>
		</td>
	</tr>
</table>

<%@ include file="/WEB-INF/template/footer.jsp" %>