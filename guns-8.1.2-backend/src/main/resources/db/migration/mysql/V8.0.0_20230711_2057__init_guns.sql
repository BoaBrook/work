CREATE TABLE `sys_click_count`  (
  `click_count_id` bigint NOT NULL COMMENT '主键id',
  `business_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务的分类标识',
  `business_key_id` bigint NULL DEFAULT NULL COMMENT '业务的主键id',
  `click_count` bigint NULL DEFAULT NULL COMMENT '点击次数',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`click_count_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户点击数量统计' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_click_status`  (
  `click_status_id` bigint NOT NULL COMMENT '主键id',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `business_key_id` bigint NULL DEFAULT NULL COMMENT '业务的主键id',
  `business_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务的分类标识',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`click_status_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户点击状态' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_hr_org_approver`  (
  `org_approver_id` bigint NOT NULL COMMENT '主键id',
  `org_approver_type` tinyint NULL DEFAULT NULL COMMENT '组织审批类型：1-负责人，2-部长，3-体系负责人，4-部门助理，5-资产助理（专员），6-考勤专员，7-HRBP，8-门禁员，9-办公账号员，10-转岗须知员',
  `org_id` bigint NULL DEFAULT NULL COMMENT '组织机构id',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`org_approver_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '组织机构审批人' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_hr_organization`  (
  `org_id` bigint NOT NULL COMMENT '主键',
  `org_parent_id` bigint NOT NULL COMMENT '父id，一级节点父id是-1',
  `org_pids` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '父ids',
  `org_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织名称',
  `org_short_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组织机构简称',
  `org_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '组织编码',
  `org_sort` decimal(10, 2) NOT NULL COMMENT '排序',
  `status_flag` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
  `org_type` tinyint NULL DEFAULT 1 COMMENT '组织机构类型：1-公司，2-部门',
  `tax_no` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '税号',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `org_level` int NULL DEFAULT NULL COMMENT '组织机构层级',
  `master_org_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对接外部主数据的机构id',
  `master_org_parent_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对接外部主数据的父级机构id',
  `expand_field` json NULL COMMENT '拓展字段',
  `version_flag` bigint NULL DEFAULT NULL COMMENT '乐观锁',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '删除标记：Y-已删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`org_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '组织机构信息' ROW_FORMAT = Dynamic;

INSERT INTO `sys_hr_organization` VALUES (1671418869810540546, -1, '[-1],', '北京公司', '总公司', '10000000', 10.00, 1, 1, NULL, NULL, NULL, '', NULL, NULL, 1, 'N', '2023-06-21 15:35:09', -1, '2023-06-28 21:51:04', 1339550467939639299, NULL);
INSERT INTO `sys_hr_organization` VALUES (1671419146928205826, 1671418869810540546, '[-1],[1671418869810540546],', '信息中心', '信息中心', '10010000', 101.00, 1, 2, NULL, NULL, NULL, NULL, NULL, NULL, 3, 'N', '2023-06-21 15:35:09', -1, '2023-06-28 23:18:55', 1339550467939639299, NULL);
INSERT INTO `sys_hr_organization` VALUES (1671419297969287170, 1671418869810540546, '[-1],[1671418869810540546],', '发展规划部', '发展规划部', '10020000', 102.00, 1, 2, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'N', '2023-06-21 15:35:09', -1, '2023-07-11 13:55:14', 1339550467939639299, NULL);
INSERT INTO `sys_hr_organization` VALUES (1671419436767195137, 1671418869810540546, '[-1],[1671418869810540546],', '法律部', '法律部', '10030000', 103.00, 1, 2, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'N', '2023-06-21 15:35:09', -1, '2023-07-11 13:55:19', 1339550467939639299, NULL);
INSERT INTO `sys_hr_organization` VALUES (1671419890196623362, 1671418869810540546, '[-1],[1671418869810540546],', '北京西城区公司', '北京西城区能源管理公司', '10040000', 104.00, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL, 2, 'N', '2023-06-21 15:35:09', -1, '2023-06-29 11:44:26', 1339550467939639299, NULL);
INSERT INTO `sys_hr_organization` VALUES (1671420255612776449, 1671419890196623362, '[-1],[1671418869810540546],[1671419890196623362],', '能源管理部', '能源部', '10040001', 1040.00, 2, 2, NULL, '', NULL, NULL, NULL, NULL, 2, 'N', '2023-06-21 15:35:09', -1, '2023-06-29 11:44:26', 1339550467939639299, NULL);
INSERT INTO `sys_hr_organization` VALUES (1674672546647220226, -1, '[-1],', '南京公司', NULL, '20000000', 20.00, 1, 1, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'N', '2023-06-30 14:53:44', 1339550467939639299, '2023-07-11 14:03:25', 1339550467939639299, NULL);
INSERT INTO `sys_hr_organization` VALUES (1674675494710255617, 1674672546647220226, '[-1],[1674672546647220226],', '综合管理部', NULL, '20000001', 201.00, 1, 2, NULL, NULL, NULL, NULL, NULL, NULL, 1, 'N', '2023-06-30 15:05:27', 1339550467939639299, '2023-07-11 13:55:54', 1339550467939639299, NULL);

CREATE TABLE `sys_hr_position`  (
  `position_id` bigint NOT NULL COMMENT '主键',
  `position_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '职位名称',
  `position_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '职位编码',
  `position_sort` decimal(10, 2) NOT NULL COMMENT '排序',
  `status_flag` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `expand_field` json NULL COMMENT '拓展字段',
  `version_flag` bigint NULL DEFAULT NULL COMMENT '乐观锁',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '删除标记：Y-已删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`position_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '职位信息' ROW_FORMAT = Dynamic;

INSERT INTO `sys_hr_position` VALUES (1671418731163627522, '员工', 'yg', 1000.00, 1, NULL, NULL, 1, 'N', '2023-06-21 15:28:21', -1, NULL, NULL, NULL);
INSERT INTO `sys_hr_position` VALUES (1671418794157879297, '部门负责人', 'bmfzr', 200.00, 1, NULL, NULL, 1, 'N', '2023-06-29 10:20:54', NULL, '2023-06-29 10:21:19', 1673708058896797697, NULL);
INSERT INTO `sys_hr_position` VALUES (1671418831935975426, '总经理', 'zjl', 100.00, 1, '', NULL, 1, 'N', '2023-06-21 15:28:21', -1, '2023-06-29 10:21:10', 1673708058896797697, NULL);


CREATE TABLE `sys_portal_user_app`  (
  `app_link_id` bigint NOT NULL COMMENT '主键id',
  `user_id` bigint NULL DEFAULT NULL COMMENT '所属用户id',
  `app_id` bigint NULL DEFAULT NULL COMMENT '冗余字段，菜单所属的应用id',
  `menu_id` bigint NULL DEFAULT NULL COMMENT '关联的菜单id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`app_link_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户常用功能' ROW_FORMAT = Dynamic;

INSERT INTO `sys_portal_user_app` VALUES (1678674927629168641, 1339550467939639299, 1671406669800796161, 1671407186899759106, '2023-07-11 15:57:46', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_portal_user_app` VALUES (1678674927629168642, 1339550467939639299, 1671406745336016898, 1671407539607171073, '2023-07-11 15:57:46', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_portal_user_app` VALUES (1678674927629168643, 1339550467939639299, 1671406745336016898, 1671408081100206081, '2023-07-11 15:57:46', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_portal_user_app` VALUES (1678674927629168644, 1339550467939639299, 1671406745336016898, 1671408144094457858, '2023-07-11 15:57:46', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_portal_user_app` VALUES (1678674927629168645, 1339550467939639299, 1671406745336016898, 1673524865274245121, '2023-07-11 15:57:46', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_portal_user_app` VALUES (1678674927629168646, 1339550467939639299, 1671406745336016898, 1671407615163363330, '2023-07-11 15:57:46', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_portal_user_app` VALUES (1678716885718315009, 1678652551806959618, 1671406669800796161, 1671407186899759106, '2023-07-11 18:44:29', 1678652551806959618, NULL, NULL, NULL);

CREATE TABLE `sys_app`  (
  `app_id` bigint NOT NULL COMMENT '主键id',
  `app_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应用名称',
  `app_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `app_icon` bigint NULL DEFAULT NULL COMMENT '应用图标，存fileId，上传的图片',
  `status_flag` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
  `app_sort` decimal(10, 2) NULL DEFAULT NULL COMMENT '排序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `expand_field` json NULL COMMENT '拓展字段',
  `version_flag` bigint NULL DEFAULT NULL COMMENT '乐观锁',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '删除标记：Y-已删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`app_id`) USING BTREE,
  UNIQUE INDEX `APP_CODE_UNIQUE`(`app_code`) USING BTREE COMMENT 'app编码唯一'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统应用' ROW_FORMAT = Dynamic;

INSERT INTO `sys_app` VALUES (1671406669800796161, '门户应用', 'portal', 1673705057339625474, 1, 100.00, '前台门户应用，包含个人信息、常用应用等界面', NULL, 1, 'N', '2023-06-21 14:37:38', NULL, NULL, NULL, NULL);
INSERT INTO `sys_app` VALUES (1671406745336016898, '后台管理', 'system_manager', 1673705057339625478, 1, 200.00, '系统后台管理应用，包含组织架构维护、权限配置等界面', NULL, 1, 'N', '2023-06-21 14:38:35', NULL, NULL, NULL, NULL);

CREATE TABLE `sys_config`  (
  `config_id` bigint NOT NULL COMMENT '主键',
  `config_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '名称',
  `config_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '属性编码',
  `config_value` varchar(3500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '属性值',
  `sys_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'Y' COMMENT '是否是系统参数：Y-是，N-否',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `status_flag` tinyint NULL DEFAULT 1 COMMENT '状态：1-正常，2-停用',
  `group_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '常量所属分类的编码，来自于“常量的分类”字典',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '是否删除：Y-被删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`config_id`) USING BTREE,
  UNIQUE INDEX `code_unique`(`config_code`) USING BTREE COMMENT '配置编码唯一索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置' ROW_FORMAT = Dynamic;

INSERT INTO `sys_config` VALUES (1, '系统配置是否已经初始化的标识', 'SYS_CONFIG_INIT_FLAG', 'false', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (4, 'Linux本地文件保存路径', 'SYS_LOCAL_FILE_SAVE_PATH_LINUX', '/tmp/tempFilePath', 'Y', NULL, 1, 'file_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (5, 'Windows本地文件保存路径', 'SYS_LOCAL_FILE_SAVE_PATH_WINDOWS', 'D:\\tempFilePath', 'Y', NULL, 1, 'file_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (6, '不需要过滤的url', 'SYS_NONE_SECURITY_URLS', '/assets/**,/login,/swagger-ui.html,/favicon.ico,/swagger-ui/**,/error,/webSocket/*,/guns-devops/**,', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (8, 'session过期时间', 'SYS_SESSION_EXPIRED_SECONDS', '3600', 'Y', NULL, 1, 'auth_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (9, '账号单端登录限制', 'SYS_SINGLE_ACCOUNT_LOGIN_FLAG', 'false', 'Y', NULL, 1, 'auth_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (10, '携带token的header头的名称', 'SYS_AUTH_HEADER_NAME', 'Authorization', 'Y', NULL, 1, 'auth_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (11, '携带token的param传参的名称', 'SYS_AUTH_PARAM_NAME', 'token', 'Y', NULL, 1, 'auth_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (12, '系统默认密码', 'SYS_DEFAULT_PASSWORD', '123456', 'Y', NULL, 1, 'auth_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (14, '会话保存在cookie中时，cooke的name', 'SYS_SESSION_COOKIE_NAME', 'Authorization', 'Y', NULL, 1, 'auth_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (21, '系统发布版本', 'SYS_RELEASE_VERSION', '20230511', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (22, '多租户开关', 'SYS_TENANT_OPEN', 'false', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (23, '验证码开关', 'SYS_CAPTCHA_OPEN', 'false', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (29, '获取文件生成auth url的失效时间', 'SYS_DEFAULT_FILE_TIMEOUT_SECONDS', '3600', 'Y', NULL, 1, 'file_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (30, '服务默认部署的环境地址', 'SYS_SERVER_DEPLOY_HOST', 'http://localhost:8080', 'Y', '主要用来生成文件的访问URL', 1, 'file_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (32, '用于auth模块权限校验的jwt失效时间', 'SYS_AUTH_JWT_TIMEOUT_SECONDS', '604800', 'Y', NULL, 1, 'auth_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (33, 'Druid监控界面的url映射', 'SYS_DRUID_URL_MAPPINGS', '/druid/*', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (34, 'Druid控制台账号', 'SYS_DRUID_ACCOUNT', 'admin', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (35, 'Druid控制台账号密码', 'SYS_DRUID_PASSWORD', 'qxvgkmsz4r7p8v4061e0', 'Y', '默认是空串，为空会让程序自动创建一个随机密码', 1, 'sys_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (36, 'Druid控制台的监控数据是否可以重置清零', 'SYS_DRUID_RESET_ENABLE', 'false', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (37, 'druid web url统计的拦截范围', 'SYS_DRUID_WEB_STAT_FILTER_URL_PATTERN', '/*', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (38, 'druid web url统计的排除拦截表达式', 'SYS_DRUID_WEB_STAT_FILTER_EXCLUSIONS', '*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (39, 'druid web url统计的session统计开关', 'SYS_DRUID_WEB_STAT_FILTER_SESSION_STAT_ENABLE', 'false', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (40, 'druid web url统计的session名称', 'SYS_DRUID_WEB_STAT_FILTER_PRINCIPAL_SESSION_NAME', 'Authorization', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (41, 'druid web url统计的session最大监控数', 'SYS_DRUID_WEB_STAT_FILTER_SESSION_STAT_MAX_COUNT', '1000', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (42, 'druid web url统计的cookie名称', 'SYS_DRUID_WEB_STAT_FILTER_PRINCIPAL_COOKIE_NAME', 'Authorization', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (43, 'druid web url统计的是否开启监控单个url调用的sql列表', 'SYS_DRUID_WEB_STAT_FILTER_PROFILE_ENABLE', 'true', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (44, '阿里云短信的accessKeyId', 'SYS_ALIYUN_SMS_ACCESS_KEY_ID', '你的accessKeyId', 'Y', NULL, 1, 'sms_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (45, '阿里云短信的accessKeySecret', 'SYS_ALIYUN_SMS_ACCESS_KEY_SECRET', '你的secret', 'Y', NULL, 1, 'sms_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (46, '阿里云短信的签名', 'SYS_ALIYUN_SMS_SIGN_NAME', '签名名称', 'Y', NULL, 1, 'sms_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (47, '短信发送验证码失效时间', 'SYS_SMS_VALIDATE_EXPIRED_SECONDS', '300', 'Y', NULL, 1, 'sms_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1350666094452482049, '获取XSS排除过滤的url范围', 'SYS_XSS_URL_EXCLUSIONS', '/sysNotice/add,/sysNotice/edit,/databaseInfo/add,/apiResource/record,/sysTheme/add,/sysTheme/edit,/webSocket/*,/sysTableWidth/setTableWidth', 'Y', '', 1, 'sys_config', 'N', '2021-01-17 12:47:46', 1339550467939639299, '2021-03-04 22:14:14', 1339550467939639299);
INSERT INTO `sys_config` VALUES (1350666094452482050, '获取XSS过滤的url范围', 'SYS_XSS_URL_INCLUDES', '/*', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1356246056131649538, 'websocket的ws-url', 'WEB_SOCKET_WS_URL', 'ws://localhost:8000/api/webSocket/{token}', 'Y', '', 1, 'sys_config', 'N', '2021-02-01 22:20:32', 1339550467939639299, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (1367118984192843778, '邮件是否启用账号密码验证', 'SYS_EMAIL_ENABLE_AUTH', 'true', 'N', '', 1, 'java_mail_config', 'N', '2021-03-03 22:25:40', 1339550467939639299, '2021-03-03 22:25:43', 1339550467939639299);
INSERT INTO `sys_config` VALUES (1367119064924807169, '邮箱的账号', 'SYS_EMAIL_ACCOUNT', 'xxx@126.com', 'N', '', 1, 'java_mail_config', 'N', '2021-03-03 22:26:00', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_config` VALUES (1367119226749444098, '邮箱的密码或者授权码', 'SYS_EMAIL_PASSWORD', 'xxx', 'N', '', 1, 'java_mail_config', 'N', '2021-03-03 22:26:38', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_config` VALUES (1367119286195314689, '邮箱的发送方邮箱', 'SYS_EMAIL_SEND_FROM', 'xxx@126.com', 'Y', '', 1, 'java_mail_config', 'N', '2021-03-03 22:26:52', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_config` VALUES (1367119399810621441, '是否开启tls', 'SYS_EMAIL_START_TLS_ENABLE', 'true', 'N', '使用 STARTTLS安全连接，STARTTLS是对纯文本通信协议的扩展。它将纯文本连接升级为加密连接（TLS或SSL）， 而不是使用一个单独的加密通信端口。', 1, 'java_mail_config', 'N', '2021-03-03 22:27:19', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_config` VALUES (1367119457260003329, 'SSL安全连接', 'SYS_EMAIL_TLS_ENABLE', 'true', 'N', '', 1, 'java_mail_config', 'N', '2021-03-03 22:27:33', 1339550467939639299, '2021-03-03 22:28:33', 1339550467939639299);
INSERT INTO `sys_config` VALUES (1367119505888763905, '指定的端口连接到在使用指定的套接字工厂', 'SYS_EMAIL_SOCKET_FACTORY_PORT', '465', 'Y', '', 1, 'java_mail_config', 'N', '2021-03-03 22:27:45', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_config` VALUES (1367119568455196674, 'SMTP超时时长，单位毫秒', 'SYS_EMAIL_SMTP_TIMEOUT', '10000', 'N', '', 1, 'java_mail_config', 'N', '2021-03-03 22:28:00', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_config` VALUES (1367119662306942977, 'Socket连接超时值，单位毫秒，缺省值不超时', 'SYS_EMAIL_CONNECTION_TIMEOUT', '10000', 'N', 'Socket连接超时值，单位毫秒，缺省值不超时', 1, 'java_mail_config', 'N', '2021-03-03 22:28:22', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610114, 'smtp服务器地址', 'SYS_EMAIL_SMTP_HOST', 'smtp.126.com', 'N', NULL, 1, 'java_mail_config', 'N', '2021-06-09 16:55:01', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610115, 'smtp服务端口', 'SYS_EMAIL_SMTP_PORT', '465', 'Y', NULL, 1, 'java_mail_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610125, '拖拽验证码开关', 'SYS_DRAG_CAPTCHA_OPEN', 'false', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610200, 'auth认证用的jwt秘钥，用于校验登录token', 'SYS_AUTH_JWT_SECRET', '995zg7hw6uwfcwfd6s8w5ibyl4i5u9', 'Y', NULL, 1, 'auth_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (1402549781675610205, '解析sso传过来的token', 'SYS_AUTH_SSO_JWT_SECRET', 'aabbccdd', 'Y', NULL, 1, 'ca_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610210, '解析sso加密的数据的秘钥，解密sso单点中jwt中payload的秘钥', 'SYS_AUTH_SSO_DECRYPT_DATA_SECRET', 'EDPpR/BQfEFJiXKgxN8Uno4OnNMGcIJW1F777yySCPA=', 'Y', NULL, 1, 'ca_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610215, '获取是否开启sso远程会话校验', 'SYS_AUTH_SSO_SESSION_VALIDATE_SWITCH', 'false', 'Y', NULL, 1, 'ca_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610220, 'sso会话校验，redis的host', 'SYS_AUTH_SSO_SESSION_VALIDATE_REDIS_HOST', 'localhost', 'Y', NULL, 1, 'ca_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610225, 'sso会话校验，redis的port', 'SYS_AUTH_SSO_SESSION_VALIDATE_REDIS_PORT', '6379', 'Y', NULL, 1, 'ca_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610230, 'sso会话校验，redis的密码', 'SYS_AUTH_SSO_SESSION_VALIDATE_REDIS_PASSWORD', '', 'Y', NULL, 1, 'ca_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610235, 'sso会话校验，redis的数据库序号', 'SYS_AUTH_SSO_SESSION_VALIDATE_REDIS_DB_INDEX', '2', 'Y', NULL, 1, 'ca_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610240, 'sso会话校验，redis的缓存前缀', 'SYS_AUTH_SSO_SESSION_VALIDATE_REDIS_CACHE_PREFIX', 'CA:USER:TOKEN:', 'Y', NULL, 1, 'ca_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610245, 'sso服务器地址', 'SYS_AUTH_SSO_HOST', 'http://localhost:8888', 'Y', NULL, 1, 'ca_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610300, 'C端用户，注册邮件标题', 'CUSTOMER_REG_EMAIL_TITLE', '用户注册', 'Y', NULL, 1, 'customer_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610305, '获取注册邮件的内容模板', 'CUSTOMER_REG_EMAIL_CONTENT', '感谢您注册Guns官方论坛，请点击此激活链接激活您的账户：<a href=\"http://localhost:8080/customer/active?verifyCode={}\">http://localhost:8080/customer/active?verifyCode={} </a>', 'Y', NULL, 1, 'customer_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610310, '获取重置密码的邮件标题', 'CUSTOMER_RESET_PWD_EMAIL_TITLE', '用户校验', 'Y', NULL, 1, 'customer_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610315, '获取重置密码的邮件内容', 'CUSTOMER_RESET_PWD_EMAIL_CONTENT', '您的验证码是【{}】，此验证码用于修改登录密码，请不要泄露给他人，如果不是您本人操作，请忽略此邮件。', 'Y', NULL, 1, 'customer_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610320, '存放用户头像的bucket的名称', 'CUSTOMER_FILE_BUCKET', 'customer-bucket', 'Y', NULL, 1, 'customer_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610325, '存放用户头像的bucket的名称的过期时间', 'CUSTOMER_FILE_BUCKET_EXPIRED_SECONDS', '600', 'Y', NULL, 1, 'customer_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610330, '获取c端用户缓存的过期时间，用在加快获取速度', 'CUSTOMER_CACHE_EXPIRED_SECONDS', '3600', 'Y', NULL, 1, 'customer_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610335, '是否开启旧版密码校验', 'CUSTOMER_OPEN_OLD_PASSWORD_VALIDATE', 'false', 'Y', NULL, 1, 'customer_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610400, '是否开启demo演示', 'SYS_DEMO_ENV_FLAG', 'false', 'Y', NULL, 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610405, '默认存储文件的bucket名称', 'SYS_FILE_DEFAULT_BUCKET', 'defaultBucket', 'Y', NULL, 1, 'file_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1402549781675610500, 'AES秘钥，用在数据库数据加密', 'SYS_ENCRYPT_SECRET_KEY', 'y7v2gpd9bv3bbi5ojhu9ool4pceimer8', 'Y', NULL, 1, 'security_config', 'N', NULL, NULL, '2023-05-11 11:10:28', 1339550467939639299);
INSERT INTO `sys_config` VALUES (1402549781675610505, '开发模式开关', 'DEVOPS_DEV_SWITCH_STATUS', 'true', 'Y', '在开发模式下，允许devops平台访问某些系统接口', 1, 'sys_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1481244035229200386, '全局日志记录，如果开启则所有请求都将记录日志', 'SYS_LOG_GLOBAL_FLAG', 'false', 'Y', NULL, 1, 'file_config', 'N', NULL, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES (1581687626275000321, '登录密码是否进行RSA加密校验，false为关闭', 'SYS_AUTH_PASSWORD_RSA_VALIDATE', 'false', 'Y', NULL, 1, 'auth_config', 'N', NULL, NULL, NULL, NULL);

CREATE TABLE `sys_database_info`  (
  `db_id` bigint NOT NULL COMMENT '主键',
  `db_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据库名称（英文名称）',
  `jdbc_driver` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'jdbc的驱动类型',
  `jdbc_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'jdbc的url',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据库连接的账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据库连接密码',
  `schema_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据库的schema名称，每种数据库的schema意义都不同',
  `status_flag` tinyint NULL DEFAULT NULL COMMENT '数据源状态：1-正常，2-无法连接',
  `error_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '连接失败原因',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注，摘要',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '是否删除，Y-被删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`db_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '多数据源信息' ROW_FORMAT = Dynamic;

INSERT INTO `sys_database_info` VALUES (1678746805743882241, 'master', 'com.mysql.cj.jdbc.Driver', 'jdbc:mysql://localhost:3306/guns?autoReconnect=true&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&serverTimezone=CTT&nullCatalogMeansCurrent=true', 'root', 'root', NULL, 1, NULL, '主数据源，项目启动数据源！', 'N', '2023-07-11 20:43:22', NULL, '2023-07-11 20:43:31', -1);

CREATE TABLE `sys_dict`  (
  `dict_id` bigint NOT NULL COMMENT '字典id',
  `dict_type_id` bigint NOT NULL COMMENT '字典类型的id',
  `dict_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典编码',
  `dict_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典名称',
  `dict_name_pinyin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典名称首字母',
  `dict_encode` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典编码',
  `dict_short_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典简称',
  `dict_short_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典简称的编码',
  `dict_parent_id` bigint NOT NULL COMMENT '上级字典的id(如果没有上级字典id，则为-1)',
  `dict_pids` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '父id集合',
  `status_flag` tinyint NOT NULL COMMENT '状态：(1-启用,2-禁用),参考 StatusEnum',
  `dict_sort` decimal(10, 2) NULL DEFAULT NULL COMMENT '排序，带小数点',
  `version_flag` bigint NULL DEFAULT NULL COMMENT '乐观锁',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '删除标记：Y-已删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`dict_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典' ROW_FORMAT = Dynamic;

INSERT INTO `sys_dict` VALUES (1348235720908619802, 1348235720908619811, 'M', '男', 'n', 'male', NULL, NULL, -1, '[-1],', 1, 110.00, 1, 'N', NULL, NULL, '2023-07-09 22:19:58', 1339550467939639299, NULL);
INSERT INTO `sys_dict` VALUES (1348235720908619803, 1348235720908619811, 'F', '女', 'n', 'female', NULL, NULL, -1, '[-1],', 1, 120.00, 1, 'N', NULL, NULL, '2023-07-09 22:19:58', 1339550467939639299, NULL);
INSERT INTO `sys_dict` VALUES (1348235720908619804, 1348235720908619812, '1', '启用', 'n', 'male', NULL, NULL, -1, '[-1],', 1, 1.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1348235720908619805, 1348235720908619812, '2', '禁用', 'n', 'female', NULL, NULL, -1, '[-1],', 1, 2.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1348235720908619806, 1348235720908619812, '3', '冻结', 'n', 'female', NULL, NULL, -1, '[-1],', 1, 2.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1350457799368257537, 1350457656690618370, 'low', '低', 'd', NULL, '低', NULL, -1, '[-1],', 1, 1.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1350457870780477442, 1350457656690618370, 'middle', '中', 'z', NULL, '中', NULL, -1, '[-1],', 1, 2.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1350457950417727489, 1350457656690618370, 'high', '高', 'g', NULL, '高', NULL, -1, '[-1],', 1, 3.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1353547360691851266, 1353547215422132226, 'sys_config', '系统配置', 'xtpz', NULL, NULL, NULL, -1, '[-1],', 1, 1.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1353547405457657857, 1353547215422132226, 'file_config', '文件配置', 'wjpz', NULL, NULL, NULL, -1, '[-1],', 1, 2.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1353547460558229506, 1353547215422132226, 'auth_config', '鉴权配置', 'jqpz', NULL, NULL, NULL, -1, '[-1],', 1, 3.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1353547539293704194, 1353547215422132226, 'sms_config', '短信配置', 'dxpz', NULL, NULL, NULL, -1, '[-1],', 1, 4.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1353998066804658177, 1353997993299480577, 'chinese', '中文', 'zw', NULL, NULL, NULL, -1, '[-1],', 1, 1.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1353998106784763906, 1353997993299480577, 'english', 'english', 'yw', NULL, NULL, NULL, -1, '[-1],', 1, 2.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1354040749627662337, 1354040335406587906, 'role_system', '系统角色', 'xtjs', NULL, NULL, NULL, -1, '[-1],', 1, 1.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1354040819219554305, 1354040335406587906, 'role_c', 'C端角色', 'Cdjs', NULL, NULL, NULL, -1, '[-1],', 1, 2.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1354041049981771778, 1354040335406587906, 'role_b', 'B端角色', 'Bdjs', NULL, NULL, NULL, -1, '[-1],', 1, 3.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1365251792270045186, 1365251549365317633, 'Y', '是', 's', NULL, NULL, NULL, -1, '[-1],', 1, 1.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1365251827812577282, 1365251549365317633, 'N', '否', 'f', NULL, NULL, NULL, -1, '[-1],', 1, 2.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1365252384094728193, 1365252142779641858, 'com.mysql.cj.jdbc.Driver', 'com.mysql.cj.jdbc.Driver', 'com.mysql.cj.jdbc.Driver', NULL, NULL, NULL, -1, '[-1],', 1, 1.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1402549554864427010, 1353547215422132226, 'java_mail_config', 'java邮件配置', 'javayjpz', NULL, NULL, NULL, -1, '[-1],', 1, 50.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1402549554864427020, 1353547215422132226, 'customer_config', 'C端用户配置', 'cdyhpz', NULL, NULL, NULL, -1, '[-1],', 1, 60.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1526221204984197121, 1353547215422132226, 'ca_config', '单点配置', 'ddpz', NULL, NULL, NULL, -1, '[-1],', 1, 70.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1569699572911206401, 1569699391469809666, '1', '负责人', 'fzr', NULL, NULL, NULL, -1, '[-1],', 1, 10.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1569699624215932930, 1569699391469809666, '2', '部长', 'bz', NULL, NULL, NULL, -1, '[-1],', 1, 20.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1569699732391227394, 1569699391469809666, '3', '体系负责人', 'txfzr', NULL, NULL, NULL, -1, '[-1],', 1, 30.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1569699780906741762, 1569699391469809666, '4', '部门助理', 'bmzl', NULL, NULL, NULL, -1, '[-1],', 1, 40.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1569699833889189889, 1569699391469809666, '5', '资产助理', 'zczl', NULL, NULL, NULL, -1, '[-1],', 1, 50.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1569699872430649345, 1569699391469809666, '6', '考勤专员', 'kqzy', NULL, NULL, NULL, -1, '[-1],', 1, 60.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1569699912133931009, 1569699391469809666, '7', 'HRBP', 'HRBP', NULL, NULL, NULL, -1, '[-1],', 1, 70.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1569699950046244865, 1569699391469809666, '8', '门禁员', 'mjy', NULL, NULL, NULL, -1, '[-1],', 1, 80.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1569700054404722690, 1569699391469809666, '9', '办公账号员', 'bgzhy', NULL, NULL, NULL, -1, '[-1],', 1, 90.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1569700365244592129, 1569699391469809666, '10', '转岗须知员', 'zgxzy', NULL, NULL, NULL, -1, '[-1],', 1, 100.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1574327462970531842, 1574327405802168321, '1', '所有人', 'syr', NULL, NULL, NULL, -1, '[-1],', 1, 10.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1574327499536474114, 1574327405802168321, '2', '当前登录人', 'dqdlr', NULL, NULL, NULL, -1, '[-1],', 1, 20.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1574327582113931266, 1574327405802168321, '3', '申请人', 'sqr', NULL, NULL, NULL, -1, '[-1],', 1, 30.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1574327869830602753, 1574327405802168321, '4', '当前登录人部门', 'dqdlrbm', NULL, NULL, NULL, -1, '[-1],', 1, 40.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict` VALUES (1574327924788568065, 1574327405802168321, '5', '当前申请人部门', 'dqsqrbm', NULL, NULL, NULL, -1, '[-1],', 1, 50.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);

CREATE TABLE `sys_dict_type`  (
  `dict_type_id` bigint NOT NULL COMMENT '字典类型id',
  `dict_type_class` int NULL DEFAULT NULL COMMENT '字典类型： 1-业务类型，2-系统类型，参考 DictTypeClassEnum',
  `dict_type_bus_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典类型业务编码',
  `dict_type_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典类型编码',
  `dict_type_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典类型名称',
  `dict_type_name_pinyin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典类型名称首字母拼音',
  `dict_type_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典类型描述',
  `status_flag` tinyint NULL DEFAULT NULL COMMENT '字典类型的状态：1-启用，2-禁用，参考 StatusEnum',
  `dict_type_sort` decimal(10, 2) NULL DEFAULT NULL COMMENT '排序，带小数点',
  `version_flag` bigint NULL DEFAULT NULL COMMENT '乐观锁',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '删除标记：Y-已删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`dict_type_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型' ROW_FORMAT = Dynamic;

INSERT INTO `sys_dict_type` VALUES (1348235720908619811, 1, 'base', 'sex', '性别', 'xb', NULL, 1, 1.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1348235720908619812, 2, 'system', 'user_status', '用户状态', 'yhzt', NULL, 1, 2.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1350457656690618370, 1, 'notice', 'priority_level', '优先级', 'yxj', '', 1, 5.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1353547215422132226, 2, NULL, 'config_group', '系统配置分组', 'xtpzfz', '系统配置分组', 1, 6.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1353997993299480577, 2, NULL, 'languages', '语种', 'yz', 'i18n 多语言', 1, 7.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1354040335406587906, 2, NULL, 'role_type', '角色类型', 'jslx', '', 1, 8.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1365251549365317633, 1, NULL, 'yn', 'yn', 'yn', NULL, 1, 7.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1365252142779641858, 1, NULL, 'jdbc_type', 'jdbc_type', 'jdbc_type', NULL, 1, 8.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1569699391469809666, 1, NULL, 'org_approver_type', '审批人类型', 'sprlx', '组织机构审批人类型', 1, 10.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_dict_type` VALUES (1574327405802168321, 1, NULL, 'select_relation', '选择关系', 'xzgx', '适用于通用选择器中的选择关系的列举', 1, 20.00, 1, 'N', NULL, NULL, NULL, NULL, NULL);

CREATE TABLE `sys_file_business`  (
  `file_business_id` bigint NOT NULL COMMENT '主键id',
  `business_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务的编码，业务自定义',
  `business_id` bigint NULL DEFAULT NULL COMMENT '业务主键id',
  `file_id` bigint NULL DEFAULT NULL COMMENT '关联文件表的id',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`file_business_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '业务关联的文件' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_file_info`  (
  `file_id` bigint NOT NULL COMMENT '文件主键id',
  `file_code` bigint NOT NULL COMMENT '文件编码，本号升级的依据，解决一个文件多个版本问题，多次上传文件编码不变',
  `file_version` int NOT NULL DEFAULT 1 COMMENT '文件版本，从1开始',
  `file_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '1' COMMENT '当前状态：0-历史版,1-最新版',
  `file_location` tinyint NOT NULL COMMENT '文件存储位置：1-阿里云，2-腾讯云，3-minio，4-本地',
  `file_bucket` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件仓库（文件夹）',
  `file_origin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名称（上传时候的文件全名）',
  `file_suffix` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件后缀，例如.txt',
  `file_size_kb` bigint NULL DEFAULT NULL COMMENT '文件大小kb为单位',
  `file_size_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件大小信息，计算后的',
  `file_object_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '存储到bucket中的名称，主键id+.后缀',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '存储路径',
  `secret_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '是否为机密文件，Y-是机密，N-不是机密',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '是否删除：Y-被删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件信息' ROW_FORMAT = Dynamic;

INSERT INTO `sys_file_info` VALUES (10000, 1479745704687820802, 1, '1', 5, 'defaultBucket', 'defaultAvatar.png', 'png', 8, '7.61 KB', '10000.png', NULL, 'N', 'N', '2022-01-08 17:24:03', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_file_info` VALUES (1479753047148322818, 1479753047165100034, 1, '1', 5, 'defaultBucket', 'logo.png', 'png', 9, '8.86 KB', '1479753047148322818.png', NULL, 'N', 'N', '2022-01-08 17:53:14', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_file_info` VALUES (1673705057339625474, 1673705057339625476, 1, '1', 5, 'defaultBucket', 'icon-app-portal.png', 'png', 10, '9.5 KB', '1673705057339625474.png', NULL, 'N', 'N', '2023-06-27 22:49:16', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_file_info` VALUES (1673705057339625478, 1673705057339625479, 1, '1', 5, 'defaultBucket', 'icon-app-backend.png', 'png', 8, '8.43 KB', '1673705057339625478.png', NULL, 'N', 'N', '2023-06-27 22:49:16', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_file_info` VALUES (1678667508563898369, 1678667508563898370, 1, '1', 5, 'defaultBucket', 'bg-login.jpg', 'jpg', 249, '248.61 KB', '1678667508563898369.jpg', NULL, 'N', 'N', '2023-07-11 15:28:17', 1339550467939639299, NULL, NULL);

CREATE TABLE `sys_file_storage`  (
  `file_id` bigint NOT NULL COMMENT '文件主键id，关联file_info表的主键',
  `file_bytes` longblob NULL COMMENT '具体文件的字节信息',
  PRIMARY KEY (`file_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '文件存储信息' ROW_FORMAT = Dynamic;

INSERT INTO `sys_file_storage` VALUES (10000, 0);
INSERT INTO `sys_file_storage` VALUES (1479753047148322818, 0);
INSERT INTO `sys_file_storage` VALUES (1673705057339625474, 0);
INSERT INTO `sys_file_storage` VALUES (1673705057339625478, 0);
INSERT INTO `sys_file_storage` VALUES (1678667508563898369, 0);

CREATE TABLE `sys_group`  (
  `group_id` bigint NOT NULL COMMENT '分组id',
  `group_biz_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所属业务类别，例如：PROJECT',
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分组名称',
  `business_id` bigint NULL DEFAULT NULL COMMENT '业务主键id',
  `user_id` bigint NULL DEFAULT NULL COMMENT '所属用户id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '业务分组' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_log`  (
  `log_id` bigint NOT NULL COMMENT '主键',
  `log_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志的名称，一般为业务名称',
  `log_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '日志记录的内容',
  `app_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '服务名称，一般为spring.application.name',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '当前用户请求的url',
  `request_params` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'http或方法的请求参数体',
  `request_result` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'http或方法的请求结果',
  `server_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '当前服务器的ip',
  `client_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户端的ip',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `http_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求http方法',
  `client_browser` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户浏览器标识',
  `client_os` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户操作系统',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`log_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日志记录' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_login_log`  (
  `llg_id` bigint NOT NULL COMMENT '主键',
  `llg_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志名称',
  `llg_succeed` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否执行成功',
  `llg_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '具体消息',
  `llg_ip_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录ip',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`llg_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录记录' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL COMMENT '主键',
  `menu_parent_id` bigint NOT NULL COMMENT '父id，顶级节点的父id是-1',
  `menu_pids` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '父id集合，中括号包住，逗号分隔',
  `menu_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单的名称',
  `menu_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单的编码',
  `app_id` bigint NOT NULL COMMENT '所属应用id',
  `menu_sort` decimal(20, 2) NOT NULL DEFAULT 100.00 COMMENT '排序',
  `status_flag` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `menu_type` tinyint NULL DEFAULT NULL COMMENT '菜单类型：10-后台菜单，20-纯前台路由界面，30-内部链接，40-外部链接',
  `antdv_router` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由地址，浏览器显示的URL，例如/menu',
  `antdv_component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '前端组件名',
  `antdv_icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'icon-default' COMMENT '图标编码',
  `antdv_link_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '外部链接地址',
  `antdv_active_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用于非菜单显示页面的重定向url设置',
  `antdv_visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'Y' COMMENT '是否可见(分离版用)：Y-是，N-否',
  `expand_field` json NULL COMMENT '拓展字段',
  `version_flag` bigint NULL DEFAULT NULL COMMENT '乐观锁',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '删除标记：Y-已删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统菜单' ROW_FORMAT = Dynamic;

INSERT INTO `sys_menu` VALUES (1671406619464953857, -1, '[-1],', '门户主页', 'PORTAL_INDEX', 1671406669800796161, 101.00, 1, NULL, 20, '/index', '/index/index', 'icon-menu-zhuye', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:39:24', NULL, '2023-07-07 17:09:26', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671407186899759106, -1, '[-1],', '个人信息', 'PERSONAL_INFO', 1671406669800796161, 102.00, 1, NULL, 20, '/index/personal', '/index/personal', 'icon-menu-gerenxinxi', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:40:04', NULL, '2023-07-07 17:09:26', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671407312775016450, -1, '[-1],', '组织架构', 'ORG_INDEX', 1671406745336016898, 101.00, 1, NULL, 10, '/system/structure', '', 'icon-menu-zuzhijiagou', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:42:07', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671407539607171073, 1671407312775016450, '[-1],[1671407312775016450],', '人员', 'ORG_USER', 1671406745336016898, 10101.00, 1, NULL, 10, '/system/structure/user', '/system/structure/user/index', 'icon-menu-renyuan', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:42:07', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671407615163363330, 1671407312775016450, '[-1],[1671407312775016450],', '机构', 'ORG_LIST', 1671406745336016898, 10102.00, 1, NULL, 10, '/system/structure/organization', '/system/structure/organization/index', 'icon-menu-jigou', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:42:07', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671407652933070850, 1671407312775016450, '[-1],[1671407312775016450],', '职位', 'ORG_POSITION', 1671406745336016898, 10103.00, 1, NULL, 10, '/system/structure/position', '/system/structure/position/index', 'icon-menu-zhiwei', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:42:07', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671407791416406018, -1, '[-1],', '权限控制', 'AUTH_CONTROL', 1671406745336016898, 102.00, 1, NULL, 10, '/system/auth', '', 'icon-menu-quanxiankongzhi', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:42:07', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671407892205531137, 1671407791416406018, '[-1],[1671407791416406018],', '应用', 'AUTH_APP', 1671406745336016898, 10201.00, 1, NULL, 10, '/system/auth/app', '/system/auth/app/index', 'icon-menu-yingyong', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:42:07', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671407967690420226, 1671407791416406018, '[-1],[1671407791416406018],', '角色', 'AUTH_ROLE', 1671406745336016898, 10202.00, 1, NULL, 10, '/system/auth/role', '/system/auth/role/index', 'icon-menu-juese', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:42:07', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671408081100206081, 1671407791416406018, '[-1],[1671407791416406018],', '权限', 'AUTH_PERMISSION', 1671406745336016898, 10203.00, 1, NULL, 10, '/system/auth/permission', '/system/auth/permission/index', 'icon-menu-quanxian', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:42:07', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671408144094457858, 1671407791416406018, '[-1],[1671407791416406018],', '菜单', 'AUTH_MENU', 1671406745336016898, 10204.00, 1, NULL, 10, '/system/auth/menu', '/system/auth/menu/index', 'icon-menu-caidan', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:42:07', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1671408194501603329, 1671407791416406018, '[-1],[1671407791416406018],', '资源', 'AUTH_RESOURCE', 1671406745336016898, 10205.00, 1, NULL, 10, '/system/auth/resource', '/system/auth/resource/index', 'icon-menu-ziyuan', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-21 14:42:07', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673524613037191169, -1, '[-1],', '后台配置', 'BACKEND_CONFIG', 1671406745336016898, 103.00, 1, NULL, 10, '/backend/config', '/backend/config', 'icon-menu-peizhi', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673524865274245118, 1673524613037191169, '[-1],[1673524613037191169],', '系统配置', 'SYS_CONFIG', 1671406745336016898, 10301.00, 1, NULL, 10, '/system/backend/sys_config', '/system/backend/sys-config/index', 'icon-menu-peizhi', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673524865274245121, 1673524613037191169, '[-1],[1673524613037191169],', '字典', 'DICT', 1671406745336016898, 10302.00, 1, NULL, 10, '/system/backend/dict', '/system/backend/dict/index', 'icon-menu-zidian', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673524941069512706, 1673524613037191169, '[-1],[1673524613037191169],', '在线用户', 'ONLINE_USER', 1671406745336016898, 10303.00, 1, NULL, 10, '/system/backend/online', '/system/backend/online/index', 'icon-menu-zaixianyonghu', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673524966277279745, 1673524613037191169, '[-1],[1673524613037191169],', '定时任务', 'TIMER', 1671406745336016898, 10304.00, 1, NULL, 10, '/system/backend/timer', '/system/backend/timer/index', 'icon-menu-dingshirenwu', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673525004151844865, 1673524613037191169, '[-1],[1673524613037191169],', '文件管理', 'FILE_MANAGER', 1671406745336016898, 10305.00, 1, NULL, 10, '/system/backend/file', '/system/backend/file/index', 'icon-menu-wenjianguanli', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673525357136080898, 1673524613037191169, '[-1],[1673524613037191169],', '多数据源', 'MULTI_DS', 1671406745336016898, 10306.00, 1, NULL, 10, '/system/backend/datasource', '/system/backend/datasource/index', 'icon-menu-duoshujuyuan', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673525659931275265, 1673524613037191169, '[-1],[1673524613037191169],', '日志查看', 'LOG_MANAGER', 1671406745336016898, 10307.00, 1, NULL, 10, '/system/backend/log', '', 'icon-menu-rizhi', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673525723072327682, 1673525659931275265, '[-1],[1673524613037191169],[1673525659931275265],', '登录日志', 'LOG_LOGIN', 1671406745336016898, 1030701.00, 1, NULL, 10, '/system/backend/log/loginlog', '/system/backend/log/login-log/index', 'icon-menu-denglurizhi', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673525798674657282, 1673525659931275265, '[-1],[1673524613037191169],[1673525659931275265],', '操作日志', 'OPERATE_LOG', 1671406745336016898, 1030702.00, 1, NULL, 10, '/system/backend/log/operatelog', '/system/backend/log/operate-log/index', 'icon-menu-caozuorizhi', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673525912344489985, 1673524613037191169, '[-1],[1673524613037191169],', '监控管理', 'MONITOR', 1671406745336016898, 10308.00, 1, '', 10, '/system/backend/monitor', '', 'icon-menu-jiankong', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673526227621933057, 1673525912344489985, '[-1],[1673524613037191169],[1673525912344489985],', 'SQL监控', 'SQL_MONITOR', 1671406745336016898, 1030801.00, 1, NULL, 40, 'http://localhost:8080/druid', 'http://localhost:8080/druid', 'icon-menu-sqljiankong', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673526479934484481, 1673525912344489985, '[-1],[1673524613037191169],[1673525912344489985],', '服务器信息', 'SERVER_MONITOR', 1671406745336016898, 1030802.00, 1, NULL, 10, '/system/backend/monitor/server', '/system/backend/monitor/server', 'icon-menu-fuwuqixinxi', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673526656565014530, 1673524613037191169, '[-1],[1673524613037191169],', '主题配置', 'THEME', 1671406745336016898, 10309.00, 1, NULL, 10, '/system/backend/theme', '', 'icon-menu-zhuti', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673526946869571585, 1673526656565014530, '[-1],[1673524613037191169],[1673526656565014530],', '主题管理', 'THEME_MANAGER', 1671406745336016898, 1030901.00, 1, NULL, 10, '/system/backend/theme/manager', '/system/backend/theme/manager/index', 'icon-menu-zhutiguanli', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673527098267168769, 1673526656565014530, '[-1],[1673524613037191169],[1673526656565014530],', '主题模板', 'THEME_TEMPLATE', 1671406745336016898, 1030902.00, 1, NULL, 10, '/system/backend/theme/template', '/system/backend/theme/template/index', 'icon-menu-zhutimuban', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);
INSERT INTO `sys_menu` VALUES (1673527401095917570, 1673526656565014530, '[-1],[1673524613037191169],[1673526656565014530],', '主题属性', 'THEME_ATTR', 1671406745336016898, 1030903.00, 1, NULL, 10, '/system/backend/theme/attr', '/system/backend/theme/attr/index', 'icon-menu-zhutishuxing', NULL, NULL, 'Y', NULL, 1, 'N', '2023-06-27 11:15:17', NULL, '2023-07-07 17:09:32', 1339550467939639299, NULL);

CREATE TABLE `sys_menu_options`  (
  `menu_option_id` bigint NOT NULL COMMENT '主键',
  `app_id` bigint NULL DEFAULT NULL COMMENT '冗余字段，菜单所属的应用id',
  `menu_id` bigint NOT NULL COMMENT '菜单id',
  `option_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '功能或操作的名称',
  `option_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '功能或操作的编码',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`menu_option_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单下的功能操作' ROW_FORMAT = Dynamic;

INSERT INTO `sys_menu_options` VALUES (1671416717948006401, 1671406745336016898, 1671407539607171073, '新增人员', 'ADD_USER', '2023-06-21 15:17:48', NULL, '2023-07-07 15:09:31', 1339550467939639299, NULL);
INSERT INTO `sys_menu_options` VALUES (1671416755763851265, 1671406745336016898, 1671407539607171073, '修改人员', 'EDIT_USER', '2023-06-21 15:17:48', NULL, '2023-07-07 15:09:34', 1339550467939639299, NULL);
INSERT INTO `sys_menu_options` VALUES (1675495204221640706, 1671406745336016898, 1671407615163363330, '新增机构', 'ADD_ORG', '2023-07-02 21:22:41', 1339550467939639299, '2023-07-07 15:10:13', 1339550467939639299, NULL);
INSERT INTO `sys_menu_options` VALUES (1677199976008040449, 1671406669800796161, 1671406619464953857, '公司概况统计', 'COMPANY_STAT_INFO', '2023-07-07 14:16:50', 1339550467939639299, '2023-07-07 14:16:57', 1339550467939639299, NULL);
INSERT INTO `sys_menu_options` VALUES (1677205540070064129, 1671406745336016898, 1671407539607171073, '删除人员', 'DELETE_USER', '2023-07-07 14:38:56', 1339550467939639299, '2023-07-07 15:09:37', 1339550467939639299, NULL);
INSERT INTO `sys_menu_options` VALUES (1677205784526684162, 1671406745336016898, 1671407539607171073, '分配角色', 'ASSIGN_USER_ROLE', '2023-07-07 14:39:55', 1339550467939639299, '2023-07-07 15:09:40', 1339550467939639299, NULL);
INSERT INTO `sys_menu_options` VALUES (1677205870040154114, 1671406745336016898, 1671407539607171073, '重置密码', 'RESET_PASSWORD', '2023-07-07 14:40:15', 1339550467939639299, '2023-07-07 15:09:43', 1339550467939639299, NULL);
INSERT INTO `sys_menu_options` VALUES (1677205994816503809, 1671406745336016898, 1671407539607171073, '修改状态', 'UPDATE_USER_STATUS', '2023-07-07 14:40:45', 1339550467939639299, '2023-07-07 15:09:46', 1339550467939639299, NULL);
INSERT INTO `sys_menu_options` VALUES (1677212372381564929, 1671406745336016898, 1671407652933070850, '新增职位', 'ADD_POSITION', '2023-07-07 15:06:05', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677212407240425474, 1671406745336016898, 1671407652933070850, '修改职位', 'EDIT_POSITION', '2023-07-07 15:06:14', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677212448021643265, 1671406745336016898, 1671407652933070850, '删除职位', 'DELETE_POSITION', '2023-07-07 15:06:23', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677213466805501954, 1671406745336016898, 1671407615163363330, '修改机构', 'EDIT_ORG', '2023-07-07 15:10:26', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677213504298385410, 1671406745336016898, 1671407615163363330, '删除机构', 'DELETE_ORG', '2023-07-07 15:10:35', 1339550467939639299, '2023-07-07 15:11:52', 1339550467939639299, NULL);
INSERT INTO `sys_menu_options` VALUES (1677213572741038081, 1671406745336016898, 1671407615163363330, '设置审批人', 'ASSIGN_APPROVER', '2023-07-07 15:10:52', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677216034650685442, 1671406745336016898, 1671407892205531137, '新增应用', 'ADD_APP', '2023-07-07 15:20:38', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677216100685807617, 1671406745336016898, 1671407892205531137, '修改应用', 'EDIT_APP', '2023-07-07 15:20:54', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677216141127286786, 1671406745336016898, 1671407892205531137, '删除应用', 'DELETE_APP', '2023-07-07 15:21:04', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677223921938694145, 1671406745336016898, 1671407892205531137, '更新应用状态', 'UPDATE_APP_STATUS', '2023-07-07 15:51:59', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677228381343678466, 1671406745336016898, 1671407967690420226, '新增角色', 'ADD_ROLE', '2023-07-07 16:09:42', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677228480924844033, 1671406745336016898, 1671407967690420226, '删除角色', 'DELETE_ROLE', '2023-07-07 16:10:06', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677228556107743233, 1671406745336016898, 1671407967690420226, '修改角色', 'EDIT_ROLE', '2023-07-07 16:10:24', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1677229379281846273, 1671406745336016898, 1671408081100206081, '修改权限', 'CHANGE_ROLE_PERMISSION', '2023-07-07 16:13:40', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1678056521171140609, 1671406745336016898, 1673524865274245121, '新增字典', 'ADD_DICT', '2023-07-09 23:00:26', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1678056564691238914, 1671406745336016898, 1673524865274245121, '修改字典', 'EDIT_DICT', '2023-07-09 23:00:36', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1678056611675832321, 1671406745336016898, 1673524865274245121, '删除字典', 'DELETE_DICT', '2023-07-09 23:00:48', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_menu_options` VALUES (1680610475986931714, 1671406745336016898, 1671408081100206081, '修改数据权限', 'CHANGE_ROLE_DATA_SCOPE', '2023-07-17 00:08:56', 1339550467939639299, NULL, NULL, NULL);

CREATE TABLE `sys_message`  (
  `message_id` bigint NOT NULL COMMENT '主键',
  `receive_user_id` bigint NULL DEFAULT NULL COMMENT '接收用户id',
  `send_user_id` bigint NULL DEFAULT NULL COMMENT '发送用户id',
  `message_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息标题',
  `message_content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息内容',
  `message_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '消息类型',
  `message_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '消息跳转的URL',
  `priority_level` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '优先级',
  `message_send_time` datetime(0) NULL DEFAULT NULL COMMENT '消息发送时间',
  `business_id` bigint NULL DEFAULT NULL COMMENT '业务id',
  `business_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务类型(根据业务id和业务类型可以确定业务数据)',
  `read_flag` tinyint NULL DEFAULT 0 COMMENT '阅读状态：0-未读，1-已读',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '是否删除：Y-被删除，N-未删除',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`message_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统消息' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_notice`  (
  `notice_id` bigint NOT NULL COMMENT '主键',
  `notice_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知标题',
  `notice_summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知摘要',
  `notice_content` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知内容',
  `priority_level` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '优先级',
  `notice_begin_time` datetime(0) NULL DEFAULT NULL COMMENT '开始时间',
  `notice_end_time` datetime(0) NULL DEFAULT NULL COMMENT '结束时间',
  `notice_scope` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '通知范围（用户id字符串）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'N' COMMENT '是否删除：Y-被删除，N-未删除',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知管理' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_resource`  (
  `resource_id` bigint NOT NULL COMMENT '资源id',
  `app_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '应用编码',
  `resource_code` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源编码',
  `resource_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源名称',
  `project_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目编码，一般为spring.application.name',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类名称',
  `method_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法名称',
  `modular_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源模块编码，一般为控制器类名排除Controller',
  `modular_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源模块名称，一般为控制器名称',
  `ip_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源初始化的服务器ip地址',
  `view_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否是视图类型：Y-是，N-否\r\n如果是视图类型，url需要以 \'/view\' 开头，\r\n视图类型的接口会渲染出html界面，而不是json数据，\r\n视图层一般会在前后端不分离项目出现',
  `url` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '资源url',
  `http_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'http请求方法',
  `resource_biz_type` tinyint NULL DEFAULT 1 COMMENT '资源的业务类型：1-业务类，2-系统类',
  `required_login_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否需要登录：Y-是，N-否',
  `required_permission_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否需要鉴权：Y-是，N-否',
  `permission_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '需要鉴权的菜单或者功能编码',
  `validate_groups` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '需要进行参数校验的分组',
  `param_field_descriptions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '接口参数的字段描述',
  `response_field_descriptions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '接口返回结果的字段描述',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`resource_id`) USING BTREE,
  INDEX `RESOURCE_CODE_URL`(`resource_code`, `url`) USING BTREE COMMENT '资源code和url的联合索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '资源' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL COMMENT '主键id',
  `role_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色编码',
  `role_sort` decimal(10, 2) NOT NULL COMMENT '序号',
  `data_scope_type` tinyint NOT NULL DEFAULT 1 COMMENT '数据范围类型：10-仅本人数据，20-本部门数据，30-本部门及以下数据，40-指定部门数据，50-全部数据',
  `status_flag` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-启用，2-禁用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `role_system_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '是否是系统角色：Y-是，N-否。系统角色不能删除',
  `expand_field` json NULL COMMENT '拓展字段',
  `version_flag` bigint NULL DEFAULT NULL COMMENT '乐观锁',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '删除标记：Y-已删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统角色' ROW_FORMAT = Dynamic;

INSERT INTO `sys_role` VALUES (1671420545250439170, '后台管理员', 'backendAdmin', 10.00, 50, 1, NULL, 'Y', NULL, 1, 'N', '2023-06-21 15:47:12', -1, NULL, NULL, NULL);
INSERT INTO `sys_role` VALUES (1671420608181776386, '普通人员', 'employee', 20.00, 50, 1, NULL, 'Y', NULL, 1, 'N', '2023-06-21 15:47:12', -1, NULL, NULL, NULL);

CREATE TABLE `sys_role_data_scope`  (
  `role_data_scope_id` bigint NOT NULL COMMENT '主键',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `organization_id` bigint NOT NULL COMMENT '机构id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`role_data_scope_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色数据范围' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_role_menu`  (
  `role_menu_id` bigint NOT NULL COMMENT '主键',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `app_id` bigint NULL DEFAULT NULL COMMENT '冗余字段，菜单所属的应用id',
  `menu_id` bigint NOT NULL COMMENT '菜单id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`role_menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色菜单关联' ROW_FORMAT = Dynamic;

INSERT INTO `sys_role_menu` VALUES (1678716210741555201, 1671420608181776386, 1671406669800796161, 1671406619464953857, '2023-07-11 18:41:48', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678716210745749505, 1671420608181776386, 1671406669800796161, 1671407186899759106, '2023-07-11 18:41:48', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220865, 1671420545250439170, 1671406669800796161, 1671406619464953857, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220866, 1671420545250439170, 1671406669800796161, 1671407186899759106, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220867, 1671420545250439170, 1671406745336016898, 1671407312775016450, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220868, 1671420545250439170, 1671406745336016898, 1671407539607171073, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220869, 1671420545250439170, 1671406745336016898, 1671407615163363330, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220870, 1671420545250439170, 1671406745336016898, 1671407652933070850, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220871, 1671420545250439170, 1671406745336016898, 1671407791416406018, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220872, 1671420545250439170, 1671406745336016898, 1671407892205531137, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220873, 1671420545250439170, 1671406745336016898, 1671407967690420226, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220874, 1671420545250439170, 1671406745336016898, 1671408081100206081, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220875, 1671420545250439170, 1671406745336016898, 1671408144094457858, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220876, 1671420545250439170, 1671406745336016898, 1671408194501603329, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220877, 1671420545250439170, 1671406745336016898, 1673524613037191169, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845405220878, 1671420545250439170, 1671406745336016898, 1673524865274245118, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609474, 1671420545250439170, 1671406745336016898, 1673524865274245121, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609475, 1671420545250439170, 1671406745336016898, 1673524941069512706, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609476, 1671420545250439170, 1671406745336016898, 1673524966277279745, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609477, 1671420545250439170, 1671406745336016898, 1673525004151844865, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609478, 1671420545250439170, 1671406745336016898, 1673525357136080898, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609479, 1671420545250439170, 1671406745336016898, 1673525659931275265, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609480, 1671420545250439170, 1671406745336016898, 1673525723072327682, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609481, 1671420545250439170, 1671406745336016898, 1673525798674657282, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609482, 1671420545250439170, 1671406745336016898, 1673525912344489985, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609483, 1671420545250439170, 1671406745336016898, 1673526227621933057, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609484, 1671420545250439170, 1671406745336016898, 1673526479934484481, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609485, 1671420545250439170, 1671406745336016898, 1673526656565014530, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609486, 1671420545250439170, 1671406745336016898, 1673526946869571585, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609487, 1671420545250439170, 1671406745336016898, 1673527098267168769, '2023-07-11 20:43:32', -1, NULL, NULL);
INSERT INTO `sys_role_menu` VALUES (1678746845413609488, 1671420545250439170, 1671406745336016898, 1673527401095917570, '2023-07-11 20:43:32', -1, NULL, NULL);

CREATE TABLE `sys_role_menu_options`  (
  `role_menu_option_id` bigint NOT NULL COMMENT '主键',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `app_id` bigint NULL DEFAULT NULL COMMENT '冗余字段，菜单所属的应用id',
  `menu_id` bigint NULL DEFAULT NULL COMMENT '冗余字段，功能所属的菜单id',
  `menu_option_id` bigint NOT NULL COMMENT '菜单功能id，关联sys_menu_options主键id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`role_menu_option_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单下的功能关联' ROW_FORMAT = Dynamic;

INSERT INTO `sys_role_menu_options` VALUES (1671417849311211521, 1339550467939639303, 1671406745336016898, 1671407539607171073, 1671416717948006401, '2023-06-21 15:20:43', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1671417849311211522, 1339550467939639303, 1671406745336016898, 1671407539607171073, 1671416755763851265, '2023-06-21 15:20:43', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678716210938687489, 1671420608181776386, 1671406669800796161, 1671406619464953857, 1677199976008040449, '2023-07-11 18:41:48', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845661073409, 1671420545250439170, 1671406745336016898, 1671407539607171073, 1671416717948006401, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845661073410, 1671420545250439170, 1671406745336016898, 1671407539607171073, 1671416755763851265, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462017, 1671420545250439170, 1671406745336016898, 1671407615163363330, 1675495204221640706, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462018, 1671420545250439170, 1671406669800796161, 1671406619464953857, 1677199976008040449, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462019, 1671420545250439170, 1671406745336016898, 1671407539607171073, 1677205540070064129, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462020, 1671420545250439170, 1671406745336016898, 1671407539607171073, 1677205784526684162, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462021, 1671420545250439170, 1671406745336016898, 1671407539607171073, 1677205870040154114, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462022, 1671420545250439170, 1671406745336016898, 1671407539607171073, 1677205994816503809, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462023, 1671420545250439170, 1671406745336016898, 1671407652933070850, 1677212372381564929, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462024, 1671420545250439170, 1671406745336016898, 1671407652933070850, 1677212407240425474, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462025, 1671420545250439170, 1671406745336016898, 1671407652933070850, 1677212448021643265, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462026, 1671420545250439170, 1671406745336016898, 1671407615163363330, 1677213466805501954, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462027, 1671420545250439170, 1671406745336016898, 1671407615163363330, 1677213504298385410, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462028, 1671420545250439170, 1671406745336016898, 1671407615163363330, 1677213572741038081, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462029, 1671420545250439170, 1671406745336016898, 1671407892205531137, 1677216034650685442, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462030, 1671420545250439170, 1671406745336016898, 1671407892205531137, 1677216100685807617, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462031, 1671420545250439170, 1671406745336016898, 1671407892205531137, 1677216141127286786, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462032, 1671420545250439170, 1671406745336016898, 1671407892205531137, 1677223921938694145, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462033, 1671420545250439170, 1671406745336016898, 1671407967690420226, 1677228381343678466, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462034, 1671420545250439170, 1671406745336016898, 1671407967690420226, 1677228480924844033, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462035, 1671420545250439170, 1671406745336016898, 1671407967690420226, 1677228556107743233, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462036, 1671420545250439170, 1671406745336016898, 1671408081100206081, 1677229379281846273, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462037, 1671420545250439170, 1671406745336016898, 1673524865274245121, 1678056521171140609, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462038, 1671420545250439170, 1671406745336016898, 1673524865274245121, 1678056564691238914, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1678746845669462039, 1671420545250439170, 1671406745336016898, 1673524865274245121, 1678056611675832321, '2023-07-11 20:43:32', -1, NULL, NULL, NULL);
INSERT INTO `sys_role_menu_options` VALUES (1680609076452622338, 1671420545250439170, 1671406745336016898, 1671408081100206081, 1680610475986931714, '2023-07-17 00:03:23', 1339550467939639299, NULL, NULL, NULL);

CREATE TABLE `sys_sms`  (
  `sms_id` bigint NOT NULL COMMENT '主键',
  `phone_number` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `validate_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '短信验证码',
  `template_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '短信模板编号',
  `biz_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务id',
  `status_flag` tinyint NULL DEFAULT NULL COMMENT '发送状态：1-未发送，2-发送成功，3-发送失败，4-失效',
  `source` int NULL DEFAULT NULL COMMENT '来源：1-app，2-pc，3-其他',
  `invalid_time` datetime(0) NULL DEFAULT NULL COMMENT '短信失效截止时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`sms_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短信发送记录' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_table_width`  (
  `table_width_id` bigint NOT NULL COMMENT '主键id',
  `field_business_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '业务标识的编码，例如：PROJECT_TABLE',
  `field_type` int NOT NULL COMMENT '宽度记录的类型：1-全体员工，2-个人独有',
  `user_id` bigint NULL DEFAULT NULL COMMENT '所属用户id',
  `table_width_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '自定义列是否显示、宽度、顺序和列的锁定，一段json',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`table_width_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '业务中表的宽度' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_theme`  (
  `theme_id` bigint NOT NULL COMMENT '主键',
  `theme_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题名称',
  `theme_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题属性，json格式',
  `template_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题模板id',
  `status_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否启用：Y-启用，N-禁用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`theme_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统主题' ROW_FORMAT = Dynamic;

INSERT INTO `sys_theme` VALUES (1477272515573542913, 'Guns后台管理系统默认主题', '{\"positionSort\":1000,\"themeId\":\"1477272515573542913\",\"themeName\":\"Guns后台管理系统默认主题\",\"templateId\":\"1477171926286020610\",\"GUNS_MGR_LOGIN_BACKGROUND_IMG\":\"1678667508563898369\",\"GUNS_MGR_BEI_URL\":\"https://beian.miit.gov.cn/\",\"GUNS_MGR_LOGO\":\"1479753047148322818\",\"GUNS_MGR_NAME\":\"Guns Tech.\",\"GUNS_MGR_FAVICON\":\"1479753047148322818\",\"GUNS_MGR_FOOTER_TEXT\":\"stylefeng开源技术 javaguns.com\",\"GUNS_MGR_BEI_NO\":\"京ICP备001-1\"}', '1477171926286020610', 'Y', '2022-01-01 21:36:29', 1339550467939639299, '2023-07-11 17:24:29', 1339550467939639299);

CREATE TABLE `sys_theme_template`  (
  `template_id` bigint NOT NULL COMMENT '主键',
  `template_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题名称',
  `template_code` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主题编码',
  `template_type` tinyint NOT NULL COMMENT '主题类型：1-系统类型，2-业务类型',
  `status_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '启用状态：Y-启用，N-禁用',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`template_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统主题-模板' ROW_FORMAT = Dynamic;

INSERT INTO `sys_theme_template` VALUES (1477171926286020610, 'Guns后台管理系统模板', 'GUNS_PLATFORM', 1, 'Y', '2022-01-01 14:56:46', 1339550467939639299, '2022-01-01 15:11:27', 1339550467939639299);

CREATE TABLE `sys_theme_template_field`  (
  `field_id` bigint NOT NULL COMMENT '主键',
  `field_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性名称',
  `field_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性编码',
  `field_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性展示类型（字典维护），例如：图片，文本等类型',
  `field_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必填：Y-必填，N-非必填',
  `field_length` int NULL DEFAULT NULL COMMENT '属性值长度',
  `field_description` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '属性描述',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`field_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统主题-模板属性' ROW_FORMAT = Dynamic;

INSERT INTO `sys_theme_template_field` VALUES (1473949204011819009, '平台名称', 'GUNS_MGR_NAME', 'string', 'Y', 10, 'Guns后台管理系统左上角名称', '2021-12-23 17:30:50', 1339550467939639299, '2022-01-01 14:30:42', 1339550467939639299);
INSERT INTO `sys_theme_template_field` VALUES (1473949858369380354, '登录页背景图片', 'GUNS_MGR_LOGIN_BACKGROUND_IMG', 'file', 'Y', NULL, 'Guns后台管理系统登录页图片', '2021-12-23 17:33:26', 1339550467939639299, '2022-01-01 14:32:14', 1339550467939639299);
INSERT INTO `sys_theme_template_field` VALUES (1473950190365319169, '平台LOGO', 'GUNS_MGR_LOGO', 'file', 'Y', NULL, 'Guns后台管理系统左上角logo', '2021-12-23 17:34:45', 1339550467939639299, '2022-01-01 14:46:07', 1339550467939639299);
INSERT INTO `sys_theme_template_field` VALUES (1473950675281387521, '浏览器Icon', 'GUNS_MGR_FAVICON', 'file', 'Y', NULL, 'Guns后台管理系统标签栏图标', '2021-12-23 17:36:40', 1339550467939639299, '2022-01-01 14:46:56', 1339550467939639299);
INSERT INTO `sys_theme_template_field` VALUES (1473951200521494529, '页脚文字', 'GUNS_MGR_FOOTER_TEXT', 'string', 'Y', 100, 'Guns后台管理系统页脚文字', '2021-12-23 17:38:46', 1339550467939639299, '2022-01-01 14:48:08', 1339550467939639299);
INSERT INTO `sys_theme_template_field` VALUES (1473951616827138050, '备案号', 'GUNS_MGR_BEI_NO', 'string', 'N', 100, 'Guns后台管理系统底部备案号', '2021-12-23 17:40:25', 1339550467939639299, '2022-01-01 14:48:46', 1339550467939639299);
INSERT INTO `sys_theme_template_field` VALUES (1477170929413206017, '备案号跳转链接', 'GUNS_MGR_BEI_URL', 'string', 'N', 200, 'Guns后台管理系统备案号跳转到的链接', '2022-01-01 14:52:49', 1339550467939639299, '2022-01-01 14:55:28', 1339550467939639299);

CREATE TABLE `sys_theme_template_rel`  (
  `relation_id` bigint NOT NULL COMMENT '主键',
  `template_id` bigint NOT NULL COMMENT '模板主键id',
  `field_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '属性编码',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`relation_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统主题-模板配置关联关系' ROW_FORMAT = Dynamic;

INSERT INTO `sys_theme_template_rel` VALUES (1477175606452236290, 1477171926286020610, 'GUNS_MGR_NAME', '2022-01-01 15:11:24', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_theme_template_rel` VALUES (1477175606519345154, 1477171926286020610, 'GUNS_MGR_LOGIN_BACKGROUND_IMG', '2022-01-01 15:11:24', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_theme_template_rel` VALUES (1477175606519345155, 1477171926286020610, 'GUNS_MGR_LOGO', '2022-01-01 15:11:24', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_theme_template_rel` VALUES (1477175606586454017, 1477171926286020610, 'GUNS_MGR_FAVICON', '2022-01-01 15:11:24', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_theme_template_rel` VALUES (1477175606653562881, 1477171926286020610, 'GUNS_MGR_FOOTER_TEXT', '2022-01-01 15:11:24', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_theme_template_rel` VALUES (1477175606720671746, 1477171926286020610, 'GUNS_MGR_BEI_NO', '2022-01-01 15:11:24', 1339550467939639299, NULL, NULL);
INSERT INTO `sys_theme_template_rel` VALUES (1477175606787780610, 1477171926286020610, 'GUNS_MGR_BEI_URL', '2022-01-01 15:11:24', 1339550467939639299, NULL, NULL);

CREATE TABLE `sys_timers`  (
  `timer_id` bigint NOT NULL COMMENT '定时器id',
  `timer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '任务名称',
  `action_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执行任务的class的类名（实现了TimerAction接口的类的全称）',
  `cron` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '定时任务表达式',
  `params` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '参数',
  `job_status` int NULL DEFAULT NULL COMMENT '状态：1-运行，2-停止',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注信息',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '是否删除：Y-被删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`timer_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务' ROW_FORMAT = Dynamic;

INSERT INTO `sys_timers` VALUES (1355878268976271362, '定时刷新服务器状态', 'cn.stylefeng.roses.kernel.monitor.system.holder.SystemHardwareInfoHolder', '0 0/1 * * * ? ', NULL, 1, '每1分钟执行一次，刷新服务器状态', 'N', '2021-01-31 21:59:05', 1339550467939639299, '2021-01-31 22:00:23', 1339550467939639299);
INSERT INTO `sys_timers` VALUES (1385068954897223681, '定时检测数据源的链接状态', 'cn.stylefeng.roses.kernel.dsctn.modular.timer.DataSourceStatusCheckTimer', '0/30 * * * * ? ', '', 1, '', 'N', '2021-04-22 11:12:27', 1339550467939639299, NULL, NULL);

CREATE TABLE `sys_translation`  (
  `tran_id` bigint NOT NULL COMMENT '主键id',
  `tran_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '编码',
  `tran_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '多语言条例名称',
  `tran_language_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '语种字典',
  `tran_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '翻译的值',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`tran_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '多语言' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL COMMENT '主键',
  `real_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `nick_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码，加密方式为MD5',
  `password_salt` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码盐',
  `avatar` bigint NULL DEFAULT NULL COMMENT '头像，存的为文件id',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '性别：M-男，F-女',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机',
  `tel` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话',
  `super_admin_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '是否是超级管理员：Y-是，N-否',
  `status_flag` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常，2-冻结',
  `login_count` int NULL DEFAULT 1 COMMENT '登录次数',
  `last_login_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最后登陆IP',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最后登陆时间',
  `user_sort` decimal(10, 2) NULL DEFAULT NULL COMMENT '用户的排序',
  `master_user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对接外部主数据的用户id',
  `expand_field` json NULL COMMENT '拓展字段',
  `version_flag` bigint NULL DEFAULT NULL COMMENT '乐观锁',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '删除标记：Y-已删除，N-未删除',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统用户' ROW_FORMAT = Dynamic;

INSERT INTO `sys_user` VALUES (1339550467939639299, '管理员', NULL, 'admin', 'ab94f1a029e3eda398cd054cdc3acfd7', '2dxzcmp9', 10000, '2020-12-01', 'M', 'sn93@qq.com', '18266668888', NULL, 'Y', 1, 2, '127.0.0.1', '2023-07-11 18:44:49', 1.00, NULL, NULL, 2, 'N', '2020-12-17 20:40:31', -1, '2023-05-23 11:34:38', -1, NULL);
INSERT INTO `sys_user` VALUES (1678652551806959618, '张三', NULL, 'zhangsan', '5e4a4cd54816f15983b793f189fae871', 'ls79ab85', 10000, NULL, 'F', NULL, NULL, NULL, 'N', 1, 2, '127.0.0.1', '2023-07-11 18:44:18', 1000.00, NULL, NULL, 3, 'N', '2023-07-11 14:28:51', 1339550467939639299, '2023-07-11 18:45:04', 1339550467939639299, NULL);

CREATE TABLE `sys_user_data_scope`  (
  `user_data_scope_id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `org_id` bigint NOT NULL COMMENT '机构id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`user_data_scope_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户数据范围' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_user_group`  (
  `user_group_id` bigint NOT NULL COMMENT '用户组id',
  `user_group_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户分组标题简称',
  `user_group_detail_name` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组内选择项的合并',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_group_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户组' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_user_group_detail`  (
  `detail_id` bigint NOT NULL COMMENT '详情id',
  `user_group_id` bigint NULL DEFAULT NULL COMMENT '所属用户组id',
  `select_type` tinyint NULL DEFAULT NULL COMMENT '授权对象类型：1-用户，2-部门，3-角色，4-职位，5-关系',
  `select_value` bigint NULL DEFAULT NULL COMMENT '授权对象id值，例如：用户id，部门id',
  `select_value_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '授权对象名称，例如：张三，研发部，管理员等',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`detail_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户组详情' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_user_org`  (
  `user_org_id` bigint NOT NULL COMMENT '企业员工主键id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `master_user_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对接外部主数据的用户id',
  `org_id` bigint NOT NULL COMMENT '所属机构id',
  `master_org_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '对接外部组织机构id',
  `position_id` bigint NULL DEFAULT NULL COMMENT '职位id',
  `main_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'N' COMMENT '是否是主部门：Y-是，N-不是',
  `expand_field` json NULL COMMENT '拓展字段',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '添加时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '添加人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`user_org_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户组织机构关联' ROW_FORMAT = Dynamic;

INSERT INTO `sys_user_org` VALUES (1678717069600796673, 1678652551806959618, NULL, 1674675494710255617, NULL, 1671418731163627522, 'Y', NULL, '2023-07-11 18:45:13', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_user_org` VALUES (1678717105206243330, 1339550467939639299, NULL, 1671419146928205826, NULL, 1671418831935975426, 'Y', NULL, '2023-07-11 18:45:22', 1339550467939639299, NULL, NULL, NULL);

CREATE TABLE `sys_user_role`  (
  `user_role_id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `role_id` bigint NOT NULL COMMENT '角色id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '修改人',
  `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`user_role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户角色关联' ROW_FORMAT = Dynamic;

INSERT INTO `sys_user_role` VALUES (1673899829178261505, 1339550467939639299, 1671420545250439170, '2023-06-28 11:43:13', 1339550467939639299, NULL, NULL, NULL);
INSERT INTO `sys_user_role` VALUES (1678716759805308929, 1678652551806959618, 1671420608181776386, '2023-07-11 18:43:59', 1339550467939639299, NULL, NULL, NULL);

CREATE TABLE `sys_toc_customer`  (
  `customer_id` bigint NOT NULL COMMENT '主键id',
  `account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码，BCrypt',
  `old_password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '旧密码',
  `old_password_salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '旧的密码盐',
  `nick_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '昵称（显示名称）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `telephone` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机',
  `verify_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱或手机验证码',
  `verified_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否已经邮箱或手机验证通过：Y-通过，N-未通过',
  `avatar` bigint NULL DEFAULT NULL COMMENT '用户头像（文件表id）',
  `avatar_object_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户头像的文件全名',
  `score` int NULL DEFAULT NULL COMMENT '用户积分',
  `status_flag` tinyint NULL DEFAULT NULL COMMENT '用户状态：1-启用，2-禁用',
  `secret_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户秘钥，用在调用会员校验等',
  `member_expire_time` datetime(0) NULL DEFAULT NULL COMMENT '会员截止日期，到期时间',
  `last_login_ip` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '上次登录ip',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '上次登录时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `create_user` bigint NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `update_user` bigint NULL DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`customer_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'C端用户表' ROW_FORMAT = Dynamic;

CREATE TABLE `sys_area`  (
  `area_id` bigint NOT NULL COMMENT '区域id',
  `area_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域编码',
  `area_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '区域全称',
  `parent_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '上级区域编码',
  `area_level` int NULL DEFAULT NULL COMMENT '区域级别',
  `area_sort` decimal(20, 2) NULL DEFAULT 9999.00 COMMENT '排序码',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'N' COMMENT '是否删除',
  `area_pids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所有的上级区域编码,用逗号分隔',
  `create_time` datetime(0) NULL DEFAULT NULL,
  `create_user` bigint NULL DEFAULT NULL,
  `update_time` datetime(0) NULL DEFAULT NULL,
  `update_user` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`area_id`) USING BTREE,
  INDEX `area_code`(`area_code`) USING BTREE,
  INDEX `area_name`(`area_name`) USING BTREE,
  INDEX `parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '行政区域表' ROW_FORMAT = Dynamic;