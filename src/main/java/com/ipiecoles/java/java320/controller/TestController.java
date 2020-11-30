package com.ipiecoles.java.java320.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// Ctrl+F9 ou Ctrl+Maj+F9: Compile et relance le serveur
@Controller
public class TestController {
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test(final ModelMap m) {
        m.put("n", "IPI");
        m.put("msg", "How are <strong>you</strong> !");
        return "test";
    }
}
