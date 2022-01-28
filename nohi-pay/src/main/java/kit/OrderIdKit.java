package kit;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import util.SnowFlake;

/**
 * <p>created on 2021/8/23</p>
 *
 * @author zhangliang
 */
public class OrderIdKit {

	public static final String ORDER = "O";
	public static final String VIP = "V";
	public static final String WITHDRAW = "W";
	public static final String QUALIFICATION_APPLY = "Q";
	public static final String JOIN_TALENT = "J";


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
