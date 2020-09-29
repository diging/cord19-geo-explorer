package edu.asu.diging.cord19.explorer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationStatsProvider;

@Controller
public class StatsController {

    @Autowired
    private PublicationStatsProvider provider;
    
    @Autowired
    private PublicationRepository repo;
    
    @RequestMapping("/stats")
    public String get(Model model) {
        model.addAttribute("paperWithAffCount", provider.getPaperWithAuthorAffiliationCount());
        model.addAttribute("paperWithoutAffCount", provider.getPaperWithoutAuthorAffiliationCount());
        model.addAttribute("authorCount", provider.getAuthorCount());
        model.addAttribute("totalPublications", repo.count());
        
        model.addAttribute("authorWithAffCount", provider.getAuthorWithAffiliationCount());
        model.addAttribute("authorWithAffAndArticleCount", provider.getAuthorWithAffiliationAndWikiarticleCount());
        model.addAttribute("paperWithAffAndArticleCount", provider.getPaperWithAtLeastOneAffiliationAndWikiarticleCount());
        model.addAttribute("authorWithIncorrectAffArticleMatch", provider.getAuthorsWithAffiliationAndIncorrectWikiarticleCount());
        model.addAttribute("authorsWithCorrectRegionAffCount", provider.getAuthorsWithAffiliationAndCorrectRegionWikiarticleCount());
        model.addAttribute("papersWithIncorrectAffArticleMatch", provider.getPapersWithAffiliationAndIncorrectWikiarticleCount());
        return "stats";
    }
}
