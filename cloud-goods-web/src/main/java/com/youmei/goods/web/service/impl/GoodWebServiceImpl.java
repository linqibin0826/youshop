package com.youmei.goods.web.service.impl;

import com.youmei.goods.web.client.BrandClient;
import com.youmei.goods.web.client.CategoryClient;
import com.youmei.goods.web.client.GoodsClient;
import com.youmei.goods.web.client.SpecificationClient;
import com.youmei.goods.web.service.GoodsWebService;
import com.youmei.item.pojo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

@Service
public class GoodWebServiceImpl implements GoodsWebService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private TemplateEngine templateEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsWebService.class);


    public void createHTML(Long spuId) {
        PrintWriter printWriter = null;
        try {
            // 获取页面的数据
            Map<String, Object> data = this.loadData(spuId);

            Context thymeLeafContext = new Context();
            // 将数据放入上下文
            thymeLeafContext.setVariables(data);
            // 创建文件,输出
            File file = new File("F:\\Java\\nginx-1.14.0\\html\\item" + spuId + ".html");
            printWriter = new PrintWriter(file);

            // 执行页面静态化
            templateEngine.process("item", thymeLeafContext, printWriter);
        } catch (Exception e) {
            LOGGER.error("页面静态化出错：{}，"+ e, spuId);
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }





    @Override
    public Map<String, Object> loadData(Long spuId) {
        Map<String, Object> data = new HashMap<>();

        Spu spu = this.goodsClient.querySpuById(spuId);
        SpuDetail spuDetail = this.goodsClient.querySpuDetail(spuId);
        List<Sku> skus = this.goodsClient.querySkuListById(spuId);
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        // 查询规格参数组以及组下的规格参数
        List<SpecGroup> groupsWithSpecs = this.specificationClient.querySpecsByCid(spu.getCid3());
        // 特殊规格参数
        List<SpecParam> specialParams = this.specificationClient.queryParams(null, spu.getCid3(), null, false);
        Map<Long, String> paramMap = new HashMap<>();
        specialParams.forEach(specialParam -> {
            paramMap.put(specialParam.getId(), specialParam.getName());
        });

        // 封装分类map
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }



        data.put("spu", spu);
        data.put("categories", categories);
        data.put("brand", brand);
        data.put("skus", skus);
        // 规格组
        data.put("groups", groupsWithSpecs);
        // 特殊规格参数
        data.put("paramMap", paramMap);
        data.put("spuDetail", spuDetail);

        return data;
    }
}
