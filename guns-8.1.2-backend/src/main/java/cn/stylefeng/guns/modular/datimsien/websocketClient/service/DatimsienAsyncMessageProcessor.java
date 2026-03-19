package cn.stylefeng.guns.modular.datimsien.websocketClient.service;

import java.util.List;
import java.util.Set;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;

import cn.stylefeng.guns.modular.datimsien.websocketClient.dto.DatimsienWebSocketRecord;
import cn.stylefeng.guns.modular.datimsien.websocketClient.handler.DatimsienWebsocketHandler;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DatimsienAsyncMessageProcessor {

    @Async
    public void callMessageHandlers(String message, Set<DatimsienWebsocketHandler> handlers) {
        List<DatimsienWebSocketRecord> records = JSON.parseArray(message, DatimsienWebSocketRecord.class);
        handlers.forEach(h -> {
            try {
                h.onMessage(records);
            } catch (Exception e) {
                log.error("error: {}, {}", h.getClass().getCanonicalName(), message, e);
            }
        });
    }
}
