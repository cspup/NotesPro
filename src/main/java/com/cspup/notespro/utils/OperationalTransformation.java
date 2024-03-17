package com.cspup.notespro.utils;

import com.cspup.notespro.delta.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.cspup.notespro.utils.Operation.Type.DELETE;
import static com.cspup.notespro.utils.Operation.Type.INSERT;

/**
 * @author csp
 * @date 2024/3/14 16:33
 * @description
 */

public class OperationalTransformation {


    public static void main(String[] args) throws JsonProcessingException {
//        var old = """
//                {
//                   "ops":  [
//                      {
//                        "insert": "1"
//                      },
//                      {
//                        "attributes": {
//                          "header": 1
//                        },
//                        "insert": "\\n"
//                      }
//                    ]
//                }
//                """;
//        var ops = """
//                {
//                    "ops":[
//                      {
//                        "retain": 1
//                      },
//                      {
//                        "insert": "2"
//                      }
//                    ]
//                }
//                """;
//
//        var expect = """
//                {
//                "ops":[
//                      {
//                        "insert": "12"
//                      },
//                      {
//                        "attributes": {
//                          "header": 1
//                        },
//                        "insert": "\\n"
//                      }
//                    ]
//                }
//                """;
//        ObjectMapper mapper = new ObjectMapper();
//        var oldDelta = mapper.readValue(old, Delta.class);
//        var opsDelta = mapper.readValue(ops, Delta.class);
//        oldDelta = oldDelta.compose(opsDelta);
////        delta.plainText();
//        System.out.println(oldDelta.plainText());
//
//        Integer version = 1;
        // 初始ops，hello world
//        OpList ops1 = new OpList();
//        ops1.add(Op.insert("hello world"));
//        Delta delta1 = new Delta(ops1);
////
//        // A的操作，预期将hello world变为hi world
//        OpList ops_a = new OpList();
//        ops_a.add(Op.retain(1));
//        ops_a.add(Op.delete(4));
//        ops_a.add(Op.insert("i"));
//        Delta delta2 = new Delta(ops_a);
//
//        // B的操作，预期将hello world变为nihao world
//        OpList ops_b = new OpList();
//        ops_b.add(Op.delete(5));
//        ops_b.add(Op.insert("nihao"));
//        Delta delta3 = new Delta(ops_b);
//
//        System.out.println(delta3);
//
//
//        // 腾讯文档的结果是inihao world
//        Delta delta = delta1.compose(delta2);
//        delta3 = delta2.transform(delta3, true);
//        System.out.println(delta.compose(delta3));
//
//        // 并发Delta完整的应用过程
//        // 原始文档origin
//        Delta origin = new Delta().insert("hello world");
//        // A用户的操作，将hello world改为hi world
//        Delta delta_a = new Delta().retain(1).delete(4).insert("i");
//        // A的版本号
//        Integer a_version = 1;
//        // B的操作，将hello world改为nihao world。
//        Delta delta_b = new Delta().delete(5).insert("nihao");
//        Delta tmp = delta_b;
//        delta_b = delta_a;
//        delta_a = tmp;
//
//        // B的版本号
//        Integer b_version = 1;
//
//
//        // 解决并发可能的冲突
//        if (a_version.equals(b_version)) {
//            origin = origin.compose(delta_a);
//            delta_b = delta_a.transform(delta_b, true);
//            origin = origin.compose(delta_b);
//        } else if (a_version > b_version) {
//            origin = origin.compose(delta_a);
//            origin = origin.compose(delta_b);
//        } else {
//            origin = origin.compose(delta_b);
//            origin = origin.compose(delta_a);
//        }
//        // 最终结果
//        System.out.println(origin);

        String json = """
                    {
                        "label": "888",
                        "version": "1",
                        "delta":{
                            "ops": [
                                    {
                                        "insert": "Hello World!"
                                    }
                                    ]
                                }
                    }
                """;
        ObjectMapper objectMapper = new ObjectMapper();
        NoteDelta noteDelta = objectMapper.readValue(json, NoteDelta.class);
        System.out.println(noteDelta);

        NoteDelta noteDelta1 = new NoteDelta("123",1,new Delta().insert("test test") );
        System.out.println(objectMapper.writeValueAsString(noteDelta1));

    }


}
