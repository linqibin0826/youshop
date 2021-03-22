package com.youmei.user.service;

import com.youmei.user.pojo.User;

public interface UserService {
    /**
     * 检查数据是否可用.
     *
     * @param data the data
     * @param type the type
     * @return the boolean
     * @author youmei
     * @since 2021 /3/6 21:57
     */
    Boolean checkData(String data, Integer type);

    /**
     * 发送短信.
     *
     * @param phone the phone
     * @author youmei
     * @since 2021 /3/8 21:18
     */
    Boolean sendCode(String phone);


    Boolean register(User user, String code);


    User queryUser(String username, String password);
}
