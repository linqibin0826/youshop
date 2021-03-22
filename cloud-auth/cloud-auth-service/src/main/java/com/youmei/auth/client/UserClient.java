package com.youmei.auth.client;

import com.youmei.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cloud-user-service")
public interface UserClient extends UserApi {
}
