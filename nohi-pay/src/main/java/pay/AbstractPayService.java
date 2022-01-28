package pay;

import com.alibaba.fastjson.JSON;
import constant.PayTerminalEnum;
import constant.PayTypeEnum;
import kit.OrderIdKit;
import model.PayModel;
import service.AliPayNoHiService;
import service.WxPayNoHiService;

import java.util.Map;

public abstract class AbstractPayService implements PayService {

    AliPayNoHiService aliPayNoHiService;

    WxPayNoHiService wxPayNoHiService;

    abstract String getPayEnum();

    @Override
    public String pay(PayModel payModel) {
        String orderType = OrderIdKit.getOrderType(payModel.getTradeNo());
        // 检查业务类型对不对
        if(orderType == null || !orderType.equals(getPayEnum())){
            throw new RuntimeException();
        }
        String result;
        PayTypeEnum payTypeEnum = payModel.getPayTypeEnum();
        switch (payTypeEnum) {
            case WX:
                Map<String,Object> resultMap = wxPayNoHiService.pay(payModel);
                result = JSON.toJSONString(resultMap);
                break;
            case ALI:
                result = aliPayNoHiService.pay(payModel);
                break;
            default:
                throw new RuntimeException();
        }
        return result;
    }
}
