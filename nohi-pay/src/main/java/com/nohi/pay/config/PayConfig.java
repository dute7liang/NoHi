package com.nohi.pay.config;

import com.egzosn.pay.ali.api.AliPayConfigStorage;
import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.common.util.sign.SignUtils;
import com.egzosn.pay.wx.api.WxPayConfigStorage;
import com.egzosn.pay.wx.api.WxPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * <p>created on 2022/1/24 18:50</p>
 * @author dute7liang
 */
@Configuration
public class PayConfig {

	@Autowired
	private AliPayBean aliPayBean;
	@Autowired
	private WxPayBean wxPayBean;

	private static final String ALI_NOTIFY_URL = "/app/notify/ali";
	private static final String WX_NOTIFY_URL = "/app/notify/wx";

	@Bean
	public AliPayService aliPayService(){
		AliPayConfigStorage aliPayConfigStorage = new AliPayConfigStorage();
//		aliPayConfigStorage.setPid(aliPayBean.get());
		aliPayConfigStorage.setAppid(aliPayBean.getAppId());
		//设置为证书方式
		aliPayConfigStorage.setCertSign(true);
		//设置证书存储方式，这里为路径
		aliPayConfigStorage.setCertStoreType(CertStoreType.CLASS_PATH);
		aliPayConfigStorage.setMerchantCert(aliPayBean.getAppCertPath());
		aliPayConfigStorage.setAliPayRootCert(aliPayBean.getAliPayRootCertPath());
		aliPayConfigStorage.setAliPayCert(aliPayBean.getAliPayCertPath());
		aliPayConfigStorage.setKeyPrivate(aliPayBean.getPrivateKey());
		aliPayConfigStorage.setNotifyUrl(aliPayBean.getDomain() + aliPayBean.getNotifyUrl());
		aliPayConfigStorage.setSignType(SignUtils.RSA2.name());
		aliPayConfigStorage.setInputCharset("utf-8");
		//是否为测试账号，沙箱环境
		aliPayConfigStorage.setTest(false);
		//请求连接池配置
		HttpConfigStorage httpConfigStorageAli = new HttpConfigStorage();
		//最大连接数
		httpConfigStorageAli.setMaxTotal(20);
		//默认的每个路由的最大连接数
		httpConfigStorageAli.setDefaultMaxPerRoute(10);
		AliPayService aliPayService = new AliPayService(aliPayConfigStorage, httpConfigStorageAli);
		//增加支付回调消息拦截器
//		aliPayService.addPayMessageInterceptor(new AliPayMessageInterceptor());
		//设置回调消息处理
//		aliPayService.setPayMessageHandler(spring.getBean(AliPayMessageHandler.class));
		return aliPayService;
	}

	@Bean
	public WxPayService wxPayService(){
		WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
		wxPayConfigStorage.setMchId(wxPayBean.getMchId());
		wxPayConfigStorage.setAppId(wxPayBean.getAppId());
		//转账公钥，转账时必填
//		wxPayConfigStorage.setKeyPublic("1567555201");
		wxPayConfigStorage.setSecretKey(wxPayBean.getPartnerKey());
		wxPayConfigStorage.setNotifyUrl(wxPayBean.getDomain() + aliPayBean.getNotifyUrl());
		wxPayConfigStorage.setSignType(SignUtils.MD5.name());
		wxPayConfigStorage.setInputCharset("utf-8");
		//https证书设置，退款必须 方式二
		HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
		httpConfigStorage.setKeystore(wxPayBean.getCertPath());
		httpConfigStorage.setStorePassword(wxPayBean.getMchId());
		httpConfigStorage.setCertStoreType(CertStoreType.CLASS_PATH);
		return new WxPayService(wxPayConfigStorage, httpConfigStorage);
	}


}
