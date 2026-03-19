package cn.stylefeng.guns.modular.index.deviceStatusMonitor;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * PostgreSQL数据库变化监听器
 * 使用PostgreSQL的NOTIFY/LISTEN机制监听设备状态变化
 */
@Component
public class DatabaseChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseChangeListener.class);

    private final DataSource dataSource;
    private final WebSocketService webSocketService;
    private final ExecutorService executorService;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Connection connection;

    @Autowired
    public DatabaseChangeListener(DataSource dataSource, WebSocketService webSocketService) {
        this.dataSource = dataSource;
        this.webSocketService = webSocketService;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 启动监听
     */
    @PostConstruct
    public void startListening() {
        if (running.compareAndSet(false, true)) {
            executorService.submit(this::listenForNotifications);
            logger.info("PostgreSQL监听器已启动");
        }
    }

    /**
     * 停止监听
     */
    @PreDestroy
    public void stopListening() {
        if (running.compareAndSet(true, false)) {
            executorService.shutdown();
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("关闭数据库连接失败", e);
                }
            }
            logger.info("PostgreSQL监听器已停止");
        }
    }

    /**
     * 监听数据库通知
     */
    private void listenForNotifications() {
        int reconnectDelay = 1000; // 初始重连延迟
        final int maxReconnectDelay = 60000; // 最大重连延迟

        while (running.get()) {
            try {
                // 获取数据库连接
                connection = dataSource.getConnection();
                PGConnection pgConnection = connection.unwrap(PGConnection.class);

                try (Statement stmt = connection.createStatement()) {
                    // 监听设备状态变更通知
                    stmt.execute("LISTEN device_status_changed");
                    logger.info("开始监听PostgreSQL通知: device_status_changed");

                    // 重置重连延迟
                    reconnectDelay = 1000;

                    // 主监听循环
                    while (running.get()) {
                        try {
                            // 等待并获取通知
                            PGNotification[] notifications = pgConnection.getNotifications(5000);

                            if (notifications != null) {
                                for (PGNotification notification : notifications) {
                                    processNotification(notification);
                                }
                            }

                            // 发送心跳以保持连接活跃
                            connection.createStatement().execute("SELECT 1");

                        } catch (SQLException e) {
                            if (isConnectionValid()) {
                                logger.error("处理通知时发生错误", e);
                                // 短暂休眠后继续
                                Thread.sleep(1000);
                            } else {
                                logger.warn("数据库连接已断开，尝试重连...");
                                break; // 跳出内层循环，重新建立连接
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("数据库连接失败", e);
            } catch (Exception e) {
                logger.error("监听线程发生异常", e);
            }

            // 连接断开后的重连逻辑
            if (running.get()) {
                try {
                    logger.info("等待{}ms后重连...", reconnectDelay);
                    Thread.sleep(reconnectDelay);

                    // 指数退避重连
                    reconnectDelay = Math.min(reconnectDelay * 2, maxReconnectDelay);

                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * 处理通知
     */
    private void processNotification(PGNotification notification) {
        try {
            String channel = notification.getName();
            String payload = notification.getParameter();

            logger.debug("收到PostgreSQL通知 - 频道: {}, 负载: {}", channel, payload);

            // 解析通知负载
            // 假设payload格式为: deviceId:status 或 JSON格式
            if ("device_status_changed".equals(channel)) {
                // 解析设备ID和状态
                String[] parts = payload.split(":");
                if (parts.length >= 2) {
                    String deviceId = parts[0];
                    String status = parts[1];

                    // 通过WebSocket推送给前端
                    webSocketService.broadcastDeviceStatus(deviceId, status);
                    logger.info("已推送设备状态更新: deviceId={}, status={}", deviceId, status);
                } else {
                    logger.warn("通知负载格式错误: {}", payload);
                }
            }

        } catch (Exception e) {
            logger.error("处理通知时发生错误", e);
        }
    }

    /**
     * 检查连接是否有效
     */
    private boolean isConnectionValid() {
        if (connection == null) {
            return false;
        }
        try (Statement stmt = connection.createStatement()) {
            return stmt.execute("SELECT 1");
        } catch (SQLException e) {
            return false;
        }
    }
}
