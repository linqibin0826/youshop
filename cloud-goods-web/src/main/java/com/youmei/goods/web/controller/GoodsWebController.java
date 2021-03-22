package com.youmei.goods.web.controller;

import com.youmei.goods.web.service.GoodsWebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/item")
public class GoodsWebController {

    @Autowired
    private GoodsWebService goodsWebService;

    /**
     * 跳转到商品详情页
     * @param model
     * @param id
     * @return
     */
    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id")Long id){
        Map<String, Object> data = this.goodsWebService.loadData(id);
        model.addAllAttributes(data);

        // 创建静态页面
        this.goodsWebService.asyncExecute(id);
        return "item";
    }
}
