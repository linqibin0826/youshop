package com.youmei.search.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youmei.common.pojo.PageResult;
import com.youmei.item.pojo.*;
import com.youmei.search.client.BrandClient;
import com.youmei.search.client.CategoryClient;
import com.youmei.search.client.GoodsClient;
import com.youmei.search.client.SpecificationClient;
import com.youmei.search.pojo.Goods;
import com.youmei.search.pojo.SearchRequest;
import com.youmei.search.pojo.SearchResult;
import com.youmei.search.repository.GoodsRepository;
import com.youmei.search.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Goods buildGoods(Spu spu) throws IOException {
        Long spuId = spu.getId();
        Goods goods = new Goods();
        // 1.先将部分属性对拷. 剩余字段: all, price, specs, skus
        BeanUtils.copyProperties(spu, goods, "skus");

        // 2.拼接并封装all字段, 需要title + 分类名称 + 品牌名称
        // (1).获取分类名称
        List<Long> categoryIds = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> categoryNameList = this.categoryClient.queryNamesByIds(categoryIds);
        String categoryNames = StringUtils.join(categoryNameList, " ");
        // (2).获取品牌名称
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        String brandName = brand.getName();
        // (3).拼接all字段
        String all = spu.getTitle() + " " + categoryNames + " " + brandName;
        goods.setAll(all);

        // 3.封装skus字段和price字段
        List<Sku> skuList = goodsClient.querySkuListById(spuId);
        // (1).初始化价格数组
        List<Long> priceList = new ArrayList<>();
        List<Map<String, Object>> skuMapList = skuList.stream().map(sku -> {
            Long price = sku.getPrice();
            priceList.add(price);
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", price);
            String images = sku.getImages();
            skuMap.put("images", StringUtils.isBlank(images) ? "" : StringUtils.split(images, ",")[0]);
            return skuMap;
        }).collect(Collectors.toList());
        goods.setPrice(priceList);
        // (2).序列化skus
        String skusString = MAPPER.writeValueAsString(skuMapList);
        goods.setSkus(skusString);

        // 4.封装specs字段.
        // (1).查询所有可搜索的规格参数.
        List<SpecParam> paramList = this.specificationClient.queryParams(null, spu.getCid3(), true, null);
        // (2).查询所有的规格参数值.
        SpuDetail spuDetail = this.goodsClient.querySpuDetail(spuId);
        // (3).获取规格参数, 并转换成map
        String genericSpec = spuDetail.getGenericSpec();
        String specialSpec = spuDetail.getSpecialSpec();
        Map<Long, Object> genericSpecMap = MAPPER.readValue(genericSpec, new TypeReference<Map<Long, Object>>() {
        });
        Map<Long, Object> specialSpecMap = MAPPER.readValue(specialSpec, new TypeReference<Map<Long, Object>>() {
        });
        Map<String, Object> finalParamMap = new HashMap<>();
        // (4).为paramList赋值
        paramList.forEach(param -> {
            if (param.getGeneric()) {
                String value = genericSpecMap.get(param.getId()).toString();
                if (param.getNumeric()) {
                    // 如果是数字类型的话,应返回一个区间(将查到的具体参数值传入)
                    value = chooseSegment(value, param);
                }
                finalParamMap.put(param.getName(), value);
            } else {
                Object value = specialSpecMap.get(param.getId());
                finalParamMap.put(param.getName(), value);
            }
        });
        goods.setSpecs(finalParamMap);

        return goods;
    }

    @Override
    public PageResult<Goods> search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        Integer page = searchRequest.getPage();
        Integer size = searchRequest.getSize();
        // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
        if (StringUtils.isBlank(key)) {
            return null;
        }
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        // 带上查询条件
        // QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);
        BoolQueryBuilder boolQueryBuilder = buildBoolQueryBuilder(searchRequest);
        nativeSearchQueryBuilder.withQuery(boolQueryBuilder);

        // 聚合函数
        String categoryAggName = "categories";
        String brandAggName = "brands";
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        // 带上分页信息
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page, size));
        // 结果过滤, 我们只需要某些字段
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));

        // 执行搜索
        Page<Goods> goodsPage = this.goodsRepository.search(nativeSearchQueryBuilder.build());
        // 转换成带聚合信息的分页
        AggregatedPage aggregatedPage = (AggregatedPage) goodsPage;
        // 解析分类和品牌聚合信息
        List<Map<String, Object>> categories = parseCategoryAgg(aggregatedPage.getAggregation(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggregatedPage.getAggregation(brandAggName));

        // 判断分类聚合结果集大小,如果是1,则参数聚合.
        List<Map<String, Object>> specs = null;
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1) {
            specs = this.getParamsAggResult((long)categories.get(0).get("id"), boolQueryBuilder);
        }

        return new SearchResult(goodsPage.getTotalElements(), (long) goodsPage.getTotalPages(),
                goodsPage.getContent(), categories, brands, specs);

    }

    /**
     * 带上过滤条件的查询器.
     *
     * @author youmei
     * @since 2021/2/28 17:53
     */
    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest searchRequest) {
        Map<String, Object> filterMap = searchRequest.getFilter();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND));
        // 判断有无过滤条件, 无则直接返回
        if (CollectionUtils.isEmpty(filterMap)) {
            return boolQueryBuilder;
        }
        // 添加过滤条件
        filterMap.entrySet().forEach(filter -> {
            String key = filter.getKey();
            // 判断过滤字段名是什么.
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                key = "cid3";
            } else {
                // 如果是规格参数名，拼接过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, filter.getValue()));
        });

        return boolQueryBuilder;
    }

    /**
     * 根据商品分类id, 查询所有可用于聚合的规格参数的值.
     * @author youmei
     * @since 2021/2/28 14:58
     */
    private List<Map<String, Object>> getParamsAggResult(long cid, QueryBuilder basicQuery) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(basicQuery);
        // 结果过滤, 不需要结果.
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        // 先根据cid查询所有可搜索字段的key
        List<SpecParam> searchableParams = this.specificationClient.queryParams(null, cid, true, null);
        // 遍历参数名, 添加上所有聚合.
        searchableParams.forEach(searchableParam ->
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(searchableParam.getName())
                    .field("specs." + searchableParam.getName() + ".keyword")
            )
        );
        // 执行搜索
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>)this.goodsRepository.search(nativeSearchQueryBuilder.build());
        // 解析结果.
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        // 遍历map
        List<Map<String, Object>> params = aggregationMap.entrySet().stream().map(entry -> {
            Map<String, Object> map = new HashMap<>();
            // 设置规格参数名称
            map.put("k", entry.getKey());
            // 设置值,是一个数组.
            StringTerms terms = (StringTerms) entry.getValue();
            List<String> options = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            map.put("options", options);
            return map;
        }).collect(Collectors.toList());

        return params;
    }

    /**
     * 提取根据品牌聚合
     *
     * @author youmei
     * @since 2021/2/27 21:33
     */
    private List<Brand> parseBrandAgg(Aggregation aggregation) {
        // 转型为LongTerms类型的聚合
        LongTerms longTerms = (LongTerms) aggregation;
        return longTerms.getBuckets().stream().map(bucket ->
                this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue())).collect(Collectors.toList());
    }

    /**
     * 提取根据cid3聚合
     *
     * @author youmei
     * @since 2021/2/27 21:31
     */
    private List<Map<String, Object>> parseCategoryAgg(Aggregation aggregation) {
        LongTerms longTerms = (LongTerms) aggregation;
        return longTerms.getBuckets().stream().map(bucket -> {
            Map<String, Object> categoryMap = new HashMap<>();
            long categoryId = bucket.getKeyAsNumber().longValue();
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(categoryId));
            String name = names.get(0);
            categoryMap.put("id", categoryId);
            categoryMap.put("name", name);
            return categoryMap;
        }).collect(Collectors.toList());
    }

    /**
     * 判断传入的值在那个区间里面, 返回的值将放入ES中,用于筛选过滤.
     *
     * @param value
     * @param param
     * @return
     */
    private String chooseSegment(String value, SpecParam param) {
        // 使用double类型进行比较
        double val = NumberUtils.toDouble(value);
        String result = "其他";

        String segments = param.getSegments();
        String[] splitSegments = StringUtils.split(segments, ",");
        for (String segment : splitSegments) {
            String[] interval = StringUtils.split(segment, "-");
            double begin = NumberUtils.toDouble(interval[0]);
            double end = Double.MAX_VALUE;
            if (interval.length > 1) {
                end = NumberUtils.toDouble(interval[1]);
            }

            // 判断值在哪个范围内
            if (val >= begin && val < end) {
                if (interval.length == 1) {
                    result = begin + param.getUnit() + "以上";
                } else if (begin == 0) {
                    result = end + param.getUnit() + "以下";
                } else {
                    result = segment + param.getUnit();
                }
                break;
            }
        }

        return result;
    }

}
