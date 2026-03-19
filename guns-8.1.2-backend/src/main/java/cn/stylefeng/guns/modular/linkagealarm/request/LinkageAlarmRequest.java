package cn.stylefeng.guns.modular.linkagealarm.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 联动报警配置请求参数
 *
 * @author system
 * @date 2026-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LinkageAlarmRequest extends BaseRequest {

    /**
     * 更新状态验证组
     */
    public @interface UpdateStatus {
    }

    /**
     * 联动报警ID
     */
    @ChineseDescription("联动报警ID")
    @NotBlank(message = "联动报警ID不能为空", groups = {detail.class, edit.class, delete.class, UpdateStatus.class})
    private String linkageAlarmId;

    /**
     * 名称
     */
    @ChineseDescription("名称")
    @NotBlank(message = "名称不能为空", groups = {add.class, edit.class})
    private String linkageAlarmName;

    /**
     * 所属站场ID
     */
    @ChineseDescription("所属站场ID")
    @NotBlank(message = "所属站场ID不能为空", groups = {add.class, edit.class})
    private String belongStationId;

    /**
     * 子系统类型
     */
    @ChineseDescription("子系统类型")
    @NotBlank(message = "子系统类型不能为空", groups = {add.class, edit.class})
    private String subsystemType;

    /**
     * 报警类型
     */
    @ChineseDescription("报警类型")
    @NotBlank(message = "报警类型不能为空", groups = {add.class, edit.class})
    private String alarmType;

    /**
     * 报警等级
     */
    @ChineseDescription("报警等级")
    @NotBlank(message = "报警等级不能为空", groups = {add.class, edit.class})
    private String alarmLevel;

    /**
     * 状态（0-关闭，1-开启）
     */
    @ChineseDescription("状态（0-关闭，1-开启）")
    @NotBlank(message = "状态不能为空", groups = {edit.class, UpdateStatus.class})
    @Pattern(regexp = "^[01]$", message = "状态值必须为0（关闭）或1（开启）", groups = {UpdateStatus.class})
    private String status;

    /**
     * 是否开启录制
     */
    @ChineseDescription("是否开启录制")
    private Boolean isEnableRecord;

    /**
     * 录制时长
     */
    @ChineseDescription("录制时长")
    private Integer recordDuration;

    /**
     * 单位（秒、分、时、天）
     */
    @ChineseDescription("单位（秒、分、时、天）")
    private String durationUnit;

    /**
     * 是否开启抓图
     */
    @ChineseDescription("是否开启抓图")
    private Boolean isEnableSnapshot;

    /**
     * 抓图张数
     */
    @ChineseDescription("抓图张数")
    private Integer snapshotCount;

    /**
     * 是否打开门禁
     */
    @ChineseDescription("是否打开门禁")
    private Boolean isOpenAccessControl;

    /**
     * 是否播放音频
     */
    @ChineseDescription("是否播放音频")
    private Boolean isPlayAudio;

    /**
     * 音频文件ID
     */
    @ChineseDescription("音频文件ID")
    private String audioFileId;

    /**
     * 音频文件名称
     */
    @ChineseDescription("音频文件名称")
    private String audioFileName;

    /**
     * 批量删除用的联动报警ID集合
     */
    @ChineseDescription("批量删除用的联动报警ID集合")
    @NotEmpty(message = "联动报警ID集合不能为空", groups = {batchDelete.class})
    private List<String> linkageAlarmIds;

    private String hostId;

    private String industrialTvId;
}
