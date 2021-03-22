package com.youmei.goods.web.listener;

import com.youmei.goods.web.service.GoodsWebService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {
    @Autowired
    private GoodsWebService goodsWebService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "youshop.create.web.queue", durable = "true"),
            exchange = @Exchange(
                    value = "youshop.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void listenCreate(Long spuId) {
        if (spuId == null) {
            return;
        }
        this.goodsWebService.asyncExecute(spuId);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    value = "youshop.delete.web.queue", durable = "true"),
            exchange = @Exchange(
                    value = "youshop.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public void listenDelete(Long spuId) {
        if (spuId == null) {
            return;
        }
        this.goodsWebService.deleteHtml(spuId);
    }
}
