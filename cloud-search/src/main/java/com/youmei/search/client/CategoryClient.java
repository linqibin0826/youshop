package com.youmei.search.client;

import com.youmei.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cloud-item-service")
public interface CategoryClient extends CategoryApi {
}
