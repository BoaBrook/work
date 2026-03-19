package cn.stylefeng.guns.modular.perimeterintrusion.remote.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PerimeterIntrusionArmZoneReq {

    private PerimeterIntrusionArmZoneRequest request;

    private String ipAddress;
}
