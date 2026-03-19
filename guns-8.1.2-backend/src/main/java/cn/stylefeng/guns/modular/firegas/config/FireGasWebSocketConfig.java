package cn.stylefeng.guns.modular.firegas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.stylefeng.guns.modular.datimsien.websocketClient.DatimsienWebsocketClient;
import cn.stylefeng.guns.modular.firegas.handler.FireGasWebsocketHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 火气系统WebSocket配置
 * 注册WebSocket处理器
 * 
 * @author system
 */
@Slf4j
@Component
@Order(20)
public class FireGasWebSocketConfig implements CommandLineRunner {

    @Autowired
    private DatimsienWebsocketClient websocketClient;

    @Autowired
    private FireGasWebsocketHandler fireGasWebsocketHandler;

    @Override
    public void run(String... args) throws Exception {
        // 注册WebSocket处理器，处理火气系统报警
        websocketClient.registerWebsocketHandler(fireGasWebsocketHandler);
        log.info("火气系统WebSocket处理器注册成功");
    }
}
