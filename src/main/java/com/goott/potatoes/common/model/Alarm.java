package com.goott.potatoes.common.model;

import lombok.Data;
import org.springframework.ui.Model;

@Data
public class Alarm {
    private String message;
    private String redirectTo;
    private String messagePage;
    private Model model;
    public Alarm(Model model){
        this.model = model;
        this.messagePage = "common/message";
        this.model.addAttribute("alarm", this);
    }
    public void setMessageAndRedirect(String message, String redirectTo){
        this.message = message;
        this.redirectTo = redirectTo;
        this.model.addAttribute("alarm", this);
    }
}
