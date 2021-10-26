CREATE TABLE "PUBLIC"."CATEGORY"(
    "ID" BIGINT auto_increment NOT NULL PRIMARY KEY,
    "NAME" VARCHAR(255) NOT NULL,
    "LFT" BIGINT,
    "RGT" BIGINT,
    "ROOT_ID" BIGINT,
    "PARENT_ID" BIGINT,
    "LEVEL" BIGINT
);