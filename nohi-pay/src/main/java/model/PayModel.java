package model;

import constant.PayBusinessType;
import constant.PayTerminalEnum;
import constant.PayTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
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
