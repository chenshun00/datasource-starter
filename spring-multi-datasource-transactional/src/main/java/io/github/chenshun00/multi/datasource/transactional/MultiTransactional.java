package io.github.chenshun00.multi.datasource.transactional;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author luobo.cs@raycloud.com
 * @since 2021/6/6 11:16 下午
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface MultiTransactional {

    String[] values() default {};

}
