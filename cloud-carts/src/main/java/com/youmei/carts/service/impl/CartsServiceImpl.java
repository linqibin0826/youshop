package com.youmei.carts.service.impl;

import com.youmei.auth.pojo.UserInfo;
import com.youmei.carts.client.GoodsClient;
import com.youmei.carts.interceptor.LoginInterceptor;
import com.youmei.carts.pojo.SkuInCarts;
import com.youmei.carts.service.CartsService;
import com.youmei.common.utils.JsonUtils;
import com.youmei.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartsServiceImpl implements CartsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    private static final String CART_PREFIX = "user:cart:";




    @Override
    public void addSkuToCarts(SkuInCarts skuInCarts) {
        // 分两种情况, 1.购物车中已有该商品 2.无该商品
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        // 绑定该用户购物车
        BoundHashOperations<String, Object, Object> boundHashOps = redisTemplate.boundHashOps(CART_PREFIX + userInfo.getId());
        // 查询购物车中是否存在该商品
        Long skuId = skuInCarts.getSkuId();
        Integer num = skuInCarts.getNum();
        if (boundHashOps.hasKey(skuId.toString())) {
            // 将其反序列化,添加数量
            String json = boundHashOps.get(skuId.toString()).toString();
            skuInCarts = JsonUtils.parse(json, SkuInCarts.class);
            skuInCarts.setNum(skuInCarts.getNum() + num);
        } else {
            // 新增商品到购物车里
            // 1. 根据skuId查询商品详细信息
            Sku sku = goodsClient.querySkuById(skuId);
            BeanUtils.copyProperties(sku, skuInCarts);
            skuInCarts.setImage(sku.getImages().split(",")[0]);
            skuInCarts.setNum(num);
            skuInCarts.setUserId(userInfo.getId());
        }
        boundHashOps.put(skuId.toString(), JsonUtils.serialize(skuInCarts));
    }

    @Override
    public List<SkuInCarts> getSkuInCarts() {
        Long userId = LoginInterceptor.getUserInfo().getId();
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(CART_PREFIX + userId.toString());
        Map<Object, Object> entries = hashOps.entries();
        List<SkuInCarts> skuInCartsArrayList = new ArrayList<>();
        entries.values().forEach(value -> {
            SkuInCarts parsedJson = JsonUtils.parse(value.toString(), SkuInCarts.class);
            skuInCartsArrayList.add(parsedJson);
        });
        return skuInCartsArrayList;
    }

    @Override
    public void addNums(SkuInCarts skuInCarts) {
        String userId = LoginInterceptor.getUserInfo().getId().toString();
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(userId);
        String skuId = skuInCarts.getSkuId().toString();
        String jsonString = hashOps.get(skuId).toString();
        SkuInCarts parsedJson = JsonUtils.parse(jsonString, SkuInCarts.class);
        parsedJson.setNum(skuInCarts.getNum());
        hashOps.put(skuId, JsonUtils.serialize(parsedJson));
    }

    @Override
    public void deleteSkuList(List<Long> skuIds) {
        String userId = LoginInterceptor.getUserInfo().getId().toString();
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(CART_PREFIX + userId);
        hashOps.delete(skuIds);
    }


}
