package cn.stylefeng.guns.modular.broadcast.remoteClient.client;

import cn.stylefeng.guns.modular.broadcast.remoteClient.config.BroadcastConfig;
import cn.stylefeng.guns.modular.broadcast.remoteClient.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * IP广播系统客户端
 * 提供完整的广播系统接口调用功能
 */
@Slf4j
public class BroadcastClient {

    /**
     * -- SETTER --
     *  设置配置
     */
    @Setter
    private BroadcastConfig config;

    /**
     * -- SETTER --
     *  设置 RestTemplate
     */
    @Setter
    private RestTemplate restTemplate;

    /**
     * -- SETTER --
     *  设置 ObjectMapper
     */
    @Setter
    private ObjectMapper objectMapper;

    /**
     * 当前有效的Token
     */
    private String currentToken;

    /**
     * Token过期时间戳（毫秒）
     */
    private long tokenExpireTime;

    /**
     * 默认构造函数
     */
    public BroadcastClient() {
    }

    /**
     * 构造函数，接受配置和依赖
     */
    public BroadcastClient(BroadcastConfig config, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 初始化客户端
     */
    public void initialize(BroadcastConfig config, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * ============================================
     * 一、服务器登录
     * ============================================
     */

    /**
     * 获取Token
     */
    public AuthResponse getAuthToken() {
        try {
            String url = config.getBaseUrl() + "/auth";
            String params = String.format("?name=%s&password=%s&force=1",
                    config.getUsername(), config.getPassword());

            ResponseEntity<String> response = restTemplate.getForEntity(url + params, String.class);

            AuthResponse authResponse = objectMapper.readValue(response.getBody(), AuthResponse.class);

            if (authResponse != null && authResponse.getResult() == 200) {
                this.currentToken = authResponse.getToken();
                // Token有效期120分钟，设置60分钟后过期（提前刷新）
                this.tokenExpireTime = System.currentTimeMillis() + 60 * 60 * 1000;
                log.info("获取广播系统Token成功");
            }

            return authResponse;
        } catch (Exception e) {
            log.error("获取广播系统Token失败", e);
            throw new RuntimeException("获取广播系统Token失败", e);
        }
    }

    /**
     * 刷新Token
     */
    public AuthResponse refreshToken() {
        try {
            String url = config.getBaseUrl() + "/auth/refresh";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", currentToken);
            headers.set("accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            AuthResponse authResponse = objectMapper.readValue(response.getBody(), AuthResponse.class);

            if (authResponse != null && authResponse.getResult() == 200) {
                this.currentToken = authResponse.getToken();
                this.tokenExpireTime = System.currentTimeMillis() + 60 * 60 * 1000;
                log.info("刷新广播系统Token成功");
            }

            return authResponse;
        } catch (Exception e) {
            log.error("刷新广播系统Token失败", e);
            throw new RuntimeException("刷新广播系统Token失败", e);
        }
    }

    /**
     * 获取有效的Token（自动刷新）
     */
    private String getValidToken() {
        if (currentToken == null || System.currentTimeMillis() >= tokenExpireTime) {
            if (currentToken == null) {
                getAuthToken();
            } else {
                refreshToken();
            }
        }
        return currentToken;
    }

    /**
     * 登录（便捷方法）
     */
    public String login(String username, String password) {
        this.config.setUsername(username);
        this.config.setPassword(password);
        AuthResponse response = getAuthToken();
        return response != null ? response.getToken() : null;
    }

    /**
     * 获取所有终端（便捷方法）
     */
    public List<TerminalInfo> getAllTerminals() {
        BroadcastResponse<TerminalInfoArray> response = getTerminalInfo();
        return response != null && response.getData() != null ? response.getData().getEndPointsArray() : null;
    }

    /**
     * 获取指定终端（便捷方法）
     */
    public List<TerminalInfo> getTerminalsById(List<Integer> endpointIds) {
        BroadcastResponse<TerminalInfoArray> response = getSpecificTerminalInfo(endpointIds, null);
        return response != null && response.getData() != null ? response.getData().getEndPointsArray() : null;
    }

    /**
     * 获取指定分组终端（便捷方法）
     */
    public List<TerminalInfo> getTerminalsByGroupId(List<Integer> endpointGroupIds) {
        BroadcastResponse<TerminalInfoArray> response = getSpecificTerminalInfo(null, endpointGroupIds);
        return response != null && response.getData() != null ? response.getData().getEndPointsArray() : null;
    }

    /**
     * 获取所有分组（便捷方法）
     */
    public List<GroupInfo> getAllTerminalsGroups() {
        BroadcastResponse<List<GroupInfo>> response = getAllTerminalGroups(true);
        return response != null ? response.getData() : null;
    }

    /**
     * 获取TTS引擎列表（便捷方法）
     */
    public List<TTSEngineInfoArray.TTSEngineInfo> getTTSEngineList() {
        BroadcastResponse<TTSEngineInfoArray> response = getTTSEngineInfo();
        return response != null && response.getData() != null ? response.getData().getTTSEngineInfo() : null;
    }

    /**
     * 获取任务状态（便捷方法）
     */
    public TaskInfo getTaskStatus(String taskId) {
        BroadcastResponse<TaskInfoArray> response = getTaskStatus();
        if (response != null && response.getData() != null) {
            for (TaskInfo task : response.getData().getTaskInfoArray()) {
                if (taskId.equals(task.getTaskID())) {
                    return task;
                }
            }
        }
        return null;
    }

    /**
     * 获取系统日志（便捷方法）
     */
    public LogData getSystemLogs(int page, int limit, String startDate, String endDate, String orderColumn, String orderType, String searchIpAddress, String searchPlatform, String searchDescription, boolean withUser) {
        try {
            String url = config.getBaseUrl() + "/logs/system?page=" + page + "&limit=" + limit;
            if (startDate != null) url += "&start_date=" + startDate;
            if (endDate != null) url += "&end_date=" + endDate;
            if (orderColumn != null) url += "&orderColumn=" + orderColumn;
            if (orderType != null) url += "&orderType=" + orderType;
            if (searchIpAddress != null) url += "&search_ip_address=" + searchIpAddress;
            if (searchPlatform != null) url += "&search_platform=" + searchPlatform;
            if (searchDescription != null) url += "&search_description=" + searchDescription;
            if (withUser) url += "&withUser=true";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            BroadcastResponse<LogData> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<BroadcastResponse<LogData>>() {});

            return result != null ? result.getData() : null;
        } catch (Exception e) {
            log.error("获取系统日志失败", e);
            throw new RuntimeException("获取系统日志失败", e);
        }
    }

    /**
     * 获取终端日志（便捷方法）
     */
    public LogData getTerminalLogs(int page, int limit, String startDate, String endDate, String orderColumn, String orderType, String searchIpAddress, String searchPlatform, String searchDescription, boolean withUser) {
        try {
            String url = config.getBaseUrl() + "/logs/terminal?page=" + page + "&limit=" + limit;
            if (startDate != null) url += "&start_date=" + startDate;
            if (endDate != null) url += "&end_date=" + endDate;
            if (orderColumn != null) url += "&orderColumn=" + orderColumn;
            if (orderType != null) url += "&orderType=" + orderType;
            if (searchIpAddress != null) url += "&search_ip_address=" + searchIpAddress;
            if (searchPlatform != null) url += "&search_platform=" + searchPlatform;
            if (searchDescription != null) url += "&search_description=" + searchDescription;
            if (withUser) url += "&withUser=true";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            BroadcastResponse<LogData> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<BroadcastResponse<LogData>>() {});

            return result != null ? result.getData() : null;
        } catch (Exception e) {
            log.error("获取终端日志失败", e);
            throw new RuntimeException("获取终端日志失败", e);
        }
    }

    /**
     * 获取任务日志（便捷方法）
     */
    public LogData getTaskLogs(int page, int limit, String startDate, String endDate, String orderColumn, String orderType, String searchIpAddress, String searchPlatform, String searchDescription, boolean withUser) {
        try {
            String url = config.getBaseUrl() + "/logs/task?page=" + page + "&limit=" + limit;
            if (startDate != null) url += "&start_date=" + startDate;
            if (endDate != null) url += "&end_date=" + endDate;
            if (orderColumn != null) url += "&orderColumn=" + orderColumn;
            if (orderType != null) url += "&orderType=" + orderType;
            if (searchIpAddress != null) url += "&search_ip_address=" + searchIpAddress;
            if (searchPlatform != null) url += "&search_platform=" + searchPlatform;
            if (searchDescription != null) url += "&search_description=" + searchDescription;
            if (withUser) url += "&withUser=true";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            BroadcastResponse<LogData> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<BroadcastResponse<LogData>>() {});

            return result != null ? result.getData() : null;
        } catch (Exception e) {
            log.error("获取任务日志失败", e);
            throw new RuntimeException("获取任务日志失败", e);
        }
    }

