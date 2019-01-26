package com.ggp.framework.servlet;

import com.ggp.common.util.StringUtil;
import com.ggp.framework.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;

/**
 * @Author:ggp
 * @Date:2019/1/21 16 04
 * @Description:
 */
public class NBDispatcherServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(this.getClass());



    private static final long serialVersionUID = 1L;
    /**
     * 配置文件的位置 classpath:application.properties
     */
    private static final String LOCATION = "contextConfigLocation";
    /**
     * 保存所有配置信息
     */
    private Properties p = new Properties();
    /**
     * 保存所有被扫描到的类名
     */
    private List<String> classNames = new ArrayList<String>();
    /**
     * IOC容器，保存所有初始化的bean
     */
    private Map<String,Object> ioc = new HashMap<String, Object>();
    /**
     * 保存 url和方法的映射关系
     */
    private Map<String,Method> handlerMapping = new HashMap<String, Method>();

    public NBDispatcherServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始初始化noobspring");
        /**
         * 加载配置文件 config来自父类
         */
        doLoadConfig(config.getInitParameter(LOCATION));
        /**
         * 扫描相关的类 （从配置文件中读取扫描包的范围）把扫描到的类名保存到className中
         */
        doScanner(p.getProperty("scanPackage"));
        /**
         * 通过反射初始化className中的所有类，保存到ioc容器中（ioc容器的key默认是类名字母小写）
         */
        doInstance();
        /**
         * 依赖注入
         */
        doAutowired();
        /**
         * 构造handlerMapping
         */
        initHandlerMapping();
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>初始化noobspring完成");
    }
    private void doLoadConfig(String path){
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>初始化配置文件");
        /**
         * 先把classpath:剥离，后续在添加内容
         */
        String[] str = path.split(":");
        InputStream fis = null;
        try {
            fis = this.getClass().getClassLoader().getResourceAsStream(str[1]);
            p.load(fis);
        }catch (Exception e){
            e.printStackTrace();
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>初始化配置文件失败");
        }finally {
            if(null != fis){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }

      logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>初始化配置文件成功");
    }
    private void doScanner(String packageName){
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始扫描类");
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File dir = new File(url.getFile());
        for(File file: dir.listFiles()){
            if(file.isDirectory()){
                doScanner(packageName + "." + file.getName());
            }else{
                classNames.add(packageName + "." + file.getName().replace(".class"," ").trim());
            }

        }
       logger.info("扫描到的类：{}",classNames.toString());
       logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>扫描成功");
    }
    private void doInstance(){
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始实例化");
        if(classNames.size() == 0){
            return;
        }
        try{
            for(String className: classNames){
                Class<?> clazz = Class.forName(className);
                if(clazz.isAnnotationPresent(NBController.class)){
                    /**
                     * 将类首字母小写
                     */
                    String beanName = StringUtil.lowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(NBService.class)){
                    /**
                     * 如果@NBService有value的话
                     */
                    String beanName = clazz.getAnnotation(NBService.class).value();
                    if(!"".equals(beanName.trim())){
                        ioc.put(StringUtil.lowerFirstCase(beanName), clazz.newInstance());
                        continue;
                    }
                    /**
                     * 如果@NBService的value为空，则根据其实现的接口创建实例
                     */
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for(Class<?> tamp : interfaces){
                        ioc.put(StringUtil.lowerFirstCase(tamp.getSimpleName()), clazz.newInstance());
                    }

                }else{
                    return;
                }
            }
        }catch (Exception e){
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>实例化失败");
            e.printStackTrace();
        }
        logger.info("实例化的类:{}",ioc.keySet().toString());
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>实例化成功");
    }
    private void doAutowired(){
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始注入");
        if(ioc.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry : ioc.entrySet()){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field : fields){
                if(!field.isAnnotationPresent(NBAutowired.class)){
                    continue;
                }
                String beanName = field.getAnnotation(NBAutowired.class).value();
                if("".equals(beanName)){
                    beanName = StringUtil.lowerFirstCase(field.getType().getSimpleName());

                }
                /**
                 * 在字段是私有变量的时候，也能获得访问权限
                 */
                field.setAccessible(true);

                try {
                    /**
                     * 给entry.getValue这个对象的这个字段field赋值（从ioc容器中拿到对应beanName的bean），请注意是对象
                     */
                    field.set(entry.getValue(),ioc.get(beanName));
                } catch (Exception e) {
                    logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>注入失败");
                    e.printStackTrace();
                }
            }
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>注入完成");
        }
    }
    private void initHandlerMapping(){
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>开始关联映射");
        if(ioc.isEmpty()){
            return;
        }
        for(Map.Entry<String,Object> entry: ioc.entrySet()){
            Class<?> clazz = entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(NBController.class)){
                continue;
            }
            String baseUrl = "";
            /**
             * 获得@Controller的父级的url
             */
            if(clazz.isAnnotationPresent(NBRequestMapping.class)){
                baseUrl = clazz.getAnnotation(NBRequestMapping.class).value();
            }
            Method[] methods = clazz.getMethods();
            for(Method method : methods){
                if(!method.isAnnotationPresent(NBRequestMapping.class)){
                    continue;
                }

                /**
                 * 将url和方法关联并存储在handlerMapping中(replaceAll和split支持正则表达式)
                 */
                String url = ("/" + baseUrl + "/" + method.getAnnotation(NBRequestMapping.class).value()).replaceAll("/+","/");
                handlerMapping.put(url,method);
                System.out.println("mapped" + url + "," + method);
            }
        }
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>关联映射完成");
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Detail:\r\n" +e.getMessage() + "\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","").replaceAll(",\\s","\r\n"));
            e.printStackTrace();

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        }catch (Exception e){
            resp.getWriter().write("500 Exception,Detail:\r\n" +e.getMessage() + "\r\n" + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","").replaceAll(",\\s","\r\n"));
            System.out.println(e.getMessage());
        }
    }
    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        if(handlerMapping.isEmpty()){
            return;
        }
        String url = req.getRequestURI();
        /**
         * 返回站点的根目录
         * 比如我们访问的地址是http://localhost:8080/demo/data/remove
         * demo是我们的项目  对应的contextPath就是http://localhost:8080/demo
         */
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");
        if(!handlerMapping.containsKey(url)){
            resp.getWriter().write("404 Not Found!");
            return;
        }
        /**
         * 获取请求参数列表
         */
        Map<String,String[]> params = req.getParameterMap();

        /**
         * 根据url拿到对应的方法
         */
        Method method = this.handlerMapping.get(url);
        /**
         * 获取方法的形参列表
         */
        Parameter[] parameters = method.getParameters();
        Annotation annotation[][] = method.getParameterAnnotations();
        /**
         * 保存参数值
         */
        Object[] paramValues = new Object[parameters.length];
        /**
         * 根据形参列表，按顺序获取请求参数列表的值
         */
        for(int i =0; i < parameters.length; i++){
            System.out.println(parameters[i].getType());
            if(parameters[i].getType() == HttpServletRequest.class){
                paramValues[i] = req;
                continue;
            }
            if(parameters[i].getType() == HttpServletResponse.class){
                paramValues[i] = resp;
                continue;
            }
            if(parameters[i].getType() == String.class){
                String name = parameters[i].getName();
                /**
                 * 按照@NBRequestParam的value 从请求参数找值
                 */
                if(annotation[i].length != 0 & annotation[i][0].annotationType() == NBRequestParam.class){
                    /**
                     * 类型转换，猜测里面存的值应该是一个注解的泛型
                     */
                    name = ((NBRequestParam)annotation[i][0]).value();
                    if(!params.containsKey(name)){
                        throw new RuntimeException("The required parameter does not exist!   " + name );
                    }
                }
                paramValues[i] = params.get(name)[0];
                continue;
            }

        }
        /**
         * 通过反射获取实体对象，并通过反射调用方法
         */
        String beanName = StringUtil.lowerFirstCase(method.getDeclaringClass().getSimpleName());
        method.invoke(this.ioc.get(beanName),paramValues);


    }

}
