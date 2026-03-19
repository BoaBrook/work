package cn.stylefeng.guns.modular.perimeterintrusion.scheduler;

import cn.stylefeng.guns.database.entity.TPerimeterIntrusionHostBaseInfo;
import cn.stylefeng.guns.database.service.TPerimeterIntrusionHostBaseInfoService;
import cn.stylefeng.guns.modular.perimeterintrusion.remote.client.PerimeterIntrusionClient;
import cn.stylefeng.guns.modular.perimeterintrusion.remote.dto.PerimeterIntrusionHostState;
import cn.stylefeng.guns.modular.perimeterintrusion.service.PerimeterIntrusionNodeReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PerimeterIntrusionScheduler {
    
    @Autowired
    private PerimeterIntrusionClient perimeterIntrusionClient;
    @Autowired
    private TPerimeterIntrusionHostBaseInfoService perimeterIntrusionHostBaseInfoService;
    @Autowired
    private PerimeterIntrusionNodeReportService nodeReportService;

    @Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000 * 60)
    public void syncHostState() {
        log.debug("同步周界系统设备状态");

        List<PerimeterIntrusionHostState> hostStateList = perimeterIntrusionClient.getHostStateList();
        if (CollectionUtils.isEmpty(hostStateList)) {
            return;
        }

        Map<String, Integer> map = hostStateList.stream()
                .collect(Collectors.toMap(PerimeterIntrusionHostState::getDeviceName, PerimeterIntrusionHostState::getState, (v1, v2) -> v1));
        List<TPerimeterIntrusionHostBaseInfo> hosts = perimeterIntrusionHostBaseInfoService.list();
        List<TPerimeterIntrusionHostBaseInfo> updates = new ArrayList<>();
        for (TPerimeterIntrusionHostBaseInfo host : hosts) {
            Integer state = map.get(host.getDeviceName());
            if (state != null && !String.valueOf(state).equals(host.getStatus())) {
                host.setStatus(state.toString());
                updates.add(host);
            }
        }

        if (!CollectionUtils.isEmpty(updates)) {
            perimeterIntrusionHostBaseInfoService.updateBatchById(updates);
            updates.forEach(host -> nodeReportService.onHostStateChange(host));
        }
        log.debug("同步周界系统设备状态完成");
    }
}
