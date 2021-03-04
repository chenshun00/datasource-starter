package io.github.chenshun00.datasource;

import javax.sql.DataSource;

/**
 * @author chenshun00@gmail.com
 * @since 2021/3/4 5:08 下午
 */
public interface WrapService {
    void wrap(DataSource druidDataSource);
}
