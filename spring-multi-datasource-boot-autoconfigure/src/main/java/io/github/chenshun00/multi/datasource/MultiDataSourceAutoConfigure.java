package io.github.chenshun00.multi.datasource;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;

/**
 * @author chenshun00@gmail.com
 * @since 2021/3/4 8:10 下午
 */
public class MultiDataSourceAutoConfigure {

    @Bean
    public static BeanPostProcessor multiDataSourcePostProcessor() {
        return new MultiDatasourceBeanFactoryPostProcessor();
    }

}
