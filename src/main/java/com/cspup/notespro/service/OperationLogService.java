package com.cspup.notespro.service;

import com.cspup.notespro.delta.Delta;
import com.cspup.notespro.delta.Op;
import com.cspup.notespro.entity.OperationLog;
import com.cspup.notespro.mapper.OperationLogMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.swing.*;

/**
 * @author csp
 * @date 2024/3/16 17:20
 * @description
 */

@Service
@Slf4j
public class OperationLogService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    OperationLogMapper operationLogMapper;

    public OperationLog getOperationLog(String label, long version) {
        return operationLogMapper.getOpLog(label, version);
    }

    /**
     * 根据标签号和版本号历史操作，在执行该操作后会将版本变为下一个版本
     *
     * @param label   编号
     * @param version 版本号
     * @return Delta
     */
    public Delta getDelta(String label, long version) {
        OperationLog operationLog = operationLogMapper.getOpLog(label, version);
        Delta delta;
        try {
            delta = objectMapper.readValue(operationLog.getOp(), Delta.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return delta;
    }

    public int addOpLog(String label, long version, String op) {
        return operationLogMapper.addOpLog(label, version, op);
    }

    public int updateOPLog(String label, long version, String op) {
        return operationLogMapper.updateOpLog(label, version, op);
    }
}
