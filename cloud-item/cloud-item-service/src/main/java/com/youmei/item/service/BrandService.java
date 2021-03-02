package com.youmei.item.service;


import com.youmei.common.pojo.PageResult;
import com.youmei.item.pojo.Brand;

import java.util.List;

public interface BrandService {

    /**
     * Query brand list page result.
     *
     * @param key    the key
     * @param page   the page
     * @param rows   the rows
     * @param sortBy the sort by
     * @param desc   the desc
     * @return the page result
     * @author youmei
     * @since 2021 /2/13 22:25
     */
    PageResult<Brand> queryBrandList(String key, Integer page, Integer rows, String sortBy, Boolean desc);

    /**
     * Save brand.
     *
     * @param brand the brand
     * @param cids  the cids
     * @author youmei
     * @since 2021 /2/15 11:03
     */
    void saveBrand(Brand brand, List<Long> cids);

    /**
     * Query brands by cid.
     *
     * @param cid the cid
     * @return the list
     * @author youmei
     * @since 2021 /2/22 11:40
     */
    List<Brand> queryBrandsByCId(Long cid);

    /**
     * Query brand by id brand.
     *
     * @param id the id
     * @return the brand
     * @author youmei
     * @since 2021 /2/25 17:37
     */
    Brand queryBrandById(Long id);
}
