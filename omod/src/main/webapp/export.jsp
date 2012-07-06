<%@ include file="/WEB-INF/template/include.jsp" %>
<openmrs:require privilege="Manage Custom Messages" otherwise="/login.htm" redirect="/module/custommessage/export.form" />
<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="localHeader.jsp" %>

<script type="text/javascript">
	
	jQuery(document).ready(function() {
		// make elemnts with class miltiselect to be listbox with checkboxes in each row
		jQuery(".multiselect").multiselect();
	});
	
	jQuery.fn.multiselect = function() {
		jQuery(this).each(function() {
	        var checkboxes = jQuery(this).find("input:checkbox");
	        checkboxes.each(function() {
	            var checkbox = jQuery(this);
	            // Highlight pre-selected checkboxes
	            if (checkbox.attr("checked")) {
	                checkbox.parent().addClass("multiselect-on");
	            }
	 
	            // Highlight checkboxes that the user selects
	            checkbox.click(function() {
	                if (checkbox.attr("checked")) {
	                    checkbox.parent().addClass("multiselect-on");
	                } else {
	                    checkbox.parent().removeClass("multiselect-on");
	                }
	            });
	        });
	    });
	};
	
	// validate if each multi select has at least one value checked  
	function validateMultiselects() {
		// check if multiselect with locales has items selected
		if (jQuery("input[name='locale']").serializeArray().length == 0) {
			alert('<openmrs:message code="custommessage.selectAtLeastOneLocaleAndLocation"/>');
			return false;
		}
		// check if multiselect with locations has items selected
		if (jQuery("input[name='location']").serializeArray().length == 0) {
			alert('<openmrs:message code="custommessage.selectAtLeastOneLocaleAndLocation"/>');
			return false;
		}
		return true;
	}
</script>
<style>
.multiselect {
    width:20em;
    height:15em;
    border:solid 1px #c0c0c0;
    overflow:auto;
}
 
.multiselect label {
    display:block;
}
 
.multiselect-on {
    color:#ffffff;
    background-color:#000099;
}
</style>

<form method="post" onsubmit="return validateMultiselects()">
	<table>
		<tr>
			<td>
				1.&nbsp;<openmrs:message code="custommessage.chooseLocalesToExport"/>:<br/><br/>
				<div class="multiselect">
					<c:forEach items="${supportedLocales}" var="localeEntry">
						<label><input type="checkbox" name="locale" value="${localeEntry.value}" />${localeEntry.key}</label>
					</c:forEach>
				</div>
			</td>
			<td>
				2.&nbsp;<openmrs:message code="custommessage.chooseLocationsToExport"/>:<br/><br/>
				<div class="multiselect">
					<c:forEach items="${messagesLocations}" var="locationEntry">
						<label><input type="checkbox" name="location" value="${locationEntry.key}" />${locationEntry.value}</label>
					</c:forEach>
				</div>
			</td>
			<td align="center">
				<input type="submit" value="<openmrs:message code="custommessage.export"/>"/> <br />
				<label><input type="checkbox" name="onlyExportCustomizedMessages" value="true" /><openmrs:message code="custommessage.onlyExportCustomizedMessages"/></label>
			</td>
		</tr>
	</table>
</form>

<%@ include file="/WEB-INF/template/footer.jsp" %>