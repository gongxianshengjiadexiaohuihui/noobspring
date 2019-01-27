package com.ggp.demo.service.impl;

import com.ggp.demo.service.DemoService;
import com.ggp.framework.annotation.mvc.NBService;

/**
 * @Author:ggp
 * @Date:2019/1/21 16 04
 * @Description:
 */
@NBService
public class DemoServiceImpl implements DemoService {
    public String genesis(String key) {
        if("ggp".equals(key)) {
            return "Hello world";
        }else{
            return "The key is error";
        }
    }
}
