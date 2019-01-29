package com.ggp.framework.Proxy;

import com.ggp.demo.aop.DemoAspect_jdk;
import com.ggp.demo.service.DemoService;
import com.ggp.demo.service.impl.DemoServiceImpl;
import com.ggp.framework.annotation.aop.*;
import com.ggp.framework.common.util.StringUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author:ggp
 * @Date:2019/1/26 15 50
 * @Description:
 */
public class JDKProxy implements InvocationHandler {
    /**
     * 真实对象
     */
    private Object target;
    /**
     * 切面配置
     */
    private Object aspect;
    /**
     * 保存切点
     */
    private HashSet points ;
    /**
     * 插入方法
     */
    private Method before;
    private Method after;
    private Method afterReturn;
    private Method afterThrowing;
    /**
     * 返回代理对象
     * @param target
     * @param aspect
     * @return
     */
    public Object bind(Object target, Object aspect){
        this.target = target;
        this.aspect = aspect;
        points = new HashSet();
        Method[] methods = aspect.getClass().getMethods();
        for(Method method : methods){
            if(method.isAnnotationPresent(NBPointcut.class)){
              String tampName =  StringUtil.getMethodName(method.getAnnotation(NBPointcut.class).value());
              points = StringUtil.setPoints(tampName, points);

            } else if(method.isAnnotationPresent(NBBefore.class)){
                this.before = method;
            } else if(method.isAnnotationPresent(NBAfter.class)){
                this.after = method;
            } else if(method.isAnnotationPresent(NBAfterReturn.class)){
                this.afterReturn = method;
            } else if(method.isAnnotationPresent(NBAfterThrowing.class)){
                this.afterThrowing = method;
            } else {continue;}
        }
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),target.getClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = StringUtil.getMethodName(method.toString());
        Object obj;
        /**
         * 如果不是切点，不织入通知
         */
        if(!points.contains(methodName)){
            System.out.println(methodName);
            System.out.println(points.toString());
            obj = method.invoke(target,args);
            /**
             * 粗心在这里忘记返回了，导致逻辑继续执行
             */
            return  obj;
        }

        if(before != null){
            before.invoke(aspect,null);
        }
        try{
             obj = method.invoke(target,args);
        }catch (Exception e){
            if(afterThrowing != null){
                afterThrowing.invoke(aspect,null);
            }
            throw e;
        }finally {
            if(after != null){
                after.invoke(aspect,null);
            }
        }
        if(afterReturn != null){
            afterReturn.invoke(aspect,null);
        }
        return obj;
    }

    public static void main(String[] args) {
        JDKProxy proxy = new JDKProxy();
        DemoService demoService = (DemoService)proxy.bind(new DemoServiceImpl(),new DemoAspect_jdk());
        demoService.genesis("ggp");
    }

}
