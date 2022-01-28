package service;

import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.common.bean.TransactionType;
import com.egzosn.pay.common.http.UriVariables;
import com.egzosn.pay.wx.bean.WxTransactionType;
import constant.PayTerminalEnum;
import kit.OrderIdKit;
import model.PayModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 *
 * 支付宝支付傻瓜式封装方法
 *
 * @see com.egzosn.pay.ali.api.AliPayService
 * <p>created on 2022/1/28 18:26</p>
 * @author dute7liang
 */
@Component
public class AliPayNoHiService {

    @Autowired
    private AliPayService aliPayService;

    public String pay(PayModel payModel) {
        PayOrder payOrder = new PayOrder();
        payOrder.setSubject(payModel.getSubject());
        payOrder.setBody(payModel.getBody());
        payOrder.setPrice(BigDecimal.valueOf((double) payModel.getAmount() / 100));
        payOrder.setOutTradeNo(payModel.getTradeNo());
        payOrder.setTransactionType(getTransactionType(payModel.getPayTerminalEnum()));
        return UriVariables.getMapToParameters(aliPayService.orderInfo(payOrder));
    }

    private TransactionType getTransactionType(PayTerminalEnum payTypeEnum){
        if(payTypeEnum == PayTerminalEnum.APP){
            return AliTransactionType.APP;
        }else if(payTypeEnum == PayTerminalEnum.H5){
            return AliTransactionType.WAP;
        }
        return null;
    }
}
