create table account.enum(id bigserial
, className                  character varying(126) NOT NULL
, name                       character varying(64) NOT NULL
, description                character varying(126) NOT NULL
, CONSTRAINT U_enum UNIQUE(className, name)
);

 
