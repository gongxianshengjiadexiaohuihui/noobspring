package com.ggp.common.util;

/**
 * @Author:ggp
 * @Date:2019/1/22 10 27
 * @Description:
 */
public class StringUtil {
    public static String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
