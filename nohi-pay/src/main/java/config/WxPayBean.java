package config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * <p>created on 2022/1/28 18:18</p>
 * @author dute7liang
 */
@Configuration
@ConfigurationProperties(prefix = "wx.pay")
@Getter
@Setter
public class WxPayBean {
    private String appId;
    private String appSecret;
    private String mchId;
    private String partnerKey;
    private String certPath;
    private String domain;
    private String notifyUrl;

    @Override
    public String toString() {
        return "WxPayBean [appId=" + appId + ", appSecret=" + appSecret + ", mchId=" + mchId + ", partnerKey="
                + partnerKey + ", certPath=" + certPath + ", domain=" + domain + "]";
    }
}
