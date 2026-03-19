package cn.stylefeng.guns.modular.accesscontrol.hikClient;


import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import cn.stylefeng.guns.database.entity.TAccessControlPersonnelBaseInfo;
import cn.stylefeng.guns.database.service.TAccessControlBaseInfoService;
import cn.stylefeng.guns.database.service.TAccessControlPersonnelBaseInfoService;
import cn.stylefeng.guns.modular.hikvision.Commom.ConfigFileUtil;
import cn.stylefeng.guns.modular.hikvision.NetSDKDemo.HCNetSDK;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.jna.ptr.IntByReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HikPersonSyncService {

    private final HikSdkManager hikSdkManager;
    private final HikUtil hikUtil;
    private final TAccessControlPersonnelBaseInfoService personnelService;
    private final TAccessControlBaseInfoService deviceService;

    /**
     * 同步人员数据（改造：循环分页查询全量数据，人脸查询复用userID）
     */
    public void syncPersonalData() {
        // 初始化SDK
        if (!hikSdkManager.initSdk()) {
            log.error("SDK初始化失败，终止人员同步");
            return;
        }
        // LambdaQueryWrapper<TAccessControlBaseInfo> wrapper = new LambdaQueryWrapper<>();
        // wrapper.eq(TAccessControlBaseInfo::getIpAddress,"171.7.99.86");
        // List<TAccessControlBaseInfo> deviceList = deviceService.list(wrapper);
        List<TAccessControlBaseInfo> deviceList = deviceService.list();
        HCNetSDK hcNetSDK = hikSdkManager.getHCNetSDK();
        List<TAccessControlPersonnelBaseInfo> syncList = new ArrayList<>();

        for (TAccessControlBaseInfo configDTO : deviceList) {
            int userID = -1;
            try {
                // 设备登录（仅登录一次，复用userID）
                userID = hikSdkManager.loginDevice(configDTO.getIpAddress(),
                        configDTO.getPort().shortValue(), configDTO.getAccessAccount(), configDTO.getAccessPassword());
                if (userID == -1) {
                    log.warn("设备登录失败，IP：{}", configDTO.getIpAddress());
                    continue;
                }

                // 分页查询参数初始化
                int pageSize = 30; // 每页查询数量
                int currentPosition = 0; // 当前查询起始位置
                boolean hasMoreData = true; // 是否还有更多数据

                // 循环分页查询全量人员数据
                while (hasMoreData) {
                    // 构建JSON请求路径
                    final int OUT_BUFFER_SIZE = 100 * 1024; // 对齐UserManage，统一为100KB
                    HCNetSDK.BYTE_ARRAY ptrByteArray = new HCNetSDK.BYTE_ARRAY(1024);    // 接口请求头缓冲区
                    String strInBuffer = "POST /ISAPI/AccessControl/UserInfo/Search?format=json";

                    // 修复：增加read()同步指针，对齐UserManage操作
                    ptrByteArray.read();
                    // 字符串拷贝到数组中
                    System.arraycopy(strInBuffer.getBytes(), 0, ptrByteArray.byValue, 0, strInBuffer.length());
                    ptrByteArray.write();

                    // 启动远程配置
                    int handler = hcNetSDK.NET_DVR_StartRemoteConfig(userID, HCNetSDK.NET_DVR_JSON_CONFIG,
                            ptrByteArray.getPointer(), strInBuffer.length(), null, null);

                    if (handler < 0) {
                        int errorCode = hcNetSDK.NET_DVR_GetLastError();
                        log.error("启动人员查询失败，设备IP：{}，错误码：{}，当前查询位置：{}",
                                configDTO.getIpAddress(), errorCode, currentPosition);
                        break;
                    }

                    try {
                        // 构建分页查询参数（核心改造：动态设置searchResultPosition）
                        Map<String, Object> parameter = new HashMap<>();
                        parameter.put("searchID", UUID.randomUUID()); // 随机查询id
                        parameter.put("maxResults", pageSize); // 每页查询数量
                        parameter.put("searchResultPosition", currentPosition); // 分页起始位置
                        String strInbuff = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/SearchUserInfoParam.json", parameter);
                        log.info("分页查询参数：{}，设备IP：{}", strInbuff, configDTO.getIpAddress());

                        // 发送查询请求
                        // 初始化输入缓冲区，存放查询JSON参数
                        HCNetSDK.BYTE_ARRAY ptrInbuff = new HCNetSDK.BYTE_ARRAY(strInbuff.length());
                        // 修复：增加read()同步指针
                        ptrInbuff.read();
                        System.arraycopy(strInbuff.getBytes(), 0, ptrInbuff.byValue, 0, strInbuff.length());
                        ptrInbuff.write();

                        // 初始化输出缓冲区，与UserManage保持一致（100KB）
                        HCNetSDK.BYTE_ARRAY respBuffer = new HCNetSDK.BYTE_ARRAY(OUT_BUFFER_SIZE);
                        IntByReference pInt = new IntByReference(0);

                        boolean isCurrentPageSuccess = false;
                        while (true) {
                            // 核心修复：第一个参数改为handler（远程配置句柄），而非userID
                            int state = hcNetSDK.NET_DVR_SendWithRecvRemoteConfig(handler,
                                    ptrInbuff.getPointer(), strInbuff.length(),
                                    respBuffer.getPointer(), OUT_BUFFER_SIZE, pInt);

                            if (state == -1) {
                                int errorCode = hcNetSDK.NET_DVR_GetLastError();
                                log.error("查询人员数据失败，错误码：{}，当前查询位置：{}", errorCode, currentPosition);
                                break;
                            } else if (state == HCNetSDK.NET_SDK_CONFIG_STATUS_NEED_WAIT) {
                                Thread.sleep(10);
                                continue;
                            } else if (state == HCNetSDK.NET_SDK_CONFIG_STATUS_EXCEPTION) {
                                int errorCode = hcNetSDK.NET_DVR_GetLastError();
                                log.error("查询人员异常：{}，当前查询位置：{}", errorCode, currentPosition);
                                break;
                            }else if (state == HCNetSDK.NET_SDK_CONFIG_STATUS_SUCCESS) {
                                respBuffer.read();
                                // 查找实际数据长度
                                int dataLen = 0;
                                for (int i = 0; i < respBuffer.byValue.length; i++) {
                                    if (respBuffer.byValue[i] == 0) {
                                        dataLen = i;
                                        break;
                                    }
                                }
                                if (dataLen > 0) {
                                    String result = new String(respBuffer.byValue, 0, dataLen, StandardCharsets.UTF_8).trim();
                                    log.info("人员查询响应（位置：{}）：{}", currentPosition, result);
                                    // 解析响应并判断是否还有更多数据
                                    hasMoreData = parsePersonResp(result, configDTO, syncList, userID);
                                    isCurrentPageSuccess = true;
                                }
                                break;
                            } else if (state == HCNetSDK.NET_SDK_CONFIG_STATUS_FINISH ||
                                    state == HCNetSDK.NET_SDK_CONFIG_STATUS_FAILED) {
                                hasMoreData = false;
                                break;
                            }
                        }

                        // 分页位置递增（仅当前页查询成功时）
                        if (isCurrentPageSuccess) {
                            currentPosition += pageSize;
                        } else {
                            hasMoreData = false;
                        }

                    } catch (InterruptedException e) {
                        log.error("查询人员数据线程休眠异常，设备IP：{}", configDTO.getIpAddress(), e);
                        Thread.currentThread().interrupt();
                        hasMoreData = false;
                    } finally {
                        // 停止远程配置（增加句柄有效性校验）
                        if (handler > 0) {
                            if (!hcNetSDK.NET_DVR_StopRemoteConfig(handler)) {
                                log.error("停止远程配置失败，错误码：{}，设备IP：{}",
                                        hcNetSDK.NET_DVR_GetLastError(), configDTO.getIpAddress());
                            }
                        }
                    }
                }

            } catch (Exception e) {
                log.error("同步人员数据异常，设备IP：{}", configDTO.getIpAddress(), e);
            } finally {
                // 登出设备（仅登录成功时）
                if (userID != -1) {
                    hikSdkManager.logoutDevice(userID);
                }
            }
        }

        // 批量保存人员数据
        if (!syncList.isEmpty()) {
            savePersonData(syncList);
        }
    }

    /**
     * 解析人员响应JSON（改造：1.返回是否有更多数据 2.传入已登录的userID用于人脸查询）
     */
    private boolean parsePersonResp(String respStr, TAccessControlBaseInfo configDTO,
                                    List<TAccessControlPersonnelBaseInfo> syncList, int userID) {
        try {
            JSONObject respJson = JSON.parseObject(respStr);
            JSONObject userSearch = respJson.getJSONObject("UserInfoSearch");
            if (userSearch == null) {
                return false;
            }

            int total = userSearch.getIntValue("totalMatches"); // 总数据量
            int currentPosition = userSearch.getIntValue("searchResultPosition"); // 当前查询位置
            int maxResults = userSearch.getIntValue("maxResults"); // 每页数量

            JSONArray userInfos = userSearch.getJSONArray("UserInfo");
            if (userInfos == null || userInfos.isEmpty()) {
                return false;
            }

            Set<String> nameSet = new HashSet<>(); // 去重

            userInfos.forEach(infoObj -> {
                JSONObject userObj = (JSONObject) infoObj;
                String name = userObj.getString("name");
                if (name == null || !nameSet.add(name)) {
                    return; // 去重（空名称也过滤）
                }

                TAccessControlPersonnelBaseInfo personnel = new TAccessControlPersonnelBaseInfo();
                personnel.setPersonnelId(userObj.getString("employeeNo"));
                personnel.setPersonnelCode(userObj.getString("employeeNo"));
                personnel.setName(name);

                // 性别转换
                String gender = userObj.getString("gender");
                personnel.setGender("male".equals(gender) ? "男" : "female".equals(gender) ? "女" : "未知");
                personnel.setAccessControlDeviceId(configDTO.getDeviceId());
                personnel.setPersonnelGroup(userObj.getString("belongGroup"));
                personnel.setBelongStationId(configDTO.getBelongStationId());
                personnel.setPersonnelType("normal".equals(userObj.getString("userType")) ? "站内人员" : "外部访客");
                personnel.setAccessPermission("全开");
                personnel.setDeviceName(configDTO.getDeviceName());

                // 有效期解析
                JSONObject validObj = userObj.getJSONObject("Valid");
                if (validObj != null) {
                    personnel.setValidityStartTime(hikUtil.parseIsoDate(validObj.getString("beginTime")));
                    personnel.setValidityEndTime(hikUtil.parseIsoDate(validObj.getString("endTime")));
                }

                // 获取人脸图片URL（改造：传入已登录的userID，无需重新登录）
                String employeeNo = userObj.getString("employeeNo");
                if (employeeNo != null) {
                    Long faceUrl = getPersonFaceUrl(employeeNo, userID, configDTO);
                    if (faceUrl != null) {
                        personnel.setFaceData(faceUrl.toString());
                    }
                }

                syncList.add(personnel);
            });

            // 判断是否还有更多数据：当前位置 + 每页数量 < 总数据量
            return (currentPosition + maxResults) < total;
        } catch (Exception e) {
            log.error("解析人员响应异常", e);
            return false;
        }
    }

    /**
     * 获取人员人脸图片URL（改造：移除重复登录逻辑，直接使用传入的已登录userID）
     */
    private Long getPersonFaceUrl(String employeeNo, int userID, TAccessControlBaseInfo configDTO) {
        // 空值校验
        if (employeeNo == null || userID == -1) {
            log.warn("人脸查询参数异常：employeeNo={}, userID={}", employeeNo, userID);
            return null;
        }

        HCNetSDK hcNetSDK = hikSdkManager.getHCNetSDK();
        HCNetSDK.BYTE_ARRAY reqBuffer = new HCNetSDK.BYTE_ARRAY(1024);
        String reqStr = "POST /ISAPI/Intelligent/FDLib/FDSearch?format=json";

        // 修复：增加read()同步指针
        reqBuffer.read();
        System.arraycopy(reqStr.getBytes(), 0, reqBuffer.byValue, 0, reqStr.length());
        reqBuffer.write();

        int handler = hcNetSDK.NET_DVR_StartRemoteConfig(userID, HCNetSDK.NET_DVR_FACE_DATA_SEARCH,
                reqBuffer.getPointer(), reqStr.length(), null, null);
        if (handler < 0) {
            log.error("启动人脸查询失败，员工号：{}，错误码：{}", employeeNo, hcNetSDK.NET_DVR_GetLastError());
            return null;
        }

        try {
            // 构建人脸查询参数
            Map<String, Object> param = new HashMap<>();
            param.put("employeeNo", employeeNo);
            String reqBody = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/SearchFaceInfoParam.json", param);

            HCNetSDK.BYTE_ARRAY reqBodyBuffer = new HCNetSDK.BYTE_ARRAY(reqBody.length());
            // 修复：增加read()同步指针
            reqBodyBuffer.read();
            System.arraycopy(reqBody.getBytes(), 0, reqBodyBuffer.byValue, 0, reqBody.length());
            reqBodyBuffer.write();

            HCNetSDK.NET_DVR_JSON_DATA_CFG jsonDataCfg = new HCNetSDK.NET_DVR_JSON_DATA_CFG();
            jsonDataCfg.write();
            IntByReference intRef = new IntByReference(0);

            while (true) {
                // 核心修复：第一个参数改为handler（远程配置句柄）
                int state = hcNetSDK.NET_DVR_SendWithRecvRemoteConfig(handler, reqBodyBuffer.getPointer(), reqBody.length(),
                        jsonDataCfg.getPointer(), jsonDataCfg.size(), intRef);
                jsonDataCfg.read();

                if (state == -1) {
                    log.error("人脸查询失败，员工号：{}，错误码：{}", employeeNo, hcNetSDK.NET_DVR_GetLastError());
                    break;
                } else if (state == HCNetSDK.NET_SDK_CONFIG_STATUS_NEED_WAIT) {
                    Thread.sleep(10);
                    continue;
                } else if (state == HCNetSDK.NET_SDK_CONFIG_STATUS_SUCCESS) {
                    log.info("人脸查询成功，员工号：{}", employeeNo);
                    // 解析人脸响应
                    if (jsonDataCfg.dwJsonDataSize > 0) {
                        HCNetSDK.BYTE_ARRAY jsonBuffer = new HCNetSDK.BYTE_ARRAY(jsonDataCfg.dwJsonDataSize);
                        jsonBuffer.write();
                        jsonDataCfg.lpJsonData.read(0, jsonBuffer.byValue, 0, jsonDataCfg.dwJsonDataSize);
                        String respStr = new String(jsonBuffer.byValue, 0, jsonDataCfg.dwJsonDataSize, StandardCharsets.UTF_8).trim();

                        // 修复：统一使用com.alibaba.fastjson.JSONObject，避免依赖冲突
                        JSONObject respJson = JSON.parseObject(respStr);
                        int matchNum = respJson.getIntValue("numOfMatches");
                        if (matchNum <= 0) {
                            return null;
                        }
                    }

                    // 提取人脸图片并上传
                    if (jsonDataCfg.lpPicData != null && jsonDataCfg.dwPicDataSize > 0) {
                        String fileName = configDTO.getBelongStationId() + "_" + employeeNo + "_FacePic.jpg";
                        return hikUtil.uploadPicWithoutUrl(jsonDataCfg.lpPicData.getByteBuffer(0, jsonDataCfg.dwPicDataSize),
                                jsonDataCfg.dwPicDataSize, fileName);
                    }
                    break;
                } else if (state == HCNetSDK.NET_SDK_CONFIG_STATUS_FINISH || state == HCNetSDK.NET_SDK_CONFIG_STATUS_FAILED) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            log.error("人脸查询线程休眠异常，员工号：{}", employeeNo, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("人脸查询异常，员工号：{}", employeeNo, e);
        } finally {
            // 停止远程配置（增加句柄有效性校验）
            if (handler > 0) {
                hcNetSDK.NET_DVR_StopRemoteConfig(handler);
            }
        }
        return null;
    }

    /**
     * 批量保存人员数据（新增）
     */
    private void savePersonData(List<TAccessControlPersonnelBaseInfo> syncList) {
        // 第一步：对syncList根据PersonnelId + BelongStationId组合去重
        // 使用LinkedHashMap保持原列表顺序，只保留第一次出现的组合
        List<TAccessControlPersonnelBaseInfo> distinctSyncList = syncList.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                // 组合键：PersonnelId + BelongStationId（避免空值，拼接特殊分隔符）
                                p -> p.getPersonnelId() + "|" + p.getBelongStationId(),
                                p -> p,
                                // 去重策略：保留第一个出现的元素
                                (existing, replacement) -> existing,
                                LinkedHashMap::new
                        ),
                        map -> new ArrayList<>(map.values())
                ));

        // 第二步：查询已有人员，构建组合键到对象的映射（方便后续匹配和更新）
        List<TAccessControlPersonnelBaseInfo> existList = personnelService.list();
        Map<String, TAccessControlPersonnelBaseInfo> existCombinationMap = existList.stream()
                .collect(Collectors.toMap(
                        p -> p.getPersonnelId() + "|" + p.getBelongStationId(),
                        p -> p,
                        (existing, replacement) -> existing // 防止数据库中本身有重复（理论上不应出现）
                ));

        // 第三步：拆分新增和更新列表
        List<TAccessControlPersonnelBaseInfo> insertList = new ArrayList<>();
        List<TAccessControlPersonnelBaseInfo> updateList = new ArrayList<>();

        for (TAccessControlPersonnelBaseInfo syncPerson : distinctSyncList) {
            String combinationKey = syncPerson.getPersonnelId() + "|" + syncPerson.getBelongStationId();
            if (existCombinationMap.containsKey(combinationKey)) {
                // 已存在：需要更新，先获取数据库中原有对象的主键（避免覆盖主键等关键字段）
                TAccessControlPersonnelBaseInfo existPerson = existCombinationMap.get(combinationKey);
                existPerson.setName(syncPerson.getName());
                existPerson.setGender(syncPerson.getGender());
                existPerson.setPersonnelGroup(syncPerson.getPersonnelGroup());
                existPerson.setPersonnelType(syncPerson.getPersonnelType());
                existPerson.setAccessPermission(syncPerson.getAccessPermission());
                existPerson.setValidityStartTime(syncPerson.getValidityStartTime());
                existPerson.setValidityEndTime(syncPerson.getValidityEndTime());
                existPerson.setFaceData(syncPerson.getFaceData());
                existPerson.setPersonnelCode(syncPerson.getPersonnelCode());
                existPerson.setUpdateTime(new Date());
                updateList.add(existPerson);
            } else {
                // 不存在：新增
                syncPerson.setCreateTime(new Date());
                syncPerson.setUpdateTime(new Date());
                insertList.add(syncPerson);
            }
        }

        // 第四步：执行新增和更新操作
        if (ObjectUtil.isNotEmpty(insertList)) {
            personnelService.saveBatch(insertList);
            log.info("新增人员数据{}条", insertList.size());
        }
        if (ObjectUtil.isNotEmpty(updateList)) {
            personnelService.updateBatchById(updateList);
            log.info("更新人员数据{}条", updateList.size());
        }
    }
}