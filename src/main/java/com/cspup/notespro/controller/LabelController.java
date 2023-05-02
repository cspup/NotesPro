package com.cspup.notespro.controller;

import com.cspup.notespro.service.LabelService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author csp
 * @date 2022/2/22 20:08
 * @description
 */
@RestController
public class LabelController {

    final private LabelService keyService;

    LabelController(LabelService keyService){
        this.keyService = keyService;
    }

    @GetMapping("/getLabel")
    public String generateLabel(){
        return keyService.generateLabel();
    }
}
