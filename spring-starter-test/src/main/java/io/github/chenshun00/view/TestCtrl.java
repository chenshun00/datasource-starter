package io.github.chenshun00.view;

import io.github.chenshun00.mapper.TestMapper;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeoutException;

/**
 * @author chenshun00@gmail.com
 * @since 2021/3/4 7:50 下午
 */
@RestController
public class TestCtrl {

    @Resource
    private TestMapper testMapper;
    @Resource
    private MemcachedClient memcachedClient;

    @GetMapping("gr")
    public Object getRequest() throws InterruptedException, MemcachedException, TimeoutException {
        final Integer test = testMapper.test();
        System.out.println(test);
        memcachedClient.set("test", 100, "12312");
        return 1;
    }

}
