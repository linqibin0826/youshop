package com.youmei.item.service;


import com.youmei.item.pojo.Category;

import java.util.List;

public interface CategoryService {

    /**
     * Gets category by pid.
     *
     * @param pid the pid
     * @return the category by pid
     * @author youmei
     * @since 2021 /2/13 18:57
     */
    List<Category> getCategoryByPid(Long pid);

    List<Category> queryByBrandId(Long bid);

    String queryCategoryByIds(List<Long> ids);

    /**
     * Query names by ids list.
     *
     * @param ids the ids
     * @return the list
     * @author youmei
     * @since 2021 /2/25 15:47
     */
    List<String> queryNamesByIds(List<Long> ids);

    /**
     * Query all by cid 3 list.
     *
     * @param id the id
     * @return the list
     * @author youmei
     * @since 2021 /2/28 18:46
     */
    List<Category> queryAllByCid3(Long id);
}
