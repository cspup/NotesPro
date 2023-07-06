package com.cspup.notespro.service;

import com.cspup.notespro.entity.Lock;
import com.cspup.notespro.mapper.LockMapper;
import com.cspup.notespro.utils.Const;
import com.cspup.notespro.utils.WebSocketServerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author csp
 * @date 2023/4/25 16:46
 * @description
 */
@Service
public class LockService {
    @Autowired
    private LockMapper lockMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    public boolean getLockStatus(Long noteId){
        if (redisTemplate.opsForValue().get(Const.noteId+noteId)==null){
            Lock lock = lockMapper.selectLockByNoteId(noteId);
            redisTemplate.opsForValue().set(Const.noteId+noteId,String.valueOf(lock!=null&&lock.isLocked()));
            return lock != null && lock.isLocked();
        }else{
            return "true".equals(redisTemplate.opsForValue().get(Const.noteId+noteId));
        }
    }

    public void updateLock(Lock lock){
        redisTemplate.opsForValue().set(Const.noteId+lock.getNoteId(),String.valueOf(lock.isLocked()));
        lockMapper.updateLock(lock);
    }

    public void locked(Long noteId,String password) throws Exception {
        Lock lock1 = lockMapper.selectLockByNoteId(noteId);
        boolean isPasswordCorrectOrNull = (lock1==null)||(lock1.getPassword()==null)
                ||(password!=null&&password.equals(lock1.getPassword()));

        if (isPasswordCorrectOrNull){
           if (lock1 == null) {
                Lock lock = new Lock();
                lock.setNoteId(noteId);
                lock.setPassword(password);
                lock.setLocked(true);
                lockMapper.insertLock(lock);
            } else {
                lock1.setPassword(password);
                lock1.setLocked(true);
                lockMapper.updateLock(lock1);
            }
        }else{
            throw new RuntimeException("密码错误");
        }
        redisTemplate.opsForValue().set(Const.noteId+noteId,"true");
    }

    /**
     * 解锁
     *
     * @param noteId   nid
     * @param password password
     */
    public void unLock(Long noteId, String password){
        Lock lock1 = lockMapper.selectLockByNoteId(noteId);
        boolean isPasswordCorrectOrNull = (lock1==null)||(lock1.getPassword()==null)
                ||(password!=null&&password.equals(lock1.getPassword()));
        if (isPasswordCorrectOrNull){
           if (lock1 != null) {
                lock1.setPassword(password);
                lock1.setLocked(false);
                lockMapper.updateLock(lock1);
            }
        }else {
            throw new RuntimeException("密码错误");
        }
    }
}
