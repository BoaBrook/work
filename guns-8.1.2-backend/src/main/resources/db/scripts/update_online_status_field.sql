-- 添加应急广播主机设备在线状态字段
ALTER TABLE t_emergency_broadcast_host_base_info 
ADD COLUMN IF NOT EXISTS online_status VARCHAR(10) DEFAULT '0' COMMENT '在线状态 (0-离线, 1-在线, 2-占用)';

ALTER TABLE t_emergency_broadcast_host_base_info 
ADD COLUMN IF NOT EXISTS online_status_desc VARCHAR(50) DEFAULT '离线' COMMENT '在线状态描述';

ALTER TABLE t_emergency_broadcast_host_base_info 
ADD COLUMN IF NOT EXISTS last_update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间';