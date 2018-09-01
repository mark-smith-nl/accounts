create table IF NOT EXISTS account.${enumClassname} (
name             character varying(64) PRIMARY KEY
, description    character varying(126) NOT NULL
, isDefaultValue boolean NOT NULL DEFAULT false
, isActiveValue  boolean NOT NULL DEFAULT true
);

CREATE UNIQUE INDEX IF NOT EXISTS ${enumClassname}_one_default ON account.${enumClassname}(isDefaultValue) WHERE (isDefaultValue AND isActiveValue);