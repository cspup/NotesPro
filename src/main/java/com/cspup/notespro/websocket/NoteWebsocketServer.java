package com.cspup.notespro.websocket;

import com.cspup.notespro.DTO.NoteDTO;
import com.cspup.notespro.entity.Note;
import com.cspup.notespro.service.NoteService;
import com.cspup.notespro.utils.ItemList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author csp
 * @date 2023/7/5 13:33
 * @description
 */
@Component
@ServerEndpoint("/noteWs/{uId}")
@Slf4j
public class NoteWebsocketServer {
    private Session session;
    private static final CopyOnWriteArraySet<NoteWebsocketServer> webSocketSet = new CopyOnWriteArraySet<>();
    private static final ConcurrentHashMap<String, NoteWebsocketServer> webSocketMap  = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, HashSet<String>> noteWsMap  = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String,ItemList> itemListMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, String> wsIdLabelMap = new ConcurrentHashMap<>();
    private String uId = null;
    private static NoteService noteService;
    private final JsonMapper jsonMapper = new JsonMapper();

    /**
     * 解决无法注入问题
     * @param noteService 方法注入noteService
     */
    @Autowired
    public void setNoteService(NoteService noteService){
        NoteWebsocketServer.noteService = noteService;
    }


    @OnOpen
    public void onOpen(Session session, @PathParam("uId") String uId){
        this.session = session;
        this.uId = uId;
        if(webSocketMap.containsKey(uId)){
            webSocketMap.remove(uId);
            webSocketMap.put(uId,this);
        }else{
            webSocketMap.put(uId,this);
            webSocketSet.add(this);
        }
        Map<String, List<String>> params = session.getRequestParameterMap();
        if (!itemListMap.containsKey(params.get("label").get(0))){
            ItemList itemList = new ItemList();
            itemListMap.put(params.get("label").get(0),itemList);
        }
        wsIdLabelMap.put(uId, params.get("label").get(0));

        log.info("【websocket消息】有新的连接，总数：{}",webSocketMap.size());
    }

    @OnClose
    public void onClose(){
        if(webSocketMap.containsKey(uId)){
            webSocketMap.remove(uId);
            //从set中删除
            webSocketSet.remove(this);
        }
        if (wsIdLabelMap.containsKey(uId)){
            String label1 = wsIdLabelMap.get(uId);
            if (noteWsMap.get(label1)!=null) {
                noteWsMap.get(label1).remove(uId);
            }
            wsIdLabelMap.remove(uId);
        }

        log.info("【websocket消息】连接断开，总数：{}",webSocketSet.size());
    }

    @OnMessage
    public void onMessage(String message,@PathParam("uId") String uId){
        log.info("【websocket消息】收到客户端发来的消息：{}",message);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            NoteDTO noteDTO = objectMapper.readValue(message, NoteDTO.class);
            Note note = new Note();
            note.setId(noteDTO.getId());
            note.setContent(noteDTO.getContent());
            note.setLabel(noteDTO.getLabel());
            note.setLogicTime(noteDTO.getLogicTime());
            if (noteWsMap.containsKey(noteDTO.getLabel())){
                noteWsMap.get(noteDTO.getLabel()).add(uId);
            }else {
                HashSet<String> set = new HashSet<>();
                set.add(uId);
                noteWsMap.put(noteDTO.getLabel(), set);
            }
            try{
                Note lastObject = (Note) itemListMap.get(noteDTO.getLabel()).getLastObject();
                // 保证更新到数据库中的内容logicTime最大
                if (lastObject==null||lastObject.getLogicTime()<=noteDTO.getLogicTime()){
                    noteService.updateNote(note);
                    itemListMap.get(noteDTO.getLabel()).append(note, noteDTO.getLogicTime());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            sendForAll(note.getLabel(),jsonMapper.writeValueAsString(noteService.getLastNoteByLabel(note.getLabel())));
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

    public void sendForAll(String label,String message){
        if (noteWsMap.containsKey(label)){
            HashSet<String> clientSet = noteWsMap.get(label);
            clientSet.removeIf(o -> !webSocketMap.containsKey(o));
            for (String uId:clientSet){
                try{
                    sendInfo(message, uId);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message,String uId) throws Exception {

        //log.info("发送消息到:"+uId+"，报文:"+message);

        if(webSocketMap.containsKey(uId)){
            webSocketMap.get(uId).sendMessage(message);
        }else{
            log.error("用户"+uId+",不在线！");
            throw new Exception("连接已关闭，请刷新页面后重试");
        }

    }
}
