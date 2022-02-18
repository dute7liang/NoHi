package com.nohi.pay.pay.handle;

import com.nohi.pay.model.PayModel;

public interface PayHandle {

    void beforePay(PayModel payModel);

    void afterPay(PayModel payModel);

    void notifyHandle(String tradeNo);

}
