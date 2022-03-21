package com.nohi.framework.config.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "xss")
public class XssProperties {

    /**
     * 防止XSS攻击
     */
    private boolean enabled;

    /**
     * excludes
     */
    private String excludes;

    /**
     * urlPatterns
     */
    private String urlPatterns;
}
