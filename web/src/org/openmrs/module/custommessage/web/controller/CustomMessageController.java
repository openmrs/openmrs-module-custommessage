package org.openmrs.module.custommessage.web.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MutableMessageSource;
import org.openmrs.messagesource.PresentationMessage;
import org.openmrs.module.custommessage.CustomMessage;
import org.openmrs.module.custommessage.CustomMessageSource;
import org.openmrs.module.custommessage.service.CustomMessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomMessageController {
    
    @RequestMapping("/module/custommessage/index.form") 
    public void viewIndex(ModelMap model) {
    	MutableMessageSource mms = Context.getMessageSourceService().getActiveMessageSource();
    	CustomMessageSource cms = (CustomMessageSource)mms;
    	model.addAttribute("codes", cms.getAllMessageCodes());
    	Map<String, Locale> localeMap = new TreeMap<String, Locale>();
    	for (Locale l : Context.getAdministrationService().getPresentationLocales()) {
    		localeMap.put(l.getDisplayName(), l);
    	}
    	model.addAttribute("supportedLocales", localeMap);
    }
    
    @RequestMapping("/module/custommessage/getMessagesForCode.form") 
    public void getMessagesForCode(HttpServletResponse response, @RequestParam("code") String code) throws Exception {
    	response.setContentType("text/json");
    	response.setCharacterEncoding("UTF-8");
    	Map<String, Object> ret = new HashMap<String, Object>();
    	
    	MutableMessageSource mms = Context.getMessageSourceService().getActiveMessageSource();
    	CustomMessageSource cms = (CustomMessageSource)mms;
    	
    	Map<String, String> defaults = new HashMap<String, String>();
    	for (PresentationMessage pm : cms.getMutableParentSource().getPresentations()) {
    		if (code.equals(pm.getCode())) {
    			defaults.put(pm.getLocale().toString(), pm.getMessage());
    		}
    	}
    	ret.put("defaults", defaults);
    	
    	Map<String, Map<String, String>> customs = new HashMap<String, Map<String, String>>();
    	for (CustomMessage cm : Context.getService(CustomMessageService.class).getCustomMessagesForCode(code)) {
    		Map<String, String> custom = new HashMap<String, String>();
    		custom.put("id", cm.getId().toString());
    		custom.put("message", cm.getMessage());
    		customs.put(cm.getLocale().toString(), custom);
    	}
    	ret.put("customs", customs);
    	
		response.getWriter().write(new ObjectMapper().writeValueAsString(ret));
    }
    
    @RequestMapping("/module/custommessage/saveMessagesForCode.form") 
    public void saveMessagesForCode(HttpServletRequest request, HttpServletResponse response, @RequestParam("code") String code) {
    	CustomMessageService cms = Context.getService(CustomMessageService.class);
    	boolean hasChanged = false;
    	for (Locale l : Context.getAdministrationService().getPresentationLocales()) {
    		String id = request.getParameter("id"+l.toString());
    		String message = request.getParameter("message"+l.toString());
    		if (StringUtils.isNotBlank(id)) {
    			CustomMessage msg = cms.getCustomMessage(Integer.parseInt(id));
    			if (StringUtils.isNotBlank(message)) { // Update existing message
    				msg.setMessage(message);
    				cms.saveCustomMessage(msg);
    				hasChanged = true;
    			}
    			else { // Delete existing message
    				cms.deleteCustomMessage(msg);
    				hasChanged = true;
    			}
    		}
    		else {
    			if (StringUtils.isNotBlank(message)) { // Insert new message
    				CustomMessage msg = new CustomMessage();
    				msg.setCode(code);
    				msg.setLocale(l);
    				msg.setMessage(message);
    				cms.saveCustomMessage(msg);
    				hasChanged = true;
    			}
    		}
    	}
    	if (hasChanged) {
    		((CustomMessageSource)Context.getMessageSourceService().getActiveMessageSource()).refreshCache();
    	}
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/module/custommessage/export.form")
    public void exportMessagePage(ModelMap model) {
    	Map<String, Locale> localeMap = new TreeMap<String, Locale>();
    	for (Locale l : Context.getAdministrationService().getPresentationLocales()) {
    		localeMap.put(l.getDisplayName(), l);
    	}
    	model.addAttribute("supportedLocales", localeMap);
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/module/custommessage/export.form")
    public void exportMessageSubmit(HttpServletResponse response, @RequestParam("locale") Locale locale) throws Exception {
    	CustomMessageSource cms = (CustomMessageSource) Context.getMessageSourceService().getActiveMessageSource();
    	cms.refreshCache();
    	Map<String, PresentationMessage> messageMap = new TreeMap<String, PresentationMessage>(cms.getCachedMessages().get(locale));
    	response.setContentType("text/plain");
    	response.addHeader("Content-disposition", "attachment; filename=messages_" + locale.toString() + ".properties");
    	for (String code : messageMap.keySet()) {
    		PresentationMessage pm = messageMap.get(code);
    		if (pm != null && StringUtils.isNotBlank(pm.getMessage())) {
    			response.getWriter().write(code + " = " + pm.getMessage() + System.getProperty("line.separator"));
    		}
    	}
    }
}
