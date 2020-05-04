package edu.asu.diging.cord19.explorer.web.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.cord19.explorer.core.service.DocumentImportManager;

@Controller
public class AffiliationCleaningController {

    @Autowired
    private DocumentImportManager importManager;

    @RequestMapping(value = "/auth/extract/affiliations", method = RequestMethod.GET)
    public String show() {

        return "auth/extractAffiliations";
    }

    @RequestMapping(value = "/auth/extract/affiliations", method = RequestMethod.POST)
    public String start() throws ClassCastException, ClassNotFoundException, IOException {
        importManager.startAffiliationCleaning();

        return "redirect:/";
    }
}
