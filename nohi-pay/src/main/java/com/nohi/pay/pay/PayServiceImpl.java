package com.nohi.pay.pay;

import com.alibaba.fastjson.JSON;
import com.nohi.pay.constant.PayTypeEnum;
import com.nohi.pay.util.OrderIdUtil;
import com.nohi.pay.model.PayModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.nohi.pay.pay.handle.PayHandle;
import com.nohi.pay.service.AliPayNoHiService;
import com.nohi.pay.service.WxPayNoHiService;

import java.util.Map;

@Component
public class PayServiceImpl implements PayService {

    @Autowired
    private AliPayNoHiService aliPayNoHiService;
    @Autowired
    private WxPayNoHiService wxPayNoHiService;
    @Autowired
    private Map<String, PayHandle> payHandleMap;

    @Override
    public String pay(PayModel payModel) {
        String orderType = OrderIdUtil.getOrderType(payModel.getTradeNo());
        // 检查业务类型对不对
        if(orderType == null){
            throw new RuntimeException();
        }
        PayHandle payHandle = payHandleMap.get(orderType);
        if(payHandle == null){
            throw new RuntimeException();
        }
        String result;
        payHandle.beforePay(payModel);
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
        payHandle.afterPay(payModel);
        return result;
    }

    @Override
    public void notifyHandle(String notify, PayTypeEnum payTypeEnum) {

    }
}
