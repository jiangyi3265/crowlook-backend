-- Crowlook 内容中台业务表与管理菜单
-- 请在执行 sql/ry_20250522.sql 后执行本文件。

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS cw_category (
  category_id    bigint(20)      NOT NULL AUTO_INCREMENT,
  parent_id      bigint(20)      DEFAULT 0                    COMMENT '父分类 ID',
  category_name  varchar(100)    NOT NULL                     COMMENT '分类名称',
  slug           varchar(100)    DEFAULT ''                   COMMENT '分类标识',
  icon           varchar(500)    DEFAULT ''                   COMMENT '图标地址',
  cover_url      varchar(500)    DEFAULT ''                   COMMENT '封面地址',
  video_url      varchar(500)    DEFAULT ''                   COMMENT '分类头图视频',
  poster_url     varchar(500)    DEFAULT ''                   COMMENT '分类视频封面',
  heading_image  varchar(500)    DEFAULT ''                   COMMENT '分类标题图',
  sort_order     int(11)         DEFAULT 0                    COMMENT '排序',
  status         char(1)         DEFAULT '0'                  COMMENT '状态（0正常 1停用）',
  article_style  tinyint(1)      DEFAULT 0                    COMMENT '是否使用文章样式',
  hidden         tinyint(1)      DEFAULT 0                    COMMENT '是否在用户端隐藏',
  create_by      varchar(64)     DEFAULT '',
  create_time    datetime        DEFAULT NULL,
  update_by      varchar(64)     DEFAULT '',
  update_time    datetime        DEFAULT NULL,
  remark         varchar(500)    DEFAULT '',
  PRIMARY KEY (category_id),
  KEY idx_cw_category_parent (parent_id),
  KEY idx_cw_category_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Crowlook 作品分类';

-- 兼容已经执行过旧版脚本的数据库，使后台新增分类可以取得自动生成的 ID。
ALTER TABLE cw_category
  MODIFY category_id bigint(20) NOT NULL AUTO_INCREMENT;

CREATE TABLE IF NOT EXISTS cw_post (
  post_id          bigint(20)      NOT NULL AUTO_INCREMENT,
  post_title       varchar(255)    NOT NULL                     COMMENT '作品标题',
  slug             varchar(255)    DEFAULT ''                   COMMENT '作品标识',
  excerpt          varchar(1000)   DEFAULT ''                   COMMENT '作品摘要',
  thumbnail        varchar(500)    DEFAULT ''                   COMMENT '封面地址',
  format           varchar(32)     DEFAULT 'image'              COMMENT '内容形态',
  format_content   longtext                                      COMMENT '形态扩展 JSON',
  category_id      bigint(20)      DEFAULT NULL                 COMMENT '主分类 ID',
  category_ids     varchar(500)    DEFAULT ''                   COMMENT '全部分类 ID，逗号分隔',
  content          longtext                                      COMMENT '正文 HTML',
  status           char(1)         DEFAULT '1'                  COMMENT '状态（0已发布 1草稿 2已下线）',
  featured         tinyint(1)      DEFAULT 0                    COMMENT '是否推荐',
  sort_order       int(11)         DEFAULT 0                    COMMENT '排序',
  views            bigint(20)      DEFAULT 0                    COMMENT '浏览量',
  favorite_count   int(11)         DEFAULT 0                    COMMENT '收藏数',
  comment_count    int(11)         DEFAULT 0                    COMMENT '已审核评论数',
  mode             varchar(32)     DEFAULT ''                   COMMENT '客户端展示模式',
  style            varchar(32)     DEFAULT ''                   COMMENT '客户端展示样式',
  publish_time     datetime        DEFAULT NULL                 COMMENT '发布时间',
  create_by        varchar(64)     DEFAULT '',
  create_time      datetime        DEFAULT NULL,
  update_by        varchar(64)     DEFAULT '',
  update_time      datetime        DEFAULT NULL,
  remark           varchar(500)    DEFAULT '',
  PRIMARY KEY (post_id),
  KEY idx_cw_post_status_time (status, publish_time),
  KEY idx_cw_post_category (category_id),
  FULLTEXT KEY ft_cw_post_search (post_title, excerpt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Crowlook 作品内容';

CREATE TABLE IF NOT EXISTS cw_page (
  page_id           bigint(20)      NOT NULL AUTO_INCREMENT,
  page_key          varchar(100)    NOT NULL                     COMMENT '页面唯一标识',
  page_title        varchar(255)    NOT NULL                     COMMENT '页面标题',
  page_name         varchar(255)    DEFAULT ''                   COMMENT '页面名称',
  slug              varchar(255)    DEFAULT ''                   COMMENT '页面路径标识',
  modules_json      longtext                                      COMMENT '模块配置 JSON 数组',
  config_json       longtext                                      COMMENT '页面配置 JSON',
  background_color  varchar(32)     DEFAULT ''                   COMMENT '背景色',
  background_image  varchar(500)    DEFAULT ''                   COMMENT '背景图',
  status             char(1)         DEFAULT '1'                  COMMENT '状态（0已发布 1草稿）',
  sort_order         int(11)         DEFAULT 0                    COMMENT '排序',
  create_by          varchar(64)     DEFAULT '',
  create_time        datetime        DEFAULT NULL,
  update_by          varchar(64)     DEFAULT '',
  update_time        datetime        DEFAULT NULL,
  remark             varchar(500)    DEFAULT '',
  PRIMARY KEY (page_id),
  UNIQUE KEY uk_cw_page_key (page_key),
  KEY idx_cw_page_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Crowlook 页面编排';

CREATE TABLE IF NOT EXISTS cw_comment (
  comment_id    bigint(20)      NOT NULL AUTO_INCREMENT,
  post_id       bigint(20)      NOT NULL                     COMMENT '作品 ID',
  parent_id     bigint(20)      DEFAULT 0                    COMMENT '父评论 ID',
  nickname      varchar(100)    DEFAULT '访客'               COMMENT '用户昵称',
  avatar        varchar(500)    DEFAULT ''                   COMMENT '头像地址',
  content       varchar(1000)   NOT NULL                     COMMENT '评论内容',
  status        char(1)         DEFAULT '0'                  COMMENT '状态（0待审核 1已通过 2已拒绝）',
  create_by     varchar(64)     DEFAULT '',
  create_time   datetime        DEFAULT NULL,
  update_by     varchar(64)     DEFAULT '',
  update_time   datetime        DEFAULT NULL,
  remark        varchar(500)    DEFAULT '',
  PRIMARY KEY (comment_id),
  KEY idx_cw_comment_post_status (post_id, status),
  KEY idx_cw_comment_created (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Crowlook 作品评论';

-- 用户端现有分类。使用可重复执行的写法，不覆盖后台已经维护过的数据。
INSERT IGNORE INTO cw_category
  (category_id, parent_id, category_name, slug, sort_order, status, article_style, create_by, create_time)
VALUES
  (164, 0, 'EXPLORE「她」', 'explore-her', 10, '0', 0, 'admin', NOW()),
  (139, 164, '女性力量', 'female-power', 1, '0', 0, 'admin', NOW()),
  (167, 164, '美式辣妹', 'american-hot-girl', 2, '0', 0, 'admin', NOW()),
  (165, 164, '纯净透亮', 'pure-and-bright', 3, '0', 0, 'admin', NOW()),
  (178, 164, '致敬经典', 'classic-tribute', 4, '0', 0, 'admin', NOW()),
  (166, 164, '光影氛围', 'light-and-shadow', 5, '0', 0, 'admin', NOW()),
  (168, 164, '节日照', 'festival', 6, '0', 0, 'admin', NOW()),
  (169, 164, '新中式', 'new-chinese', 7, '0', 0, 'admin', NOW()),
  (15, 164, '3rd电影影调', 'third-film-tone', 8, '0', 0, 'admin', NOW()),
  (173, 164, '大艺术家们', 'artists', 9, '0', 0, 'admin', NOW()),
  (143, 0, 'EXPLORE「他」', 'explore-him', 20, '0', 0, 'admin', NOW()),
  (172, 0, '和重要的人一起', 'together', 30, '0', 0, 'admin', NOW()),
  (11, 172, '两人一世界', 'couple', 1, '0', 0, 'admin', NOW()),
  (163, 172, '姐妹照', 'friends', 2, '0', 0, 'admin', NOW()),
  (174, 172, '人宠', 'people-and-pets', 3, '0', 0, 'admin', NOW()),
  (155, 172, '孕照', 'maternity', 4, '0', 0, 'admin', NOW()),
  (153, 172, '团队照', 'team', 5, '0', 0, 'admin', NOW()),
  (154, 172, '家庭影像', 'family', 6, '0', 0, 'admin', NOW()),
  (1, 0, '烏鴉作品', 'crowlook-works', 40, '0', 0, 'admin', NOW()),
  (158, 1, '明星艺人', 'celebrity', 1, '0', 0, 'admin', NOW()),
  (159, 1, '达人博主', 'creator', 2, '0', 0, 'admin', NOW()),
  (160, 1, '顾客定制案例', 'customer-case', 3, '0', 0, 'admin', NOW()),
  (4, 0, '品牌动态', 'brand-news', 50, '0', 1, 'admin', NOW()),
  (3, 0, '微电影', 'short-film', 60, '0', 0, 'admin', NOW()),
  (13, 0, '其他作品', 'other-works', 70, '0', 0, 'admin', NOW()),
  (179, 13, '别的', 'others', 1, '0', 0, 'admin', NOW()),
  (170, 13, '外景', 'outdoor', 2, '0', 0, 'admin', NOW()),
  (161, 13, 'AI摄影', 'ai-photography', 3, '0', 0, 'admin', NOW());

-- 内容运营菜单。管理员自动拥有全部权限；其他角色可在角色管理中分配。
INSERT IGNORE INTO sys_menu VALUES
  (2000, '内容运营', 0, 1, 'crowlook', NULL, '', '', 1, 0, 'M', '0', '0', '', 'picture', 'admin', NOW(), '', NULL, 'Crowlook 三端内容运营'),
  (2001, '作品管理', 2000, 1, 'post', 'crowlook/post/index', '', 'CrowlookPost', 1, 0, 'C', '0', '0', 'crowlook:post:list', 'documentation', 'admin', NOW(), '', NULL, ''),
  (2002, '分类管理', 2000, 2, 'category', 'crowlook/category/index', '', 'CrowlookCategory', 1, 0, 'C', '0', '0', 'crowlook:category:list', 'tree-table', 'admin', NOW(), '', NULL, ''),
  (2003, '页面装修', 2000, 3, 'page', 'crowlook/page/index', '', 'CrowlookPage', 1, 0, 'C', '0', '0', 'crowlook:page:list', 'edit', 'admin', NOW(), '', NULL, ''),
  (2004, '评论中心', 2000, 4, 'comment', 'crowlook/comment/index', '', 'CrowlookComment', 1, 0, 'C', '0', '0', 'crowlook:comment:list', 'message', 'admin', NOW(), '', NULL, '');

INSERT IGNORE INTO sys_menu VALUES
  (2101, '作品查询', 2001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:post:query', '#', 'admin', NOW(), '', NULL, ''),
  (2102, '作品新增', 2001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:post:add', '#', 'admin', NOW(), '', NULL, ''),
  (2103, '作品修改', 2001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:post:edit', '#', 'admin', NOW(), '', NULL, ''),
  (2104, '作品删除', 2001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:post:remove', '#', 'admin', NOW(), '', NULL, ''),
  (2111, '分类查询', 2002, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:category:query', '#', 'admin', NOW(), '', NULL, ''),
  (2112, '分类新增', 2002, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:category:add', '#', 'admin', NOW(), '', NULL, ''),
  (2113, '分类修改', 2002, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:category:edit', '#', 'admin', NOW(), '', NULL, ''),
  (2114, '分类删除', 2002, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:category:remove', '#', 'admin', NOW(), '', NULL, ''),
  (2121, '页面查询', 2003, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:page:query', '#', 'admin', NOW(), '', NULL, ''),
  (2122, '页面新增', 2003, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:page:add', '#', 'admin', NOW(), '', NULL, ''),
  (2123, '页面修改', 2003, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:page:edit', '#', 'admin', NOW(), '', NULL, ''),
  (2124, '页面删除', 2003, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:page:remove', '#', 'admin', NOW(), '', NULL, ''),
  (2125, '快照导入', 2003, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:page:import', '#', 'admin', NOW(), '', NULL, ''),
  (2131, '评论查询', 2004, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:comment:query', '#', 'admin', NOW(), '', NULL, ''),
  (2132, '评论审核', 2004, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:comment:edit', '#', 'admin', NOW(), '', NULL, ''),
  (2133, '评论删除', 2004, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'crowlook:comment:remove', '#', 'admin', NOW(), '', NULL, '');
