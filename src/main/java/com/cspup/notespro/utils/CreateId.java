package com.cspup.notespro.utils;

/**
 * @author csp
 * @date 2023/7/5 16:30
 * @description
 */
public class CreateId {

    private static final long startTimeStamp = 1648555089099L;
    private static long lastTimeStamp = -1L;
    private static long sequence = 0L;

    /**
     * 获得生成的id
     * @return 雪花算法生成的id
     */
    static public synchronized long next() {
        long currTimeStamp = System.currentTimeMillis();
        if (currTimeStamp < lastTimeStamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }
        if (currTimeStamp == lastTimeStamp) {
            sequence++;
            // 每毫秒支持4096个请求
            if (sequence>4096){
                currTimeStamp = getNextMill();
            }
        } else {
            sequence = 0L;

        }
        lastTimeStamp = currTimeStamp;
//        System.out.println(currTimeStamp - startTimeStamp+"  "+sequence);
//        System.out.println(currTimeStamp - startTimeStamp << 13 | sequence);
        // 左移13位，因为设置的最大序列为4096，左移之后的后13位为0000000000再按位或
        return currTimeStamp - startTimeStamp << 13 | sequence;
    }

    /**
     * 获取62位编码后的短id
     * @return 62位编码的短id
     */
    static public synchronized String nextId() {
        return encode62(next());
    }


    /**
     * 转换为62进制
     * @param num 数字
     * @return 结果
     */
    public static String encode62(long num) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int scale = 62;
        StringBuilder sb = new StringBuilder();
        int remainder;
        while (num > scale - 1) {
            //对 scale 进行求余，然后将余数追加至 sb 中，由于是从末位开始追加的，因此最后需要反转字符串
            remainder = Long.valueOf(num % scale).intValue();
            sb.append(chars.charAt(remainder));
            //除以进制数，获取下一个末尾数
            num = num / scale;
        }
        sb.append(chars.charAt(Long.valueOf(num).intValue()));
        return sb.reverse().toString();
    }

    private static long getNextMill() {
        long mill = System.currentTimeMillis();
        while (mill <= lastTimeStamp) {
            mill = System.currentTimeMillis();
        }
        return mill;
    }
}