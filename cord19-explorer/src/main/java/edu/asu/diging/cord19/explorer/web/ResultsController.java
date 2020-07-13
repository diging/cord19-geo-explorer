package edu.asu.diging.cord19.explorer.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ResultsController {

    @RequestMapping(value = "/result")
    public String home(Model model) {
        return "results";
    }

}
