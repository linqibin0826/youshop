package com.youmei.goods.web.service;

import java.util.Map;

public interface GoodsWebService {
    /**
     * Load data map.
     *
     * @param spuId the spu id
     * @return the map
     * @author youmei
     * @since 2021 /3/1 16:50
     */
    Map<String, Object> loadData(Long spuId);
}
