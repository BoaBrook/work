package cn.stylefeng.guns.modular.demo.service;

import cn.hutool.crypto.asymmetric.RSA;
import org.springframework.stereotype.Service;

/**
 * 示例服务
 *
 * @author fengshuonan
 * @since 2021/1/24 10:58
 */
@Service
public class DemoService {

    /**
     * demo方法
     *
     * @author fengshuonan
     * @since 2021/1/24 10:58
     */
    public void demoService(){
        System.out.println("这是一个demo方法");
    }

    public static void main(String[] args) {
        RSA rsa = new RSA();
        rsa.getPublicKeyBase64();
        rsa.getPrivateKeyBase64();
    }

}
