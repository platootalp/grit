一 、项目概述
1.1 背景
【可选】描述项目背景。（红色示例仅做参考，不必加入设计文档）

1.2 项目目标及收益
【可选】描述项目要达成的目标及收益

1.3 名词解释
【可选】需要对专业的业务名词做出说明

1.4 文档资料
【可选】项目关联文档汇总

#

类型

链接

备注

1	MRD	wiki链接	备注
2	PRD	wiki链接	备注
3	三方文档	wiki链接	备注
二、系统分析
2.1 业务流程图
【可选】描述关键业务流程，使用高亮突出增量变更流程。





2.2 用例图
【可选】描述系统或软件的功能及其与外界交互者之间的交互关系





2.3 逻辑架构
【可选】全局性描述系统间逻辑交互。







2.4 状态图
【可选】系统核心状态流转描述



2.5 领域模型
【可选】领域实体关系描述



2.6 领域交互关系
【可选】上下游领域的描述着重外部领域交互边界。

领域

与本领域关系

交互内容

业务系统	上游	集成进件页面，收集进件数据并上送到二清合规平台；部分业务系统还会承接清分数据的计算，并将清分数据推送到二清合规平台
支付系统	上游	推送支付数据
清结算	上游	推送补贴、分佣、确认收货等数据
账务系统	下游	开立内部记账账户；调整内部账户余额
对账系统	下游	向对账系统推送清算数据以供对账
企业客户	下游	进件开户和绑卡信息来源
2.7 影响面分析
【可选】项目影响面评估，列举所影响的领域和流程。ps：一个需求至少会引发1个领域内的1个流程的变更。

领域

流程

是否影响

影响概述

交易	下单		描述对下单的影响
支付	支付
交易	提交
库存	占库
WMS	打包

TMS	发货

门店	签收

门店	安装

清结算	分账

2.8 核心关注点
【可选】需要提炼设计中的关键要点,，关注点是项目设计分析中需要着重强调和容易遗漏的部分

核心关注点
汽配龙白条核心流程中涉及原老系统流程，需要确保兼容性和场景测试覆盖
因本功能涉及三方对接，需要提前安排外部联调的工期
因涉及3方上线，且有发布顺序依赖，需要拉通上下游及前端团队，严格按上线方案的发布顺序发布发布
由于门店量大，灰度切换需做好阶段性把控
因整体上线系统流量较大，上线前需做好压测，上线后关注流量监控和服务器压力
三、系统建设要素
3.1 应用架构图
【可选】每一个业务域需要有一张通用应用架构图，以SOA框及功能块也维度，需标记出已有能力及本次待建。



3.2 变更模块清单
【可选】所有涉及改动的appid模块清单

领域
APPID

描述

支付
appid名

xxxxx变动
门店	appid名	yyyyy变动
3.3 接口设计
【可选】系统接口描述 ，核心接口需要做时序图

领域
APPID

接口类型
接口url

接口报文
API短链

备注
信贷
int-dubbo-fin-loan-engine

新增	/credit/repay-bill
https://forseti.tuhu.work?hash=760d59fc70d7fab73ab37773895ea953
信贷
int-dubbo-fin-loan-engine

更新	/loan/pay
https://forseti.tuhu.work?hash=cfc1cecdb36fda20fd5b17c069d61675	获取额度测算


xx接口时序



3.4 配置系统
【可选】配置描述 

领域
APPID

配置项描述

配置项KEY

配置项VALUE
信贷	int-spring-fin-finance-bil	账单下载信贷平安还款计划	file.center.biz.config.loan_pab_repay_download	具体value值一
信贷	int-spring-fin-finance-bil	pegion事件通知解析中心	event.subscriber.config.loan_pab_repay_notify_resolve	具体value值二
3.5 定时任务
【可选】定时任务描述 ，核心job需要做时序图

领域
appid

job

类型

描述
信贷
int-spring-fin-finance-bil

billDownloadTaskJobHandler

更新

账单下载任务，新增定时拉取平安前一日还款计划文件（每日08:00）
信贷	ext-spring-fin-credit-business-engine	pushPABCustomerInfoJobHandler	新增	推送平安贷后客户数据任务，每月5日上传客户贷后数据
3.6 消息设计
【可选】消息信息描述 ，核心消息需要有交互图

主题名称	appid	realname
描述

fin.credit.config.change.topic	ext-service-yw-loan-config-engine	production_default_fin.credit.config.change.topic	融信贷配置引擎，配置更新推送
3.7 库表设计
【可选】库表信息描述 

库表清单
库	表	状态	备注
loan	table_001	新增
loan	table_002	更新
risk	table_003	新增
表字段清单（可选）
E-R图


