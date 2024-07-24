/*
 Navicat Premium Dump SQL

 Source Server         : localhost_5432
 Source Server Type    : PostgreSQL
 Source Server Version : 160003 (160003)
 Source Host           : localhost:5432
 Source Catalog        : postgres
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 160003 (160003)
 File Encoding         : 65001

 Date: 21/07/2024 18:13:37
*/


-- ----------------------------
-- Table structure for sys_filelist
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_filelist";
CREATE TABLE "public"."sys_filelist" (
  "id" int8 NOT NULL,
  "filename" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "identifier" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
  "url" varchar(200) COLLATE "pg_catalog"."default" NOT NULL,
  "location" varchar(200) COLLATE "pg_catalog"."default",
  "total_size" int8
)
;
COMMENT ON COLUMN "public"."sys_filelist"."id" IS '主键ID';
COMMENT ON COLUMN "public"."sys_filelist"."filename" IS '文件名';
COMMENT ON COLUMN "public"."sys_filelist"."identifier" IS '唯一标识,MD5';
COMMENT ON COLUMN "public"."sys_filelist"."url" IS '链接';
COMMENT ON COLUMN "public"."sys_filelist"."location" IS '本地地址';
COMMENT ON COLUMN "public"."sys_filelist"."total_size" IS '文件总大小';
COMMENT ON TABLE "public"."sys_filelist" IS '文件列表';

-- ----------------------------
-- Records of sys_filelist
-- ----------------------------

-- ----------------------------
-- Indexes structure for table sys_filelist
-- ----------------------------
CREATE UNIQUE INDEX "FILE_UNIQUE_KEY" ON "public"."sys_filelist" USING btree (
  "filename" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
  "identifier" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
);

-- ----------------------------
-- Primary Key structure for table sys_filelist
-- ----------------------------
ALTER TABLE "public"."sys_filelist" ADD CONSTRAINT "sys_filelist_pkey" PRIMARY KEY ("id");
