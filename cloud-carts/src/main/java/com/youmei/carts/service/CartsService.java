package com.youmei.carts.service;

import com.youmei.carts.pojo.SkuInCarts;

import java.util.List;

public interface CartsService {
    void addSkuToCarts(SkuInCarts skuInCarts);

    List<SkuInCarts> getSkuInCarts();

    void addNums(SkuInCarts skuInCarts);

    void deleteSkuList(List<Long> skuIds);

}
