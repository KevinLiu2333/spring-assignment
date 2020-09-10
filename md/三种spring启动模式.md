### 1.1BeanFactory和ApplicationContext的关系
BeanFactory是IOC顶层接口,只用来定义一些基础功能.基础规范.
ApplicationContext是他的一个子接口,所以Application包含BeanFactory提供的全部功能
通常称BeanFactory是基础容器,ApplicationContext是高级接口,比BeanFactory拥有更多功能,比如国际化支持和资源访问(xml,java配置类)等等
### 1.2实例化bean的三种方式
 springIOC实例化bean的三种方式
 方式一:使用无参构造(推荐)  
<bean id="connectionUtils" class="com.lagou.edu.utils.ConnectionUtils"/>
 另外两种方式:为了让new的对象加入springIOC容器管理中  
方式二:静态方法  
<bean id="connectionUtils" class="com.lagou.edu.factory.CreateBeanFactory" factory-method="getInstanceStatic"/>
方式三:实例化方法  
  <bean id="createBeanFactory" class="com.lagou.edu.factory.CreateBeanFactory"/>
<bean id="connectionUtils" factory-bean="createBeanFactory" factory-method="getInstance"></bean>

* bean的作用范围和生命周期  
  singleton  
  prototype
* DI
### xml与注解相结合
1. 实际企业开发中,纯xml已经很少了
2. 不需要引入额外的jar
3. xml+注解相结合的模式中,xml文件依然存在,spring仍然从xml开始启动
xml: 第三方jar包的bean
注解: 自己开发的bean

### 纯注解模式
配置类启动,不包含任何xml
