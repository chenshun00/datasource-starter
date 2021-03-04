package io.github.chenshun00.multi;

import io.github.chenshun00.multi.datasource.export.EnableMultiDatasource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chenshun00@gmail.com
 * @since 2021/3/4 7:56 下午
 */
@SpringBootApplication
@EnableMultiDatasource
public class MultiWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiWebApplication.class);
    }

}
