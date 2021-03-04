package io.github.chenshun00.multi.view;

import io.github.chenshun00.multi.mapper.first.FirstMapper;
import io.github.chenshun00.multi.mapper.second.SecondMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author chenshun00@gmail.com
 * @since 2021/3/4 8:01 下午
 */
@RestController
public class TestCtrl {

    @Resource
    private FirstMapper firstMapper;
    @Resource
    private SecondMapper secondMapper;

    @GetMapping("gr")
    public Object getRequest() {
        System.out.println(firstMapper.first());
        System.out.println(secondMapper.second());
        return 1;
    }


}
