package cn.stylefeng.guns.database.entity;

import cn.stylefeng.roses.kernel.db.api.pojo.entity.BaseEntity;
import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 语音播报素材基础信息表
 *
 * @author system
 * @date 2026-01-14
 */
@TableName(value = "t_voice_broadcast_material_base_info", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class TVoiceBroadcastMaterialBaseInfo extends BaseEntity {

    /**
     * 语音ID
     */
    @TableId(value = "voice_id")
    @ChineseDescription("语音ID")
    private String voiceId;

    /**
     * 单位
     */
    @TableField(value = "company")
    @ChineseDescription("单位")
    private String company;

    /**
     * 所属站场
     */
    @TableField(value = "belong_station_id")
    @ChineseDescription("所属站场")
    private String belongStationId;

    /**
     * 语音名称
     */
    @TableField(value = "voice_name")
    @ChineseDescription("语音名称")
    private String voiceName;

    /**
     * 音频类型（0：普通音频，1：告警音频）
     */
    @TableField(value = "audio_type")
    @ChineseDescription("音频类型")
    private String audioType;

    /**
     * 启用状态（0：禁用，1：启用）
     */
    @TableField(value = "enable_status")
    @ChineseDescription("启用状态")
    private String enableStatus;

    /**
     * 音频文件路径
     */
    @TableField(value = "audio_file_path")
    @ChineseDescription("音频文件路径")
    private String audioFilePath;

    /**
     * 播放内容
     */
    @TableField(value = "broadcast_content")
    @ChineseDescription("播放内容")
    private String broadcastContent;

    /**
     * 备注
     */
    @TableField(value = "remark")
    @ChineseDescription("备注")
    private String remark;

    /**
     * 语音文件id
     */
    @TableField(value = "audio_file_id")
    @ChineseDescription("语音文件id")
    private Long audioFileId;

}