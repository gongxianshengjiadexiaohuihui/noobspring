package com.ggp.framework.common.util;

import java.util.Arrays;

/**
 * @Author:ggp
 * @Date:2019/1/22 10 27
 * @Description:
 */
public class StringUtil {
    /**
     * 把首字母由大写变小写
     * @param str
     * @return
     */
    public static String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 获取方法的全限定名
     * @param name
     * @return
     */
    public static String getMethodName(String name){
        String[] str_1 = name.split("\\(");
        String[] str_2 = str_1[0].split(" ");
        return str_2[str_2.length - 1];
    }

    /**
     * 从方法的全限定命中获取类名
     * @param name
     * @return
     */
    public static String getNameByMethod(String name){
        String[] str_1 = name.split("\\.");
        System.out.println(str_1.length);
        if(str_1.length == 1){
            throw new RuntimeException("无法从方法的全限定名中获取类名，格式有错：" + name);
        }
        String[] str_2 = new String[str_1.length - 1];
        System.arraycopy(str_1,0,str_2,0,str_2.length);
        StringBuilder str = new StringBuilder();
        for(int i = 0 ; i < str_2.length - 1; i++){
            str.append(str_2[i]);
            str.append(".");
        }

        str.append(str_2[str_2.length - 1]);
        return str.toString();

    }

}
