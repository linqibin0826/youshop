package com.youmei.item.api;

import com.youmei.item.pojo.SpecGroup;
import com.youmei.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spec")
public interface SpecificationApi {

    @GetMapping("/groups/{cid}")
    List<SpecGroup> queryGroupsByCid(@PathVariable("cid") Long cid);

    @GetMapping("/params")
    List<SpecParam> queryParams(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching,
            @RequestParam(value = "generic", required = false) Boolean generic
    );

    @GetMapping("{cid}")
    List<SpecGroup> querySpecsByCid(@PathVariable("cid") Long cid);

}
