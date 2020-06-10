package edu.asu.diging.cord19.explorer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.model.Publication;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;

@Controller
public class ArxivDatasetController {
    
    @Autowired
    private PublicationRepository pubRepo;

    @RequestMapping("/arxiv/publications")
    public String list(Model model, Pageable pageable) {
        model.addAttribute("publications", pubRepo.findByArxivIdIsNotNullOrDatabase(Publication.DATABASE_ARXIV, pageable));
        
        long pubCount = pubRepo.countByArxivIdIsNotNullOrDatabase(Publication.DATABASE_ARXIV);
        model.addAttribute("pubCount", pubCount);
        
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pageSize", pageable.getPageSize());
        
        long pageCount = pubCount/pageable.getPageSize();
        if (pubCount%pageable.getPageSize() > 0) {
            pageCount += 1;
        }
        model.addAttribute("pageCount", pageCount);
        return "arxiv/publications";
    }
}
