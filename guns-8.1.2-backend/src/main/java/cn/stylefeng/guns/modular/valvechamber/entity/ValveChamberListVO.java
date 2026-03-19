package cn.stylefeng.guns.modular.valvechamber.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 阀室列表 VO
 *
 * @author system
 */
@Data
public class ValveChamberListVO implements Serializable {

    private String valveChamberId;

    private String valveChamberName;

    private String belongStationAreaId;

    private String belongStationAreaName;

    private String belongStationId;

    private String belongStationName;

    private String valveChamberCode;

    private String valveChamberLocation;

    private String remark;

    private Date createTime;

    private Date updateTime;
}
