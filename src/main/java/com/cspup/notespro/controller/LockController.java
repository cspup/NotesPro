package com.cspup.notespro.controller;

import com.cspup.notespro.entity.Lock;
import com.cspup.notespro.service.LockService;
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

    @PostMapping("/lock")
    public Object updateLock(@RequestBody Lock lock){
        return lockService.updateLock(lock);
    }
}
