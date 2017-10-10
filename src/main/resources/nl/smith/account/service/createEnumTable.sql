create table IF NOT EXISTS account.${enumClassname} (id bigserial
, name                       character varying(64) NOT NULL UNIQUE
, description                character varying(126) NOT NULL
);