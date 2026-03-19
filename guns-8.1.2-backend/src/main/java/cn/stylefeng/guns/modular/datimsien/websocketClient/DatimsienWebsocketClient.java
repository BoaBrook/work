package cn.stylefeng.guns.modular.datimsien.websocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import cn.stylefeng.guns.core.utils.CountMap;
import cn.stylefeng.guns.core.utils.KifUtils;
import cn.stylefeng.guns.modular.datimsien.DatimsienConfig;
import cn.stylefeng.guns.modular.datimsien.websocketClient.handler.DatimsienWebsocketHandler;
import cn.stylefeng.guns.modular.datimsien.websocketClient.service.DatimsienAsyncMessageProcessor;
import lombok.extern.slf4j.Slf4j;

@Order(10)
@Component
@Slf4j
public class DatimsienWebsocketClient implements CommandLineRunner {

    @Lazy
    @Autowired
    private DatimsienAsyncMessageProcessor messageProcessor;

    @Autowired
    private DatimsienConfig systemConfig;

    public static final String COMMAND_PING = "ping";
    public static final String COMMAND_TEMPLATE_REGISTER_DATA = "register{\"cmd\":\"datimsien_rt\",\"content\":{\"client_name\":\"Scada\",\"units\": %s}}";
    public static final String COMMAND_TEMPLATE_UNREGISTER_DATA = "unregister{\"cmd\":\"datimsien_rt\",\"content\":{\"client_name\":\"Scada\",\"units\": %s}}";

    private String url;
    private static final int SEND_COUNT_ONE_TIME = 500;
    private final long maxNumRetries = -1;
    private static final long delayBetweenRetries = 1000;

    private static DatimsienClient currentClient;

    private volatile boolean isRunning = true;
    private volatile boolean isSocketConnected = false;
    private volatile boolean shouldResendRegisteredUnits = false;

    private static Set<DatimsienWebsocketHandler> handlers = new HashSet<>();

    // save registered units, for reconnect register again
    private static CountMap<String> registeredUnits = new CountMap<>();

    private static List<String> toBeRegisteredUnits = new ArrayList<String>();
    private static List<String> toBeUnRegisteredUnits = new ArrayList<String>();

    public void registerWebsocketHandler(DatimsienWebsocketHandler handler) {
        if (!handlers.contains(handler)) {
            handlers.add(handler);
            // 初始化调用onStatusChanged，避免漏消息
            handler.onStatusChanged(isSocketConnected);
        }
    }

    public void registerUnit(String unit) {
        synchronized (toBeRegisteredUnits) {
            toBeRegisteredUnits.add(unit);
        }
    }

    public void registerUnits(Collection<String> units) {
        synchronized (toBeRegisteredUnits) {
            toBeRegisteredUnits.addAll(units);
        }
    }

    public void unRegisterUnit(String unit) {
        synchronized (toBeUnRegisteredUnits) {
            toBeUnRegisteredUnits.add(unit);
        }
    }

    public void unRegisterUnits(Collection<String> units) {
        synchronized (toBeUnRegisteredUnits) {
            toBeUnRegisteredUnits.addAll(units);
        }
    }

    public void sendTestMessage(String message) {
        if (!isSocketConnected || !currentClient.isOpen()) {
            throw new RuntimeException("Websocket not connected");
        }
        currentClient.onMessage(message);
    }

