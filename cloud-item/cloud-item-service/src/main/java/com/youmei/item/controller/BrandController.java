package com.youmei.item.controller;

import com.youmei.common.pojo.PageResult;
import com.youmei.item.pojo.Brand;
import com.youmei.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandList(
            @RequestParam(value = "key") String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc
            ) {
        PageResult<Brand> brandPageResult = brandService.queryBrandList(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(brandPageResult.getItems())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(brandPageResult);
    }

    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCId(@PathVariable(value = "cid") Long cid) {
        List<Brand> brands = this.brandService.queryBrandsByCId(cid);
        if (CollectionUtils.isEmpty(brands)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(brands);
    }

    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids){
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id) {
        Brand brand = brandService.queryBrandById(id);
        if (brand != null) {
            return ResponseEntity.ok(brand);
        }
        return ResponseEntity.notFound().build();
    }
}
