package cn.stylefeng.guns.modular.datimsien;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import cn.stylefeng.guns.database.entity.TFireGasHostBaseInfo;
import cn.stylefeng.guns.database.service.TFireGasHostBaseInfoService;
import cn.stylefeng.guns.modular.datimsien.websocketClient.DatimsienWebsocketClient;
import lombok.extern.slf4j.Slf4j;

/**
 * 实时库RT数据存储服务
 * 启动时获取t_fire_gas_host_base_info表中所有不同的acq_unit_id进行订阅
 * 
 * @author system
 */
@Slf4j
@Service
public class DatimsienRTStore implements CommandLineRunner {

    @Autowired
    private DatimsienWebsocketClient websocketClient;

    @Autowired
    private TFireGasHostBaseInfoService fireGasHostBaseInfoService;

    @Override
    public void run(String... args) throws Exception {
        init();
    }

    /**
     * 初始化订阅
     * 从t_fire_gas_host_base_info表中获取所有不同的acq_unit_id并订阅
     */
    private void init() {
        try {
            Set<String> toRegister = new HashSet<>();
            
            // 查询所有主机设备，获取不同的acq_unit_id
            List<TFireGasHostBaseInfo> hostList = fireGasHostBaseInfoService.list();
            
            // 提取所有非空的acq_unit_id
            toRegister = hostList.stream()
                    .map(TFireGasHostBaseInfo::getAcqUnitId)
                    .filter(acqUnitId -> acqUnitId != null && !acqUnitId.trim().isEmpty())
                    .collect(Collectors.toSet());
            
            if (!toRegister.isEmpty()) {
                log.info("开始订阅采集单元，数量: {}", toRegister.size());
                websocketClient.registerUnits(toRegister);
                log.info("订阅采集单元完成: {}", toRegister);
            } else {
                log.warn("未找到需要订阅的采集单元ID");
            }
        } catch (Exception e) {
            log.error("初始化订阅采集单元失败", e);
        }
    }
}
