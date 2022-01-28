package pay;

import constant.PayTerminalEnum;
import constant.PayTypeEnum;
import model.PayModel;

public interface PayService {

    /**
     * 统一支付接口
     * @param payModel
     * @return
     */
    String pay(PayModel payModel);

}
