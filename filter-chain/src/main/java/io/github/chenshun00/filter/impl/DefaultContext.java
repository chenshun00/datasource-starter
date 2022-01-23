package io.github.chenshun00.filter.impl;

import io.github.chenshun00.filter.Context;

import java.util.Map;

/**
 * @author chenshun00@gmail.com
 * @since 2022/1/23 2:23 下午
 */
public class DefaultContext implements Context {

    private final Map<String, String> results;

    public DefaultContext(Map<String, String> results) {
        this.results = results;
    }

    @Override
    public String get(String key) {
        return results.get(key);
    }

    @Override
    public String get(String key, String def) {
        return results.getOrDefault(key, def);
    }
}
