package com.youmei.goods.web.service;

import java.util.Map;

public interface GoodsWebService {

    /**
     * Create static html.
     *
     * @param spuId the spu id
     * @author youmei
     * @since 2021 /3/4 18:21
     */
    void createHTML(Long spuId);

    /**
     * 异步创建静态页面
     * @param spuId
     */
    void asyncExecute(Long spuId);

    /**
     * Load data map.
     *
     * @param spuId the spu id
     * @return the map
     * @author youmei
     * @since 2021 /3/1 16:50
     */
    Map<String, Object> loadData(Long spuId);

    /**
     * Delete html.
     *
     * @param spuId the spu id
     * @author youmei
     * @since 2021 /3/5 21:27
     */
    void deleteHtml(Long spuId);
}
