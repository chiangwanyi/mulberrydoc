DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
	`id` VARCHAR (10) COMMENT '用户ID，主键',
	`username` VARCHAR (20) NOT NULL UNIQUE COMMENT '用户名称',
	`avatar` VARCHAR (255) NOT NULL COMMENT '用户头像地址',
	`password` VARCHAR (256) NOT NULL COMMENT '用户密码密文',
	`created_at` DATETIME NOT NULL COMMENT '账号创建日期',
	`updated_at` DATETIME NOT NULL COMMENT '账号最后修改日期',
	`deleted_at` DATETIME DEFAULT NULL COMMENT '账号移除日期',
	PRIMARY KEY (id)
) ENGINE = INNODB DEFAULT CHARSET = UTF8;