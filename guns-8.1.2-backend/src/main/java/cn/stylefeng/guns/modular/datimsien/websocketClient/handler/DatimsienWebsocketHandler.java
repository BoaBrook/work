package cn.stylefeng.guns.modular.datimsien.websocketClient.handler;

import java.util.List;

import cn.stylefeng.guns.modular.datimsien.websocketClient.dto.DatimsienWebSocketRecord;

public interface DatimsienWebsocketHandler {

    void onOpen();

    void onMessage(List<DatimsienWebSocketRecord> records);

    void onError(Exception ex);

    void onClose(int code, String reason, boolean remote);

    void onStatusChanged(boolean isSocketConnected);
}
