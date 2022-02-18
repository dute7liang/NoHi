package com.nohi.pay.pay;

import com.nohi.pay.constant.PayTypeEnum;
import com.nohi.pay.model.PayModel;

public interface PayService {

    /**
     * 统一支付接口
     * @param payModel
     * @return
     */
    String pay(PayModel payModel);

    /**
     * 回调处理
     */
    void notifyHandle(String notify,PayTypeEnum payTypeEnum);

}
