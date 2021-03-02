package com.youmei.search.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.youmei.common.pojo.PageResult;
import com.youmei.item.pojo.Spu;
import com.youmei.search.pojo.Goods;
import com.youmei.search.pojo.SearchRequest;

import java.io.IOException;

public interface SearchService {
    /**
     * Transfer spu into goods.
     *
     * @param spu the spu
     * @return the goods
     * @author youmei
     * @since 2021 /2/25 16:54
     */
    Goods buildGoods(Spu spu) throws IOException;

    /**
     * Search page result.
     *
     * @param searchRequest the search request
     * @return the page result
     * @author youmei
     * @since 2021 /2/26 11:44
     */
    PageResult<Goods> search(SearchRequest searchRequest);
}
