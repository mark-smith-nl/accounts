create table IF NOT EXISTS account.${enumClassname} (
name             character varying(64) PRIMARY KEY
, isdefault      boolean NOT NULL DEFAULT false
, description    character varying(126) NOT NULL
, active         boolean NOT NULL DEFAULT true
);

CREATE UNIQUE INDEX IF NOT EXISTS ${enumClassname}_one_default ON account.${enumClassname}(isdefault) WHERE (isdefault AND active);