package cn.stylefeng.guns.modular.alarmconfig.request;

import cn.stylefeng.roses.kernel.rule.annotation.ChineseDescription;
import cn.stylefeng.roses.kernel.rule.pojo.request.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 报警配置请求参数
 *
 * @author system
 * @date 2026-01-14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AlarmConfigRequest extends BaseRequest {

    /**
     * 配置ID
     */
    @ChineseDescription("配置ID")
    @NotNull(message = "配置ID不能为空", groups = {detail.class, edit.class, delete.class})
    private String configId;

    /**
     * 所属站场
     */
    @ChineseDescription("所属站场")
    @NotBlank(message = "所属站场不能为空", groups = {add.class, edit.class})
    private String stationId;

    /**
     * 名称
     */
    @ChineseDescription("名称")
    @NotBlank(message = "名称不能为空", groups = {add.class, edit.class})
    private String name;

    /**
     * 子系统类型
     */
    @ChineseDescription("子系统类型")
    @NotBlank(message = "子系统类型不能为空", groups = {add.class, edit.class})
    private String subSystemType;

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
     * 通知方式（默认1报警弹窗）
     */
    @ChineseDescription("通知方式（默认1报警弹窗）")
    @NotBlank(message = "通知方式不能为空", groups = {add.class, edit.class})
    private String notificationMethod;

    /**
     * 推送方向（可多选：station站场侧、workArea作业区侧、province省公司侧）
     */
    @ChineseDescription("推送方向（可多选：station站场侧、workArea作业区侧、province省公司侧）")
    @NotBlank(message = "推送方向不能为空", groups = {add.class, edit.class})
    private String pushDirection;

    /**
     * 报警间隔（单位s）
     */
    @ChineseDescription("报警间隔（单位s）")
    @NotNull(message = "报警间隔不能为空", groups = {add.class, edit.class})
    private Integer alarmInterval;

    /**
     * 是否弹窗
     */
    @ChineseDescription("是否弹窗")
    @NotBlank(message = "是否弹窗不能为空", groups = {add.class, edit.class})
    private String isPopup;

    /**
     * 是否报警提示音
     */
    @ChineseDescription("是否报警提示音")
    @NotBlank(message = "是否报警提示音不能为空", groups = {add.class, edit.class})
    private String isAlarmSound;

    /**
     * 音频地址
     */
    @ChineseDescription("音频地址")
    private String soundAddress;

    /**
     * 提示音播放时长（单位s）
     */
    @ChineseDescription("提示音播放时长（单位s）")
    private Integer soundDuration;

    /**
     * 备注
     */
    @ChineseDescription("备注")
    private String remark;

    /**
     * 批量删除用的配置ID集合
     */
    @ChineseDescription("批量删除用的配置ID集合")
    @NotEmpty(message = "配置ID集合不能为空", groups = {batchDelete.class})
    private List<String> configIds;
}
