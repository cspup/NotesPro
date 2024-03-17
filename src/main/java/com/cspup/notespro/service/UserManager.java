package com.cspup.notespro.service;

import jakarta.websocket.Session;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author csp
 * @date 2024/3/13 22:27
 * @description
 */

public class UserManager {
    // 使用ConcurrentHashMap来存储用户，确保线程安全
    private final Map<String, User> users = new ConcurrentHashMap<>();

    // 添加新用户
    public void addUser(Session session, String username) {
//        String userId = UUID.randomUUID().toString(); // 生成唯一的用户ID
        String userId = session.getId();
        User user = new User(userId, username, session);
        users.put(userId, user);
        System.out.println("User " + username + " connected with ID: " + userId);
    }

    // 移除用户
    public void removeUser(String userId) {
        User user = users.remove(userId);
        if (user != null) {
            System.out.println("User " + user.getUsername() + " disconnected with ID: " + userId);
        }
    }

    // 获取用户
    public User getUser(String userId) {
        return users.get(userId);
    }

    // 获取所有用户
    public Map<String, User> getAllUsers() {
        return users; // 返回不可修改的视图以保护内部状态
    }

    // 用户类，表示协同编辑中的用户
    public static class User {
        private final String id;
        private final String username;
        private Session session;
        // 可以添加其他属性，如编辑的文档ID、光标位置等

        public User(String id, String username, Session session) {
            this.id = id;
            this.username = username;
            this.session = session;
        }

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public Session getSession() {
            return session;
        }

        public void setSession(Session session) {
            this.session = session;
        }

        // 可以添加其他getter和setter方法
    }
}