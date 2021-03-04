package io.github.chenshun00.multi.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Map;
import java.util.Objects;

/**
 * @author chenshun00@gmail.com
 * @see BeanPostProcessor
 * @since 2021/1/10 12:38 下午
 */
public class MultiDatasourceBeanFactoryPostProcessor implements BeanPostProcessor, EnvironmentAware, BeanFactoryAware {

    public static final Logger logger = LoggerFactory.getLogger(MultiDatasourceBeanFactoryPostProcessor.class);

    private static final String PREFIX = "chenshun00.multi.datasource";

    private Map<String, DataSourcePropertyBean.PropertyBean> datasourceProperties;

    private BeanFactory beanFactory;

    /**
     * Apply this {@code BeanPostProcessor} to the given new bean instance <i>before</i> any bean
     * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
     * or a custom init-method). The bean will already be populated with property values.
     * The returned bean instance may be a wrapper around the original.
     * <p>The default implementation returns the given {@code bean} as-is.
     *
     * @param bean     the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use, either the original or a wrapped one;
     * if {@code null}, no subsequent BeanPostProcessors will be invoked
     * @throws BeansException in case of errors
     * @see InitializingBean#afterPropertiesSet
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) bean;
            druidDataSource.setName(beanName);
            final DataSourcePropertyBean.PropertyBean propertyBean = datasourceProperties.get(beanName);
            if (Objects.isNull(propertyBean)) {
                throw new IllegalStateException(String.format("根据【%s】获取datasource失败", beanName));
            }
            druidDataSource.setUsername(propertyBean.getUsername());
            druidDataSource.setUrl(propertyBean.getUrl());
            druidDataSource.setPassword(propertyBean.getPassword());
            druidDataSource.setMinIdle(propertyBean.getMinIdle());
            druidDataSource.setInitialSize(propertyBean.getInitialSize());
            druidDataSource.setMaxActive(propertyBean.getMaxActive());
            druidDataSource.setMaxWait(propertyBean.getMaxActive());
            if (druidDataSource.getMaxWait() > -1) {
                druidDataSource.setUseUnfairLock(true);
            }
            druidDataSource.setPoolPreparedStatements(false);
            druidDataSource.setValidationQuery(propertyBean.getValidationQuery());
            druidDataSource.setTestWhileIdle(propertyBean.getTestWhileIdle());
            druidDataSource.setTestOnBorrow(propertyBean.getTestOnBorrow());
            druidDataSource.setTestOnReturn(propertyBean.getTestOnReturn());
            return druidDataSource;
        }
        return bean;
    }

    /**
     * Callback that supplies the owning factory to a bean instance.
     * <p>Invoked after the population of normal bean properties
     * but before an initialization callback such as
     * {@link InitializingBean#afterPropertiesSet()} or a custom init-method.
     *
     * @param beanFactory owning BeanFactory (never {@code null}).
     *                    The bean can immediately call methods on the factory.
     * @throws BeansException in case of initialization errors
     * @see BeanInitializationException
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        datasourceProperties = Binder.get(environment).bindOrCreate(PREFIX,
                DataSourcePropertyBean.class).getDatasourceProperties();
    }
}
