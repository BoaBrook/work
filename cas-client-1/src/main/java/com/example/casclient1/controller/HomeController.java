package com.example.casclient1.controller;

import com.example.casclient1.utils.CasServiceUtil;
import com.example.casclient1.utils.JSON;
import com.example.casclient1.utils.RedisUtil;
import com.example.casclient1.utils.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 主控制器
 * 提供基本的API端点和认证相关功能
 */
@Slf4j
@Controller
public class HomeController {

    @Autowired
    private RedisUtil redisUtil;

    /** CAS服务器登录URL */
    @Value("${cas.server-login-url}")
    private String casServerLoginUrl;

    /** CAS服务器登出URL */
    @Value("${cas.server-logout-url}")
    private String casServerLogoutUrl;

    /** CAS客户端主机地址 */
    @Value("${cas.client-host-url}")
    private String casClientHostUrl;

    /** CAS客户端主机地址 */
    @Value("${cas.server-valid-url}")
    private String serverValidUrl;

    /**
     * 公开页面
     */
    @GetMapping("/public")
    public String publicPage() {
        return "public";
    }

    /**
     * 受保护页面
     */
    @GetMapping("/protected")
    public String protectedPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        Object user = redisUtil.get(auth.getName());
        if (Objects.nonNull(user)) {
            Map<String, Object> map = JSON.getJsonToBean(user, Map.class);
            List<String> list = (List<String>) map.get("customDeparts");
            model.addAttribute("customDeparts", String.join(",", list));
            model.addAttribute("customRoles", map.get("customRoles"));
        }
        return "protected";
    }

    /**
     * 首页 - 重定向到公开页面
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/protected";
    }

    /**
     * 认证状态检查端点
     * 返回当前用户的认证状态和信息
     */
    @GetMapping("/login/cas")
    public ResponseEntity<Map<String, Object>> userInfo(@RequestParam(name="ticket") String ticket,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) throws Exception {
        String service = URLEncoder.encode("http://localhost:8081/client1/login/cas");
//        String service = "http%3A%2F%2Flocalhost%3A8081%2Fclient1%2Flogin%2Fcas";
        String res = CasServiceUtil.getStValidate(serverValidUrl, ticket, service);
        log.info("res."+res);
        final String error = XmlUtils.getTextForElement(res, "authenticationFailure");
        if(!StringUtils.isEmpty(error)) {
            throw new Exception(error);
        }
        final String principal = XmlUtils.getTextForElement(res, "user");
        if (StringUtils.isEmpty(principal)) {
            throw new Exception("No principal was found in the response from the CAS server.");
        }
        log.info("-------token----username---"+principal);
        Map<String, Object> result = new HashMap<>();
        result.put("username", principal);
        return ResponseEntity.ok(result);
    }

//    /**
//     * 首页端点
//     * 返回基本信息和认证状态
//     */
//    @GetMapping("/")
//    public ResponseEntity<Map<String, Object>> homePage() {
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Welcome to CAS Client 1");
//
//        // 检查是否认证（排除匿名用户）
//        boolean isAuthenticated = auth != null && auth.isAuthenticated() &&
//                                !"anonymousUser".equals(auth.getName());
//        response.put("isAuthenticated", isAuthenticated);
//
//        // 如果已认证，添加用户名
//        if (isAuthenticated) {
//            response.put("username", auth.getName());
//        }
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 登录端点
//     * 返回CAS服务器登录URL
//     */
//    @GetMapping("/login")
//    public ResponseEntity<Map<String, Object>> login() {
//        Map<String, Object> response = new HashMap<>();
//        // 构建服务URL（CAS客户端回调地址）
//        String serviceUrl = casClientHostUrl + "/";
//        // 构建完整的CAS登录URL
//        String loginUrl = casServerLoginUrl + "?service=" + serviceUrl;
//        response.put("loginUrl", loginUrl);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * 登出端点
//     * 返回CAS服务器登出URL
//     */
//    @GetMapping("/logout")
//    public ResponseEntity<Map<String, Object>> logout() {
//        Map<String, Object> response = new HashMap<>();
//        // 构建登出后的回调URL
//        String serviceUrl = casClientHostUrl;
//        // 构建完整的CAS登出URL
//        String logoutUrl = casServerLogoutUrl + "?service=" + serviceUrl;
//        response.put("logoutUrl", logoutUrl);
//        return ResponseEntity.ok(response);
//    }
//
    /**
     * 认证状态检查端点
     * 返回当前用户的认证状态和信息
     */
    @GetMapping("/auth/status")
    public ResponseEntity<Map<String, Object>> authStatus() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 检查是否认证（排除匿名用户）
        boolean isAuthenticated = auth != null && auth.isAuthenticated() &&
                                !"anonymousUser".equals(auth.getName());

        Map<String, Object> response = new HashMap<>();
        response.put("isAuthenticated", isAuthenticated);

        // 如果已认证，添加用户信息
        if (isAuthenticated) {
            response.put("username", auth.getName());
            response.put("authorities", auth.getAuthorities());
        }
        return ResponseEntity.ok(response);
    }
} 