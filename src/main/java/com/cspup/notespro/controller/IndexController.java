package com.cspup.notespro.controller;

import com.cspup.notespro.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author csp
 * @date 2022/2/22 20:41
 * @description
 */
@Controller
public class IndexController {

    /**
     * 任何页面都使用index.html
     * @return 跳转到index
     */
    @RequestMapping(value = "/{any}", method = RequestMethod.GET)
    public String index(@PathVariable String any) {
        return "index";
    }

}
