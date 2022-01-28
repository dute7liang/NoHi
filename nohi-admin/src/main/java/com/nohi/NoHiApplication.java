package com.nohi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author nohi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class NoHiApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(NoHiApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  NoHi启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}
