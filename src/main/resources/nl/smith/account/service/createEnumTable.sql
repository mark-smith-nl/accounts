create table IF NOT EXISTS account.${enumClassname} (
name             character varying(64) PRIMARY KEY
, description    character varying(126) NOT NULL
, active         boolean NOT NULL DEFAULT true
);