package com.cspup.notespro.websocket;

import com.cspup.notespro.delta.Delta;
import com.cspup.notespro.delta.NoteDelta;
import com.cspup.notespro.service.NoteService;
import com.cspup.notespro.service.OperationLogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author csp
 * @date 2024/3/13 21:55
 * @description
 */
@Component
@ServerEndpoint("/ws")
@Slf4j
public class WebsocketEndpoint {


    // 使用线程安全的集合来存储Session对象
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();

    // 保存label对应的session
    private static final Map<String, Set<Session>> sessionsManger = new ConcurrentHashMap<>();

    private static final ObjectMapper objectMapper = new ObjectMapper();
    // session对应的label
    private static final Map<Session, String> sessionLabel = new ConcurrentHashMap<>();
    private static NoteService noteService;

    private static OperationLogService oplogService;

    @Autowired
    public void setNoteService(NoteService noteService) {
        WebsocketEndpoint.noteService = noteService;
    }

    @Autowired
    public void setOplogService(OperationLogService oplogService) {
        WebsocketEndpoint.oplogService = oplogService;
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        log.info("【websocket消息】有新的连接: {}", session.getId());
        sessions.add(session);
        Map<String, List<String>> params = session.getRequestParameterMap();
        List<String> labels = params.get("label");
        if (!labels.isEmpty()) {
            String label = labels.get(0);
            sessionLabel.put(session, label);
            // computeIfAbsent会检查是否存在key对应的value，如果不存在使用给出lambda表达式创建，如果存在返回value
            // computeIfAbsent是原子操作，保证线程安全
            sessionsManger.computeIfAbsent(label,k -> ConcurrentHashMap.newKeySet()).add(session);
//            Set<Session> sessions;
//            if (!sessionsManger.containsKey(label)) {
//                // 保存对应的session
//                sessions = new CopyOnWriteArraySet<>();
//            } else {
//                sessions = sessionsManger.get(label);
//            }
//            sessions.add(session);
//            sessionsManger.put(label, sessions);
        } else {
            onClose(session);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("【websocket消息】收到客户端发来的消息：{}", message);
        try {
            // 先验证客户端和服务端版本号是否一致，一致则直接应用操作
            // 不一致，从客户端版本到服务端版本遍历操作日志，进行操作转换解决冲突
            // 应用转换后的操作
            NoteDelta client = objectMapper.readValue(message, NoteDelta.class);
            NoteDelta server = noteService.getServerDelta(client.getLabel());
            Delta delta = client.getDelta();
            Delta origin = server.getDelta();
            if (client.getVersion() != server.getVersion()) {
                // 不一致，遍历进行转换
                long version = client.getVersion();
                while (version <= server.getVersion()) {
                    Delta historyDelta = oplogService.getDelta(client.getLabel(), version);
                    delta = historyDelta.transform(delta, true);
                    // 更新操作日志
                    oplogService.updateOPLog(client.getLabel(), version, delta.toString());
                    version++;
                }
                // 向发送冲突的客户端发送应用更新后的全量文档
                NoteDelta fullNoteDelta = new NoteDelta(true, client.getLabel(), server.getVersion() + 1, origin.compose(delta));
                sendMessage(session, objectMapper.writeValueAsString(fullNoteDelta));
            }
            // 应用操作
            origin = origin.compose(delta);
            server.setDelta(origin);
            server.increaseVersion();

            // 保存文档
            noteService.saveNote(sessionLabel.get(session), objectMapper.writeValueAsString(server));
            // 保存操作日志
            oplogService.addOpLog(server.getLabel(), server.getVersion(), delta.toString());

            // 向其它客户端广播此次操作
            NoteDelta sendDelta = new NoteDelta(false, server.getLabel(), server.getVersion(), delta);
            broadcast(sessionLabel.get(session), objectMapper.writeValueAsString(sendDelta), session.getId());

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @OnClose
    public void onClose(Session session) {
        log.info("【websocket消息】连接断开：{}", session.getId());
        sessions.remove(session);
    }

    @OnError
    public void onError(Throwable error) {
        System.err.println("WebSocket Server Error: " + error.getMessage());
    }

    /**
     * 向所有客户端发送消息
     *
     * @param message 消息
     */
    public static void broadcast(String label, String message) {

        Collection<Session> sessionsList = sessionsManger.get(label);

        for (Session session : sessionsList) {
            if (session.isOpen()) {
                asyncSend(session, message);
            } else {
                sessions.remove(session);
                sessionsList.remove(session);
            }
        }
    }

    /**
     * 排除某客户端
     *
     * @param label   标号
     * @param message 消息
     * @param exclude 排除的客户端
     */
    public static void broadcast(String label, String message, String exclude) {

        Collection<Session> sessionsList = sessionsManger.get(label);

        for (Session session : sessionsList) {
            if (session.isOpen()) {
                log.info("广播了信息：{}", message);
                if (!session.getId().equals(exclude)) {
                    asyncSend(session, message);
                }
            } else {
                sessions.remove(session);
                sessionsList.remove(session);
            }
        }
    }

    public static void sendMessage(Session session, String message) {
        if (session.isOpen()) {
            asyncSend(session, message);
        }
    }

    /**
     * 异步发送消息给客户端
     *
     * @param session 客户端会话
     * @param message 消息
     */
    private static void asyncSend(Session session, String message) {
        session.getAsyncRemote().sendText(message);
    }

    static class Consumer implements Runnable {
        private final BlockingQueue<String> queue;

        public Consumer(BlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                while (true) {
                    String message = queue.take();  //从队列中取出消息
                    System.out.println("消费了消息：" + message);
                    Map<String, Object> map = objectMapper.readValue(message, Map.class);
                    String label = map.get("label").toString();
                    String content = map.get("content").toString();
                    String delta = map.get("changes").toString();
                    String sessionId = map.get("sessionId").toString();
                    WebsocketEndpoint.broadcast(label, objectMapper.writeValueAsString(map.get("changes")), sessionId);
                }
            } catch (InterruptedException | JsonProcessingException e) {

                Thread.currentThread().interrupt();
                //响应中断，通常就是你希望线程在捕获到异常后能够退出而不是继续运行
            }
        }
    }
}


