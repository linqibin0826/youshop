package com.youmei.sms.listener;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.youmei.sms.config.SmsProperties;
import com.youmei.sms.service.MessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@EnableConfigurationProperties(value = SmsProperties.class)
@Component
public class SmsListener {

    @Autowired
    private SmsProperties props;

    @Autowired
    private MessageService messageService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "youshop.sms.queue", durable = "true"),
            exchange = @Exchange(
                    value = "youshop.sms.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"sms.verify.code"}
    ))
    public void sendListen(Map<String, String> msg) throws ClientException {
        if (msg == null || msg.size() <= 0) {
            // 放弃处理
            return;
        }
        String phone = msg.get("phone");
        System.out.println(phone);
        String code = msg.get("code");
        System.out.println(code);

        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code)) {
            // 放弃处理
            return;
        }
        // 发送消息
        /*this.messageService.sendSms(phone, code,
                props.getSignName(),
                props.getVerifyCodeTemplate());*/
        System.out.println("处理成功");
    }
}
