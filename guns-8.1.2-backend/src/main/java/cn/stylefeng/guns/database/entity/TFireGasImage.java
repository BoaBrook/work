package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

/**
 * 火气系统图片表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_fire_gas_image", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TFireGasImage extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ChineseDescription("主键ID")
    private String id;

    /**
     * 文件ID
     */
    @TableField(value = "file_id")
    @ChineseDescription("文件ID")
    private String fileId;

    /**
     * 所属站场ID
     */
    @TableField(value = "belong_station_id")
    @ChineseDescription("所属站场ID")
    private String belongStationId;

    /**
     * 位置
     */
    @TableField(value = "position")
    @ChineseDescription("位置")
    private String position;

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
     * 模型地址（图片地址）
     */
    @TableField(value = "model_url")
    @ChineseDescription("模型地址（图片地址）")
    private String modelUrl;

    /**
     * 所属站场名称（不存库）
     */
    @TableField(exist = false)
    @ChineseDescription("所属站场名称")
    private String belongStationName;

    /**
     * 图片文件（不存库，上传时使用）
     */
    @TableField(exist = false)
    @ChineseDescription("图片文件")
    private MultipartFile file;

}
