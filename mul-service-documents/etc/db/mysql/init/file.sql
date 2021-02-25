DROP TABLE IF EXISTS `file`;

CREATE TABLE `file` (
	`hash` VARCHAR (32) NOT NULL UNIQUE COMMENT '文件标识',
    `uid` VARCHAR (10) NOT NULL COMMENT '所属用户ID',
    `folder_hash` VARCHAR (32) NOT NULL COMMENT '所属文件夹标识',
    `type` VARCHAR (10) NOT NULL COMMENT '文件类型',
	`name` VARCHAR (256) NOT NULL COMMENT '文件名称',
	`rw_status` INT DEFAULT 1 COMMENT '文件读写状态',
	`ownership` INT DEFAULT 0 COMMENT '文件所属状态',
	`created_at` DATETIME NOT NULL COMMENT '创建日期',
	`updated_at` DATETIME NOT NULL COMMENT '最后修改日期',
	`deleted_at` DATETIME DEFAULT NULL COMMENT '移除日期',
	PRIMARY KEY (hash),
	FOREIGN KEY (uid) REFERENCES `user`(id)
) ENGINE = INNODB DEFAULT CHARSET = UTF8;