    public void resetRegistered(Collection<String> units) {
        synchronized (toBeUnRegisteredUnits) {
            synchronized (toBeRegisteredUnits) {
                toBeRegisteredUnits.clear();
                toBeUnRegisteredUnits.clear();

                for (String unit : units) {
                    if (!registeredUnits.contains(unit)) {
                        toBeRegisteredUnits.add(unit);
                    }
                }
                for (String unit : registeredUnits.keySet()) {
                    if (!units.contains(unit)) {
                        toBeUnRegisteredUnits.add(unit);
                    }
                }
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {
        start();
    }

    public void start() {
        // 必须要在新线程里启动，否则会导致阻塞主线程
        new Thread() {
            @Override
            public void run() {
                try {
                    startInternal();
                } catch (Exception e) {
                    log.error("Error in start thread", e);
                }
            }
        }.start();
    }

    public void startInternal() throws Exception {
        url = systemConfig.getWebsocketURL();
        long attempt = 0;
        while (isRunning) {
            if (isSocketConnected) {
                try {
                    sendRequest(COMMAND_TEMPLATE_UNREGISTER_DATA, toBeUnRegisteredUnits, false);
                    sendRequest(COMMAND_TEMPLATE_REGISTER_DATA, toBeRegisteredUnits, true);

                    Thread.sleep(2000);
                } catch (Exception e) {
                    log.error("Register unit failed:{}", e);
                }
                continue;
            }
            try {
                if (currentClient == null) {
                    currentClient = new DatimsienClient();
                    log.info("Connecting to Datimsien server {}...", url);
                    isSocketConnected = currentClient.connectBlocking();
                } else {
                    log.info("reconnecting to Datimsien server {}...", url);
                    isSocketConnected = currentClient.reconnectBlocking();
                }
                log.info("connect/reconnecting to Datimsien server {} time, end: {}.", attempt, isSocketConnected);
                if (!isSocketConnected) {
                    attempt++;
                    Thread.sleep(delayBetweenRetries);
                } else {
                    attempt = 0;
                }
            } catch (Exception e) {
                log.info("Connecting to Datimsien server {} failed. error: {}", url, e.getMessage());
                // if we dropped out of this loop due to an EOF, sleep and retry
                if (isRunning) {
                    attempt++;
                    if (maxNumRetries == -1 || attempt < maxNumRetries) {
                        log.warn("Lost connection to server socket. Retrying in " + delayBetweenRetries + " msecs...");
                        Thread.sleep(delayBetweenRetries);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void sendRequest(String commandTemplate, List<String> data, boolean isReg) {
        synchronized (data) {
            if (!data.isEmpty()) {
                try {
                    List<List<String>> lists = KifUtils.splitList(data, SEND_COUNT_ONE_TIME);
                    for (List<String> list : lists) {
                        Set<String> toBeSend = new HashSet<>();
                        for (String unit : list) {
                            if (isReg) { // register
                                if (!registeredUnits.contains(unit)) {
                                    toBeSend.add(unit);
                                }
                                registeredUnits.add(unit);
                            } else { // unregister
                                registeredUnits.remove(unit);
                                if (!registeredUnits.contains(unit)) {
                                    toBeSend.add(unit);
                                }
                            }
                        }
                        if (!toBeSend.isEmpty()) {
                            String msg = String.format(commandTemplate, JSON.toJSONString(toBeSend));
                            log.info("send datimsien subscribe:{}", msg.length() < 500 ? msg : msg.substring(0, 500) + "...");
                            currentClient.send(msg);
                        }
                    }
                    data.clear();
                } catch (Exception e) {
                    // websocket 异常需要抛出，上层会重连
                    if (e instanceof WebsocketNotConnectedException) {
                        isSocketConnected = false;
                    } else { // 其它异常是数据问题，清除数据
                        log.error("error to send isReg: {} data :{},error:{}", isReg, data, e);
                        data.clear();
                    }
                }
            }
        }
    }

    @PreDestroy
    public void stop() {
        isRunning = false;
        // we need to close the socket as well, because the Thread.interrupt() function will
        // not wake the thread in the socketStream.read() method when blocked.
        if (currentClient != null) {
            isSocketConnected = false;
            currentClient.close();
            currentClient = null;
        }
    }

    public int getWebsocketStatus() {
        return isSocketConnected ? 1 : 0;
    }

    class DatimsienClient extends WebSocketClient implements AutoCloseable {
        private Thread heartBeatThread;

        public DatimsienClient() throws URISyntaxException {
            super(new URI(url));
            heartBeatThread = new Thread() {
                @Override
                public void run() {
                    while (isRunning) {
                        if (isSocketConnected) {
                            send(COMMAND_PING);
                        }
                        try {
                            Thread.sleep(55 * 1000); // 每分钟发送心跳
                        } catch (InterruptedException e) {
                        }
                    }
                }
            };
            heartBeatThread.start();
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            isSocketConnected = true;
            log.info("new Datimsien connection opened, isSocketConnected:{}", isSocketConnected);
            // send registeredUnits if reconnect
            if (shouldResendRegisteredUnits && !registeredUnits.isEmpty()) {
                List<List<String>> lists = KifUtils.splitList(new ArrayList<>(registeredUnits.keySet()), SEND_COUNT_ONE_TIME);
                for (List<String> list : lists) {
                    String msg = String.format(COMMAND_TEMPLATE_REGISTER_DATA, JSON.toJSONString(list));
                    log.info("resend datimsien subscribe reconnected:{}", msg);
                    currentClient.send(msg);
                }
            }
            handlers.forEach(h -> {
                h.onOpen();
                h.onStatusChanged(isSocketConnected);
            });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            isSocketConnected = false;
            shouldResendRegisteredUnits = registeredUnits.isEmpty() ? false : true;
            log.info("Datimsien closed with exit code :{} additional info:{} ", code, reason);
            handlers.forEach(h -> {
                h.onClose(code, reason, remote);
                h.onStatusChanged(isSocketConnected);
            });
        }

        @Override
        public void onMessage(String message) {
            log.trace("websocket message:{}", message);
            messageProcessor.callMessageHandlers(message, handlers);
        }

        @Override
        public void onMessage(ByteBuffer message) {
            log.error("Should not happen -- Datimsien received ByteBuffer:{}", message);
        }

        @Override
        public void onError(Exception ex) {
            log.error("Datimsien error occurred:" + ex);
            handlers.forEach(h -> {
                h.onError(ex);
            });
        }
    }
}
