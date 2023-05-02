package com.cspup.notespro.service;

import com.cspup.notespro.entity.Note;
import com.cspup.notespro.mapper.NoteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @author csp
 * @date 2022/2/22 20:09
 * @description
 */
@Service
public class LabelService {
    @Autowired
    NoteMapper noteMapper;
    public String generateLabel(){
        Random random = new Random(System.currentTimeMillis());
        String label = String.valueOf(random.nextInt(10000));
        return label;
    }
}
