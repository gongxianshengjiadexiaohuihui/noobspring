package com.ggp.demo.controller;

import com.ggp.demo.service.DemoService;
import com.ggp.demo.service.impl.CglibTest;
import com.ggp.framework.annotation.mvc.NBAutowired;
import com.ggp.framework.annotation.mvc.NBController;
import com.ggp.framework.annotation.mvc.NBRequestMapping;
import com.ggp.framework.annotation.mvc.NBRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author:ggp
 * @Date:2019/1/21 16 04
 * @Description:
 */
@NBController
@NBRequestMapping("/genesis")
public class GenesisController {

    @NBAutowired
    private DemoService demoService;
    @NBAutowired
    private CglibTest cglibTest;

    @NBRequestMapping("/hello")
    public void hello(HttpServletRequest req, HttpServletResponse resp, @NBRequestParam("key") String value)throws Exception{
        String result = demoService.genesis(value);
        resp.getWriter().write(result);

    }
    @NBRequestMapping("/hi")
    public void hi(HttpServletRequest req, HttpServletResponse resp, @NBRequestParam("key") String value)throws Exception{
        String result = cglibTest.hi(value);
        resp.getWriter().write(result);

    }

}
