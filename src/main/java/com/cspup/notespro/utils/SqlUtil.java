package com.cspup.notespro.utils;

/**
 * @author csp
 * @date 2022/2/26 16:27
 * @description
 */
public class SqlUtil {

    public static String toSql(String sql,String param){
        return sql.replace("?","\""+param+"\"");
    }

    public static String toSql(String sql, int index, String param){
        int n = 0;
        for (int i=0;i<sql.length();i++){
           i = sql.indexOf("?",n++);
           if (n==index){
               return sql.replace("?",param);
           }
        }
        return sql;
    }
}
