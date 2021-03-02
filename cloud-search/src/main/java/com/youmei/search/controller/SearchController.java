package com.youmei.search.controller;

import com.youmei.common.pojo.PageResult;
import com.youmei.search.pojo.Goods;
import com.youmei.search.pojo.SearchRequest;
import com.youmei.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;


    @PostMapping("/page")
    public ResponseEntity<PageResult<Goods>> queryGoodByPage(@RequestBody SearchRequest searchRequest) {

        PageResult<Goods> result = this.searchService.search(searchRequest);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }
}
