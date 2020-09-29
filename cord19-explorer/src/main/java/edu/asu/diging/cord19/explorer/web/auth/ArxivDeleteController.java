package edu.asu.diging.cord19.explorer.web.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;

@Controller
public class ArxivDeleteController {
    
    @Autowired
    private PublicationRepository pubRepo;

    @RequestMapping(value="/auth/arxiv/delete")
    public String get() {
        return "auth/deleteArxiv";
    }
    
    @RequestMapping(value="/auth/arxiv/delete", method=RequestMethod.POST)
    public String post(RedirectAttributes redirectAttrs) {
        pubRepo.deleteByDatabase(Publication.DATABASE_ARXIV);
        
        redirectAttrs.addFlashAttribute("show_alert", true);
        redirectAttrs.addFlashAttribute("alert_type", "success");
        redirectAttrs.addFlashAttribute("alert_msg", "Arxiv dataset successfully deleted.");
        
        return "redirect:/auth/arxiv/delete";
    }

}

