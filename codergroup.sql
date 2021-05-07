/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 50643
 Source Host           : localhost:3306
 Source Schema         : codergroup

 Target Server Type    : MySQL
 Target Server Version : 50643
 File Encoding         : 65001

 Date: 13/05/2020 22:42:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for answer
-- ----------------------------
DROP TABLE IF EXISTS `answer`;
CREATE TABLE `answer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `cai_size` int(11) DEFAULT NULL COMMENT '踩数量',
  `comment_size` int(11) DEFAULT NULL COMMENT '评论数量',
  `content` longtext NOT NULL COMMENT '内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `is_accepted` int(11) DEFAULT NULL COMMENT '是否采纳',
  `pid` bigint(20) DEFAULT NULL COMMENT '父回复ID',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `zan_size` int(11) DEFAULT NULL COMMENT '赞数量',
  `question_id` bigint(20) DEFAULT NULL COMMENT '问题ID',
  `reply_user_id` int(11) DEFAULT NULL COMMENT '回复者用户ID',
  `user_id` int(11) DEFAULT NULL COMMENT '回答者用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='回答表';

-- ----------------------------
-- Records of answer
-- ----------------------------
BEGIN;
INSERT INTO `answer` VALUES (1, 0, 0, '<p>111</p>', '2020-04-02 10:53:47', 0, NULL, 'publish', 0, 1, 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for answer_cai
-- ----------------------------
DROP TABLE IF EXISTS `answer_cai`;
CREATE TABLE `answer_cai` (
  `answer_id` bigint(20) NOT NULL COMMENT '回答ID',
  `cai_id` bigint(20) NOT NULL COMMENT '踩ID',
  UNIQUE KEY `UK_r7pma6ib5mc7vmmav21bxhxsm` (`cai_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答和踩关联表';

-- ----------------------------
-- Records of answer_cai
-- ----------------------------
BEGIN;
INSERT INTO `answer_cai` VALUES (5, 16);
INSERT INTO `answer_cai` VALUES (11, 28);
INSERT INTO `answer_cai` VALUES (24, 37);
INSERT INTO `answer_cai` VALUES (31, 40);
INSERT INTO `answer_cai` VALUES (29, 41);
INSERT INTO `answer_cai` VALUES (28, 42);
INSERT INTO `answer_cai` VALUES (36, 51);
COMMIT;

