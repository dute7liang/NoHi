package com.nohi.web.core.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "swagger")
@Data
public class SwaggerBean {

    /**
     * 是否开启swagger
     */
    private boolean enabled;

    /**
     * 设置请求的统一前缀
     */
    private String pathMapping;
}
