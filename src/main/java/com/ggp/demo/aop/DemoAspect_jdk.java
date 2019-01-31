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
public class DemoAspect_jdk {

    @NBPointcut(value = "com.ggp.demo.service.impl.DemoServiceImpl.genesis")
    public void print(){

    };
    @NBBefore
    public void before(String key){
        System.out.println("aspect>>>>>>>>before" + key);
    }
    @NBAfter
    public void after(){
        System.out.println("aspect>>>>>>>>after");
    };
    @NBAfterReturn
    public void afterReturning(){
        System.out.println("aspect>>>>>>>>afterReturning");
    }
    @NBAfterThrowing
    public void afterThrowing(){
        System.out.println("aspect>>>>>>>>afterThrowing");
    }
}
