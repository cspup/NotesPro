package com.cspup.notespro.utils;

import com.cspup.notespro.entity.Note;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.JacksonObjectReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author csp
 * @date 2023/7/5 13:33
 * @description
 */
@Component
@ServerEndpoint("/websocket/{uId}")
@Slf4j
public class WebSocketServerUtil {
    private Session session;
    private static CopyOnWriteArraySet<WebSocketServerUtil > webSocketSet = new CopyOnWriteArraySet<>();
    private static ConcurrentHashMap<Long,WebSocketServerUtil > webSocketMap  = new ConcurrentHashMap<>();
    private Long uId = null;

    @OnOpen
    public void onOpen(Session session, @PathParam("uId") Long uId){
        this.session = session;
        this.uId = uId;
        if(webSocketMap .containsKey(uId)){
            webSocketMap .remove(uId);
            webSocketMap .put(uId,this);
        }else{
            webSocketMap .put(uId,this);
            webSocketSet.add(this);
        }

        log.info("【websocket消息】有新的连接，总数：{}",webSocketMap.size());
    }

    @OnClose
    public void onClose(){
        if(webSocketMap.containsKey(uId)){
            webSocketMap.remove(uId);
            //从set中删除
            webSocketSet.remove(this);
        }
        log.info("【websocket消息】连接断开，总数：{}",webSocketSet.size());
    }

    @OnMessage
    public void onMessage(String message){
        log.info("【websocket消息】收到客户端发来的消息：{}",message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode node = objectMapper.readTree(message);
            log.info(node.toPrettyString());
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void sendMessage(String message){
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message,Long uId) throws Exception {

        //log.info("发送消息到:"+uId+"，报文:"+message);

            if(webSocketMap.containsKey(uId)){
                webSocketMap.get(uId).sendMessage(message);
            }else{
                log.error("用户"+uId+",不在线！");
                throw new Exception("连接已关闭，请刷新页面后重试");
            }

    }
}
