package com.nohi.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author duteliang
 */
@Component
@ConfigurationProperties(prefix = "nohi")
public class NoHiConfig {
    /**
     * 项目名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 版本
     */
    @Getter
    @Setter
    private String version;

    /**
     * 版权年份
     */
    @Getter
    @Setter
    private String copyrightYear;

    /**
     * 实例演示开关
     */
    @Getter
    @Setter
    private boolean demoEnabled;

    /**
     * 上传路径
     */
    private static String profile;

    /**
     * 获取地址开关
     */
    private static boolean addressEnabled;

    /**
     * 验证码类型
     */
    private static String captchaType;

    public static String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        NoHiConfig.profile = profile;
    }

    public static boolean isAddressEnabled() {
        return addressEnabled;
    }

    public void setAddressEnabled(boolean addressEnabled) {
        NoHiConfig.addressEnabled = addressEnabled;
    }

    public static String getCaptchaType() {
        return captchaType;
    }

    public void setCaptchaType(String captchaType) {
        NoHiConfig.captchaType = captchaType;
    }

    /**
     * 获取导入上传路径
     */
    public static String getImportPath() {
        return getProfile() + "/import";
    }

    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath() {
        return getProfile() + "/avatar";
    }

    /**
     * 获取下载路径
     */
    public static String getDownloadPath() {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getProfile() + "/upload";
    }
}
