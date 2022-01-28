package config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 * <p>created on 2022/1/24 18:53</p>
 * @author dute7liang
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ali.pay")
@Getter
@Setter
public class AliPayBean {

    private String appId;
    private String privateKey;
    private String publicKey;
    private String appCertPath;
    private String aliPayCertPath;
    private String aliPayRootCertPath;
    private String notifyUrl;
    private String domain;

}
