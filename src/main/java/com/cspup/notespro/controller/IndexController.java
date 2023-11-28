package com.cspup.notespro.controller;

import com.cspup.notespro.DTO.ClientDTO;
import com.cspup.notespro.utils.R;
import com.cspup.notespro.utils.WebSocketUtil;
import org.springframework.stereotype.Controller;
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
     *
     * @return wsId
     */
    @GetMapping(value = "/ws/getId")
    @ResponseBody
    public long getWsId(){
        return WebSocketUtil.generateWsId();
    }

    /**
     * 客户端获取id和时间戳
     * @return ClientDTO
     */
    @GetMapping(value = "/client/getId")
    @ResponseBody
    public R<?> getIdAndTimestamp(){
        long id = WebSocketUtil.generateWsId();
        long timestamp = System.currentTimeMillis();
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(id);
        clientDTO.setTime(timestamp);
        return R.ok(clientDTO);
    }

}
