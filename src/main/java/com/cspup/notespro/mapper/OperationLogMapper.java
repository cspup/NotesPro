package com.cspup.notespro.mapper;

import com.cspup.notespro.entity.OperationLog;

/**
 * @author csp
 * @date 2024/3/16 17:18
 * @description
 */
public interface OperationLogMapper {
    OperationLog getOpLog(String label,long version);
    int addOpLog(String label,long version, String op);
    int updateOpLog(String label,long version, String op);
}
