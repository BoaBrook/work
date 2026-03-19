package cn.stylefeng.guns.modular.industrialTVManagement.service;

import cn.stylefeng.guns.database.entity.TIndustrialTvBaseInfo;
import cn.stylefeng.guns.modular.industrialTVManagement.entity.IndustrialTvWithStationInfo;
import cn.stylefeng.guns.modular.industrialTVManagement.entity.AlgorithmOptionDTO;
import cn.stylefeng.guns.modular.industrialTVManagement.entity.StationAreaOptionDTO;
import cn.stylefeng.guns.modular.industrialTVManagement.entity.StationOptionDTO;
import cn.stylefeng.guns.modular.industrialTVManagement.request.IndustrialTVListRequest;
import cn.stylefeng.roses.kernel.db.api.pojo.page.PageResult;
import java.util.List;

/**
 * 工业电视设备管理Service接口
 *
 * @author system
 * @date 2026-01-27
 */
public interface IndustrialTVManagementService {

    /**
     * 获取工业电视设备列表
     *
     * @param request 查询参数
     * @return 分页结果
     */
    PageResult<IndustrialTvWithStationInfo> list(IndustrialTVListRequest request);

    /**
     * 获取工业电视设备详情
     *
     * @param deviceId 设备ID
     * @return 设备详情
     */
    IndustrialTvWithStationInfo getId(String deviceId);

    /**
     * 新增工业电视设备
     *
     * @param industrialTvBaseInfo 设备信息
     * @return 是否成功
     */
    boolean add(TIndustrialTvBaseInfo industrialTvBaseInfo);

    /**
     * 编辑工业电视设备
     *
     * @param industrialTvBaseInfo 设备信息
     * @return 是否成功
     */
    boolean update(TIndustrialTvBaseInfo industrialTvBaseInfo);

    /**
     * 删除工业电视设备
     *
     * @param deviceId 设备ID
     * @return 是否成功
     */
    boolean delete(String deviceId);

    /**
     * 获取站场下拉列表（含站场ID、站场名称、所属作业区、所属管线）
     * @return 站场选项列表
     */
    List<StationOptionDTO> listStationOptions();

    /**
     * 获取配置算法字典
     * @return 算法选项列表
     */
    List<AlgorithmOptionDTO> listAlgorithmOptions();

    /**
     * 获取站场区域下拉列表
     *
     * @param stationId 站场ID
     * @return 区域选项列表
     */
    List<StationAreaOptionDTO> listStationAreaOptions(String stationId);

    /**
     * 校验同一站场下设备编码的唯一性
     *
     * @param belongStationId 站场ID
     * @param deviceCode 设备编码
     * @param deviceId 设备ID（编辑时传入，用于排除自身；新增时传null）
     * @return true-唯一（可以使用），false-不唯一（已存在）
     */
    boolean checkDeviceCodeUnique(String belongStationId, String deviceCode, String deviceIp, String deviceId);
}
