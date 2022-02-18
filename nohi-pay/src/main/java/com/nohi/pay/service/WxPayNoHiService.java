package com.nohi.pay.service;

import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.common.bean.TransactionType;
import com.egzosn.pay.wx.api.WxPayService;
import com.egzosn.pay.wx.bean.WxTransactionType;
import com.nohi.pay.constant.PayTerminalEnum;
import com.nohi.pay.model.PayModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 微信傻瓜式封装方法
 *
 * @see com.egzosn.pay.wx.api.WxPayService
 * <p>created on 2022/1/28 18:27</p>
 * @author dute7liang
 */
@Component
public class WxPayNoHiService {

    @Autowired
    private WxPayService wxPayService;

    public Map<String,Object> pay(PayModel payModel){
        PayOrder payOrder = new PayOrder();
        payOrder.setSubject(payModel.getSubject());
        payOrder.setBody(payModel.getBody());
        payOrder.setPrice(BigDecimal.valueOf((double) payModel.getAmount() / 100));
        payOrder.setOutTradeNo(payModel.getTradeNo());
        payOrder.setTransactionType(getTransactionType(payModel.getPayTerminalEnum()));
        return wxPayService.orderInfo(payOrder);
    }

    private TransactionType getTransactionType(PayTerminalEnum payTypeEnum){
        if(payTypeEnum == PayTerminalEnum.APP){
            return WxTransactionType.APP;
        }else if(payTypeEnum == PayTerminalEnum.H5){
            return WxTransactionType.MWEB;
        }
        return null;
    }

}
