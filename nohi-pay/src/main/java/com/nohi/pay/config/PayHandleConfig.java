package com.nohi.pay.config;

import com.nohi.common.utils.StringUtils;
import com.nohi.pay.annotation.PayHandleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.nohi.pay.pay.handle.PayHandle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class PayHandleConfig {

    @Bean("payHandleMap")
    public Map<String, PayHandle> payHandleMap(List<PayHandle> payHandles){
        Map<String,PayHandle> map = new HashMap<>();
        if(payHandles == null){
            return map;
        }
        for (PayHandle payHandle : payHandles) {
            PayHandleType annotation = payHandle.getClass().getAnnotation(PayHandleType.class);
            if(StringUtils.isNotEmpty(annotation.value())){
                map.put(annotation.value(),payHandle);
            }
        }
        return map;
    }

}
