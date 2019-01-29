package com.ggp.demo.service.impl;

import com.ggp.framework.annotation.mvc.NBComponent;

/**
 * @Author:ggp
 * @Date:2019/1/28 11 34
 * @Description:
 */
@NBComponent
public class CglibTest {
    public String  hi(String key){
        if("ggp".equals(key)){
            System.out.println("Hi");
            return "Hi";
        }
        else{
            return "The key is error";
        }
    }
}
