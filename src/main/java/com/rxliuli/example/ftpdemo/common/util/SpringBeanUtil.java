package com.rxliuli.example.ftpdemo.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Spring Bean 相关的工具类
 * <p>
 * 注意：如果在 Spring 启动时即使用，请务必在类上使用 {@link org.springframework.context.annotation.DependsOn} 注解来引用 {@link SpringBeanUtil}，避免 Spring Bean 加载顺序引起的问题
 *
 * @author rxliuli
 */
@Component("springBeanUtil")
@Order(Integer.MIN_VALUE)
public class SpringBeanUtil {
    private static final Logger logger = LoggerFactory.getLogger(SpringBeanUtil.class);
    private static ApplicationContext context;
    private final ApplicationContext applicationContext;

    @Autowired
    public SpringBeanUtil(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据名字获取 Spring 中的 Bean
     *
     * @param name Bean 名称
     * @param <T>  获取到的 Bean 的类型
     * @return 返回获取到的 Bean, 发生异常则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return using(context -> (T) context.getBean(name));
    }

    //region 获取 Bean

    /**
     * 根据名字和类型获取 Spring 中的 Bean
     *
     * @param name         Bean 名称
     * @param requiredType Bean 的 Class 类型参数
     * @param <T>          获取到的 Bean 的类型
     * @return 返回获取到的 Bean, 发生异常则返回 null
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        return using(context -> context.getBean(name, requiredType));
    }

    /**
     * 根据名字和参数列表获取 Spring 中的 Bean
     *
     * @param name Bean 名称
     * @param args 创建新的 Bean 实例时使用的参数
     * @param <T>  获取到的 Bean 的类型
     * @return 返回获取到的 Bean, 发生异常则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name, Object... args) {
        return using(context -> (T) context.getBean(name, args));
    }

    /**
     * 根据类型获取 Spring 中的 Bean
     *
     * @param requiredType Bean 的 Class 类型参数
     * @param <T>          获取到的 Bean 的类型
     * @return 返回获取到的 Bean, 发生异常则返回 null
     */
    public static <T> T getBean(Class<T> requiredType) {
        return using(context -> context.getBean(requiredType));
    }

    /**
     * 根据类型和参数列表获取 Spring 中的 Bean
     *
     * @param requiredType Bean 的 Class 类型参数
     * @param args         创建新的 Bean 实例时使用的参数
     * @param <T>          获取到的 Bean 的类型
     * @return 返回获取到的 Bean, 发生异常则返回 null
     */
    public static <T> T getBean(Class<T> requiredType, Object... args) {
        return using(context -> context.getBean(requiredType, args));
    }

    /**
     * 根据 Bean 对象获取 Bean 的名字数组
     *
     * @param bean Bean 的对象
     * @param <T>  Bean 的类型
     * @return Bean 对象名字的数组(可能有多个)
     */
    public static <T> String[] getBeanNames(T bean) {
        return getBeanNames(bean.getClass());
    }

    /**
     * 根据 Bean 类型获取 Bean 的名字数组
     *
     * @param beanClass Bean 的类型对象
     * @param <T>       Bean 的类型
     * @return Bean 对象名字的数组(可能有多个)
     */
    public static <T> String[] getBeanNames(Class<T> beanClass) {
        return using(context -> context.getBeanNamesForType(beanClass));
    }

    /**
     * SpringBeanUtil 内部使用的统一捕获异常的方法
     *
     * @param function 执行的函数
     * @param <T>      执行的函数的返回值类型
     * @return 如果没有发生异常就返回执行函数的返回值, 否则返回 null
     */
    private static <T> T using(Function<ApplicationContext, T> function) {
        try {
            return function.apply(context);
        } catch (Exception e) {
            logger.error("An exception has occurred for use SpringBeanUtil get a Bean: ", e);
            return null;
        }
    }

    //endregion

    //region 注册 Bean

    /**
     * 动态注册 Bean
     * 具体实例由 Spring 自动生成，相当于你在类型上添加 {@link Component} 注解
     *
     * @param beanClass Bean 的类型对象
     * @param beanName  Bean 的名称
     * @param <T>       Bean 的类型
     * @return 注册成功的 Bean
     */
    public static <T> T registerBean(Class<T> beanClass, String beanName) {
        Objects.requireNonNull(beanClass);
        Objects.requireNonNull(beanName);
        //获取 BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        //创建 Bean 信息
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        //为 bean 设置参数
        //动态注册 Bean
        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        //noinspection unchecked
        return (T) context.getBean(beanName);
    }

    /**
     * 动态注册 Bean
     * 需要传入一个 bean 对象，相当于使用 {@link org.springframework.context.annotation.Bean} 注解注册的 Bean
     *
     * @param beanName Bean 的名称
     * @param <T>      Bean 的类型
     * @return 注册成功后的 Bean
     */
    public static <T> T registerBean(T bean, String beanName) {
        Objects.requireNonNull(bean);
        Objects.requireNonNull(beanName);
        //获取 BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        //为 bean 设置参数
        //动态注册 Bean
        defaultListableBeanFactory.registerSingleton(beanName, bean);
        //noinspection unchecked
        return (T) context.getBean(beanName);
    }

    //endregion

    //region 删除 Bean

    /**
     * 根据 Bean 的名称动态删除
     *
     * @param beanName Bean 的名称
     * @param <T>      Bean 的类型
     * @return 删除的 Bean 对象
     */
    public static <T> T removeBean(String beanName) {
        Objects.requireNonNull(beanName);
        //获取 BeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();
        //noinspection unchecked
        T bean = (T) context.getBean(beanName);
        //删除 Bean
        defaultListableBeanFactory.removeBeanDefinition(beanName);
        return bean;
    }

    /**
     * 根据父类类型获取所有子类
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    //endregion

    /**
     * 初始化静态的 context 变量
     */
    @PostConstruct
    public void init() {
        context = applicationContext;
    }
}
