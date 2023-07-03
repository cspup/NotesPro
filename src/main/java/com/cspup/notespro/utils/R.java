package com.cspup.notespro.utils;

/**
 * @author csp
 * @date 2023/7/3 10:16
 * @description Result
 */
public class  R<T> {
    private int status;
    private String message;
    private T data;

    public R (int status,String message,T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> R<T> ok(){
        return new R<>(200,"",null);
    }
    public static <T> R<T> ok(String message){
        return new R<>(200, message,null);
    }
    public static <T> R<T> ok(T data){
        return new R<>(200,"ok",data);
    }

    public static <T> R<T> failed(int status,String message){
        return new R<>(status,message,null);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
