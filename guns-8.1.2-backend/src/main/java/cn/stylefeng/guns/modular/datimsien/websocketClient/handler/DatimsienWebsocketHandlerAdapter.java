package cn.stylefeng.guns.modular.datimsien.websocketClient.handler;

import java.util.List;

import cn.stylefeng.guns.modular.datimsien.websocketClient.dto.DatimsienWebSocketRecord;

import lombok.Getter;

public abstract class DatimsienWebsocketHandlerAdapter implements DatimsienWebsocketHandler {
    @Getter
    private boolean isSocketConnected = false;

    @Override
    public void onOpen() {
    }

    @Override
    public void onMessage(List<DatimsienWebSocketRecord> records) {
    }

    @Override
    public void onError(Exception ex) {
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
    }

    @Override
    public void onStatusChanged(boolean isSocketConnected) {
        this.isSocketConnected = isSocketConnected;
    }
}
