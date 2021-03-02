package com.youmei.item.service.impl;

import com.youmei.item.mapper.CategoryMapper;
import com.youmei.item.pojo.Category;
import com.youmei.item.service.CategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    @Override
    public List<Category> getCategoryByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        return this.categoryMapper.select(category);
    }

    @Override
    public List<Category> queryByBrandId(Long bid) {
        return this.categoryMapper.queryByBrandId(bid);
    }

    @Override
    public String queryCategoryByIds(List<Long> ids) {
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        List<String> nameList = categories.stream().map(category -> category.getName()).collect(Collectors.toList());
        String names = StringUtils.join(nameList, "/");
        return names;
    }

    @Override
    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> categoryList = this.categoryMapper.selectByIdList(ids);
        List<String> names = categoryList.stream().map(category -> category.getName()).collect(Collectors.toList());
        return names;
    }

    @Override
    public List<Category> queryAllByCid3(Long id) {
        Category c3 = this.categoryMapper.selectByPrimaryKey(id);
        Category c2 = this.categoryMapper.selectByPrimaryKey(c3.getParentId());
        Category c1 = this.categoryMapper.selectByPrimaryKey(c2.getParentId());
        return Arrays.asList(c1,c2,c3);
    }
}
