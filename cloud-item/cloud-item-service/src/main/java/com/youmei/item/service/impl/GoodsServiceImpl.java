package com.youmei.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youmei.common.pojo.PageResult;
import com.youmei.item.bo.SpuBo;
import com.youmei.item.mapper.*;
import com.youmei.item.pojo.Sku;
import com.youmei.item.pojo.Spu;
import com.youmei.item.pojo.SpuDetail;
import com.youmei.item.pojo.Stock;
import com.youmei.item.service.CategoryService;
import com.youmei.item.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Override
    public PageResult<SpuBo> queryGoodsInfoByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        // 1.封装查询条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }

        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        // 2.封装分页信息
        PageHelper.startPage(page, rows);

        // 3.查询数据库
        List<Spu> spuList = this.spuMapper.selectByExample(example);
        PageInfo<Spu> spuPageInfo = new PageInfo<>(spuList);

        // 4.封装Bo
        List<SpuBo> goods = spuList.stream().map(spu -> {
            SpuBo good = new SpuBo();
            BeanUtils.copyProperties(spu, good);
            // 5. 查询品牌名称并封装
            String brandName = this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName();
            good.setBname(brandName);
            // 6. 查询分类名称并封装
            String categoryName = this.categoryService.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            good.setCname(categoryName);
            return good;
        }).collect(Collectors.toList());

        System.out.println(spuPageInfo.getTotal());
        return new PageResult<>(spuPageInfo.getTotal(), goods);
    }

    @Override
    public void saveGoods(SpuBo spuBo) {
        // 1.将spu基本信息保存至tb_spu
        spuBo.setId(null);
        spuBo.setValid(true);
        spuBo.setSaleable(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.spuMapper.insertSelective(spuBo);

        // 2.将SpuDetail信息保存至tb_spu_detail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);



        addSkuAndStock(spuBo);


    }

    private void addSkuAndStock(SpuBo spuBo) {
        // 3.获取sku信息
        List<Sku> skus = spuBo.getSkus();

        skus.forEach(sku -> {
            // 4.保存sku信息
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            // 5.保存sku库存信息
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    @Override
    public void updateGoods(SpuBo spuBo) {

        // 1.将原有的sku信息删除
        Long spuId = spuBo.getId();
        // 1.1 先根据spuId查询出所有的skuId,再删除指定库存信息
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> oldSkuList = this.skuMapper.select(record);

        oldSkuList.forEach(oldSku -> {
            // 删除指定库存信息
            this.stockMapper.deleteByPrimaryKey(oldSku.getId());

            // 删除所有sku
            this.skuMapper.deleteByPrimaryKey(oldSku.getId());
        });

        // 2.新增sku和库存信息
        this.addSkuAndStock(spuBo);

        // 3.更新spu基本信息
        spuBo.setLastUpdateTime(new Date());
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);

        // 4.更新spuDetail表
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
    }

    @Override
    public SpuDetail querySpuDetail(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    @Override
    public List<Sku> querySkuListBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = this.skuMapper.select(sku);
        skuList = skuList.stream().map(item -> {
                    Integer stock = this.stockMapper.selectByPrimaryKey(item.getId()).getStock();
                    item.setStock(stock);
                    return item;
                }).collect(Collectors.toList());
        return skuList;
    }

    @Override
    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }
}
