package com.thundax.analysis.config;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;

public class Configuration {
    // 读取config/LacAnalyzer.cfg.xml文件，获取service_url配置项 没有的话就是用默认值
    public static final String DEFAULT_SERVICE_URL = "http://localhost:8866/paddlehub/predict/lac";

    private final Settings settings;

    /**
     * Service URL
     */
    private final String serviceUrl;


    @Inject
    public Configuration(Settings settings) {
        this.settings = settings;
        this.serviceUrl = settings.get("service_url", DEFAULT_SERVICE_URL);
    }

    public Settings getSettings() {
        return settings;
    }

    public String getServiceUrl() {
        return this.serviceUrl;
    }

}