    /**
     * 获取呼叫日志（便捷方法）
     */
    public LogData getCallLogs(int page, int limit, String startDate, String endDate, String orderColumn, String orderType, String searchIpAddress, String searchPlatform, String searchDescription, boolean withUser) {
        try {
            String url = config.getBaseUrl() + "/logs/call?page=" + page + "&limit=" + limit;
            if (startDate != null) url += "&start_date=" + startDate;
            if (endDate != null) url += "&end_date=" + endDate;
            if (orderColumn != null) url += "&orderColumn=" + orderColumn;
            if (orderType != null) url += "&orderType=" + orderType;
            if (searchIpAddress != null) url += "&search_ip_address=" + searchIpAddress;
            if (searchPlatform != null) url += "&search_platform=" + searchPlatform;
            if (searchDescription != null) url += "&search_description=" + searchDescription;
            if (withUser) url += "&withUser=true";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            BroadcastResponse<LogData> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<BroadcastResponse<LogData>>() {});

            return result != null ? result.getData() : null;
        } catch (Exception e) {
            log.error("获取呼叫日志失败", e);
            throw new RuntimeException("获取呼叫日志失败", e);
        }
    }

    /**
     * 获取对讲日志（便捷方法）
     */
    public LogData getTalkLogs(int page, int limit, String startDate, String endDate, String orderColumn, String orderType, String searchIpAddress, String searchPlatform, String searchDescription, boolean withUser) {
        try {
            String url = config.getBaseUrl() + "/logs/talk?page=" + page + "&limit=" + limit;
            if (startDate != null) url += "&start_date=" + startDate;
            if (endDate != null) url += "&end_date=" + endDate;
            if (orderColumn != null) url += "&orderColumn=" + orderColumn;
            if (orderType != null) url += "&orderType=" + orderType;
            if (searchIpAddress != null) url += "&search_ip_address=" + searchIpAddress;
            if (searchPlatform != null) url += "&search_platform=" + searchPlatform;
            if (searchDescription != null) url += "&search_description=" + searchDescription;
            if (withUser) url += "&withUser=true";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            BroadcastResponse<LogData> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<BroadcastResponse<LogData>>() {});

            return result != null ? result.getData() : null;
        } catch (Exception e) {
            log.error("获取对讲日志失败", e);
            throw new RuntimeException("获取对讲日志失败", e);
        }
    }

    /**
     * ============================================
     * 二、数据获取
     * ============================================
     */

    /**
         * 获取终端信息
         */
    public BroadcastResponse<TerminalInfoArray> getTerminalInfo() {
        return executeForwarder("c2ls_get_server_terminals_status", null, TerminalInfoArray.class);
    }
    /**
     * 获取正在执行的任务信息
     */
    public BroadcastResponse<TaskInfoArray> getTaskStatus() {
        return executeForwarder("c2ls_get_task_status", null,  TaskInfoArray.class);
    }

    /**
     * 获取所有终端分组
     */
    public BroadcastResponse<List<GroupInfo>> getAllTerminalGroups(boolean withTerminals) {
        try {
            String url = config.getBaseUrl() + "/terminals-groups/all?withTerminals=" + withTerminals;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            BroadcastResponse<List<GroupInfo>> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<BroadcastResponse<List<GroupInfo>>>() {});

            return result;
        } catch (Exception e) {
            log.error("获取终端分组失败", e);
            throw new RuntimeException("获取终端分组失败", e);
        }
    }

    /**
     * 获取服务器音频列表
     */
    public BroadcastResponse<MusicInfo> getServerMusicList() {
        return executeForwarder("c2ls_get_server_music_list", null, MusicInfo.class);
    }

    /**
     * 获取服务器TTS语音引擎
     */
    public BroadcastResponse<TTSEngineInfoArray> getTTSEngineInfo() {
        return executeForwarder("c2ls_get_tts_engine_info", null, TTSEngineInfoArray.class);
    }

    /**
     * 获取指定终端信息
     */
    public BroadcastResponse<TerminalInfoArray> getSpecificTerminalInfo(List<Integer> endpointIds,
                                                                          List<Integer> endpointGroupIds) {
        Map<String, Object> data = new HashMap<>();
        // 修复：根据 PDF 文档，两个参数二选一，不能同时为空
        if (endpointIds != null && !endpointIds.isEmpty()) {
            data.put("EndpointIDs", endpointIds);
            data.put("EndpointGroupIDs", new ArrayList<>());
        } else if (endpointGroupIds != null && !endpointGroupIds.isEmpty()) {
            data.put("EndpointIDs", new ArrayList<>());
            data.put("EndpointGroupIDs", endpointGroupIds);
        } else {
            throw new IllegalArgumentException("EndpointIDs 和 EndpointGroupIDs 必须提供其中一个");
        }

        return executeTerminal("c2ls_get_server_terminals_status", data, TerminalInfoArray.class);
    }

    /**
     * 获取远程播放任务状态
     */
    public BroadcastResponse<TaskPlayStatus> getTaskPlayStatus(String taskId) {
        Map<String, Object> data = new HashMap<>();
        data.put("TaskID", taskId);

        return executeForwarder("c2ls_get_task_play_status", data, TaskPlayStatus.class);
    }

    /**
     * 向会话添加终端
     */
    public BroadcastResponse<Object> addTerminalsToTask(String taskId, String endpointsList) {
        Map<String, Object> data = new HashMap<>();
        data.put("TaskID", taskId);
        data.put("EndPointsList", endpointsList);
        data.put("EndPointsAddditionalProp", "");

        return executeForwarder("c2ls_add_terminals_to_task", data,  Object.class);
    }

    /**
     * 从会话中移除终端
     */
    public BroadcastResponse<Object> removeTerminalsFromTask(String taskId, String endpointsList) {
        Map<String, Object> data = new HashMap<>();
        data.put("TaskID", taskId);
        data.put("EndPointsList", endpointsList);

        return executeForwarder("c2ls_remove_terminals_from_task", data, Object.class);
    }

    /**
     * ============================================
     * 三、任务控制
     * ============================================
     */

    /**
     * 点播服务器音乐
     */
    public BroadcastResponse<TaskResult> playServerMusic(List<Integer> endpointIds,
                                                           List<Integer> endpointGroupIds,
                                                           List<Integer> musicIds,
                                                           List<Integer> musicGroupIds,
                                                           int volume,
                                                           String playMode,
                                                           int priority) {
        String taskId = generateTaskId();

        Map<String, Object> data = new HashMap<>();
        data.put("EndPointsAdditionalProp", "");
        // 修复：直接传递 List，Jackson 会自动转换为数组
        data.put("EndPointIDs", endpointIds != null && !endpointIds.isEmpty() ? endpointIds : new ArrayList<>());
        data.put("EndPointGroupIDs", endpointGroupIds != null && !endpointGroupIds.isEmpty() ? endpointGroupIds : new ArrayList<>());
        data.put("MusicIDs", musicIds != null && !musicIds.isEmpty() ? musicIds : new ArrayList<>());
        data.put("MusicGroupIDs", musicGroupIds != null && !musicGroupIds.isEmpty() ? musicGroupIds : new ArrayList<>());
        data.put("TaskID", taskId);
        data.put("TaskName", "音乐_" + System.currentTimeMillis());
        data.put("Priority", priority);
        data.put("Volume", volume);
        data.put("PlayMode", playMode);

        return executeForwarder("c2ls_mobile_terminal_damand_music", data,  TaskResult.class);
    }

    /**
     * 点播HTTP流媒体音乐
     */
    public BroadcastResponse<TaskResult> playMediaStream(String endpointsList,
                                                          String mediaStreamUrls,
                                                          int volume,
                                                          String playMode,
                                                          int priority) {
        String taskId = generateTaskId();

        Map<String, Object> data = new HashMap<>();
        data.put("TaskName", "流媒体_" + System.currentTimeMillis());
        data.put("TaskID", taskId);
        data.put("MediaStreamUrls", mediaStreamUrls);
        data.put("EndPointsList", endpointsList);
        data.put("TaskPriority", priority);
        data.put("Volume", volume);
        data.put("PlayMode", playMode);
        data.put("EndPointsAdditionalProp", "");

        return executeForwarder("c2ls_damand_media_stream", data, TaskResult.class);
    }

    /**
     * 发起TTS播放（文本播放）
     */
    public BroadcastResponse<TaskResult> playTTS(String endpointsList,
                                                  String ttsEngineName,
                                                  int ttsSpeed,
                                                  int repeatTime,
                                                  String textContent,
                                                  int volume,
                                                  int priority) {
        String taskId = generateTaskId();

        Map<String, Object> data = new HashMap<>();
        data.put("EndPointsAdditionalProp", "");
        data.put("EndPointsList", endpointsList);
        data.put("TTSEngineName", ttsEngineName);
        data.put("TTSSpeed", ttsSpeed);
        data.put("RepeatTime", repeatTime);
        data.put("TaskID", taskId);
        data.put("TaskName", "文本_" + System.currentTimeMillis());
        data.put("TaskPriority", priority);
        data.put("TextContent", textContent);
        data.put("Volume", volume);

        return executeForwarder("c2ls_server_tts_mp3_play", data, TaskResult.class);
    }

    /**
     * 控制发起人工广播
     */
    public BroadcastResponse<TaskResult> startBroadcast(int initiatorEndpointId,
                                                        String receiverList,
                                                        int volume,
                                                        int priority) {
        String taskId = generateTaskId();

        Map<String, Object> data = new HashMap<>();
        data.put("EndPointsAdditionalProp", "");
        data.put("InitiatorEndPointID", initiatorEndpointId);
        data.put("ReceiverList", receiverList);
        data.put("TaskID", taskId);
        data.put("TaskPriority", priority);
        data.put("Volume", volume);

        return executeForwarder("c2ls_broadcast_task", data, TaskResult.class);
    }

    /**
     * 控制发起人工对讲
     */
    public BroadcastResponse<TaskResult> startTalk(int callingEndpointId,
                                                    int calledEndpointId,
                                                    int volume,
                                                    int priority) {
        String taskId = generateTaskId();

        Map<String, Object> data = new HashMap<>();
        data.put("CalledEndPointID", calledEndpointId);
        data.put("CallingEndPointID", callingEndpointId);
        data.put("EndPointsAdditionalProp", "");
        data.put("TaskID", taskId);
        data.put("TaskPriority", priority);
        data.put("Volume", volume);

        return executeForwarder("c2ls_talk_task", data, TaskResult.class);
    }

    /**
     * 设置任务音量
     */
    public BroadcastResponse<Object> setTaskVolume(String taskId, int volume) {
        Map<String, Object> data = new HashMap<>();
        data.put("TaskID", taskId);
        data.put("Volume", volume);

        return executeForwarder("c2ls_set_task_volume", data, Object.class);
    }

    /**
     * 设置终端音量
     */
    public BroadcastResponse<Object> setTerminalVolume(String terminalId, int volume) {
        Map<String, Object> data = new HashMap<>();
        data.put("TerminalID", terminalId);
        data.put("Volume", volume);

        return executeForwarder("c2ls_set_terminal_volume", data, Object.class);
    }

    /**
     * 停止任务
     */
    public BroadcastResponse<Object> stopTask(String taskId) {
        Map<String, Object> data = new HashMap<>();
        data.put("TaskID", taskId);

        return executeForwarder("c2ls_stop_task", data, Object.class);
    }

    /**
     * 远程播放任务控制
     */
    public BroadcastResponse<Object> controlRemoteTask(String taskId, String controlCode, String controlValue) {
        Map<String, Object> data = new HashMap<>();
        data.put("TaskID", taskId);
        data.put("ControlCode", controlCode);
        data.put("ControlValue", controlValue);

        return executeForwarder("c2ls_control_remote_task", data, Object.class);
    }

    /**
     * ============================================
     * 四、数据管理
     * ============================================
     */

    /**
     * 创建终端分组
     */
    public BroadcastResponse<GroupInfo> createTerminalGroup(String name, int callCode, List<Integer> terminals) {
        try {
            String url = config.getBaseUrl() + "/terminals-groups";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("call_code", callCode);

            List<Map<String, Integer>> terminalList = new ArrayList<>();
            for (Integer terminalId : terminals) {
                Map<String, Integer> terminal = new HashMap<>();
                terminal.put("terminals_id", terminalId);
                terminalList.add(terminal);
            }
            body.put("terminals", terminalList);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            BroadcastResponse<GroupInfo> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<BroadcastResponse<GroupInfo>>() {});

            return result;
        } catch (Exception e) {
            log.error("创建终端分组失败", e);
            throw new RuntimeException("创建终端分组失败", e);
        }
    }

    /**
     * 创建媒体库
     */
    public BroadcastResponse<MediaGroupInfo> createMediaGroup(String name, int isPublic) {
        try {
            String url = config.getBaseUrl() + "/medias-groups";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("Content-Type", "application/json");

            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("is_public", isPublic);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            BroadcastResponse<MediaGroupInfo> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<BroadcastResponse<MediaGroupInfo>>() {});

            return result;
        } catch (Exception e) {
            log.error("创建媒体库失败", e);
            throw new RuntimeException("创建媒体库失败", e);
        }
    }

    /**
     * 上传MP3文件
     */
    public BroadcastResponse<MediaInfo> uploadMP3(int groupId, byte[] fileData, String fileName) {
        try {
            String url = config.getBaseUrl() + "/medias/upload/" + groupId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(fileData) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            });

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            BroadcastResponse<MediaInfo> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<BroadcastResponse<MediaInfo>>() {});

            return result;
        } catch (Exception e) {
            log.error("上传MP3文件失败", e);
            throw new RuntimeException("上传MP3文件失败", e);
        }
    }

    /**
     * ============================================
     * 五、日志相关
     * ============================================
     */

    /**
     * 获取系统日志
     */
    public BroadcastResponse<LogData> getSystemLogs(int page, int limit, String startDate, String endDate) {
        try {
            String url = config.getBaseUrl() + "/logs/system?page=" + page + "&limit=" + limit;
            if (startDate != null) url += "&start_date=" + startDate;
            if (endDate != null) url += "&end_date=" + endDate;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            BroadcastResponse<LogData> result = objectMapper.readValue(response.getBody(),
                    new TypeReference<BroadcastResponse<LogData>>() {});

            return result;
        } catch (Exception e) {
            log.error("获取系统日志失败", e);
            throw new RuntimeException("获取系统日志失败", e);
        }
    }

    /**
     * ============================================
     * 通用方法
     * ============================================
     */

    /**
     * 执行forwarder接口（通用方法）
     */
    private <T> BroadcastResponse<T> executeForwarder(String actionCode, Map<String, Object> data, Class<T> dataType) {
        try {
            String url = config.getBaseUrl() + "/ws/forwarder";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("accept", "application/json");
            headers.set("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("company", "BL");
            requestBody.put("actioncode", actionCode);
            requestBody.put("token", getValidToken());
            requestBody.put("data", data != null ? data : new HashMap<>());
            requestBody.put("result", 0);
            requestBody.put("return_message", "");
            requestBody.put("sign", "");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            BroadcastResponse<T> result = objectMapper.readValue(response.getBody(),
                    objectMapper.getTypeFactory().constructParametricType(BroadcastResponse.class, dataType));

            return result;
        } catch (Exception e) {
            log.error("执行forwarder接口失败，actionCode: {}", actionCode, e);
            throw new RuntimeException("执行forwarder接口失败", e);
        }
    }

    /**
     * 执行forwarder接口（通用方法）
     */
    private <T> BroadcastResponse<T> executeTerminal(String actionCode, Map<String, Object> data, Class<T> dataType) {
        try {
            String url = config.getBaseUrl() + "/ws/terminal";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", getValidToken());
            headers.set("accept", "application/json");
            headers.set("Content-Type", "application/json");

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("company", "BL");
            requestBody.put("actioncode", actionCode);
            requestBody.put("token", getValidToken());
            requestBody.put("data", data != null ? data : "");
            requestBody.put("result", 0);
            requestBody.put("return_message", "");
            requestBody.put("sign", "rand string");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            BroadcastResponse<T> result = objectMapper.readValue(response.getBody(),
                    objectMapper.getTypeFactory().constructParametricType(BroadcastResponse.class, dataType));

            return result;
        } catch (Exception e) {
            log.error("执行forwarder接口失败，actionCode: {}", actionCode, e);
            throw new RuntimeException("执行forwarder接口失败", e);
        }
    }

    /**
     * 生成任务ID
     */
    private String generateTaskId() {
        return "{" + UUID.randomUUID().toString().toUpperCase() + "}";
    }
}