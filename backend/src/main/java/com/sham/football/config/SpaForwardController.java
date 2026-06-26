package com.sham.football.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({
            "/dashboard",
            "/matches",
            "/teams",
            "/players",
            "/sentiment",
            "/analytics",
            "/rankings",
            "/predictions"
    })
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}
