package com.example.casclient1.config;

import com.example.casclient1.utils.RedisUtil;
import com.example.casclient1.utils.XmlUtils;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Map;

public class CustomCas30ServiceTicketValidator extends Cas30ServiceTicketValidator {

    @Autowired
    private RedisUtil redisUtil;

    public CustomCas30ServiceTicketValidator(String casServerUrlPrefix) {
        super(casServerUrlPrefix);
    }

    protected Map<String, Object> extractCustomAttributes(String xml) {
        Map<String, Object> map = super.extractCustomAttributes(xml);
        final String principal = XmlUtils.getTextForElement(xml, "user");
        if (!StringUtils.isEmpty(principal)) {
            redisUtil.set(principal, map);
        }
        return map;
    }
}
