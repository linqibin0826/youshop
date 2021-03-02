package com.youmei.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youmei.common.pojo.PageResult;
import com.youmei.item.mapper.BrandMapper;
import com.youmei.item.pojo.Brand;
import com.youmei.item.service.BrandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public PageResult<Brand> queryBrandList(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        // 是否带模糊查询或根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        // 拼装分页条件
        PageHelper.startPage(page, rows);

        // 排序
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        List<Brand> brands = brandMapper.selectByExample(example);

        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        return new PageResult<>(pageInfo.getTotal(), (long) pageInfo.getPages(), pageInfo.getList());
    }

    @Override
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        // 保存品牌信息
        this.brandMapper.insertSelective(brand);

        Long brandId = brand.getId();

        // 保存品牌与分类关系信息
        cids.forEach(cId -> {
            this.brandMapper.saveBrandAndCategoryRelation(brandId, cId);
        });

    }

    @Override
    public List<Brand> queryBrandsByCId(Long cid) {
        return this.brandMapper.queryBrandsByCId(cid);
    }

    @Override
    public Brand queryBrandById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
