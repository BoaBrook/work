package cn.stylefeng.guns.core.consts;

/**
 * 报警结果常量
 */
public interface AlarmResultConstants {

    /**
     * 处置状态-未处置
     */
    String DISPOSAL_STATUS_UNDISPOSED = "1";

    /**
     * 处置状态-已响应
     */
    String DISPOSAL_STATUS_RESPONDED = "2";

    /**
     * 处置状态-已处置
     */
    String DISPOSAL_STATUS_DISPOSED = "3";

    /**
     * 处理结果-为误报
     */
    String PROCESS_RESULT_FALSE_ALARM = "1";

    /**
     * 处理结果-人工处理
     */
    String PROCESS_RESULT_MANUAL = "2";

    /**
     * 处理结果-无需处理
     */
    String PROCESS_RESULT_NO_NEED = "3";

    /**
     * 处理结果-其他
     */
    String PROCESS_RESULT_OTHER = "4";

}
