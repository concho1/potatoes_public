package com.goott.potatoes.common.testController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
    @GetMapping("/")
    public ModelAndView getMainPages(
            ModelAndView modelAndView){
        modelAndView.setViewName("concho/concho-main1");
        return modelAndView;
    }
}
