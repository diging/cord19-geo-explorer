package edu.asu.diging.cord19.explorer.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.model.impl.ParagraphImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.web.model.Section;

@Controller
public class PaperController {

    @Autowired
    private PublicationRepository pubRepo;

    @RequestMapping("/paper/{paperId:.+}")
    public String findPublications(@PathVariable("paperId") String paperId, Model model) {
        Publication pub = pubRepo.findFirstByPaperId(paperId);
        model.addAttribute("publication", pub);
        List<Section> sections = new ArrayList<Section>();
        Section currentSection = new Section();
        sections.add(currentSection);
        if (pub.getBodyText() != null) {
            for (ParagraphImpl para : pub.getBodyText()) {
                if (currentSection.getTitle() == null) {
                    currentSection.setTitle(para.getSection());
                    if (currentSection.getParagraphs() == null) {
                        currentSection.setParagraphs(new ArrayList<>());
                    }
                    currentSection.getParagraphs().add(para);
                    continue;
                }
                
                if (currentSection.getTitle().equals(para.getSection())) {
                    currentSection.getParagraphs().add(para);
                    continue;
                }
                
                currentSection = new Section();
                sections.add(currentSection);
                currentSection.setParagraphs(new ArrayList<>());
                currentSection.getParagraphs().add(para);
                
            }
        }
        model.addAttribute("sections", sections);
        return "paper";
    }
}
