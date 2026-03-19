package cn.stylefeng.guns.modular.perimeterintrusion.mock;

import cn.stylefeng.guns.modular.perimeterintrusion.remote.dto.*;
import cn.stylefeng.roses.kernel.rule.enums.ResBizTypeEnum;
import cn.stylefeng.roses.kernel.scanner.api.annotation.ApiResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.GetResource;
import cn.stylefeng.roses.kernel.scanner.api.annotation.PostResource;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@ApiResource(name = "周界系统mock", resBizType = ResBizTypeEnum.BUSINESS)
public class PerimeterIntrusionMockController {

    private static final String ZONE_LIST_JSON = "[\n" +
            "  {\n" +
            "    \"id\": \"ZONE001\",\n" +
            "    \"clazz\": \"0\",\n" +
            "    \"code\": \"FJ-A-001\",\n" +
            "    \"defenceState\": \"0\",\n" +
            "    \"description\": \"厂区东门安防防区\",\n" +
            "    \"name\": \"东门防区\",\n" +
            "    \"fullName\": \"一期厂区-东门安防防区\",\n" +
            "    \"fullOfficeName\": \"江苏省南京市江宁区XX产业园一期厂区\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"ZONE002\",\n" +
            "    \"clazz\": \"1\",\n" +
            "    \"code\": \"FJ-F-001\",\n" +
            "    \"defenceState\": \"1\",\n" +
            "    \"description\": \"仓库消防防区，配备烟感+温感探测器\",\n" +
            "    \"name\": \"仓库消防防区\",\n" +
            "    \"fullName\": \"一期厂区-仓库消防防区\",\n" +
            "    \"fullOfficeName\": \"江苏省南京市江宁区XX产业园一期厂区仓库区\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"id\": \"ZONE003\",\n" +
            "    \"clazz\": \"0\",\n" +
            "    \"code\": \"FJ-A-002\",\n" +
            "    \"defenceState\": \"0\",\n" +
            "    \"description\": \"办公楼一层安防防区\",\n" +
            "    \"name\": \"办公楼一层防区\",\n" +
            "    \"fullName\": \"二期厂区-办公楼一层安防防区\",\n" +
            "    \"fullOfficeName\": \"江苏省南京市江宁区XX产业园二期厂区办公楼\"\n" +
            "  }\n" +
            "]";

    private static final String HOST_STATE_LIST_JSON = "[\n" +
            "  {\n" +
            "    \"deviceId\": \"HOST001\",\n" +
            "    \"deviceName\": \"乌压气站三线 2#变频室南 BF\",\n" +
            "    \"state\": 0\n" +
            "  },\n" +
            "  {\n" +
            "    \"deviceId\": \"HOST002\",\n" +
            "    \"deviceName\": \"乌压气站三线 3#配电室北 BF\",\n" +
            "    \"state\": 1\n" +
            "  },\n" +
            "  {\n" +
            "    \"deviceId\": \"HOST003\",\n" +
            "    \"deviceName\": \"东输气站一线 1#监控室西 BF\",\n" +
            "    \"state\": 1\n" +
            "  },\n" +
            "  {\n" +
            "    \"deviceId\": \"HOST004\",\n" +
            "    \"deviceName\": \"西调压站二线 4#操作间东 BF\",\n" +
            "    \"state\": 1\n" +
            "  }\n" +
            "]";

    private List<PerimeterIntrusionZone> list = new ArrayList<>();

    @PostConstruct
    public void init() {
        list = JSON.parseArray(ZONE_LIST_JSON, PerimeterIntrusionZone.class);
    }

    @PostResource(name = "周界系统mock-布防撤防", path = "/api/DefenceArea/ChangeDefenceState", requiredLogin = false)
    public PerimeterIntrusionResponse<?> changeState(@RequestBody PerimeterIntrusionArmZoneRequest request) {
        log.debug("布防撤防mock,request:{}", request);
        list.forEach(item -> {
            if (request.getIds().contains(item.getId())) {
                item.setDefenceState(request.getDefenceState());
            }
        });

        PerimeterIntrusionResponse<Object> response = new PerimeterIntrusionResponse<>();
        response.setCode(200);
        response.setMsg("操作成功");
        return response;
    }

    @GetResource(name = "周界系统mock-分页查询", path = "/api/DefenceArea/GetWithStatusPageList", requiredLogin = false)
    public PerimeterIntrusionResponse<PerimeterIntrusionZone> pageList(Integer pageNumber, Integer pageSize) {
        log.debug("分页查询mock,pageNumber:{},pageSize:{}", pageNumber, pageSize);
        PerimeterIntrusionResponse.CommonData<PerimeterIntrusionZone> commonData = new PerimeterIntrusionResponse.CommonData<>();
        commonData.setRows(list);
        commonData.setTotal(list.size());

        PerimeterIntrusionResponse<PerimeterIntrusionZone> response = new PerimeterIntrusionResponse<>();
        response.setData(commonData);
        response.setCode(200);
        response.setMsg("操作成功");
        return response;
    }

    @GetResource(name = "周界系统mock-设备状态列表", path = "/api/device/getdevicestate", requiredLogin = false)
    public PerimeterIntrusionDataResponse<PerimeterIntrusionHostState> getDeviceStateList(String deviceType) {
        log.debug("设备状态列表mock,deviceType:{}", deviceType);
        List<PerimeterIntrusionHostState> list = JSON.parseArray(HOST_STATE_LIST_JSON, PerimeterIntrusionHostState.class);
        PerimeterIntrusionDataResponse<PerimeterIntrusionHostState> response = new PerimeterIntrusionDataResponse<>();
        response.setData(list);
        response.setCode(200);
        response.setMsg("操作成功");
        return response;
    }
}
