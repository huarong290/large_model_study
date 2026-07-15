CREATE TABLE IF NOT EXISTS lost_item (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                         user_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '失主姓名',
    phone VARCHAR(20) NOT NULL DEFAULT '' COMMENT '失主手机号',
    lost_name VARCHAR(100) NOT NULL DEFAULT '' COMMENT '失物名称',
    lost_date_time VARCHAR(100) NOT NULL DEFAULT '' COMMENT '丢失时间',
    lost_location VARCHAR(200) NOT NULL DEFAULT '' COMMENT '丢失地址',
    lost_description TEXT COMMENT '失物特征',
    status INT NOT NULL DEFAULT 0 COMMENT '状态：0-登记 1-已找到 2-已归还',
    delete_flag         BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    create_by           VARCHAR(64)   NOT NULL DEFAULT 'admin',
    create_time          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           VARCHAR(64)   NOT NULL DEFAULT 'admin',
    update_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失物登记表';

CREATE TABLE IF NOT EXISTS found_item (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                          finder_name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '拾得人姓名',
    finder_phone VARCHAR(20) NOT NULL DEFAULT '' COMMENT '拾得人手机号',
    find_name VARCHAR(100) NOT NULL DEFAULT '' COMMENT '失物名称',
    find_date_time VARCHAR(100) NOT NULL DEFAULT '' COMMENT '丢失时间',
    find_location VARCHAR(200) NOT NULL DEFAULT '' COMMENT '丢失地址',
    find_description TEXT COMMENT '物品特征',
    status INT NOT NULL DEFAULT 0 COMMENT '状态：0-登记 1-已找到 2-已归还',
    delete_flag         BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    create_by           VARCHAR(64)   NOT NULL DEFAULT 'admin',
    create_time          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           VARCHAR(64)   NOT NULL DEFAULT 'admin',
    update_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招领登记表';

CREATE TABLE IF NOT EXISTS chat_his_msg (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
                                            chat_id VARCHAR(64) NOT NULL DEFAULT ''  COMMENT '会话ID',
    message VARCHAR(6000) NOT NULL DEFAULT ''  COMMENT '消息内容',
    role_type VARCHAR(10) NOT NULL DEFAULT ''  COMMENT 'User-用户消息 AI-LLM 返回消息',
    delete_flag         BIGINT UNSIGNED NOT NULL DEFAULT '0' COMMENT '删除标识',
    create_by           VARCHAR(64)   NOT NULL DEFAULT 'admin',
    create_time          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_by           VARCHAR(64)   NOT NULL DEFAULT 'admin',
    update_time         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_chat_id (chat_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话历史表';