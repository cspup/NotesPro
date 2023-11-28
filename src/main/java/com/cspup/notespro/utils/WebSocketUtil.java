package com.cspup.notespro.utils;

/**
 * @author csp
 * @date 2023/7/5 16:17
 * @description
 */
public class WebSocketUtil {

    /**
     * 由雪花算法生成Websocket的唯一id
     *
     * @return wsId
     */
    public static long generateWsId(){
        return CreateId.next();
    }


}
