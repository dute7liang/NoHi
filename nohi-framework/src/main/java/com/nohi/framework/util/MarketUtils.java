package com.nohi.framework.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取市场工具类
 * <p>created on 2022/2/18 16:00</p>
 * @author dute7liang
 */
public class MarketUtils {

	public enum MarketEnum {
		none("none","未知"),
		iPhone("iPhone","苹果"),
		huawei("huawei","华为"),
		xiaomi("xiaomi","小米"),
		a360("a360","360"),
		vivo("vivo","vivo"),
		baidu("baidu591","百度"),
		share("share","分享"),
		;

		private String code;
		private String name;

		MarketEnum(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static String getMarketValue(String key){
			MarketEnum[] values = MarketEnum.values();
			for (MarketEnum value : values) {
				if(value.getCode().equals(key)){
					return value.getName();
				}
			}
			return key;
		}
	}


	private final static String APP_MARKET = "channelId";

	private final static String VERSION_NAME  = "versionName";

	public static String getVersion(HttpServletRequest request){
		return request.getHeader(VERSION_NAME);
	}

	public static String getAgent(HttpServletRequest request){
		return request.getHeader(HttpHeaders.USER_AGENT);
	}

	public static String getAgentCode(HttpServletRequest request){
		return request.getHeader(APP_MARKET);
	}

	public static boolean checkIos(HttpServletRequest request){
		String agent = request.getHeader(HttpHeaders.USER_AGENT);
		if(agent.contains("iPhone")|| agent.contains("iPod") || agent.contains("iPad")){
			return true;
		}
		return false;
	}

	public static String getMarket(HttpServletRequest request){
		String market = request.getHeader(APP_MARKET);
		if(StringUtils.isBlank(market)){ // 可能是IOS，也可能谁都不是
			String agent = request.getHeader(HttpHeaders.USER_AGENT);
			if(agent.contains("iPhone")|| agent.contains("iPod") || agent.contains("iPad")){
				return MarketEnum.iPhone.getName();
			}else{
				return MarketEnum.none.getName();
			}
		}
		return MarketEnum.getMarketValue(market);
	}
}
