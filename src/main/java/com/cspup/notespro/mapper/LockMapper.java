package com.cspup.notespro.mapper;

import com.cspup.notespro.entity.Lock;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author csp
 * @date 2023/4/25 16:45
 * @description
 */

@Mapper
public interface LockMapper {

    public Lock selectLockById(Long id);
    public Lock selectLockByNoteId(Long noteId);

    public int updateLock(Lock lock);

    public int insertLock(Lock lock);


}
