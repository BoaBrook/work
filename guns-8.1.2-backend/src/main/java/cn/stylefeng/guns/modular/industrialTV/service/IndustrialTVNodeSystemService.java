package cn.stylefeng.guns.modular.industrialTV.service;

import cn.hutool.core.date.DateUtil;
import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.entity.TNvrBaseInfo;
import cn.stylefeng.guns.database.entity.TPipelineBaseInfo;
import cn.stylefeng.guns.database.entity.TStationAreaBaseInfo;
import cn.stylefeng.guns.database.entity.TStationBaseInfo;
import cn.stylefeng.guns.database.service.TIndustrialTvBaseInfoService;
import cn.stylefeng.guns.database.service.TNvrBaseInfoService;
import cn.stylefeng.guns.database.service.TPipelineBaseInfoService;
import cn.stylefeng.guns.database.service.TStationAreaBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.nodeSystem.constants.dict.DeviceTypeEnum;
import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawHandleDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceAggregationDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceInventoryDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceStatusDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

/**
 * 工业电视向省级平台推送数据服务
 * 封装工业电视设备数据上报方法，简化调用时的传参
 *
 * @author system
 * @date 2026-03-18
 */
@Slf4j
@Service
public class IndustrialTVNodeSystemService {

    /**
     * 操作标识：新增
     */
    public static final String OPERATE_TYPE_ADD = "A";

    /**
     * 操作标识：修改
     */
    public static final String OPERATE_TYPE_UPDATE = "U";

    /**
     * 操作标识：删除
     */
    public static final String OPERATE_TYPE_DELETE = "D";

    /**
     * 设备状态：在线
     */
    public static final Integer DEVICE_STATUS_ONLINE = 1;

    /**
     * 设备状态：离线
     */
    public static final Integer DEVICE_STATUS_OFFLINE = 2;

    @Resource
    private NodeSystemService nodeSystemService;

    @Resource
    private TStationBaseInfoService stationBaseInfoService;

    @Resource
    private TStationAreaBaseInfoService stationAreaBaseInfoService;

    @Resource
    private TNvrBaseInfoService nvrBaseInfoService;

    @Resource
    private TPipelineBaseInfoService pipelineBaseInfoService;

    @Resource
    private TIndustrialTvBaseInfoService industrialTvBaseInfoService;

    /**
     * 上报设备清单（单个设备）
     * 用途：节点设备清单数据上报，设备新增、编辑、删除时上报。
     * 频率：初始全量发送、变更时增量发送
     *
     * @param industrialTv 工业电视设备信息
     * @param operateType  操作标识：A-新增，U-修改，D-删除
     * @return 是否发送成功
     */
    public boolean sendDeviceInventory(TIndustrialTvBaseInfo industrialTv, String operateType) {
        if (industrialTv == null) {
            log.warn("IndustrialTV is null, skip sending device inventory");
            return false;
        }
        DeviceInventoryDTO dto = buildDeviceInventoryDTO(industrialTv, operateType);
        if (dto == null) {
            return false;
        }
        return nodeSystemService.sendDeviceInventory(Collections.singletonList(dto));
    }

    /**
     * 上报设备清单（批量设备）
     * 用途：节点设备清单数据上报，设备新增、编辑、删除时上报。
     * 频率：初始全量发送、变更时增量发送
     *
     * @param industrialTvList 工业电视设备信息列表
     * @param operateType      操作标识：A-新增，U-修改，D-删除
     * @return 是否发送成功
     */
    public boolean sendDeviceInventory(List<TIndustrialTvBaseInfo> industrialTvList, String operateType) {
        if (CollectionUtils.isEmpty(industrialTvList)) {
            log.warn("IndustrialTV list is empty, skip sending device inventory");
            return false;
        }
        List<DeviceInventoryDTO> dtoList = new ArrayList<>();
        for (TIndustrialTvBaseInfo industrialTv : industrialTvList) {
            DeviceInventoryDTO dto = buildDeviceInventoryDTO(industrialTv, operateType);
            if (dto != null) {
                dtoList.add(dto);
            }
        }
        if (dtoList.isEmpty()) {
            log.warn("No valid device inventory to send");
            return false;
        }
        return nodeSystemService.sendDeviceInventory(dtoList);
    }

    /**
     * 上报设备状态
     * 用途：设备上线/离线时上报设备状态。
     * 频率：设备上线/离线时上报
     *
     * @param industrialTv 工业电视设备信息
     * @param isOnline     是否在线：true-在线，false-离线
     * @return 是否发送成功
     */
    public boolean sendDeviceStatus(TIndustrialTvBaseInfo industrialTv, boolean isOnline) {
        if (industrialTv == null) {
            log.warn("IndustrialTV is null, skip sending device status");
            return false;
        }
        DeviceStatusDTO dto = buildDeviceStatusDTO(industrialTv, isOnline);
        if (dto == null) {
            return false;
        }
        return nodeSystemService.sendDeviceStatus(dto);
    }

