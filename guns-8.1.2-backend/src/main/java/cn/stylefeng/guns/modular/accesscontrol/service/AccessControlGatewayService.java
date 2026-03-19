package cn.stylefeng.guns.modular.accesscontrol.service;

import cn.hutool.core.util.ObjectUtil;
import cn.stylefeng.guns.database.entity.TAccessControlBaseInfo;
import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.database.service.TAccessControlBaseInfoService;
import cn.stylefeng.guns.modular.accesscontrol.hikClient.HikSdkManager;
import cn.stylefeng.guns.modular.accesscontrol.request.AccessControlGatewayRequest;
import cn.stylefeng.guns.modular.hikvision.NetSDKDemo.HCNetSDK;
import cn.stylefeng.roses.kernel.rule.exception.base.ServiceException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class AccessControlGatewayService {

    private HCNetSDK hCNetSDK;
    @Resource
    private HikSdkManager hikSdkManager;
    @Resource
    private TAccessControlBaseInfoService tAccessControlBaseInfoService;

    @PostConstruct
    public void init() {
        if (!hikSdkManager.initSdk()) {
            log.error("SDK初始化失败，终止出入记录同步");
            return;
        }
        hCNetSDK = hikSdkManager.getHCNetSDK();
    }

    public boolean remoteControlGate(AccessControlGatewayRequest request) {
        LambdaQueryWrapper<TAccessControlBaseInfo> wrapper = new LambdaQueryWrapper<>();
        if(ObjectUtil.isNotEmpty(request.getDeviceIds())){
            wrapper.in(TAccessControlBaseInfo::getDeviceId, request.getDeviceIds());
        }
        List<TAccessControlBaseInfo> list = tAccessControlBaseInfoService.list(wrapper);
        if(ObjectUtil.isEmpty(list)){
            return true;
        }
        for (TAccessControlBaseInfo device : list) {
            int userId = loginDevice(device);
            try {
                //参数lGatewayIndex[in] 门禁序号（楼层编号、锁ID），从1开始，-1表示对所有门（或者梯控的所有楼层）进行操作
                boolean b_gate = hCNetSDK.NET_DVR_ControlGateway(userId, -1, request.getCommand());
                if (!b_gate) {
                    log.error("远程控门失败,err:{}", hCNetSDK.NET_DVR_GetLastError());
                    return false;
                }
            }finally {
                logoutDevice(userId);
            }
        }
        return true;
    }


    public int loginDevice(TAccessControlBaseInfo device) {
        int userId = hikSdkManager.loginDevice(device.getIpAddress(), device.getPort().shortValue(), device.getAccessAccount(), device.getAccessPassword());
        if (userId == -1) {
            int errorCode = hCNetSDK.NET_DVR_GetLastError();
            log.error("设备[{}]登录失败，错误码：{}", device.getIpAddress(), errorCode);
            throw new ServiceException("工业电视", "500", "设备登录失败，错误码：" + errorCode);
        }
        log.info("设备[{}]登录成功，userId={}", device.getIpAddress(), userId);
        return userId;
    }

    public void logoutDevice(int userId) {
        if (userId >= 0 && hCNetSDK != null) {
            hCNetSDK.NET_DVR_Logout(userId);
            log.info("设备登出成功，userId={}", userId);
        }
    }
}