建表语句
CREATE TABLE `credit_apply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `apply_no` varchar(32) NOT NULL COMMENT '申请编号',
  `user_id_card_no` varchar(32) NOT NULL COMMENT '客户唯一标识号',
  `user_category` varchar(32) NOT NULL COMMENT '客户类型 PERSON:对私 COMPANY:对公',
  `user_name` varchar(64) NOT NULL COMMENT '客户名称',
  `apply_mode` varchar(16) NOT NULL COMMENT '申请模式 AUTO:自动 MANUAL:手工',
  `apply_category` varchar(16) NOT NULL COMMENT '申请类型 ADD:新增 CHANGE:变更',
  `credit_amount` bigint(20) NOT NULL COMMENT '授信金额 以分为单位',
  `operator` varchar(32) NOT NULL COMMENT '申请操作员',
  `apply_status` varchar(32) NOT NULL COMMENT '授信申请状态',
  `finish_time` datetime DEFAULT NULL COMMENT '结束时间',
  `addition_info` text COMMENT '额外信息',
  `deleted_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标志',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_user_id_card_no` (`user_id_card_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1698 DEFAULT CHARSET=utf8mb4 COMMENT='授信总申请表'
3.8 数仓影响
【可选】数仓变更描述 

类型

具体内容

已通知数仓

数仓确认

备注

DDL	示例：ALTER TABLE XXX ADD `XXX` XXX(?) NOT NULL DEFAULT 'XXX' COMMENT 'XXX';	否
黄纵

数据源切换	示例：XXX表由XXXDB切换到XXXDB	是
黄纵

枚举变更	示例：XXX表XXX字段XXX枚举由XXX变更为XXX	是
黄纵

四、非功能性设计
【可选】非功能性设计描述，依据清单展开相关内容的设计描述，没有则不填。

清单	是否涉及	备注
稳定性设计	是
安全性和权限设计	是
性能设计	否
扩展性和可维护性设计	否
上线策略	否	如涉及复杂的上线策略，需要体现设计方案
4.1 稳定性设计
灰度&回滚方案
门店类型	业务灰度	流量灰度	灰度切换时间	回滚方式
工场店	100	1%	8.30	关闭灰度开关，流量回切
工场店	500	10%	9.30	关闭灰度开关，流量回切
工场店	1000	50%	10.30	关闭灰度开关，流量回切
工场店	5000	100%	11.30	关闭灰度开关，流量回切
Sentinel

自定义降级策略

手工开关

读写分离和数据库降级

服务降级

缓存降级

异步处理

4.2 安全性和权限设计
数据加密&数据保护&访问控制

对称加密
非对称加密
密码哈希
数字签名
安全随机数生成
TLS/SSL 
数据掩码
数据脱敏
数据分隔
越权
身份验证：
授权
会话管理
API安全
加密设计





掩码设计：



越权设计：





4.3 性能设计
代码优化

数据库优化

缓存

并发和线程优化

网络优化

JVM调优

监控和日志

容量规划

测试

架构优化

4.4 扩展性和可维护性设计
水平扩展与垂直扩展：

无状态设计

分解系统组件

数据库的分片与复制

消息队列和异步处理

代码质量和清晰度

模块化和解耦

自动化测试

日志和监控

灾难恢复和备份策略

4.5 上线策略


五、任务分解&排期
【可选】



任务大类	功能块	功能点	优先级	开发工时（人日）(开发+单元测试)	pd汇总（模块）
设计	贷款预申请	概要设计文档	P0	3	3
开发-前端

P1

开发-后端









接口定义	forseti接口定义	P1	1	1
新工程初始化
新的appid申请：ext-spring-fin-credit-business-api

新工程初始化

P1	0.5	0.5
产品配置	蓝虎端宣传页运营产品	P1	0.5	0.5
风控中台	根据门店ID查询授信信息	P1	1	1
进件	进件作废	P2	0.5	1

进件关联预申请	P1	0.5
合同签约	合同签约工单配置与发起	P1	1	2

合同签约工单回调结果监听	P1	1
放款	放款工单配置与发起	P1	1	2

放款工单回调结果监听	P1	1
提测	预申请	联调	P1	2	3
全流程	冒烟	P1	1
总计


设计：pd

后端：pd

前端：

联调&冒烟：pd



金融排期计划 



六、技术评审
【可选】评审过程及结论

会议主题：xxxx技术评审

评审日期

20xx-xx-xx

会议开始时间

xx:xx

会议结束时间

xx:xx
地点
会议主持	xxx	记录人	xxx
参会人员

张三，李四，王五
评审结论

todo代办

1.系统设计-变更点描述细化 @aaa 回复确认 bbb

2.业务流补充xx,yy,zz场景 @ccc 回复确认 ddd

3.job需要增加轮询频次 @eee 回复确认 fff



