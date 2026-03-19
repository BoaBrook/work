package cn.stylefeng.guns.modular.casClient.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.stylefeng.guns.modular.casClient.config.CasProperties;
import cn.stylefeng.roses.kernel.auth.api.SessionManagerApi;
import cn.stylefeng.roses.kernel.auth.api.TenantCodeGetApi;
import cn.stylefeng.roses.kernel.auth.api.context.AuthJwtContext;
import cn.stylefeng.roses.kernel.auth.api.expander.LoginConfigExpander;
import cn.stylefeng.roses.kernel.auth.api.pojo.auth.LoginRequest;
import cn.stylefeng.roses.kernel.auth.api.pojo.auth.LoginResponse;
import cn.stylefeng.roses.kernel.auth.api.pojo.login.LoginUser;
import cn.stylefeng.roses.kernel.auth.api.pojo.payload.DefaultJwtPayload;
import cn.stylefeng.roses.kernel.auth.auth.LoginService;
import cn.stylefeng.roses.kernel.cache.api.CacheOperatorApi;
import cn.stylefeng.roses.kernel.demo.expander.DemoConfigExpander;
import cn.stylefeng.roses.kernel.log.api.LoginLogServiceApi;
import cn.stylefeng.roses.kernel.rule.pojo.response.ResponseData;
import cn.stylefeng.roses.kernel.rule.pojo.response.SuccessResponseData;
import cn.stylefeng.roses.kernel.rule.util.HttpServletUtil;
import cn.stylefeng.roses.kernel.scanner.api.exception.ScannerException;
import cn.stylefeng.roses.kernel.scanner.api.exception.enums.ScannerExceptionEnum;
import cn.stylefeng.roses.kernel.scanner.api.holder.InitScanFlagHolder;
import cn.stylefeng.roses.kernel.sys.api.SysUserServiceApi;
import cn.stylefeng.roses.kernel.sys.api.pojo.user.UserValidateDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping({"/cas"})
public class CasCallbackController {

    @Autowired
    private CasProperties casProperties;

    @Autowired
    private SessionManagerApi sessionManagerApi;

    @Resource
    private SysUserServiceApi sysUserServiceApi;

    @Resource
    private LoginLogServiceApi loginLogServiceApi;

    @Resource(name = "loginErrorCountCacheApi")
    private CacheOperatorApi<Integer> loginErrorCountCacheApi;

    @Resource
    private TenantCodeGetApi tenantCodeGetApi;

    @GetMapping({"/callBack"})
    public ResponseData<?> casCallback(@RequestParam(value = "ticket", required = false) String ticket, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        if (StrUtil.isBlank(ticket)) {
            redirectToCas(response);
            return null;
        }
        try {
            String casUser = validateCasTicket(ticket);
            if (StrUtil.isBlank(casUser)) {
                return null;
            }
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setAccount(this.casProperties.getTestUsername());
            loginRequest.setPassword("");
            LoginResponse loginResponse = loginAction(loginRequest, casUser, null);
            String token = loginResponse.getToken();
            if (this.casProperties.getSessionTimeout() != null) {
                LoginUser loginUser = this.sessionManagerApi.getSession(token);
                if (loginUser != null) {
                    loginUser.setLoginTime(new Date());
                    this.sessionManagerApi.refreshSession(token);
                }
            }
            log.info("CAS{}", token);
            return new SuccessResponseData<>(loginResponse);
        } catch (Exception e) {
            log.error("ticket校验出错");
            return null;
        }
    }

    public LoginResponse loginAction(LoginRequest loginRequest, String casUser, String caToken) {
        if (!InitScanFlagHolder.getFlag())
            throw new ScannerException(ScannerExceptionEnum.SYSTEM_RESOURCE_URL_NOT_INIT);
        String tenantCode = loginRequest.getTenantCode();
        Long tenantId = this.tenantCodeGetApi.getTenantIdByCode(tenantCode);
        UserValidateDTO userValidateInfo = this.sysUserServiceApi.getUserLoginValidateDTO(tenantId, loginRequest.getAccount());
        DefaultJwtPayload defaultJwtPayload = new DefaultJwtPayload(userValidateInfo.getUserId(), loginRequest.getAccount(), loginRequest.getRememberMe().booleanValue(), caToken);
        String userLoginToken = AuthJwtContext.me().generateTokenDefaultPayload(defaultJwtPayload);
        LoginUser loginUser = new LoginUser(userValidateInfo.getUserId(), casUser, userLoginToken, tenantId);
        String ip = HttpServletUtil.getRequestClientIp(HttpServletUtil.getRequest());
        loginUser.setLoginIp(ip);
        loginUser.setLoginTime(new Date());
        synchronized (loginRequest.getAccount()) {
            this.sessionManagerApi.createSession(userLoginToken, loginUser);
            if (LoginConfigExpander.getSingleAccountLoginFlag())
                this.sessionManagerApi.removeSessionExcludeToken(userLoginToken);
        }
        if (!DemoConfigExpander.getDemoEnvFlag()) {
            this.sysUserServiceApi.updateUserLoginInfo(loginUser.getUserId(), ip);
            this.loginLogServiceApi.loginSuccess(loginUser.getUserId());
        }
        this.loginErrorCountCacheApi.remove(loginRequest.getAccount());
        return new LoginResponse(loginUser.getUserId(), userLoginToken);
    }

    private String validateCasTicket(String ticket) {
        try {
            String serviceParam = URLEncoder.encode(this.casProperties.getService(), "UTF-8");
            String validateUrl = this.casProperties.getValidateUrl() + "?service=" + serviceParam + "&ticket=" + ticket + "&format=json";
            log.debug("{}", validateUrl);
            HttpResponse httpResponse = HttpRequest.get(validateUrl).timeout(5000).execute();
            if (httpResponse.getStatus() != 200) {
                log.error("CAS{}", Integer.valueOf(httpResponse.getStatus()));
                return null;
            }
            String responseBody = httpResponse.body();
            log.debug("CAS{}", responseBody);
            JSONObject jsonResponse = JSON.parseObject(responseBody);
            JSONObject serviceResponse = jsonResponse.getJSONObject("serviceResponse");
            if (serviceResponse != null && serviceResponse.containsKey("authenticationSuccess")) {
                JSONObject success = serviceResponse.getJSONObject("authenticationSuccess");
                String casUser = success.getString("user");
                return casUser;
            }
            if (serviceResponse != null && serviceResponse.containsKey("authenticationFailure")) {
                log.error("CAS{}", serviceResponse.getString("authenticationFailure"));
                return null;
            }
        } catch (Exception e) {
            log.error("校验失败");
        }
        return null;
    }

    private void redirectToCas(HttpServletResponse response) throws IOException {
        String serviceParam = URLEncoder.encode(this.casProperties.getService(), "UTF-8");
        String casLoginUrl = this.casProperties.getServerUrl() + "?service=" + serviceParam;
        response.sendRedirect(casLoginUrl);
    }

    @GetMapping({"/logout"})
    public ResponseData<String> casLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String token = extractToken(request);
        if (StrUtil.isNotBlank(token))
            this.sessionManagerApi.removeSession(token);
        String casLogoutUrl = this.casProperties.getServerUrl().replace("/cas", "/cas/logout");
        // response.sendRedirect(casLogoutUrl);
        return new SuccessResponseData<>(casLogoutUrl);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StrUtil.isNotBlank(authHeader) && authHeader.startsWith("Bearer "))
            return authHeader.substring(7);
        return null;
    }
}
