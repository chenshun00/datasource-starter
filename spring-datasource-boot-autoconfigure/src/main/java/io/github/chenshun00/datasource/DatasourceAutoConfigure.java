package io.github.chenshun00.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.ServiceLoader;

/**
 * @author chenshun00@gmail.com
 * @since 2019/7/12
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ConfigurationProperties(prefix = "chenshun00.datasource")
@Data
public class DatasourceAutoConfigure implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String username;
    private String password;
    private String url;
    private String xy;
    private String dbInfoZk;
    private String driverClassName = "com.mysql.jdbc.Driver";
    private String type = "com.alibaba.druid.pool.DruidDataSource";
    private Integer minIdle = 5;
    private Integer initialSize = 5;
    private Integer maxActive = 10;
    private Integer maxWait = 60000;
    private String validationQuery = "SELECT 'x'";
    private Boolean testWhileIdle = true;
    private Boolean testOnBorrow = false;
    private Boolean testOnReturn = false;
    private String dbInfoRequestVersion;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean(initMethod = "init", destroyMethod = "close")
    @Primary
    public DataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(username);
        druidDataSource.setUrl(url);
        druidDataSource.setPassword(password);
        druidDataSource.setMinIdle(minIdle);
        druidDataSource.setInitialSize(initialSize);
        druidDataSource.setMaxActive(maxActive);
        druidDataSource.setMaxWait(maxWait);
        if (druidDataSource.getMaxWait() > -1) {
            druidDataSource.setUseUnfairLock(true);
        }
        druidDataSource.setPoolPreparedStatements(false);
        druidDataSource.setValidationQuery(validationQuery);
        druidDataSource.setTestWhileIdle(testWhileIdle);
        druidDataSource.setTestOnBorrow(testOnBorrow);
        druidDataSource.setTestOnReturn(testOnReturn);

        ServiceLoader<WrapService> wrapServices = ServiceLoader.load(WrapService.class);
        for (WrapService wrapService : wrapServices) {
            wrapService.wrap(druidDataSource);
        }
        return druidDataSource;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(dataSource(), "dataSource不能为空");
    }
}
