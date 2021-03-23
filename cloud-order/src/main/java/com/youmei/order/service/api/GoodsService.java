package com.youmei.order.service.api;

import com.youmei.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cloud-item-service")
public interface GoodsService extends GoodsApi {
}