-- ----------------------------
-- Table structure for answer_zan
-- ----------------------------
DROP TABLE IF EXISTS `answer_zan`;
CREATE TABLE `answer_zan` (
  `answer_id` bigint(20) NOT NULL COMMENT '回答ID',
  `zan_id` bigint(20) NOT NULL COMMENT '赞ID',
  UNIQUE KEY `UK_gi6jlruc76s5sotrdj5lama07` (`zan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='回答和赞关联表';

-- ----------------------------
-- Records of answer_zan
-- ----------------------------
BEGIN;
INSERT INTO `answer_zan` VALUES (4, 47);
INSERT INTO `answer_zan` VALUES (5, 51);
INSERT INTO `answer_zan` VALUES (12, 72);
INSERT INTO `answer_zan` VALUES (11, 78);
INSERT INTO `answer_zan` VALUES (24, 96);
INSERT INTO `answer_zan` VALUES (28, 105);
INSERT INTO `answer_zan` VALUES (29, 106);
INSERT INTO `answer_zan` VALUES (32, 110);
INSERT INTO `answer_zan` VALUES (36, 121);
COMMIT;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `bookmark_size` int(11) DEFAULT NULL COMMENT '收藏数',
  `comment_size` int(11) DEFAULT NULL COMMENT '评论数',
  `content` longtext NOT NULL COMMENT '内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `guid` varchar(100) DEFAULT NULL COMMENT 'URL',
  `is_allow_comment` int(11) DEFAULT NULL COMMENT '是否允许评论',
  `is_sticky` int(11) DEFAULT NULL COMMENT '是否置顶',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `summary` varchar(2000) NOT NULL COMMENT '摘要',
  `tags` varchar(100) DEFAULT NULL COMMENT '标签数',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `view_size` int(11) DEFAULT NULL COMMENT '访问数',
  `zan_size` int(11) DEFAULT NULL COMMENT '点赞数',
  `category_id` bigint(20) DEFAULT NULL COMMENT '分类ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10519 DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- ----------------------------
-- Records of article
-- ----------------------------
BEGIN;
INSERT INTO `article` VALUES (10000, 0, 0, '111111111', '2018-06-14 03:13:33', '/articles/10000', 0, 0, 'deleted', '111111111', '现代诗,诗歌,舒婷', '舒婷《致橡树》', '2019-12-16 18:14:55', 30, 1, 10000, 1);
INSERT INTO `article` VALUES (10001, 0, 1, '111111111', '2018-06-14 09:35:27', '/articles/10001', 0, 0, 'publish', '111111111', 'Java,面试', 'Java面试题全集（上）—— Java基础篇', '2019-03-18 00:39:22', 64, 1, 10001, 1);
INSERT INTO `article` VALUES (10002, 0, 7, '111111111', '2018-06-14 20:35:58', '/articles/10002', 0, 0, 'publish', '111111111', 'Java,面试', '为什么不能根据返回类型区分重载？', '2019-03-30 00:57:04', 51, 1, 10001, 1);
INSERT INTO `article` VALUES (10003, 2, 5, '111111111', '2018-06-15 16:04:21', '/articles/10003', 0, 0, 'publish', '111111111', '', '愿得一心人，白首不相离', '2019-03-30 00:57:30', 65, 2, 10002, 8);
INSERT INTO `article` VALUES (10004, 0, 1, '111111111', '2018-06-17 09:30:52', '/articles/10004', 0, 0, 'publish', '111111111', 'Java,JavaWeb,JDBC', '理解Class.forName()', '2019-09-12 11:15:54', 9, 0, 10003, 1);
INSERT INTO `article` VALUES (10005, 0, 0, '111111111', '2018-06-17 10:49:37', '/articles/10005', 0, 0, 'publish', '111111111', '锁,共享锁,排它锁,数据库', '共享锁（S锁）和排它锁（X锁）', '2019-03-29 09:15:24', 17, 0, 10004, 1);
INSERT INTO `article` VALUES (10006, 0, 0, '111111111', '2018-06-17 12:09:01', '/articles/10006', 0, 0, 'publish', '111111111', '脏读,幻读,不可重复读,丢失更新', '脏读、幻读、不可重复读和丢失更新', '2018-10-11 10:35:02', 10, 0, 10004, 1);
INSERT INTO `article` VALUES (10008, 0, 0, '111111111', '2018-06-18 11:42:17', '/articles/10008', 0, 0, 'deleted', '111111111', 'Java,JavaWeb,面试', 'Java面试题全集（中）—— Java Web 篇', '2019-12-16 18:14:53', 13, 0, 10001, 1);
INSERT INTO `article` VALUES (10009, 0, 2, '111111111', '2018-06-18 15:24:42', '/articles/10009', 0, 0, 'deleted', '111111111', 'Java,面试,框架', 'Java面试题全集（下）—— 框架篇', '2019-12-16 18:14:50', 21, 0, 10001, 1);
INSERT INTO `article` VALUES (10010, 1, 1, '111111111', '2018-06-18 22:21:18', '/articles/10010', 0, 1, 'publish', '111111111', 'Java,面试', '2018JAVA面试题复习附答案汇总', '2019-12-16 18:15:11', 64, 1, 10001, 1);
INSERT INTO `article` VALUES (10011, 0, 0, '111111111', '2018-06-19 15:31:12', '/articles/10011', 0, 0, 'publish', '111111111', 'Java,String,StringBuilder,StringBuffer', '探秘Java中的String、StringBuilder以及StringBuffer', '2018-08-01 13:32:06', 9, 1, 10006, 1);
INSERT INTO `article` VALUES (10012, 0, 0, '111111111', '2018-06-19 17:01:16', '/articles/10012', 0, 0, 'publish', '111111111', 'Java,集合,', '【Java集合源码剖析】Java集合框架', '2018-08-05 15:57:30', 15, 0, 10006, 1);
INSERT INTO `article` VALUES (10013, 1, 0, '111111111', '2018-06-19 21:03:52', '/articles/10013', 0, 0, 'publish', '111111111', 'Java,HashMap,源码', '【Java集合源码剖析】HashMap源码剖析', '2019-01-11 16:11:16', 23, 1, 10006, 1);
INSERT INTO `article` VALUES (10014, 0, 0, '111111111', '2018-06-20 09:17:10', '/articles/10014', 0, 0, 'publish', '111111111', 'Java,LinkedList,源码', '【Java集合源码剖析】LinkedList源码剖析', '2018-09-06 10:48:57', 10, 0, 10006, 1);
INSERT INTO `article` VALUES (10015, 0, 0, '111111111', '2018-06-20 10:23:09', '/articles/10015', 0, 0, 'publish', '111111111', 'Java,ArrayList,源码', '【Java集合源码剖析】ArrayList源码剖析', '2019-01-11 16:11:28', 14, 0, 10006, 1);
INSERT INTO `article` VALUES (10016, 1, 0, '111111111', '2018-06-20 10:30:31', '/articles/10016', 0, 0, 'publish', '111111111', 'Java,Vector,源码', '【Java集合源码剖析】Vector源码剖析', '2018-12-26 16:49:38', 7, 0, 10006, 1);
INSERT INTO `article` VALUES (10017, 0, 1, '111111111', '2018-06-20 10:39:51', '/articles/10017', 0, 0, 'publish', '111111111', 'Java,HashTable,源码', '【Java集合源码剖析】Hashtable源码剖析', '2018-12-10 13:15:20', 16, 0, 10006, 1);
INSERT INTO `article` VALUES (10018, 0, 1, '111111111', '2018-06-20 11:35:38', '/articles/10018', 0, 0, 'publish', '111111111', '数据结构,红黑树', '史上最清晰的红黑树讲解', '2018-10-16 14:28:19', 27, 0, 10007, 1);
INSERT INTO `article` VALUES (10019, 0, 0, '111111111', '2018-06-21 16:01:45', '/articles/10019', 0, 0, 'publish', '111111111', 'Java,多线程', '实现多线程的两种方法', '2018-07-05 08:57:44', 6, 0, 10008, 1);
INSERT INTO `article` VALUES (10020, 0, 0, '111111111', '2018-06-21 16:52:51', '/articles/10020', 0, 0, 'publish', '111111111', 'Java,多线程,synchronized', 'Java虚拟机对synchronized的优化', '2018-07-04 14:05:00', 9, 0, 10008, 1);
INSERT INTO `article` VALUES (10021, 0, 0, '111111111', '2018-06-21 17:38:32', '/articles/10021', 0, 0, 'publish', '111111111', 'Java,多线程,wait,notify', 'wait()和notify实现生产者和消费者模式(生产一个消费一个)', '2019-04-16 10:51:07', 3, 0, 10008, 1);
INSERT INTO `article` VALUES (10022, 0, 0, '111111111', '2018-06-21 18:39:46', '/articles/10022', 0, 0, 'publish', '111111111', 'Java,Lock,Condition,多线程', '使用 Lock 和 Condition 实现生产者和消费者模式(生产一个消费一个)', '2018-11-11 15:08:53', 6, 0, 10008, 1);
INSERT INTO `article` VALUES (10023, 0, 0, '111111111', '2018-06-21 21:00:16', '/articles/10023', 0, 0, 'publish', '111111111', 'Java,IO,IO模型', '五种IO模型', '2018-09-27 01:43:12', 9, 0, 10006, 1);
INSERT INTO `article` VALUES (10024, 0, 0, '111111111', '2018-06-22 19:31:12', '/articles/10024', 0, 0, 'publish', '111111111', 'Java,多线程,ThreadLocal', 'Java并发编程：深入剖析ThreadLocal', '2019-03-11 09:36:54', 8, 0, 10008, 1);
INSERT INTO `article` VALUES (10025, 0, 0, '111111111', '2018-06-22 21:01:11', '/articles/10025', 0, 0, 'publish', '111111111', 'Java,多线程,Lock,锁,读写锁', 'Java并发编程：Lock', '2018-07-06 23:39:09', 7, 0, 10008, 1);
INSERT INTO `article` VALUES (10026, 0, 0, '111111111', '2018-06-22 23:22:31', '/articles/10026', 0, 0, 'publish', '111111111', 'Java,多线程', '线程的五大状态', '2018-10-30 22:54:31', 10, 0, 10008, 1);
INSERT INTO `article` VALUES (10027, 0, 0, '111111111', '2018-06-23 16:00:01', '/articles/10027', 0, 0, 'publish', '111111111', 'Java,面试,多线程', '50道Java线程面试题', '2018-10-12 15:36:35', 13, 1, 10008, 1);
INSERT INTO `article` VALUES (10028, 0, 0, '111111111', '2018-06-23 16:39:44', '/articles/10028', 0, 0, 'publish', '111111111', 'Java,ConcurrentHashMap,HashMap,多线程', 'ConcurrentHashMap的实现原理', '2018-10-05 04:19:50', 20, 0, 10008, 1);
INSERT INTO `article` VALUES (10030, 0, 0, '111111111', '2018-06-23 21:52:03', '/articles/10030', 0, 0, 'publish', '111111111', '数据库,事务,事务隔离级别', '理解事务的4种隔离级别', '2019-09-12 11:22:25', 11, 0, 10004, 1);
INSERT INTO `article` VALUES (10031, 0, 1, '111111111', '2018-06-24 08:10:42', '/articles/10031', 0, 0, 'publish', '111111111', '计算机网络,GET,POST', 'GET 和 POST 的区别？', '2018-11-26 09:30:56', 25, 0, 10009, 1);
INSERT INTO `article` VALUES (10032, 0, 0, '111111111', '2018-06-24 09:50:48', '/articles/10032', 0, 0, 'publish', '111111111', '计算机网络,三次握手,四次挥手,TCP', '三次握手和四次挥手', '2019-09-12 11:22:04', 30, 0, 10009, 1);
INSERT INTO `article` VALUES (10033, 0, 2, '111111111', '2018-06-24 20:54:51', '/articles/10033', 0, 0, 'publish', '111111111', 'Java,fail-fast,迭代器', 'fail-fast机制', '2018-08-29 15:09:36', 28, 0, 10006, 1);
INSERT INTO `article` VALUES (10034, 0, 0, '111111111', '2018-06-27 16:13:04', '/articles/10034', 0, 0, 'publish', '111111111', '', '期末复习中，暂停更新', '2018-11-26 09:30:43', 46, 0, 10010, 1);
INSERT INTO `article` VALUES (10035, 0, 2, '111111111', '2018-07-03 16:40:13', '/articles/10035', 0, 0, 'publish', '111111111', '测试,测试1', '标题不短', '2018-10-10 21:25:56', 21, 0, 10011, 23);
INSERT INTO `article` VALUES (10036, 0, 0, '111111111', '2018-07-04 11:02:27', '/articles/10036', 0, 0, 'deleted', '111111111', 'MySQL,存储引擎,MyISAM,InnoDB', 'MySQL存储引擎－－MyISAM与InnoDB区别', '2019-12-16 18:14:39', 33, 0, 10004, 1);
INSERT INTO `article` VALUES (10037, 0, 0, '111111111', '2018-07-04 12:42:07', '/articles/10037', 0, 0, 'publish', '111111111', 'MySQL,数据库', '数据表的垂直拆分和水平拆分', '2019-01-22 01:47:00', 22, 0, 10004, 1);
INSERT INTO `article` VALUES (10039, 0, 0, '111111111', '2018-07-04 17:08:33', '/articles/10039', 0, 0, 'publish', '111111111', '乐观锁,悲观锁,并发', '并发控制中的乐观锁与悲观锁', '2019-02-26 15:57:57', 23, 0, 10004, 1);
INSERT INTO `article` VALUES (10040, 0, 1, '111111111', '2018-07-04 22:59:32', '/articles/10040', 0, 0, 'publish', '111111111', '数据库,MySQL', '【MySQL】20个经典面试题', '2019-02-26 16:00:20', 57, 1, 10004, 1);
INSERT INTO `article` VALUES (10041, 0, 0, '111111111', '2018-07-05 16:35:51', '/articles/10041', 0, 0, 'publish', '111111111', '算法,哈希,Hash', '哈希算法（Hash、散列）应用场景小结', '2019-04-29 17:39:30', 34, 1, 10013, 1);
INSERT INTO `article` VALUES (10042, 0, 0, '111111111', '2018-07-06 10:21:06', '/articles/10042', 0, 0, 'publish', '111111111', 'HTML,JS', '前端的简单知识——input框限制只能输入整数&浮点数', '2019-05-06 20:16:32', 95, 0, 10014, 7);
INSERT INTO `article` VALUES (10043, 0, 0, '111111111', '2018-07-06 15:28:27', '/articles/10043', 0, 0, 'publish', '111111111', 'Ubuntu16.04,redis,配置', 'redis安装与简单配置（Ubuntu16.04）', '2019-05-14 20:24:42', 32, 0, 10015, 9);
INSERT INTO `article` VALUES (10044, 0, 0, '111111111', '2018-07-06 18:03:19', '/articles/10044', 0, 0, 'publish', '111111111', 'Java,jvm', 'Java对象回收流程', '2019-04-27 18:13:19', 31, 0, 10016, 1);
INSERT INTO `article` VALUES (10045, 0, 1, '111111111', '2018-07-06 18:20:18', '/articles/10045', 0, 0, 'publish', '111111111', 'Java,jvm,gc', 'java垃圾回收算法和垃圾收集器', '2019-05-14 19:15:35', 33, 0, 10016, 1);
INSERT INTO `article` VALUES (10046, 0, 0, '111111111', '2018-07-06 19:53:30', '/articles/10046', 0, 0, 'publish', '111111111', 'JVM', '聊聊JVM的年轻代', '2019-05-12 22:41:45', 35, 0, 10016, 1);
INSERT INTO `article` VALUES (10518, 0, 0, '<p>1111</p>', '2020-04-02 10:52:02', '/articles/10518', 0, 0, 'publish', '1111', '', '11', '2020-04-26 15:17:50', 2, 0, 10001, 1);
COMMIT;

-- ----------------------------
-- Table structure for article_zan
-- ----------------------------
DROP TABLE IF EXISTS `article_zan`;
CREATE TABLE `article_zan` (
  `article_id` bigint(20) NOT NULL COMMENT '文章ID',
  `zan_id` bigint(20) NOT NULL COMMENT '赞ID',
  UNIQUE KEY `UK_as6utms0q91qhl62ytxedd9b4` (`zan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章和赞关联表';

-- ----------------------------
-- Table structure for authority
-- ----------------------------
DROP TABLE IF EXISTS `authority`;
CREATE TABLE `authority` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(255) NOT NULL COMMENT '角色名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- Records of authority
-- ----------------------------
BEGIN;
INSERT INTO `authority` VALUES (1, 'ROLE_ADMIN');
INSERT INTO `authority` VALUES (2, 'ROLE_USER');
COMMIT;

-- ----------------------------
-- Table structure for bind
-- ----------------------------
DROP TABLE IF EXISTS `bind`;
CREATE TABLE `bind` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `credential` varchar(255) DEFAULT NULL,
  `identifier` varchar(255) NOT NULL COMMENT '唯一凭证',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `bind_type_id` int(11) DEFAULT NULL COMMENT '绑定类型ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方绑定表';

-- ----------------------------
-- Table structure for bind_type
-- ----------------------------
DROP TABLE IF EXISTS `bind_type`;
CREATE TABLE `bind_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(20) DEFAULT NULL COMMENT '名称',
  `style` varchar(100) DEFAULT NULL COMMENT '图标样式',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_mc0u8igslj2oxpmqmge36owa7` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='第三方绑定类型';

-- ----------------------------
-- Records of bind_type
-- ----------------------------
BEGIN;
INSERT INTO `bind_type` VALUES (1, 'qq', 'fa fa-qq text-primary');
INSERT INTO `bind_type` VALUES (2, 'github', 'fa fa-github text-success');
COMMIT;

-- ----------------------------
-- Table structure for bookmark
-- ----------------------------
DROP TABLE IF EXISTS `bookmark`;
CREATE TABLE `bookmark` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `article_id` bigint(20) DEFAULT NULL COMMENT '文章ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ----------------------------
-- Table structure for bulletin
-- ----------------------------
DROP TABLE IF EXISTS `bulletin`;
CREATE TABLE `bulletin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content` longtext NOT NULL COMMENT '内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `guid` varchar(255) DEFAULT NULL COMMENT 'URL',
  `is_sticky` int(11) DEFAULT NULL COMMENT '是否置顶',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `position` int(11) DEFAULT NULL COMMENT '排序',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- ----------------------------
-- Records of bulletin
-- ----------------------------
BEGIN;
INSERT INTO `bulletin` VALUES (1, '<h1>使用协议</h1><p>特别提示：您只有无条件接受以下所有服务条款，才能申请CoderGroup及旗下网站和软件（以下简称CoderGroup）的注册帐号。我们会坚定不移的实施保护我们用户隐私的措施。本隐私政策适用於您与CoderGroup的交互行为以及您登记和使用CoderGroup的在线服务。除了在本隐私政策和服务条款以及其 他公布的准则的规定的情况下，我们不会公布与用户个人身份有关的资料。请注意CoderGroup不时地会检查其隐私措施，因此有关的措施会随之变化。我们恳请您定期光顾 本页以确保对我们隐私政策最新版本始终保持了解。在阅读完本政策之後，如果您对CoderGroup的隐私措施有进一步的问题，请与admin@codergroup.cn 联系。</p><p><br></p><h2>接受条款</h2><p>CoderGroup系列产品根据以下服务条款为您提供服务。这些条款可由CoderGroup随时更新，且毋须另行通知。您在使用CoderGroup系列产品提供的各项服务之前，应仔细阅读本使用协议。如您不同意本使用协议及/或随时对其的修改，请您立即停止使用CoderGroup所提供的全部服务；您一旦使用CoderGroup系列产品，即视为您已了解并完全同意本使用协议各项内容，包括CoderGroup对使用协议随时所做的任何修改，并成为CoderGroup用户（以下简称“用户”）。</p><h2>服务说明</h2><p>1）因技术故障或其它不可抗力导致长时间无法正常服务时，不承担任何赔偿责任，CoderGroup系列产品保留不经事先通知为维修保养、升级或其它目的暂停任何部分的权利；</p><p>2）除非本使用协议另有其它明示规定，增加或强化目前本服务的任何新功能，包括所推出的新产品，均受到本使用协议之规范。</p><h2>账号说明及安全</h2><p>用户有义务保证密码和帐号的安全，用户利用该密码和帐号所进行的一切活动引起的任何损失或损害，由用户自行承担全部责任，CoderGroup不承担任何责任。如用户发现帐号遭到未授权的使用或发生其他任何安全问题，应立即修改帐号密码并妥善保管，如有必要，请通知CoderGroup。因黑客行为或用户的保管疏忽导致帐号非法使用，CoderGroup不承担任何责任。</p><h2>隐私声明</h2><p>CoderGroup非常重视对用户隐私权的保护，CoderGroup承诺不会在任何情况下擅自将用户的个人资料即注册信息以任何方式提供给其他个人或组织</p><h2>用户行为</h2><p>用户对其自行发表、上传或传送的内容负全部责任，所有用户不得在CoderGroup任何页面发布、转载、传送含有下列内容之一的信息，否则CoderGroup有权自行处理并不通知用户：</p><p>1) 违反宪法确定的基本原则的；</p><p>2) 危害国家安全，泄漏国家机密，颠覆国家政权，破坏国家统一的；</p><p>3) 损害国家荣誉和利益的；</p><p>4) 煽动民族仇恨、民族歧视，破坏民族团结的；</p><p>5) 破坏国家宗教政策，宣扬邪教和封建迷信的；</p><p>6) 散布谣言，扰乱社会秩序，破坏社会稳定的；</p><p>7) 散布淫秽、色情、赌博、暴力、恐怖或者教唆犯罪的；</p><p>8) 侮辱或者诽谤他人，侵害他人合法权益的；</p><p>9) 煽动非法集会、结社、游行、示威、聚众扰乱社会秩序的；</p><p>10) 以非法民间组织名义活动的；</p><p>11) 含有法律、行政法规禁止的其他内容的。</p><h2>责任赔偿</h2><p>由于您通过本服务提供、发布或传送之内容、您与本服务连线、您违反本使用协议、或您侵害他人任何权利因而衍生或导致任何第三人提出任何索赔或请求，包括合理的律师费，您同意赔偿CoderGroup及其子公司、关联企业、高级职员、代理人、品牌共有人或其它合作伙伴及员工，并使其免受损害，并承担由此引发的全部法律责任。</p><h2>责任声明</h2><p>1）用户明确同意其使用CoderGroup服务所存在的风险及一切后果将完全由用户本人承担，CoderGroup对此不承担任何责任。</p><p>2）CoderGroup无法保证服务一定能满足用户的要求，也不保证服务的及时性、安全性、准确性。</p><p>3）对于因不可抗力或CoderGroup不能控制的原因造成的服务中断或其它缺陷，CoderGroup不承担任何责任，但将尽力减少因此而给用户造成的损失和影响。</p><p>4）CoderGroup有权于任何时间暂时或永久修改或终止本服务(或其任何部分)，而无论其通知与否，CoderGroup对用户和任何第三人均无需承担任何责任。</p><h2>责任声明</h2><p>1）本协议的订立、执行和解释及争议的解决均应适用中华人民共和国法律。</p><p>2）如本协议中的任何条款无论因何种原因完全或部分无效或不具有执行力，本协议的其余条款仍应有效并且有约束力。</p><p>3）本协议解释权及修订权归CoderGroup团队所有。</p>', '2018-06-14 02:59:03', '/bulletins/uprotocol', 0, 'uprotocol', 1, 'publish', '用户协议', 1);
INSERT INTO `bulletin` VALUES (2, '<p><b>文章</b>\n</p><ul><li>1、每发布一篇文章：可获得10分；</li><li>2、每评论一次文章：可获得2分；\n</li><li>3、博主的文章被赞一次：可获得5分;</li><li>4、博主的文章被收藏一次：可获得5分;</li><li>5、博主的文章被评论一次：可获得2分;</li></ul><p>\n</p><p><b>问答</b></p><ul><li>1、每提出一个问题：可获得2分；</li><li>2、每回答一个问题：可获得3分；</li><li>3、回答被采纳：可获得5分；</li><li>4、回答被投票：顶1票+1分，踩1票-1分</li></ul><p>\n</p><p>\n</p><p><span style=\\\"color: rgb(227, 55, 55);\\\">温馨提示：鼓励大家多发文章，回答问题，积极互动。</span></p><p><span style=\\\"color: rgb(227, 55, 55);\\\">对于恶意刷分者，我们将扣除其相应的得分，情节严重的，将予以警告。</span></p>', '2018-06-14 03:03:37', '/bulletins/reputation-explanation', 0, 'reputation-explanation', 1, 'publish', '用户积分(声望)说明', 1);
INSERT INTO `bulletin` VALUES (4, '<p>本站源码出售，可当毕业设计，也可以当博客系统项目学习</p><p>本站基于 SpringBoot + SpringData JPA +Thymeleaf 实现，MySQL数据库。</p><p>引入Spring Security作为权限框架，Redis 作为部分缓存。</p><p>前端采用 BootStrap + jQuery 实现。</p><p><br></p><p>需要的朋友可以联系博主：847064370 (微信同号)</p><p>价格可以谈</p><p><br></p><h2><b>提供服务</b></h2><p>1.本系统所有源码和数据库文件（通过GitHub私有仓库方式拉人或直接发送zip包代码文件）</p><p>2.远程调试运行部署</p><p>3.需要服务器部署也可以援助</p><p>4.不懂的地方提供讲解</p><p>5.提出Bug，博主会进行修改</p><p>6.提出改造建议，博主会考虑采纳并修改</p><p><br></p><h2><b>适合用户</b></h2><p>1.做毕设的同学，该项目五脏俱全，如果做博客、论坛类项目可以直接拿来用，或者删减一些不必要的功能。</p><p>2.做个人网站的朋友，可以保留前端页面，如果你有自己的前端模板，也可以很方面的进行修改，无需修改后端代码。</p><p>3.学习SpringBoot的同学，可以学习到整个网站的结构，每个模块怎么实现</p>', '2019-05-06 19:49:02', '/bulletins/sell', 1, 'sell', 100, 'publish', '本站源码出售', 1);
COMMIT;

-- ----------------------------
-- Table structure for cai
-- ----------------------------
DROP TABLE IF EXISTS `cai`;
CREATE TABLE `cai` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '踩ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点踩表';

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `guid` varchar(255) DEFAULT NULL COMMENT 'URL',
  `is_hidden` varchar(1) DEFAULT NULL COMMENT '是否在前台显示',
  `name` varchar(20) NOT NULL COMMENT '名称',
  `position` int(11) DEFAULT NULL COMMENT '排序',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10080 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of category
-- ----------------------------
BEGIN;
INSERT INTO `category` VALUES (10000, '/saysky/articles?category=10000', 'N', '诗歌', 1, 1);
INSERT INTO `category` VALUES (10001, '/saysky/articles?category=10001', 'N', '面试', 2, 1);
INSERT INTO `category` VALUES (10002, '/qiqi/articles?category=10002', 'N', 'love', 1, 8);
INSERT INTO `category` VALUES (10003, '/saysky/articles?category=10003', 'N', 'JavaWeb', 3, 1);
INSERT INTO `category` VALUES (10004, '/saysky/articles?category=10004', 'N', '数据库', 4, 1);
INSERT INTO `category` VALUES (10005, '/xinyue/articles?category=10005', 'N', 'ceshi', 1, 10);
INSERT INTO `category` VALUES (10006, '/saysky/articles?category=10006', 'N', 'Java', 5, 1);
INSERT INTO `category` VALUES (10007, '/saysky/articles?category=10007', 'N', '数据结构', 6, 1);
INSERT INTO `category` VALUES (10008, '/saysky/articles?category=10008', 'N', 'Java并发编程', 7, 1);
INSERT INTO `category` VALUES (10009, '/saysky/articles?category=10009', 'N', '计算机网络', 8, 1);
INSERT INTO `category` VALUES (10010, '/saysky/articles?category=10010', 'N', '其他', 9, 1);
INSERT INTO `category` VALUES (10011, '/l1012384/articles?category=10011', 'N', '20180501', 1, 23);
INSERT INTO `category` VALUES (10012, '/sunny/articles?category=10012', 'N', 'Java', 1, 26);
INSERT INTO `category` VALUES (10013, '/saysky/articles?category=10013', 'N', '算法', 10, 1);
INSERT INTO `category` VALUES (10014, '/ai211932/articles?category=10014', 'N', '日常记录', 1, 7);
INSERT INTO `category` VALUES (10015, '/HickSalmon/articles?category=10015', 'N', 'Ubuntu16.04', 1, 9);
INSERT INTO `category` VALUES (10016, '/saysky/articles?category=10016', 'N', 'JVM', 11, 1);
INSERT INTO `category` VALUES (10017, '/admin1/articles?category=10017', 'N', 'd\'s', 1, 31);
INSERT INTO `category` VALUES (10018, '/ai211932/articles?category=10018', 'N', 'springboot学习之路', 2, 7);
INSERT INTO `category` VALUES (10019, '/poker/articles?category=10019', 'N', '101', 1, 33);
INSERT INTO `category` VALUES (10020, '/saysky/articles?category=10020', 'N', '个人情感', 12, 1);
INSERT INTO `category` VALUES (10021, '/saysky/articles?category=10021', 'N', 'saysky', 13, 1);
INSERT INTO `category` VALUES (10022, '/qiqi/articles?category=10022', 'N', 'python', 2, 8);
INSERT INTO `category` VALUES (10023, '/ai211932/articles?category=10023', 'N', '日常总结', 3, 7);
INSERT INTO `category` VALUES (10024, '/chree/articles?category=10024', 'N', '123', 1, 47);
INSERT INTO `category` VALUES (10025, '/wangbing1991/articles?category=10025', 'N', 'test', 1, 48);
INSERT INTO `category` VALUES (10026, '/codegroup/articles?category=10026', 'N', 'Java', 1, 54);
INSERT INTO `category` VALUES (10027, '/yitiao/articles?category=10027', 'N', '123', 1, 51);
INSERT INTO `category` VALUES (10028, '/yitiao/articles?category=10028', 'N', '456', 2, 51);
INSERT INTO `category` VALUES (10029, '/sunweijia/articles?category=10029', 'N', 'javac', 1, 71);
INSERT INTO `category` VALUES (10031, '/qwertyuiop/articles?category=10031', 'N', '测试数据', 1, 82);
INSERT INTO `category` VALUES (10035, '/sunweijia/articles?category=10035', 'N', '撒旦法', 2, 71);
INSERT INTO `category` VALUES (10036, '/sunweijia/articles?category=10036', 'N', '不心腹大患樊', 3, 71);
INSERT INTO `category` VALUES (10037, '/sunweijia/articles?category=10037', 'N', '是大法官', 4, 71);
INSERT INTO `category` VALUES (10038, '/sunweijia/articles?category=10038', 'N', '哈哈', 5, 71);
INSERT INTO `category` VALUES (10039, '/sunweijia/articles?category=10039', 'N', '匹配', 6, 71);
INSERT INTO `category` VALUES (10040, '/sunweijia/articles?category=10040', 'N', 'china', 7, 71);
INSERT INTO `category` VALUES (10041, '/Albert/articles?category=10041', 'N', 'Albert', 1, 94);
INSERT INTO `category` VALUES (10042, '/test1/articles?category=10042', 'N', 'wenzhang', 1, 77);
INSERT INTO `category` VALUES (10043, '/chenzi/articles?category=10043', 'N', 'ddd', 1, 96);
INSERT INTO `category` VALUES (10044, '/Zachary/articles?category=10044', 'N', 'java', 1, 93);
INSERT INTO `category` VALUES (10045, '/Lewis/articles?category=10045', 'N', 'Spring', 1, 70);
INSERT INTO `category` VALUES (10046, '/Lewis/articles?category=10046', 'N', 'SpringBoot', 2, 70);
INSERT INTO `category` VALUES (10047, '/Lewis/articles?category=10047', 'N', 'Java', 3, 70);
INSERT INTO `category` VALUES (10048, '/tvb1/articles?category=10048', 'N', '心情', 1, 97);
INSERT INTO `category` VALUES (10049, '/mmma/articles?category=10049', 'N', '撒旦', 1, 101);
INSERT INTO `category` VALUES (10050, '/Webb_Chen/articles?category=10050', 'N', 'test', 1, 100);
INSERT INTO `category` VALUES (10051, '/Webb_Chen/articles?category=10051', 'N', '测试', 2, 100);
INSERT INTO `category` VALUES (10052, '/jlb0906/articles?category=10052', 'N', '123', 1, 102);
INSERT INTO `category` VALUES (10053, '/tiger/articles?category=10053', 'N', 'test', 1, 109);
INSERT INTO `category` VALUES (10054, '/fans/articles?category=10054', 'N', '1', 1, 111);
INSERT INTO `category` VALUES (10055, '/qwe86314/articles?category=10055', 'N', 'js', 1, 113);
INSERT INTO `category` VALUES (10056, '/ME_Pu/articles?category=10056', 'N', 'java', 1, 25);
INSERT INTO `category` VALUES (10057, '/ME_Pu/articles?category=10057', 'N', '默认分类', 2, 25);
INSERT INTO `category` VALUES (10058, '/Empirefree/articles?category=10058', 'N', 'Empirefree', 2, 115);
INSERT INTO `category` VALUES (10059, '/Empirefree/articles?category=10059', 'N', '阿斯蒂芬', 1, 115);
INSERT INTO `category` VALUES (10060, '/Empirefree/articles?category=10060', 'N', '大师傅', 3, 115);
INSERT INTO `category` VALUES (10061, '/achi/articles?category=10061', 'N', '因垂丝汀', 1, 59);
INSERT INTO `category` VALUES (10062, '/Hhc0917/articles?category=10062', 'N', 'ceshi', 1, 120);
INSERT INTO `category` VALUES (10063, '/gszdc/articles?category=10063', 'N', 'aerors', 1, 122);
INSERT INTO `category` VALUES (10065, '/fans/articles?category=10065', 'N', 'myself', 2, 111);
INSERT INTO `category` VALUES (10066, '/qwe86314/articles?category=10066', 'N', '9999', 2, 113);
INSERT INTO `category` VALUES (10068, '/fantasy/articles?category=10068', 'N', 'test', 1, 139);
INSERT INTO `category` VALUES (10069, '/qbian/articles?category=10069', 'N', 'test', 1, 140);
INSERT INTO `category` VALUES (10070, '/Forever/articles?category=10070', 'N', '1', 1, 129);
INSERT INTO `category` VALUES (10071, '/2530047598/articles?category=10071', 'N', '测试', 1, 136);
INSERT INTO `category` VALUES (10078, '/wangwei/articles?category=10078', 'N', 'Java', 1, 153);
INSERT INTO `category` VALUES (10079, '/saysky/articles?category=10079', 'N', '商品', 14, 1);
COMMIT;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `cai_size` int(11) DEFAULT NULL COMMENT '踩数量',
  `content` varchar(10000) NOT NULL COMMENT '赞数量',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `floor` int(11) DEFAULT NULL COMMENT '楼层数',
  `is_sticky` int(11) DEFAULT NULL COMMENT '是否置顶',
  `pid` bigint(20) DEFAULT NULL COMMENT '父ID',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `zan_size` int(11) DEFAULT NULL COMMENT '赞数量',
  `article_id` bigint(20) DEFAULT NULL COMMENT '文章ID',
  `reply_user_id` int(11) DEFAULT NULL COMMENT '回复用户ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8mb4 COMMENT='文章评论表';

-- ----------------------------
-- Records of comment
-- ----------------------------
BEGIN;
INSERT INTO `comment` VALUES (87, 0, '111111', '2019-12-16 18:14:14', 1, 0, NULL, 'publish', 0, 10010, 1, 1);
INSERT INTO `comment` VALUES (88, 0, '1111', '2019-12-16 18:15:11', 1, 0, NULL, 'deleted', 0, 10010, 1, 1);
INSERT INTO `comment` VALUES (89, 0, '11', '2020-04-26 15:12:47', 1, 0, NULL, 'deleted', 0, 10518, 1, 1);
INSERT INTO `comment` VALUES (90, 0, '222', '2020-04-26 15:12:56', 2, 0, NULL, 'deleted', 0, 10518, 1, 1);
INSERT INTO `comment` VALUES (91, 0, '222', '2020-04-26 15:13:17', 3, 0, NULL, 'deleted', 0, 10518, 1, 1);
INSERT INTO `comment` VALUES (92, 0, '222', '2020-04-26 15:13:47', 4, 0, NULL, 'deleted', 0, 10518, 1, 1);
INSERT INTO `comment` VALUES (93, 0, '33', '2020-04-26 15:13:56', 6, 0, NULL, 'deleted', 0, 10518, 1, 1);
INSERT INTO `comment` VALUES (94, 0, '222', '2020-04-26 15:17:44', 7, 0, NULL, 'deleted', 0, 10518, 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for comment_cai
-- ----------------------------
DROP TABLE IF EXISTS `comment_cai`;
CREATE TABLE `comment_cai` (
  `comment_id` bigint(20) NOT NULL COMMENT '评论ID',
  `cai_id` bigint(20) NOT NULL COMMENT '踩ID',
  UNIQUE KEY `UK_c3bitmqg2fgcktae238xlf28k` (`cai_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论和踩关联表';

-- ----------------------------
-- Table structure for comment_zan
-- ----------------------------
DROP TABLE IF EXISTS `comment_zan`;
CREATE TABLE `comment_zan` (
  `comment_id` bigint(20) NOT NULL COMMENT '评论ID',
  `zan_id` bigint(20) NOT NULL COMMENT '赞ID',
  UNIQUE KEY `UK_qph99kuqwhept3uff6iockfbg` (`zan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论和点赞关联表';

-- ----------------------------
-- Table structure for job
-- ----------------------------
DROP TABLE IF EXISTS `job`;
CREATE TABLE `job` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(20) NOT NULL COMMENT '名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_atcl7ldp04r846fq0cep4e3wi` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COMMENT='职业表';

-- ----------------------------
-- Records of job
-- ----------------------------
BEGIN;
INSERT INTO `job` VALUES (15, 'CG影视动画师');
INSERT INTO `job` VALUES (3, 'Java工程师');
INSERT INTO `job` VALUES (5, 'JS工程师');
INSERT INTO `job` VALUES (9, 'Linux系统工程师');
INSERT INTO `job` VALUES (4, 'PHP工程师');
INSERT INTO `job` VALUES (7, 'Phython工程师');
INSERT INTO `job` VALUES (12, 'UI设计师');
INSERT INTO `job` VALUES (6, 'WEB前端工程师');
INSERT INTO `job` VALUES (10, '交互设计师');
INSERT INTO `job` VALUES (17, '产品经理');
INSERT INTO `job` VALUES (14, '全栈工程师');
INSERT INTO `job` VALUES (19, '其他');
INSERT INTO `job` VALUES (2, '学生');
INSERT INTO `job` VALUES (16, '数据库工程师');
INSERT INTO `job` VALUES (1, '未设置');
INSERT INTO `job` VALUES (8, '移动开发工程师');
INSERT INTO `job` VALUES (13, '算法工程师');
INSERT INTO `job` VALUES (11, '软件测试工程师');
INSERT INTO `job` VALUES (18, '页面重构设计');
COMMIT;

-- ----------------------------
-- Table structure for login_record
-- ----------------------------
DROP TABLE IF EXISTS `login_record`;
CREATE TABLE `login_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `area` varchar(100) DEFAULT NULL COMMENT '地域',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `ip` varchar(40) DEFAULT NULL COMMENT 'IP',
  `login_type` varchar(255) DEFAULT NULL COMMENT '登录类型',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='登录记录表';

-- ----------------------------
-- Records of login_record
-- ----------------------------
BEGIN;
INSERT INTO `login_record` VALUES (1, '', '2020-03-01 10:39:51', '192.168.8.185', '账号密码登录', 1);
INSERT INTO `login_record` VALUES (2, '', '2020-03-31 15:16:42', '192.168.98.133', '账号密码登录', 1);
INSERT INTO `login_record` VALUES (3, '', '2020-04-21 10:56:24', '192.168.99.153', '账号密码登录', 1);
INSERT INTO `login_record` VALUES (4, '', '2020-04-26 15:12:40', '192.168.97.170', '账号密码登录', 1);
INSERT INTO `login_record` VALUES (5, '', '2020-04-30 10:33:51', '192.168.97.170', '账号密码登录', 1);
COMMIT;

-- ----------------------------
-- Table structure for mail_retrieve
-- ----------------------------
DROP TABLE IF EXISTS `mail_retrieve`;
CREATE TABLE `mail_retrieve` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `account` varchar(100) DEFAULT NULL COMMENT '用户ID',
  `create_time` bigint(20) DEFAULT NULL COMMENT '创建时间',
  `out_time` bigint(20) DEFAULT NULL COMMENT '过期时间',
  `sid` varchar(255) DEFAULT NULL COMMENT '自定义ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_c12ytnb53vmfbpsg08ta7wl4v` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件记录表';

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content` varchar(500) NOT NULL COMMENT '内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `type` int(11) DEFAULT NULL COMMENT '类型',
  `friend_id` int(11) DEFAULT NULL COMMENT '对方ID',
  `receiver_id` int(11) DEFAULT NULL COMMENT '接收人ID',
  `sender_id` int(11) DEFAULT NULL COMMENT '发送人ID',
  `user_id` int(11) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='私信表';

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content` varchar(255) DEFAULT NULL COMMENT '内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `guid` varchar(255) DEFAULT NULL COMMENT 'URL',
  `more` varchar(255) DEFAULT NULL COMMENT '详细内容',
  `status` int(11) DEFAULT NULL COMMENT '状态',
  `from_user_id` int(11) DEFAULT NULL COMMENT '来自人ID',
  `notice_type_id` int(11) DEFAULT NULL COMMENT '通知类型ID',
  `to_user_id` int(11) DEFAULT NULL COMMENT '接收人ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- ----------------------------
-- Records of notice
-- ----------------------------
BEGIN;
INSERT INTO `notice` VALUES (1, NULL, '2020-04-28 10:03:35', '/manage/relationships/fans', NULL, 0, 1, 3, 71);
INSERT INTO `notice` VALUES (2, NULL, '2020-04-28 10:04:57', '/manage/relationships/fans', NULL, 0, 1, 3, 71);
INSERT INTO `notice` VALUES (3, NULL, '2020-04-28 10:05:10', '/manage/relationships/fans', NULL, 0, 1, 3, 71);
COMMIT;

-- ----------------------------
-- Table structure for notice_type
-- ----------------------------
DROP TABLE IF EXISTS `notice_type`;
CREATE TABLE `notice_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `content` varchar(255) DEFAULT NULL COMMENT '内容',
  `style` varchar(255) DEFAULT NULL COMMENT '图标样式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='通知类型表';

-- ----------------------------
-- Records of notice_type
-- ----------------------------
BEGIN;
INSERT INTO `notice_type` VALUES (1, '新的评论消息', 'fa fa-comments text-orange');
INSERT INTO `notice_type` VALUES (2, '新的回复消息', 'fa fa-comment text-aqua');
INSERT INTO `notice_type` VALUES (3, '新的粉丝消息', 'fa fa-users text-red');
INSERT INTO `notice_type` VALUES (4, '新的回答消息', 'fa fa-comments text-orange');
INSERT INTO `notice_type` VALUES (5, '答案被回复', 'fa fa-comment text-aqua');
INSERT INTO `notice_type` VALUES (6, '答案被采纳', 'fa fa-check-circle text-success');
COMMIT;

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `answer_size` int(11) DEFAULT NULL COMMENT '回答数量',
  `content` longtext NOT NULL COMMENT '内容',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `guid` varchar(100) DEFAULT NULL COMMENT 'URL',
  `status` varchar(10) DEFAULT NULL COMMENT '状态',
  `summary` varchar(2000) NOT NULL COMMENT '摘要',
  `tags` varchar(100) DEFAULT NULL COMMENT '标签',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `view_size` int(11) DEFAULT NULL COMMENT '访问数',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='提问表';

-- ----------------------------
-- Records of question
-- ----------------------------
BEGIN;
INSERT INTO `question` VALUES (1, 1, '<p style=\"margin-left: 40px;\">111111111</p>', '2020-04-02 10:53:27', '/questions/1', 'publish', '111111111', '11111', '111111', NULL, 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for relationship
-- ----------------------------
DROP TABLE IF EXISTS `relationship`;
CREATE TABLE `relationship` (
  `to_user_id` int(11) NOT NULL COMMENT '我方ID',
  `from_user_id` int(11) NOT NULL COMMENT '对方ID',
  PRIMARY KEY (`to_user_id`,`from_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='粉丝关系表';

-- ----------------------------
-- Records of relationship
-- ----------------------------
BEGIN;
INSERT INTO `relationship` VALUES (71, 1);
COMMIT;

-- ----------------------------
-- Table structure for slide
-- ----------------------------
DROP TABLE IF EXISTS `slide`;
CREATE TABLE `slide` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `guid` varchar(255) DEFAULT NULL COMMENT '跳转URL',
  `picture` varchar(255) DEFAULT NULL COMMENT '图片URL',
  `position` int(11) DEFAULT NULL COMMENT '排序',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='首页幻灯片表';

-- ----------------------------
-- Records of slide
-- ----------------------------
BEGIN;
INSERT INTO `slide` VALUES (1, '#', '/img/slide-1.jpg', 5, 'publish', '欢迎加入 CoderGroup2');
INSERT INTO `slide` VALUES (2, '#', '/img/slide-2.jpg', 2, 'publish', '技术分享，疑难求助，简单而方便');
INSERT INTO `slide` VALUES (4, '#', '/img/slide-4.jpg', 1, 'publish', '程序人生，义无反顾');
COMMIT;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `article_size` int(11) DEFAULT NULL COMMENT '文章数',
  `guid` varchar(255) DEFAULT NULL COMMENT 'URL',
  `name` varchar(20) DEFAULT NULL COMMENT '名称',
  `position` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1wdpsed5kna2y38hnbgrnhi5b` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='文章标签表';

-- ----------------------------
-- Records of tag
-- ----------------------------
BEGIN;
INSERT INTO `tag` VALUES (1, NULL, '/search/articles?keywords=面试', '面试', 1);
INSERT INTO `tag` VALUES (2, NULL, '/search/articles?keywords=源码', '源码', 1);
INSERT INTO `tag` VALUES (3, NULL, '/search/articles?keywords=Java', 'Java', 1);
INSERT INTO `tag` VALUES (4, NULL, '/search/articles?keywords=算法', '算法', 1);
INSERT INTO `tag` VALUES (5, NULL, '/search/articles?keywords=数据结构', '数据结构', 1);
INSERT INTO `tag` VALUES (6, NULL, '/search/articles?keywords=计算机网络', '计算机网络', 1);
INSERT INTO `tag` VALUES (7, NULL, '/search/articles?keywords=操作系统', '操作系统', 1);
INSERT INTO `tag` VALUES (8, NULL, '/search/articles?keywords=test', 'test', 1);
COMMIT;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `answer_size` bigint(20) DEFAULT NULL COMMENT '回答数',
  `article_size` bigint(20) DEFAULT NULL COMMENT '文章数',
  `avatar` varchar(200) DEFAULT NULL COMMENT '头像URL',
  `contact` varchar(100) DEFAULT NULL COMMENT '联系方式',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `email` varchar(50) NOT NULL COMMENT '电子邮箱',
  `fan_size` bigint(20) DEFAULT NULL COMMENT '粉丝数',
  `follow_size` bigint(20) DEFAULT NULL COMMENT '关注数',
  `github` varchar(50) DEFAULT NULL COMMENT 'GitHub',
  `homepage` varchar(50) DEFAULT NULL COMMENT '个人主页',
  `is_verify_email` varchar(1) DEFAULT NULL COMMENT '是否验证了邮箱',
  `last_login_time` datetime DEFAULT NULL COMMENT '上次登录时间',
  `nickname` varchar(30) NOT NULL COMMENT '昵称',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `profile` varchar(1000) DEFAULT NULL COMMENT '个人信息',
  `question_size` bigint(20) DEFAULT NULL COMMENT '提问数',
  `reputation` int(11) DEFAULT NULL COMMENT '声望',
  `status` varchar(255) DEFAULT NULL COMMENT '状态',
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `view_size` bigint(20) DEFAULT NULL COMMENT '阅读数',
  `job_id` int(11) DEFAULT NULL COMMENT '职业ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES (1, 0, 37, 'http://www.gravatar.com/avatar/3ae8728fec3cd5cbfe99c4b966695f03?s=128&d=identicon&r=PG', 'QQ: 847064370(微信同号)', '2018-06-14 02:47:12', '847064370@qq.com', 10, 1, 'http://github.com/saysky', 'https://liuyanzhao.com', 'N', '2020-04-30 10:33:51', '言曌', '$2a$10$hM2JURFYczVtvoMMomQly.p7rDDKVanmIsVQtpb75gqHXDh/3U09y', '<p>山无陵，江水为竭。冬雷震震，夏雨雪。天地合，乃敢与君绝。</p>\r\n<p class=\"text-maroon\">长期求实习和内推等就业机会！</p>\r\n\r\n', 0, 506, 'normal', 'saysky', 1, 2);
INSERT INTO `user` VALUES (7, 0, 5, 'http://cdn.codergroup.cn/uploads/2018/7/6/ai211932/1530844254014', '1092702721', '2018-06-15 14:43:27', '111@qq.com', 4, 3, 'https://github.com/ai211932', '', 'Y', '2018-09-15 17:45:41', '刘某人', '$2a$10$U/HsZzN3ty4yKkNyH5WR.eZOAMD8XuGhTMft4rBiITp4vYeXGTjBm', '那天我痴痴的望着你性感的身躯一丝不挂地在我面前扭动，轻轻地抚摸你的肌肤，我无法抵挡住你的诱惑......“老板，我要这条鱼”', 0, 168, 'normal', 'ai211932', 1, 3);
INSERT INTO `user` VALUES (8, 0, 2, 'http://cdn.codergroup.cn/uploads/2018/6/18/qiqi/1529331879879', NULL, '2018-06-15 15:26:03', '222@qq.com', 2, 2, NULL, NULL, 'N', '2019-02-16 20:59:44', 'qiqi', '$2a$10$nlwWeSq3thwbSm9jq.DW1.pRRXmrjrjsLMkpyWwg7y0aVG07/jgQG', NULL, 0, 36, 'normal', 'qiqi', 1, 1);
INSERT INTO `user` VALUES (9, 0, 1, 'http://www.gravatar.com/avatar/7c55e74423287b25e4f36cbbb706d0f9?s=128&d=identicon&r=PG', NULL, '2018-06-17 20:46:23', '333@qq.com', 0, 0, '', '', 'N', '2018-07-06 15:22:04', 'HickSalmon', '$2a$10$IIOUuWLMQksb2eYT7dsQ4.S3KeufU0hT0K6Cu4g/VOT1gAiY4mpE2', '', 0, 22, 'normal', 'HickSalmon', 1, 1);
INSERT INTO `user` VALUES (10, 0, 8, 'http://www.gravatar.com/avatar/e0438b47141558af1473cbd84e5a4c63?s=128&d=identicon&r=PG', NULL, '2018-06-17 20:54:24', '333@sina.com', 1, 0, NULL, NULL, 'N', '2018-06-17 20:54:24', 'xinyue', '$2a$10$zemJun.5c5JY.7pvMGbkfO6/HzqcqO2Tzcm/K6vndZHoC0V97U9DG', NULL, 0, 20, 'normal', 'xinyue', 1, 1);
INSERT INTO `user` VALUES (23, 0, 36, 'http://thirdqq.qlogo.cn/qqapp/101472393/7E52AF82A3534AE6836335B1393DE1DC/100', NULL, '2018-07-03 16:38:15', '3332@qq.com', 0, 0, NULL, NULL, 'Y', '2018-07-03 16:38:15', '潘多拉魔盒', '$2a$10$i8JJOD/xT8qVIKSDDYm9o.tRXf7N18Yfw.5jDTn51vYG1pWF698fe', NULL, 0, 20, 'normal', 'l1012384', 1, 1);
INSERT INTO `user` VALUES (31, 0, 1, 'http://www.gravatar.com/avatar/d1d3ff672d2fd49286b79d0b3dc30016?s=128&d=identicon&r=PG', NULL, '2018-07-11 00:41:15', '123@qq.com', 0, 0, NULL, NULL, 'N', '2018-07-11 00:41:15', 'admin1', '$2a$10$EIHnV.iDtL6K3PmxWmN4CORHEbIhqZUubZWmXH6gCAbFwhEHptdW.', NULL, 0, 20, 'normal', 'admin1', 1, 1);
INSERT INTO `user` VALUES (33, 0, 1, 'http://www.gravatar.com/avatar/c1b5364130884363fe0b3cebe9a49083?s=128&d=identicon&r=PG', NULL, '2018-07-12 21:06:45', 'you@gmail.com', 0, 0, NULL, NULL, 'N', '2018-07-12 21:06:45', 'poker', '$2a$10$3374j2nZKwBVnZJ1g9y09e73EtvfAeoi6gzUNpxG1nu/uJMay4Mbq', NULL, 0, 20, 'normal', 'poker', 1, 1);
INSERT INTO `user` VALUES (48, 0, 1, 'http://thirdqq.qlogo.cn/qqapp/101472393/2293CF0CC64A50324C348E03F5F60874/100', NULL, '2018-08-17 22:19:29', '584201871@qq.com', 0, 0, NULL, NULL, 'Y', '2018-08-17 22:20:27', '在风中等你?', '$2a$10$IRk305yA0cXtushFns7sy.SuBLsPcekSdxR1yQFVJi1YZZOlDhnIy', NULL, 0, 30, 'normal', 'wangbing1991', 1, 1);
INSERT INTO `user` VALUES (54, 0, 2, 'http://cdn.codergroup.cn/uploads/2018/8/30/codegroup/1535643223434', '', '2018-08-30 22:37:42', '123456@qq.com', 1, 0, '', '', 'N', '2018-08-31 15:17:50', 'codegroup', '$2a$10$qzqGd6jPL7oSjZAjGSLLXuDNKkXiT2XHl9CxTG20CtK5moDrmeKG6', '哈哈哈哈', 0, 75, 'normal', 'codegroup', 1, 1);
INSERT INTO `user` VALUES (59, 0, 1, 'http://thirdqq.qlogo.cn/qqapp/101472393/206DA74F9BB45F02A2E778077E5ED984/100', NULL, '2018-09-11 00:00:24', '11022995396@qq.com', 0, 0, NULL, NULL, 'Y', '2019-01-14 23:28:46', 'OscarDBQ', '$2a$10$YzIRVtLDsjqFujNXIw/Iq.jpiYh7ewq2Qvlc4lgOUxoVmA42QkYo2', NULL, 0, 30, 'normal', 'achi', 1, 1);
INSERT INTO `user` VALUES (70, 0, 1, 'http://thirdqq.qlogo.cn/qqapp/101472393/8FBE18109DC95CDDED943DE8E05DC540/100', NULL, '2018-10-03 11:00:28', '613041@qq.com', 1, 1, NULL, NULL, 'Y', '2018-11-13 19:44:00', 'Aurora of Lewis', '$2a$10$QqsYkqRKpRhc9rJ1Mj/brueGm0tczIOaa26k3Ds1iUJsArbFxPjum', NULL, 0, 35, 'normal', 'Lewis', 1, 1);
INSERT INTO `user` VALUES (71, 0, 13, 'http://www.gravatar.com/avatar/8fe55fcdfd4b227f4fdbaf0f08196186?s=128&d=identicon&r=PG', NULL, '2018-10-10 11:53:33', '4333@qq.com', 1, 1, NULL, NULL, 'N', '2018-11-10 13:13:19', 'sunweijia', '$2a$10$rHFVIAXOHwzK70kPTKnc0eEYCse64FBMAt/4kWD/E7bZKZ4vFq9F6', NULL, 0, 288, 'normal', 'sunweijia', 1, 1);
INSERT INTO `user` VALUES (77, 0, 2, 'http://cdn.codergroup.cn/uploads/2018/11/8/test1/1541664542479', 'qq739090072', '2018-10-11 20:36:38', '435678@qq.com', 1, 4, '', '', 'Y', '2018-11-02 14:13:41', '南城花已开', '$2a$10$DmAjnLEQnW0r9unQ3.iMnOU4So.mvKtzZAevB621pT0k9EBZ0DUca', '这个人很懒什么也没有留下...', 0, 50, 'normal', 'test1', 1, 2);
INSERT INTO `user` VALUES (82, 0, 1, 'http://thirdqq.qlogo.cn/qqapp/101472393/74F0DCD3BB4FF2E27FC65E6273103B98/100', NULL, '2018-10-27 20:14:19', 'sadfsdf@qq.com', 2, 1, NULL, NULL, 'N', '2018-10-30 15:08:10', '暖暖的风', '$2a$10$YC9oDZ4cEMnNi.uEkbtcfe1YchAG9F5UGeZzWIfDg/5Tvuse7wLUe', NULL, 0, 48, 'normal', 'qwertyuiop', 1, 1);
INSERT INTO `user` VALUES (93, 0, 1, 'http://www.gravatar.com/avatar/f1545d13d4dade804eee440808eedf40?s=128&d=identicon&r=PG', NULL, '2018-11-08 15:32:09', '2748365@qq.com', 0, 0, NULL, NULL, 'N', '2019-01-25 17:29:03', 'Zachary', '$2a$10$ryLfE0VXvq1mIlb0L3sEa.OxhHejRZMOLfdX.iAuzjdDmeNqzaDGq', NULL, 0, 35, 'normal', 'Zachary', 1, 1);
INSERT INTO `user` VALUES (94, 0, 1, 'http://www.gravatar.com/avatar/5c12806622f9e6f69ddac2798c1fe371?s=128&d=identicon&r=PG', NULL, '2018-11-08 20:36:19', '2anyang@hotmail.com', 1, 0, NULL, NULL, 'Y', '2018-11-08 20:36:19', 'Albert', '$2a$10$FIHnzy4bSD5zrkmhlqblRerbsHyCofEc44MIDi9T.KT53UWJvAKbK', NULL, 0, 20, 'normal', 'Albert', 1, 1);
INSERT INTO `user` VALUES (96, 0, 1, 'http://www.gravatar.com/avatar/5be46f16285ee5b8551731c7a28c1c43?s=128&d=identicon&r=PG', NULL, '2018-11-11 22:38:59', '294266@qq.com', 0, 0, NULL, NULL, 'Y', '2018-11-11 22:38:59', 'chenzi', '$2a$10$srRrwHBdFgUfpd6L5NQpm.g1n9tvpo2AKiprCIkT91paG60ginXfi', NULL, 0, 29, 'normal', 'chenzi', 1, 1);
INSERT INTO `user` VALUES (101, 0, 1, 'http://thirdqq.qlogo.cn/qqapp/101472393/035E5AE504FE740A69EA6D3BF435E184/100', NULL, '2018-11-18 19:48:07', '33130276@163.com', 0, 0, NULL, NULL, 'N', '2018-11-19 09:49:57', '?®™™™™?', '$2a$10$zS0NyFBj3W0DlEa0J8znnO8MVI61jDeKq5PbzX40E8DKcvzoH7i6y', NULL, 0, 41, 'normal', 'mmma', 1, 1);
INSERT INTO `user` VALUES (102, 0, 1, 'http://www.gravatar.com/avatar/f425f2f91b6bbecdc26242366f3d0c82?s=128&d=identicon&r=PG', NULL, '2018-11-19 15:47:02', '3809252882@qq.com', 0, 0, NULL, NULL, 'N', '2018-11-20 22:37:07', 'jlb0906', '$2a$10$CGlQUkk0xJjDsTIAbtLzsOqXHUrmhfV.cKB4NXTBH0RkuRMXPzCSi', NULL, 0, 25, 'normal', 'jlb0906', 1, 1);
INSERT INTO `user` VALUES (113, 0, 4, 'http://www.gravatar.com/avatar/2d9234e6da3d407827c3025efccf47b8?s=128&d=identicon&r=PG', '863146675', '2018-12-29 14:06:36', '3863146675@qq.com', 0, 0, '', '', 'Y', '2019-04-12 08:37:55', 'qwe86314', '$2a$10$MqklorkjkwAemAxLc03Ft.GNui6lF14nZQdM3gQTxoP/rttyK5wie', 'alert(\"xxx\");', 0, 165, 'normal', 'qwe86314', 1, 1);
INSERT INTO `user` VALUES (115, 0, 2, 'http://cdn.codergroup.cn/uploads/2019/1/10/Empirefree/1547086807569', NULL, '2019-01-08 12:59:17', '349680@qq.com', 0, 0, NULL, NULL, 'Y', '2019-03-26 18:30:56', 'Empirefree', '$2a$10$98FrD8ial8yQdta788g2x.Km3lAP8Lp9wMuk67snpkOBGSexyuuIO', NULL, 0, 64, 'normal', 'Empirefree', 1, 1);
INSERT INTO `user` VALUES (120, 0, 1, 'http://www.gravatar.com/avatar/6ab20f07fc4adceda6347dbac18a081b?s=128&d=identicon&r=PG', NULL, '2019-01-18 14:46:41', '38675@qq.com', 1, 0, NULL, NULL, 'Y', '2019-01-18 14:46:41', 'Hhc0917', '$2a$10$S1r3m1liZBP5yBMcIgc3AuDwW77X2v9ZPAjWgzi4uYzcwburYdC72', NULL, 0, 20, 'normal', 'Hhc0917', 1, 1);
INSERT INTO `user` VALUES (122, 0, 1, 'http://www.gravatar.com/avatar/b130ec9d374885695c3ce7c585918300?s=128&d=identicon&r=PG', NULL, '2019-01-24 09:51:30', '3dc518@qq.com', 1, 0, NULL, NULL, 'Y', '2019-01-24 09:51:30', 'gszdc', '$2a$10$WbYhfYBk/3YAWe0foZ00xeU9hleXkzR.G1F842EH7x3HROcA7aPo2', NULL, 0, 42, 'normal', 'gszdc', 1, 1);
INSERT INTO `user` VALUES (139, 0, 1, 'http://www.gravatar.com/avatar/e7c7b6eb675ba780985c7e1cbadd2484?s=128&d=identicon&r=PG', NULL, '2019-03-15 21:00:09', '3sy@gmail.com', 0, 0, NULL, NULL, 'N', '2019-03-16 19:10:46', 'fantasy', '$2a$10$kQB12BTgGTm0d.ml/tc05.JqwE.mvW4ZPXPkb4REL4mjaP/fw9lsi', NULL, 0, 22, 'normal', 'fantasy', 1, 1);
INSERT INTO `user` VALUES (140, 0, 1, 'http://www.gravatar.com/avatar/fb04a8d441dcb3cb1296a3b7f2858d2d?s=128&d=identicon&r=PG', NULL, '2019-03-19 12:57:45', '3608572@163.com', 0, 0, NULL, NULL, 'Y', '2019-03-21 09:36:17', 'qbian', '$2a$10$Y2J1lqdS7kS2wDw7xQh9T.qARBogJEP4ZcRWh3yftyixow79RLtuO', NULL, 0, 37, 'normal', 'qbian', 1, 1);
INSERT INTO `user` VALUES (141, 0, 0, 'http://www.gravatar.com/avatar/45962c0cd23bc6a4cc9e33246b7cf1e7?s=128&d=identicon&r=PG', NULL, '2020-03-01 10:39:25', 'mayun666@qq.com', 0, 0, NULL, NULL, 'N', '2020-03-01 10:39:25', 'mayun666', '$2a$10$hM2JURFYczVtvoMMomQly.p7rDDKVanmIsVQtpb75gqHXDh/3U09y', NULL, 0, 10, 'normal', 'mayun666', 1, 1);
COMMIT;

-- ----------------------------
-- Table structure for user_authority
-- ----------------------------
DROP TABLE IF EXISTS `user_authority`;
CREATE TABLE `user_authority` (
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `authority_id` int(11) NOT NULL COMMENT '角色ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

-- ----------------------------
-- Records of user_authority
-- ----------------------------
BEGIN;
INSERT INTO `user_authority` VALUES (1, 2);
INSERT INTO `user_authority` VALUES (1, 1);
INSERT INTO `user_authority` VALUES (2, 2);
INSERT INTO `user_authority` VALUES (3, 2);
INSERT INTO `user_authority` VALUES (4, 2);
INSERT INTO `user_authority` VALUES (5, 2);
INSERT INTO `user_authority` VALUES (6, 2);
INSERT INTO `user_authority` VALUES (7, 2);
INSERT INTO `user_authority` VALUES (8, 2);
INSERT INTO `user_authority` VALUES (10, 2);
INSERT INTO `user_authority` VALUES (11, 2);
INSERT INTO `user_authority` VALUES (12, 2);
INSERT INTO `user_authority` VALUES (13, 2);
INSERT INTO `user_authority` VALUES (14, 2);
INSERT INTO `user_authority` VALUES (15, 2);
INSERT INTO `user_authority` VALUES (16, 2);
INSERT INTO `user_authority` VALUES (17, 2);
INSERT INTO `user_authority` VALUES (18, 2);
INSERT INTO `user_authority` VALUES (19, 2);
INSERT INTO `user_authority` VALUES (21, 2);
INSERT INTO `user_authority` VALUES (22, 2);
INSERT INTO `user_authority` VALUES (23, 2);
INSERT INTO `user_authority` VALUES (24, 2);
INSERT INTO `user_authority` VALUES (25, 2);
INSERT INTO `user_authority` VALUES (26, 2);
INSERT INTO `user_authority` VALUES (9, 2);
INSERT INTO `user_authority` VALUES (27, 2);
INSERT INTO `user_authority` VALUES (28, 2);
INSERT INTO `user_authority` VALUES (29, 2);
INSERT INTO `user_authority` VALUES (30, 2);
INSERT INTO `user_authority` VALUES (31, 2);
INSERT INTO `user_authority` VALUES (32, 2);
INSERT INTO `user_authority` VALUES (33, 2);
INSERT INTO `user_authority` VALUES (34, 2);
INSERT INTO `user_authority` VALUES (35, 2);
INSERT INTO `user_authority` VALUES (36, 2);
INSERT INTO `user_authority` VALUES (37, 2);
INSERT INTO `user_authority` VALUES (38, 2);
INSERT INTO `user_authority` VALUES (39, 2);
INSERT INTO `user_authority` VALUES (40, 2);
INSERT INTO `user_authority` VALUES (41, 2);
INSERT INTO `user_authority` VALUES (42, 2);
INSERT INTO `user_authority` VALUES (43, 2);
INSERT INTO `user_authority` VALUES (44, 2);
INSERT INTO `user_authority` VALUES (45, 2);
INSERT INTO `user_authority` VALUES (46, 2);
INSERT INTO `user_authority` VALUES (47, 2);
INSERT INTO `user_authority` VALUES (48, 2);
INSERT INTO `user_authority` VALUES (49, 2);
INSERT INTO `user_authority` VALUES (50, 2);
INSERT INTO `user_authority` VALUES (51, 2);
INSERT INTO `user_authority` VALUES (52, 2);
INSERT INTO `user_authority` VALUES (53, 2);
INSERT INTO `user_authority` VALUES (54, 2);
INSERT INTO `user_authority` VALUES (55, 2);
INSERT INTO `user_authority` VALUES (56, 2);
INSERT INTO `user_authority` VALUES (57, 2);
INSERT INTO `user_authority` VALUES (58, 2);
INSERT INTO `user_authority` VALUES (59, 2);
INSERT INTO `user_authority` VALUES (60, 2);
INSERT INTO `user_authority` VALUES (61, 2);
INSERT INTO `user_authority` VALUES (62, 2);
INSERT INTO `user_authority` VALUES (63, 2);
INSERT INTO `user_authority` VALUES (64, 2);
INSERT INTO `user_authority` VALUES (65, 2);
INSERT INTO `user_authority` VALUES (66, 2);
INSERT INTO `user_authority` VALUES (67, 2);
INSERT INTO `user_authority` VALUES (68, 2);
INSERT INTO `user_authority` VALUES (69, 2);
INSERT INTO `user_authority` VALUES (70, 2);
INSERT INTO `user_authority` VALUES (20, 2);
INSERT INTO `user_authority` VALUES (71, 2);
INSERT INTO `user_authority` VALUES (72, 2);
INSERT INTO `user_authority` VALUES (73, 2);
INSERT INTO `user_authority` VALUES (74, 2);
INSERT INTO `user_authority` VALUES (75, 2);
INSERT INTO `user_authority` VALUES (76, 2);
INSERT INTO `user_authority` VALUES (77, 2);
INSERT INTO `user_authority` VALUES (78, 2);
INSERT INTO `user_authority` VALUES (79, 2);
INSERT INTO `user_authority` VALUES (80, 2);
INSERT INTO `user_authority` VALUES (81, 2);
INSERT INTO `user_authority` VALUES (82, 2);
INSERT INTO `user_authority` VALUES (83, 2);
INSERT INTO `user_authority` VALUES (84, 2);
INSERT INTO `user_authority` VALUES (85, 2);
INSERT INTO `user_authority` VALUES (86, 2);
INSERT INTO `user_authority` VALUES (87, 2);
INSERT INTO `user_authority` VALUES (88, 2);
INSERT INTO `user_authority` VALUES (89, 2);
INSERT INTO `user_authority` VALUES (90, 2);
INSERT INTO `user_authority` VALUES (91, 2);
INSERT INTO `user_authority` VALUES (92, 2);
INSERT INTO `user_authority` VALUES (93, 2);
INSERT INTO `user_authority` VALUES (94, 2);
INSERT INTO `user_authority` VALUES (95, 2);
INSERT INTO `user_authority` VALUES (96, 2);
INSERT INTO `user_authority` VALUES (97, 2);
INSERT INTO `user_authority` VALUES (98, 2);
INSERT INTO `user_authority` VALUES (99, 2);
INSERT INTO `user_authority` VALUES (100, 2);
INSERT INTO `user_authority` VALUES (101, 2);
INSERT INTO `user_authority` VALUES (102, 2);
INSERT INTO `user_authority` VALUES (103, 2);
INSERT INTO `user_authority` VALUES (104, 2);
INSERT INTO `user_authority` VALUES (105, 2);
INSERT INTO `user_authority` VALUES (106, 2);
INSERT INTO `user_authority` VALUES (107, 2);
INSERT INTO `user_authority` VALUES (108, 2);
INSERT INTO `user_authority` VALUES (109, 2);
INSERT INTO `user_authority` VALUES (110, 2);
INSERT INTO `user_authority` VALUES (111, 2);
INSERT INTO `user_authority` VALUES (112, 2);
INSERT INTO `user_authority` VALUES (113, 2);
INSERT INTO `user_authority` VALUES (114, 2);
INSERT INTO `user_authority` VALUES (115, 2);
INSERT INTO `user_authority` VALUES (116, 2);
INSERT INTO `user_authority` VALUES (117, 2);
INSERT INTO `user_authority` VALUES (118, 2);
INSERT INTO `user_authority` VALUES (119, 2);
INSERT INTO `user_authority` VALUES (120, 2);
INSERT INTO `user_authority` VALUES (121, 2);
INSERT INTO `user_authority` VALUES (122, 2);
INSERT INTO `user_authority` VALUES (123, 2);
INSERT INTO `user_authority` VALUES (124, 2);
INSERT INTO `user_authority` VALUES (125, 2);
INSERT INTO `user_authority` VALUES (126, 2);
INSERT INTO `user_authority` VALUES (127, 2);
INSERT INTO `user_authority` VALUES (128, 2);
INSERT INTO `user_authority` VALUES (129, 2);
INSERT INTO `user_authority` VALUES (130, 2);
INSERT INTO `user_authority` VALUES (131, 2);
INSERT INTO `user_authority` VALUES (132, 2);
INSERT INTO `user_authority` VALUES (133, 2);
INSERT INTO `user_authority` VALUES (134, 2);
INSERT INTO `user_authority` VALUES (135, 2);
INSERT INTO `user_authority` VALUES (136, 2);
INSERT INTO `user_authority` VALUES (137, 2);
INSERT INTO `user_authority` VALUES (138, 2);
INSERT INTO `user_authority` VALUES (139, 2);
INSERT INTO `user_authority` VALUES (140, 2);
INSERT INTO `user_authority` VALUES (141, 2);
INSERT INTO `user_authority` VALUES (142, 2);
INSERT INTO `user_authority` VALUES (149, 2);
INSERT INTO `user_authority` VALUES (152, 2);
INSERT INTO `user_authority` VALUES (153, 2);
INSERT INTO `user_authority` VALUES (150, 2);
INSERT INTO `user_authority` VALUES (141, 2);
COMMIT;

-- ----------------------------
-- Table structure for zan
-- ----------------------------
DROP TABLE IF EXISTS `zan`;
CREATE TABLE `zan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

SET FOREIGN_KEY_CHECKS = 1;
