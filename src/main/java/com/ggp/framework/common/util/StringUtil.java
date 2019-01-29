package com.ggp.framework.common.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

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

    /**
     * 从方法完整名字中获取方法名简称
     * @param name
     * @return
     */
    public static String getSimpleMethodName(String name){
        String[] str =  StringUtil.getMethodName(name).split("\\.");
        return str[str.length - 1];

    }

    /**
     * 设置切点，从实现方法中，设置接口方法 eg：com.ggp.demo.demoServiceImpl.hello(),->com.ggp.demo.demoService.hello();
     * @param name 方法名
     * @param points 切点集合
     * @return
     */
    public static HashSet setPoints(String name, HashSet points){
        /**
         * 把本身的作为切点方法添加进去
         */
        points.add(name);
        String className = StringUtil.getNameByMethod(name);
        String[] str = name.split("\\.");
        String  simpleName = str[str.length - 1];
        try {
            Class[] interfaces = Class.forName(className).getInterfaces();
            if(interfaces.length == 0){
                return points;
            }
            /**
             * 如果存在接口，就把接口相同的方法也添加到切点方法集合中
             */
            for(Class cl : interfaces){
                Method[] methods = cl.getMethods();
                for(Method method : methods){
                    if(StringUtil.getSimpleMethodName(method.toString()).equals(simpleName)){
                        points.add(StringUtil.getMethodName(method.toString()));
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return points;
    }

    public static void main(String[] args) {
        System.out.println(StringUtil.setPoints("com.ggp.demo.service.impl.DemoServiceImpl.genesis",new HashSet()).toString());
    }
}
