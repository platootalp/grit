CREATE TABLE `article` (
  `pkid` int(10) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '标题',
  `description` varchar(1024) COMMENT '描述',
  `status` varchar(30) NOT NULL COMMENT '状态',
   content TEXT COMMENT '内容',
   category_id INT(10) COMMENT '分类ID',
   user_id INT(10) NOT NULL COMMENT '用户ID',
  `sys_create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sys_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sys_deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`pkid`),
  KEY idx_user_id(idx_user_id),
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

CREATE TABLE `user` (
  `pkid` int(10) NOT NULL AUTO_INCREMENT,
  `account` varchar(32) NOT NULL COMMENT '账户',
  `username` varchar(32) NOT NULL COMMENT '用户名',
  `password` varchar(32) NOT NULL COMMENT '密码',
  `avatar` varchar(1024) COMMENT '头像',
  `sys_create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `sys_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `sys_deleted` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`pkid`),
  KEY `idx_account` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
