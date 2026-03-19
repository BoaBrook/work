package cn.stylefeng.guns.modular.firegas.handler;

import java.util.List;

import cn.stylefeng.guns.modular.datimsien.websocketClient.handler.DatimsienWebsocketHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.stylefeng.guns.modular.datimsien.websocketClient.dto.DatimsienWebSocketRecord;
import cn.stylefeng.guns.modular.firegas.service.FireGasAlarmService;
import lombok.extern.slf4j.Slf4j;

/**
 * 火气系统WebSocket消息处理器
 * 接收消息并处理报警
 * 
 * @author system
 */
@Slf4j
@Component
public class FireGasWebsocketHandler extends DatimsienWebsocketHandlerAdapter {

    @Autowired
    private FireGasAlarmService fireGasAlarmService;

    @Override
    public void onMessage(List<DatimsienWebSocketRecord> records) {
        try {
            // 调用火气系统报警服务处理消息
            fireGasAlarmService.processAlarm(records);
        } catch (Exception e) {
            log.error("处理火气系统WebSocket消息失败", e);
        }
    }
}
