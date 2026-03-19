-- 1. 站场基础信息表
CREATE TABLE t_station_base_info (
                                     STATION_ID VARCHAR PRIMARY KEY,
                                     STATION_NAME VARCHAR NOT NULL UNIQUE,
                                     BELONG_OPERATION_AREA VARCHAR,
                                     BELONG_PIPELINE VARCHAR,
                                     BELONG_POINT VARCHAR,
                                     STATION_CODE VARCHAR NOT NULL UNIQUE,
                                     STATION_LOCATION TEXT,
                                     REMARK TEXT,
                                     CREATE_USER Bigint NOT NULL,
                                     CREATE_TIME TIMESTAMP NOT NULL,
                                     UPDATE_USER Bigint,
                                     UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_station_base_info IS '站场基础信息表';
COMMENT ON COLUMN t_station_base_info.STATION_ID IS '站场ID';
COMMENT ON COLUMN t_station_base_info.STATION_NAME IS '站场名称';
COMMENT ON COLUMN t_station_base_info.BELONG_OPERATION_AREA IS '所属作业区';
COMMENT ON COLUMN t_station_base_info.BELONG_PIPELINE IS '所属管线';
COMMENT ON COLUMN t_station_base_info.BELONG_POINT IS '所属节点';
COMMENT ON COLUMN t_station_base_info.STATION_CODE IS '场站编码';
COMMENT ON COLUMN t_station_base_info.STATION_LOCATION IS '站场位置';
COMMENT ON COLUMN t_station_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_station_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_station_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_station_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_station_base_info.UPDATE_TIME IS '更新时间';

-- 2. 站场区域基础信息表
CREATE TABLE t_station_area_base_info (
                                          AREA_ID VARCHAR PRIMARY KEY,
                                          AREA_NAME VARCHAR NOT NULL,
                                          BELONG_STATION_ID VARCHAR NOT NULL,
                                          AREA_TYPE VARCHAR NOT NULL,
                                          AREA_LOCATION TEXT,
                                          REMARK TEXT,
                                          CREATE_USER Bigint NOT NULL,
                                          CREATE_TIME TIMESTAMP NOT NULL,
                                          UPDATE_USER Bigint,
                                          UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_station_area_base_info IS '站场区域基础信息表';
COMMENT ON COLUMN t_station_area_base_info.AREA_ID IS '区域ID';
COMMENT ON COLUMN t_station_area_base_info.AREA_NAME IS '区域名称';
COMMENT ON COLUMN t_station_area_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_station_area_base_info.AREA_TYPE IS '区域类型';
COMMENT ON COLUMN t_station_area_base_info.AREA_LOCATION IS '位置';
COMMENT ON COLUMN t_station_area_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_station_area_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_station_area_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_station_area_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_station_area_base_info.UPDATE_TIME IS '更新时间';

-- 3. 阀室基础信息表
CREATE TABLE t_valve_chamber_base_info (
                                           VALVE_CHAMBER_ID VARCHAR PRIMARY KEY,
                                           VALVE_CHAMBER_NAME VARCHAR NOT NULL,
                                           BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                           VALVE_CHAMBER_CODE VARCHAR NOT NULL UNIQUE,
                                           LONGITUDE DECIMAL,
                                           LATITUDE DECIMAL,
                                           REMARK TEXT,
                                           CREATE_USER Bigint NOT NULL,
                                           CREATE_TIME TIMESTAMP NOT NULL,
                                           UPDATE_USER Bigint,
                                           UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_valve_chamber_base_info IS '阀室基础信息表';
COMMENT ON COLUMN t_valve_chamber_base_info.VALVE_CHAMBER_ID IS '阀室ID';
COMMENT ON COLUMN t_valve_chamber_base_info.VALVE_CHAMBER_NAME IS '阀室名称';
COMMENT ON COLUMN t_valve_chamber_base_info.BELONG_STATION_AREA_ID IS '所属站场区域';
COMMENT ON COLUMN t_valve_chamber_base_info.VALVE_CHAMBER_CODE IS '阀室编码';
COMMENT ON COLUMN t_valve_chamber_base_info.LONGITUDE IS '经度';
COMMENT ON COLUMN t_valve_chamber_base_info.LATITUDE IS '纬度';
COMMENT ON COLUMN t_valve_chamber_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_valve_chamber_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_valve_chamber_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_valve_chamber_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_valve_chamber_base_info.UPDATE_TIME IS '更新时间';

-- 4. 工业电视设备基础信息表
CREATE TABLE t_industrial_tv_base_info (
                                           DEVICE_ID VARCHAR PRIMARY KEY,
                                           DEVICE_NAME VARCHAR NOT NULL,
                                           DEVICE_CODE VARCHAR NOT NULL UNIQUE,
                                           BELONG_STATION_ID VARCHAR NOT NULL,
                                           BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                           NVR_ID VARCHAR NOT NULL,
                                           BRAND VARCHAR,
                                           MODEL VARCHAR,
                                           VIDEO_RETENTION_DAYS INT DEFAULT 30,
                                           CAMERA_TYPE VARCHAR NOT NULL,
                                           CAMERA_IP VARCHAR,
                                           CAMERA_PORT INT,
                                           CAMERA_USERNAME VARCHAR,
                                           CAMERA_PASSWORD VARCHAR,
                                           LONGITUDE DECIMAL,
                                           LATITUDE DECIMAL,
                                           HEIGHT DECIMAL,
                                           STREAM_ADDRESS VARCHAR,
                                           STREAM_CHANNEL VARCHAR,
                                           CONFIGURED_ALGORITHM VARCHAR,
                                           CREATE_USER Bigint NOT NULL,
                                           CREATE_TIME TIMESTAMP NOT NULL,
                                           UPDATE_USER Bigint,
                                           UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_industrial_tv_base_info IS '工业电视设备基础信息表';
COMMENT ON COLUMN t_industrial_tv_base_info.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_industrial_tv_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_industrial_tv_base_info.DEVICE_CODE IS '设备编码';
COMMENT ON COLUMN t_industrial_tv_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_industrial_tv_base_info.BELONG_STATION_AREA_ID IS '所属站场区域';
COMMENT ON COLUMN t_industrial_tv_base_info.NVR_ID IS '硬盘录像机ID';
COMMENT ON COLUMN t_industrial_tv_base_info.BRAND IS '品牌';
COMMENT ON COLUMN t_industrial_tv_base_info.MODEL IS '型号';
COMMENT ON COLUMN t_industrial_tv_base_info.VIDEO_RETENTION_DAYS IS '录像保留天数';
COMMENT ON COLUMN t_industrial_tv_base_info.CAMERA_TYPE IS '摄像头类型';
COMMENT ON COLUMN t_industrial_tv_base_info.CAMERA_IP IS '摄像头IP';
COMMENT ON COLUMN t_industrial_tv_base_info.CAMERA_PORT IS '摄像头端口';
COMMENT ON COLUMN t_industrial_tv_base_info.CAMERA_USERNAME IS '摄像头用户名';
COMMENT ON COLUMN t_industrial_tv_base_info.CAMERA_PASSWORD IS '摄像头密码';
COMMENT ON COLUMN t_industrial_tv_base_info.LONGITUDE IS '经度';
COMMENT ON COLUMN t_industrial_tv_base_info.LATITUDE IS '纬度';
COMMENT ON COLUMN t_industrial_tv_base_info.HEIGHT IS '高度';
COMMENT ON COLUMN t_industrial_tv_base_info.STREAM_ADDRESS IS '流媒体地址';
COMMENT ON COLUMN t_industrial_tv_base_info.STREAM_CHANNEL IS '流媒体通道';
COMMENT ON COLUMN t_industrial_tv_base_info.CONFIGURED_ALGORITHM IS '配置算法';
COMMENT ON COLUMN t_industrial_tv_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_industrial_tv_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_industrial_tv_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_industrial_tv_base_info.UPDATE_TIME IS '更新时间';

-- 5. 配置算法基础信息表
CREATE TABLE t_configured_algorithm_base_info (
                                                  algorithm_id VARCHAR PRIMARY KEY,
                                                  algorithm_name VARCHAR NOT NULL,
                                                  CREATE_USER Bigint,
                                                  CREATE_TIME TIMESTAMP,
                                                  UPDATE_USER Bigint,
                                                  UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_configured_algorithm_base_info IS '配置算法基础信息表';
COMMENT ON COLUMN t_configured_algorithm_base_info.algorithm_id IS '算法ID';
COMMENT ON COLUMN t_configured_algorithm_base_info.algorithm_name IS '算法名称';
COMMENT ON COLUMN t_configured_algorithm_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_configured_algorithm_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_configured_algorithm_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_configured_algorithm_base_info.UPDATE_TIME IS '更新时间';

-- 6. 硬盘录像机设备基础信息表
CREATE TABLE t_nvr_base_info (
                                 DEVICE_ID VARCHAR PRIMARY KEY,
                                 DEVICE_NAME VARCHAR NOT NULL,
                                 NVR_IP VARCHAR,
                                 NVR_PORT INT,
                                 BELONG_STATION_ID VARCHAR NOT NULL,
                                 BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                 CREATE_USER Bigint NOT NULL,
                                 CREATE_TIME TIMESTAMP NOT NULL,
                                 UPDATE_USER Bigint,
                                 UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_nvr_base_info IS '硬盘录像机设备基础信息表';
COMMENT ON COLUMN t_nvr_base_info.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_nvr_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_nvr_base_info.NVR_IP IS '硬盘录像机IP';
COMMENT ON COLUMN t_nvr_base_info.NVR_PORT IS '硬盘录像机端口';
COMMENT ON COLUMN t_nvr_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_nvr_base_info.BELONG_STATION_AREA_ID IS '所属站场区域';
COMMENT ON COLUMN t_nvr_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_nvr_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_nvr_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_nvr_base_info.UPDATE_TIME IS '更新时间';

-- 7. 工业电视预设位表
CREATE TABLE t_industrial_tv_preset (
                                        PRESET_ID VARCHAR PRIMARY KEY,
                                        INDUSTRIAL_TV_ID VARCHAR NOT NULL,
                                        PRESET_NAME VARCHAR NOT NULL,
                                        HORIZONTAL_ANGLE DECIMAL NOT NULL,
                                        VERTICAL_ANGLE DECIMAL NOT NULL,
                                        ZOOM_MULTIPLE DECIMAL NOT NULL,
                                        CREATE_USER Bigint NOT NULL,
                                        CREATE_TIME TIMESTAMP NOT NULL,
                                        UPDATE_USER Bigint,
                                        UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_industrial_tv_preset IS '工业电视预设位表';
COMMENT ON COLUMN t_industrial_tv_preset.PRESET_ID IS '预设位ID';
COMMENT ON COLUMN t_industrial_tv_preset.INDUSTRIAL_TV_ID IS '工业电视ID';
COMMENT ON COLUMN t_industrial_tv_preset.PRESET_NAME IS '点位名称';
COMMENT ON COLUMN t_industrial_tv_preset.HORIZONTAL_ANGLE IS '水平角度';
COMMENT ON COLUMN t_industrial_tv_preset.VERTICAL_ANGLE IS '垂直角度';
COMMENT ON COLUMN t_industrial_tv_preset.ZOOM_MULTIPLE IS '缩放倍数';
COMMENT ON COLUMN t_industrial_tv_preset.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_industrial_tv_preset.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_industrial_tv_preset.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_industrial_tv_preset.UPDATE_TIME IS '更新时间';

-- 8. 设备关联关系记录表
CREATE TABLE t_device_relation_records (
                                           RELATION_ID VARCHAR PRIMARY KEY,
                                           RELATED_DEVICE_ID VARCHAR NOT NULL,
                                           PRESET_ID VARCHAR NOT NULL,
                                           ACCESS_CONTROL_DEVICE_ID VARCHAR,
                                           EMERGENCY_BROADCAST_ID VARCHAR,
                                           CREATE_USER Bigint NOT NULL,
                                           CREATE_TIME TIMESTAMP NOT NULL,
                                           UPDATE_USER Bigint,
                                           UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_device_relation_records IS '设备关联关系记录表';
COMMENT ON COLUMN t_device_relation_records.RELATION_ID IS '关联ID';
COMMENT ON COLUMN t_device_relation_records.RELATED_DEVICE_ID IS '关联设备ID';
COMMENT ON COLUMN t_device_relation_records.PRESET_ID IS '预设位ID';
COMMENT ON COLUMN t_device_relation_records.ACCESS_CONTROL_DEVICE_ID IS '门禁设备ID';
COMMENT ON COLUMN t_device_relation_records.EMERGENCY_BROADCAST_ID IS '应急广播设备ID';
COMMENT ON COLUMN t_device_relation_records.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_device_relation_records.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_device_relation_records.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_device_relation_records.UPDATE_TIME IS '更新时间';

-- 9. 门禁设备基础信息表
CREATE TABLE t_access_control_base_info (
                                            DEVICE_ID VARCHAR PRIMARY KEY,
                                            DEVICE_NAME VARCHAR NOT NULL,
                                            DEVICE_CODE VARCHAR NOT NULL UNIQUE,
                                            BELONG_STATION_ID VARCHAR NOT NULL,
                                            BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                            BRAND VARCHAR,
                                            MODEL VARCHAR,
                                            IP_ADDRESS VARCHAR UNIQUE,
                                            PORT INT,
                                            REMARK TEXT,
                                            CREATE_USER Bigint NOT NULL,
                                            CREATE_TIME TIMESTAMP NOT NULL,
                                            UPDATE_USER Bigint,
                                            UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_access_control_base_info IS '门禁设备基础信息表';
COMMENT ON COLUMN t_access_control_base_info.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_access_control_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_access_control_base_info.DEVICE_CODE IS '设备编码';
COMMENT ON COLUMN t_access_control_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_access_control_base_info.BELONG_STATION_AREA_ID IS '所属站场区域';
COMMENT ON COLUMN t_access_control_base_info.BRAND IS '品牌';
COMMENT ON COLUMN t_access_control_base_info.MODEL IS '型号';
COMMENT ON COLUMN t_access_control_base_info.IP_ADDRESS IS 'IP地址';
COMMENT ON COLUMN t_access_control_base_info.PORT IS '端口';
COMMENT ON COLUMN t_access_control_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_access_control_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_access_control_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_access_control_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_access_control_base_info.UPDATE_TIME IS '更新时间';

-- 10. 门禁人员管理基础信息表
CREATE TABLE t_access_control_personnel_base_info (
                                                      ID VARCHAR PRIMARY KEY,
                                                      PERSONNEL_ID VARCHAR,
                                                      NAME VARCHAR NOT NULL,
                                                      PERSONNEL_CODE VARCHAR NOT NULL UNIQUE,
                                                      PERSONNEL_TYPE VARCHAR NOT NULL,
                                                      BELONG_STATION_ID VARCHAR NOT NULL,
                                                      VISITING_COMPANY VARCHAR,
                                                      ACCESS_PERMISSION VARCHAR NOT NULL,
                                                      MOBILE_PHONE VARCHAR,
                                                      GENDER VARCHAR,
                                                      VALIDITY_START_TIME TIMESTAMP NOT NULL,
                                                      VALIDITY_END_TIME TIMESTAMP NOT NULL,
                                                      PERSONNEL_GROUP VARCHAR NOT NULL,
                                                      ID_CARD_NUMBER VARCHAR,
                                                      DEVICE_NAME VARCHAR,
                                                      BELONG_COMPANY VARCHAR,
                                                      face_data VARCHAR,
                                                      job_number VARCHAR,
                                                      CREATE_USER Bigint NOT NULL,
                                                      CREATE_TIME TIMESTAMP NOT NULL,
                                                      UPDATE_USER Bigint,
                                                      UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_access_control_personnel_base_info IS '门禁人员管理基础信息表';
COMMENT ON COLUMN t_access_control_personnel_base_info.ID IS 'ID';
COMMENT ON COLUMN t_access_control_personnel_base_info.PERSONNEL_ID IS '人员ID';
COMMENT ON COLUMN t_access_control_personnel_base_info.NAME IS '姓名';
COMMENT ON COLUMN t_access_control_personnel_base_info.PERSONNEL_CODE IS '人员编码';
COMMENT ON COLUMN t_access_control_personnel_base_info.PERSONNEL_TYPE IS '人员类型';
COMMENT ON COLUMN t_access_control_personnel_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_access_control_personnel_base_info.VISITING_COMPANY IS '来访单位';
COMMENT ON COLUMN t_access_control_personnel_base_info.ACCESS_PERMISSION IS '门禁权限';
COMMENT ON COLUMN t_access_control_personnel_base_info.MOBILE_PHONE IS '手机';
COMMENT ON COLUMN t_access_control_personnel_base_info.GENDER IS '性别';
COMMENT ON COLUMN t_access_control_personnel_base_info.VALIDITY_START_TIME IS '有效期起点';
COMMENT ON COLUMN t_access_control_personnel_base_info.VALIDITY_END_TIME IS '有效期截止';
COMMENT ON COLUMN t_access_control_personnel_base_info.PERSONNEL_GROUP IS '人员分组';
COMMENT ON COLUMN t_access_control_personnel_base_info.ID_CARD_NUMBER IS '身份证号';
COMMENT ON COLUMN t_access_control_personnel_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_access_control_personnel_base_info.BELONG_COMPANY IS '所属单位';
COMMENT ON COLUMN t_access_control_personnel_base_info.face_data IS '人脸数据';
COMMENT ON COLUMN t_access_control_personnel_base_info.job_number IS '工号';
COMMENT ON COLUMN t_access_control_personnel_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_access_control_personnel_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_access_control_personnel_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_access_control_personnel_base_info.UPDATE_TIME IS '更新时间';

-- 11. 门禁出入记录表
CREATE TABLE t_access_control_entry_exit_records (
                                                     ENTRY_TIME TIMESTAMP NOT NULL,
                                                     ENTRY_EXIT_TYPE VARCHAR NOT NULL,
                                                     VISITOR_INFO VARCHAR,
                                                     ACCESS_CONTROL_DEVICE_ID VARCHAR NOT NULL,
                                                     PERSONNEL_ID VARCHAR NOT NULL,
                                                     ENTRY_METHOD VARCHAR NOT NULL,
                                                     IMAGE_ADDRESS VARCHAR,
                                                     IN_STATION_STATUS VARCHAR NOT NULL,
                                                     CREATE_USER Bigint,
                                                     CREATE_TIME TIMESTAMP,
                                                     UPDATE_USER Bigint,
                                                     UPDATE_TIME TIMESTAMP,
                                                     PRIMARY KEY (ENTRY_TIME, ACCESS_CONTROL_DEVICE_ID, PERSONNEL_ID)
);
COMMENT ON TABLE t_access_control_entry_exit_records IS '门禁出入记录表';
COMMENT ON COLUMN t_access_control_entry_exit_records.ENTRY_TIME IS '进站时间';
COMMENT ON COLUMN t_access_control_entry_exit_records.ENTRY_EXIT_TYPE IS '进/出';
COMMENT ON COLUMN t_access_control_entry_exit_records.VISITOR_INFO IS '访客信息';
COMMENT ON COLUMN t_access_control_entry_exit_records.ACCESS_CONTROL_DEVICE_ID IS '门禁设备ID';
COMMENT ON COLUMN t_access_control_entry_exit_records.PERSONNEL_ID IS '人员ID';
COMMENT ON COLUMN t_access_control_entry_exit_records.ENTRY_METHOD IS '进站方式';
COMMENT ON COLUMN t_access_control_entry_exit_records.IMAGE_ADDRESS IS '图片地址';
COMMENT ON COLUMN t_access_control_entry_exit_records.IN_STATION_STATUS IS '在站状态';
COMMENT ON COLUMN t_access_control_entry_exit_records.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_access_control_entry_exit_records.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_access_control_entry_exit_records.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_access_control_entry_exit_records.UPDATE_TIME IS '更新时间';

-- 12. 人员定位主机设备基础信息表
CREATE TABLE t_personnel_position_host_base_info (
                                                     DEVICE_ID VARCHAR PRIMARY KEY,
                                                     DEVICE_CODE VARCHAR NOT NULL UNIQUE,
                                                     DEVICE_NAME VARCHAR NOT NULL,
                                                     BELONG_STATION_ID VARCHAR NOT NULL,
                                                     BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                                     BRAND VARCHAR,
                                                     MODEL VARCHAR,
                                                     IP_ADDRESS VARCHAR,
                                                     PORT INT,
                                                     ACCOUNT VARCHAR,
                                                     PASSWORD VARCHAR,
                                                     REMARK TEXT,
                                                     CREATE_USER Bigint NOT NULL,
                                                     CREATE_TIME TIMESTAMP NOT NULL,
                                                     UPDATE_USER Bigint,
                                                     UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_personnel_position_host_base_info IS '人员定位主机设备基础信息表';
COMMENT ON COLUMN t_personnel_position_host_base_info.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_personnel_position_host_base_info.DEVICE_CODE IS '设备编码';
COMMENT ON COLUMN t_personnel_position_host_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_personnel_position_host_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_personnel_position_host_base_info.BELONG_STATION_AREA_ID IS '所属站场区域';
COMMENT ON COLUMN t_personnel_position_host_base_info.BRAND IS '品牌';
COMMENT ON COLUMN t_personnel_position_host_base_info.MODEL IS '型号';
COMMENT ON COLUMN t_personnel_position_host_base_info.IP_ADDRESS IS 'IP地址';
COMMENT ON COLUMN t_personnel_position_host_base_info.PORT IS '端口';
COMMENT ON COLUMN t_personnel_position_host_base_info.ACCOUNT IS '账号';
COMMENT ON COLUMN t_personnel_position_host_base_info.PASSWORD IS '密码';
COMMENT ON COLUMN t_personnel_position_host_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_personnel_position_host_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_personnel_position_host_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_personnel_position_host_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_personnel_position_host_base_info.UPDATE_TIME IS '更新时间';

-- 13. 定位卡基础信息表
CREATE TABLE t_position_card_base_info (
                                           DEVICE_ID VARCHAR PRIMARY KEY,
                                           POSITION_CARD_NUMBER VARCHAR NOT NULL UNIQUE,
                                           DEVICE_NAME VARCHAR NOT NULL,
                                           PERSONNEL_POSITION_HOST_ID VARCHAR NOT NULL,
                                           CREATE_USER Bigint NOT NULL,
                                           CREATE_TIME TIMESTAMP NOT NULL,
                                           UPDATE_USER Bigint,
                                           UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_position_card_base_info IS '定位卡基础信息表';
COMMENT ON COLUMN t_position_card_base_info.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_position_card_base_info.POSITION_CARD_NUMBER IS '定位卡号';
COMMENT ON COLUMN t_position_card_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_position_card_base_info.PERSONNEL_POSITION_HOST_ID IS '人员定位主机设备ID';
COMMENT ON COLUMN t_position_card_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_position_card_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_position_card_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_position_card_base_info.UPDATE_TIME IS '更新时间';

-- 14. 定位卡绑定记录表
CREATE TABLE t_position_card_binding_records (
                                                 POSITION_CARD_DEVICE_ID VARCHAR PRIMARY KEY,
                                                 POSITION_CARD_NUMBER VARCHAR NOT NULL,
                                                 PERSONNEL_ID VARCHAR NOT NULL,
                                                 BINDING_STATUS VARCHAR NOT NULL,
                                                 CREATE_USER Bigint NOT NULL,
                                                 CREATE_TIME TIMESTAMP NOT NULL,
                                                 UPDATE_USER Bigint,
                                                 UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_position_card_binding_records IS '定位卡绑定记录表';
COMMENT ON COLUMN t_position_card_binding_records.POSITION_CARD_DEVICE_ID IS '定位卡设备ID';
COMMENT ON COLUMN t_position_card_binding_records.POSITION_CARD_NUMBER IS '定位卡号';
COMMENT ON COLUMN t_position_card_binding_records.PERSONNEL_ID IS '人员ID';
COMMENT ON COLUMN t_position_card_binding_records.BINDING_STATUS IS '绑定状态';
COMMENT ON COLUMN t_position_card_binding_records.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_position_card_binding_records.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_position_card_binding_records.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_position_card_binding_records.UPDATE_TIME IS '更新时间';

-- 15. 火气系统主机设备基础信息表
CREATE TABLE t_fire_gas_host_base_info (
                                           DEVICE_ID VARCHAR PRIMARY KEY,
                                           DEVICE_CODE VARCHAR NOT NULL,
                                           DEVICE_NAME VARCHAR NOT NULL,
                                           BELONG_STATION_ID VARCHAR NOT NULL,
                                           BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                           BRAND VARCHAR,
                                           MODEL VARCHAR,
                                           IP_ADDRESS VARCHAR,
                                           PORT INT,
                                           ACCOUNT VARCHAR,
                                           PASSWORD VARCHAR,
                                           REMARK TEXT,
                                           ACQ_UNIT_ID VARCHAR,
                                           CREATE_USER Bigint NOT NULL,
                                           CREATE_TIME TIMESTAMP NOT NULL,
                                           UPDATE_USER Bigint,
                                           UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_fire_gas_host_base_info IS '火气系统主机设备基础信息表';
COMMENT ON COLUMN t_fire_gas_host_base_info.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_fire_gas_host_base_info.DEVICE_CODE IS '设备编码';
COMMENT ON COLUMN t_fire_gas_host_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_fire_gas_host_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_fire_gas_host_base_info.BELONG_STATION_AREA_ID IS '所属站场区域';
COMMENT ON COLUMN t_fire_gas_host_base_info.BRAND IS '品牌';
COMMENT ON COLUMN t_fire_gas_host_base_info.MODEL IS '型号';
COMMENT ON COLUMN t_fire_gas_host_base_info.IP_ADDRESS IS 'IP地址';
COMMENT ON COLUMN t_fire_gas_host_base_info.PORT IS '端口';
COMMENT ON COLUMN t_fire_gas_host_base_info.ACCOUNT IS '账号';
COMMENT ON COLUMN t_fire_gas_host_base_info.PASSWORD IS '密码';
COMMENT ON COLUMN t_fire_gas_host_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_fire_gas_host_base_info.ACQ_UNIT_ID IS '采集单元ID';
COMMENT ON COLUMN t_fire_gas_host_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_fire_gas_host_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_fire_gas_host_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_fire_gas_host_base_info.UPDATE_TIME IS '更新时间';

-- 同一站场下设备编码、IP地址唯一（避免跨站场误限制）
ALTER TABLE t_fire_gas_host_base_info
    ADD CONSTRAINT uk_fire_gas_host_station_device_code UNIQUE (BELONG_STATION_ID, DEVICE_CODE);
ALTER TABLE t_fire_gas_host_base_info
    ADD CONSTRAINT uk_fire_gas_host_station_ip UNIQUE (BELONG_STATION_ID, IP_ADDRESS);

-- 16. 火气系统传感器设备基础信息表
CREATE TABLE t_fire_gas_sensor_base_info (
                                             DEVICE_ID VARCHAR PRIMARY KEY,
                                             DEVICE_CODE VARCHAR NOT NULL UNIQUE,
                                             DEVICE_NAME VARCHAR NOT NULL,
                                             DEVICE_SERIAL_NUMBER VARCHAR NOT NULL,
                                             DEVICE_MODEL VARCHAR NOT NULL,
                                             DEVICE_TYPE VARCHAR,
                                             BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                             LOCATION VARCHAR,
                                             FIRE_GAS_HOST_ID VARCHAR NOT NULL,
                                             FIRE_GAS_IMAGE_ID VARCHAR,
                                             OFFSET_ADDRESS VARCHAR,
                                             X_AXIS DECIMAL,
                                             Y_AXIS DECIMAL,
                                             CREATE_USER Bigint NOT NULL,
                                             CREATE_TIME TIMESTAMP NOT NULL,
                                             UPDATE_USER Bigint,
                                             UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_fire_gas_sensor_base_info IS '火气系统传感器设备基础信息表';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.DEVICE_CODE IS '设备编码';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.DEVICE_SERIAL_NUMBER IS '设备序号';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.DEVICE_MODEL IS '设备型号';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.DEVICE_TYPE IS '设备种类';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.BELONG_STATION_AREA_ID IS '所属站场区域ID';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.LOCATION IS '所在位置';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.FIRE_GAS_HOST_ID IS '火气系统主机设备ID';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.FIRE_GAS_IMAGE_ID IS '火气系统图片ID';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.OFFSET_ADDRESS IS '偏移地址';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.X_AXIS IS 'X轴';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.Y_AXIS IS 'Y轴';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_fire_gas_sensor_base_info.UPDATE_TIME IS '更新时间';

-- 16.1. 火气系统图片表
CREATE TABLE t_fire_gas_image (
                                  ID VARCHAR PRIMARY KEY,
                                  FILE_ID VARCHAR NOT NULL,
                                  BELONG_STATION_ID VARCHAR NOT NULL,
                                  POSITION VARCHAR NOT NULL,
                                  MODEL_CODE VARCHAR NOT NULL,
                                  MODEL_NAME VARCHAR NOT NULL,
                                  MODEL_URL VARCHAR,
                                  CREATE_USER Bigint NOT NULL,
                                  CREATE_TIME TIMESTAMP NOT NULL,
                                  UPDATE_USER Bigint,
                                  UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_fire_gas_image IS '火气系统图片表';
COMMENT ON COLUMN t_fire_gas_image.ID IS '主键ID';
COMMENT ON COLUMN t_fire_gas_image.FILE_ID IS '文件ID';
COMMENT ON COLUMN t_fire_gas_image.BELONG_STATION_ID IS '所属站场ID';
COMMENT ON COLUMN t_fire_gas_image.POSITION IS '位置';
COMMENT ON COLUMN t_fire_gas_image.MODEL_CODE IS '模型代码';
COMMENT ON COLUMN t_fire_gas_image.MODEL_NAME IS '模型名称';
COMMENT ON COLUMN t_fire_gas_image.MODEL_URL IS '模型地址（图片地址）';
COMMENT ON COLUMN t_fire_gas_image.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_fire_gas_image.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_fire_gas_image.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_fire_gas_image.UPDATE_TIME IS '更新时间';

-- 17. 应急广播主机设备基础信息表
CREATE TABLE t_emergency_broadcast_host_base_info (
                                                      DEVICE_ID VARCHAR PRIMARY KEY,
                                                      DEVICE_CODE VARCHAR NOT NULL UNIQUE,
                                                      DEVICE_NAME VARCHAR NOT NULL,
                                                      BELONG_STATION_ID VARCHAR NOT NULL,
                                                      BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                                      BRAND VARCHAR,
                                                      MODEL VARCHAR,
                                                      IP_ADDRESS VARCHAR,
                                                      PORT INT,
                                                      USERNAME VARCHAR,
                                                      PASSWORD VARCHAR,
                                                      LONGITUDE DECIMAL,
                                                      LATITUDE DECIMAL,
                                                      REMARK TEXT,
                                                      CREATE_USER Bigint NOT NULL,
                                                      CREATE_TIME TIMESTAMP NOT NULL,
                                                      UPDATE_USER Bigint,
                                                      UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_emergency_broadcast_host_base_info IS '应急广播主机设备基础信息表';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.DEVICE_CODE IS '设备编码';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.BELONG_STATION_AREA_ID IS '所属站场区域';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.BRAND IS '品牌';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.MODEL IS '型号';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.IP_ADDRESS IS 'IP地址';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.PORT IS '端口';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.USERNAME IS '用户名';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.PASSWORD IS '密码';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.LONGITUDE IS '经度';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.LATITUDE IS '纬度';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.UPDATE_TIME IS '更新时间';

-- 18. 语音播报素材基础信息表
CREATE TABLE t_voice_broadcast_material_base_info (
                                                      VOICE_ID VARCHAR PRIMARY KEY,
                                                      COMPANY VARCHAR NOT NULL,
                                                      VOICE_NAME VARCHAR NOT NULL,
                                                      AUDIO_TYPE VARCHAR NOT NULL,
                                                      ENABLE_STATUS VARCHAR NOT NULL,
                                                      AUDIO_FILE_PATH VARCHAR NOT NULL UNIQUE,
                                                      BROADCAST_CONTENT VARCHAR NOT NULL,
                                                      REMARK TEXT,
                                                      CREATE_USER Bigint NOT NULL,
                                                      CREATE_TIME TIMESTAMP NOT NULL,
                                                      UPDATE_USER Bigint,
                                                      UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_voice_broadcast_material_base_info IS '语音播报素材基础信息表';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.VOICE_ID IS '语音ID';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.COMPANY IS '单位';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.VOICE_NAME IS '语音名称';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.AUDIO_TYPE IS '音频类型';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.ENABLE_STATUS IS '启用状态';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.AUDIO_FILE_PATH IS '音频文件路径';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.BROADCAST_CONTENT IS '播放内容';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.UPDATE_TIME IS '更新时间';

-- 19. 激光云台设备基础信息表
CREATE TABLE t_laser_pan_tilt_base_info (
                                            DEVICE_ID VARCHAR PRIMARY KEY,
                                            DEVICE_CODE VARCHAR NOT NULL UNIQUE,
                                            DEVICE_NAME VARCHAR NOT NULL,
                                            BELONG_STATION_ID VARCHAR NOT NULL,
                                            BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                            BRAND VARCHAR,
                                            MODEL VARCHAR,
                                            IP_ADDRESS VARCHAR,
                                            PORT INT,
                                            REMARK TEXT,
                                            INSPECTION_STATUS VARCHAR NOT NULL,
                                            CREATE_USER Bigint NOT NULL,
                                            CREATE_TIME TIMESTAMP NOT NULL,
                                            UPDATE_USER Bigint,
                                            UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_laser_pan_tilt_base_info IS '激光云台设备基础信息表';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.DEVICE_CODE IS '设备编码';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.BELONG_STATION_AREA_ID IS '所属站场区域';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.BRAND IS '品牌';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.MODEL IS '型号';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.IP_ADDRESS IS 'IP地址';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.PORT IS '端口';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.INSPECTION_STATUS IS '巡检状态';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_laser_pan_tilt_base_info.UPDATE_TIME IS '更新时间';

-- 20. 周界入侵主机设备基础信息表
CREATE TABLE t_perimeter_intrusion_host_base_info (
                                                      DEVICE_ID VARCHAR PRIMARY KEY,
                                                      DEVICE_CODE VARCHAR NOT NULL UNIQUE,
                                                      DEVICE_NAME VARCHAR NOT NULL,
                                                      BELONG_STATION_ID VARCHAR NOT NULL,
                                                      BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                                      STATUS VARCHAR NOT NULL DEFAULT '0'
                                                      BRAND VARCHAR,
                                                      MODEL VARCHAR,
                                                      MANUFACTURER VARCHAR,
                                                      IP_ADDRESS VARCHAR,
                                                      PORT INT,
                                                      DEVICE_TYPE VARCHAR NOT NULL DEFAULT '主机',
                                                      REMARK TEXT,
                                                      CREATE_USER Bigint NOT NULL,
                                                      CREATE_TIME TIMESTAMP NOT NULL,
                                                      UPDATE_USER Bigint,
                                                      UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_perimeter_intrusion_host_base_info IS '周界入侵主机设备基础信息表';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.DEVICE_CODE IS '设备编码';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.DEVICE_NAME IS '设备名称';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.BELONG_STATION_AREA_ID IS '所属站场区域';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.STATUS IS '状态 0-离线 1-在线';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.BRAND IS '品牌';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.MODEL IS '型号';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.MANUFACTURER IS '制造厂家';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.IP_ADDRESS IS 'IP地址';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.PORT IS '端口';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.DEVICE_TYPE IS '设备类型';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_perimeter_intrusion_host_base_info.UPDATE_TIME IS '更新时间';

-- 21. 周界入侵防区基础信息表
CREATE TABLE t_perimeter_intrusion_zone_base_info (
                                                      ZONE_ID VARCHAR PRIMARY KEY,
                                                      ZONE_CODE VARCHAR NOT NULL UNIQUE,
                                                      ZONE_NAME VARCHAR NOT NULL,
                                                      BELONG_STATION_AREA_ID VARCHAR NOT NULL,
                                                      PERIMETER_INTRUSION_HOST_ID VARCHAR NOT NULL,
                                                      ZONE_PATH VARCHAR NOT NULL,
                                                      LOCATION_DESP VARCHAR,
                                                      START_LOCATION VARCHAR,
                                                      END_LOCATION VARCHAR,
                                                      CHANNEL_ID VARCHAR,
                                                      DEVICE_TYPE VARCHAR NOT NULL DEFAULT '防区',
                                                      REMARK TEXT,
                                                      CREATE_USER Bigint NOT NULL,
                                                      CREATE_TIME TIMESTAMP NOT NULL,
                                                      UPDATE_USER Bigint,
                                                      UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_perimeter_intrusion_zone_base_info IS '周界入侵防区基础信息表';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.ZONE_ID IS '防区ID';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.ZONE_CODE IS '防区编码';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.ZONE_NAME IS '防区名称';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.BELONG_STATION_AREA_ID IS '所属站场区域';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.PERIMETER_INTRUSION_HOST_ID IS '周界入侵主机设备ID';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.ZONE_PATH IS '防区路径';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.LOCATION_DESP IS '防区位置信息描述';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.START_LOCATION IS '防区开始位置';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.END_LOCATION IS '防区结束位置';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.CHANNEL_ID IS '通道号';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.DEVICE_TYPE IS '设备类型';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.REMARK IS '备注';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.UPDATE_TIME IS '更新时间';

-- 22. 周界入侵防区状态记录表
CREATE TABLE t_perimeter_intrusion_zone_status_records (
                                                           ZONE_ID VARCHAR NOT NULL,
                                                           ARMED_STATUS VARCHAR NOT NULL,
                                                           MODIFY_USER VARCHAR NOT NULL,
                                                           MODIFY_TIME TIMESTAMP NOT NULL,
                                                           PRIMARY KEY (ZONE_ID, MODIFY_TIME)
);
COMMENT ON TABLE t_perimeter_intrusion_zone_status_records IS '周界入侵防区状态记录表';
COMMENT ON COLUMN t_perimeter_intrusion_zone_status_records.ZONE_ID IS '防区ID';
COMMENT ON COLUMN t_perimeter_intrusion_zone_status_records.ARMED_STATUS IS '布防状态';
COMMENT ON COLUMN t_perimeter_intrusion_zone_status_records.MODIFY_USER IS '修改人';
COMMENT ON COLUMN t_perimeter_intrusion_zone_status_records.MODIFY_TIME IS '修改时间';

-- 23. 报警结果记录表
CREATE TABLE t_alarm_result_records (
                                        ALARM_ID VARCHAR PRIMARY KEY,
                                        ALARM_DEVICE_ID VARCHAR NOT NULL,
                                        ALARM_LOCATION VARCHAR NOT NULL,
                                        SUBSYSTEM_TYPE VARCHAR NOT NULL,
                                        ALARM_TYPE VARCHAR NOT NULL,
                                        ALARM_LEVEL VARCHAR NOT NULL,
                                        ALARM_CONTENT VARCHAR NOT NULL,
                                        ALARM_TIME TIMESTAMP NOT NULL,
                                        RESPONSE_TIME TIMESTAMP,
                                        DISPOSAL_STATUS VARCHAR,
                                        PROCESS_RESULT VARCHAR,
                                        PROCESS_REMARK VARCHAR,
                                        PROCESS_USER VARCHAR,
                                        PROCESS_TIME TIMESTAMP,
                                        RECOVER_TIME TIMESTAMP,
                                        CREATE_USER Bigint,
                                        CREATE_TIME TIMESTAMP,
                                        UPDATE_USER Bigint,
                                        UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_alarm_result_records IS '报警结果记录表';
COMMENT ON COLUMN t_alarm_result_records.ALARM_ID IS '报警ID';
COMMENT ON COLUMN t_alarm_result_records.ALARM_DEVICE_ID IS '报警设备ID';
COMMENT ON COLUMN t_alarm_result_records.ALARM_LOCATION IS '报警位置';
COMMENT ON COLUMN t_alarm_result_records.SUBSYSTEM_TYPE IS '子系统类型';
COMMENT ON COLUMN t_alarm_result_records.ALARM_TYPE IS '告警类型';
COMMENT ON COLUMN t_alarm_result_records.ALARM_LEVEL IS '告警等级';
COMMENT ON COLUMN t_alarm_result_records.ALARM_CONTENT IS '报警内容';
COMMENT ON COLUMN t_alarm_result_records.ALARM_TIME IS '报警时间';
COMMENT ON COLUMN t_alarm_result_records.RESPONSE_TIME IS '响应时间';
COMMENT ON COLUMN t_alarm_result_records.DISPOSAL_STATUS IS '处置状态';
COMMENT ON COLUMN t_alarm_result_records.PROCESS_RESULT IS '处理结果';
COMMENT ON COLUMN t_alarm_result_records.PROCESS_REMARK IS '处理备注';
COMMENT ON COLUMN t_alarm_result_records.PROCESS_USER IS '处理人';
COMMENT ON COLUMN t_alarm_result_records.PROCESS_TIME IS '处理时间';
COMMENT ON COLUMN t_alarm_result_records.RECOVER_TIME IS '恢复时间';
COMMENT ON COLUMN t_alarm_result_records.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_alarm_result_records.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_alarm_result_records.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_alarm_result_records.UPDATE_TIME IS '更新时间';

-- 24. 视频巡检任务
CREATE TABLE t_video_inspection_tasks (
                                          VIDEO_INSPECTION_ID VARCHAR PRIMARY KEY,
                                          VIDEO_INSPECTION_NAME VARCHAR NOT NULL UNIQUE,
                                          INSPECTION_CYCLE VARCHAR NOT NULL,
                                          INITIAL_INSPECTION_TIME TIMESTAMP NOT NULL,
                                          inspection_interval INT NOT NULL,
                                          interval_unit VARCHAR NOT NULL,
                                          remark VARCHAR,
                                          industrial_tv_id VARCHAR NOT NULL,
                                          inspection_serial_number VARCHAR NOT NULL,
                                          preset_id VARCHAR NOT NULL,
                                          preset_algorithm VARCHAR NOT NULL,
                                          CREATE_USER Bigint NOT NULL,
                                          CREATE_TIME TIMESTAMP NOT NULL,
                                          UPDATE_USER Bigint,
                                          UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_video_inspection_tasks IS '视频巡检任务';
COMMENT ON COLUMN t_video_inspection_tasks.VIDEO_INSPECTION_ID IS '视频巡检ID';
COMMENT ON COLUMN t_video_inspection_tasks.VIDEO_INSPECTION_NAME IS '视频巡检名称';
COMMENT ON COLUMN t_video_inspection_tasks.INSPECTION_CYCLE IS '巡检周期';
COMMENT ON COLUMN t_video_inspection_tasks.INITIAL_INSPECTION_TIME IS '初次巡检时间';
COMMENT ON COLUMN t_video_inspection_tasks.inspection_interval IS '巡检间隔';
COMMENT ON COLUMN t_video_inspection_tasks.interval_unit IS '间隔单位';
COMMENT ON COLUMN t_video_inspection_tasks.remark IS '备注';
COMMENT ON COLUMN t_video_inspection_tasks.industrial_tv_id IS '工业电视ID';
COMMENT ON COLUMN t_video_inspection_tasks.inspection_serial_number IS '巡检序号';
COMMENT ON COLUMN t_video_inspection_tasks.preset_id IS '预设位ID';
COMMENT ON COLUMN t_video_inspection_tasks.preset_algorithm IS '点位算法';
COMMENT ON COLUMN t_video_inspection_tasks.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_video_inspection_tasks.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_video_inspection_tasks.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_video_inspection_tasks.UPDATE_TIME IS '更新时间';

-- 25. 巡检任务执行记录
CREATE TABLE t_inspection_task_execution_records (
                                                     EXECUTION_BATCH_ID VARCHAR PRIMARY KEY,
                                                     VIDEO_INSPECTION_ID VARCHAR NOT NULL,
                                                     EXECUTION_BATCH_NUMBER VARCHAR NOT NULL,
                                                     EXECUTION_START_TIME TIMESTAMP NOT NULL,
                                                     TOTAL_INSPECTION_ITEMS INT NOT NULL,
                                                     TOTAL_CONSUMPTION_TIME INT NOT NULL,
                                                     EXECUTION_STATUS VARCHAR NOT NULL,
                                                     EXECUTION_RESULT VARCHAR NOT NULL,
                                                     CREATE_USER Bigint,
                                                     CREATE_TIME TIMESTAMP,
                                                     UPDATE_USER Bigint,
                                                     UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_inspection_task_execution_records IS '巡检任务执行记录';
COMMENT ON COLUMN t_inspection_task_execution_records.EXECUTION_BATCH_ID IS '执行批次ID';
COMMENT ON COLUMN t_inspection_task_execution_records.VIDEO_INSPECTION_ID IS '视频巡检ID';
COMMENT ON COLUMN t_inspection_task_execution_records.EXECUTION_BATCH_NUMBER IS '执行批次号';
COMMENT ON COLUMN t_inspection_task_execution_records.EXECUTION_START_TIME IS '执行开始时间';
COMMENT ON COLUMN t_inspection_task_execution_records.TOTAL_INSPECTION_ITEMS IS '总巡检项数';
COMMENT ON COLUMN t_inspection_task_execution_records.TOTAL_CONSUMPTION_TIME IS '总耗时';
COMMENT ON COLUMN t_inspection_task_execution_records.EXECUTION_STATUS IS '执行状态';
COMMENT ON COLUMN t_inspection_task_execution_records.EXECUTION_RESULT IS '执行结果';
COMMENT ON COLUMN t_inspection_task_execution_records.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_inspection_task_execution_records.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_inspection_task_execution_records.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_inspection_task_execution_records.UPDATE_TIME IS '更新时间';

-- 26. 巡检任务执行记录详情
CREATE TABLE t_inspection_task_execution_detail_records (
                                                            EXECUTION_BATCH_ID VARCHAR NOT NULL,
                                                            STAY_DURATION INT NOT NULL,
                                                            COLLECTED_IMAGE_ADDRESS VARCHAR NOT NULL,
                                                            RESULT VARCHAR NOT NULL,
                                                            RECORD_TIME TIMESTAMP NOT NULL,
                                                            PRIMARY KEY (EXECUTION_BATCH_ID, RECORD_TIME)
);
COMMENT ON TABLE t_inspection_task_execution_detail_records IS '巡检任务执行记录详情';
COMMENT ON COLUMN t_inspection_task_execution_detail_records.EXECUTION_BATCH_ID IS '执行批次ID';
COMMENT ON COLUMN t_inspection_task_execution_detail_records.STAY_DURATION IS '停留时长';
COMMENT ON COLUMN t_inspection_task_execution_detail_records.COLLECTED_IMAGE_ADDRESS IS '采集影像地址';
COMMENT ON COLUMN t_inspection_task_execution_detail_records.RESULT IS '结果';
COMMENT ON COLUMN t_inspection_task_execution_detail_records.RECORD_TIME IS '记录时间';

-- 27. 模型地图管理
CREATE TABLE t_model_map_management (
                                        MODEL_ID VARCHAR PRIMARY KEY,
                                        MODEL_CODE VARCHAR NOT NULL UNIQUE,
                                        MODEL_NAME VARCHAR NOT NULL,
                                        BELONG_STATION_VALVE_CHAMBER_ID VARCHAR NOT NULL,
                                        MODEL_ADDRESS VARCHAR NOT NULL UNIQUE,
                                        MODEL_TYPE VARCHAR NOT NULL,
                                        CREATE_USER Bigint NOT NULL,
                                        CREATE_TIME TIMESTAMP NOT NULL,
                                        UPDATE_USER Bigint,
                                        UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_model_map_management IS '模型地图管理';
COMMENT ON COLUMN t_model_map_management.MODEL_ID IS '模型ID';
COMMENT ON COLUMN t_model_map_management.MODEL_CODE IS '模型代码';
COMMENT ON COLUMN t_model_map_management.MODEL_NAME IS '模型名称';
COMMENT ON COLUMN t_model_map_management.BELONG_STATION_VALVE_CHAMBER_ID IS '所属站场/阀室ID';
COMMENT ON COLUMN t_model_map_management.MODEL_ADDRESS IS '模型地址';
COMMENT ON COLUMN t_model_map_management.MODEL_TYPE IS '模型类型';
COMMENT ON COLUMN t_model_map_management.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_model_map_management.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_model_map_management.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_model_map_management.UPDATE_TIME IS '更新时间';

-- 28. 标签管理
CREATE TABLE t_tag_management (
                                  TAG_ID VARCHAR PRIMARY KEY,
                                  TAG_NAME VARCHAR NOT NULL,
                                  DEVICE_ID VARCHAR NOT NULL,
                                  MODEL_ID VARCHAR NOT NULL,
                                  X_COORDINATE DECIMAL NOT NULL,
                                  Y_COORDINATE DECIMAL NOT NULL,
                                  Z_COORDINATE DECIMAL,
                                  CREATE_USER Bigint NOT NULL,
                                  CREATE_TIME TIMESTAMP NOT NULL,
                                  UPDATE_USER Bigint,
                                  UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_tag_management IS '标签管理';
COMMENT ON COLUMN t_tag_management.TAG_ID IS '标签ID';
COMMENT ON COLUMN t_tag_management.TAG_NAME IS '标签名称';
COMMENT ON COLUMN t_tag_management.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN t_tag_management.MODEL_ID IS '模型ID';
COMMENT ON COLUMN t_tag_management.X_COORDINATE IS 'X';
COMMENT ON COLUMN t_tag_management.Y_COORDINATE IS 'Y';
COMMENT ON COLUMN t_tag_management.Z_COORDINATE IS 'Z';
COMMENT ON COLUMN t_tag_management.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_tag_management.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_tag_management.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_tag_management.UPDATE_TIME IS '更新时间';

Drop table if exists t_report_records;
CREATE TABLE t_report_records
(
    REPORT_ID                         VARCHAR PRIMARY KEY,
    CONTACT_PERSON VARCHAR,
    STATION_ID                        VARCHAR   NOT NULL,
    REPORT_CONTENT                    VARCHAR   NOT NULL,
    EXPECTED_RECTIFICATION_START_TIME TIMESTAMP NOT NULL,
    EXPECTED_RECTIFICATION_END_TIME   TIMESTAMP NOT NULL,
    STATUS                            INTEGER   NOT NULL,
    FLOW_CONTENT   TEXT,
    REPORT_TIME                       TIMESTAMP,
    CREATE_USER    Bigint NOT NULL,
    CREATE_TIME                       TIMESTAMP NOT NULL,
    UPDATE_USER    Bigint,
    UPDATE_TIME                       TIMESTAMP
);
COMMENT ON TABLE t_report_records IS '上报记录表';
COMMENT ON COLUMN t_report_records.REPORT_ID IS '报备ID';
COMMENT ON COLUMN t_report_records.CONTACT_PERSON IS '联系人';
COMMENT ON COLUMN t_report_records.STATION_ID IS '站场ID';
COMMENT ON COLUMN t_report_records.REPORT_CONTENT IS '报备内容';
COMMENT ON COLUMN t_report_records.EXPECTED_RECTIFICATION_START_TIME IS '预计整改开始时间';
COMMENT ON COLUMN t_report_records.EXPECTED_RECTIFICATION_END_TIME IS '预计整改结束时间';
COMMENT ON COLUMN t_report_records.REPORT_TIME IS '上报时间';
COMMENT ON COLUMN t_report_records.STATUS IS '状态 0-待上报、1-审核中、2-已通过、3-已驳回';
COMMENT ON COLUMN t_report_records.FLOW_CONTENT IS '审核内容';
COMMENT ON COLUMN t_report_records.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_report_records.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_report_records.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_report_records.UPDATE_TIME IS '更新时间';

-- 作业区配置表
CREATE TABLE t_workarea_config (
    id VARCHAR(64) PRIMARY KEY,
    workarea_url VARCHAR(500) NOT NULL COMMENT '作业区URL',
    create_time TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time TIMESTAMP COMMENT '更新时间'
);

COMMENT ON TABLE t_workarea_config IS '作业区配置表';
COMMENT ON COLUMN t_workarea_config.id IS '主键ID';
COMMENT ON COLUMN t_workarea_config.workarea_url IS '作业区URL';
COMMENT ON COLUMN t_workarea_config.create_time IS '创建时间';
COMMENT ON COLUMN t_workarea_config.update_time IS '更新时间';

--30.安全运行天数表
CREATE TABLE safety_operation_days_record (
                                              RECORD_ID VARCHAR(64) NOT NULL,
                                              STATION_ID VARCHAR(64) NOT NULL,
                                              SAFETY_OPERATION_START_DATE DATE NOT NULL,
                                              MODIFY_TIME TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              MODIFY_USER VARCHAR(64) NOT NULL,
                                              DEFINITION VARCHAR(255),
                                              PRIMARY KEY (RECORD_ID),
                                              FOREIGN KEY (STATION_ID) REFERENCES t_station_base_info(STATION_ID)
                                                  ON DELETE CASCADE ON UPDATE CASCADE
);
COMMENT ON TABLE safety_operation_days_record IS '安全运行天数记录表';
COMMENT ON COLUMN safety_operation_days_record.RECORD_ID IS '记录ID，唯一标识';
COMMENT ON COLUMN safety_operation_days_record.STATION_ID IS '关联站场基础信息表STATION_ID';
COMMENT ON COLUMN safety_operation_days_record.SAFETY_OPERATION_START_DATE IS '安全运行开始日期';
COMMENT ON COLUMN safety_operation_days_record.MODIFY_TIME IS '修改时间';
COMMENT ON COLUMN safety_operation_days_record.MODIFY_USER IS '修改人';
COMMENT ON COLUMN safety_operation_days_record.DEFINITION IS '定义';


--节点系统Kafka日志表
Drop table if exists t_node_system_kafka_log;
CREATE TABLE t_node_system_kafka_log
(
    ID              VARCHAR PRIMARY KEY,
    MSG_ID          VARCHAR,
    OPERATION_TYPE  VARCHAR NOT NULL,
    TOPIC           VARCHAR,
    PARTITION_ID    INT,
    OFFSET_IDX      BIGINT,
    MESSAGE_TYPE    VARCHAR,
    NODE_CODE       VARCHAR,
    STATUS          INT     NOT NULL,
    ERROR_MESSAGE   TEXT,
    MESSAGE_CONTENT TEXT,
    PROCESS_TIME    BIGINT,
    CREATE_USER     BIGINT,
    CREATE_TIME     TIMESTAMP,
    UPDATE_USER     BIGINT,
    UPDATE_TIME     TIMESTAMP
);

COMMENT ON TABLE t_node_system_kafka_log IS '节点系统Kafka日志表';
COMMENT ON COLUMN t_node_system_kafka_log.ID IS '日志ID';
COMMENT ON COLUMN t_node_system_kafka_log.MSG_ID IS '消息ID';
COMMENT ON COLUMN t_node_system_kafka_log.OPERATION_TYPE IS '操作类型：PRODUCE-生产，CONSUME-消费';
COMMENT ON COLUMN t_node_system_kafka_log.TOPIC IS '主题';
COMMENT ON COLUMN t_node_system_kafka_log.PARTITION_ID IS '分区';
COMMENT ON COLUMN t_node_system_kafka_log.OFFSET_IDX IS '偏移量';
COMMENT ON COLUMN t_node_system_kafka_log.MESSAGE_TYPE IS '消息类型';
COMMENT ON COLUMN t_node_system_kafka_log.NODE_CODE IS '节点编码';
COMMENT ON COLUMN t_node_system_kafka_log.STATUS IS '状态：1-成功，2-失败';
COMMENT ON COLUMN t_node_system_kafka_log.ERROR_MESSAGE IS '错误信息';
COMMENT ON COLUMN t_node_system_kafka_log.MESSAGE_CONTENT IS '消息内容（JSON字符串）';
COMMENT ON COLUMN t_node_system_kafka_log.PROCESS_TIME IS '处理时间（毫秒）';
COMMENT ON COLUMN t_node_system_kafka_log.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_node_system_kafka_log.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_node_system_kafka_log.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_node_system_kafka_log.UPDATE_TIME IS '更新时间';

--管线基础信息表
CREATE TABLE t_pipeline_base_info (
                                      PIPELINE_ID VARCHAR PRIMARY KEY,
                                      PIPELINE_NAME VARCHAR NOT NULL UNIQUE,
                                      PIPELINE_CODE VARCHAR NOT NULL UNIQUE,
                                      PIPELINE_COLOR VARCHAR NOT NULL,
                                      PIPELINE_LENGTH VARCHAR NOT NULL,
                                      REMARK TEXT,
                                      CREATE_USER Bigint NOT NULL,
                                      CREATE_TIME TIMESTAMPTZ NOT NULL
);
COMMENT ON TABLE t_pipeline_base_info IS ' 管线基础信息表 ';
COMMENT ON COLUMN t_pipeline_base_info.PIPELINE_ID IS ' 管线 ID';
COMMENT ON COLUMN t_pipeline_base_info.PIPELINE_NAME IS ' 管线名称 ';
COMMENT ON COLUMN t_pipeline_base_info.PIPELINE_CODE IS ' 管线编码 ';
COMMENT ON COLUMN t_pipeline_base_info.PIPELINE_COLOR IS ' 管线颜色 ';
COMMENT ON COLUMN t_pipeline_base_info.PIPELINE_LENGTH IS ' 管线长度 ';
COMMENT ON COLUMN t_pipeline_base_info.REMARK IS ' 备注 ';
COMMENT ON COLUMN t_pipeline_base_info.CREATE_USER IS ' 创建人 ';
COMMENT ON COLUMN t_pipeline_base_info.CREATE_TIME IS ' 创建时间 ';

-- 31. 报警配置表
CREATE TABLE t_alarm_config (
    CONFIG_ID VARCHAR PRIMARY KEY,
    STATION_ID VARCHAR NOT NULL,
    NAME VARCHAR NOT NULL,
    SUB_SYSTEM_TYPE VARCHAR NOT NULL,
    ALARM_TYPE VARCHAR NOT NULL,
    ALARM_LEVEL VARCHAR NOT NULL,
    NOTIFICATION_METHOD VARCHAR NOT NULL DEFAULT '1',
    PUSH_DIRECTION VARCHAR,
    ALARM_INTERVAL INT,
    IS_POPUP VARCHAR NOT NULL DEFAULT '1',
    IS_ALARM_SOUND VARCHAR NOT NULL DEFAULT '0',
    SOUND_ADDRESS VARCHAR,
    SOUND_DURATION INT,
    REMARK VARCHAR,
    CREATE_USER Bigint NOT NULL,
    CREATE_TIME TIMESTAMP NOT NULL,
    UPDATE_USER Bigint,
    UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_alarm_config IS '报警配置表';
COMMENT ON COLUMN t_alarm_config.CONFIG_ID IS '配置ID';
COMMENT ON COLUMN t_alarm_config.STATION_ID IS '所属站场';
COMMENT ON COLUMN t_alarm_config.NAME IS '名称';
COMMENT ON COLUMN t_alarm_config.SUB_SYSTEM_TYPE IS '子系统类型';
COMMENT ON COLUMN t_alarm_config.ALARM_TYPE IS '报警类型';
COMMENT ON COLUMN t_alarm_config.ALARM_LEVEL IS '报警等级';
COMMENT ON COLUMN t_alarm_config.NOTIFICATION_METHOD IS '通知方式（默认1报警弹窗）';
COMMENT ON COLUMN t_alarm_config.PUSH_DIRECTION IS '推送方向（可多选：station站场侧、workArea作业区侧、province省公司侧）';
COMMENT ON COLUMN t_alarm_config.ALARM_INTERVAL IS '报警间隔（单位s）';
COMMENT ON COLUMN t_alarm_config.IS_POPUP IS '是否弹窗';
COMMENT ON COLUMN t_alarm_config.IS_ALARM_SOUND IS '是否报警提示音';
COMMENT ON COLUMN t_alarm_config.FILE_NAME IS '文件名称';
COMMENT ON COLUMN t_alarm_config.FILE_ID IS '文件ID';
COMMENT ON COLUMN t_alarm_config.SOUND_DURATION IS '提示音播放时长（单位s）';
COMMENT ON COLUMN t_alarm_config.REMARK IS '备注';
COMMENT ON COLUMN t_alarm_config.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_alarm_config.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_alarm_config.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_alarm_config.UPDATE_TIME IS '更新时间';

-- 32. 联动报警配置表
CREATE TABLE t_linkage_alarm_config (
    LINKAGE_ALARM_ID VARCHAR PRIMARY KEY,
    LINKAGE_ALARM_NAME VARCHAR NOT NULL,
    BELONG_STATION_ID VARCHAR NOT NULL,
    SUBSYSTEM_TYPE VARCHAR NOT NULL,
    ALARM_TYPE VARCHAR NOT NULL,
    ALARM_LEVEL VARCHAR NOT NULL,
    STATUS VARCHAR NOT NULL DEFAULT '0',
    IS_ENABLE_RECORD BOOLEAN,
    RECORD_DURATION INTEGER,
    DURATION_UNIT VARCHAR,
    IS_ENABLE_SNAPSHOT BOOLEAN,
    SNAPSHOT_COUNT INTEGER,
    IS_OPEN_ACCESS_CONTROL BOOLEAN,
    IS_PLAY_AUDIO BOOLEAN,
    AUDIO_FILE_ID VARCHAR,
    AUDIO_FILE_NAME VARCHAR,
    CREATE_USER Bigint NOT NULL,
    CREATE_TIME TIMESTAMP NOT NULL,
    UPDATE_USER Bigint,
    UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_linkage_alarm_config IS '联动报警配置表';
COMMENT ON COLUMN t_linkage_alarm_config.LINKAGE_ALARM_ID IS '联动报警ID';
COMMENT ON COLUMN t_linkage_alarm_config.LINKAGE_ALARM_NAME IS '名称';
COMMENT ON COLUMN t_linkage_alarm_config.BELONG_STATION_ID IS '所属站场ID';
COMMENT ON COLUMN t_linkage_alarm_config.SUBSYSTEM_TYPE IS '子系统类型';
COMMENT ON COLUMN t_linkage_alarm_config.ALARM_TYPE IS '报警类型';
COMMENT ON COLUMN t_linkage_alarm_config.ALARM_LEVEL IS '报警等级';
COMMENT ON COLUMN t_linkage_alarm_config.STATUS IS '状态（0-关闭，1-开启）';
COMMENT ON COLUMN t_linkage_alarm_config.IS_ENABLE_RECORD IS '是否开启录制';
COMMENT ON COLUMN t_linkage_alarm_config.RECORD_DURATION IS '录制时长';
COMMENT ON COLUMN t_linkage_alarm_config.DURATION_UNIT IS '单位（秒、分、时、天）';
COMMENT ON COLUMN t_linkage_alarm_config.IS_ENABLE_SNAPSHOT IS '是否开启抓图';
COMMENT ON COLUMN t_linkage_alarm_config.SNAPSHOT_COUNT IS '抓图张数';
COMMENT ON COLUMN t_linkage_alarm_config.IS_OPEN_ACCESS_CONTROL IS '是否打开门禁';
COMMENT ON COLUMN t_linkage_alarm_config.IS_PLAY_AUDIO IS '是否播放音频';
COMMENT ON COLUMN t_linkage_alarm_config.AUDIO_FILE_ID IS '音频文件ID';
COMMENT ON COLUMN t_linkage_alarm_config.AUDIO_FILE_NAME IS '音频文件名称';
COMMENT ON COLUMN t_linkage_alarm_config.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_linkage_alarm_config.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_linkage_alarm_config.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_linkage_alarm_config.UPDATE_TIME IS '更新时间';

-- ========================================
-- 以下为Entity与SQL同步的增量更新语句
-- 生成时间: 2026-03-18
-- ========================================

-- 1. t_station_area_base_info 添加缺失字段
ALTER TABLE t_station_area_base_info ADD COLUMN IF NOT EXISTS AREA_CODE VARCHAR;
COMMENT ON COLUMN t_station_area_base_info.AREA_CODE IS '区域编码';

-- 2. t_valve_chamber_base_info 添加缺失字段
ALTER TABLE t_valve_chamber_base_info ADD COLUMN IF NOT EXISTS LOCATION VARCHAR;
COMMENT ON COLUMN t_valve_chamber_base_info.LOCATION IS '位置';

-- 3. t_industrial_tv_base_info 添加缺失字段
ALTER TABLE t_industrial_tv_base_info ADD COLUMN IF NOT EXISTS GB_CODE VARCHAR;
ALTER TABLE t_industrial_tv_base_info ADD COLUMN IF NOT EXISTS ONLINE_STATUS VARCHAR;
COMMENT ON COLUMN t_industrial_tv_base_info.GB_CODE IS '国标编号';
COMMENT ON COLUMN t_industrial_tv_base_info.ONLINE_STATUS IS '在线状态 (0-离线, 1-在线, 2-占用)';

-- 4. t_nvr_base_info 添加缺失字段
ALTER TABLE t_nvr_base_info ADD COLUMN IF NOT EXISTS DEVICE_CODE VARCHAR;
ALTER TABLE t_nvr_base_info ADD COLUMN IF NOT EXISTS RTSP_URL VARCHAR;
COMMENT ON COLUMN t_nvr_base_info.DEVICE_CODE IS '设备编码';
COMMENT ON COLUMN t_nvr_base_info.RTSP_URL IS 'rtsp地址';

-- 5. t_industrial_tv_preset 添加缺失字段
ALTER TABLE t_industrial_tv_preset ADD COLUMN IF NOT EXISTS PRESET_CODE INT;
ALTER TABLE t_industrial_tv_preset ADD COLUMN IF NOT EXISTS COORDINATE VARCHAR;
COMMENT ON COLUMN t_industrial_tv_preset.PRESET_CODE IS '预设位编号';
COMMENT ON COLUMN t_industrial_tv_preset.COORDINATE IS '坐标';

-- 6. t_device_relation_records 添加缺失字段
ALTER TABLE t_device_relation_records ADD COLUMN IF NOT EXISTS SUBSYSTEM_TYPE VARCHAR;
COMMENT ON COLUMN t_device_relation_records.SUBSYSTEM_TYPE IS '子系统类型';

-- 7. t_access_control_base_info 添加缺失字段
ALTER TABLE t_access_control_base_info ADD COLUMN IF NOT EXISTS STATE INT;
ALTER TABLE t_access_control_base_info ADD COLUMN IF NOT EXISTS STREAM_ADDRESS VARCHAR;
ALTER TABLE t_access_control_base_info ADD COLUMN IF NOT EXISTS BELONG_PIPELINE_ID VARCHAR;
ALTER TABLE t_access_control_base_info ADD COLUMN IF NOT EXISTS IS_COLLECTION_MACHINE INT;
ALTER TABLE t_access_control_base_info ADD COLUMN IF NOT EXISTS ACCESS_ACCOUNT VARCHAR;
ALTER TABLE t_access_control_base_info ADD COLUMN IF NOT EXISTS ACCESS_PASSWORD VARCHAR;
ALTER TABLE t_access_control_base_info ADD COLUMN IF NOT EXISTS IS_BIG_DOOR VARCHAR;
COMMENT ON COLUMN t_access_control_base_info.STATE IS '在线状态 (1-在线, 0-离线)';
COMMENT ON COLUMN t_access_control_base_info.STREAM_ADDRESS IS '视频流id';
COMMENT ON COLUMN t_access_control_base_info.BELONG_PIPELINE_ID IS '所属管线id';
COMMENT ON COLUMN t_access_control_base_info.IS_COLLECTION_MACHINE IS '是否为采集机 (1-是, 0-否)';
COMMENT ON COLUMN t_access_control_base_info.ACCESS_ACCOUNT IS '账号';
COMMENT ON COLUMN t_access_control_base_info.ACCESS_PASSWORD IS '密码';
COMMENT ON COLUMN t_access_control_base_info.IS_BIG_DOOR IS '是否为大门设备';

-- 8. t_emergency_broadcast_host_base_info 添加缺失字段
ALTER TABLE t_emergency_broadcast_host_base_info ADD COLUMN IF NOT EXISTS ONLINE_STATUS VARCHAR;
COMMENT ON COLUMN t_emergency_broadcast_host_base_info.ONLINE_STATUS IS '在线状态 (0-离线, 1-在线, 2-占用)';

-- 9. t_voice_broadcast_material_base_info 添加缺失字段
ALTER TABLE t_voice_broadcast_material_base_info ADD COLUMN IF NOT EXISTS BELONG_STATION_ID VARCHAR;
ALTER TABLE t_voice_broadcast_material_base_info ADD COLUMN IF NOT EXISTS AUDIO_FILE_ID BIGINT;
COMMENT ON COLUMN t_voice_broadcast_material_base_info.BELONG_STATION_ID IS '所属站场';
COMMENT ON COLUMN t_voice_broadcast_material_base_info.AUDIO_FILE_ID IS '语音文件id';

-- 10. t_perimeter_intrusion_zone_base_info 添加缺失字段
ALTER TABLE t_perimeter_intrusion_zone_base_info ADD COLUMN IF NOT EXISTS ZONE_LOCATIONS VARCHAR;
COMMENT ON COLUMN t_perimeter_intrusion_zone_base_info.ZONE_LOCATIONS IS '防区位置信息';

-- 11. t_alarm_result_records 添加缺失字段
ALTER TABLE t_alarm_result_records ADD COLUMN IF NOT EXISTS ALARM_IMAGE VARCHAR;
COMMENT ON COLUMN t_alarm_result_records.ALARM_IMAGE IS '报警图片';

-- 12. t_video_inspection_tasks 添加缺失字段
ALTER TABLE t_video_inspection_tasks ADD COLUMN IF NOT EXISTS STATION_ID VARCHAR;
ALTER TABLE t_video_inspection_tasks ADD COLUMN IF NOT EXISTS INSPECTION_CUSTOM_START_TIME TIMESTAMP;
ALTER TABLE t_video_inspection_tasks ADD COLUMN IF NOT EXISTS INSPECTION_CUSTOM_END_TIME TIMESTAMP;
ALTER TABLE t_video_inspection_tasks ADD COLUMN IF NOT EXISTS TASK_STATUS INT;
COMMENT ON COLUMN t_video_inspection_tasks.STATION_ID IS '站编号';
COMMENT ON COLUMN t_video_inspection_tasks.INSPECTION_CUSTOM_START_TIME IS '自定义巡检周期开始时间';
COMMENT ON COLUMN t_video_inspection_tasks.INSPECTION_CUSTOM_END_TIME IS '自定义巡检周期结束时间';
COMMENT ON COLUMN t_video_inspection_tasks.TASK_STATUS IS '任务状态';

-- 13. t_model_map_management 添加缺失字段
ALTER TABLE t_model_map_management ADD COLUMN IF NOT EXISTS MODEL_FILE_ID BIGINT;
ALTER TABLE t_model_map_management ADD COLUMN IF NOT EXISTS POSITION VARCHAR;
ALTER TABLE t_model_map_management ADD COLUMN IF NOT EXISTS SYSTEM_EXTENSION VARCHAR;
COMMENT ON COLUMN t_model_map_management.MODEL_FILE_ID IS '模型文件id';
COMMENT ON COLUMN t_model_map_management.POSITION IS '位置（存两个坐标）';
COMMENT ON COLUMN t_model_map_management.SYSTEM_EXTENSION IS '系统扩展字段';

-- 14. t_tag_management 添加缺失字段
ALTER TABLE t_tag_management ADD COLUMN IF NOT EXISTS SUBSYSTEM_TYPE VARCHAR;
ALTER TABLE t_tag_management ADD COLUMN IF NOT EXISTS LONGITUDE VARCHAR;
ALTER TABLE t_tag_management ADD COLUMN IF NOT EXISTS LATITUDE VARCHAR;
ALTER TABLE t_tag_management ADD COLUMN IF NOT EXISTS HEIGHT VARCHAR;
COMMENT ON COLUMN t_tag_management.SUBSYSTEM_TYPE IS '子系统类型';
COMMENT ON COLUMN t_tag_management.LONGITUDE IS '经度';
COMMENT ON COLUMN t_tag_management.LATITUDE IS '纬度';
COMMENT ON COLUMN t_tag_management.HEIGHT IS '高度';

-- 15. t_alarm_config 添加缺失字段
ALTER TABLE t_alarm_config ADD COLUMN IF NOT EXISTS FILE_NAME VARCHAR;
ALTER TABLE t_alarm_config ADD COLUMN IF NOT EXISTS FILE_ID VARCHAR;
COMMENT ON COLUMN t_alarm_config.FILE_NAME IS '文件名称';
COMMENT ON COLUMN t_alarm_config.FILE_ID IS '文件ID';

-- ========================================
-- 以下为Entity存在但SQL缺失的表
-- ========================================

-- 工业电视轮巡配置表
CREATE TABLE IF NOT EXISTS t_industrial_tv_roll_poling (
    ROLL_POLING_ID VARCHAR PRIMARY KEY,
    ROLL_POLING_THEME VARCHAR NOT NULL,
    BELONG_UNIT VARCHAR,
    STAY_DURATION INT,
    REMARK VARCHAR,
    CREATE_TIME TIMESTAMP,
    UPDATE_TIME TIMESTAMP,
    RELATED_TV VARCHAR,
    ENABLE VARCHAR,
    STATION_ID VARCHAR
);
COMMENT ON TABLE t_industrial_tv_roll_poling IS '工业电视轮巡配置表';
COMMENT ON COLUMN t_industrial_tv_roll_poling.ROLL_POLING_ID IS '轮巡编号';
COMMENT ON COLUMN t_industrial_tv_roll_poling.ROLL_POLING_THEME IS '轮巡主题';
COMMENT ON COLUMN t_industrial_tv_roll_poling.BELONG_UNIT IS '所属单位';
COMMENT ON COLUMN t_industrial_tv_roll_poling.STAY_DURATION IS '停留时长（分）';
COMMENT ON COLUMN t_industrial_tv_roll_poling.REMARK IS '备注信息';
COMMENT ON COLUMN t_industrial_tv_roll_poling.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_industrial_tv_roll_poling.UPDATE_TIME IS '更新时间';
COMMENT ON COLUMN t_industrial_tv_roll_poling.RELATED_TV IS '关联工业电视id,多个用","分割';
COMMENT ON COLUMN t_industrial_tv_roll_poling.ENABLE IS '启用状态（Y：启用，N：停止）';
COMMENT ON COLUMN t_industrial_tv_roll_poling.STATION_ID IS '站场id';

-- 站场子系统配置表
CREATE TABLE IF NOT EXISTS t_station_subsystem_config (
    ID BIGINT PRIMARY KEY,
    STATION_ID VARCHAR NOT NULL,
    SUBSYSTEM_TYPE VARCHAR NOT NULL,
    CREATE_USER BIGINT,
    CREATE_TIME TIMESTAMP,
    UPDATE_USER BIGINT,
    UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_station_subsystem_config IS '站场子系统配置表';
COMMENT ON COLUMN t_station_subsystem_config.ID IS '主键';
COMMENT ON COLUMN t_station_subsystem_config.STATION_ID IS '站场ID';
COMMENT ON COLUMN t_station_subsystem_config.SUBSYSTEM_TYPE IS '子系统类型';
COMMENT ON COLUMN t_station_subsystem_config.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_station_subsystem_config.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_station_subsystem_config.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_station_subsystem_config.UPDATE_TIME IS '更新时间';

-- 阈值配置表
CREATE TABLE IF NOT EXISTS threshold_config (
    DEVICE_ID VARCHAR PRIMARY KEY,
    HIGH_HIGH_OPERATOR VARCHAR,
    HIGH_HIGH_VALUE VARCHAR,
    HIGH_OPERATOR VARCHAR,
    HIGH_OPERATOR_MAX VARCHAR,
    HIGH_VALUE_MIN VARCHAR,
    HIGH_VALUE_MAX VARCHAR,
    LOW_OPERATOR VARCHAR,
    LOW_VALUE VARCHAR,
    CREATE_USER BIGINT,
    CREATE_TIME TIMESTAMP,
    UPDATE_USER BIGINT,
    UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE threshold_config IS '阈值配置表';
COMMENT ON COLUMN threshold_config.DEVICE_ID IS '设备ID';
COMMENT ON COLUMN threshold_config.HIGH_HIGH_OPERATOR IS '高高报比较符';
COMMENT ON COLUMN threshold_config.HIGH_HIGH_VALUE IS '高高报阈值';
COMMENT ON COLUMN threshold_config.HIGH_OPERATOR IS '高报比较符';
COMMENT ON COLUMN threshold_config.HIGH_OPERATOR_MAX IS '高报比较符上限';
COMMENT ON COLUMN threshold_config.HIGH_VALUE_MIN IS '高报下限';
COMMENT ON COLUMN threshold_config.HIGH_VALUE_MAX IS '高报上限';
COMMENT ON COLUMN threshold_config.LOW_OPERATOR IS '低报比较符';
COMMENT ON COLUMN threshold_config.LOW_VALUE IS '低报阈值';
COMMENT ON COLUMN threshold_config.CREATE_USER IS '创建人';
COMMENT ON COLUMN threshold_config.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN threshold_config.UPDATE_USER IS '更新人';
COMMENT ON COLUMN threshold_config.UPDATE_TIME IS '更新时间';

-- 视频巡检摄像头预设位配置表
CREATE TABLE IF NOT EXISTS t_video_inspection_camera_preset (
    CAMERA_PRESET_ID VARCHAR PRIMARY KEY,
    VIDEO_INSPECTION_ID VARCHAR NOT NULL,
    INDUSTRIAL_TV_ID VARCHAR NOT NULL,
    PRESET_ID VARCHAR NOT NULL,
    PRESET_ALGORITHM VARCHAR,
    INSPECTION_SERIAL_NUMBER INT,
    STAY_DURATION INT,
    CREATE_USER BIGINT,
    CREATE_TIME TIMESTAMP,
    UPDATE_USER BIGINT,
    UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_video_inspection_camera_preset IS '视频巡检摄像头预设位配置表';
COMMENT ON COLUMN t_video_inspection_camera_preset.CAMERA_PRESET_ID IS '视频巡检预设点位ID';
COMMENT ON COLUMN t_video_inspection_camera_preset.VIDEO_INSPECTION_ID IS '视频巡检ID';
COMMENT ON COLUMN t_video_inspection_camera_preset.INDUSTRIAL_TV_ID IS '工业电视ID';
COMMENT ON COLUMN t_video_inspection_camera_preset.PRESET_ID IS '预设位ID';
COMMENT ON COLUMN t_video_inspection_camera_preset.PRESET_ALGORITHM IS '点位算法';
COMMENT ON COLUMN t_video_inspection_camera_preset.INSPECTION_SERIAL_NUMBER IS '巡检序号';
COMMENT ON COLUMN t_video_inspection_camera_preset.STAY_DURATION IS '停留时长';
COMMENT ON COLUMN t_video_inspection_camera_preset.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_video_inspection_camera_preset.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_video_inspection_camera_preset.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_video_inspection_camera_preset.UPDATE_TIME IS '更新时间';

-- 视频巡检任务结果表
CREATE TABLE IF NOT EXISTS t_video_inspection_task_result (
    INSPECTION_RESULT_ID VARCHAR PRIMARY KEY,
    STATION_ID VARCHAR,
    VIDEO_INSPECTION_ID VARCHAR NOT NULL,
    START_TIME TIMESTAMP,
    END_TIME TIMESTAMP,
    INSPECTION_STATUS INT,
    INSPECTION_MESSAGE VARCHAR,
    CREATE_USER BIGINT,
    CREATE_TIME TIMESTAMP,
    UPDATE_USER BIGINT,
    UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_video_inspection_task_result IS '视频巡检任务结果表';
COMMENT ON COLUMN t_video_inspection_task_result.INSPECTION_RESULT_ID IS '视频巡检任务结果ID';
COMMENT ON COLUMN t_video_inspection_task_result.STATION_ID IS '站编号';
COMMENT ON COLUMN t_video_inspection_task_result.VIDEO_INSPECTION_ID IS '视频巡检ID';
COMMENT ON COLUMN t_video_inspection_task_result.START_TIME IS '巡检开始时间';
COMMENT ON COLUMN t_video_inspection_task_result.END_TIME IS '巡检结束时间';
COMMENT ON COLUMN t_video_inspection_task_result.INSPECTION_STATUS IS '巡检状态';
COMMENT ON COLUMN t_video_inspection_task_result.INSPECTION_MESSAGE IS '巡检消息';
COMMENT ON COLUMN t_video_inspection_task_result.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_video_inspection_task_result.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_video_inspection_task_result.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_video_inspection_task_result.UPDATE_TIME IS '更新时间';

-- 视频巡检任务结果明细表
CREATE TABLE IF NOT EXISTS t_video_inspection_task_result_raw (
    INSPECTION_RESULT_RAW_ID VARCHAR PRIMARY KEY,
    VIDEO_INSPECTION_ID VARCHAR NOT NULL,
    INSPECTION_RESULT_ID VARCHAR NOT NULL,
    INDUSTRIAL_TV_ID VARCHAR NOT NULL,
    PRESET_ID VARCHAR NOT NULL,
    PRESET_INSPECT_RESULT_STATUS INT,
    PRESET_INSPECT_RESULT_PIC BIGINT,
    REMARK VARCHAR,
    CREATE_USER BIGINT,
    CREATE_TIME TIMESTAMP,
    UPDATE_USER BIGINT,
    UPDATE_TIME TIMESTAMP
);
COMMENT ON TABLE t_video_inspection_task_result_raw IS '视频巡检任务结果明细表';
COMMENT ON COLUMN t_video_inspection_task_result_raw.INSPECTION_RESULT_RAW_ID IS '视频巡检任务结果明细ID';
COMMENT ON COLUMN t_video_inspection_task_result_raw.VIDEO_INSPECTION_ID IS '视频巡检ID';
COMMENT ON COLUMN t_video_inspection_task_result_raw.INSPECTION_RESULT_ID IS '视频巡检任务结果ID';
COMMENT ON COLUMN t_video_inspection_task_result_raw.INDUSTRIAL_TV_ID IS '工业电视ID';
COMMENT ON COLUMN t_video_inspection_task_result_raw.PRESET_ID IS '预设位ID';
COMMENT ON COLUMN t_video_inspection_task_result_raw.PRESET_INSPECT_RESULT_STATUS IS '预设位巡检结果';
COMMENT ON COLUMN t_video_inspection_task_result_raw.PRESET_INSPECT_RESULT_PIC IS '巡检影像';
COMMENT ON COLUMN t_video_inspection_task_result_raw.REMARK IS '备注';
COMMENT ON COLUMN t_video_inspection_task_result_raw.CREATE_USER IS '创建人';
COMMENT ON COLUMN t_video_inspection_task_result_raw.CREATE_TIME IS '创建时间';
COMMENT ON COLUMN t_video_inspection_task_result_raw.UPDATE_USER IS '更新人';
COMMENT ON COLUMN t_video_inspection_task_result_raw.UPDATE_TIME IS '更新时间';

-- 作业区基础信息表
CREATE TABLE IF NOT EXISTS t_workarea_base_info (
    WORKAREA_ID VARCHAR PRIMARY KEY,
    WORKAREA_NAME VARCHAR NOT NULL,
    WORKAREA_CODE VARCHAR NOT NULL
);
COMMENT ON TABLE t_workarea_base_info IS '作业区基础信息表';
COMMENT ON COLUMN t_workarea_base_info.WORKAREA_ID IS '作业区ID';
COMMENT ON COLUMN t_workarea_base_info.WORKAREA_NAME IS '作业区名称';
COMMENT ON COLUMN t_workarea_base_info.WORKAREA_CODE IS '作业区代码';