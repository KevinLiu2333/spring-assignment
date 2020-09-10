package com.lagou.edu.factory;

import com.alibaba.druid.util.StringUtils;
import com.lagou.edu.anno.Autowired;
import com.lagou.edu.anno.Service;
import com.lagou.edu.anno.Transactional;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 工厂类,生产对象(使用反射技术)
 * Created with IntelliJ IDEA.
 * User: kevliu3
 * Date: 2020/9/5
 * Time: 8:49 AM
 *
 * @author kevliu3
 */
public class BeanFactory {

    /**
     * 任务一:读取解析xml,通过反射技术实例化对象并存储待用(map)
     * 任务二:对外提供获取实例对象的接口(根据id获取)
     */
    private static Map<String, Object> map = new HashMap<>();

//    static {
//        //任务一:读取解析xml,通过反射技术实例化对象并且存储待用(map集合)
//        //加载xml
//        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
//
//        SAXReader saxReader = new SAXReader();
//        try {
//            Document document = saxReader.read(resourceAsStream);
//            Element rootElement = document.getRootElement();
//            //返回所有bean标签
//            List<Element> beanList = rootElement.selectNodes("//bean");
//            for (int i = 0; i < beanList.size(); i++) {
//                Element element = beanList.get(i);
//                //acountDao
//                String id = element.attributeValue("id");
//                //com.lagou.edu.dao.impl.JdbcAccountDaoImpl
//                String clazz = element.attributeValue("class");
//                //通过反射技术
//                Class<?> aClass = Class.forName(clazz);
//                Object o = aClass.newInstance();
//                //存到map中待用
//                map.put(id, o);
//
//            }
//
//            //实例化完成之后,检查哪些对象需要传值进去,根据他的配置传入相应的值
//            List<Element> propertyList = rootElement.selectNodes("//property");
//            //解析property,获取父元素
//            for (int i = 0; i < propertyList.size(); i++) {
//                Element element = propertyList.get(i);
//                String name = element.attributeValue("name");
//                String ref = element.attributeValue("ref");
//                //找到当前需要被处理依赖关系的bean--父元素
//                Element parent = element.getParent();
//                //调用父元素的反射功能
//                String parentId = parent.attributeValue("id");
//                Object parentObject = map.get(parentId);
//                //遍历父对象中的所有方法,找到"set" + name
//                Method[] methods = parentObject.getClass().getMethods();
//                for (Method method : methods) {
//                    //该方法是set方法
//                    if (method.getName().equalsIgnoreCase("set" + name)) {
//                        method.invoke(parentObject, map.get(ref));
//                    }
//                }
//
//                //把处理之后的parentObject重新放到map中
//                map.put(parentId, parentObject);
//            }
//
//        } catch (DocumentException | ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }

    static {
        //入参 要扫描的包名
        Reflections f = new Reflections("com.lagou.edu");
        //入参 目标注解类
        try {
            Set<Class<?>> serviceSet = f.getTypesAnnotatedWith(Service.class);
            for (Class<?> aClass : serviceSet) {
                Object o = aClass.newInstance();
                Service serviceAnno = aClass.getAnnotation(Service.class);
                String value = serviceAnno.value();
                String id = StringUtils.isEmpty(value) ? aClass.getSimpleName() : value;
                map.put(id, o);
            }
            for (Object value : map.values()) {
                //FIXME getAllfeilds
                for (Field field : value.getClass().getDeclaredFields()) {
                    if (field.getAnnotation(Autowired.class) != null) {
                        //FIXME do not set accessible
                        field.setAccessible(true);
                        field.set(value, map.get(field.getName()));
                    }
                }
            }
            ProxyFactory proxyFactory = (ProxyFactory) map.get("proxyFactory");
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Class<?> clazz = entry.getValue().getClass();
                if (clazz.getAnnotation(Transactional.class) != null) {
                    Object proxy = null;
                    if (clazz.getInterfaces().length == 0) {
                        proxy = proxyFactory.getCglibProxy(entry.getValue());
                    } else {
                        proxy = proxyFactory.getJdkProxy(entry.getValue());

                    }
                    map.put(entry.getKey(), proxy);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        //入参 要扫描的包名
//        Reflections f = new Reflections("com.lagou.edu");
//        //入参 目标注解类
//        try {
//            Set<Class<?>> serviceSet = f.getTypesAnnotatedWith(Service.class);
//            for (Class<?> aClass : serviceSet) {
//                Object o = aClass.newInstance();
//                Service serviceAnno = aClass.getAnnotation(Service.class);
//                String value = serviceAnno.value();
//                String id = StringUtils.isEmpty(value) ? aClass.getSimpleName() : value;
//                map.put(id, o);
//            }
//            for (Object value : map.values()) {
//                //FIXME getAllfeilds
//                for (Field field : value.getClass().getDeclaredFields()) {
//                    if (field.getAnnotation(Autowired.class) != null) {
//                        //FIXME do not set accessible
//                        field.setAccessible(true);
//                        field.set(value, map.get(field.getName()));
//                    }
//                }
//            }
//            ProxyFactory proxyFactory = (ProxyFactory) map.get("proxyFactory");
//            for (Map.Entry<String, Object> entry : map.entrySet()) {
//                Class<?> clazz = entry.getValue().getClass();
//                if (clazz.getAnnotation(Transactional.class) != null) {
//                    Object proxy = null;
//                    if (clazz.getInterfaces().length == 0) {
//                        proxy = proxyFactory.getCglibProxy(entry.getValue());
//                    } else {
//                        proxy = proxyFactory.getJdkProxy(entry.getValue());
//
//                    }
//                    map.put(entry.getKey(), proxy);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //任务二:对外提供获取实例对象接口(根据id获取)
    public static Object getBean(String id) {
        return map.get(id);
    }
}
