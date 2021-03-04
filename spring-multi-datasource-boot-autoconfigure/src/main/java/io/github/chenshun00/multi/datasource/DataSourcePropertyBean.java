package io.github.chenshun00.multi.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * 这里是不需要的,添加的原因也很简单,只是为了给使用者一个自动提示而已
 *
 * @author chenshun00@gmail.com
 * @since 2021/1/10 10:20 上午
 */
@ConfigurationProperties(prefix = "chenshun00.multi.datasource")
public class DataSourcePropertyBean {

    private final Map<String, PropertyBean> datasourceProperties = new HashMap<>();

    public Map<String, PropertyBean> getDatasourceProperties() {
        return datasourceProperties;
    }

    @Getter
    @Setter
    public static class PropertyBean {
        /**
         * @see DruidDataSource#getUsername()
         */
        private String username;
        /**
         * @see DruidDataSource#getPassword()
         */
        private String password;
        /**
         * @see DruidDataSource#getUrl()
         */
        private String url;
        /**
         * @see DruidDataSource#getDriverClassName()
         */
        private String driverClassName = "com.mysql.jdbc.Driver";
        /**
         * @see DruidDataSource#getMinIdle()
         */
        private Integer minIdle = 5;
        /**
         * @see DruidDataSource#getInitialSize()
         */
        private Integer initialSize = 5;
        /**
         * @see DruidDataSource#getMaxActive()
         */
        private Integer maxActive = 10;
        /**
         * @see DruidDataSource#getMaxWait()
         */
        private Integer maxWait = 60000;
        /**
         * @see DruidDataSource#getValidationQuery()
         */
        private String validationQuery = "SELECT 'x'";
        /**
         * @see DruidDataSource#isTestWhileIdle()
         */
        private Boolean testWhileIdle = true;
        /**
         * @see DruidDataSource#isTestOnBorrow()
         */
        private Boolean testOnBorrow = false;
        /**
         * @see DruidDataSource#isTestOnReturn()
         */
        private Boolean testOnReturn = false;
        /**
         * 选择dbInfoRequest的zk地址，默认是不需要的
         */
        private String dbInfoZk;
        /**
         * mybatis mapper文件扫描路径
         * 例如:classpath*:/mapper/dms/*Mapper.xml
         */
        private String locationPattern;

        /**
         * @see MapperScan#basePackages()
         */
        private String basePackages;
    }
}
