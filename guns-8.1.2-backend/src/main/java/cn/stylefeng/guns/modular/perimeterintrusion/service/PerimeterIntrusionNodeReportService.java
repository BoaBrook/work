package cn.stylefeng.guns.modular.perimeterintrusion.service;

import cn.hutool.core.date.DateUtil;
import cn.stylefeng.guns.database.entity.*;
import cn.stylefeng.guns.database.service.TPerimeterIntrusionHostBaseInfoService;
import cn.stylefeng.guns.database.service.TStationAreaBaseInfoService;
import cn.stylefeng.guns.database.service.TStationBaseInfoService;
import cn.stylefeng.guns.modular.nodeSystem.dto.AlarmRawDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceInventoryDTO;
import cn.stylefeng.guns.modular.nodeSystem.dto.DeviceStatusDTO;
import cn.stylefeng.guns.modular.nodeSystem.service.NodeSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Slf4j
@Service
public class PerimeterIntrusionNodeReportService {

    @Autowired
    private NodeSystemService nodeSystemService;
    @Autowired
    private TStationBaseInfoService stationService;
    @Autowired
    private TStationAreaBaseInfoService stationAreaService;
    @Autowired
    private TPerimeterIntrusionHostBaseInfoService hostService;

    @Async
    public void onHostInfoChange(TPerimeterIntrusionHostBaseInfo hostBaseInfo, String operateType) {
        try {
            String stationId = hostBaseInfo.getBelongStationId();
            TStationBaseInfo station = stationService.getById(stationId);
            if (station == null) {
                log.warn("周界主机信息变化上报消息取消：未找到对应的站场信息：{}", stationId);
                return;
            }
            TStationAreaBaseInfo stationArea = stationAreaService.getById(hostBaseInfo.getBelongStationAreaId());
            if (stationArea == null) {
                log.warn("周界主机信息变化上报消息取消：未找到对应的站场区域信息：{}", hostBaseInfo.getBelongStationAreaId());
                return;
            }

            DeviceInventoryDTO dto = new DeviceInventoryDTO();
            dto.setNodeCode(nodeSystemService.getNodeCode());
            dto.setPipelineCode(station.getBelongPipeline());
            dto.setWorkAreaCode(station.getBelongOperationArea());
            dto.setStationCode(station.getStationCode());
            dto.setDeviceAreaCode(hostBaseInfo.getBelongStationAreaId());
            dto.setDeviceAreaName(stationArea.getAreaName());
            dto.setDeviceType(hostBaseInfo.getDeviceType());
            dto.setDeviceCode(hostBaseInfo.getDeviceCode());
            dto.setDeviceName(String.format("%s-%s-%s", station.getBelongPipeline(), station.getStationName(), hostBaseInfo.getDeviceName()));

            // todo 经纬度
            dto.setLongitude(null);
            dto.setLatitude(null);

            dto.setDeviceBrand(hostBaseInfo.getBrand());
            dto.setDeviceModel(hostBaseInfo.getModel());
            dto.setDeviceManufacturer(hostBaseInfo.getManufacturer());
            dto.setStorageTime(DateUtil.format(hostBaseInfo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            dto.setOperateType(operateType);
            nodeSystemService.sendDeviceInventory(Collections.singletonList(dto));
        } catch (Exception e) {
            log.error("Error sending perimeter host change", e);
        }
    }

    @Async
    public void onZoneInfoChange(TPerimeterIntrusionZoneBaseInfo zone, String operateType) {
        try {
            String hostId = zone.getPerimeterIntrusionHostId();
            TPerimeterIntrusionHostBaseInfo host = hostService.getById(hostId);
            String stationId = host.getBelongStationId();
            TStationBaseInfo station = stationService.getById(stationId);
            if (station == null) {
                log.warn("周界主机信息变化上报消息取消：未找到对应的站场信息：{}", stationId);
                return;
            }
            TStationAreaBaseInfo stationArea = stationAreaService.getById(host.getBelongStationAreaId());
            if (stationArea == null) {
                log.warn("周界主机信息变化上报消息取消：未找到对应的站场区域信息：{}", host.getBelongStationAreaId());
                return;
            }

            DeviceInventoryDTO dto = new DeviceInventoryDTO();
            dto.setNodeCode(nodeSystemService.getNodeCode());
            dto.setPipelineCode(station.getBelongPipeline());
            dto.setWorkAreaCode(station.getBelongOperationArea());
            dto.setStationCode(station.getStationCode());
            dto.setDeviceAreaCode(host.getBelongStationAreaId());
            dto.setDeviceAreaName(stationArea.getAreaName());
            dto.setDeviceType(zone.getDeviceType());
            dto.setDeviceCode(zone.getZoneCode());
            dto.setDeviceName(String.format("%s-%s-%s", station.getBelongPipeline(), station.getStationName(), zone.getZoneCode()));

            // todo 经纬度
            dto.setLongitude(null);
            dto.setLatitude(null);
            // todo 品牌型号厂家
            dto.setDeviceBrand(null);
            dto.setDeviceModel(null);
            dto.setDeviceManufacturer(null);

            dto.setStorageTime(DateUtil.format(zone.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            dto.setOperateType(operateType);
            nodeSystemService.sendDeviceInventory(Collections.singletonList(dto));
        } catch (Exception e) {
            log.error("Error sending perimeter zone change", e);
        }
    }

    @Async
    public void onHostStateChange(TPerimeterIntrusionHostBaseInfo host) {
        try {
            String stationId = host.getBelongStationId();
            TStationBaseInfo station = stationService.getById(stationId);
            if (station == null) {
                log.warn("周界主机信息变化上报消息取消：未找到对应的站场信息：{}", stationId);
                return;
            }
            TStationAreaBaseInfo stationArea = stationAreaService.getById(host.getBelongStationAreaId());
            if (stationArea == null) {
                log.warn("周界主机信息变化上报消息取消：未找到对应的站场区域信息：{}", host.getBelongStationAreaId());
                return;
            }

            DeviceStatusDTO dto = new DeviceStatusDTO();
            dto.setNodeCode(nodeSystemService.getNodeCode());
            dto.setPipelineCode(station.getBelongPipeline());
            dto.setWorkAreaCode(station.getBelongOperationArea());
            dto.setStationCode(station.getStationCode());
            dto.setDeviceName(String.format("%s-%s-%s", station.getBelongPipeline(), station.getStationName(), host.getDeviceName()));
            dto.setDeviceCode(host.getDeviceCode());
            dto.setType("1".equals(host.getStatus()) ? 1 : 2);
            dto.setTriggerTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            nodeSystemService.sendDeviceStatus(dto);
        } catch (Exception e) {
            log.error("Error sending perimeter host state change", e);
        }
    }

    @Async
    public void onNewAlarm(TAlarmResultRecords alarmResultRecords, TPerimeterIntrusionZoneBaseInfo zoneBaseInfo, TPerimeterIntrusionHostBaseInfo hostBaseInfo, TStationBaseInfo station) {


        try {
            AlarmRawDTO dto = new AlarmRawDTO();
            dto.setAlarmId(alarmResultRecords.getAlarmId());
            dto.setNodeCode(nodeSystemService.getNodeCode());
            dto.setPipelineCode(station.getBelongPipeline());
            dto.setWorkAreaCode(station.getBelongOperationArea());
            dto.setStationCode(station.getStationCode());
            dto.setDeviceCode(zoneBaseInfo.getZoneCode());
            dto.setDeviceName(String.format("%s-%s-%s", station.getBelongPipeline(), station.getStationName(), zoneBaseInfo.getZoneName()));
            dto.setDeviceLocation(zoneBaseInfo.getLocationDesp());
            dto.setDeviceType(zoneBaseInfo.getDeviceType());
            dto.setAlarmLevel(Integer.parseInt(alarmResultRecords.getAlarmLevel()));
            dto.setAlarmType(alarmResultRecords.getAlarmType());
            dto.setAlarmContent(alarmResultRecords.getAlarmContent());
            dto.setAlarmTime(DateUtil.format(alarmResultRecords.getAlarmTime(), "yyyy-MM-dd HH:mm:ss"));
            // todo
            dto.setAlarmCameraPam(null);
            nodeSystemService.sendAlarmRaw(dto);
        } catch (NumberFormatException e) {
            log.error("Error sending perimeter alarm", e);
        }
    }
}
