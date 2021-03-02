package com.youmei.item.api;

import com.youmei.item.pojo.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public interface CategoryApi {


    @GetMapping("/list")
    List<Category> getCategoryByPid(@RequestParam(value = "pid", defaultValue = "0") Long pid);

    @GetMapping("bid/{bid}")
    List<Category> queryByBrandId(@PathVariable("bid") Long bid);

    @GetMapping("/names")
    List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);
}
