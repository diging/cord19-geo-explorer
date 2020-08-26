package edu.asu.diging.cord19.explorer.web.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import edu.asu.diging.cord19.explorer.core.service.ExportManager;

@Controller
public class ExportController {
    
    @Autowired
    private ExportManager manager;

    @RequestMapping(value="/auth/export", method=RequestMethod.GET)
    public String get(Model model) {
        return "auth/export";
    }
    
    @RequestMapping(value="/auth/export/start", method=RequestMethod.POST)
    public String post() {
        manager.startExport();
        return "redirect:/auth/export/list?sort=task.dateStarted,desc";
    }
}
