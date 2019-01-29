package com.ggp.framework.Proxy;

import com.ggp.framework.annotation.aop.*;
import com.ggp.framework.common.util.StringUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author:ggp
 * @Date:2019/1/28 09 43
 * @Description:
 */
public class CglibProxy implements MethodInterceptor {
    /**
     * 切面配置
     */
    private Object aspect;
    /**
     * 保存切点
     */
    private Set points ;
    /**
     * 插入方法
     */
    private Method before;
    private Method after;
    private Method afterReturn;
    private Method afterThrowing;

    /**
     * 生成cglib代理对象
     * @param cl
     * @param aspect
     * @return
     */
    public Object getProxy(Class cl, Object aspect){
        /**
         * 初始化通知
         */
        this.aspect = aspect;
        points = new HashSet();
        Method[] methods = aspect.getClass().getMethods();
        for(Method method : methods){
            if(method.isAnnotationPresent(NBPointcut.class)){
                points.add(StringUtil.getMethodName(method.getAnnotation(NBPointcut.class).value()));
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
        /**
         * 创建增强类对象
         */
        Enhancer enhancer = new Enhancer();
        /**
         * 设置超类（让代理类成为目标类的子类）
         */
        enhancer.setSuperclass(cl);
        /**
         * 定义代理逻辑对象为当前对象，要求当前对象实现MethodInterceptor接口
         */
        enhancer.setCallback(this);
        /**
         * 生成并返回代理对象
         */
        return enhancer.create();
    }

    /**
     * 代理逻辑方法
     * @param o 代理对象
     * @param method 方法
     * @param objects 方法参数
     * @param methodProxy 方法代理
     * @return
     * @throws Throwable
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String methodName = StringUtil.getMethodName(method.toString());
        Object obj;
        /**
         * 如果不是切点，不织入通知
         */
        if(!points.contains(methodName)){
           obj = methodProxy.invokeSuper(o,objects);
           return obj;
        }

        if(before != null){
            before.invoke(aspect,null);
        }
        try{
            obj = methodProxy.invokeSuper(o,objects);
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

}
