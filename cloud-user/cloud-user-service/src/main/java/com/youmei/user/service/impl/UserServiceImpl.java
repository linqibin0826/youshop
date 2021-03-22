package com.youmei.user.service.impl;

import com.youmei.common.utils.CodecUtils;
import com.youmei.common.utils.NumberUtils;
import com.youmei.user.mapper.UserMapper;
import com.youmei.user.pojo.User;
import com.youmei.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final String KEY_PREFIX = "user:code:phone:";

    @Override
    public Boolean checkData(String data, Integer type) {
        User user = new User();
        if (type == 1) {
            user.setUsername(data);
        } else if (type == 2) {
            user.setPhone(data);
        } else {
            return null;
        }
        return this.userMapper.selectCount(user) == 0;

    }

    @Override
    public Boolean sendCode(String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        }
        String code = NumberUtils.generateCode(6);
        try {
            Map<String, String> msg = new HashMap<>(16);
            msg.put("phone", phone);
            msg.put("code", code);
            // 调用消息中间件发送短信
            this.amqpTemplate.convertAndSend("youshop.sms.exchange", "sms.verify.code", msg);
            // 将验证码放入redis中
            this.stringRedisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return true;
        } catch (AmqpException e) {
            LOGGER.error("发送短信失败. phone: {}, code: {}", phone, code);
            return false;
        }
    }

    @Override
    public Boolean register(User user, String code) {
        // 校验验证码
        String cacheCode = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(cacheCode, code)) {
            return false;
        }

        // 生成salt
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        // 设置编码过后的密码
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        user.setCreated(new Date());

        // 添加到数据库
        boolean boo = this.userMapper.insertSelective(user) == 1;
        if (boo) {
            // 删除cacheCode
            this.stringRedisTemplate.delete(KEY_PREFIX + user.getPhone());
        }

        return boo;
    }

    @Override
    public User queryUser(String username, String password) {
        // 查询用户
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        // 判断是否存在该用户
        if (user == null) {
            return null;
        }

        // 校验密码
        if (!user.getPassword().equals(CodecUtils.md5Hex(password, user.getSalt()))) {
            return null;
        }

        return user;
    }
}
