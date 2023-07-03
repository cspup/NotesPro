package com.cspup.notespro.controller;

import com.cspup.notespro.entity.Lock;
import com.cspup.notespro.service.LockService;
import com.cspup.notespro.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author csp
 * @date 2023/4/25 16:45
 * @description
 */
@RestController
public class LockController {

    @Autowired
    LockService lockService;

    @GetMapping("/lock")
    public Object getLockStatus(@RequestParam Long noteId){
        return lockService.getLockStatus(noteId);
    }

    @PostMapping("/locked")
    public R<?> locked(@RequestBody Lock lock){
        try{
            lockService.locked(lock.getNoteId(),lock.getPassword());
        }catch (Exception e){
            return R.failed(403,e.getMessage());
        }
        return R.ok();
    }

    @PostMapping("/unlock")
    public R<?> unlock(@RequestBody Lock lock){
        try{
            lockService.unLock(lock.getNoteId(),lock.getPassword());
        }catch (Exception e){
            return R.failed(403,e.getMessage());
        }
        return R.ok();
    }
}
