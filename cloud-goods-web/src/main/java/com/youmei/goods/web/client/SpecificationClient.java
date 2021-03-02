package com.youmei.goods.web.client;

import com.youmei.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cloud-item-service")
public interface SpecificationClient extends SpecificationApi {
}
