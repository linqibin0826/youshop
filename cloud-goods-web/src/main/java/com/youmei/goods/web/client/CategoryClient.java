package com.youmei.goods.web.client;

import com.youmei.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cloud-item-service")
public interface CategoryClient extends CategoryApi {
}
