package com.youmei.item.api;

import com.youmei.common.pojo.PageResult;
import com.youmei.item.bo.SpuBo;
import com.youmei.item.pojo.Sku;
import com.youmei.item.pojo.Spu;
import com.youmei.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public interface GoodsApi {

    @GetMapping("/spu/page")
    PageResult<SpuBo> queryGoodsInfoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false) Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows
    );

    @PostMapping("/goods")
    Void saveGoods(@RequestBody SpuBo spuBo);

    @PutMapping("/goods")
    Void updateGoods(@RequestBody SpuBo spuBo);

    @GetMapping("/spu/detail/{spuId}")
    SpuDetail querySpuDetail(@PathVariable("spuId") Long spuId);


    @GetMapping("/sku/list")
    List<Sku> querySkuListById(@RequestParam("id") Long spuId);

    @GetMapping("/spu/{id}")
    Spu querySpuById(@PathVariable("id") Long id);

    @GetMapping("/sku/{id}")
    Sku querySkuById(@PathVariable("id") Long id);
}
