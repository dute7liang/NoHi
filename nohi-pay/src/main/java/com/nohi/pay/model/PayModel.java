package com.nohi.pay.model;

import com.nohi.pay.constant.PayTerminalEnum;
import com.nohi.pay.constant.PayTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayModel {

    private String tradeNo;

    private String subject;

    private String body;

    private Long amount;

    private PayTerminalEnum payTerminalEnum;

    private PayTypeEnum payTypeEnum;

}
