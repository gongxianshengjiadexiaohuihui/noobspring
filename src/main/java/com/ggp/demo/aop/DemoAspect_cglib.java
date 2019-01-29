package com.ggp.demo.aop;

import com.ggp.framework.annotation.aop.*;
import com.ggp.framework.annotation.mvc.NBComponent;

/**
 * @Author:ggp
 * @Date:2019/1/28 11 45
 * @Description:
 */
@NBAspect
@NBComponent
public class DemoAspect_cglib {
    @NBPointcut(value = "com.ggp.demo.service.impl.CglibTest.hi")
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
