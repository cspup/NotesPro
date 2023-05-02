package com.cspup.notespro.service;

import com.cspup.notespro.entity.Lock;
import com.cspup.notespro.mapper.LockMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
    public boolean getLockStatus(Long noteId){
        Lock lock = lockMapper.selectLockByNoteId(noteId);
        if (lock!=null){
            return lock.isLocked();
        }else {
            return false;
        }
    }

    public int updateLock(Lock lock){
        Lock lock1 = lockMapper.selectLockByNoteId(lock.getNoteId());
        if (lock1==null){
            lock.setPassword("1234");
            return lockMapper.insertLock(lock);
        }else {
            return lockMapper.updateLock(lock);
        }
    }
}
