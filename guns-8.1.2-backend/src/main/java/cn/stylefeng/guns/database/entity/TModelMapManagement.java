package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模型地图管理
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_model_map_management", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TModelMapManagement extends BaseEntity {

    /**
     * 模型ID
     */
    @TableId(value = "model_id")
    @ChineseDescription("模型ID")
    private String modelId;

    /**
     * 模型代码
     */
    @TableField(value = "model_code")
    @ChineseDescription("模型代码")
    private String modelCode;

    /**
     * 模型名称
     */
    @TableField(value = "model_name")
    @ChineseDescription("模型名称")
    private String modelName;

    /**
     * 所属站场/阀室ID
     */
    @TableField(value = "belong_station_valve_chamber_id")
    @ChineseDescription("所属站场/阀室ID")
    private String belongStationValveChamberId;

    /**
     * 所属站场/阀室名称
     */
    @TableField(exist = false)
    private String belongStationValveChamberName;

    /**
     * 模型地址
     */
    @TableField(value = "model_address")
    @ChineseDescription("模型地址")
    private String modelAddress;

    /**
     * 模型类型
     */
    @TableField(value = "model_type")
    @ChineseDescription("模型类型")
    private String modelType;

    /**
     * 模型文件id
     */
    @TableField(value = "model_file_id")
    @ChineseDescription("模型文件id")
    private Long modelFileId;

    /**
     * 位置-存两个坐标
     * 例：{
     *   "point1": {
     *     "coordinateX": 116.403874,
     *     "coordinateY": 39.914885,
     *     "longitude": 116.403874,
     *     "latitude": 39.914885
     *   },
     *   "point2": {
     *     "coordinateX": 121.473701,
     *     "coordinateY": 31.230416,
     *     "longitude": 121.473701,
     *     "latitude": 31.230416
     *   }
     * }
     */
    @TableField(value = "position")
    @ChineseDescription("位置")
    private String position;

    @TableField(value = "system_extension")
    @ChineseDescription("系统扩展字段")
    private String systemExtension;

}