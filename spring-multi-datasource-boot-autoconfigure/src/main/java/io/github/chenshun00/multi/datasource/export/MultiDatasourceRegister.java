package io.github.chenshun00.multi.datasource.export;

import com.alibaba.druid.pool.DruidDataSource;
import io.github.chenshun00.multi.datasource.DataSourcePropertyBean;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 注入 {@link BeanDefinition},分别注入了
 *
 * <ul>
 *     <li>{@link DruidDataSource}</li>
 *     <li>{@link SqlSessionFactoryBean}</li>
 *     <li>{@link DataSourceTransactionManager}</li>
 *     <li>{@link SqlSessionTemplate}</li>
 *     <li>{@link MapperScannerConfigurer}</li>
 * </ul>
 *
 * @author chenshun00@gmail.com
 * @see EnableMultiDatasource
 * @since 2021/1/10 8:20 下午
 */
public class MultiDatasourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private final static Logger logger = LoggerFactory.getLogger(MultiDatasourceRegister.class);

    private Environment environment;

    /**
     * 注入 {@link BeanDefinition}
     * <p>
     * 分别注入了5个class，根据配置的数据源的数目，最终生成的实例个数 = 数据源数目 * 5
     * <p>
     * 可以使用 {@link DefaultListableBeanFactory#registerSingleton(String, Object)} 和 {@link DefaultListableBeanFactory#registerBeanDefinition(String, BeanDefinition)}
     * 来对实例进行注入，这里我们选择了后者,其中一个原因详见链接
     * <p>
     * 另外一个原因就是 {@link DruidDataSource} 需要配置 {@link DruidDataSource#init()} 和 {@link DruidDataSource#close()} ,通过 {@link DefaultListableBeanFactory#registerSingleton(String, Object)}
     * 是不能配置 <strong>init()方法</strong> 和 <strong>close()方法</strong> 的
     *
     * @see <a href="https://github.com/chanjarster/spring-boot-all-callbacks/issues/1">请问@ConfigurationProperties的加载顺序在什么位置？ #1</a>
     * @see MapperScannerConfigurer
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        Assert.notNull(environment, "environment 不能为空");

        final AnnotationAttributes annotationAttributes =
                AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableMultiDatasource.class.getName(), false));

        Class platformTransactionManagerClass = DataSourceTransactionManager.class;
        if (annotationAttributes != null) {
            platformTransactionManagerClass = annotationAttributes.getClass("value");
        }

        final Map<String, DataSourcePropertyBean.PropertyBean> datasourceProperties
                = Binder.get(environment).bindOrCreate("chenshun00.multi.datasource", DataSourcePropertyBean.class).getDatasourceProperties();

        for (Map.Entry<String, DataSourcePropertyBean.PropertyBean> entry : datasourceProperties.entrySet()) {
            final String datasourceName = entry.getKey();
            final DataSourcePropertyBean.PropertyBean dataSourceProperty = entry.getValue();

            {
                BeanDefinitionBuilder datasource = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class);
                datasource.setInitMethodName("init");
                datasource.setDestroyMethodName("close");
                registry.registerBeanDefinition(datasourceName, datasource.getBeanDefinition());
            }

            {
                final BeanDefinitionBuilder sqlSessionFactory = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionFactoryBean.class);
                sqlSessionFactory.addPropertyReference("dataSource", datasourceName);
                final Resource[] resources;
                try {
                    resources = new PathMatchingResourcePatternResolver()
                            .getResources(Objects.requireNonNull(dataSourceProperty.getLocationPattern(),
                                    "mybatis Mapper.xml扫描路径不能为空"));
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage());
                }
                logger.info("[数据源:{} 加载resource文件][个数:{}]", datasourceName, resources.length);
                for (Resource resource : resources) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("数据源:{} ,加载文件:{}", datasourceName, resource.toString());
                    }
                }
                sqlSessionFactory.addPropertyValue("mapperLocations", resources);
                registry.registerBeanDefinition(String.format("%s%s", datasourceName, "SqlSessionFactory"), sqlSessionFactory.getBeanDefinition());
            }

            {
                final BeanDefinitionBuilder platformTransactionManager = BeanDefinitionBuilder.genericBeanDefinition(platformTransactionManagerClass);
                platformTransactionManager.addConstructorArgReference(datasourceName);
                registry.registerBeanDefinition(String.format("%s%s", datasourceName, "PlatformTransactionManager"), platformTransactionManager.getBeanDefinition());
            }

            {
                final BeanDefinitionBuilder sqlSessionTemplate = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionTemplate.class);
                sqlSessionTemplate.addConstructorArgReference(String.format("%s%s", datasourceName, "SqlSessionFactory"));
                registry.registerBeanDefinition(String.format("%s%s", datasourceName, "SqlSessionTemplate"), sqlSessionTemplate.getBeanDefinition());
            }

            {
                final String basePackages = Objects.requireNonNull(dataSourceProperty.getBasePackages(), "basePackages 不能为空");
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
                builder.addPropertyValue("sqlSessionTemplateBeanName", datasourceName + "SqlSessionTemplate");
                builder.addPropertyValue("basePackage", Collections.singletonList(basePackages));
                builder.setLazyInit(false);
                registry.registerBeanDefinition(String.format("%s%s", datasourceName, "MapperScannerConfigurer"),
                        builder.getBeanDefinition());
            }
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
