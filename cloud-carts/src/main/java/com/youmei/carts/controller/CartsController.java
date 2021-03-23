package com.youmei.carts.controller;

import com.youmei.carts.pojo.SkuInCarts;
import com.youmei.carts.service.CartsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartsController {

    @Autowired
    private CartsService cartsService;

    @PostMapping
    public ResponseEntity<Void> addSkuToCarts(@RequestBody SkuInCarts skuInCarts) {
        if (skuInCarts.getSkuId() != null && skuInCarts.getNum() > 0) {
            this.cartsService.addSkuToCarts(skuInCarts);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping
    public ResponseEntity<List<SkuInCarts>> getSkuInCarts() {
        List<SkuInCarts> skuInCartsList = this.cartsService.getSkuInCarts();
        return ResponseEntity.ok(skuInCartsList);
    }

    @PutMapping("/add")
    public ResponseEntity<Void> addNums(@RequestBody SkuInCarts skuInCarts) {
        this.cartsService.addNums(skuInCarts);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteSku(@RequestBody List<Long> skuIds) {
        this.cartsService.deleteSkuList(skuIds);
        return ResponseEntity.noContent().build();
    }
}
