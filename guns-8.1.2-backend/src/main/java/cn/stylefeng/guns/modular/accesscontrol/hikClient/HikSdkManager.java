package cn.stylefeng.guns.modular.accesscontrol.hikClient;


import cn.stylefeng.guns.modular.hikvision.Commom.osSelect;
import cn.stylefeng.guns.modular.hikvision.NetSDKDemo.HCNetSDK;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HikSdkManager {
    private static HCNetSDK hCNetSDK;
    private static final String WIN_DLL_PATH = System.getProperty("user.dir") + "\\guns-8.1.2-backend\\lib\\win\\HCNetSDK.dll";
    private static final String LINUX_SO_PATH = "/service/app/lib/linux/libhcnetsdk.so";
    private static final String LINUX_CRYPTO_SO = "/service/app/lib/linux/libcrypto.so.1.1";
    private static final String LINUX_SSL_SO = "/service/app/lib/linux/libssl.so.1.1";
    private static final String LINUX_SDK_PATH = "/service/app/lib/linux/";

    /**
     * 初始化SDK实例（单例）
     */
    public boolean initSdk() {
        if (hCNetSDK != null) {
            return true;
        }
        synchronized (HCNetSDK.class) {
            if (hCNetSDK == null) {
                try {
                    String sdkPath = osSelect.isWindows() ? WIN_DLL_PATH : LINUX_SO_PATH;
                    hCNetSDK = (HCNetSDK) Native.loadLibrary(sdkPath, HCNetSDK.class);
                    log.info("SDK实例化成功，路径：{}", sdkPath);

                    // Linux系统额外加载依赖库
                    if (osSelect.isLinux()) {
                        loadLinuxDeps();
                    }

                    // SDK全局初始化
                    hCNetSDK.NET_DVR_Init();
                    // 开启SDK日志
                    hCNetSDK.NET_DVR_SetLogToFile(3, "./sdklog", false);
                    return true;
                } catch (Exception e) {
                    log.error("SDK初始化失败", e);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Linux系统加载SSL/加密依赖库
     */
    private void loadLinuxDeps() {
        HCNetSDK.BYTE_ARRAY cryptoPath = new HCNetSDK.BYTE_ARRAY(256);
        HCNetSDK.BYTE_ARRAY sslPath = new HCNetSDK.BYTE_ARRAY(256);
        System.arraycopy(LINUX_CRYPTO_SO.getBytes(), 0, cryptoPath.byValue, 0, LINUX_CRYPTO_SO.length());
        cryptoPath.write();
        hCNetSDK.NET_DVR_SetSDKInitCfg(HCNetSDK.NET_SDK_INIT_CFG_LIBEAY_PATH, cryptoPath.getPointer());

        System.arraycopy(LINUX_SSL_SO.getBytes(), 0, sslPath.byValue, 0, LINUX_SSL_SO.length());
        sslPath.write();
        hCNetSDK.NET_DVR_SetSDKInitCfg(HCNetSDK.NET_SDK_INIT_CFG_SSLEAY_PATH, sslPath.getPointer());

        HCNetSDK.NET_DVR_LOCAL_SDK_PATH sdkPath = new HCNetSDK.NET_DVR_LOCAL_SDK_PATH();
        System.arraycopy(LINUX_SDK_PATH.getBytes(), 0, sdkPath.sPath, 0, LINUX_SDK_PATH.length());
        sdkPath.write();
        hCNetSDK.NET_DVR_SetSDKInitCfg(HCNetSDK.NET_SDK_INIT_CFG_SDK_PATH, sdkPath.getPointer());
    }

    /**
     * 设备登录
     */
    public int loginDevice(String ip, short port, String user, String psw) {
        if (hCNetSDK == null && !initSdk()) {
            log.error("SDK未初始化，登录失败");
            return -1;
        }

        HCNetSDK.NET_DVR_USER_LOGIN_INFO loginInfo = new HCNetSDK.NET_DVR_USER_LOGIN_INFO();
        HCNetSDK.NET_DVR_DEVICEINFO_V40 deviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V40();

        // 设置设备IP
        byte[] deviceAddress = new byte[HCNetSDK.NET_DVR_DEV_ADDRESS_MAX_LEN];
        byte[] ipBytes = ip.getBytes();
        System.arraycopy(ipBytes, 0, deviceAddress, 0, Math.min(ipBytes.length, deviceAddress.length));
        loginInfo.sDeviceAddress = deviceAddress;

        // 设置用户名
        byte[] userName = new byte[HCNetSDK.NET_DVR_LOGIN_USERNAME_MAX_LEN];
        System.arraycopy(user.getBytes(), 0, userName, 0, Math.min(user.length(), userName.length));
        loginInfo.sUserName = userName;

        // 设置密码
        byte[] password = psw.getBytes();
        System.arraycopy(password, 0, loginInfo.sPassword, 0, Math.min(password.length, loginInfo.sPassword.length));

        // 配置登录参数
        loginInfo.wPort = port;
        loginInfo.bUseAsynLogin = false; // 同步登录
        loginInfo.byLoginMode = 0;       // SDK私有协议

        int userID = hCNetSDK.NET_DVR_Login_V40(loginInfo, deviceInfo);
        if (userID == -1) {
            log.error("设备[{}:{}]登录失败，错误码：{}", ip, port, hCNetSDK.NET_DVR_GetLastError());
        } else {
            log.info("设备[{}:{}]登录成功", ip, port);
        }
        return userID;
    }

    /**
     * 设备登出
     */
    public void logoutDevice(int userID) {
        if (userID >= 0 && hCNetSDK != null) {
            if (hCNetSDK.NET_DVR_Logout(userID)) {
                log.info("设备登出成功");
            } else {
                log.error("设备登出失败，错误码：{}", hCNetSDK.NET_DVR_GetLastError());
            }
        }
    }

    /**
     * 获取SDK实例
     */
    public HCNetSDK getHCNetSDK() {
        return hCNetSDK;
    }
}
