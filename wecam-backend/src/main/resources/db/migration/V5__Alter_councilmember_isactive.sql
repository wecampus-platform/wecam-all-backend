-- V2__alter_is_active_default_true.sql
ALTER TABLE council_member
    ALTER COLUMN is_active SET DEFAULT true;
