package com.cspup.notespro.utils;

/**
 * @author csp
 * @date 2023/7/6 14:03
 * @description
 */
public class ItemList {

    Item head;
    Item last;


    class Item{
        Object object;
        long logicTime;
        Item next;
        Item (Object obj,long l){
            object = obj;
            logicTime = l;
        }
    }

    public void append(Object obj, long logicTime){
        Item new_item = new Item(obj,logicTime);
        if (head == null) {
            head = new Item(obj,logicTime);
            last = head;
            return;
        }
        new_item.next = null;
        if (last.logicTime<new_item.logicTime){
            last.next = new_item;
            last = new_item;
        }else{
            throw new RuntimeException("The new logicTime is lower than last logicTime.");
        }

    }

    public Object getLastObject(){
        if (last!=null){
            return last.object;
        }else {
            return null;
        }

    }
}
