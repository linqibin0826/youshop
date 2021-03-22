package com.youmei.item.service;

import com.youmei.common.pojo.PageResult;
import com.youmei.item.bo.SpuBo;
import com.youmei.item.pojo.Sku;
import com.youmei.item.pojo.Spu;
import com.youmei.item.pojo.SpuDetail;

import java.util.List;

public interface GoodsService {

    /**
     * Query goods info by page return page result.
     *
     * @param key      the key
     * @param saleable the saleable
     * @param page     the page
     * @param rows     the rows
     * @return the page result
     * @author youmei
     * @since 2021 /2/22 10:20
     */
    PageResult<SpuBo> queryGoodsInfoByPage(String key, Boolean saleable, Integer page, Integer rows);

    /**
     * Save goods.
     *
     * @param spuBo the spu bo
     * @author youmei
     * @since 2021 /2/22 16:12
     */
    void saveGoods(SpuBo spuBo);

    /**
     * Update goods.
     *
     * @param spuBo the spu bo
     * @author youmei
     * @since 2021 /2/22 20:47
     */
    void updateGoods(SpuBo spuBo);

    /**
     * Query spu detail.
     *
     * @param spuId the spu id
     * @return the spu detail
     * @author youmei
     * @since 2021 /2/22 17:47
     */
    SpuDetail querySpuDetail(Long spuId);

    /**
     * Query sku list by spuId.
     *
     * @param spuId the spuId
     * @return the list
     * @author youmei
     * @since 2021 /2/22 17:53
     */
    List<Sku> querySkuListBySpuId(Long spuId);


    /**
     * Query spu by id spu.
     *
     * @param id the id
     * @return the spu
     * @author youmei
     * @since 2021 /3/1 16:25
     */
    Spu querySpuById(Long id);

    Sku querySkuById(Long id);
}
