package edu.asu.diging.cord19.explorer.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;

@Controller
public class ShowAffiliationController {

    @Autowired
    private PublicationDao pubDao;

    @RequestMapping("/affiliations")
    public String get(Model model, Pageable pageable) {
        model.addAttribute("affs", pubDao.getAffiliationsAndArticles(pageable.getOffset(), pageable.getPageSize()));
        long affCount = pubDao.getAffiliationCount();
        model.addAttribute("total", affCount);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pageCount",
                affCount / pageable.getPageSize() + (affCount % pageable.getPageSize() > 0 ? 1 : 0));
        return "affiliations";
    }
}
