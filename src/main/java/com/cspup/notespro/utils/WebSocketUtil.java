package com.cspup.notespro.utils;

import java.util.UUID;

/**
 * @author csp
 * @date 2023/7/5 16:17
 * @description
 */
public class WebSocketUtil {

    /**
     * 由雪花算法生成Websocket的唯一id
     * @return wsId
     */
    public static String generateWsId(){
        return CreateId.nextId();
    }


}
