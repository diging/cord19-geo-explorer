package edu.asu.diging.cord19.explorer.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.asu.diging.cord19.explorer.core.model.impl.PublicationImpl;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationDao;
import edu.asu.diging.cord19.explorer.core.mongo.PublicationRepository;

@Controller
public class ShowPublicationController {

    @Autowired
    private PublicationDao pubDao;
    
    @Autowired
    private PublicationRepository pubRepo;

    @RequestMapping("/pubs")
    public String get(Model model, @PageableDefault(size = 20)Pageable pageable, @RequestParam(value = "last",required=false)String last, @RequestParam boolean init) {
        PublicationImpl pub = (PublicationImpl) pubRepo.findFirstByPaperId(last);
        List<PublicationImpl> page = pubDao.getPublications(pageable, pub, init);
        long count = pubDao.getPublicationCount();
        model.addAttribute("pubs", page);
        model.addAttribute("total", count);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pageCount", count / pageable.getPageSize() + (count % pageable.getPageSize() > 0 ? 1 : 0));
        model.addAttribute("sort", pageable.getSort().toString().replace(": ", ","));
        model.addAttribute("order", pageable.getSort().toString().split(": ")[1]);
        return "publications";
    }

}
