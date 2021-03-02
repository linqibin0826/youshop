package com.youmei.item.service.impl;

import com.youmei.item.mapper.SpecGroupMapper;
import com.youmei.item.mapper.SpecParamMapper;
import com.youmei.item.pojo.SpecGroup;
import com.youmei.item.pojo.SpecParam;
import com.youmei.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    @Override
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);

        return specGroupMapper.select(specGroup);
    }

    @Override
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean searching, Boolean generic) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        specParam.setGeneric(generic);

        return specParamMapper.select(specParam);
    }

    @Override
    public List<SpecGroup> querySpecsByCid(Long cid) {
        List<SpecGroup> groups = this.queryGroupsByCid(cid);
        groups.forEach(group -> {
            group.setParams(this.queryParams(group.getId(), null, null, null));
        });
        return groups;
    }
}
