package io.github.chenshun00.multi.datasource.export;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动导入实例
 *
 * @author chenshun00@gmail.com
 * @since 2021/1/10 8:19 下午
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({MultiDatasourceRegister.class})
public @interface EnableMultiDatasource {
}
