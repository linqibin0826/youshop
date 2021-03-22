package com.youmei.search.listener;

import com.youmei.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GoodsListener {
    @Autowired
    private SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "youshop.create.index.queue", durable = "true"),
            exchange = @Exchange(value = "youshop.item.exchange", type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"
            ),
            key = {"item.insert", "item.update"}))
    public void listenCreate(Long spuId) throws IOException {
        if (spuId != null) {
            this.searchService.createIndex(spuId);
        } else {
            return;
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "youshop.delete.index.queue", durable = "true"),
            exchange = @Exchange(
                    value = "youshop.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            key = {"item.delete"}))
    public void listenDelete(Long spuId) {
        this.searchService.deleteIndex(spuId);
    }
}
