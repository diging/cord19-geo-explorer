package edu.asu.diging.cord19.explorer.web.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.cord19.explorer.core.service.DocumentImportManager;

@Controller
public class YearExtractionController {

	@Autowired
	private DocumentImportManager importManager;

	@RequestMapping(value = "/auth/extract/years", method=RequestMethod.GET)
	public String show() {
		
		return "auth/extractYears";
	}
	
	@RequestMapping(value = "/auth/extract/years", method=RequestMethod.POST)
	public String start() {
		importManager.startYearExtraction();
		
		return "redirect:/";
	}
}
