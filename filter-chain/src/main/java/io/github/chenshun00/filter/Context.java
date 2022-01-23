package io.github.chenshun00.filter;

/**
 * @author chenshun00@gmail.com
 * @since 2022/1/23 2:08 下午
 */
public interface Context {

    String get(String key);

    String get(String key, String def);

}
