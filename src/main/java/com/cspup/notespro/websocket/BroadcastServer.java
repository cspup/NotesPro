package com.cspup.notespro.websocket;

import jakarta.websocket.server.ServerEndpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author csp
 * @date 2023/7/10 16:44
 * @description 向连接同一文档的所有客户端广播客户端操作产生的item
 */
@Component
@ServerEndpoint("/broadcast")
@Slf4j
public class BroadcastServer {
    private static final CopyOnWriteArraySet<NoteWebsocketServer> webSocketSet = new CopyOnWriteArraySet<>();


}
