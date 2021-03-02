package com.youmei.item.api;

import com.youmei.common.pojo.PageResult;
import com.youmei.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public interface BrandApi {


    @GetMapping("/page")
    PageResult<Brand> queryBrandList(
            @RequestParam(value = "key") String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "desc", defaultValue = "false") Boolean desc
    );

    @GetMapping("/cid/{cid}")
    List<Brand> queryBrandsByCId(@PathVariable(value = "cid") Long cid);

    @PostMapping
    Void saveBrand(Brand brand, @RequestParam("cids") List<Long> cids);

    @GetMapping("/id/{id}")
    Brand queryBrandById(@PathVariable("id") Long id);
}
