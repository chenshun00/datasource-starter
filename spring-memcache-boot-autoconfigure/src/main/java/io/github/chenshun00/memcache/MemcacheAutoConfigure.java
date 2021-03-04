package io.github.chenshun00.memcache;

import lombok.Data;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.buffer.SimpleBufferAllocator;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.utils.XMemcachedClientFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author chenshun00@gmail.com
 * @since 2019/7/12
 */
@Configuration
@ConfigurationProperties(prefix = "chenshun00.memcache")
@Data
public class MemcacheAutoConfigure {

    private Integer poolSize = 10;
    private String userName;
    private String password;
    private String url;
    private Integer port = 11211;

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(name = "chenshun00.memcache.url")
    public MemcachedClient memcachedClient() {
        XMemcachedClientFactoryBean clientFactoryBean = new XMemcachedClientFactoryBean();
        clientFactoryBean.setConnectionPoolSize(poolSize);
        Map<InetSocketAddress, AuthInfo> authInfoMap = new HashMap<>();
        if (userName == null || userName.length() == 0) {
            ;//没有密码的情况
        } else {
            Assert.notNull(url, "memcache url 不能为空");
            Assert.notNull(userName, "memcache userName 不能为空");
            Assert.notNull(password, "memcache password 不能为空");
            authInfoMap.put(new InetSocketAddress(url, port),
                    AuthInfo.plain(userName, password));
            clientFactoryBean.setAuthInfoMap(authInfoMap);
        }
        clientFactoryBean.setServers(url + ":" + port);
        clientFactoryBean.setCommandFactory(new net.rubyeye.xmemcached.command.BinaryCommandFactory());
        clientFactoryBean.setSessionLocator(new KetamaMemcachedSessionLocator());
        clientFactoryBean.setTranscoder(new SerializingTranscoder());
        clientFactoryBean.setBufferAllocator(new SimpleBufferAllocator());
        try {
            return (MemcachedClient) clientFactoryBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException("连接memcache失败:" + url + ":" + port, e);
        }
    }
}
