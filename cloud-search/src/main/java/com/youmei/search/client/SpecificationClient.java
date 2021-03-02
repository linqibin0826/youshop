package com.youmei.search.client;

import com.youmei.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cloud-item-service")
public interface SpecificationClient extends SpecificationApi {
}
