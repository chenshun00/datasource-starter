package io.github.chenshun00.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author chenshun00@gmail.com
 * @since 2019/7/13
 */
public class DatasourceAutoLogListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        final Logger logger = LoggerFactory.getLogger(getClass());

        String bannerText = buildBannerText();

        if (logger.isInfoEnabled()) {
            logger.info(bannerText);
        } else {
            System.out.print(bannerText);
        }

    }


    private String buildBannerText() {
        return LINE_SEPARATOR +
                LINE_SEPARATOR +
                " :: Spring Boot Datasource" + ") : " +
                "chenshun00.github.io" +
                LINE_SEPARATOR +
                " :: Datasource ::" +
                "chenshun00.github.io" +
                LINE_SEPARATOR;

    }
}
