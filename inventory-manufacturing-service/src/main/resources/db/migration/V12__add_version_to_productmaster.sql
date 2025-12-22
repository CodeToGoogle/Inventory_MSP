-- V12__add_version_to_productmaster.sql
-- Add version column for optimistic locking

ALTER TABLE ProductMaster
ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
