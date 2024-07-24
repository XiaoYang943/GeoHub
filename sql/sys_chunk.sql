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

 Date: 21/07/2024 18:13:26
*/


-- ----------------------------
-- Table structure for sys_chunk
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_chunk";
CREATE TABLE "public"."sys_chunk" (
  "id" int8 NOT NULL,
  "chunk_number" int4 NOT NULL,
  "chunk_size" int8 NOT NULL,
  "current_chunk_size" int8 NOT NULL,
  "filename" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "identifier" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "relative_path" varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
  "total_chunks" int4 NOT NULL,
  "total_size" int8 NOT NULL
)
;
COMMENT ON COLUMN "public"."sys_chunk"."id" IS '主键ID';
COMMENT ON COLUMN "public"."sys_chunk"."chunk_number" IS '文件块编号';
COMMENT ON COLUMN "public"."sys_chunk"."chunk_size" IS '分块大小';
COMMENT ON COLUMN "public"."sys_chunk"."current_chunk_size" IS '当前分块大小';
COMMENT ON COLUMN "public"."sys_chunk"."filename" IS '文件名';
COMMENT ON COLUMN "public"."sys_chunk"."identifier" IS '文件标识,MD5';
COMMENT ON COLUMN "public"."sys_chunk"."relative_path" IS '相对路径';
COMMENT ON COLUMN "public"."sys_chunk"."total_chunks" IS '总块数';
COMMENT ON COLUMN "public"."sys_chunk"."total_size" IS '总大小';
COMMENT ON TABLE "public"."sys_chunk" IS '文件分片记录表';

-- ----------------------------
-- Records of sys_chunk
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table sys_chunk
-- ----------------------------
ALTER TABLE "public"."sys_chunk" ADD CONSTRAINT "sys_chunk_pkey" PRIMARY KEY ("id");
