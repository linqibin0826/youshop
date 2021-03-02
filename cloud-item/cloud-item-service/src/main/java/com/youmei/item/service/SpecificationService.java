package com.youmei.item.service;

import com.youmei.item.pojo.SpecGroup;
import com.youmei.item.pojo.SpecParam;

import java.util.List;

public interface SpecificationService {

    /**
     * Query groups by cid list.
     *
     * @param cid the cid
     * @return the list
     * @author youmei
     * @since 2021 /2/21 14:00
     */
    List<SpecGroup> queryGroupsByCid(Long cid);




    /**
     * Query params list.
     *
     * @param gid       the gid
     * @param cid       the cid
     * @param searching the searching
     * @param valid     the valid
     * @return the list
     * @author youmei
     * @since 2021 /2/22 15:30
     */
    List<SpecParam> queryParams(Long gid, Long cid, Boolean searching, Boolean generic);

    /**
     * Query specs by cid list.
     *
     * @param cid the cid
     * @return list
     */
    List<SpecGroup> querySpecsByCid(Long cid);
}
