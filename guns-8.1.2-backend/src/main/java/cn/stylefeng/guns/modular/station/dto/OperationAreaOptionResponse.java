package cn.stylefeng.guns.modular.station.dto;

import lombok.Data;

/**
 * 作业区下拉选项
 */
@Data
public class OperationAreaOptionResponse {

    /**
     * 作业区ID（org_id）
     */
    private String operationAreaId;

    /**
     * 作业区名称（org_name）
     */
    private String operationAreaName;
}
