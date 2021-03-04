package io.github.chenshun00;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenshun00@gmail.com
 * @since 2021/3/4 7:38 下午
 */
@SpringBootApplication
@MapperScan("io.github.chenshun00")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class);
    }

}
