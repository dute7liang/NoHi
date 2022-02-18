package com.nohi.pay.util;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * <p>created on 2021/8/23</p>
 *
 * @author zhangliang
 */
public class OrderIdUtil {

	private static final SnowFlake snowFlake = new SnowFlake(2, 3);

	public static String createOrder(String orderType){
		return orderType + snowFlake.nextId();
	}

	public static String getOrderType(String tradeNo){
		if(StringUtils.isBlank(tradeNo)){
			return null;
		}
		return String.valueOf(tradeNo.charAt(0));
	}

}
