package com.youmei.goods.web.client;

import com.youmei.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cloud-item-service")
public interface GoodsClient extends GoodsApi {
}
