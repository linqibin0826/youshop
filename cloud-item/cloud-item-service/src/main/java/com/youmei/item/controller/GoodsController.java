package com.youmei.item.controller;

import com.youmei.common.pojo.PageResult;
import com.youmei.item.bo.SpuBo;
import com.youmei.item.pojo.Sku;
import com.youmei.item.pojo.Spu;
import com.youmei.item.pojo.SpuDetail;
import com.youmei.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>> queryGoodsInfoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    ) {
        PageResult<SpuBo> goods = this.goodsService.queryGoodsInfoByPage(key, saleable, page, rows);
        if (CollectionUtils.isEmpty(goods.getItems())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(goods);
    }

    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo) {
        this.goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuBo spuBo) {
        this.goodsService.updateGoods(spuBo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetail(@PathVariable("spuId") Long spuId) {
        SpuDetail spuDetail = this.goodsService.querySpuDetail(spuId);
        if (spuDetail == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(spuDetail);

    }


    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuListById(@RequestParam("id") Long spuId) {
        List<Sku> skuList = this.goodsService.querySkuListBySpuId(spuId);
        if (CollectionUtils.isEmpty(skuList)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(skuList);
    }

    @GetMapping("/spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        Spu spu = this.goodsService.querySpuById(id);
        if (spu == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(spu);
    }

    @GetMapping("/sku/{id}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("id") Long id) {
        Sku sku = this.goodsService.querySkuById(id);
        if (sku != null) {
            return ResponseEntity.ok(sku);
        }
        return ResponseEntity.badRequest().build();
    }
}
