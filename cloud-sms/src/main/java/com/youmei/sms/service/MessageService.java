package com.youmei.sms.service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

public interface MessageService {
    SendSmsResponse sendSms(String phone, String code, String signName, String template) throws ClientException;
}
