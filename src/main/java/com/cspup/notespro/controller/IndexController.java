package com.cspup.notespro.controller;

import com.cspup.notespro.service.NoteService;
import com.cspup.notespro.utils.WebSocketUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 获取Websocket连接id
     * @return wsId
     */
    @GetMapping(value = "/ws/getId")
    @ResponseBody
    public String getWsId(){
        return WebSocketUtil.generateWsId();
    }

}
