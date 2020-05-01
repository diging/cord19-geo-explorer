package edu.asu.diging.cord19.explorer.web.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.asu.diging.cord19.explorer.core.data.TaskRepository;

@Controller
public class TaskListController {

    @Autowired
    private TaskRepository taskRepo;

    @RequestMapping("/auth/task")
    public String show(Model model) {
        model.addAttribute("tasks", taskRepo.findAll());

        return "auth/importList";

    }
}
