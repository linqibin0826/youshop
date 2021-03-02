package com.youmei.item.mapper;

import com.youmei.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {

    @Insert("insert into tb_category_brand(brand_id, category_id) values (#{brandId}, #{cId})" )
    void saveBrandAndCategoryRelation(@Param("brandId") Long brandId, @Param("cId") Long cId);

    @Select("select * from tb_brand where id in (select brand_id from tb_category_brand where category_id = #{cid})")
    List<Brand> queryBrandsByCId(Long cid);
}
