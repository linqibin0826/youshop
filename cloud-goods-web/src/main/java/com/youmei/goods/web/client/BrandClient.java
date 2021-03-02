package com.youmei.goods.web.client;

import com.youmei.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cloud-item-service")
public interface BrandClient extends BrandApi {
}
