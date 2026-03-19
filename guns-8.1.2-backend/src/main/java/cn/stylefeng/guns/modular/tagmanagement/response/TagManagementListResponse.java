package cn.stylefeng.guns.modular.tagmanagement.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TagManagementListResponse {

    private String tagId;
    private String tagName;

    private String deviceId;
    private String deviceName;

    private String modelId;
    private String modelName;
    private String modelAddress;
    private Long modelFileId;
    private String position;
    private String longitude;
    private String latitude;
    private String height;

    private String subsystemType;

    private String subsystemTypeName;

    private BigDecimal xCoordinate;
    private BigDecimal yCoordinate;
    private BigDecimal zCoordinate;

    private String belongStationId;
    private String belongStationName;

    private String belongPipeline;
    private String belongPipelineName;

    private String belongOperationArea;
    private String belongOperationAreaName;
}