    /**
     * 上报设备状态（根据设备ID查询后上报）
     * 用途：设备上线/离线时上报设备状态。
     * 频率：设备上线/离线时上报
     *
     * @param deviceId 设备ID
     * @param isOnline 是否在线：true-在线，false-离线
     * @return 是否发送成功
     */
    public boolean sendDeviceStatus(String deviceId, boolean isOnline) {
        if (StringUtils.isBlank(deviceId)) {
            log.warn("Device ID is blank, skip sending device status");
            return false;
        }
        TIndustrialTvBaseInfo industrialTv = industrialTvBaseInfoService.getById(deviceId);
        if (industrialTv == null) {
            log.warn("IndustrialTV not found, deviceId: {}", deviceId);
            return false;
        }
        return sendDeviceStatus(industrialTv, isOnline);
    }

    /**
     * 上报设备汇总数据
     * 用途：上报场站设备汇总数据，按照设备类型进行汇总
     * 频率：设备数量或状态变更时上报，或省平台下发相应指令时节点平台及时上报
     *
     * @param stationId 站场ID
     * @return 是否发送成功
     */
    public boolean sendDeviceAggregation(String stationId) {
        if (StringUtils.isBlank(stationId)) {
            log.warn("Station ID is blank, skip sending device aggregation");
            return false;
        }
        DeviceAggregationDTO dto = buildDeviceAggregationDTO(stationId);
        if (dto == null) {
            return false;
        }
        return nodeSystemService.sendDeviceAggregation(Collections.singletonList(dto));
    }

    /**
     * 上报告警数据
     * 用途：节点上报告警数据
     * 频率：告警发生时及时发送
     *
     * @param alarmRaw 报警数据（需调用方自行构建）
     * @return 是否发送成功
     */
    public boolean sendAlarmRaw(AlarmRawDTO alarmRaw) {
        return nodeSystemService.sendAlarmRaw(alarmRaw);
    }

    /**
     * 上报告警处置信息
     * 用途：告警响应或告警处置完成时上报告警处置信息
     * 频率：告警处置响应或完成时上报
     *
     * @param alarmRawHandle 报警处置信息（需调用方自行构建）
     * @return 是否发送成功
     */
    public boolean sendAlarmRawHandle(AlarmRawHandleDTO alarmRawHandle) {
        return nodeSystemService.sendAlarmRawHandle(alarmRawHandle);
    }

