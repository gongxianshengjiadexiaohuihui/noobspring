package com.ggp.demo.aop;

import com.ggp.framework.annotation.aop.*;
import com.ggp.framework.annotation.mvc.NBComponent;

/**
 * @Author:ggp
 * @Date:2019/1/26 14 54
 * @Description:
 */
@NBAspect
@NBComponent
public class DemoAspect {

    @NBPointcut(value = "com.ggp.demo.service.impl.DemoServiceImpl.genesis")
    public void print(){

    };
    @NBBefore(value = "print")
    public void before(){
        System.out.println("aspect>>>>>>>>before");
    }
    @NBAfter(value = "print")
    public void after(){
        System.out.println("aspect>>>>>>>>after");
    };
    @NBAfterReturn(value = "print")
    public void afterReturning(){
        System.out.println("aspect>>>>>>>>afterReturning");
    }
    @NBAfterThrowing(value = "print")
    public void afterThrowing(){
        System.out.println("aspect>>>>>>>>afterThrowing");
    }
}
