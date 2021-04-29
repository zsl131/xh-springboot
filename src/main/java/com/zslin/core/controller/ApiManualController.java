package com.zslin.core.controller;

import com.zslin.core.api.tools.ExplainTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "apiManual")
public class ApiManualController {

    Logger log = LoggerFactory.getLogger(ApiManualController.class);

    @Autowired
    private ExplainTools explainTools;

    @GetMapping(value = {"", "/", "index"})
    public String index(Model model) {
//        log.info("已进入ApiManualController的index方法11111");
        model.addAttribute("explainList", explainTools.buildAllExplain());
        model.addAttribute("explainResultList", explainTools.buildExplainResult());
        return "apiManual/index";
    }
}