    /**
     * 构建设备清单DTO
     */
    private DeviceInventoryDTO buildDeviceInventoryDTO(TIndustrialTvBaseInfo industrialTv, String operateType) {
        try {
            // 查询站场区域信息
            TStationAreaBaseInfo stationArea = stationAreaBaseInfoService.getById(industrialTv.getBelongStationAreaId());
            if (stationArea == null) {
                log.warn("Station area not found, areaId: {}", industrialTv.getBelongStationAreaId());
                return null;
            }

            // 查询站场信息
            TStationBaseInfo station = stationBaseInfoService.getById(stationArea.getBelongStationId());
            if (station == null) {
                log.warn("Station not found, stationId: {}", stationArea.getBelongStationId());
                return null;
            }

            // 查询管线信息
            TPipelineBaseInfo pipeline = null;
            if (StringUtils.isNotBlank(station.getBelongPipeline())) {
                pipeline = pipelineBaseInfoService.getById(station.getBelongPipeline());
            }

            // 查询NVR信息
            TNvrBaseInfo nvr = null;
            if (StringUtils.isNotBlank(industrialTv.getNvrId())) {
                nvr = nvrBaseInfoService.getById(industrialTv.getNvrId());
            }

            DeviceInventoryDTO dto = new DeviceInventoryDTO();
            dto.setNodeCode(nodeSystemService.getNodeCode());
            dto.setPipelineCode(pipeline != null ? pipeline.getPipelineCode() : null);
            dto.setWorkAreaCode(station.getBelongOperationArea());
            dto.setStationCode(station.getStationCode());
            dto.setDeviceAreaCode(stationArea.getAreaCode());
            dto.setDeviceAreaName(stationArea.getAreaName());
            dto.setDeviceType(DeviceTypeEnum.INDUSTRIAL_TELEVISION.getCode());
            dto.setDeviceCode(industrialTv.getDeviceCode());
            dto.setDeviceName(industrialTv.getDeviceName());

            // 工业电视必填字段
            dto.setNvrIp(nvr != null ? nvr.getNvrIp() : null);
            dto.setPortNumber(industrialTv.getStreamChannel());
            dto.setChannelGbId(industrialTv.getGbCode());
            dto.setIndustrialTvType(industrialTv.getCameraType());

            // 经纬度
            if (industrialTv.getLongitude() != null) {
                dto.setLongitude(industrialTv.getLongitude().intValue());
            }
            if (industrialTv.getLatitude() != null) {
                dto.setLatitude(industrialTv.getLatitude().intValue());
            }

            // 设备信息
            dto.setDeviceBrand(industrialTv.getBrand());
            dto.setDeviceModel(industrialTv.getModel());
            dto.setDeviceIp(industrialTv.getCameraIp());
            dto.setDevicePort(industrialTv.getCameraPort());
            dto.setAccount(industrialTv.getCameraUsername());

            // 密码base64编码
            if (StringUtils.isNotBlank(industrialTv.getCameraPassword())) {
                dto.setPassword(Base64.getEncoder().encodeToString(industrialTv.getCameraPassword().getBytes()));
            }

            // 入库时间
            if (industrialTv.getCreateTime() != null) {
                dto.setStorageTime(DateUtil.format(industrialTv.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            }

            dto.setOperateType(operateType);

            return dto;
        } catch (Exception e) {
            log.error("Error building DeviceInventoryDTO: ", e);
            return null;
        }
    }

    /**
     * 构建设备状态DTO
     */
    private DeviceStatusDTO buildDeviceStatusDTO(TIndustrialTvBaseInfo industrialTv, boolean isOnline) {
        try {
            // 查询站场区域信息
            TStationAreaBaseInfo stationArea = stationAreaBaseInfoService.getById(industrialTv.getBelongStationAreaId());
            if (stationArea == null) {
                log.warn("Station area not found, areaId: {}", industrialTv.getBelongStationAreaId());
                return null;
            }

            // 查询站场信息
            TStationBaseInfo station = stationBaseInfoService.getById(stationArea.getBelongStationId());
            if (station == null) {
                log.warn("Station not found, stationId: {}", stationArea.getBelongStationId());
                return null;
            }

            // 查询管线信息
            TPipelineBaseInfo pipeline = null;
            if (StringUtils.isNotBlank(station.getBelongPipeline())) {
                pipeline = pipelineBaseInfoService.getById(station.getBelongPipeline());
            }

            DeviceStatusDTO dto = new DeviceStatusDTO();
            dto.setNodeCode(nodeSystemService.getNodeCode());
            dto.setPipelineCode(pipeline != null ? pipeline.getPipelineCode() : null);
            dto.setWorkAreaCode(station.getBelongOperationArea());
            dto.setStationCode(station.getStationCode());
            dto.setDeviceCode(industrialTv.getDeviceCode());

            // 设备名称格式：管线名称-场站名称-摄像头名称
            String pipelineName = pipeline != null ? pipeline.getPipelineName() : "";
            dto.setDeviceName(pipelineName + "-" + station.getStationName() + "-" + industrialTv.getDeviceName());

            dto.setType(isOnline ? DEVICE_STATUS_ONLINE : DEVICE_STATUS_OFFLINE);
            dto.setTriggerTime(DateUtil.now());

            return dto;
        } catch (Exception e) {
            log.error("Error building DeviceStatusDTO: ", e);
            return null;
        }
    }

    /**
     * 构建设备汇总DTO
     */
    private DeviceAggregationDTO buildDeviceAggregationDTO(String stationId) {
        try {
            // 查询站场信息
            TStationBaseInfo station = stationBaseInfoService.getById(stationId);
            if (station == null) {
                log.warn("Station not found, stationId: {}", stationId);
                return null;
            }

            // 查询管线信息
            TPipelineBaseInfo pipeline = null;
            if (StringUtils.isNotBlank(station.getBelongPipeline())) {
                pipeline = pipelineBaseInfoService.getById(station.getBelongPipeline());
            }

            // 查询站场下的区域
            List<TStationAreaBaseInfo> areaList = stationAreaBaseInfoService.lambdaQuery()
                    .eq(TStationAreaBaseInfo::getBelongStationId, stationId)
                    .list();
            List<String> areaIds = new ArrayList<>();
            for (TStationAreaBaseInfo area : areaList) {
                areaIds.add(area.getAreaId());
            }

            // 统计设备数量
            int totalCount = 0;
            int onlineCount = 0;
            int offlineCount = 0;

            if (CollectionUtils.isNotEmpty(areaIds)) {
                List<TIndustrialTvBaseInfo> tvList = industrialTvBaseInfoService.lambdaQuery()
                        .in(TIndustrialTvBaseInfo::getBelongStationAreaId, areaIds)
                        .list();

                totalCount = tvList.size();
                for (TIndustrialTvBaseInfo tv : tvList) {
                    if ("1".equals(tv.getOnlineStatus())) {
                        onlineCount++;
                    } else {
                        offlineCount++;
                    }
                }
            }

            DeviceAggregationDTO dto = new DeviceAggregationDTO();
            dto.setNodeCode(nodeSystemService.getNodeCode());
            dto.setPipelineCode(pipeline != null ? pipeline.getPipelineCode() : null);
            dto.setWorkAreaCode(station.getBelongOperationArea());
            dto.setStationCode(station.getStationCode());
            dto.setDeviceType(DeviceTypeEnum.INDUSTRIAL_TELEVISION.getCode());
            dto.setDeviceSummary(totalCount);
            dto.setOnDeviceSummary(onlineCount);
            dto.setOfflineDeviceSummary(offlineCount);

            return dto;
        } catch (Exception e) {
            log.error("Error building DeviceAggregationDTO: ", e);
            return null;
        }
    }

}